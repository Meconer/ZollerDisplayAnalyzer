/*
 * Copyright Mats Andersson, Mecona Teknik AB
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import se.mecona.zollerDisplayAnalyzer.gui.Globals;
import se.mecona.zollerDisplayAnalyzer.gui.ImageEvent;

/**
 * Represents an image of the display on a zoller tool presetter.
 *
 * @author Mats Andersson Mecona Teknik AB
 */
class ZollerImage {



    /**
     * Default threshold value for this image
     */
    private static final int DEFAULT_THRESHOLD = 85;
    
    /**
     * No of digits in one line.
     */
    private static final int NO_OF_DIGITS = 9;

    /**
     * The fraction of the image length that is used as spacing between the digits.
     */
    private static final int PADDING_DIVISOR = 50;

    /**
     * The fraction of the image that is used for extra padding if last digit is a "1"
     */
    private static final int PADDING_FRACTION_FOR_LAST_DIGIT_1 = 30;

    /**
     * The amount of shearing that should be applied to the digits to make them straight
     */
    private static final double SHEAR_VALUE = 0.1;

    /**
     * The digit no that contains a decimalpoint.
     */
    private static final int DECIMAL_POINT_DIGIT_NO = 5;

    /**
     * The digit no that can contain the minus sign
     */
    private static final int MINUS_SIGN_DIGIT_NO = 2;

    /**
     * Digit value representing an empty or non valid digit
     */
    public static final int NO_DIGIT = -3;

    /**
     * Digit value representing a minus sign
     */
    public static final int MINUS_SIGN = -2;

    /**
     * Digit value representing a decimal point.
     */
    public static final int DECIMAL_POINT = -1;

    /**
     * Enum to describe wich part of the image that should be analyzed
     */
    public enum AnalyzeRow { 

        /**
         * Analyze only the upper part of the image
         */
        UPPER, 

        /**
         * Analyze only the lower part of the image
         */
        LOWER, 

        /**
         * Analyze both parts of the image
         */
        BOTH };

    /**
     * The image
     */
    private BufferedImage image;

    /**
     * The threshold value. Ignores pixels darker than this value.
     */
    private short thresholdValue = DEFAULT_THRESHOLD;
    
    // Used for debug purposes. During the analysis the image containing this 
    // digit position is sent via the eventbus.

    /**
     * Used for debug purposes. During the analysis the image containing this 
     * digit position is sent via the eventbus.
     */
    private int digitToShow;
    
    /**
     * Two arrays for storing the result of the analysis.
     * The upper value
     */
    private final int[] zollerUpperDigit;

    /**
     * The lower value
     */
    private final int[] zollerLowerDigit;

    

    /**
     *  public constructor. Sets the image
     * @param image The image to be analyzed
     */
    public ZollerImage(BufferedImage image) {
        this.image = image;
        zollerUpperDigit = new int[NO_OF_DIGITS];
        zollerLowerDigit = new int[NO_OF_DIGITS];
    }

    /**
     * Get a specific digit from the result of the analysis of the image.
     * @param ar        The row to get the digit from. 
     * @param digitNo   The digit position to get.
     * @return the digit specified. Returns garbage if the analysis isn't done
     */
    private int getDigit( AnalyzeRow ar, int digitNo  ) {
        if ( ar == AnalyzeRow.UPPER ) 
            return zollerUpperDigit[ digitNo ];
        else 
            return zollerLowerDigit[ digitNo ];
    }
    
    /**
     * Gets the digit from the analysis of the image.
     * @param analyzeRow The row to get the digits from.
     * @return a string with the digits.
     */
    public String getDigits(AnalyzeRow analyzeRow) {
        StringBuilder number = new StringBuilder();

        // Check the minus sign
        int nCnt = MINUS_SIGN_DIGIT_NO;
        int digit = getDigit( analyzeRow, nCnt );
        if ( digit == MINUS_SIGN ) number.append("-");

        // Find first non digit
        do {
            nCnt++;
            digit = getDigit( analyzeRow, nCnt );
        } while ( digit == NO_DIGIT );

        // Add the digits
        while (nCnt < NO_OF_DIGITS ) {
            digit = getDigit(analyzeRow, nCnt );
            
            if (digit == NO_DIGIT) return "FEL";
            number.append(digit);
            nCnt++;
        }
        
        // Insert the decimal point
        int l = number.length();
        number = number.insert(l-3, ".");
        return number.toString();

    }
    
    /**
     * Performs the analysis of the image
     * @param analyzeRow    The row to be analyzed. UPPER, LOWER or BOTH.
     */
    public void analyze( AnalyzeRow analyzeRow) {
        if (image != null) {
            // Prepare the image for analysis
            prepareImage();

            //Globals.getEventBus().post( new ImageEvent(ImageEvent.imageType.RIGHT, image));

            if ( analyzeRow == AnalyzeRow.LOWER || analyzeRow == AnalyzeRow.BOTH ) {
                // Analyzes the upper half of the image
                // Get the upper half of the image.
                BufferedImage imageLower = image.getSubimage(0, image.getHeight() / 2, image.getWidth(), image.getHeight() / 2);
                analyzeDigitRow(imageLower, AnalyzeRow.LOWER);
            }

            if ( analyzeRow == AnalyzeRow.UPPER  || analyzeRow == AnalyzeRow.BOTH) {
                // Analyzes the lower half of the image
                // Get the upper half of the image.
                BufferedImage imageUpper = image.getSubimage(0, 0, image.getWidth(), image.getHeight() / 2);
                analyzeDigitRow(imageUpper, AnalyzeRow.UPPER);
            }
        }

    }

    /**
     * Prepares the image for analysis. Performs a threshold operation 
     * to get only bright or dark pixels.
     */
    private void prepareImage() {
        // Blacken all pixels below thresholdValue with a lookupop
        // Do it only for the green channel. The other channels is set to 0.
        // Create the lookup table

        //System.out.println("Preparing image");
        short[] threshold = new short[256];

        for (int i = 0; i < 256; i++) {
            threshold[i] = (i < thresholdValue) ? (short) 0 : (short) 255;
        }

        short[][] greenThreshold = new short[][]{threshold, threshold, threshold};
        // Create the thresholdValue operation
        BufferedImageOp thresholdOp = new LookupOp(new ShortLookupTable(0, greenThreshold), null);

        // Create a new image with the same dimensions as the original
        BufferedImage lupopedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Do the thresholdValue operation.
        thresholdOp.filter(image, lupopedImage);
        image = lupopedImage;

    }

    /**
     * Sets the threshold value for the analysis
     * @param threshold Threshold value.
     */
    public void setThreshold(int threshold) {
        thresholdValue = (short) threshold;
    }

    /**
     * Analyzes an image containing one line of digits.
     * @param image         The image to analyse
     * @param rowToAnalyze  The row to analyse. UPPER, LOWER or BOTH
     */
    private void analyzeDigitRow(BufferedImage image, AnalyzeRow rowToAnalyze) {

        // Shear the image so the digits are straight up.
        image = ImageAnalyzer.shearImage(image, SHEAR_VALUE);

        // Get the part of the image that contains digits.
        BufferedImage subImage = ImageAnalyzer.getNonEmptyPart(image, PADDING_DIVISOR, 0);

        // Now, check if the last digit is a "1". If that is the case we need to redo the subimage
        // and add a little extra padding at the end.
        if (checkIfLastDigitIsOne(subImage)) {
            subImage = ImageAnalyzer.getNonEmptyPart(image, PADDING_DIVISOR, PADDING_FRACTION_FOR_LAST_DIGIT_1);
        }

        // Loop and get the subimage for each digit and try to work out what digit it is.
        int digitWidth = subImage.getWidth() / NO_OF_DIGITS;
        int digitHeight = subImage.getHeight();
        BufferedImage digitImage;
        for (int digitNo = 0; digitNo < NO_OF_DIGITS; digitNo++) {
            digitImage = subImage.getSubimage(
                    digitNo * digitWidth,
                    0,
                    digitWidth,
                    digitHeight);
            if (digitNo == digitToShow) {
                Globals.getEventBus().post(new ImageEvent(ImageEvent.selectedImageView.RIGHT, digitImage));
            }
            int digit = analyzeDigitImage(digitImage, digitNo);
            if ( rowToAnalyze == AnalyzeRow.UPPER) zollerUpperDigit[digitNo] = digit;
            else zollerLowerDigit[digitNo] = digit;
        }
    }

    /**
     * Checks if the last digit is a "1". 
     * @param image The image to check
     * @return
     */
    private boolean checkIfLastDigitIsOne(BufferedImage image) {
        int width = image.getWidth();
        int digitAreaWidth = width / NO_OF_DIGITS;
        BufferedImage lastDigitImage = image.getSubimage(width - digitAreaWidth, 0, digitAreaWidth, image.getHeight());

        // If the last digit is one it is possible that some "dirt" from the previous digit is in the left part of
        // the digit part. Remove that.
        lastDigitImage = ImageAnalyzer.removeBrightAtLeft(lastDigitImage);

        int digitWidth = ImageAnalyzer.calcFilledWidth(lastDigitImage);

        if ( digitAreaWidth / digitWidth > 3 ) {
            // If this digit is a "1" then return true;
            //System.out.println("Last digit is one (width is small)");
            return true;
        }
        return false;
    }

    /**
     * Sets the digit to send via the event bus during the analysis.
     * @param digitToShow The digit position to send.
     */
    void setDigitToShow(int digitToShow) {
        this.digitToShow = digitToShow;
    }

    /**
     * returns an int representing the digit found or NO_DIGIT or DECIMAL_POINT or MINUS_SIGN
     * @param digitImage    The image of one digit.
     * @param digitNo       The digit position.
     * @return
     */
    private int analyzeDigitImage(BufferedImage digitImage, int digitNo ) {

        // Return NO_DIGIT if image is null or digitNo less than minus sign position.
        if (digitImage == null  || digitNo < MINUS_SIGN_DIGIT_NO ) {
            return NO_DIGIT;
        }

        // Get the digit image dimensions.
        int height = digitImage.getHeight();
        int width = digitImage.getWidth();

        // if it is empty, return NO_DIGIT
        if (height == 0 || width == 0) {
            return NO_DIGIT;
        }

        // Check if this is the digit with decimal point and blacken the part that contains the 
        // point if that is the case.
        if ( digitNo == DECIMAL_POINT_DIGIT_NO ) { 
            digitImage = removeDecimalPoint( digitImage );
            //Globals.getEventBus().post(new ImageEvent(ImageEvent.imageType.RIGHT , digitImage ));
        }
        
        // If the image is all black, return NO_DIGIT
        if (ImageAnalyzer.isAllBlack(digitImage)) {
            return NO_DIGIT;
        } else {
            // if this is the minus sign position and the image is not black then it
            // must be a minus sign.
            if ( digitNo == MINUS_SIGN_DIGIT_NO ) return MINUS_SIGN;
        }

        // Check if the digit is a "1" by checking the width of the bright area.
        if ( isOne( digitImage) ) {
            // Digit is a "1". 
            return 1;
        }
        
        // Now look at the bright places in the image and find out what digit it is
        int digit = analyzeSegments(digitImage);
        
        return digit;
    }

    /**
     * Performs an analysis of the bright parts of an image containing just one
     * digit. 
     * @param digitImage The digit image to analyze
     * @return an int representing the image found.
     */
    private int analyzeSegments(BufferedImage digitImage) {
        // First remove black space around the image.
        
        digitImage = ImageAnalyzer.getNonEmptyPart(digitImage, 0,0);
        
        /*
        These are the segments for the different digits.
        Possible digits:
        1 - already detected 
        2 - 0 and 11 empty
        3 - 0 and 1 empty
        4 - 1 and 2-7 empty
        5 - 1 and 10 empty
        6 - 10 empty
        7 - 0, 1 and 3-8 empty
        8 - none empty
        9 - 1 empty
        0 - 3-8 empty
         -- 2 -- -- 7 --
        |       |       |
        |       |       |
        0       5      10
        |       |       |
        |       |       |
         -- 3 -- -- 8 --
        |       |       |
        |       |       |
        1       6      11
        |       |       |
        |       |       |
         -- 4 -- -- 9 --  
        
        6 areas to analyze. 
        We split the digit image in eight parts vertically and tree parts horisontally.
                    colStart    colEnd     rowStart     rowEnd
        Area 0      0          1/3         1/8         3/8
        Area 1      0          1/3         5/8         7/8
        Area 10     2/3        3/3         1/8         3/8
        Area 11     2/3        3/3         5/8         7/8
        Area 2-7    1/3        2/3         0           2/8
        Area 3-8    1/3        2/3         3/8         5/8
         */
        //             Areas   :   0  1  10 11 27 38
        final boolean[][] areas = {{true, true, true, true, true, false}, // digit 0
        {false, false, false, false, true, true}, // 1 
        {false, true, true, false, true, true}, // 2
        {false, false, true, true, true, true}, // 3
        {true, false, true, true, false, true}, // 4
        {true, false, false, true, true, true}, // 5
        {true, true, false, true, true, true}, // 6
        {false, false, true, true, true, false}, // 7
        {true, true, true, true, true, true}, // 8
        {true, false, true, true, true, true}};// 9
        final int[] colStart = {0, 0, 2, 2, 1, 1}; // Start column for area
        final int[] rowStart = {1, 5, 1, 5, 0, 3}; // Start row for area


        boolean[] filledArea = new boolean[6];
        for (int i = 0; i < 6; i++) {
            filledArea[i] = scanArea(colStart[i], rowStart[i], digitImage );
        }

        // Now scan the filled areas to find out which digit it is.
        int thisDigit = NO_DIGIT;
        for (int i = 0; i <= 9; i++) {
            if (isThisDigit(areas[i], filledArea)) {
                thisDigit = i;
                break;
            }
        }
        return thisDigit;
    }

    /**
     * Checking the bright areas against the specified for a digit
     * 
     * @param areas         The areas that should be bright for the digit to check
     * @param filledArea    The areas that is bright in this image.
     * @return
     */
    private boolean isThisDigit(boolean[] areas, boolean[] filledArea) {
        // Check for first mismatch and then return false
        // if no mismatches then this must be the correct digit so return true
        for (int area = 0; area < areas.length; area++) {
            if (areas[area] != filledArea[area]) {
                return false;
            }
        }
        // All segments are checked and if we got here the digit must be the correct one so return true
        return true;

    }

    /**
     * Removes the decimal point area (sets it to black) of the digit image.
     * @param image The image to use.
     * @return a new image with the decimal point area removed.
     */
    private BufferedImage removeDecimalPoint(BufferedImage image) {
        if ( image != null ) {
            int width = image.getWidth();
            int height = image.getHeight();
            image = ImageAnalyzer.fillWithBlack( image, width * 3/4, height * 7/8, width/4, height/8 );
        }
        return image;
    }

    
    /**
     * Check if the bright area is narrow compared to the image width. Then it is
     *  probably a "1". Returns true if that's the case.
     * 
     * @param digitImage    The image to check
     * @return true if the image contains a "1".
     */
    private boolean isOne(BufferedImage digitImage) {
        int digitWidth = ImageAnalyzer.calcFilledWidth(digitImage);
        
        if ( digitWidth == 0 ) {
            System.out.println("digit width = 0. Shouldn't happen here.");
            //Globals.getEventBus().post(new ImageEvent(ImageEvent.imageType.RIGHT, digitImage));
            
        }

        return ( digitImage.getWidth() / digitWidth > 3 );
    }


    /**
     * Scans different segment areas of the image for bright or dark parts.
     * @param colStart      The column to start checking
     * @param rowStart      The row to start checking
     * @param digitImage    The image to check.
     * @return true if this segment is bright.
     */
    private boolean scanArea(int colStart, int rowStart, BufferedImage digitImage ) {
        int digitWidth = digitImage.getWidth();
        int xStart = digitWidth * colStart / 3;
        int xWidth = digitWidth / 3;

        // Width is a third of image width
        int digitHeight = digitImage.getHeight();
        int yStart = digitHeight * rowStart / 8;
        int yHeigth = digitHeight / 4; // Height is 2/8 of image heigth
        BufferedImage area = digitImage.getSubimage(xStart, yStart, xWidth, yHeigth);
        boolean isBright =  !ImageAnalyzer.isAllBlack(area);
        return isBright;
    }

    /**
     * Writes an image to disk.
     * @param im        The image to write
     * @param filename  The filename of the image file to write.
     */
    private void saveImage(BufferedImage im, String filename) {
        try {
                ImageIO.write(im, "JPEG", new File(filename));
            } catch (IOException ex) {
                System.out.println("Problem im saveImage " + ex.getMessage());
            }
    }

}
