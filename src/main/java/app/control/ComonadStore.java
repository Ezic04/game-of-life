package app.control;

import java.util.function.Function;

public interface ComonadStore<S, T> extends Comonad<T> {

    @Override
    <R> ComonadStore<S, R> map(Function<T, R> f);

    @Override
    default ComonadStore<S, Comonad<T>> duplicate() {
        return extend(c -> c);
    }

    @Override
    default <R> ComonadStore<S, R> extend(Function<Comonad<T>, R> f) {
        return duplicate().map(f);
    }

    default ComonadStore<S, ComonadStore<S, T>> duplicateStore() {
        return extendStore(c -> c);
    }

    default <R> ComonadStore<S, R> extendStore(Function<ComonadStore<S, T>, R> f) {
        return duplicateStore().map(f);
    }

    @Override
    default T extract() {
        return peek(pos());
    }

    T peek(S s);

    S pos();

}