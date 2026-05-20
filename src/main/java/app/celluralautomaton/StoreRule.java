package app.celluralautomaton;

import app.control.ComonadStore;

public class StoreRule implements Rule<ComonadStore<Integer, BinCell>, BinCell> {

    @Override
    public BinCell apply(ComonadStore<Integer, BinCell> st) {
        int leftBit = st.peek(st.pos() - 1) == BinCell.Alive ? 1 : 0;
        int currentBit = st.extract() == BinCell.Alive ? 1 : 0;
        int rightBit = st.peek(st.pos() + 1) == BinCell.Alive ? 1 : 0;

        int neighborhood = (leftBit << 2) | (currentBit << 1) | rightBit;
        int nextState = (30 >> neighborhood) & 1;

        return nextState == 1 ? BinCell.Alive : BinCell.Dead;
    }

}