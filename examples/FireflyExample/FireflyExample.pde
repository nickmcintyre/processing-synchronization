/*
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
  int networkSize = 150;
  float coupling = 2.5;
  float stepSize = 0.05;
  float noiseLevel = 0.5;
  net = new PNetwork(this, networkSize, coupling, stepSize, noiseLevel);
  swarm = new Swarm(net);
  for (int i = 0; i < networkSize; i++) {
    swarm.addFly(new Firefly(width/2, height/2));
  }
}

void draw() {
  background(0, 5, 20);
  swarm.run();
}
