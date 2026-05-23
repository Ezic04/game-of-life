package app.data;

import java.util.function.Supplier;

public final class MemoizedSupplier<T> implements Supplier<T> {

    private Supplier<T> task;
    private T value;

    public MemoizedSupplier(Supplier<T> task) {
        this.task = task;
    }

    @Override
    public T get() {
        if (task != null) {
            value = task.get();
            task = null;
        }
        return value;
    }
}