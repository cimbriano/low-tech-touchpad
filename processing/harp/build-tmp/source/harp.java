import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.ugens.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class harp extends PApplet {

/*
  Much of this sketch is derived from the Minim examples/constantExample.pde
 */





// create all of the variables that will need to be accessed in
// more than one methods (setup(), draw(), stop()).
Minim minim;
AudioOutput out;
Constant freqControl;

float[] frequencies = new float[9];
float playingFrequency = 0.0f;

Serial myPort;
String serialMsg = "";
String[] stringValues;
int xReading;
int yReading;

// boolean backgroundIsBlack = true;

public void setup(){
  println(Serial.list());

  myPort = new Serial(this, "/dev/tty.usbmodemfa131", 9600);

  // Read first line in case its a partial message
  myPort.readStringUntil('\n');

  // initialize the drawing window
  size(512, 200, P2D);
  
  // initialize the minim and out objects
  minim = new Minim(this);
  out = minim.getLineOut(Minim.MONO, 2048);

  //Setup frequencies
  frequencies[0] = 261.626f;
  frequencies[1] = 293.665f;
  frequencies[2] = 329.628f;
  frequencies[3] = 349.228f;
  frequencies[4] = 391.995f;
  frequencies[5] = 440.0f;
  frequencies[6] = 493.883f;
  frequencies[7] = 523.251f;

  // make our Oscil, which we'll simply patch to the output
  // arguments are frequency, amplitude and waveform
  Oscil osc = new Oscil( 220.f, 0.5f, Waves.SINE );
  // make a constant UGen that starts with the same value 
  // that we used for the frequency of osc. however, 
  // we are going to change this value over time
  // using mouseX.
  freqControl = new Constant(220.f);
  // patch it directly to the frequency of the Oscil
  freqControl.patch( osc.frequency );
  
  // and connect the oscil to the output
  osc.patch(out);
}

public void draw() {
  // set the value of our Constant UGen based on mouse position
  // float freq = map(mouseX, 0, width, 220.f, 880.f);
  

  // freqControl.setConstant( freq );
  freqControl.setConstant(playingFrequency);

  // // erase the window to black
  // // draw using a white stroke
  background( 255 );
  stroke( 0 );
  
  // if(backgroundIsBlack){
  //   background( 255 );
  //   stroke( 0 );
  // } else {
  //   background( 0 );
  //   stroke( 255 );
  // }
  

  // draw the waveforms
  for( int i = 0; i < out.bufferSize() - 1; i++ )
  {
    // find the x position of each buffer value
    float x1  =  map( i, 0, out.bufferSize(), 0, width );
    float x2  =  map( i + 1, 0, out.bufferSize(), 0, width );
    // draw a line from one buffer position to the next for both channels
    line( x1, 50 + out.left.get(i) * 50, x2, 50 + out.left.get(i + 1) * 50);
    line( x1, 150 + out.right.get(i) * 50, x2, 150 + out.right.get(i + 1) * 50);
  }  
}

public void mousePressed(){
  // backgroundIsBlack = !backgroundIsBlack;
}

public void keyPressed(){
  // backgroundIsBlack = !backgroundIsBlack;
  if('0' <= key && key <= '8'){
    // println("Pressed: " + key);
    // println("int of keypress is: " + int(key - '0'));

    setNote(PApplet.parseInt(key - '1'));
  } else {
    println("Unsupported keyPress: " + key);
  }
}

public void keyReleased(){
  // println("Setting note to 0.0f");
  playingFrequency = 0.0f;
}

public void setNote(int noteNum){
  // println("Setting note to " + frequencies[noteNum]);
  playingFrequency = frequencies[noteNum];
}

public void turnOff(){
  // println("Using turnOff to set note to 0.0f");
  playingFrequency = 0.0f; 
}

public void getTouchpadReadingsFromSerial(){
  if(myPort.available() > 0){
    serialMsg = myPort.readStringUntil('\n');

    if(serialMsg != null) {
      println("Rcvd => '" + serialMsg + "'");

      stringValues = serialMsg.trim().split(",");

      if(stringValues.length != 2) {
        println("INVALID length of CSV line");
      } else {

        if(PApplet.parseInt(stringValues[1]) == -1) {
          turnOff();
        } else {
          float f = new Float(stringValues[1]);
          float noteNum = map(f, 0.0f, 1023.0f, 0.0f, 7.0f);
          setNote(PApplet.parseInt(noteNum));
        }

      }
    } // serialMsg != null
  } // myPort.available() > 0
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "harp" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
