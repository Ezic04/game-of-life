package app.celluralautomaton;

import app.data.Zipper;

public class ZipperRule implements Rule<Zipper<BinCell>, BinCell> {

    @Override
    public BinCell apply(Zipper<BinCell> st) {
        int leftBit = st.moveLeft().extract() == BinCell.Alive ? 1 : 0;
        int currentBit = st.extract() == BinCell.Alive ? 1 : 0;
        int rightBit = st.moveRight().extract() == BinCell.Alive ? 1 : 0;

        int neighborhood = (leftBit << 2) | (currentBit << 1) | rightBit;
        int nextState = (30 >> neighborhood) & 1;

        return nextState == 1 ? BinCell.Alive : BinCell.Dead;
    }

}
