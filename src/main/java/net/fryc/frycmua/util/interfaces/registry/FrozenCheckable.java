package net.fryc.frycmua.util.interfaces.registry;

public interface FrozenCheckable {

    boolean isAlreadyFrozen();

    void throwIfFrozen();
}
