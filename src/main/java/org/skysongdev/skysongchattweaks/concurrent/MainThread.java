package org.skysongdev.skysongchattweaks.concurrent;

import java.util.concurrent.Callable;

/**
 * Runs work on the server's main thread. Most SkysongStats API calls throw when called from
 * another thread, and chat events often fire on one.
 */
public interface MainThread {

    /**
     * Runs the read on the main thread and waits a short time for its result: directly when
     * already on it.
     *
     * @return the result, or the fallback when the read failed or took too long
     */
    <T> T read(Callable<T> read, T fallback);

    /** Runs the task on the main thread: now when already on it, otherwise on the next tick without waiting. */
    void run(Runnable task);
}
