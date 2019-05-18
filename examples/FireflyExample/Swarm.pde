// The Swarm (a list of Firefly objects)

class Swarm {
  ArrayList<Firefly> flies; // An ArrayList for all the fireflies
  PNetwork net; // A PNetwork of coupled oscillators for managing brightness

  Swarm(PNetwork _net) {
    flies = new ArrayList<Firefly>(); // Initialize the ArrayList
    net = _net;
  }

  void run() {
    for (int i = 0; i < flies.size(); i++) {
      flies.get(i).run(flies, net.phase[i]);  // Passing the entire list of fireflies to each firefly individually
    }
    net.step();
  }

  void addFly(Firefly f) {
    flies.add(f);
  }
}
