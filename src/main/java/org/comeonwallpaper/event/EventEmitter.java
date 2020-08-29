package org.comeonwallpaper.event;

import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventListener;
import java.util.Set;

public class EventEmitter<T extends EventListener> {
    private static final Logger logger = LoggerFactory.getLogger(EventEmitter.class);
    private final Set<T> listeners = Sets.newIdentityHashSet();
    public void emit(@NonNull ListenerCaller<T> caller) {
        for (T listener : listeners) {
            try {
                caller.call(listener);
            } catch (Exception ex) {
                logger.error("Failed to invoke listener: ", ex);
            }
        }
    }
    public boolean addListener(@NonNull T listener) {
        return listeners.add(listener);
    }
    public boolean removeListener(@NonNull T listener) {
        return listeners.remove(listener);
    }
    public void clearListeners() {
        listeners.clear();
    }
    @SuppressWarnings("unchecked")
    public T[] getListeners() {
        return (T[])listeners.toArray();
    }
}
