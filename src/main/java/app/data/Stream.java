package app.data;

import java.util.function.Function;
import java.util.function.Supplier;
import app.control.Functor;

public record Stream<T>(T head, Supplier<Stream<T>> tail) implements Functor<T> {

    public Stream {
        if (!(tail instanceof MemoizedSupplier)) {
            tail = new MemoizedSupplier<>(tail);
        }
    }

    @Override
    public <R> Stream<R> map(Function<T, R> f) {
        return new Stream<>(f.apply(head), () -> tail.get().map(f));
    }

    public static <T> Stream<T> iterate(Function<T, T> f, T t) {
        return new Stream<>(t, () -> iterate(f, f.apply(t)));
    }

    public static <T> Stream<T> iterateTail(Function<T, T> f, T t) {
        return iterate(f, t).tail().get();
    }

    public static <T> Stream<T> repeat(T t) {
        return new Stream<>(t, () -> repeat(t));
    }

    public static <T> Stream<T> repeatM(Supplier<T> t) {
        return new Stream<>(t.get(), () -> repeatM(t));
    }

    public Stream<T> cropStream(int limit, T fallback) {
        if (limit <= 0) {
            return repeat(fallback);
        }
        Stream<T> evaluatedTail = tail.get().cropStream(limit - 1, fallback);
        return new Stream<>(head, () -> evaluatedTail);
    }
}