package net.fryc.frycmua.item.action;

import net.fryc.frycmua.item.action.registry.UseActionRegistries;

public record ItemUseInstance(
        Class<?> classLevel,
        boolean allowSubclasses,
        UseActionRegistries.ItemUse use,
        UseActionRegistries.ItemTickUsage tickUsage,
        UseActionRegistries.ItemOnStoppedUsing onStoppedUsing,
        UseActionRegistries.ItemFinishUsing finishUsing,
        UseActionRegistries.ItemMaxUseTime maxUseTime,
        UseActionRegistries.ItemUseAction useAction
) { }
