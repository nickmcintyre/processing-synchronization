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
