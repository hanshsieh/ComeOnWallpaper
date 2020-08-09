package org.comeonwallpaper;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImgObserverFuture implements Future<Image>, ImageObserver {

    private transient Image observedImg;
    private transient Throwable error;
    private transient boolean canceled;
    private transient boolean done;

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (done) {
            return false;
        }
        canceled = true;
        done = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public synchronized Image get() throws InterruptedException, ExecutionException {
        if (!done) {
            wait();
        }
        if (observedImg == null) {
            throw new ExecutionException("Error occurs when doing image update", error);
        }
        return observedImg;
    }

    @Override
    public Image get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!done) {
            wait(unit.toMillis(timeout));
        }
        if (observedImg == null) {
            throw new ExecutionException("Error occurs when doing image update", error);
        }
        return observedImg;
    }

    @Override
    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & ImageObserver.ABORT) != 0) {
            // Failure
            this.done = true;
            this.observedImg = null;
            this.error = new RuntimeException(String.format("Image update aborted. flags=%X", infoflags));
            notifyAll();
            return false;
        }
        if ((infoflags & ImageObserver.ALLBITS) != 0) {
            // Done
            this.done = true;
            this.observedImg = img;
            notifyAll();
            return false;
        }
        // In progress
        return !canceled;
    }
}
