package app.cellural.automaton;

import app.cellural.rule.Rule;
import app.data.Zipper;

public record ZipperAutomaton<Cell>(Zipper<Cell> state, Rule<Zipper<Cell>, Cell> rule)
		implements Automaton<Zipper<Cell>, Cell> {

	public ZipperAutomaton<Cell> step() {
		return new ZipperAutomaton<>(state.extendZipper(rule), rule);
	}

}
