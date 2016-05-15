/*
 * Copyright Mats Andersson, Mecona Teknik AB
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Helper util class for image routines on buffered images.
 *
 * @author Mats Andersson Mecona Teknik AB
 */
public class ImageAnalyzer {

    

    /**
     * Method that returns the non empty part of the image.
     * @param image             The original image
     * @param paddingFraction   The fraction of the image width that is used as spacing between the digits
     * @param addExtraFraction  The fraction of the image width that is used for extra padding if last digit is a "1"
     * @return a new image that contains the part of the image that has digits.
     */
    public static BufferedImage getNonEmptyPart(BufferedImage image, int paddingFraction, int addExtraFraction) {
        // Get first non empty column
        int startCol = getNonEmptyCol(image, 0, 1);

        int width = getNonEmptyCol(image, image.getWidth() - 1, -1) - startCol;
        int padding;
        if (paddingFraction == 0) {
            padding = 0;
        } else {
            padding = width / paddingFraction;
        }

        int addExtraFractionPadding;
        if (addExtraFraction == 0) {
            addExtraFractionPadding = 0;
        } else {
            addExtraFractionPadding = width / addExtraFraction;
        }

        if (startCol < padding) {
            throw new IllegalArgumentException("padding too big for image left");
        }
        if (startCol + padding + addExtraFractionPadding + width > image.getWidth()) {
            throw new IllegalArgumentException("padding too big for image right");
        }

        image = image.getSubimage(startCol - padding, 0, width + 2 * padding + addExtraFractionPadding, image.getHeight());

        int startRow = getNonEmptyRow(image, 0, 1);

        int height = getNonEmptyRow(image, image.getHeight() - 1, -1) - startRow;

        image = image.getSubimage(0, startRow, image.getWidth(), height);
        //Globals.getEventBus().post( new ImageEvent(ImageEvent.imageType.RIGHT, image));
        return image;
    }

    

    /**
     * Returns the first column that contains any bright pixels. returns -1 if not found.
     * 
     * @param image         The image to check
     * @param start         The column to start the check in.
     * @param direction     The direction the check should be going. 1 or -1 for positive or 
     *                      negative direction
     * @return  the column number that has the first bright pixels 
     */
    public static int getNonEmptyCol(BufferedImage image, int start, int direction) {
        Raster raster = image.getData();
        int height = image.getHeight();
        if (direction > 0) {

            for (int col = start; col < image.getWidth(); col += direction) {
                if (colIsBright(raster, col, height)) {
                    return col;
                }
            }
        } else {
            for (int col = start; col >= 0 ; col += direction) {
                if (colIsBright(raster, col, height)) {
                    return col;
                }
            }
        }
        return -1; // Not found
    }

    /**
     * Returns the first column that is dark. returns -1 if not found.
     * 
     * @param image         The image to check
     * @param start         The column to start the check in.
     * @param direction     The direction the check should be going. 1 or -1 for positive or 
     *                      negative direction
     * @return  the first column number that has no bright pixels.
     */
    public static int getEmptyCol(BufferedImage image, int start, int direction) {
        Raster raster = image.getData();
        int height = image.getHeight();

        if (direction > 0) {

            for (int col = start; col < image.getWidth(); col += direction) {
                if (!colIsBright(raster, col, height)) {
                    return col;
                }
            }
        } else {
            for (int col = start; col >= 0 ; col += direction) {
                if (!colIsBright(raster, col, height)) {
                    return col;
                }
            }
        }
        return -1; // Not found
    }

    /**
     * Returns the first row that contains any bright pixels. returns -1 if not found.
     * 
     * @param image         The image to check
     * @param start         The row to start the check in.
     * @param direction     The direction the check should be going. 1 or -1 for positive or 
     *                      negative direction
     * @return  the row number that has the first bright pixels 
     */
    public static int getNonEmptyRow(BufferedImage image, int start, int direction) {
        Raster raster = image.getData();
        int width = image.getWidth();
        for (int row = start; row < image.getHeight(); row += direction) {
            if (rowIsBright(raster, row, width)) {
                return row;
            }
        }
        return -1; // Not found
    }

    /**
     * Checks if the specified colum in a raster is bright (has light pixels).
     * 
     * @param raster    Raster to check
     * @param col       Column in the raster to check
     * @param height    The height of the raster.
     * @return  true if this column is bright.
     */
    public static boolean colIsBright(Raster raster, int col, int height) {

        for (int row = 0; row < height; row++) {
            int green = raster.getSample(col, row, 1);
            if ((int) green > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified colum in a raster is bright (has light pixels).
     * 
     * @param raster    Raster to check
     * @param row       Row in the raster to check
     * @param width     The height of the raster.
     * @return  true if this row is bright.
     */
    public static boolean rowIsBright(Raster raster, int row, int width) {
        int pixel;
        for (int col = 0; col < width; col++) {
            pixel = raster.getSample(col, row, 1);
            if ((int) pixel > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an image that is sheared with a specified amount
     * @param image         The image to be sheared
     * @param shearValue    The amount that the image should be sheared with.
     * @return a new sheared image.
     */
    public static BufferedImage shearImage(BufferedImage image, double shearValue) {
        // Shear image so numbers are straight
        AffineTransform at = AffineTransform.getShearInstance(shearValue, 0);
        AffineTransformOp aop = new AffineTransformOp(at, null);
        BufferedImage transformedImage = aop.filter(image, null);

        return transformedImage;
    }

    /**
     * Calculates the width of the image that has bright pixels in it.
     * @param image     The image to analyze
     * @return  The width of the image that is not dark.
     */
    public static int calcFilledWidth(BufferedImage image) {
        int startCol = getNonEmptyCol(image, 0, 1);
        int endCol = getNonEmptyCol(image, image.getWidth(), -1);
        return endCol - startCol;
    }

    /**
     * Calculates the height of the image that has bright pixels in it.
     * @param image     The image to analyze
     * @return  The height of the image that is not dark.
     */
    public static int calcFilledHeight(BufferedImage image) {
        int startRow = getNonEmptyRow(image, 0, 1);
        int endRow = getNonEmptyRow(image, image.getHeight(), -1);
        return endRow - startRow;
    }

    /**
     * Returns a new image where the bright on the left side is removed.
     * @param image The image to analyze
     * @return a new image with bright columns at the left removed.
     */
    public static BufferedImage removeBrightAtLeft(BufferedImage image) {
        int startCol = getEmptyCol(image, 0, 1);
        image = image.getSubimage(startCol, 0, image.getWidth() - startCol, image.getHeight());
        return image;
    }

    /**
     * Checks if an image is all dark
     * @param image The image to analyze
     * @return  true if image is completely dark. False if not.
     */
    public static boolean isAllBlack(BufferedImage image) {
        return getNonEmptyCol(image, 0, 1) == -1;
        
    }

    /**
     * Returns a new image with a specified area filled with black
     * @param image     The original image
     * @param startCol  Start column of area to set black
     * @param startRow  Start row of area to set black
     * @param width     Width of area to set black
     * @param height    Height of area to set black
     * @return a new image with specified area set to black.
     */
    public static BufferedImage fillWithBlack(BufferedImage image, int startCol, int startRow, int width, int height) {
        WritableRaster raster = image.getSubimage(startCol, startRow, width, height).getRaster();
        for ( int col = 0; col <= width; col++ ) {
            for ( int row = 0 ; row <= height; row++ ) {
                raster.setSample(col, row, 0, 0);
                raster.setSample(col, row, 1, 0);
                raster.setSample(col, row, 2, 0);
            }
        }
        return image;
    }

}
