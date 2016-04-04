/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.gui;

import java.awt.image.BufferedImage;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.IOResult;
import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.ImageTester;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Mats
 */
public class UiController {
    
    ImageTester imageTester;
    
    @FXML
    private ImageView imageView1;
    private ImageView imageView2;

    public UiController(ImageTester imageTester) {
        this.imageTester = imageTester;
    }
    
    @FXML 
    private void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("E:\\Mats"));
        File file = fileChooser.showOpenDialog(null);
        if ( file != null ) {
            IOResult<BufferedImage> iOResult = imageTester.open( file );
            if ( iOResult.isOk() && iOResult.hasData() ) {
                BufferedImage bimg = iOResult.getData();
                WritableImage wimg = new WritableImage(bimg.getWidth(), bimg.getHeight());
                SwingFXUtils.toFXImage(bimg, wimg);
                imageView1.setImage(wimg);
            }
        }
    }
    
    @FXML
    private void onAnalyze() {
        imageTester.analyze();
    }
    
    @FXML 
    private void onClose() {
        System.exit(0);
    }
    
    @FXML 
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("Test application for zollerDisplayAnalyzer");
        alert.show();
    }
    

}
