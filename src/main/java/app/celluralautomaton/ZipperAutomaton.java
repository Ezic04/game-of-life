package app.celluralautomaton;

import app.data.Zipper;

public record ZipperAutomaton<Cell>(Zipper<Cell> state, Rule<Zipper<Cell>, Cell> rule) implements Automaton<Cell> {

	public ZipperAutomaton<Cell> step() {
		return new ZipperAutomaton<>(state.extendZipper(rule), rule);
	}
}
