/*
 * @(#)FloatArrayFactory.java
 *
 * $Date: 2012-07-03 01:10:05 -0500 (Tue, 03 Jul 2012) $
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
package com.bric.util;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import com.bric.math.MutableInteger;

/** This is a mechanism to recycle arrays.
 * <P>Creating arrays in a heavy, fast-paced loop
 * ends up being very expensive.  This stores
 * arrays and returns them on demand.
 * <P>This factory stores these arrays with
 * strong references, so this class should only
 * really be used when the possible array sizes
 * are limited and the usage is predictable.
 *
 */
public class FloatArrayFactory {
	private static FloatArrayFactory globalFactory;
	
	public static FloatArrayFactory getStaticFactory() {
		if(globalFactory==null)
			globalFactory = new FloatArrayFactory();
		return globalFactory;
	}
	
	private Map<Number, Stack<float[]>> map = createMap();
	
	@SuppressWarnings("unchecked")
	private static Map<Number, Stack<float[]>> createMap() {
		try {
			//break this up into two strings for the JarWriter
			//If the whole name is listed, then the JarWriter
			//will bundle the trove jar automatically...
			Class<?> troveMap = Class.forName("gnu."+"trove.THashMap");
			Constructor<?>[] constructors = troveMap.getConstructors();
			for(int a = 0; a<constructors.length; a++) {
				if(constructors[a].getParameterTypes().length==0)
					return (Map<Number, Stack<float[]>>)constructors[a].newInstance(new Object[] {});
			}
		} catch(Throwable e) {
			//in addition to the expected exceptions, consider
			//UnsupportedClassVersionErrors, and other weirdnesses.
		}
		
		return new Hashtable<Number, Stack<float[]>>();
	}
	
	private MutableInteger key = new MutableInteger(0);
	
	/** Returns a float array of the indicated size.
	 * <P>If arrays of that size have previously been
	 * stored in this factory, then an existing array
	 * will be returned.
	 * @param size the array size you need.
	 * @return a float array of the size indicated.
	 */
	public float[] getArray(int size) {
		Stack<float[]> stack;
		synchronized(key) {
			key.value = size;
			stack = map.get(key);
			if(stack==null) {
				stack = new Stack<float[]>();
				map.put(new MutableInteger(size),stack);
			}
		}
		if(stack.size()==0) {
			return new float[size];
		}
		return stack.pop();
	}
	
	/** Stores an array for future use.
	 * <P>As soon as you call this method you should nullify
	 * all other references to the argument.  If you continue
	 * to use it, and someone else retrieves this array
	 * by calling <code>getArray()</code>, then you may have
	 * two entities using the same array to manipulate data...
	 * and that can be really hard to debug!
	 * 
	 * @param array the array you no longer need that might be
	 * needed later.
	 */
	public void putArray(float[] array) {
		Stack<float[]> stack;
		synchronized(key) {
			key.value = array.length;
			stack = map.get(key);
			if(stack==null) {
				stack = new Stack<float[]>();
				map.put(new MutableInteger(array.length),stack);
			}
		}
		stack.push(array);
	}
}
