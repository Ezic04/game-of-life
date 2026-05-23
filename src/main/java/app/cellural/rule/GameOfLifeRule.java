package app.cellural.rule;

import app.cellural.BinCell;
import app.cellural.Direction;
import app.data.Zipper2D;

public class GameOfLifeRule implements Rule<Zipper2D<BinCell>, BinCell> {

    @Override
    public BinCell apply(Zipper2D<BinCell> t) {
        int count = 0;
        Direction[] dirs = Direction.values();
        for (Direction dir : dirs) {
            Zipper2D<BinCell> moved = t.move(dir);
            count += moved.extract() == BinCell.ALIVE ? 1 : 0;

            Direction nextDir = dirs[(dir.ordinal() + 1) % dirs.length];
            count += moved.move(nextDir).extract() == BinCell.ALIVE ? 1 : 0;
        }

        BinCell current = t.extract();
        if (current == BinCell.ALIVE) {
            return count == 2 || count == 3 ? BinCell.ALIVE : BinCell.DEAD;
        } else {
            return count == 3 ? BinCell.ALIVE : BinCell.DEAD;
        }
    }

}
