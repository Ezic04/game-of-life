package app.cellural.automaton;

import app.cellural.rule.Rule;
import app.data.Zipper2D;

public record Automaton2D<Cell>(Zipper2D<Cell> state, Rule<Zipper2D<Cell>, Cell> rule)
        implements Automaton<Zipper2D<Cell>, Cell> {

    public Automaton2D<Cell> step() {
        return new Automaton2D<>(state.extendZipper2D(rule), rule);
    }

}