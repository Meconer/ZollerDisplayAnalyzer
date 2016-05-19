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

Globals.getEventBus().post( new ImageEvent(ImageEvent.imageType.RIGHT, image));

Of course, if you have more than one of these calls the last one will be the
only one shown.

The eventbus is from the google guava library. The DisplayAnalyzer package
uses it only for sending images to the gui. The reason I used guava is because
I wanted to try it out a little. It's easily deleted if you don't need the 
image sending.