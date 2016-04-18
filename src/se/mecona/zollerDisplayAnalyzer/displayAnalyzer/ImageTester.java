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
import se.mecona.zollerDisplayAnalyzer.displayAnalyzer.ZollerImage.AnalyzeRow;
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
        if (image != null) {
            ZollerImage zollerImage = new ZollerImage(image);
            zollerImage.setThreshold(threshold);
            zollerImage.setDigitToShow(digitToShow);

            zollerImage.analyze(ZollerImage.AnalyzeRow.BOTH);

            String upperDigits = zollerImage.getDigits(AnalyzeRow.UPPER);
            System.out.println("Upper digits = " + upperDigits);

            String lowerDigits = zollerImage.getDigits(AnalyzeRow.LOWER);
            System.out.println("Lower digits = " + lowerDigits);
        }
    }

    public void setThresholdValue(double value) {
        threshold = (int) Math.round(value);
    }

    public void setDigitNoToShow(int digitToShow) {
        this.digitToShow = digitToShow;
    }

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

        int errorCount = 0;

        while (picNo < upperNumbers.length) {
            int fileNo = picNo + 10;
            String fileName = "E:\\Netbeansprojects\\ZollerDisplayAnalyzer\\TestImages\\zoller" + fileNo + ".jpg";
            IOResult<BufferedImage> ior = open(new File(fileName));
            if (ior.hasData() && ior.isOk()) {
                ZollerImage zollerImage = new ZollerImage(ior.getData());
                zollerImage.setThreshold(threshold);
                zollerImage.setDigitToShow(digitToShow);
                zollerImage.analyze(ZollerImage.AnalyzeRow.UPPER);
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
            }
            picNo++;
        }
        if ( errorCount > 0 ) System.out.println(errorCount + " errors in analysis");
        else System.out.println("No errors");
    }

}
