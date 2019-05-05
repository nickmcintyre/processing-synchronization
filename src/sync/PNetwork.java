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
 * @example MotionExample
 * @example PhaseExample
 */
public class PNetwork implements PConstants {

	public PApplet parent;
	
	public int networkSize;
	public float time;
	public float stepSize;
	public float coupling;
	public float orderParameter;
	public float averagePhase;
	
	public float[] naturalFrequency;
	public float[] phase;
	public float[] velocity;
	public float[] acceleration;

	/**
	 * Initialize the PNetwork object.
	 *
	 * @param _parent The parent PApplet object
	 * @param _networkSize The number of oscillators in the network
	 * @param _coupling The level of coupling in the network
	 * @param _stepSize The size of the time step
	 */
	public PNetwork(PApplet _parent, int _networkSize, float _coupling, float _stepSize) {
		parent = _parent;
		parent.registerMethod("dispose", this);
		networkSize = _networkSize;
		time = 0.0f;
		stepSize = _stepSize;
		coupling = _coupling;
		orderParameter = 1.0f;
		averagePhase = PI;
		naturalFrequency = new float[networkSize];
	    phase = new float[networkSize];
	    velocity = new float[networkSize];
	    acceleration = new float[networkSize];
		initializeFrequency();
		initializePhase();
		initializeVelocity();
		initializeAcceleration();
	}
	
	/**
	 * Set natural frequencies of oscillators using Perlin noise.
	 */
	private void initializeFrequency() {
		int t = 0;
		int dt = 1;
		for (int i = 0; i < networkSize; i++) {
			naturalFrequency[i] = TWO_PI * parent.noise(t);
			t += dt;
		}
	}
	
	/**
	 * Set initial phases of oscillators using Perlin noise.
	 */
	private void initializePhase() {
		int t = 0;
		int dt = 1;
		for (int i = 0; i < networkSize; i++) {
			phase[i] = TWO_PI * parent.noise(t);
			t += dt;
		}
	}
	
	/**
	 * Set initial angular velocity of oscillators to 0.
	 */
	private void initializeVelocity() {
		for (int i = 0; i < networkSize; i++) {
			velocity[i] = 0;
		}
	}
	
	/**
	 * Set initial angular acceleration of oscillators to 0.
	 */
	private void initializeAcceleration() {
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
		int numSteps = 1;
		step(numSteps);
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
		calculateOrder();
		float noise = parent.noise(time);
		for (int i = 0; i < networkSize; i++) {
			float k1 = stepSize*differentiate(phase[i], noise, naturalFrequency[i]);
			float k2 = stepSize*differentiate(phase[i] + k1/2, noise, naturalFrequency[i]);
			float k3 = stepSize*differentiate(phase[i] + k2/2, noise, naturalFrequency[i]);
			float k4 = stepSize*differentiate(phase[i] + k3, noise, naturalFrequency[i]);
			// Update phase
			float oldPhase = phase[i];
			float newPhase = (oldPhase + (k1 + 2*k2 + 2*k3 + k4)/6) % TWO_PI;
			phase[i] = newPhase;
			// Update velocity
			float oldVelocity = velocity[i];
			float newVelocity = calculateVelocity(oldPhase, newPhase);
			velocity[i] = newVelocity;
			// Update acceleration
			float oldAcceleration = acceleration[i];
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
	 * @param phase
	 * @param noise
	 * @param naturalFrequency
	 * @param orderParameter
	 * @param averagePhase
	 * 
	 * @return {@code float} time derivative
	 */
	private float differentiate(float phase, float noise, float naturalFrequency) {
		float phaseDerivative = naturalFrequency 
				+ noise
				+ coupling * orderParameter * PApplet.sin(averagePhase - phase);

		return phaseDerivative;
	}
	
	/**
	 * Calculate the complex "order" of the network.
	 * 
	 * @return {@code PVector} x-real y-imaginary
	 */
	private void calculateOrder() {
		PVector orderVector = new PVector();
		for (int i = 0; i < networkSize; i++) {
			PVector phaseVector = new PVector(PApplet.cos(phase[i]), PApplet.sin(phase[i]));
			orderVector.add(phaseVector);
		}
		
		orderVector.div(networkSize);
		orderParameter = orderVector.mag();
		averagePhase = orderVector.heading();
	}
	
	/**
	 * Dispose of the PNetwork object.
	 */
	public void dispose() {

	}

}
