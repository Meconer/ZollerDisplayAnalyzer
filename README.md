# ZollerDisplayAnalyzer
Application to test a zoller display analysis class

The app is divided in two parts, the gui part and the analysis part.

The analysis part is made to be used in one of my projects on a raspberry pi
with a camera module placed in front of the
display of a Zoller tool presetter. 

To use it, just hit File-Open and then choose one of the sample files in 
the "Testimages" folder. Press Analyze and the result of the reading is 
sent to system.out. The digit in the position set in the text field is shown
to the right.

If you want to play with the code there is a way to get intermediate images
displayed. You just need to send it via the eventbus with the call:

```java
Globals.getEventBus().post( new ImageEvent(ImageEvent.imageType.RIGHT, image));
```

Of course, if you have more than one of these calls the last one will be the
only one shown.

The eventbus is from the google guava library. The DisplayAnalyzer package
uses it only for sending images to the gui. The reason I used guava is because
I wanted to try it out a little. It's easily deleted if you don't need the 
image sending.

### Analysis steps

The analysis is done in a few steps I am trying to explain here.

First the image is loaded from disk into an object of the imageTester class
created by the gui.

The analysis process is started with the press of the "Analyze" button. The
threshold value is set from the slider in the ui. The digit to show in the
ui is also set up from the text field.

A call to imageTester.analyze starts the analysis. The imageTester object 
then creates a new object of class ZollerImage which does
the real work.

* The zollerImage objects firsts prepares the image by applying a threshold
operation to clean up the image. It is only done in the green channel since
the display is mostly green.

* Then a check is made wich part of the image should be analyzed. I've done this
to save time if only one of the two numbers is needed. The analysis is made on
half of the image at a time in the method AnalyzeDigitRow

* In the original image the digits are leaning a little. A shearing operation is
done so they are straightened.

![image](https://cloud.githubusercontent.com/assets/7084694/15448910/30b3f456-1f6f-11e6-97af-af4bc73ea8ae.png)

* Now the image is cropped so the black area around the digits is removed. This
is an important part of the process. I have to consider the space between the 
digits. We need to save a little part to the left and right that is equal to 
half the space width. If we don't do this and divide the remaining image in 
nine parts (one for each digit) We won't get the digits in the middle of each
of the nine sub images. To fix this I use a constant PADDING_DIVISOR. I take
the width of the bright area, divide it with PADDING_DIVISOR and then get the
number of pixel columns that should be added to the left and right.

If, for example, the bright area is 644 pixels wide and PADDING_DIVISOR is 50,
we add 644/50 = 12 pixels to the right and left.

Since the first position always has a letter in it (X or Z in the test images) 
and the last position always has a digit we know that these positions is always
bright. This makes it easier since we always know how many digit (or symbols 
really) positions are in the image.

There is one additional problem however. The last digit could be a "1". Since
this display has 12 segments the segments for the digit "1" is not the rightmost
segments but in the middle. We have to consider this and thus we have
an extra check for this in the method checkIfLastDigitIsOne. In this method we
check the width of the bright part in this digit position. If it is less than
a third of the total width then we decide that this must be a "1" and the image
is recropped with a little more area to the right. This extra width is set with
the PADDING_FRACTION_FOR_LAST_DIGIT_1 constant.

All this is handled in the method getNonEmptyArea.

Now we have an image looking like this:
![image](https://cloud.githubusercontent.com/assets/7084694/15453582/635a28a6-201b-11e6-98ba-7c6c695e5b37.png)


* Now that we have got the digit area we can start to analyze each digit position.
This is made in the analyzeDigitImage method.
There are som special positions in this display. The first position is always a
letter containing the measuring axis. We don't care about this one so we just
ignore it and store the NO_DIGIT constant.

The next position is always the sign position. For this we only need to see if
the digit area has any bright pixels in it since there is no plus sign. Only 
minus if it is a negative number.

The position containing the decimal point is always position no 5. For this, we
set the area containg the point to black so it don't interfer with the segment
analysis. I divided the width into four parts and the height in eight parts and
sets the lowest rightmost 1/4 by 1/8 to black.

* Finally it's time to look at the different segments of each digit. First we 
remove the black empty part around the digit. We can then look at the width of
the remaining part. If it is narrow, the digit is a "1". This check is made in
the method isOne. Otherwise we carry on and look at the segments.

If we number the segments like this:

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

and do a bit of thinking we get the following unique patterns for the different
digits. 

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

This description is work in progress but will hopefully be ready soon.
