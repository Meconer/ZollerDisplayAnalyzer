/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import se.mecona.zollerDisplayAnalyzer.gui.Globals;
import se.mecona.zollerDisplayAnalyzer.gui.ImageEvent;

/**
 * Helper util class for image routines on buffered images.
 *
 * @author Mats
 */
public class ImageAnalyzer {

    // returns a subimage of the non empty parts of
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
        //System.out.println("Padding = "+ padding);

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

    // returns the first column that contains any bright pixels. returns -1 if not found.
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

    public static boolean colIsBright(Raster raster, int col, int height) {

        for (int row = 0; row < height; row++) {
            int green = raster.getSample(col, row, 1);
            if ((int) green > 0) {
                return true;
            }
        }
        return false;
    }

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

    public static BufferedImage shearImage(BufferedImage image, double shearValue) {
        // Shear image so numbers are straight
        AffineTransform at = AffineTransform.getShearInstance(shearValue, 0);
        AffineTransformOp aop = new AffineTransformOp(at, null);
        BufferedImage transformedImage = aop.filter(image, null);

        return transformedImage;
    }

    public static int calcFilledWidth(BufferedImage image) {
        int startCol = getNonEmptyCol(image, 0, 1);
        int endCol = getNonEmptyCol(image, image.getWidth(), -1);
        return endCol - startCol;
    }

    public static int calcFilledHeight(BufferedImage image) {
        int startRow = getNonEmptyRow(image, 0, 1);
        int endRow = getNonEmptyRow(image, image.getHeight(), -1);
        return endRow - startRow;
    }

    public static BufferedImage removeBrightAtLeft(BufferedImage image) {
        int startCol = getEmptyCol(image, 0, 1);
        image = image.getSubimage(startCol, 0, image.getWidth() - startCol, image.getHeight());
        return image;
    }

    public static boolean isAllBlack(BufferedImage image) {
        return getNonEmptyCol(image, 0, 1) == -1;
        
    }

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
