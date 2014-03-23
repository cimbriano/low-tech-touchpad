/*
  AnalogReadSerial
  Reads an analog input on pin 0, prints the result to the serial monitor.
  Attach the center pin of a potentiometer to pin A0, and the outside pins to +5V and ground.
 
 This example code is in the public domain.
 */

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
}

// the loop routine runs over and over again forever:
void loop() {
  // read the input on analog pin 0:
  int sensorValue = analogRead(A0);
  int sensorValue2 = analogRead(A1);
  // print out the value you read:
  //Serial.print("x: ");
  if (sensorValue < 500 && sensorValue2 < 500) {
    Serial.print(map(sensorValue, 80, 450, 0, 1024));
    Serial.print(",");
    Serial.println(map(sensorValue2, 30, 460, 0, 1024));
  } else if (sensorValue2 < 500) {
    Serial.print("-1,");
    Serial.println(map(sensorValue2, 30, 460, 0, 1024));
  } else {
    Serial.println("-1,-1");
  }
  delay();        // delay in between reads for stability
}
