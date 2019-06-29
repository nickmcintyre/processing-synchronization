package sync;

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
 * @example MotionExample
 * @example PhaseExample
 * @example ParameterExample
 * @example FireflyExample
 */
public class PNetwork implements PConstants {

	public PApplet parent;
	
	public int networkSize;
	public float time;
	public float stepSize;
	public float[][] coupling;
	public float noiseLevel;
	
	public float[] naturalFrequency;
	public float[] phase;
	public float[] velocity;
	public float[] acceleration;
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param _parent The parent PApplet object
	 * @param _networkSize The number of oscillators in the network
	 * @param _coupling The global level of coupling in the network
	 * @param _noiseLevel The level of noise (0-1)
	 * @param _stepSize The size of the time step
	 */
	public PNetwork(PApplet _parent, int _networkSize, float _coupling, float _noiseLevel, float _stepSize) {
		parent = _parent;
		parent.registerMethod("dispose", this);
		time = 0.0f;
		stepSize = _stepSize;
		noiseLevel = PApplet.constrain(_noiseLevel, 0.0f, 1.0f);
		networkSize = _networkSize;
	    initializeCoupling(_coupling);
		initializeFrequency();
		initializePhase();
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param _parent The parent PApplet object
	 * @param _networkSize The number of oscillators in the network
	 * @param _coupling The global level of coupling in the network
	 * @param _noiseLevel The level of noise (0-1)
	 */
	public PNetwork(PApplet _parent, int _networkSize, float _coupling, float _noiseLevel) {
		this(_parent, _networkSize, _coupling, _noiseLevel, 0.05f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param _parent The parent PApplet object
	 * @param _networkSize The number of oscillators in the network
	 * @param _coupling The global level of coupling in the network
	 */
	public PNetwork(PApplet _parent, int _networkSize, float _coupling) {
		this(_parent, _networkSize, _coupling, 0.0f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param _parent
	 * @param _phase
	 * @param _naturalFrequency
	 * @param _coupling
	 * @param _noiseLevel
	 * @param _stepSize
	 */
	public PNetwork(PApplet _parent, float[] _phase, float[] _naturalFrequency, float[][] _coupling, float _noiseLevel, float _stepSize) {
		parent = _parent;
		parent.registerMethod("dispose", this);
		time = 0.0f;
		naturalFrequency = _naturalFrequency;
		networkSize = naturalFrequency.length;
		coupling = _coupling;
		noiseLevel = PApplet.constrain(_noiseLevel, 0.0f, 1.0f);
		stepSize = _stepSize;
		phase = _phase;
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param _parent
	 * @param _phase
	 * @param _naturalFrequency
	 * @param _coupling
	 * @param _noiseLevel
	 */
	public PNetwork(PApplet _parent, float[] _phase, float[] _naturalFrequency, float[][] _coupling, float _noiseLevel) {
		this(_parent, _phase, _naturalFrequency, _coupling, _noiseLevel, 0.05f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param _parent
	 * @param _phase
	 * @param _naturalFrequency
	 * @param _coupling
	 */
	public PNetwork(PApplet _parent, float[] _phase, float[] _naturalFrequency, float[][] _coupling) {
		this(_parent, _phase, _naturalFrequency, _coupling, 0.0f);
	}
	
	/**
	 * Initialize the coupling matrix for the case of uniform, global coupling.
	 * 
	 * @param _coupling
	 */
	private void initializeCoupling(float _coupling) {
	    coupling = new float[networkSize][networkSize];
		for (int i = 0; i < networkSize; i++) {
			for (int j = 0; j < networkSize; j++) {
				coupling[i][j] = _coupling;
			}
		}
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
		for (int i = 0; i < networkSize; i++) {
			velocity[i] = 0;
		}
	}
	
	/**
	 * Set initial angular acceleration of oscillators to 0.
	 */
	private void initializeAcceleration() {
	    acceleration = new float[networkSize];
		for (int i = 0; i < networkSize; i++) {
			acceleration[i] = 0;
		}
	}
	
	/**
	 * Calculate the angular velocity of each oscillator.
	 */
	private float calculateVelocity(float oldPhase, float newPhase) {
		return (newPhase - oldPhase) / stepSize;
	}
	
	/**
	 * Calculate the angular acceleration of each oscillator.
	 */
	private float calculateAcceleration(float oldVelocity, float newVelocity) {
		return (newVelocity - oldVelocity) / stepSize;
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
	 * @param numSteps The number of time steps to take
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
		float averagePhase = 0.0f;
		for (int i = 0; i < networkSize; i++) {
			averagePhase += phase[i];
		}
		
		averagePhase /= networkSize;
		float noise = noiseLevel * parent.noise(time);
		
		for (int i = 0; i < networkSize; i++) {
			// Calculate increments
			float k1 = stepSize * differentiate(0.0f, i, averagePhase, noise);
			float k2 = stepSize * differentiate(k1/2, i, averagePhase, noise);
			float k3 = stepSize * differentiate(k2/2, i, averagePhase, noise);
			float k4 = stepSize * differentiate(k3, i, averagePhase, noise);
			// Update phase
			float oldPhase = phase[i];
			float newPhase = (phase[i] + (k1 + 2*k2 + 2*k3 + k4)/6) % TWO_PI;
			phase[i] = newPhase;
			// Update velocity
			float oldVelocity = velocity[i];
			float newVelocity = calculateVelocity(oldPhase, newPhase);
			velocity[i] = newVelocity;
			// Update acceleration
			float newAcceleration = calculateAcceleration(oldVelocity, newVelocity);
			acceleration[i] = newAcceleration;
		}
		
		time += stepSize;
	}
	
	/**
	 * Calculate the time derivative of an oscillator's phase using the Kuramoto model.
	 * 
	 * https://en.wikipedia.org/wiki/Kuramoto_model
	 * 
	 * @param increment
	 * @param i
	 * @param averagePhase
	 * @param noise
	 * 
	 * @return {@code float} time derivative
	 */
	private float differentiate(float increment, int i, float averagePhase, float noise) {
		float derivative = naturalFrequency[i] + noise;
		for (int j = 0; j < networkSize; j++) {
			derivative += coupling[i][j] * PApplet.sin(averagePhase - phase[i] - increment) / networkSize;
		}


		return derivative;
	}
	
	/**
	 * Calculate the complex "order" of the network.
	 * 
	 * @return {@code PVector} x-real y-imaginary
	 */
	public PVector calculateOrder() {
		PVector orderVector = new PVector();
		for (int i = 0; i < networkSize; i++) {
			PVector phaseVector = new PVector(PApplet.cos(phase[i]), PApplet.sin(phase[i]));
			orderVector.add(phaseVector);
		}
		
		orderVector.div(networkSize);
		
		return orderVector;
	}
	
	/**
	 * Dispose of the PNetwork object.
	 */
	public void dispose() {

	}

}
