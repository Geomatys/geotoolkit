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

import java.util.ArrayList;
import org.junit.Test;

import static org.apache.sis.test.TestUtilities.createRandomNumberGenerator;


/**
 * Tests the {@link ListIteratorTestCase} class.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @since   3.22
 * @version 3.22
 * @module
 */
public final strictfp class ListIteratorTest extends ListIteratorTestCase<Integer> {
    /**
     * Tests using {@link ArrayList} as the reference implementation.
     */
    @Test
    public void compareToArrayList() {
        final ArrayList<Integer> list = new ArrayList<>(100);
        for (int i=100; --i>=0;) {
            list.add(i);
        }
        data         = list;
        iterator     = list.listIterator();
        random       = createRandomNumberGenerator();
        testForward  = true;
        testBackward = true;
        testRemove   = true;
        runTest(0, 300);
    }
}
