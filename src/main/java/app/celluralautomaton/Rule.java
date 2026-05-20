package app.celluralautomaton;

import java.util.function.Function;
import app.control.Comonad;

public interface Rule<Context extends Comonad<State>, State> extends Function<Context, State> {
}
