package org.comeonwallpaper.imgsource;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.awt.*;

/**
 * This class calculates the information needed for fitting an image to a canvas with background filling.
 * Given a canvas and an image, we want to scale the image so that the image is inner fitting the canvas. That is, the
 * scaled image maintains the aspect ratio, and the image is not cropped.
 * If the image doesn't have the same width-and-height ratio as the canvas, then there will be gaps at its two sides.
 * To fill the gap, we form a background by outer fitting the image. That is, the aspect ratio is still maintained, but
 * the canvas is fitted without any gap. Parts of the image may be cropped.
 * See the link below for the information calculated by this class.
 * @link https://www.notion.so/Design-for-Image-Fitting-for-Come-On-Wallpaper-3b568a1f780f4f679958476d9d935444
 */
public class ScaledFillCalculator {
    private final Rectangle firstBgOnCanvas, secondBgOnCanvas, imageOnCanvas, firstBgOnImage, secondBgOnImage;

    /**
     * Constructs a new calculator for the given image and canvas sizes.
     *
     * @param imageWidth Width of the image.
     * @param imageHeight Height of the image.
     * @param canvasWidth Width of the canvas.
     * @param canvasHeight Height of the canvas.
     */
    public ScaledFillCalculator(int imageWidth, int imageHeight, int canvasWidth, int canvasHeight) {
        boolean shouldTranspose = isRatioWider(imageWidth, imageHeight, canvasWidth, canvasHeight);
        if (shouldTranspose) {
            int tmp;
            tmp = imageWidth;
            imageWidth = imageHeight;
            imageHeight = tmp;
            
            tmp = canvasWidth;
            canvasWidth = canvasHeight;
            canvasHeight = tmp;
        }
        final int imgWidthOnCanvas = canvasHeight * imageWidth / imageHeight;
        final int firstBgWidthOnCanvas = (canvasWidth - imgWidthOnCanvas) / 2;
        final int secondBgWidthOnCanvas = canvasWidth - firstBgWidthOnCanvas - imgWidthOnCanvas;
        final int bgHeightOnCanvas = canvasHeight;
        this.firstBgOnCanvas = new Rectangle(0, 0, firstBgWidthOnCanvas, bgHeightOnCanvas);
        this.secondBgOnCanvas = new Rectangle(
                firstBgWidthOnCanvas + imgWidthOnCanvas, 0, secondBgWidthOnCanvas, bgHeightOnCanvas);
        this.imageOnCanvas = new Rectangle(
                firstBgWidthOnCanvas, 0, imgWidthOnCanvas, canvasHeight);
        final int reversedBgHeight = imageWidth * canvasHeight / canvasWidth;
        final int reversedImgWidth = imageWidth * reversedBgHeight / imageHeight;
        final int reversedLeftBgWidth = (imageWidth - reversedImgWidth) / 2;
        final int reversedRightBgWidth = imageWidth - reversedLeftBgWidth - reversedImgWidth;
        final int reversedOverflowHeight = (imageHeight - reversedBgHeight) / 2;
        this.firstBgOnImage = new Rectangle(
                0, reversedOverflowHeight,
                reversedLeftBgWidth, reversedBgHeight);
        this.secondBgOnImage = new Rectangle(
                reversedLeftBgWidth + reversedImgWidth, reversedOverflowHeight,
                reversedRightBgWidth, reversedBgHeight);
        if (shouldTranspose) {
            transposeRectangle(this.firstBgOnCanvas);
            transposeRectangle(this.secondBgOnCanvas);
            transposeRectangle(this.imageOnCanvas);
            transposeRectangle(this.firstBgOnImage);
            transposeRectangle(this.secondBgOnImage);
        }
    }

    /**
     * Gets the rectangle on the canvas for the 1st background area.
     * For the case that the canvas's ratio is wider than the image, the rectangle is the background at the left
     * hand side. In the other case, it's the top side.
     *
     * @return Rectangle.
     */
    @NonNull
    public Rectangle get1stBackgroundOnCanvas() {
        return new Rectangle(this.firstBgOnCanvas);
    }

    /**
     * Gets the rectangle on the canvas for the 2nd background area.
     * For the case that the canvas's ratio is wider than the image, the rectangle is the background at the right
     * hand side. In the other case, it's the bottom side.
     *
     * @return Rectangle.
     */
    @NonNull
    public Rectangle get2ndBackgroundOnCanvas() {
        return new Rectangle(this.secondBgOnCanvas);
    }

    /**
     * Gets the rectangle on the canvas for the scaled image.
     *
     * @return Rectangle.
     */
    @NonNull
    public Rectangle getImageOnCanvas() {
        return new Rectangle(this.imageOnCanvas);
    }

    /**
     * Gets the rectangle on the image for the region corresponding to the 1st background on the canvas.
     *
     * @see #get1stBackgroundOnCanvas()
     * @return Rectangle.
     */
    @NonNull
    public Rectangle get1stBackgroundOnImage() {
        return new Rectangle(this.firstBgOnImage);
    }

    /**
     * Gets the rectangle on the image for the region corresponding to the 2nd background on the canvas.
     *
     * @see #get2ndBackgroundOnCanvas()
     * @return Rectangle.
     */
    @NonNull
    public Rectangle get2ndBackgroundOnImage() {
        return new Rectangle(this.secondBgOnImage);
    }

    /**
     * Transpose the rectangle.
     * The width and height are switched, and the x and y are also switched.
     *
     * @param rectangle Transposed rectangle.
     */
    private static void transposeRectangle(@NonNull Rectangle rectangle) {
        int tmp = rectangle.x;
        rectangle.x = rectangle.y;
        rectangle.y = tmp;

        tmp = rectangle.width;
        rectangle.width = rectangle.height;
        rectangle.height = tmp;
    }

    /**
     * Returns whether the first region's ratio is wider than the 2nd one.
     *
     * @param w1 Width of the first region.
     * @param h1 Height of the first region.
     * @param w2 Width of the second region.
     * @param h2 Height of the second region.
     * @return True is the first one's ratio is wider.
     */
    private static boolean isRatioWider(int w1, int h1, int w2, int h2) {
        return (long) w1 * h2 >= (long) w2 * h1;
    }
}
