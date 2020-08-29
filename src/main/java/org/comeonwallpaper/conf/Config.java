package org.comeonwallpaper.conf;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Period;
import java.util.*;

public class Config {
    public static class ConfigBuilder {
        private final List<SourceConf> sources = new ArrayList<>();
        private String rootSourceId;
        private Period interval;
        @NonNull
        public ConfigBuilder addSource(@NonNull SourceConf source) {
            sources.add(source);
            return this;
        }
        @NonNull
        public ConfigBuilder setRootSourceId(@NonNull String id) {
            rootSourceId = id;
            return this;
        }
        @NonNull
        public ConfigBuilder setInterval(@NonNull Period interval) {
            this.interval = interval;
            return this;
        }
        public Config build() throws IllegalStateException {
            validateNotNull(interval, "Interval not set");
            validateNotNull(rootSourceId, "Root source ID not set");
            Map<String, SourceConf> sourcesMap = new HashMap<>();
            for (SourceConf source : sources) {
                if (sourcesMap.containsKey(source.getId())) {
                    throw new IllegalStateException("Duplicate source ID " + source.getId());
                }
                sourcesMap.put(source.getId(), source);
            }
            if (!sourcesMap.containsKey(rootSourceId)) {
                throw new IllegalStateException("No source exists with ID \"" + rootSourceId + "\"");
            }
            checkNoCycle(sourcesMap);
            return new Config(this, sourcesMap);
        }
        private void validateNotNull(@Nullable Object value, @NonNull String message) {
            if (value == null) {
                throw new IllegalStateException(message);
            }
        }
        private void checkNoCycle(Map<String, SourceConf> sourcesMap) throws IllegalStateException {
            for (SourceConf source : sourcesMap.values()) {
                checkNoCycleFrom(source, sourcesMap, new HashSet<>());
            }
        }
        private void checkNoCycleFrom(SourceConf start, Map<String, SourceConf> sources, Set<String> visited)
                throws IllegalStateException {
            if (visited.contains(start.getId())) {
                throw new IllegalStateException(
                        "Cycle is detected for source \"" + start.getId() + "\"");
            }
            visited.add(start.getId());
            for (String parentId : start.getParentIds()) {
                SourceConf parent = sources.get(parentId);
                if (parent == null) {
                    throw new IllegalStateException("Parent \"" + parentId + "\" of source \"" + start.getId()
                        + "\" doesn't exist");
                }
                checkNoCycleFrom(parent, sources, visited);
            }
            visited.remove(start.getId());
        }
    }
    private final Map<String, SourceConf> sourcesMap;
    private final String rootSourceId;
    private final Period interval;

    private Config(@NonNull ConfigBuilder builder, @NonNull Map<String, SourceConf> sourcesMap) {
        this.interval = builder.interval;
        this.rootSourceId = builder.rootSourceId;
        this.sourcesMap = sourcesMap;
    }

    @NonNull
    public SourceConf[] getSources() {
        return sourcesMap.values().toArray(new SourceConf[0]);
    }

    @NonNull
    public SourceConf getSourceById(@NonNull String id) {
        SourceConf source = sourcesMap.get(id);
        if (source == null) {
            throw new IllegalArgumentException("No source with ID \"" + id + "\" exists");
        }
        return source;
    }

    @NonNull
    public Period getInterval() {
        return interval;
    }

    @NonNull
    SourceConf getRootSource() {
        return sourcesMap.get(rootSourceId);
    }
}
