package app.cellural.automaton;

import app.control.Comonad;

public interface Automaton<Context extends Comonad<Cell>, Cell> {

    Automaton<Context, Cell> step();

    Context state();

}
