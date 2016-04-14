/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import se.mecona.zollerDisplayAnalyzer.gui.Globals;
import se.mecona.zollerDisplayAnalyzer.gui.ImageEvent;

/**
 *
 * @author Mats
 */
public class ImageTester {
    private BufferedImage image;
    private int threshold;
    private int digitToShow;
    
    public IOResult open(File file) {
        try {
            image = ImageIO.read(file);
            Globals.getEventBus().post(new ImageEvent(ImageEvent.imageType.LEFT, image));
            return new IOResult<>(true, image);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            image = null;
            return new IOResult<>(false, null);
        }
    }

    public void analyze() {
        if ( image != null ) {
            ZollerImage zollerImage = new ZollerImage( image );
            zollerImage.setThreshold( threshold );
            zollerImage.setDigitToShow( digitToShow );
            zollerImage.analyze();
        }
    }

    public void setThresholdValue(double value) {
        threshold = (int) Math.round(value);
    }

    public void setDigitNoToShow(int digitToShow) {
        this.digitToShow = digitToShow;
    }

    
}
