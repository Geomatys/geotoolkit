/*
 * @(#)Function.java
 *
 * $Date: 2011-05-02 16:01:45 -0500 (Mon, 02 May 2011) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.bric.math.function;

/** This is a simple mathematical function: f(x) */
public interface Function {
	/** Evaluates f(x).
	 * 
	 */
	public double evaluate(double x);
	
	/** Returns all the x-values for the equation f(x) = y.
	 * 
	 */
	public double[] evaluateInverse(double y);
}
