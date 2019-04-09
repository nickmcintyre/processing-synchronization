package sync;

import processing.core.PApplet;

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
 * @example BasicExample
 */
public class PNetwork {

	public PApplet parent;

	/**
	 * Initialize the PSystem object.
	 *
	 * @param theParent
	 */
	public PNetwork(PApplet theParent) {
		parent = theParent;
		parent.registerMethod("dispose", this);
	}
	
	/**
	 * Dispose of the PNetwork object.
	 */
	public void dispose() {

	}

}
