package org.comeonwallpaper.ui.setting;

import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class SettingUI {
    private static final Logger logger = LoggerFactory.getLogger(SettingUI.class);
    private final Set<StateListener> stateListeners = Sets.newIdentityHashSet();
    private final Set<OnSaveListener> saveListeners = Sets.newIdentityHashSet();
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
        for (StateListener listener : stateListeners) {
            try {
                listener.onChange(oldState, newState);
            } catch (Exception ex) {
                logger.error("Exception thrown when calling status listener: ", ex);
            }
        }
    }
    public void addStatusChangeListener(@NonNull StateListener listener) {
        this.stateListeners.add(listener);
    }
    public void removeStatusChangeListener(@NonNull StateListener listener) {
        this.stateListeners.remove(listener);
    }
    public void addOnSaveListener(@NonNull OnSaveListener listener) {
        this.saveListeners.add(listener);
    }
    public void removeOnSaveListener(@NonNull OnSaveListener listener) {
        this.saveListeners.remove(listener);
    }
}
