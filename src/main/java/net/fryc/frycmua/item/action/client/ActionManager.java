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
    public final KeyBinding keybinding;
    private ActionPriority actionPriority;
    private ItemStack mainHandStack = ItemStack.EMPTY;
    private ItemStack offHandStack = ItemStack.EMPTY;
// TODO keybindy chyba tak rozwiaze ze bedzie opcja dodawania nowych keybindow jsonem i action managery beda przypisane do konkretnego keybindu

    public ActionManager(KeyBinding keybinding, ActionPriority priority, ClientPlayerEntity player){
        this.keybinding = keybinding;
        this.actionPriority = priority;
        this.player = player;
    }

    public void updateAvailableActions(){
        if(this.shouldUpdateAvailableActions()){
            this.mainHandStack = player.getMainHandStack();
            this.offHandStack = player.getOffHandStack();
            this.availableActions = this.actionPriority.getOrderedUseActions(this.mainHandStack, this.offHandStack);
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
    }

    public boolean shouldUpdateAvailableActions(){
        return /* TODO this.availableActions.isEmpty() || */this.mainHandStack != this.player.getMainHandStack() || this.offHandStack != this.player.getOffHandStack();
    }

    public List<Identifier> getAvailableActions(){
        return this.availableActions;
    }

    public void setActionPriority(ActionPriority priority){
        this.actionPriority = priority;
    }

}
