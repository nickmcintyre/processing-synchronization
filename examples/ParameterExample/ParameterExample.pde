/*
 * Parameter Example.
 *
 * Initialize a small network of coupled oscillators and vary the natural frequency of
 * each oscillator based on user input.
 * 
 * Visualization idea borrowed from the Wikipedia article for the Kuramoto model.
 * https://en.wikipedia.org/wiki/Kuramoto_model
 */
import sync.*;

PNetwork net;
float radius;
int t;
int dt;

void setup() {
  size(640, 360);
  int networkSize = 5;
  float coupling = 1.5;
  float stepSize = 0.05;
  float noiseLevel = 0;
  net = new PNetwork(this, networkSize, coupling, stepSize, noiseLevel);
  radius = 75;
  t = 0;
  dt = 1;
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

void keyPressed() {
  switch (key) {
    case '0':
      net.naturalFrequency[0] = TWO_PI*noise(t);
      net.naturalFrequency[1] = PI;
      net.naturalFrequency[2] = PI;
      net.naturalFrequency[3] = PI;
      net.naturalFrequency[4] = PI;
      t += dt;
      break;
    case '1':
      net.naturalFrequency[1] = TWO_PI*noise(t);
      net.naturalFrequency[0] = PI;
      net.naturalFrequency[2] = PI;
      net.naturalFrequency[3] = PI;
      net.naturalFrequency[4] = PI;
      t += dt;
      break;
    case '2':
      net.naturalFrequency[2] = TWO_PI*noise(t);
      net.naturalFrequency[0] = PI;
      net.naturalFrequency[1] = PI;
      net.naturalFrequency[3] = PI;
      net.naturalFrequency[4] = PI;
      t += dt;
      break;
    case '3':
      net.naturalFrequency[3] = TWO_PI*noise(t);
      net.naturalFrequency[0] = PI;
      net.naturalFrequency[1] = PI;
      net.naturalFrequency[2] = PI;
      net.naturalFrequency[4] = PI;
      t += dt;
      break;
    case '4':
      net.naturalFrequency[4] = TWO_PI*noise(t);
      net.naturalFrequency[0] = PI;
      net.naturalFrequency[1] = PI;
      net.naturalFrequency[2] = PI;
      net.naturalFrequency[3] = PI;
      t += dt;
      break;
    default:
      net.naturalFrequency[0] = TWO_PI*noise(t);
      t += dt;
      net.naturalFrequency[1] = TWO_PI*noise(t);
      t += dt;
      net.naturalFrequency[2] = TWO_PI*noise(t);
      t += dt;
      net.naturalFrequency[3] = TWO_PI*noise(t);
      t += dt;
      net.naturalFrequency[4] = TWO_PI*noise(t);
      t += dt;
      break;
  }
}
