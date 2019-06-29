/*
 * Phase Example.
 *
 * Initialize a small network of coupled, oscillating squares and watch them
 * synchronize.
 */
import sync.*;

PNetwork net;
float side;
float displacement;

void setup() {
  size(640, 360);
  int networkSize = 20;
  float coupling = 3;
  float stepSize = 0.05;
  net = new PNetwork(this, networkSize, coupling, stepSize);
  side = height/networkSize;
  displacement = width/4;
  frameRate(12);
}

void draw() {
  background(220);
  translate(width/2, 0);
  
  // Draw a square corresponding to the phase of each oscillator
  fill(75, 190, 70);
  stroke(75, 190, 70);
  for (int i = 0; i < net.networkSize; i++) {
    pushMatrix();
    float x = displacement*cos(net.phase[i]);
    translate(x, i*side);
    rect(0, 0, side, side);
    popMatrix();
  }
  
  net.step();
}
