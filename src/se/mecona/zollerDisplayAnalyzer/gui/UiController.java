/**
 * Copyright Mats Andersson, Mecona Teknik AB
 */
package se.mecona.zollerDisplayAnalyzer.gui;

import com.google.common.eventbus.Subscribe;
import java.awt.image.BufferedImage;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.IOResult;
import java.io.File;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.ImageTester;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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

    @FXML
    private TextField digitNo;

    /**
     * Constructor. Creates the imageTester object and registers google guavas 
     * eventbus
     */
    public UiController() {
        imageTester = new ImageTester();
        Globals.getEventBus().register(this);
    }

    @FXML
    /**
     * Handler for the File - Open menu choice
     */
    private void onOpen() {
        FileChooser fileChooser = new FileChooser();
        File dir = new File("E:\\NetBeansProjects\\ZollerDisplayAnalyzer\\TestImages");
        if (dir.exists()) {
            fileChooser.setInitialDirectory(dir);
        } else {
            dir = new File("C:\\Users\\Mcx8\\Documents\\NetBeansProjects\\ZollerDisplayAnalyzer\\TestImages");
            if (dir.exists()) {
                fileChooser.setInitialDirectory(dir);
            }
        }

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            IOResult<BufferedImage> iOResult = imageTester.open(file);
            if (iOResult.isOk() && iOResult.hasData()) {
            }
        }
    }

    @FXML
    /**
     * Handler for the analyze button. Sets up imageTester object from
     * ui elements and calls the analyzer.
     */
    private void onAnalyze() {
        imageTester.setThresholdValue(thresholdSlider.getValue());
        imageTester.setDigitNoToShow(Integer.parseInt(digitNo.getText()));
        imageTester.analyze();
    }

    @FXML
    /**
     * Close event handler
     */
    private void onClose() {
        System.exit(0);
    }

    @FXML
    /**
     * Handler for the Help - About menu choice.
     */
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("Test application for zollerDisplayAnalyzer");
        alert.show();
    }

    @FXML
    /**
     * Handler for the "Run Tests" button
     * Sets up the imageTester objects from ui elements and runs the tests.
     */
    private void onRunTests() {
        imageTester.setThresholdValue(thresholdSlider.getValue());
        imageTester.setDigitNoToShow(Integer.parseInt(digitNo.getText()));
        imageTester.runTests();
    }

    @Subscribe
    /**
     * Handler for ImageEvent. Displays the image sent in the event in either
     * the left or right image view.
     */
    private void handleDebugImageEvent(ImageEvent event) {
        BufferedImage image = event.getImage();
        WritableImage wimg = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, wimg);
        switch (event.getSelectedView()) {
            case LEFT:
                imageView1.setImage(wimg);
                break;

            case RIGHT:
                imageView2.setImage(wimg);
                break;

        }
    }

    /**
     *
     */
    public void initialize() {
        thresholdLabel.textProperty().bind(
                Bindings.format(
                        "%2.0f",
                        thresholdSlider.valueProperty()
                )
        );
        digitNo.setText("5");
    }

}
