/* touchpad.c
 *
 * author: Peter Enns
 */

// constants
const int x_pin = A0;
const int y_pin = A1;
const int RESTING = 1024 - 20; // Readings above RESTING mean that the sensor is NOT being pressed
const int TOLERANCE = 10; // How much the current analog read value must differ from the previous one
const int DELAY_TIME = 3; // How long to wait between measurements
const int BUFF_SIZE = 7;

int xbuff[BUFF_SIZE] = {0};
int ybuff[BUFF_SIZE] = {0};
int xp = 0;
int yp = 0;
int last_x = 0;
int last_y = 0;
boolean touch_in_progress = false;

void setup() {
  Mouse.begin();
}

int delta(int last, int curr);
int ave(int buff[]) {
  int sum = 0;
  for (int i = 0; i < BUFF_SIZE; i++) {
    sum += buff[i];
  }
  return sum / BUFF_SIZE;
}
  
void loop() {
  int x = analogRead(x_pin);
  int y = analogRead(y_pin);
  int xcoord = map(x, 80, 450, 0, 1024);
  int ycoord = map(y, 30, 460, 0, 1024);
  
  // Move the cursor if the touchpad is continuously being touched
  // in "different enough" (see delta() and TOLERANCE) places
  if (x < RESTING && y < RESTING) {
    if (touch_in_progress) {
      xbuff[xp++ % BUFF_SIZE] = delta(last_x, xcoord);
      ybuff[yp++ % BUFF_SIZE] = delta(last_y, ycoord);
      //Mouse.move(delta(last_x, x), delta(last_y, y), 0);
      Mouse.move(ave(xbuff), ave(ybuff), 0);
    } else {
      touch_in_progress = true;
    }
    
    last_x = xcoord;
    last_y = ycoord;
  } else if (touch_in_progress) {
    for (int i = 0; i < BUFF_SIZE; i++) {
      xbuff[i] = 0;
      ybuff[i] = 0;
    }
    touch_in_progress = false;
  }
  
  delay(DELAY_TIME);
}

/* delay
 *
 * return a distance for the mouse to move unless we just read noise
 */
int delta(int last, int curr) {
  if (abs(curr - last) > 10 && abs(curr - last) < 25) {
    return 2 * (curr - last);
    //if (curr - last > 0)
    //  return 2 * min(curr - last, 20);
    //return 2 * max(curr - last, -20);
  }
  return 0;
}
