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

/**
 *
 * @author Mats
 */
public class ImageTester {
    BufferedImage image;

    public IOResult open(File file) {
        try {
            image = ImageIO.read(file);
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
            zollerImage.analyze();
        }
    }
    
}
