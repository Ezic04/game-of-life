package app.celluralautomaton;

public interface Automaton<Cell> {
    Automaton<Cell> step();
}
