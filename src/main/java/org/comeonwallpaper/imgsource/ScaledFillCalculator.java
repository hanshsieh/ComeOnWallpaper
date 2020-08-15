package org.comeonwallpaper.imgsource;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.awt.*;

/**
 * @link https://drive.google.com/file/d/1JjfkJmH_kcIPSWjYK8eaE9KBtoz-2qBu/view?usp=sharing
 */
public class ScaledFillCalculator {
    private final Rectangle firstBgOnCanvas, secondBgOnCanvas, imageOnCanvas, firstBgOnImage, secondBgOnImage;
    public ScaledFillCalculator(int imageWidth, int imageHeight, int canvasWidth, int canvasHeight) {
        boolean rotate = isRatioWider(imageWidth, imageHeight, canvasWidth, canvasHeight);
        if (rotate) {
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
        if (rotate) {
            rotateRectangle(this.firstBgOnCanvas);
            rotateRectangle(this.secondBgOnCanvas);
            rotateRectangle(this.imageOnCanvas);
            rotateRectangle(this.firstBgOnImage);
            rotateRectangle(this.secondBgOnImage);
        }
    }

    @NonNull
    public Rectangle get1stBackgroundOnCanvas() {
        return new Rectangle(this.firstBgOnCanvas);
    }

    @NonNull
    public Rectangle get2ndBackgroundOnCanvas() {
        return new Rectangle(this.secondBgOnCanvas);
    }

    @NonNull
    public Rectangle getImageOnCanvas() {
        return new Rectangle(this.imageOnCanvas);
    }

    @NonNull
    public Rectangle get1stBackgroundOnImage() {
        return new Rectangle(this.firstBgOnImage);
    }

    @NonNull
    public Rectangle get2ndBackgroundOnImage() {
        return new Rectangle(this.secondBgOnImage);
    }

    private static void rotateRectangle(@NonNull Rectangle rectangle) {
        int tmp = rectangle.x;
        rectangle.x = rectangle.y;
        rectangle.y = tmp;

        tmp = rectangle.width;
        rectangle.width = rectangle.height;
        rectangle.height = tmp;
    }
    
    private static boolean isRatioWider(int w1, int h1, int w2, int h2) {
        return (long) w1 * h2 >= (long) w2 * h1;
    }
}
