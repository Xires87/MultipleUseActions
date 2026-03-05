package net.fryc.frycmua.item.action.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionManager {

    private List<Identifier> availableActions = List.of();
    private final ClientPlayerEntity player;
    private final KeyBinding keybinding;
    private final PriorityManager priorityManager;
    private ItemStack mainHandStack = ItemStack.EMPTY;
    private ItemStack offHandStack = ItemStack.EMPTY;


    public ActionManager(KeyBinding keybinding, PriorityManager priorityManager, ClientPlayerEntity player){
        this.keybinding = keybinding;
        this.priorityManager = priorityManager;
        this.player = player;
    }

    public void updateAvailableActions(){
        if(this.shouldUpdateAvailableActions()){
            this.forceUpdateAvailableActions();
        }
    }

    public void forceUpdateAvailableActions() {
        this.mainHandStack = player.getMainHandStack();
        this.offHandStack = player.getOffHandStack();
        this.availableActions = this.getPriorityManager().getOrderedAvailableUseActions(this.mainHandStack, this.offHandStack);
        this.availableActions.forEach(el -> {
            MultipleUseActions.LOGGER.warn(el.toString());
        });
            /*
            ((HoldsUseActions) first.getItem()).getAllActionsIds().stream().filter(id -> {
// TODO nie ma sensu wedlug rak robic (jak beda 2 takie same to bedzie sie prawa odpalala i tyle)
                return ((HoldsUseActions) first.getItem()).getItemUseActionInstance();// TODO musze w ktoryms miejscu przefiltrowac zeby tylko dostepne akcje byly (niekoniecznie tutaj)
            })

             */
    }

    public boolean shouldUpdateAvailableActions(){
        return this.mainHandStack != this.player.getMainHandStack() || this.offHandStack != this.player.getOffHandStack();
    }

    public List<Identifier> getAvailableActions(){
        return this.availableActions;
    }

    public boolean shouldUseVanillaSystem(){
        return this.getPriorityManager().getActionPriority().equals(ActionPriority.DEFAULT);
    }

    public KeyBinding getKeyBinding() {
        return this.keybinding;
    }

    public PriorityManager getPriorityManager() {
        return this.priorityManager;
    }

}
