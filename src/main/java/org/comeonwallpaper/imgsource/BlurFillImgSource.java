package org.comeonwallpaper.imgsource;

import boofcv.abst.distort.FDistort;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * This image source will take the image from the given delegate, and then generate an image fitting the canvas size
 * (target size).
 * The original image is scaled to fill the image so that the aspect ratio is not changed, and the whole canvas is filled.
 * Then gaussian blur is applied so that the scaled image can act as the background.
 * Finally, the original image is scaled to fit inside the canvas without changing the aspect ratio.
 */
public class BlurFillImgSource implements ImgSource {

    private final ImgSource delegate;
    private final int radius;
    private final int sigma;

    /**
     * Constructs a new image source using the given delegate.
     * Gaussian blur will be applied to the background with the given radius and sigma.
     * If one of the radius or sigma is -1, then the other will be calculated using normal distribution.
     *
     * @param delegate Delegate.
     * @param radius Radius of Gaussian blur.
     * @param sigma Sigma of Gaussian blur.
     */
    public BlurFillImgSource(@NonNull ImgSource delegate, int radius, int sigma) {
        this.delegate = delegate;
        this.radius = radius;
        this.sigma = sigma;

    }

    /**
     * Constructs a new image source using the given delegate.
     * The sigma of Gaussian blur will be decided from normal distribution.
     *
     * @see #BlurFillImgSource(ImgSource, int, int)
     * @param delegate Delegate.
     * @param radius Radius of Gaussian blur.
     */
    public BlurFillImgSource(@NonNull ImgSource delegate, int radius) {
        this(delegate, radius, -1);
    }

    @NonNull
    @Override
    public Planar<GrayU8> next(@NonNull ImgPrefs prefs) throws NoSuchElementException, IOException {
        Planar<GrayU8> image = delegate.next(prefs);
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, prefs.getWidth(), prefs.getHeight(), ImgPrefs.NUM_BANDS);
        ScaledFillCalculator calculator = new ScaledFillCalculator(
                image.getWidth(), image.getHeight(), canvas.getWidth(), canvas.getHeight());
        Planar<GrayU8> firstBgOnCanvas = subImageByRectangle(canvas, calculator.get1stBackgroundOnCanvas());
        Planar<GrayU8> secondBgOnCanvas = subImageByRectangle(canvas, calculator.get2ndBackgroundOnCanvas());
        Planar<GrayU8> imgOnCanvas = subImageByRectangle(canvas, calculator.getImageOnCanvas());
        Planar<GrayU8> firstBgOnImage = subImageByRectangle(image, calculator.get1stBackgroundOnImage());
        Planar<GrayU8> secondBgOnImage = subImageByRectangle(image, calculator.get2ndBackgroundOnImage());
        Planar<GrayU8> firstBgOnCanvasScaled = firstBgOnCanvas.createSameShape();
        Planar<GrayU8> secondBgOnCanvasScaled = secondBgOnCanvas.createSameShape();
        scaleTo(firstBgOnImage, firstBgOnCanvasScaled);
        GBlurImageOps.gaussian(firstBgOnCanvasScaled, firstBgOnCanvas, this.sigma, this.radius, null);
        scaleTo(secondBgOnImage, secondBgOnCanvasScaled);
        GBlurImageOps.gaussian(secondBgOnCanvasScaled, secondBgOnCanvas, this.sigma, this.radius, null);
        scaleTo(image, imgOnCanvas);
        return canvas;
    }

    /**
     * Scale the "from" image to fill the "to" image.
     *
     * @param from The source of the image to be scaled.
     * @param to The target canvas to put the resulting image.
     */
    private static void scaleTo(@NonNull Planar<GrayU8> from, @NonNull Planar<GrayU8> to) {
        new FDistort(from, to)
                .scaleExt()
                .apply();
    }

    /**
     * Create a sub-image from the region described by the given rectangle.
     *
     * @param image The image to take sub-image.
     * @param rec Rectangle.
     * @return The sub-image.
     */
    private static Planar<GrayU8> subImageByRectangle(@NonNull Planar<GrayU8> image, @NonNull Rectangle rec) {
        return image.subimage(
            rec.x, rec.y,
            rec.x + rec.width, rec.y + rec.height
        );
    }

    @Override
    public boolean hasNext() throws IOException {
        return delegate.hasNext();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
