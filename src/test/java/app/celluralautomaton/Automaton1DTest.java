package app.celluralautomaton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.function.BiFunction;
import java.util.function.Function;

import app.cellural.BinCell;
import app.cellural.automaton.Automaton;
import app.cellural.automaton.StoreAutomaton;
import app.cellural.automaton.ZipperAutomaton;
import app.cellural.rule.StoreRule;
import app.cellural.rule.ZipperRule;
import app.control.Comonad;
import app.control.ComonadStore;
import app.data.Store;
import app.data.Zipper;
import app.data.Stream;

public class Automaton1DTest {
    private final int ruleNumber = 30;
    private final int boardSize = 15;

    Function<Integer, BinCell> initStore = i -> (i == 0 || i == 1) ? BinCell.ALIVE : BinCell.DEAD;
    StoreAutomaton<Integer, BinCell> storeAuto = new StoreAutomaton<>(new Store<>(0, initStore),
            new StoreRule(ruleNumber));

    Stream<BinCell> left = Stream.repeat(BinCell.DEAD);
    Stream<BinCell> rightTail = Stream.repeat(BinCell.DEAD);
    Stream<BinCell> right = new Stream<>(BinCell.ALIVE, () -> rightTail);
    ZipperAutomaton<BinCell> zipperAuto = new ZipperAutomaton<>(new Zipper.Zip<>(left, BinCell.ALIVE, right),
            new ZipperRule(ruleNumber));

    private <Context extends Comonad<BinCell>> void printGrid(
            Context state,
            BiFunction<Context, Integer, BinCell> accessor) {
        for (int i = -boardSize; i <= boardSize; i++) {
            System.out.print(accessor.apply(state, i) == BinCell.ALIVE ? "[X]" : "[ ]");
        }
        System.out.println();
    }

    private <Context extends Comonad<BinCell>> void verifyAutomaton(
            Automaton<Context, BinCell> automaton,
            BiFunction<Context, Integer, BinCell> accessor,
            String name) {

        long start = System.nanoTime();

        Automaton<Context, BinCell> current = automaton;
        for (int i = 0; i < boardSize; i++) {
            printGrid(current.state(), accessor);
            current = current.step();
        }

        long end = System.nanoTime();
        System.out.println(name + " timing: " + (end - start) / 1000000.0 + " ms");

        Assertions.assertNotNull(current.state());
        Assertions.assertEquals(BinCell.ALIVE, accessor.apply(current.state(), 0));
        Assertions.assertEquals(BinCell.DEAD, accessor.apply(current.state(), -1));
    }

    @Test
    public void testStoreAutomaton() {
        StoreAutomaton<Integer, BinCell> automaton = new StoreAutomaton<>(
                new Store<>(0, initStore),
                new StoreRule(ruleNumber));
        verifyAutomaton(automaton, ComonadStore::peek, "Store");
    }

    @Test
    public void testZipperAutomaton() {
        ZipperAutomaton<BinCell> automaton = zipperAuto;
        verifyAutomaton(automaton, Zipper::getAt, "Zipper");
    }

    @Test
    public void testCompareAutomatons() {
        StoreAutomaton<Integer, BinCell> store = storeAuto;
        ZipperAutomaton<BinCell> zipper = zipperAuto;
        for (int i = 0; i < boardSize; i++) {
            store = store.step();
            zipper = zipper.step();
        }
        for (int i = -boardSize; i <= boardSize; i++) {
            Assertions.assertEquals(store.state().peek(i), zipper.state().getAt(i));
        }
    }
}