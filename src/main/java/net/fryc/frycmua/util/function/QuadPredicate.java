package net.fryc.frycmua.util.function;


@FunctionalInterface
public interface QuadPredicate<T, U, V, W> {
    boolean test(T t, U u, V v, W w);
}
