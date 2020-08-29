package org.comeonwallpaper.ui.setting;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.event.EventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingUI {
    private static final Logger logger = LoggerFactory.getLogger(SettingUI.class);
    private final EventEmitter<StateListener> stateEventEmitter = new EventEmitter<>();
    private final EventEmitter<OnSaveListener> onSaveEventEmitter = new EventEmitter<>();
    private State state = State.INIT;
    private JFrame frame;
    public void show() {
        if (state != State.INIT) {
            throw new IllegalStateException("Expecting status to be in " + State.INIT + " but see " + state);
        }
        frame = new JFrame("Settings");
        frame.setPreferredSize(new Dimension(500, 600));
        frame.pack();
        // Show the window at the center of the main monitor
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                frame = null;
                changeState(State.CLOSE);
            }
        });
        frame.setVisible(true);
        changeState(State.OPEN);
    }
    private void changeState(State newState) {
        State oldState = this.state;
        this.state = newState;
        logger.debug("Settings state changed from {} to {}", oldState, newState);
        stateEventEmitter.emit((listener) -> listener.onChange(oldState, newState));
    }
    public void addStatusChangeListener(@NonNull StateListener listener) {
        stateEventEmitter.addListener(listener);
    }
    public void removeStatusChangeListener(@NonNull StateListener listener) {
        stateEventEmitter.removeListener(listener);
    }
    public void addOnSaveListener(@NonNull OnSaveListener listener) {
        onSaveEventEmitter.addListener(listener);
    }
    public void removeOnSaveListener(@NonNull OnSaveListener listener) {
        onSaveEventEmitter.removeListener(listener);
    }
}
