package net.fryc.frycmua.util.mixin_interfaces.registry;

public interface FrozenCheckable {

    boolean isAlreadyFrozen();

    void throwIfFrozen();
}
