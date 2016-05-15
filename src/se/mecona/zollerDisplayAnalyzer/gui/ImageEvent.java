/**
 * Copyright Mats Andersson, Mecona Teknik AB
 */

package se.mecona.zollerDisplayAnalyzer.gui;

import java.awt.image.BufferedImage;

/**
 * Class for image event used to send intermediate images from the analysis
 * to display them in the left or right views in the gui.
 * @author Mats
 */
public class ImageEvent {

    /**
     * The view to show the image in. LEFT or RIGHT
     */
    public enum selectedImageView { 

        /**
         * Selects the LEFT view for the image
         */
        LEFT, 

        /**
         * Selects the RIGHT view for the image
         */
        RIGHT };
    private final selectedImageView selectedView;
    private final BufferedImage image;

    /**
     * Constructor for the ImageEvent class. Takes the view to show the image in
     * and the image as parameters.
     * @param selectedView  The view to show the image in. LEFT or RIGHT
     * @param image         The image to show
     */
    public ImageEvent(selectedImageView selectedView, BufferedImage image) {
        this.selectedView = selectedView;
        this.image = image;
    }

    /**
     * 
     * @return the selected image view, LEFT or RIGHT
     */
    public selectedImageView getSelectedView() {
        return selectedView;
    }

    /**
     * 
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }
    
    
    
}
