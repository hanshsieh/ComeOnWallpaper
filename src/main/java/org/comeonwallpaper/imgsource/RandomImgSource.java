package org.comeonwallpaper.imgsource;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomImgSource implements ImgSource {
    public static class Entry {
        private final ImgSource source;
        private final int weight;
        public Entry(@NonNull ImgSource source, int weight) {
            Validate.isTrue(weight >= 0, "Weight must be non-negative");
            this.source = source;
            this.weight = weight;
        }
        public int getWeight() {
            return weight;
        }
        @NonNull
        public ImgSource getSource() {
            return source;
        }
    }
    private final List<Entry> entries;
    private final int totalWeight;
    private final Random rand;
    private ImgSource nextSource = null;

    public RandomImgSource(@NonNull List<Entry> entries ) {
        this.entries = new ArrayList<>(entries);
        int totalWeightTmp = 0;
        for (Entry entry : entries) {
            totalWeightTmp += entry.weight;
        }
        this.totalWeight = totalWeightTmp;
        this.rand = new Random();
    }

    @Override
    public @NonNull Planar<GrayU8> next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException {
        pickNextSource();
        if (nextSource == null) {
            throw new NoSuchElementException("Total weight is 0, so no source is available");
        }
        return nextSource.next(prefs);
    }

    private void pickNextSource() {
        if (nextSource != null || totalWeight <= 0) {
            return;
        }
        int pivot = rand.nextInt(totalWeight);
        for (Entry entry : entries) {
            if (pivot < entry.weight) {
                nextSource = entry.getSource();
                return;
            }
            pivot -= entry.weight;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        pickNextSource();
        return nextSource != null && nextSource.hasNext();
    }

    @Override
    public void close() throws IOException {
        Exception lastEx = null;
        for (Entry entry : entries) {
            try {
                entry.source.close();
            } catch (Exception ex) {
                lastEx = ex;
            }
        }
        if (lastEx != null) {
            throw new IOException(lastEx);
        }
    }
}
