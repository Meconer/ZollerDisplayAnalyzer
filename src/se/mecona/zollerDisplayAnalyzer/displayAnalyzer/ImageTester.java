/*
 * Copyright Mats Andersson, Mecona Teknik AB
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.ZollerImage.AnalyzeRow;
import se.mecona.zollerDisplayAnalyzer.gui.Globals;
import se.mecona.zollerDisplayAnalyzer.gui.ImageEvent;

/**
 *
 * @author Mats Andersson Mecona Teknik AB
 */
public class ImageTester {

    private BufferedImage image;
    private int threshold;
    private int digitToShow;
    private String lowerDigits;
    private String upperDigits;

    /**
     * Opens an image file. It also sends the image on the event bus for the gui
     * to show.
     * 
     * @param file  The file to open
     * @return an IOResult object with the data and status information.
     * @see IOResult
     */
    public IOResult open(File file) {
        try {
            image = ImageIO.read(file);
            Globals.getEventBus().post(new ImageEvent(ImageEvent.selectedImageView.LEFT, image));
            return new IOResult<>(true, image);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            image = null;
            return new IOResult<>(false, null);
        }
    }

    /**
     * Analyzes the image contained in this object, if it exists.
     * Stores the result internally
     */
    public void analyze() {
        if (image != null) {
            ZollerImage zollerImage = new ZollerImage(image);
            zollerImage.setThreshold(threshold);
            zollerImage.setDigitToShow(digitToShow);

            zollerImage.analyze(ZollerImage.AnalyzeRow.BOTH);

            upperDigits = zollerImage.getDigits(AnalyzeRow.UPPER);
            System.out.println("Upper digits = " + upperDigits);

            lowerDigits = zollerImage.getDigits(AnalyzeRow.LOWER);
            System.out.println("Lower digits = " + lowerDigits);
        }
    }

    /**
     * Sets the threshold value for the analysis. Every pixel brighter than the
     * threshold value is set to full bright and all others are set to dark.
     * @param value The threshold value (0-255)
     */
    public void setThresholdValue(double value) {
        threshold = (int) Math.round(value);
    }

    /**
     * Set digit to show field. Used to send the image containing the digit in 
     * this position via the event bus. For debugging purposes.
     * @param digitToShow The digit position to send via event bus.
     */
    public void setDigitNoToShow(int digitToShow) {
        this.digitToShow = digitToShow;
    }

    /**
     * Run a test sequence. For debugging purposes. Takes a series of images
     * containing display pictures, analyzes them and checks to see if the 
     * result is correct. Two arrays of correct numbers is set first. Sends
     * the result of the test to standard output.
     */
    public void runTests() {
        int picNo = 0;
        String[] upperNumbers = {"140.500",
            "140.501",
            "140.502",
            "140.503",
            "140.504",
            "140.505",
            "140.506",
            "140.507",
            "140.508",
            "140.509",
            "161.494",
            "158.888",
            "158.030",
            "158.122",
            "158.216",
            "158.323",
            "158.421",
            "158.536",
            "158.637",
            "158.735",
            "158.836",
            "158.911",
            "83.627",
            "113.663"
        };

        String[] lowerNumbers = {".055",
            ".056",
            ".056",
            ".056",
            ".056",
            ".056",
            ".056",
            ".056",
            ".056",
            ".056",
            ".057",
            ".057",
            ".057",
            ".057",
            ".057",
            ".058",
            ".058",
            ".058",
            ".058",
            ".058",
            ".058",
            ".058",
            "34.788",
            ".022"
        };

        int errorCount = 0;

        while (picNo < upperNumbers.length) {
            int fileNo = picNo + 10;
            String fileName = "E:\\Netbeansprojects\\ZollerDisplayAnalyzer\\TestImages\\zoller" + fileNo + ".jpg";
            IOResult<BufferedImage> ior = open(new File(fileName));
            if (ior.hasData() && ior.isOk()) {
                ZollerImage zollerImage = new ZollerImage(ior.getData());
                zollerImage.setThreshold(threshold);
                zollerImage.setDigitToShow(digitToShow);
                zollerImage.analyze(ZollerImage.AnalyzeRow.BOTH);
                
                String upperDigits = zollerImage.getDigits(AnalyzeRow.UPPER);
                if ( !upperDigits.equals(upperNumbers[picNo]) ) {
                    System.out.println("********** Analysis Error!! ***********");
                    System.out.println("Upper digits = " + upperDigits + " : should be : " + upperNumbers[picNo]);
                    System.out.println("File: " + fileName);
                    errorCount++;
                } else {
                    //System.out.println("File: " + fileName);
                    System.out.println("Digits : " + upperDigits);
                }
                
                String lowerDigits = zollerImage.getDigits(AnalyzeRow.LOWER);
                if ( !lowerDigits.equals(lowerNumbers[picNo]) ) {
                    System.out.println("********** Analysis Error!! ***********");
                    System.out.println("Lower digits = " + lowerDigits + " : should be : " + lowerNumbers[picNo]);
                    System.out.println("File: " + fileName);
                    errorCount++;
                } else {
                    //System.out.println("File: " + fileName);
                    System.out.println("Digits : " + lowerDigits);
                }
            }
            picNo++;
        }
        if ( errorCount > 0 ) System.out.println(errorCount + " errors in analysis");
        else System.out.println("No errors");
    }

    /**
     * Get the upper digit result.
     * @return the upper digits as analyzed.
     */
    public String getUpper() {
        return upperDigits;
    }

    
    /**
     * Get the lower digit result.
     * @return the lower digits as analyzed.
     */
    public String getLower() {
        return lowerDigits;
    }

}
