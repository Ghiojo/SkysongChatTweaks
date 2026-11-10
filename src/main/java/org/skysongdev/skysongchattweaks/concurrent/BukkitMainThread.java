package org.skysongdev.skysongchattweaks.concurrent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/** {@link MainThread} on the Bukkit scheduler. */
public final class BukkitMainThread implements MainThread {

    /** How long a chat thread waits for a read before leaving the message as typed. */
    private static final long READ_TIMEOUT_MS = 1000;

    private final Plugin plugin;

    public BukkitMainThread(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T> T read(Callable<T> read, T fallback) {
        if (Bukkit.isPrimaryThread()) {
            try {
                return read.call();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "A SkysongStats read failed", e);
                return fallback;
            }
        }
        Future<T> result = Bukkit.getScheduler().callSyncMethod(plugin, read);
        try {
            return result.get(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            result.cancel(false);
            plugin.getLogger().warning("A SkysongStats read took longer than " + READ_TIMEOUT_MS + " ms; skipped it.");
            return fallback;
        } catch (ExecutionException e) {
            plugin.getLogger().log(Level.WARNING, "A SkysongStats read failed", e.getCause());
            return fallback;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fallback;
        }
    }

    @Override
    public void run(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
