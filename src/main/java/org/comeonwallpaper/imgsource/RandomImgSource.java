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
  public static class Candidate {
    private final ImgSource source;
    private final int weight;

    public Candidate(@NonNull ImgSource source, int weight) {
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

  private final List<Candidate> entries;
  private final int totalWeight;
  private final Random rand;
  private ImgSource nextSource = null;

  public RandomImgSource(@NonNull List<Candidate> candidates) {
    this.entries = new ArrayList<>(candidates);
    int totalWeightTmp = 0;
    for (Candidate candidate : candidates) {
      totalWeightTmp += candidate.weight;
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
    for (Candidate candidate : entries) {
      if (pivot < candidate.weight) {
        nextSource = candidate.getSource();
        return;
      }
      pivot -= candidate.weight;
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
    for (Candidate candidate : entries) {
      try {
        candidate.source.close();
      } catch (Exception ex) {
        lastEx = ex;
      }
    }
    if (lastEx != null) {
      throw new IOException(lastEx);
    }
  }
}
