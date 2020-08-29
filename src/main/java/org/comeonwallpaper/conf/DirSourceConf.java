package org.comeonwallpaper.conf;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DirSourceConf implements SourceConf {
    public enum Ordering {
        RANDOM,
        ALPHABETICAL
    }
    private final String id;
    private final String dirPath;
    private final Ordering ordering;
    public DirSourceConf(@NonNull String id, @NonNull String dirPath, @NonNull Ordering ordering) {
        this.id = id;
        this.dirPath = dirPath;
        this.ordering = ordering;
    }

    public String getDirPath() {
        return dirPath;
    }

    public Ordering getOrdering() {
        return ordering;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String[] getParentIds() {
        return new String[0];
    }
}
