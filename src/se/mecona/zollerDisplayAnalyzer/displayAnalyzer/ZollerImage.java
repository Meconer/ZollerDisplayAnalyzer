/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import se.mecona.zollerDisplayAnalyzer.gui.Globals;
import se.mecona.zollerDisplayAnalyzer.gui.ImageEvent;

/**
 * Represents an image of the display on a zoller tool presetter.
 * @author Mats
 */
class ZollerImage {
    private static final int DEFAULT_THRESHOLD = 100;
    private static final int NO_OF_DIGITS = 9;
    private static final int PADDING_DIVISOR = 63;
    private static final int PADDING_FRACTION_FOR_LAST_DIGIT_1 = 30;
    private static final double SHEAR_VALUE = 0.1;
    
    
    private BufferedImage image;

    private short thresholdValue = DEFAULT_THRESHOLD;

    // public constructor. Sets the image
    public ZollerImage(BufferedImage image) {
        this.image = image;
    }

    // Analyzes the image
    public void analyze() {
        if ( image != null ) {
            // Prepare the image for analysis
            image = prepareImage();
            
            //Globals.getEventBus().post( new ImageEvent(ImageEvent.imageType.RIGHT, image));
            
            // Get the upper half of the image.
            BufferedImage imageHalf = image.getSubimage(0, 0, image.getWidth(), image.getHeight()/2);
            
            analyzeHalf(imageHalf);
            
            //Globals.getEventBus().post( new ImageEvent(ImageEvent.imageType.RIGHT, imageHalf));
        }
        
    }

    private BufferedImage prepareImage() {
        // Blacken all pixels below thresholdValue with a lookupop
        // Do it only for the green channel. The other channels is set to 0.
        // Create the lookup table

        //System.out.println("Preparing image");

        short[] threshold = new short[256];

        for (int i = 0; i < 256; i++) {
            threshold[i] = (i < thresholdValue) ? (short)0 : (short)255;
        }

        short[][]greenThreshold = new short[][]{ threshold, threshold,threshold };
        // Create the thresholdValue operation
        BufferedImageOp thresholdOp = new LookupOp(new ShortLookupTable(0, greenThreshold), null);
        
        // Create a new image with the same dimensions as the original
        BufferedImage lupopedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        // Do the thresholdValue operation.
        thresholdOp.filter(image, lupopedImage);
        return lupopedImage;

    }

    void setThreshold(int threshold) {
        thresholdValue = (short) threshold;
    }

    private void analyzeHalf(BufferedImage image) {
        image = ImageAnalyzer.shearImage(image, SHEAR_VALUE);
        BufferedImage subImage = ImageAnalyzer.getNonEmptyPart(image, PADDING_DIVISOR, 0 );
        if ( checkIfLastDigitIsOne(subImage) ) {
            subImage = ImageAnalyzer.getNonEmptyPart(image, PADDING_DIVISOR, PADDING_FRACTION_FOR_LAST_DIGIT_1 );
        }
        Globals.getEventBus().post(new ImageEvent(ImageEvent.imageType.RIGHT, subImage));
    }

    private boolean checkIfLastDigitIsOne(BufferedImage image) {
        int width = image.getWidth();
        int digitAreaWidth = width / NO_OF_DIGITS;
        BufferedImage lastDigitImage = image.getSubimage(width-digitAreaWidth, 0, digitAreaWidth, image.getHeight());
        
        // If the last digit is one it is possible that some "dirt" from the previous digit is in the left part of
        // the digit part. Remove that.
        lastDigitImage = ImageAnalyzer.removeBrightAtLeft( lastDigitImage );
        
        //Globals.getEventBus().post(new ImageEvent(ImageEvent.imageType.RIGHT, lastDigitImage));
        
        int digitWidth = ImageAnalyzer.calcFilledWidth(lastDigitImage);

        //Globals.getEventBus().post(new ImageEvent( ImageEvent.imageType.RIGHT, lastDigitImage));

        return digitAreaWidth / digitWidth  > 4; // If this digit is a "1" then return true;
    }

    
    
}
