package app.cellural.rule;

import app.cellural.BinCell;
import app.control.ComonadStore;

public class StoreRule extends Rule1D<ComonadStore<Integer, BinCell>, BinCell> {

    public StoreRule(int ruleNumber) {
        super(ruleNumber);
    }

    @Override
    public BinCell apply(ComonadStore<Integer, BinCell> st) {
        int leftBit = st.peek(st.pos() - 1) == BinCell.ALIVE ? 1 : 0;
        int currentBit = st.extract() == BinCell.ALIVE ? 1 : 0;
        int rightBit = st.peek(st.pos() + 1) == BinCell.ALIVE ? 1 : 0;

        int neighborhood = (leftBit << 2) | (currentBit << 1) | rightBit;
        int nextState = (ruleNumber >> neighborhood) & 1;

        return nextState == 1 ? BinCell.ALIVE : BinCell.DEAD;
    }
}