package app.data;

import java.util.function.Function;

import app.control.Comonad;

public sealed interface Zipper<T> extends Comonad<T> permits Zipper.Zip {

    @Override
    <R> Zipper<R> map(Function<T, R> f);

    default Zipper<Zipper<T>> duplicateZipper() {
        return extendZipper(z -> z);
    }

    default <R> Zipper<R> extendZipper(Function<Zipper<T>, R> f) {
        return duplicateZipper().map(f);
    }

    @Override
    default Zipper<Comonad<T>> duplicate() {
        return duplicateZipper().map(z -> z);
    }

    @Override
    default <R> Zipper<R> extend(Function<Comonad<T>, R> f) {
        return duplicate().map(f);
    }

    Zipper<T> moveLeft();

    Zipper<T> moveRight();

    default T getAt(int index) {
        if (index == 0) {
            return extract();
        }
        java.util.function.UnaryOperator<Zipper<T>> direction = index < 0 ? Zipper::moveLeft : Zipper::moveRight;
        return java.util.stream.Stream.iterate(this, direction)
                .skip(Math.abs(index))
                .findFirst()
                .orElseThrow()
                .extract();
    }

    record Zip<T>(Stream<T> ls, T focus, Stream<T> rs) implements Zipper<T> {

        @Override
        public <R> Zip<R> map(Function<T, R> f) {
            return new Zip<>(ls.map(f), f.apply(focus), rs.map(f));
        }

        @Override
        public T extract() {
            return focus;
        }

        @Override
        public Zip<T> moveLeft() {
            return new Zip<>(ls.tail().get(), ls.head(), new Stream<>(focus, () -> rs));
        }

        @Override
        public Zip<T> moveRight() {
            return new Zip<>(new Stream<>(focus, () -> ls), rs.head(), rs.tail().get());
        }

        @Override
        public Zip<Zipper<T>> duplicateZipper() {
            return new Zip<>(
                    Stream.iterateTail(Zipper::moveLeft, this),
                    this,
                    Stream.iterateTail(Zipper::moveRight, this));
        }
    }
}