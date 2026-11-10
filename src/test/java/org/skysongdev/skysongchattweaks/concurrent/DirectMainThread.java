package org.skysongdev.skysongchattweaks.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/** A {@link MainThread} for tests: reads run at once, tasks wait until {@link #runPending()}. */
public final class DirectMainThread implements MainThread {

    private final List<Runnable> pending = new ArrayList<>();
    private boolean readsFail;

    /** Makes every read return its fallback, as a timed out read does. */
    public void failReads() {
        readsFail = true;
    }

    @Override
    public <T> T read(Callable<T> read, T fallback) {
        if (readsFail) {
            return fallback;
        }
        try {
            return read.call();
        } catch (Exception e) {
            return fallback;
        }
    }

    @Override
    public void run(Runnable task) {
        pending.add(task);
    }

    /** Runs the tasks handed to {@link #run(Runnable)} so far. */
    public void runPending() {
        List<Runnable> tasks = new ArrayList<>(pending);
        pending.clear();
        tasks.forEach(Runnable::run);
    }

    public int pendingCount() {
        return pending.size();
    }
}
