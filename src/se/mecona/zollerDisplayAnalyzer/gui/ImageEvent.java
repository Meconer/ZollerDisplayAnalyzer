/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.gui;

import java.awt.image.BufferedImage;

/**
 *
 * @author Mats
 */
public class ImageEvent {
    public enum imageType { LEFT, RIGHT };
    private final imageType type;
    private final BufferedImage image;

    public ImageEvent(imageType type, BufferedImage image) {
        this.type = type;
        this.image = image;
    }

    public imageType getType() {
        return type;
    }

    public BufferedImage getImage() {
        return image;
    }
    
    
    
}
