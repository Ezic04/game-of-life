package app.celluralautomaton;

import app.control.ComonadStore;

public record StoreAutomaton<Index, Cell>(
        ComonadStore<Index, Cell> state,
        Rule<ComonadStore<Index, Cell>, Cell> rule) implements Automaton<Cell> {

    public StoreAutomaton<Index, Cell> step() {
        return new StoreAutomaton<>(state.extendStore(rule), rule);
    }
}