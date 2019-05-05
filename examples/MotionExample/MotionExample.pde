/*
 * Motion Example.
 *
 * Initialize a small network of coupled oscillators and watch them synchronize
 * around a circle. Color-code based on angular velocity and acceleration. Point
 * to the average phase (angle) of the network, varying length based on the
 * order (cohesion).
 * 
 * Visualization idea borrowed from the Wikipedia article for the Kuramoto model.
 * https://en.wikipedia.org/wiki/Kuramoto_model
 */
import sync.*;

PNetwork net;
float radius;

void setup() {
  size(640, 360);
  int networkSize = 5;
  float coupling = 2;
  float stepSize = 0.05;
  net = new PNetwork(this, networkSize, coupling, stepSize);
  radius = 75;
  frameRate(12);
}

void draw() {
  background(220);
  translate(width/2, height/2);
  
  // Draw a track
  stroke(100);
  noFill();
  ellipse(0, 0, 2*radius, 2*radius);
  
  // Draw a circle corresponding to the phase of each oscillator
  fill(25, 165, 255);
  stroke(25, 165, 255);
  for (int i = 0; i < net.networkSize; i++) {
    pushMatrix();
    float x = radius*cos(net.phase[i]);
    float y = radius*sin(net.phase[i]);
    translate(x, y);
    // Fill circles based on velocity
    if (net.velocity[i] > 0) {
      fill(0, 0, 255, 50);
    } else {
      fill(255, 0, 0, 50);
    }
    // Outline circles based on acceleration
    if (net.acceleration[i] > 0) {
      stroke(0, 0, 255);
    } else {
      stroke(255, 0, 0);
    }
    
    ellipse(0, 0, 10, 10);
    popMatrix();
  }
  
  // Draw a line pointing to the average phase of the network
  pushMatrix();
  scale(net.orderParameter);
  float x = radius*cos(net.averagePhase);
  float y = radius*sin(net.averagePhase);
  stroke(100);
  line(0, 0, x, y);
  popMatrix();

  net.step();
}
