package net.fryc.frycmua.mixin;

import com.google.common.collect.ImmutableSet;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.action.*;
import net.fryc.frycmua.util.UseActionHelper;
import net.fryc.frycmua.util.mixin_interfaces.item.ExecutesUseActions;
import net.fryc.frycmua.util.mixin_interfaces.item.HoldsUseActions;
import net.fryc.frycmua.util.mixin_interfaces.item.UseActionRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Set;

@Mixin(Item.class)
abstract class ItemMixin implements ItemConvertible, ExecutesUseActions, HoldsUseActions, UseActionRegistry {

    private final HashMap<Identifier, ItemUseInstance> availableItemUseActions = initHashMapWithDefaultValue(DefaultUseActions.USE_DEFAULT);
    private final HashMap<Identifier, ItemUseOnBlockInstance> availableItemUseOnBlockActions = initHashMapWithDefaultValue(DefaultUseActions.USE_ON_BLOCK_DEFAULT);
    private final HashMap<Identifier, ItemUseOnEntityInstance> availableItemUseOnEntityActions = initHashMapWithDefaultValue(DefaultUseActions.USE_ON_ENTITY_DEFAULT);

    private static <T> HashMap<Identifier, T> initHashMapWithDefaultValue(Identifier id){
        HashMap<Identifier, T> map = new HashMap<>();
        map.put(id, null);

        return map;
    }

    @Shadow
    public abstract TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);

    @Shadow
    public abstract ActionResult useOnBlock(ItemUsageContext context);

    @Shadow
    public abstract ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand);


    @Override
    public TypedActionResult<ItemStack> muaUseItem(Identifier selectedAction, World world, PlayerEntity user, Hand hand) {
        if(UseActionHelper.shouldUseSelectedUseAction(selectedAction)){
            UseInstance instance = this.getUseInstance(selectedAction);
            if(instance != null){
                if(instance instanceof ItemUseInstance useInstance){
                    return useInstance.getUse().useItem(this.asItem(), world, user, hand);
                }
            }
        }

        return this.use(world, user, hand);
    }

    @Override
    public ActionResult muaUseItemOnBlock(Identifier selectedAction, ItemUsageContext context) {
        if(UseActionHelper.shouldUseSelectedUseAction(selectedAction)){
            UseInstance instance = this.getUseInstance(selectedAction);
            if(instance != null){
                if(instance instanceof ItemUseOnBlockInstance useOnBlockInstance) {
                    return useOnBlockInstance.getUseOnBlock().useItemOnBlock(context);
                }
            }
        }

        return this.useOnBlock(context);
    }

    @Override
    public ActionResult muaUseItemOnEntity(Identifier selectedAction, ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(UseActionHelper.shouldUseSelectedUseAction(selectedAction)){
            UseInstance instance = this.getUseInstance(selectedAction);
            if(instance != null){
                if(instance instanceof ItemUseOnEntityInstance useOnEntityInstance) {
                    return useOnEntityInstance.getUseOnEntity().useItemOnEntity(stack, user, entity, hand);
                }
            }
        }

        return this.useOnEntity(stack, user, entity, hand);
    }

    @Override
    @Nullable
    public UseInstance getUseInstance(Identifier id) {
        if(this.availableItemUseActions.containsKey(id)){
            return this.availableItemUseActions.get(id);
        }
        if(this.availableItemUseOnEntityActions.containsKey(id)){
            return this.availableItemUseOnEntityActions.get(id);
        }
        if(this.availableItemUseOnBlockActions.containsKey(id)){
            return this.availableItemUseOnBlockActions.get(id);
        }

        return null;
    }

    @Override
    public Set<Identifier> getAllUseActionIds() {
        ImmutableSet.Builder<Identifier> builder = ImmutableSet.builder();
        return builder.addAll(this.availableItemUseActions.keySet()).build();
    }

    @Override
    public Set<Identifier> getAllUseOnBlockActionIds() {
        ImmutableSet.Builder<Identifier> builder = ImmutableSet.builder();
        return builder.addAll(this.availableItemUseOnBlockActions.keySet()).build();
    }

    @Override
    public Set<Identifier> getAllUseOnEntityActionIds() {
        ImmutableSet.Builder<Identifier> builder = ImmutableSet.builder();
        return builder.addAll(this.availableItemUseOnEntityActions.keySet()).build();
    }

    @Override
    public void addUseAction(Identifier id, ItemUseInstance useInstance) {
        if(this.availableItemUseActions.containsKey(id)){
            Class<?> clazz = this.availableItemUseActions.get(id).getClassLevel();
            if(!shouldOverrideUseInstance(clazz, useInstance.getClassLevel())){
                this.informAboutIgnoredUseAction(id, useInstance.getClassLevel());

                return;
            }
        }

        this.availableItemUseActions.put(id, useInstance);
    }

    @Override
    public void addUseOnBlockAction(Identifier id, ItemUseOnBlockInstance useInstance) {
        if(this.availableItemUseOnBlockActions.containsKey(id)){
            Class<?> clazz = this.availableItemUseOnBlockActions.get(id).getClassLevel();
            if(!shouldOverrideUseInstance(clazz, useInstance.getClassLevel())){
                this.informAboutIgnoredUseAction(id, useInstance.getClassLevel());

                return;
            }
        }

        this.availableItemUseOnBlockActions.put(id, useInstance);
    }

    @Override
    public void addUseOnEntityAction(Identifier id, ItemUseOnEntityInstance useInstance) {
        if(this.availableItemUseOnEntityActions.containsKey(id)){
            Class<?> clazz = this.availableItemUseOnEntityActions.get(id).getClassLevel();
            if(!shouldOverrideUseInstance(clazz, useInstance.getClassLevel())){
                this.informAboutIgnoredUseAction(id, useInstance.getClassLevel());

                return;
            }
        }

        this.availableItemUseOnEntityActions.put(id, useInstance);
    }

    private boolean shouldOverrideUseInstance(Class<?> presentUseInstanceClass, Class<?> newUseInstanceClass) {
        return presentUseInstanceClass != newUseInstanceClass && presentUseInstanceClass.isAssignableFrom(newUseInstanceClass);
    }

    private void informAboutIgnoredUseAction(Identifier id, Class<?> clazz) {
        MultipleUseActions.LOGGER.warn(
                "Due to existing use action, the following use action for {} has not been added: {}, with class level: {}",
                this.asItem().getClass().getName(),
                id.toString(),
                clazz.getName()
        );
    }

}
