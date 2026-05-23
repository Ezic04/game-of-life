package app.data;

import java.util.function.Function;

import app.cellural.Direction;
import app.control.Comonad;

public sealed interface Zipper2D<T> extends Comonad<T> permits Zipper2D.Zip2D {

    @Override
    <R> Zipper2D<R> map(Function<T, R> f);

    Zipper2D<T> move(Direction direction);

    default Zipper2D<Zipper2D<T>> duplicateZipper2D() {
        return extendZipper2D(z -> z);
    }

    default <R> Zipper2D<R> extendZipper2D(Function<Zipper2D<T>, R> f) {
        return duplicateZipper2D().map(f);
    }

    @Override
    default Zipper2D<Comonad<T>> duplicate() {
        return duplicateZipper2D().map(z -> z);
    }

    @Override
    default <R> Zipper2D<R> extend(Function<Comonad<T>, R> f) {
        return duplicate().map(f);
    }

    default T getAt(int x, int y) {
        Zipper2D<T> current = this;
        if (x != 0) {
            Direction dirX = x < 0 ? Direction.LEFT : Direction.RIGHT;
            current = java.util.stream.Stream.iterate(current, z -> z.move(dirX))
                    .skip(Math.abs(x))
                    .findFirst()
                    .orElseThrow();
        }
        if (y != 0) {
            Direction dirY = y < 0 ? Direction.UP : Direction.DOWN;
            current = java.util.stream.Stream.iterate(current, z -> z.move(dirY))
                    .skip(Math.abs(y))
                    .findFirst()
                    .orElseThrow();
        }
        return current.extract();
    }

    Zipper2D<T> crop(int radiusX, int radiusY, T fallback);

    record Zip2D<T>(Zipper<Zipper<T>> grid) implements Zipper2D<T> {

        @Override
        public <R> Zip2D<R> map(Function<T, R> f) {
            return new Zip2D<>(grid.map(row -> row.map(f)));
        }

        @Override
        public T extract() {
            return grid.extract().extract();
        }

        @Override
        public Zipper2D<T> move(Direction direction) {
            return switch (direction) {
                case UP -> new Zip2D<>(grid.moveLeft());
                case DOWN -> new Zip2D<>(grid.moveRight());
                case LEFT -> new Zip2D<>(grid.map(Zipper::moveLeft));
                case RIGHT -> new Zip2D<>(grid.map(Zipper::moveRight));
            };
        }

        @Override
        public Zip2D<Zipper2D<T>> duplicateZipper2D() {
            Function<Zipper2D<T>, Zipper<Zipper2D<T>>> makeRow = z -> new Zipper.Zip<>(
                    Stream.iterateTail(curr -> curr.move(Direction.LEFT), z),
                    z,
                    Stream.iterateTail(curr -> curr.move(Direction.RIGHT), z));

            Zipper2D<T> self = this;

            Zipper<Zipper<Zipper2D<T>>> plane = new Zipper.Zip<>(
                    Stream.iterateTail(curr -> curr.move(Direction.UP), self).map(makeRow),
                    makeRow.apply(self),
                    Stream.iterateTail(curr -> curr.move(Direction.DOWN), self).map(makeRow));

            return new Zip2D<>(plane);
        }

        @Override
        public Zip2D<T> crop(int radiusX, int radiusY, T fallback) {
            Zipper<Zipper<T>> newGrid = new Zipper.Zip<>(
                    cropRows(grid.ls(), radiusX, radiusY, fallback),
                    grid.extract().crop(radiusX, fallback),
                    cropRows(grid.rs(), radiusX, radiusY, fallback));
            return new Zip2D<>(newGrid);
        }

        private Stream<Zipper<T>> cropRows(Stream<Zipper<T>> stream, int radiusX, int radiusY, T fallback) {
            if (radiusY <= 0) {
                Zipper<T> deadRow = new Zipper.Zip<>(
                        Stream.iterateTail(x -> fallback, fallback),
                        fallback,
                        Stream.iterateTail(x -> fallback, fallback));
                return Stream.iterateTail(x -> deadRow, deadRow);
            }
            Zipper<T> croppedRow = stream.head().crop(radiusX, fallback);
            Stream<Zipper<T>> evaluatedTail = cropRows(stream.tail().get(), radiusX, radiusY - 1, fallback);
            return new Stream<>(croppedRow, () -> evaluatedTail);
        }

    }
}