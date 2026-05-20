package app.data;

import java.util.function.Function;

import app.control.Comonad;
import app.control.ComonadStore;

public class Store<S, T> implements ComonadStore<S, T> {
    public final S s;
    public final Function<S, T> f;

    public Store(S s, Function<S, T> f) {
        this.s = s;
        this.f = f;
    }

    @Override
    public <R> ComonadStore<S, R> map(Function<T, R> f) {
        return new Store<S, R>(s, this.f.andThen(f));
    }

    @Override
    public ComonadStore<S, Comonad<T>> duplicate() {
        return new Store<S, Comonad<T>>(s, newS -> new Store<>(newS, f));
    }

    @Override
    public ComonadStore<S, ComonadStore<S, T>> duplicateStore() {
        return new Store<S, ComonadStore<S, T>>(s, newS -> new Store<>(newS, f));
    }

    @Override
    public T peek(S s) {
        return f.apply(s);
    }

    @Override
    public S pos() {
        return s;
    }
}
