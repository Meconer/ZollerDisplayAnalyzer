/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.image.BufferedImage;

/**
 * Represents an image of the display on a zoller tool presetter.
 * @author Mats
 */
class ZollerImage {
    
    private final BufferedImage image;

    // public constructor. Sets the image
    public ZollerImage(BufferedImage image) {
        this.image = image;
    }

    // Analyzes the image
    void analyze() {
        if ( image != null ) {
            // Prepare the image for analysis
            prepareImage();
            
            // Get the upper half of the image.
            BufferedImage imageHalf = image.getSubimage(0, 0, image.getWidth(), image.getHeight()/2);
            imageHalf = ImageAnalyzer.getNonEmptyPart(imageHalf);
        }
    }

    private void prepareImage() {
        System.out.println("Preparing image");
    }
    
    
    
}
