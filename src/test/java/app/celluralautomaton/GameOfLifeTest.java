package app.celluralautomaton;

import org.junit.jupiter.api.Test;

import app.cellural.BinCell;
import app.cellural.automaton.Automaton2D;
import app.cellural.rule.GameOfLifeRule;
import app.data.Stream;
import app.data.Zipper;
import app.data.Zipper2D;

public class GameOfLifeTest {

    @Test
    public void testGameOfLive() {

        Zipper<BinCell> zipper = new Zipper.Zip<>(
                Stream.repeat(BinCell.DEAD),
                BinCell.ALIVE,
                Stream.repeat(BinCell.DEAD));
        Zipper2D<BinCell> board = new Zipper2D.Zip2D<>(zipper.duplicateZipper());
        Automaton2D<BinCell> gameOfLife = new Automaton2D<>(board, new GameOfLifeRule());

    }

}
