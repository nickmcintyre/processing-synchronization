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
