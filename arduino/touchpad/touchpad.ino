/* touchpad.c
 *
 * author: Peter Enns
 */

// constants
const int x_pin = A0;
const int y_pin = A1;
const int RESTING = 1024 - 20; // Readings above RESTING mean that the sensor is NOT being pressed
const int TOLERANCE = 20; // How much the current analog read value must differ from the previous one
const int DELAY_TIME = 50; // How long to wait between measurements

int last_x = 0;
int last_y = 0;
boolean touch_in_progress = false;

void setup() {
  Mouse.begin();
}

int delta(int last, int curr);
  
void loop() {
  int x = analogRead(x_pin);
  int y = analogRead(y_pin);
  
  // Move the cursor if the touchpad is continuously being touched
  // in "different enough" (see delta() and TOLERANCE) places
  if (x < RESTING || y < RESTING) {
    if (touch_in_progress) {
      Mouse.move(delta(last_x, x), delta(last_y, y), 0);
    } else {
      touch_in_progress = true;
    }
    
    last_x = x;
    last_y = y;
  } else if (touch_in_progress) {
    touch_in_progress = false;
  }
  
  delay(DELAY_TIME);
}

/* delay
 *
 * return a distance for the mouse to move unless we just read noise
 */
int delta(int last, int curr) {
  if (abs(curr - last) > TOLERANCE)
    return curr - last;
  return 0;
}
