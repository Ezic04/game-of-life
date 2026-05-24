package app.celluralautomaton;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.cellural.BinCell;
import app.cellural.automaton.Automaton2D;
import app.cellural.rule.GameOfLifeRule;
import app.data.Stream;
import app.data.Zipper;
import app.data.Zipper2D;

public class GameOfLifeTest {

    @Test
    public void testGameOfLifeBlinker() {
        Stream<BinCell> deadLeft = Stream.repeat(BinCell.DEAD);
        Stream<BinCell> deadRight = Stream.repeat(BinCell.DEAD);
        Zipper<BinCell> deadRow = new Zipper.Zip<>(deadLeft, BinCell.DEAD, deadRight);

        Stream<BinCell> blinkerLeft = new Stream<>(BinCell.ALIVE, () -> Stream.repeat(BinCell.DEAD));
        Stream<BinCell> blinkerRight = new Stream<>(BinCell.ALIVE, () -> Stream.repeat(BinCell.DEAD));
        Zipper<BinCell> blinkerRow = new Zipper.Zip<>(blinkerLeft, BinCell.ALIVE, blinkerRight);

        Stream<Zipper<BinCell>> deadUp = Stream.repeat(deadRow);
        Stream<Zipper<BinCell>> deadDown = Stream.repeat(deadRow);

        Zipper<Zipper<BinCell>> grid = new Zipper.Zip<>(deadUp, blinkerRow, deadDown);
        Zipper2D<BinCell> board = new Zipper2D.Zip2D<>(grid);

        Automaton2D<BinCell> gameOfLife = new Automaton2D<>(board, new GameOfLifeRule());

        assertEquals(BinCell.ALIVE, gameOfLife.state().getAt(0, 0));
        assertEquals(BinCell.ALIVE, gameOfLife.state().getAt(-1, 0));
        assertEquals(BinCell.ALIVE, gameOfLife.state().getAt(1, 0));
        assertEquals(BinCell.DEAD, gameOfLife.state().getAt(0, -1));
        assertEquals(BinCell.DEAD, gameOfLife.state().getAt(0, 1));

        Automaton2D<BinCell> nextGen = gameOfLife.step();
        Zipper2D<BinCell> nextState = nextGen.state();

        assertEquals(BinCell.ALIVE, nextState.getAt(0, 0));
        assertEquals(BinCell.DEAD, nextState.getAt(-1, 0));
        assertEquals(BinCell.DEAD, nextState.getAt(1, 0));
        assertEquals(BinCell.ALIVE, nextState.getAt(0, -1));
        assertEquals(BinCell.ALIVE, nextState.getAt(0, 1));
    }

}
