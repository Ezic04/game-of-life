package app.cellural.rule;

import app.cellural.BinCell;
import app.data.Zipper;

public class ZipperRule extends Rule1D<Zipper<BinCell>, BinCell> {

    public ZipperRule(int ruleNumber) {
        super(ruleNumber);
    }

    @Override
    public BinCell apply(Zipper<BinCell> st) {
        int leftBit = st.moveLeft().extract() == BinCell.ALIVE ? 1 : 0;
        int currentBit = st.extract() == BinCell.ALIVE ? 1 : 0;
        int rightBit = st.moveRight().extract() == BinCell.ALIVE ? 1 : 0;

        int neighborhood = (leftBit << 2) | (currentBit << 1) | rightBit;
        int nextState = (ruleNumber >> neighborhood) & 1;

        return nextState == 1 ? BinCell.ALIVE : BinCell.DEAD;
    }

}
