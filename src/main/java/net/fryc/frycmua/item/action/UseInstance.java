package net.fryc.frycmua.item.action;

import net.fryc.frycmua.item.action.registry.UseActionRegistries;

public abstract class UseInstance {

    private final Class<?> classLevel;
    private final boolean allowSubclasses;
    private final UseActionRegistries.ItemTickUsage tickUsage;
    private final UseActionRegistries.ItemOnStoppedUsing onStoppedUsing;
    private final UseActionRegistries.ItemFinishUsing finishUsing;
    private final UseActionRegistries.ItemMaxUseTime maxUseTime;
    private final UseActionRegistries.ItemUseAction useAction;

    public UseInstance(
            Class<?> classLevel,
            boolean allowSubclasses,
            UseActionRegistries.ItemTickUsage tickUsage,
            UseActionRegistries.ItemOnStoppedUsing onStoppedUsing,
            UseActionRegistries.ItemFinishUsing finishUsing,
            UseActionRegistries.ItemMaxUseTime maxUseTime,
            UseActionRegistries.ItemUseAction useAction
    ){
        this.classLevel = classLevel;
        this.allowSubclasses = allowSubclasses;
        this.tickUsage = tickUsage;
        this.onStoppedUsing = onStoppedUsing;
        this.finishUsing = finishUsing;
        this.maxUseTime = maxUseTime;
        this.useAction = useAction;
    }


    public Class<?> getClassLevel() {
        return this.classLevel;
    }

    public boolean isAllowSubclasses() {
        return this.allowSubclasses;
    }

    public UseActionRegistries.ItemTickUsage getTickUsage() {
        return this.tickUsage;
    }

    public UseActionRegistries.ItemOnStoppedUsing getOnStoppedUsing() {
        return this.onStoppedUsing;
    }

    public UseActionRegistries.ItemFinishUsing getFinishUsing() {
        return this.finishUsing;
    }

    public UseActionRegistries.ItemMaxUseTime getMaxUseTime() {
        return this.maxUseTime;
    }

    public UseActionRegistries.ItemUseAction getUseAction() {
        return this.useAction;
    }
}
