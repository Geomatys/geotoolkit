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

import java.util.Random;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.sis.test.TestCase;

import static org.opengis.test.Assert.*;


/**
 * Base class for testing {@link ListIterator} implementations.
 *
 * @param <E> The type of elements in the iterator to test.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @since   3.22
 * @version 3.22
 * @module
 */
public abstract strictfp class ListIteratorTestCase<E> extends TestCase {
    /**
     * The iterator to test, which must be supplied by the sub-class.
     */
    protected ListIterator<E> iterator;

    /**
     * A reference collection containing all the data to be traversed by the {@linkplain #iterator}.
     * It should be an instance from the standard standard Java collection framework - not an instance
     * of a class to be tested - because this collection will be used as a reference implementation.
     */
    protected Collection<E> data;

    /**
     * A random number generator. This generator shall be initialized (if needed) by the subclass
     * as below:
     *
     * {@preformat java
     *   random = TestUtilities.createRandomNumberGenerator("myTestMethod");
     * }
     */
    protected Random random;

    /**
     * {@code true} for testing forward iteration using {@link ListIterator#next()}.
     * At least one of {@code testForward} and {@link #testBackward} must be {@code true}.
     */
    protected boolean testForward;

    /**
     * {@code true} for testing backward iteration using {@link ListIterator#next()}.
     * At least one of {@link #testForward} and {@code testBackward} must be {@code true}.
     */
    protected boolean testBackward;

    /**
     * {@code true} for testing {@link ListIterator#remove()}.
     */
    protected boolean testRemove;

    /**
     * Creates a new test case. Subclasses shall initialize the protected fields either in their
     * constructor, in method annotated by {@code BeforeTest} or at the beginning of the test method.
     */
    protected ListIteratorTestCase() {
    }

    /**
     * Returns the data as a {@link List}, by casting the {@linkplain #collection} if possible
     * or copying it otherwise.
     */
    private List<E> asList() {
        if (data instanceof List<?>) {
            return (List<E>) data;
        } else {
            return new ArrayList<E>(data);
        }
    }

    /**
     * Runs the tests on the current {@linkplain #iterator}. This method goes forward or
     * backward randomly, and compares the following methods against the expected values:
     *
     * <ul>
     *   <li>{@link ListIterator#nextIndex()}</li>
     *   <li>{@link ListIterator#previousIndex()}</li>
     *   <li>{@link ListIterator#next()}</li>
     *   <li>{@link ListIterator#previous()}</li>
     * </ul>
     *
     * Additional optional tests:
     *
     * <ul>
     *   <li>{@link ListIterator#remove()} if {@link #testRemove} is {@code true}.</li>
     * </ul>
     *
     * @param index Expected initial position of the iterator.
     * @param numIterations Maximal number of iterations to perform.
     */
    protected void runTest(int index, final int numIterations) {
        final boolean testForward  = this.testForward;
        final boolean testBackward = this.testBackward;
        final boolean testRemove   = this.testRemove;
        final ListIterator<E> iterator = this.iterator; // Protect from changes.
        assertTrue("At least one of 'testForward' and 'testBackward' shall be true.", testForward | testBackward);

        final List<E> asList = asList();
        int count = asList.size();
        boolean forward = testForward;
        for (int iter=0; iter<numIterations; iter++) {
            /*
             * Get the direction of traversal. We will perform on average 4 steps
             * forward or backward before to reverse the direction of traversal.
             */
            if (index == 0) {
                assertFalse("ListIterator[0].hasPrevious()", iterator.hasPrevious());
                if (!testForward) break;
                forward = true;
            } else if (index == count) {
                assertFalse("ListIterator[end].hasNext()", iterator.hasNext());
                if (!testBackward) break;
                forward = false;
            } else if ((testForward & testBackward) && random.nextInt(5) == 0) {
                forward = !forward;
            }
            /*
             * Move the iterator to the next or previous position and check.
             */
            final String message;
            final int indexOfExpected;
            final E actual;
            if (forward) {
                message = "On iter=" + iter + ", ListIterator[" + index + "].next";
                assertEquals(message, index, iterator.nextIndex());
                assertTrue(message, iterator.hasNext());
                actual = iterator.next();
                assertEquals(message, index, iterator.previousIndex());
                indexOfExpected = index++;
            } else {
                indexOfExpected = --index;
                message = "On iter=" + iter + ", ListIterator[" + index + "].previous";
                assertEquals(message, index, iterator.previousIndex());
                assertTrue(message, iterator.hasPrevious());
                actual = iterator.previous();
                assertEquals(message, index, iterator.nextIndex());
            }
            assertEquals(message, asList.get(indexOfExpected), actual);
            /*
             * Optionally tests removal operations, if enabled.
             * We will remove only 1/3 of data on average.
             */
            if (testRemove && random.nextInt(3) == 0) {
                iterator.remove();
                if (forward) {
                    index--;
                }
                assertEquals(message, index,   iterator.nextIndex());
                assertEquals(message, index-1, iterator.previousIndex());
                if (data != asList) {
                    asList.clear();
                    asList.addAll(data);
                }
                assertEquals(--count, asList.size());
                if (count == 0) {
                    break;
                }
            }
        }
    }
}
