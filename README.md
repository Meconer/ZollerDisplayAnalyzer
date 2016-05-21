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
half of the image at a time.

* In the original image the digits are leaning a little. A shearing operation is
done so they are straightened.

![image](https://cloud.githubusercontent.com/assets/7084694/15448910/30b3f456-1f6f-11e6-97af-af4bc73ea8ae.png)

* Now the image is cropped so the black area around the digits is removed. This
is an important part of the process. We have to consider the space between the 
digits. We need to save a little part to the left and right that is equal to 
half the space width. If we don't do this and divide the remaining image in 
nine parts (one for each digit) We won't get the digits in the middle of each
of the nine sub images. To fix this I use a constant PADDING_DIVISOR. I take
the width of the bright area, divide it with PADDING_DIVISOR and then get the
number of pixel columns that should be added to the left and right.

Since the first position always has a letter in it (X or Z in the test images) 
and the last position always has a digit we know that these positions is always
bright. This makes it easier since we always know how many digit (or symbols 
really) positions are in the image.

There is one additional problem however. The last digit could be a "1". Since
this display has 12 segments the segments for the digit "1" is not the rightmost
segments but they are in the middle. We have to consider this and thus we have
an extra check for this in the method checkIfLastDigitIsOne. In this method we
check the width of the bright part in this digit position. If it is less than
a third of the total width then we decide that this must be a "1" and the image
is recropped with a little more area to the right. This extra width is set with
the PADDING_FRACTION_FOR_LAST_DIGIT_1 constant.