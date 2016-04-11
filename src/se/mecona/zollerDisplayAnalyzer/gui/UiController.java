/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.gui;

import com.google.common.eventbus.Subscribe;
import java.awt.image.BufferedImage;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.IOResult;
import java.io.File;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.ImageTester;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    
    @FXML
    private ImageView imageView2;
    
    @FXML
    private Slider thresholdSlider;
    
    @FXML
    private Label thresholdLabel;
    

    
    public UiController() {
        imageTester = new ImageTester();
        Globals.getEventBus().register(this);
    }


    
    @FXML 
    private void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("E:\\Mats"));
        File file = fileChooser.showOpenDialog(null);
        if ( file != null ) {
            IOResult<BufferedImage> iOResult = imageTester.open( file );
            if ( iOResult.isOk() && iOResult.hasData() ) {
            }
        }
    }
    
    @FXML
    private void onAnalyze() {
        imageTester.setThresholdValue( thresholdSlider.getValue() );
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
    
    @Subscribe
    private void handleDebugImageEvent(ImageEvent event) {
        BufferedImage image = event.getImage();
        WritableImage wimg = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, wimg);
        switch ( event.getType() ) {
            case LEFT : imageView1.setImage(wimg);
            break;
            
            case RIGHT : imageView2.setImage(wimg);
            break;
            
        }
    }
    
    public void initialize() {
            thresholdLabel.textProperty().bind(
            Bindings.format(
                "%2.0f",
                thresholdSlider.valueProperty()
            )
        );

    }

}
