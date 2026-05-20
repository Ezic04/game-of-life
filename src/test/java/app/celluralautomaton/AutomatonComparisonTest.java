package app.celluralautomaton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.function.Function;
import app.control.ComonadStore;
import app.data.Store;
import app.data.Zipper;
import app.data.Stream;

public class AutomatonComparisonTest {

    private void printStore(ComonadStore<Integer, BinCell> state) {
        for (int i = -15; i <= 15; i++) {
            System.out.print(state.peek(i) == BinCell.Alive ? "[X]" : "[ ]");
        }
        System.out.println();
    }

    private void printZipper(Zipper<BinCell> state) {
        Zipper<BinCell> current = state;
        for (int i = 0; i < 15; i++) {
            current = current.moveLeft();
        }
        for (int i = -15; i <= 15; i++) {
            System.out.print(current.extract() == BinCell.Alive ? "[X]" : "[ ]");
            current = current.moveRight();
        }
        System.out.println();
    }

    @Test
    public void testStoreAutomaton() {
        long start = System.nanoTime();
        Function<Integer, BinCell> init = i -> (i == 0 || i == 1) ? BinCell.Alive : BinCell.Dead;
        StoreAutomaton<Integer, BinCell> automaton = new StoreAutomaton<>(new Store<>(0, init), new StoreRule());

        for (int i = 0; i < 15; i++) {
            printStore(automaton.state());
            automaton = automaton.step();
        }
        long end = System.nanoTime();
        System.out.println("Store timing: " + (end - start) / 1000000.0 + " ms");

        Assertions.assertNotNull(automaton.state());
        Assertions.assertEquals(BinCell.Alive, automaton.state().peek(0));
        Assertions.assertEquals(BinCell.Dead, automaton.state().peek(-1));
    }

    @Test
    public void testZipperAutomaton() {
        long start = System.nanoTime();
        Stream<BinCell> left = Stream.iterateTail(x -> BinCell.Dead, BinCell.Dead);
        Stream<BinCell> rightTail = Stream.iterateTail(x -> BinCell.Dead, BinCell.Dead);
        Stream<BinCell> right = new Stream<>(BinCell.Alive, () -> rightTail);
        ZipperAutomaton<BinCell> automaton = new ZipperAutomaton<>(new Zipper.Zip<>(left, BinCell.Alive, right),
                new ZipperRule());

        for (int i = 0; i < 15; i++) {
            printZipper(automaton.state());
            automaton = automaton.step();
        }
        long end = System.nanoTime();
        System.out.println("Zipper timing: " + (end - start) / 1000000.0 + " ms");

        Assertions.assertNotNull(automaton.state());
        Assertions.assertEquals(BinCell.Alive, automaton.state().extract());
        Assertions.assertEquals(BinCell.Dead, automaton.state().getAt(-1));
    }

    @Test
    public void testCompareAutomatons() {
        Function<Integer, BinCell> initStore = i -> (i == 0 || i == 1) ? BinCell.Alive : BinCell.Dead;
        StoreAutomaton<Integer, BinCell> storeAuto = new StoreAutomaton<>(new Store<>(0, initStore), new StoreRule());

        Stream<BinCell> left = Stream.iterateTail(x -> BinCell.Dead, BinCell.Dead);
        Stream<BinCell> rightTail = Stream.iterateTail(x -> BinCell.Dead, BinCell.Dead);
        Stream<BinCell> right = new Stream<>(BinCell.Alive, () -> rightTail);
        ZipperAutomaton<BinCell> zipperAuto = new ZipperAutomaton<>(new Zipper.Zip<>(left, BinCell.Alive, right),
                new ZipperRule());

        for (int i = 0; i < 15; i++) {
            storeAuto = storeAuto.step();
            zipperAuto = zipperAuto.step();
        }

        for (int i = -15; i <= 15; i++) {
            Assertions.assertEquals(storeAuto.state().peek(i), zipperAuto.state().getAt(i));
        }
    }
}