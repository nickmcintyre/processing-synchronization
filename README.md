# processing-synchronization
**Simulate oscillator synchronization with Processing**

- Implements the [Kuramoto model](https://en.wikipedia.org/wiki/Kuramoto_model) for synchronization.
- Supports oscillator networks with arbitrary sizes, levels of coupling, and time steps.
- Includes Perlin noise.
- Still early days. Things will break.

## Example
The following example initializes a small network of coupled oscillators and tracks their synchronization. Visualization borrowed from the Wikipedia article.

```java
import sync.*;

PNetwork net;
float radius;

void setup() {
  size(640, 360);
  int networkSize = 10;
  float coupling = 3.5;
  float stepSize = 0.05;
  net = new PNetwork(this, networkSize, coupling, stepSize);
  radius = 75;
  frameRate(12);
}

void draw() {
  background(220);
  translate(width/2, height/2);

  // Draw an elliptical track
  stroke(100);
  noFill();
  ellipse(0, 0, 2*radius, 2*radius);

  // Draw an ellipse corresponding to the phase of each oscillator
  fill(25, 165, 255);
  stroke(25, 165, 255);
  for (int i = 0; i < net.networkSize; i++) {
    pushMatrix();
    float x = radius*cos(net.phase[i]);
    float y = radius*sin(net.phase[i]);
    translate(x, y);
    ellipse(0, 0, 10, 10);
    popMatrix();
  }

  // Draw a line pointing to the average phase of the network
  pushMatrix();
  rotate(-net.averagePhase);
  stroke(100);
  line(0, 0, radius*net.orderParameter, 0);
  popMatrix();

  net.step();
}
```
