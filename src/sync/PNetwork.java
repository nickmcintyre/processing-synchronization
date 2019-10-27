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
 * @example MotionExample
 * @example PhaseExample
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
	 * @param parent The parent PApplet object
	 * @param networkSize The number of oscillators in the network
	 * @param coupling The global level of coupling in the network
	 * @param noiseLevel The level of noise (0-1)
	 * @param stepSize The size of the time step
	 */
	public PNetwork(PApplet parent, int networkSize, float coupling, float noiseLevel, float stepSize) {
		this.parent = parent;
		this.parent.registerMethod("dispose", this);
		time = 0.0f;
		this.stepSize = PApplet.constrain(stepSize, 0.001f, 1.0f);
		this.noiseLevel = PApplet.constrain(noiseLevel, 0.0f, 1.0f);
		this.networkSize = networkSize;
	    initializeCoupling(coupling);
		initializeFrequency();
		initializePhase();
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param parent The parent PApplet object
	 * @param networkSize The number of oscillators in the network
	 * @param coupling The global level of coupling in the network
	 * @param noiseLevel The level of noise (0-1)
	 */
	public PNetwork(PApplet parent, int networkSize, float coupling, float noiseLevel) {
		this(parent, networkSize, coupling, noiseLevel, 0.05f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 *
	 * @param parent The parent PApplet object
	 * @param networkSize The number of oscillators in the network
	 * @param coupling The global level of coupling in the network
	 */
	public PNetwork(PApplet parent, int networkSize, float coupling) {
		this(parent, networkSize, coupling, 0.0f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param parent
	 * @param phase
	 * @param naturalFrequency
	 * @param coupling
	 * @param noiseLevel
	 * @param stepSize
	 */
	public PNetwork(PApplet parent, float[] phase, float[] naturalFrequency, float[][] coupling, float noiseLevel, float stepSize) {
		this.parent = parent;
		parent.registerMethod("dispose", this);
		time = 0.0f;
		this.naturalFrequency = naturalFrequency;
		networkSize = naturalFrequency.length;
		this.coupling = coupling;
		this.stepSize = PApplet.constrain(stepSize, 0.001f, 1.0f);
		this.noiseLevel = PApplet.constrain(noiseLevel, 0.0f, 1.0f);
		this.phase = phase;
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param parent
	 * @param phase
	 * @param naturalFrequency
	 * @param coupling
	 * @param noiseLevel
	 */
	public PNetwork(PApplet parent, float[] phase, float[] naturalFrequency, float[][] coupling, float noiseLevel) {
		this(parent, phase, naturalFrequency, coupling, noiseLevel, 0.05f);
	}
	
	/**
	 * Initialize the PNetwork object.
	 * 
	 * @param parent
	 * @param phase
	 * @param naturalFrequency
	 * @param coupling
	 */
	public PNetwork(PApplet parent, float[] phase, float[] naturalFrequency, float[][] coupling) {
		this(parent, phase, naturalFrequency, coupling, 0.0f);
	}
	
	/**
	 * Initialize the coupling matrix for the case of uniform, global coupling.
	 * 
	 * @param coupling
	 */
	private void initializeCoupling(float coupling) {
	    this.coupling = new float[networkSize][networkSize];
	    for (int i = 0; i < networkSize; i++) {
		    Arrays.fill(this.coupling[i], coupling);
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
		float noise = noiseLevel * parent.noise(time);
		float[] oldPhase = Arrays.copyOf(phase, networkSize);
		float[] oldVelocity = Arrays.copyOf(velocity, networkSize);
		for (int i = 0; i < networkSize; i++) {
			// Calculate increments
			float k1 = stepSize * differentiate(0.0f, i, noise);
			float k2 = stepSize * differentiate(k1/2, i, noise);
			float k3 = stepSize * differentiate(k2/2, i, noise);
			float k4 = stepSize * differentiate(k3, i, noise);
			// Update phase
			phase[i] = (phase[i] + (k1 + 2*k2 + 2*k3 + k4)/6) % TWO_PI;
			// Update velocity
			velocity[i] = (phase[i] - oldPhase[i]) / stepSize;
			// Update acceleration
			acceleration[i] = (velocity[i] - oldVelocity[i]) / stepSize;
		}
		
		time += stepSize;
	}
	
	/**
	 * Calculate the time derivative of an oscillator's phase using the Kuramoto model.
	 * 
	 * https://en.wikipedia.org/wiki/Kuramoto_model
	 * 
	 * @param increment
	 * @param oscIndex
	 * @param noise
	 * 
	 * @return {@code float} time derivative
	 */
	private float differentiate(float increment, int oscIndex, float noise) {
		float derivative = naturalFrequency[oscIndex] + noise;
		for (int j = 0; j < networkSize; j++) {
			derivative += coupling[oscIndex][j] * PApplet.sin(phase[j] - increment);
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
