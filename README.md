# processing-synchronization
**Simulate oscillator synchronization with Processing**

- Implements the [Kuramoto model](https://en.wikipedia.org/wiki/Kuramoto_model) for synchronization.
- Supports oscillator networks of arbitrary size, coupling, connective arrangement, and noise level.
- Still early days. Things will break.

## Example
The following example simulates the synchronization of neurons in our brains.

```java
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

class Arrangement {
  
  int networkSize;
  float coupling;
  
  float[][] linearUnidirectional;
  float[][] linearBidirectional;
  float[][] boxUnidirectional;
  float[][] boxBidirectional;
  float[][] allToAll;
  
  Arrangement(int _networkSize, float _coupling) {
    networkSize = _networkSize;
    coupling = _coupling;
    initializeMatrices();
  }
  
  void initializeMatrices() {
    initializeLU();
    initializeLB();
    initializeBU();
    initializeBB();
    initializeA2A();
  }
  
  /**
   * Linear Unidirectional
   * 
   * {{0, X, 0, 0},
   *  {0, 0, X, 0},
   *  {0, 0, 0, X},
   *  {0, 0, 0, 0}}
   */
  void initializeLU() {
    linearUnidirectional = new float[networkSize][networkSize];
    for (int i = 0; i < networkSize; i++) {
      for (int j = 0; j < networkSize; j++) {
        if (j == i + 1) {
          linearUnidirectional[i][j] = coupling;
        } else {
          linearUnidirectional[i][j] = 0;
        }
      }
    }
  }
  
  /**
   * Linear Bidirectional
   * 
   * {{0, X, 0, 0},
   *  {X, 0, X, 0},
   *  {0, X, 0, X},
   *  {0, 0, X, 0}}
   */
  void initializeLB() {
    linearBidirectional = new float[networkSize][networkSize];
    for (int i = 0; i < networkSize; i++) {
      for (int j = 0; j < networkSize; j++) {
        if (j == i + 1) {
          linearBidirectional[i][j] = coupling;
        } else if (i == j + 1) {
          linearBidirectional[i][j] = coupling;
        } else {
          linearBidirectional[i][j] = 0;
        }
      }
    }
  }
  
  /**
   * Box Unidirectional
   * 
   * {{0, X, 0, 0},
   *  {0, 0, X, 0},
   *  {0, 0, 0, X},
   *  {X, 0, 0, 0}}
   */
  void initializeBU() {
    boxUnidirectional = new float[networkSize][networkSize];
    for (int i = 0; i < networkSize; i++) {
      for (int j = 0; j < networkSize; j++) {
        if (j == i + 1) {
          boxUnidirectional[i][j] = coupling;
        } else if (j == 0 && i == networkSize - 1) {
          boxUnidirectional[i][j] = coupling;
        } else {
          boxUnidirectional[i][j] = 0;
        }
      }
    }
  }
  
  /**
   * Box Bidirectional
   * 
   * {{0, X, 0, X},
   *  {X, 0, X, 0},
   *  {0, X, 0, X},
   *  {X, 0, X, 0}}
   */
  void initializeBB() {
    boxBidirectional = new float[networkSize][networkSize];
    for (int i = 0; i < networkSize; i++) {
      for (int j = 0; j < networkSize; j++) {
        if (j == i + 1) {
          boxBidirectional[i][j] = coupling;
        } else if (i == j + 1) {
          boxBidirectional[i][j] = coupling;
        } else if (j == 0 && i == networkSize - 1) {
          boxBidirectional[i][j] = coupling;
        } else if (i == 0 && j == networkSize - 1) {
          boxBidirectional[i][j] = coupling;
        } else {
          boxBidirectional[i][j] = 0;
        }
      }
    }
  }
  
  /**
   * All-to-all
   * 
   * {{0, X, X, X},
   *  {X, 0, X, X},
   *  {X, X, 0, X},
   *  {X, X, X, 0}}
   */
  void initializeA2A() {
    allToAll = new float[networkSize][networkSize];
    for (int i = 0; i < networkSize; i++) {
      for (int j = 0; j < networkSize; j++) {
        if (i == j) {
          allToAll[i][j] = 0;
        } else {
          allToAll[i][j] = coupling;
        }
      }
    }
  }
}
```