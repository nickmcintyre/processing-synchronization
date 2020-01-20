/**
 * Firefly Example. Based on "Flocking" by Daniel Shiffman. CC BY-NC-SA 4.0
 * 
 * Simulate the swarming behavior and bioluminescence of fireflies.
 * Pairs well with Owl City https://youtu.be/zlxPp0vAniY
 */
import sync.*;

PNetwork net;
Swarm swarm;

void setup() {
  size(640, 360);
  int networkSize = 100;
  float[] phase = new float[networkSize];
  float[] naturalFrequency = new float[networkSize];
  float[][] coupling = new float[networkSize][networkSize];
  for (int i = 0; i < networkSize; i++) {
    phase[i] = random(TWO_PI);
    naturalFrequency[i] = random(PI);
  }
  net = new PNetwork(this, phase, naturalFrequency, coupling);
  swarm = new Swarm(net);
  for (int i = 0; i < networkSize; i++) {
    swarm.addFly(new Firefly(width / 2, height / 2));
  }
}

void draw() {
  background(0, 5, 20);
  swarm.run();
}

// Vary the range of the swarm's natural frequencies
void mousePressed() {
  float lo = random(TWO_PI);
  float hi = lo + random(TWO_PI - lo);
  for (int i = 0; i < net.naturalFrequency.length; i++) {
    net.naturalFrequency[i] = random(lo, hi);
  }
}
