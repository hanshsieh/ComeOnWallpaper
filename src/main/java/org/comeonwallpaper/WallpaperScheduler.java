package org.comeonwallpaper;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class schedules periodic update of the wallpaper.
 */
@ThreadSafe
public class WallpaperScheduler implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(WallpaperScheduler.class);
    private final WallpaperRenderer renderer;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> runningJob;
    public WallpaperScheduler(@NonNull WallpaperRenderer renderer) {
        this.renderer = renderer;
        this.executor = Executors.newSingleThreadScheduledExecutor((runnable) -> {
           Thread thread = new Thread(runnable);
           thread.setDaemon(true);
           return thread;
        });
    }

    /**
     * Starts periodic update of the wallpaper at the specified rate.
     * If this scheduler is already running, it will be stopped first.
     *
     * @param interval Interval between each update.
     * @param timeUnit The unit of the time.
     */
    public synchronized void schedule(long interval, TimeUnit timeUnit) {
        stop();
        runningJob = executor.scheduleWithFixedDelay(() -> {
            try {
                renderer.render();
            } catch (IOException ex) {
                logger.error("Failed to render wallpaper: ", ex);
            }
        }, 0, interval, timeUnit);
    }

    public boolean isRunning() {
        return runningJob != null;
    }

    /**
     * Stops the running schedule.
     * If the scheduler isn't running, it has no effect.
     */
    public void stop() {
        if (isRunning()) {
            runningJob.cancel(true);
            runningJob = null;
        }
    }

    @Override
    public void close() throws IOException {
        renderer.close();
        executor.shutdownNow();
    }
}
