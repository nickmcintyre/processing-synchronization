/*
 * Brain Example.
 *
 * Simulate the synchronization of neurons in the brain by varying their connective
 * arrangement.
 *
 * https://researchspace.auckland.ac.nz/bitstream/handle/2292/2666/esc-tr-638-1.pdf
 */
import sync.*;

PNetwork net;
Arrangement arr;

void setup() {
  size(640, 360);
  int networkSize = 8;
  float coupling = 5;
  arr = new Arrangement(networkSize, coupling);
  float[] phase = new float[networkSize];
  float[] naturalFrequency = new float[networkSize];
  for (int i = 0; i < networkSize; i++) {
    phase[i] = random(TWO_PI);
    naturalFrequency[i] = random(1);
  }
  
  net = new PNetwork(this, phase, naturalFrequency, arr.allToAll);
}

void draw() {
  background(255);
  noStroke();
  translate(width/2, height/2);
  for (int i = 0; i < net.networkSize; i++) {
    float r = i * (TWO_PI / net.networkSize);
    float a = map(net.phase[i], 0, TWO_PI, 0, 255);
    pushMatrix();
    rotate(r);
    translate(100, 0);
    fill(54, 86, 148, a);
    circle(0, 0, 50);
    popMatrix();
  }
  net.step();
}

void keyPressed() {
  switch (key) {
    case '0':
      net.coupling = arr.linearUnidirectional;
      break;
    case '1':
      net.coupling = arr.linearBidirectional;
      break;
    case '2':
      net.coupling = arr.boxUnidirectional;
      break;
    case '3':
      net.coupling = arr.boxBidirectional;
      break;
    case '4':
      net.coupling = arr.allToAll;
      break;
  }
}
