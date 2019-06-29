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
