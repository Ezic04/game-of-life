package app.data;

import java.util.function.Function;
import java.util.function.Supplier;
import app.control.Functor;

public record Stream<T>(T head, Supplier<Stream<T>> tail) implements Functor<T> {

    @Override
    public <R> Stream<R> map(Function<T, R> f) {
        return new Stream<>(f.apply(head), () -> tail.get().map(f));
    }

    public static <T> Stream<T> iterateTail(Function<T, T> f, T seed) {
        T next = f.apply(seed);
        return new Stream<>(next, () -> iterateTail(f, next));
    }
}