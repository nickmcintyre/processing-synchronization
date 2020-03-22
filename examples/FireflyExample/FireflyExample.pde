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
  float coupling = 10;
  net = new PNetwork(this, networkSize, coupling);
  swarm = new Swarm(net);
  for (int i = 0; i < net.size(); i++) {
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
  for (int i = 0; i < net.size(); i++) {
    net.naturalFrequency[i] = random(lo, hi);
  }
}
