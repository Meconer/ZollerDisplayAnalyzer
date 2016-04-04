/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 * Helper util class for image routines on buffered images.
 * @author Mats
 */
public class ImageAnalyzer {
    
    // returns a subimage of the non empty parts of
    public static BufferedImage getNonEmptyPart(BufferedImage image) {
        // Get first non empty column
        int startCol = getNonEmptyCol( image, 0, 1 );
        
        int endCol = getNonEmptyCol(image, image.getWidth()-1, -1);
        
        image = image.getSubimage(startCol, 0, endCol, image.getHeight()-1);
        
        int startRow = getNonEmptyRow( image, 0, 1);
        
        int endRow = getNonEmptyRow( image, image.getHeight()-1, -1);
        
        
        return image.getSubimage(0, startRow, image.getWidth()-1, endRow);
    }

    private static int getNonEmptyCol(BufferedImage image, int start, int direction ) {
        Raster raster = image.getData();
        for ( int col = start; col < image.getWidth(); col += direction ) {
            if ( colIsBright( raster, col )) return col;
        }
        return -1; // Not found
    }
    
    private static int getNonEmptyRow(BufferedImage image, int start, int direction ) {
        Raster raster = image.getData();
        for ( int row = start; row < image.getHeight(); row += direction ) {
            if ( rowIsBright( raster, row )) return row;
        }
        return -1; // Not found
    }

    private static boolean colIsBright(Raster raster, int col) {
        int pixel;
        for ( int row = 0; row < raster.getHeight(); row++ ) {
            pixel = raster.getSample(col, row, 1);
            System.out.println("Pixel = " + pixel);
            if ( (int) pixel != 0 ) return true;
        }
        return false;
    }

    private static boolean rowIsBright(Raster raster, int row) {
        int pixel;
        for ( int col = 0; col < raster.getHeight(); col++ ) {
            pixel = raster.getSample(col, row, 1);
            if ( (int) pixel != 0 ) return true;
        }
        return false;
    }
}
