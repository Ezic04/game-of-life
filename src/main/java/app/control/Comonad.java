package app.control;

import java.util.function.Function;

public interface Comonad<T> extends Functor<T> {
    @Override
    <R> Comonad<R> map(Function<T, R> f);

    T extract();

    default Comonad<Comonad<T>> duplicate() {
        return extend(c -> c);
    }

    default <R> Comonad<R> extend(Function<Comonad<T>, R> f) {
        return duplicate().map(f);
    }
}
