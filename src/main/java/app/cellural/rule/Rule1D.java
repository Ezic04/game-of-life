package app.cellural.rule;

import app.control.Comonad;

abstract class Rule1D<Context extends Comonad<Cell>, Cell> implements Rule<Context, Cell> {

    protected final int ruleNumber;

    public Rule1D(int ruleNumber) {
        if (ruleNumber < 0 || ruleNumber > 255) {
            throw new IllegalArgumentException();
        }
        this.ruleNumber = ruleNumber;
    }
}