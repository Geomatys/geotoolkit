/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.test;

import java.util.Iterator;

import static org.junit.Assert.*;


/**
 * Copy of test methods provided by Apache SIS.
 */
public final class TestUtilities {
    /**
     * Do not allow instantiation of this class.
     */
    private TestUtilities() {
    }

    /**
     * Returns the single element from the given array. If the given array is null or
     * does not contains exactly one element, then an {@link AssertionError} is thrown.
     *
     * @param  <E>    the type of array elements.
     * @param  array  the array from which to get the singleton.
     * @return the singleton element from the array.
     */
    public static <E> E getSingleton(final E[] array) {
        assertNotNull("Null array.", array);
        assertEquals("Not a singleton array.", 1, array.length);
        return array[0];
    }

    /**
     * Returns the single element from the given collection. If the given collection is null
     * or does not contains exactly one element, then an {@link AssertionError} is thrown.
     *
     * @param  <E>         the type of collection elements.
     * @param  collection  the collection from which to get the singleton.
     * @return the singleton element from the collection.
     */
    public static <E> E getSingleton(final Iterable<? extends E> collection) {
        assertNotNull("Null collection.", collection);
        final Iterator<? extends E> it = collection.iterator();
        assertTrue("The collection is empty.", it.hasNext());
        final E element = it.next();
        assertFalse("The collection has more than one element.", it.hasNext());
        return element;
    }
}
