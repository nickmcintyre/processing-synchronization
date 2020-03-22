# processing-synchronization
**Simulate oscillator synchronization with Processing**

- Implements the [Kuramoto model](https://en.wikipedia.org/wiki/Kuramoto_model) for synchronization.
- Supports oscillator networks of arbitrary size, coupling, connective arrangement, and noise level.

## Example
The following example simulates the swarming behavior and bioluminescence of fireflies. Pairs well with [Owl City](https://youtu.be/zlxPp0vAniY).

```java
// Based on "Flocking" by Daniel Shiffman. CC BY-NC-SA 4.0
import sync.*;

PNetwork net;
Swarm swarm;

void setup() {
  size(640, 360);
  int networkSize = 100;
  float coupling = 10;
  net = new PNetwork(this, networkSize, coupling);
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
  for (int i = 0; i < net.size(); i++) {
    net.naturalFrequency[i] = random(lo, hi);
  }
}

// The Firefly class

class Firefly {

  PVector position;
  PVector velocity;
  PVector acceleration;
  float d;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed

  Firefly(float x, float y) {
    acceleration = new PVector(0, 0);
    velocity = PVector.random2D();
    position = new PVector(x, y);
    d = 10;
    maxspeed = 2;
    maxforce = 0.03;
  }

  void run(float phase) {
    update(phase);
    borders();
    render(phase);
  }

  void applyForce(PVector force) {
    // We could add mass here if we want A = F / M
    acceleration.add(force);
  }

  // Method to update position
  void update(float phase) {
    // Update velocity
    velocity.set(cos(phase), sin(phase));
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    position.add(velocity);
    // Reset accelertion to 0 each cycle
    acceleration.mult(0);
  }

  void render(float phase) {
    float alpha = 175 * round(map(phase, 0, TWO_PI, 0, 1));
    fill(225, 240, 45, alpha);
    stroke(225, 240, 45, alpha);
    circle(position.x, position.y, d);
  }

  // Wraparound
  void borders() {
    float r = d / 2;
    if (position.x < -r) position.x = width + r;
    if (position.y < -r) position.y = height + r;
    if (position.x > width + r) position.x = -r;
    if (position.y > height + r) position.y = -r;
  }
}

// The Swarm (a list of Firefly objects)

class Swarm {
  ArrayList<Firefly> flies; // An ArrayList for all the fireflies
  PNetwork net; // A PNetwork of coupled oscillators
  float maxdist = sqrt(pow(width, 2) + pow(height, 2));
  float maxcouple = 10;

  Swarm(PNetwork net_) {
    flies = new ArrayList<Firefly>(); // Initialize the ArrayList
    net = net_;
  }

  void run() {
    // Update coupling based on proximity
    for (int i = 0; i < flies.size(); i++) {
      Firefly a = flies.get(i);
      for (int j = i; j < flies.size(); j++) {
        Firefly b = flies.get(j);
        float coupling = maxcouple - map(a.position.dist(b.position), 0, maxdist, 0, maxcouple);
        net.coupling[i][j] = coupling;
        net.coupling[j][i] = coupling;
      }
      a.run(net.phase[i]);  // Passing each firefly its updated phase
    }
    net.step();
  }

  void addFly(Firefly f) {
    flies.add(f);
  }
}
```

