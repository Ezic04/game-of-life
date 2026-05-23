package app.cellural.rule;

import java.util.function.Function;
import app.control.Comonad;

public interface Rule<Context extends Comonad<Cell>, Cell> extends Function<Context, Cell> {
}
