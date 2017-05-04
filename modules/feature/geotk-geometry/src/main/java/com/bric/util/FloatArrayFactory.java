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

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This is a mechanism to recycle arrays.
 * <P>
 * Creating arrays in a heavy, fast-paced loop ends up being very expensive.
 * This stores arrays and returns them on demand.
 * <P>
 * This factory stores these arrays with strong references, so this class should
 * only really be used when the possible array sizes are limited and the usage
 * is predictable.
 *
 */
public final class FloatArrayFactory {

    private static final FloatArrayFactory globalFactory = new FloatArrayFactory();

    public static FloatArrayFactory getStaticFactory() {
        return globalFactory;
    }

    private final Map<Number, Deque<float[]>> map = new ConcurrentHashMap<>();


    /**
     * Returns a float array of the indicated size.
     * <P>
     * If arrays of that size have previously been stored in this factory, then
     * an existing array will be returned.
     *
     * @param size the array size you need.
     * @return a float array of the size indicated.
     */
    public float[] getArray(int size) {
        final Deque<float[]> stack = map.get(size);
        float[] array = (stack != null) ? stack.poll() : null;
        if(array==null){
            array= new float[size];
        }
        return array;
    }

    /**
     * Stores an array for future use.
     * <P>
     * As soon as you call this method you should nullify all other references
     * to the argument. If you continue to use it, and someone else retrieves
     * this array by calling <code>getArray()</code>, then you may have two
     * entities using the same array to manipulate data... and that can be
     * really hard to debug!
     *
     * @param array the array you no longer need that might be needed later.
     */
    public void putArray(float[] array) {
        Deque stack = map.get(array.length);
        if(stack==null){
            stack = new ConcurrentLinkedDeque();
            map.put(array.length, stack);
        }
        stack.push(array);
    }
}
