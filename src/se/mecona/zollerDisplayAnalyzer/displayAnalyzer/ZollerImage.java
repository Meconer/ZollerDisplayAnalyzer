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
import jdk.nashorn.internal.objects.Global;
import se.mecona.zollerDisplayAnalyzer.gui.Globals;
import se.mecona.zollerDisplayAnalyzer.gui.ImageEvent;

/**
 * Represents an image of the display on a zoller tool presetter.
 * @author Mats
 */
class ZollerImage {
    private static final int DEFAULT_THRESHOLD = 100;
    
    private final BufferedImage image;

    private short thresholdValue = DEFAULT_THRESHOLD;

    // public constructor. Sets the image
    public ZollerImage(BufferedImage image) {
        this.image = image;
    }

    // Analyzes the image
    public void analyze() {
        if ( image != null ) {
            // Prepare the image for analysis
            ImageEvent imageEvent = new ImageEvent(ImageEvent.imageType.RIGHT, prepareImage());
            Globals.getEventBus().post( imageEvent);
            
            // Get the upper half of the image.
            BufferedImage imageHalf = image.getSubimage(0, 0, image.getWidth(), image.getHeight()/2);
            imageHalf = ImageAnalyzer.getNonEmptyPart(imageHalf);
        }
        
    }

    private BufferedImage prepareImage() {
        // Blacken all pixels below thresholdValue with a lookupop
        // Do it only for the green channel. The other channels is set to 0.
        // Create the lookup table

        System.out.println("Preparing image");

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

    
    
}
