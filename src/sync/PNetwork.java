package sync;

import java.util.Arrays;

import processing.core.*;

/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 *
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 *
 * @example BrainExample
 * @example FireflyExample
 * @example OrderExample
 * @example PhaseExample
 */
public class PNetwork implements PConstants {
	
	private int networkSize;

	public PApplet parent;
	
	public float time;
	public float stepSize;
	public float[][] coupling;
	public float noiseLevel;
	
	public float[] naturalFrequency;
	public float[] phase;
	public float[] velocity;
	public float[] acceleration;
	public float[] oldPhase;
	
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param parent     The parent PApplet object.
	 * @param size       The number of oscillators in the network.
	 * @param coupling   The global level of coupling in the network.
	 * @param noiseLevel The level of noise.
	 * @param stepSize   The size of the time step.
	 */
	public PNetwork(PApplet parent, int size, float coupling, float noiseLevel, float stepSize) {
		this.parent = parent;
		this.parent.registerMethod("dispose", this);
		time = 0.0f;
		this.stepSize = stepSize;
		this.noiseLevel = noiseLevel;
		this.networkSize = size;
		this.oldPhase = new float[size];
	    initializeCoupling(coupling);
		initializeFrequency();
		initializePhase();
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param parent     The parent PApplet object.
	 * @param size       The number of oscillators in the network.
	 * @param coupling   The global level of coupling in the network.
	 * @param noiseLevel The level of noise.
	 */
	public PNetwork(PApplet parent, int size, float coupling, float noiseLevel) {
		this(parent, size, coupling, noiseLevel, 0.05f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param parent   The parent PApplet object.
	 * @param size     The number of oscillators in the network.
	 * @param coupling The global level of coupling in the network.
	 */
	public PNetwork(PApplet parent, int size, float coupling) {
		this(parent, size, coupling, 0.0f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param parent           The parent PApplet object.
	 * @param phase            The phase of each oscillator.
	 * @param naturalFrequency The natural frequency of each oscillator.
	 * @param coupling         The level of coupling between each pair of oscillators.
	 * @param noiseLevel       The level of noise.
	 * @param stepSize         The size of the time step.
	 */
	public PNetwork(PApplet parent, float[] phase, float[] naturalFrequency, float[][] coupling, float noiseLevel, float stepSize) {
		this.parent = parent;
		parent.registerMethod("dispose", this);
		time = 0.0f;
		this.naturalFrequency = naturalFrequency;
		networkSize = naturalFrequency.length;
		this.coupling = coupling;
		this.stepSize = stepSize;
		this.noiseLevel = noiseLevel;
		this.phase = phase;
		this.oldPhase = new float[networkSize];
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param parent           The parent PApplet object.
	 * @param phase            The phase of each oscillator.
	 * @param naturalFrequency The natural frequency of each oscillator.
	 * @param coupling         The level of coupling between each pair of oscillators.
	 * @param noiseLevel       The level of noise.
	 */
	public PNetwork(PApplet parent, float[] phase, float[] naturalFrequency, float[][] coupling, float noiseLevel) {
		this(parent, phase, naturalFrequency, coupling, noiseLevel, 0.05f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param parent           The parent PApplet object.
	 * @param phase            The phase of each oscillator.
	 * @param naturalFrequency The natural frequency of each oscillator.
	 * @param coupling         The level of coupling between each pair of oscillators.
	 */
	public PNetwork(PApplet parent, float[] phase, float[] naturalFrequency, float[][] coupling) {
		this(parent, phase, naturalFrequency, coupling, 0.0f);
	}
	
	/**
	 * Initialize the coupling matrix for the case of uniform, global coupling.
	 * 
	 * @param coupling The level of coupling between all oscillators.
	 */
	private void initializeCoupling(float coupling) {
	    this.coupling = new float[networkSize][networkSize];
	    setCoupling(coupling);
	}
	
	/**
	 * Set natural frequencies of oscillators using Perlin noise.
	 */
	private void initializeFrequency() {
		naturalFrequency = new float[networkSize];
		float t = 0.0f;
		float dt = 0.1f;
		for (int i = 0; i < networkSize; i++) {
			naturalFrequency[i] = TWO_PI * parent.noise(t);
			t += dt;
		}
	}
	
	/**
	 * Set initial phases of oscillators using Perlin noise.
	 */
	private void initializePhase() {
		phase = new float[networkSize];
		float t = 0.0f;
		float dt = 0.1f;
		for (int i = 0; i < networkSize; i++) {
			phase[i] = TWO_PI * parent.noise(t);
			t += dt;
		}
	}
	
	/**
	 * Set initial angular velocity of oscillators to 0.
	 */
	private void initializeVelocity() {
	    velocity = new float[networkSize];
		Arrays.fill(velocity, 0);
	}
	
	/**
	 * Set initial angular acceleration of oscillators to 0.
	 */
	private void initializeAcceleration() {
	    acceleration = new float[networkSize];
		Arrays.fill(acceleration, 0);
	}
	
	/**
	 * Return the number of oscillators in the network.
	 * 
	 * @return The size of the network.
	 */
	public int size() {
		return networkSize;
	}

	/**
	 * Shift the natural frequency of each oscillator to that of a
	 * neighbor's.
	 * 
	 * @param n The number of indices to shift.
	 */
	public void shiftFrequencies(int n) {
		shiftArray(naturalFrequency, n);
	}
	
	/**
	 * Shift the phase of each oscillator to that of a neighbor's.
	 * 
	 * @param n The number of indices to shift.
	 */
	public void shiftPhases(int n) {
		shiftArray(phase, n);
		shiftArray(oldPhase, n);
	}
	
	/**
	 * Shift the elements of an array to the left or right.
	 * 
	 * @param a The array to be shifted.
	 * @param n The number of indices to shift.
	 */
	private void shiftArray(float[] a, int n) {
		float[] b = Arrays.copyOf(a,  a.length);
		if (n > 0) { // shift right
			System.arraycopy(b, 0, a, n, a.length - n);
			System.arraycopy(b, a.length - n, a, 0, n);
		} else { // shift left
			System.arraycopy(b, -n, a, 0, a.length + n);
			System.arraycopy(b, 0, a, a.length + n, -n);
		}
	}
	
	/**
	 * Set the coupling strength within the network.
	 * 
	 * @param coupling The level of coupling.
	 */
	public void setCoupling(float coupling) {
		setCoupling("A2A", coupling);
	}
	
	/**
	 * Set the connective arrangement and coupling strength within the network.
	 * 
	 * @param arrangement The connective arrangement of the network.
	 * @param coupling    The level of coupling.
	 */
	public void setCoupling(String arrangement, float coupling) {
		if (arrangement.equals("A2A")) {
			/**
			 * All-to-all
			 * 
			 * {{0, X, X, X},
			 *  {X, 0, X, X},
			 *  {X, X, 0, X},
			 *  {X, X, X, 0}}
			 */
			for (int i = 0; i < networkSize; i++) {
				for (int j = 0; j < networkSize; j++) {
					this.coupling[i][j] = coupling;
				}
			}
		} else if (arrangement.equals("LU")) {
			/**
			 * Linear Unidirectional
			 * 
			 * {{0, X, 0, 0},
			 *  {0, 0, X, 0},
			 *  {0, 0, 0, X},
			 *  {0, 0, 0, 0}}
			 */
			for (int i = 0; i < networkSize; i++) {
				for (int j = 0; j < networkSize; j++) {
			        if (j == i + 1) {
			        	this.coupling[i][j] = coupling;
			        } else {
			        	this.coupling[i][j] = 0;
			        }
			    }
			}
		} else if (arrangement.equals("LB")) {
			/**
			 * Linear Bidirectional
			 * 
			 * {{0, X, 0, 0},
			 *  {X, 0, X, 0},
			 *  {0, X, 0, X},
			 *  {0, 0, X, 0}}
			 */
			for (int i = 0; i < networkSize; i++) {
				for (int j = 0; j < networkSize; j++) {
					if (j == i + 1) {
						this.coupling[i][j] = coupling;
					} else if (i == j + 1) {
						this.coupling[i][j] = coupling;
					} else {
						this.coupling[i][j] = 0;
			        }
			    }
			}
		} else if (arrangement.equals("BU")) {
			/**
			 * Box Unidirectional
			 * 
			 * {{0, X, 0, 0},
			 *  {0, 0, X, 0},
			 *  {0, 0, 0, X},
			 *  {X, 0, 0, 0}}
			 */
			for (int i = 0; i < networkSize; i++) {
				for (int j = 0; j < networkSize; j++) {
					if (j == i + 1) {
			            this.coupling[i][j] = coupling;
			        } else if (j == 0 && i == networkSize - 1) {
			            this.coupling[i][j] = coupling;
			        } else {
			            this.coupling[i][j] = 0;
			        }
			    }
			}
		} else if (arrangement.equals("BB")) {
			/**
			 * Box Bidirectional
			 * 
			 * {{0, X, 0, X},
			 *  {X, 0, X, 0},
			 *  {0, X, 0, X},
			 *  {X, 0, X, 0}}
			 */
			for (int i = 0; i < networkSize; i++) {
				for (int j = 0; j < networkSize; j++) {
					if (j == i + 1) {
			            this.coupling[i][j] = coupling;
			        } else if (i == j + 1) {
			            this.coupling[i][j] = coupling;
			        } else if (j == 0 && i == networkSize - 1) {
			            this.coupling[i][j] = coupling;
			        } else if (i == 0 && j == networkSize - 1) {
			            this.coupling[i][j] = coupling;
			        } else {
			            this.coupling[i][j] = 0;
			        }
			    }
			}
		}
	}
	
	/**
	 * Advance the simulation forward one time step.
	 */
	public void step() {
		step(1);
	}
	
	/**
	 * Advance the simulation forward a given number of time steps.
	 * 
	 * @param numSteps The number of time steps to take.
	 */
	public void step(int numSteps) {
		for (int i = 0; i < numSteps; i++) {
			solveRK4();
		}
	}
	
	/**
	 * Calculate the next phase of the oscillator network by solving the governing
	 * equation with the classical Runge-Kutta method.
	 * 
	 * https://en.wikipedia.org/wiki/Runge-Kutta_methods
	 */
	private void solveRK4() {
		float noise = noiseLevel * parent.noise(time);
		oldPhase = Arrays.copyOf(phase, networkSize);
		float[] oldVelocity = Arrays.copyOf(velocity, networkSize);
		for (int i = 0; i < networkSize; i++) {
			// Calculate increments
			float k1 = stepSize * differentiate(0.0f, i, noise);
			float k2 = stepSize * differentiate(k1/2, i, noise);
			float k3 = stepSize * differentiate(k2/2, i, noise);
			float k4 = stepSize * differentiate(k3, i, noise);
			// Update phase
			phase[i] = (phase[i] + (k1 + 2*k2 + 2*k3 + k4)/6);
			// Update velocity
			velocity[i] = (phase[i] - oldPhase[i]) / stepSize;
			// Update acceleration
			acceleration[i] = (velocity[i] - oldVelocity[i]) / stepSize;
			// Keep phase within TWO_PI
			 phase[i] %= TWO_PI;
		}
		
		time += stepSize;
	}
	
	/**
	 * Calculate the time derivative of an oscillator's phase using the Kuramoto model.
	 * 
	 * https://en.wikipedia.org/wiki/Kuramoto_model
	 * 
	 * @param increment The last increment calculated.
	 * @param oscIndex  The index of the oscillator being updated.
	 * @param noise     The amount of noise.
	 * 
	 * @return {@code float} time derivative
	 */
	private float differentiate(float increment, int oscIndex, float noise) {
		float derivative = naturalFrequency[oscIndex] + noise;
		for (int j = 0; j < networkSize; j++) {
			derivative += (coupling[oscIndex][j] / networkSize) * PApplet.sin(oldPhase[j] - increment);
		}

		return derivative;
	}
	
	/**
	 * Calculate the complex "order" of the network.
	 * 
	 * @return {@code PVector} x-real y-imaginary
	 */
	public PVector getOrderVector() {
		PVector orderVector = new PVector();
		for (int i = 0; i < networkSize; i++) {
			PVector phaseVector = new PVector(PApplet.cos(phase[i]), PApplet.sin(phase[i]));
			orderVector.add(phaseVector);
		}
		
		orderVector.div(networkSize);
		
		return orderVector;
	}
	
	/**
	 * Calculate the order parameter of the network (0-1).
	 * 
	 * @return {@code float} order parameter
	 */
	public float getOrderParameter() {
		PVector orderVector = getOrderVector();
		
		return orderVector.mag();
	}
	
	/**
	 * Dispose of the PNetwork object.
	 */
	public void dispose() {

	}

}
