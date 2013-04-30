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
package org.geotoolkit.internal;

import java.util.LinkedHashSet;
import org.junit.Test;
import org.junit.Ignore;
import org.apache.sis.test.DependsOnMethod;
import org.geotoolkit.test.ListIteratorTestCase;

import static org.opengis.test.Assert.*;
import static org.apache.sis.test.TestUtilities.createRandomNumberGenerator;


/**
 * Tests the {@link ListIteratorAdapter} class.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @since   0.3
 * @version 0.3
 * @module
 */
@SuppressWarnings("deprecation")
public final strictfp class ListIteratorAdapterTest extends ListIteratorTestCase<Integer> {
    /**
     * Number of initial values in {@link #data}.
     */
    private static final int COUNT = 20;

    /**
     * Creates a new test case. The {@link #data} field will be set to an {@link LinkedHashSet}
     * instance instead than {@code List} in order to prevent {@code ListIteratorAdapter} to
     * delegate to {@code data.listIterator()}.
     */
    public ListIteratorAdapterTest() {
        data = new LinkedHashSet<>(COUNT + COUNT/4);
        for (int i=0; i<COUNT; i++) {
            assertTrue(data.add(i));
        }
    }

    /**
     * Asserts that the current {@linkplain #iterator} is an instance of {@link ListIteratorAdapter}.
     */
    private void assertInstanceOfAdapter() {
        assertInstanceOf("Adapter for Set.iterator()", ListIteratorAdapter.class, iterator);
    }

    /**
     * Tests iterator traversal starting from index 0 and
     * without calls to {@link ListIteratorAdapter#remove()}.
     */
    @Test
    public void testReadOnlyFromStart() {
        iterator     = ListIteratorAdapter.first(data);
        random       = createRandomNumberGenerator("testReadOnlyFromStart");
        testForward  = true;
        testBackward = true;
        assertInstanceOfAdapter();
        runTest(0, 50);
    }

    /**
     * Tests iterator traversal starting from index 20 and
     * without calls to {@link ListIteratorAdapter#remove()}.
     */
    @Test
    public void testReadOnlyFromEnd() {
        iterator     = ListIteratorAdapter.last(data);
        random       = createRandomNumberGenerator("testReadOnlyFromEnd");
        testForward  = true;
        testBackward = true;
        assertInstanceOfAdapter();
        runTest(COUNT, 50);
    }

    /**
     * Tests iterator traversal starting from index 0 and
     * with calls to {@link ListIteratorAdapter#remove()}.
     */
    @Test
    @DependsOnMethod("testReadOnlyFromStart")
    public void testForwardReadWrite() {
        iterator     = ListIteratorAdapter.first(data);
        random       = createRandomNumberGenerator("testForwardReadWrite");
        testForward  = true;
        testRemove   = true;
        assertInstanceOfAdapter();
        runTest(0, COUNT);
    }

    /**
     * Tests iterator traversal starting from index 20 and
     * with calls to {@link ListIteratorAdapter#remove()}.
     */
    @Test
    @DependsOnMethod("testReadOnlyFromEnd")
    public void testBackwardReadWrite() {
        iterator     = ListIteratorAdapter.last(data);
        random       = createRandomNumberGenerator("testBackwardReadWrite");
        testBackward = true;
        testRemove   = true;
        assertInstanceOfAdapter();
        runTest(COUNT, COUNT);
    }

    /**
     * Tests iterator traversal in both directions
     * with calls to {@link ListIteratorAdapter#remove()}.
     */
    @Test
    @Ignore("ListIteratorAdapter implementation still have bugs.")
    @DependsOnMethod({"testForwardReadWrite","testBackwardReadWrite"})
    public void testRandomReadWrite() {
        iterator     = ListIteratorAdapter.first(data);
        random       = new java.util.Random(33553325902459L); //createRandomNumberGenerator("testRandomReadWrite");
        testForward  = true;
        testBackward = true;
        testRemove   = true;
        assertInstanceOfAdapter();
        runTest(0, COUNT);
    }
}
