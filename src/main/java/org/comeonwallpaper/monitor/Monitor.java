package org.comeonwallpaper.monitor;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;

public class Monitor {
    static class Builder {
        private Rectangle displayArea;
        private Rectangle workingArea;

        @NonNull
        public Builder setDisplayArea(@NonNull Rectangle area) {
            this.displayArea = area;
            return this;
        }

        @NonNull
        public Builder setWorkingArea(@NonNull Rectangle area) {
            this.workingArea = area;
            return this;
        }

        @NonNull
        public Monitor build() {
            Preconditions.checkNotNull(displayArea, "Display area cannot be null");
            Preconditions.checkNotNull(workingArea, "Working area cannot be null");
            return new Monitor(this);
        }
    }
    private final Rectangle displayArea;
    private final Rectangle workingArea;
    private Monitor(@NonNull Builder builder) {
        this.displayArea = new Rectangle(builder.displayArea);
        this.workingArea = new Rectangle(builder.workingArea);
    }

    @NonNull
    public Rectangle getDisplayArea() {
        return new Rectangle(displayArea);
    }

    @NonNull
    public Rectangle getWorkingArea() {
        return new Rectangle(workingArea);
    }
}
