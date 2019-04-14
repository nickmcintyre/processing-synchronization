/*
 * Circle Example.
 *
 * Initialize a small network of couple oscillators and watch them synchronize
 * around a circle. Point to the average phase (angle) of the network, varying
 * length based on the order (cohesion).
 * 
 * Visualization idea borrowed from the Wikipedia article for the Kuramoto model.
 * https://en.wikipedia.org/wiki/Kuramoto_model
 */
import sync.*;

PNetwork net;
int networkSize;
float coupling;
float stepSize;
float radius;

void setup() {
  size(400, 400);
  networkSize = 10;
  coupling = 3.5;
  stepSize = 0.05;
  net = new PNetwork(this, networkSize, coupling, stepSize);
  radius = 75;
  frameRate(12);
}

void draw() {
  background(220);
  translate(width/2, height/2);
  
  // Draw an ellipse corresponding to the phase of each oscillator
  fill(25, 165, 255);
  for (int i = 0; i < net.networkSize; i++) {
    pushMatrix();
    float x = radius * cos(net.phase[i]);
    float y = radius * sin(net.phase[i]);
    translate(x, y);
    ellipse(0, 0, 10, 10);
    popMatrix();
  }
  
  // Draw a line pointing to the average phase of the network
  pushMatrix();
  rotate(-net.averagePhase);
  line(0, 0, radius*net.orderParameter, 0);
  popMatrix();
  
  net.step();
}
