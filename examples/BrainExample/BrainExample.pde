/**
 * Brain Example.
 *
 * Simulate the synchronization of neurons in the brain by varying their connective
 * arrangement.
 *
 * https://researchspace.auckland.ac.nz/bitstream/handle/2292/2666/esc-tr-638-1.pdf
 */
import sync.*;

PNetwork net;
String arrangement = "All-to-All";;
float coupling = 25;

void setup() {
  size(640, 360);
  int networkSize = 25;
  net = new PNetwork(this, networkSize, coupling);
  noStroke();
}

void draw() {
  background(255);
  fill(54, 86, 148);
  text(arrangement, 10, 20);
  translate(width/2, height/2);
  for (int i = 0; i < net.size(); i++) {
    float r = i * (TWO_PI / net.size());
    float a = map(net.phase[i], 0, TWO_PI, 0, 255);
    pushMatrix();
    rotate(r);
    translate(100, 0);
    fill(54, 86, 148, a);
    circle(0, 0, 25);
    popMatrix();
  }
  net.step();
}

void keyPressed() {
  switch (key) {
    case '1':
      arrangement = "Linear Unidirectional";
      net.setCoupling("LU", coupling);
      break;
    case '2':
      arrangement = "Linear Bidirectional";
      net.setCoupling("LB", coupling);
      break;
    case '3':
      arrangement = "Box Unidirectional";
      net.setCoupling("BU", coupling);
      break;
    case '4':
      arrangement = "Box Bidirectional";
      net.setCoupling("BB", coupling);
      break;
    case '5':
      arrangement = "All-to-All";
      net.setCoupling("A2A", coupling);
      break;
  }
}
