package net.fryc.frycmua.action.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionManager {

    private List<Identifier> availableActions = List.of();
    private final LinkedList<Identifier> switchableActions = new LinkedList<>();
    private final ClientPlayerEntity player;
    private final KeyBinding keybinding;
    private final PriorityManager priorityManager;
    private @Nullable Identifier chosenAction;
    private ItemStack mainHandStack = ItemStack.EMPTY;
    private ItemStack offHandStack = ItemStack.EMPTY;
    private boolean isFullyInitialized = false;


    public ActionManager(KeyBinding keybinding, PriorityManager priorityManager, ClientPlayerEntity player) {
        this.keybinding = keybinding;
        this.priorityManager = priorityManager;
        this.player = player;
        this.priorityManager.validate();
        this.chosenAction = null;
        this.switchableActions.add(null);
    }

    public void updateAvailableActions() {
        if(!this.isFullyInitialized) {
            this.chosenAction = null;
            this.forceUpdateAvailableActions();
            this.resetChosenAction();
            this.isFullyInitialized = true;
        }
        else if(this.shouldUpdateAvailableActions()) {
            this.updateItemsInHands();
            this.softResetChosenAction();
            this.forceUpdateAvailableActions();
        }
    }

    public void forceUpdateAvailableActions() {
        this.updateItemsInHands();
        this.availableActions = this.getAvailableActions(false);
    }

    public void resetChosenAction() {
        this.chosenAction = null;
        this.switchableActions.clear();
        this.switchableActions.addFirst(null);
        this.switchableActions.addAll(this.getAvailableSwitchableActions());
    }

    public void softResetChosenAction() {
        Identifier id = this.chosenAction;
        this.resetChosenAction();

        if(id != null) {
            while(this.switchableActions.contains(id) && this.chosenAction != id) {
                this.switchChosenAction(true);
            }
        }
    }

    public void switchChosenAction(boolean forward) {
        if(forward) {
            this.switchableActions.addLast(this.switchableActions.pollFirst());
        }
        else {
            this.switchableActions.addFirst(this.switchableActions.pollLast());
        }

        this.chosenAction = this.switchableActions.peekFirst();
    }

    public boolean shouldUpdateAvailableActions(){
        return this.mainHandStack != this.player.getMainHandStack() || this.offHandStack != this.player.getOffHandStack();
    }

    public void updateItemsInHands() {
        this.mainHandStack = this.player.getMainHandStack();
        this.offHandStack = this.player.getOffHandStack();
    }

    public List<Identifier> getAvailableActions(){
        return this.getAvailableActions(true);
    }

    public List<Identifier> getAvailableActions(boolean cached){
        if(cached) {
            return this.availableActions;
        }

        return this.chosenAction != null ? List.of(this.chosenAction) : this.getPriorityManager().getOrderedAvailableUseActions(this.mainHandStack, this.offHandStack);
    }

    public List<Identifier> getAvailableSwitchableActions() {
        return this.priorityManager.getSwitchableUseActions(this.mainHandStack, this.offHandStack);
    }

    public boolean shouldUseVanillaSystem(){
        return this.chosenAction == null && this.getPriorityManager().getActionPriority().equals(ActionPriority.DEFAULT);
    }

    public boolean shouldShowCurrentAction() {
        return this.getPriorityManager().shouldShowCurrentAction(this.chosenAction == null);
    }

    public KeyBinding getKeyBinding() {
        return this.keybinding;
    }

    public PriorityManager getPriorityManager() {
        return this.priorityManager;
    }
}
