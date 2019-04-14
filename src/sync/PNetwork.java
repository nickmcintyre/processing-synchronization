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
 * @example CircleExample
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
		setNaturalFrequencies();
		setInitialPhases();
	}
	
	/**
	 * Set natural frequencies of oscillators using Perlin noise.
	 */
	private void setNaturalFrequencies() {
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
	private void setInitialPhases() {
		int t = 0;
		int dt = 1;
		for (int i = 0; i < networkSize; i++) {
			phase[i] = TWO_PI * parent.noise(t);
			t += dt;
		}
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
		for (int i = 0; i < networkSize; i++) {
			float noise = parent.noise(time);
			float k1 = stepSize*differentiate(phase[i], noise, naturalFrequency[i]);
			float k2 = stepSize*differentiate(phase[i] + k1/2, noise, naturalFrequency[i]);
			float k3 = stepSize*differentiate(phase[i] + k2/2, noise, naturalFrequency[i]);
			float k4 = stepSize*differentiate(phase[i] + k3, noise, naturalFrequency[i]);
			phase[i] = (phase[i] + (k1 + 2*k2 + 2*k3 + k4)/6) % TWO_PI;
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
		PVector phaseVector = new PVector();
		for (int i = 0; i < networkSize; i++) {
			phaseVector.set(PApplet.cos(phase[i]), PApplet.sin(phase[i]));
			orderVector.add(phaseVector);
		}
		
		orderVector.div(networkSize);		
		orderParameter = orderVector.mag();
		averagePhase = PApplet.acos(orderVector.x/orderParameter);
	}
	
	/**
	 * Dispose of the PNetwork object.
	 */
	public void dispose() {

	}

}
