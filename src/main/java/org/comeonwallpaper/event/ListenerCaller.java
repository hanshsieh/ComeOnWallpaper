package org.comeonwallpaper.event;

import java.util.EventListener;

public interface ListenerCaller<T extends EventListener> {
    void call(T listener) throws Exception;
}
