/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.utils;

import java.util.Arrays;

/**
 * Helpers for unsigned 64 bits values (TypeScript {@code bigint} cell ids stored in Java {@code long}).
 *
 * <p>Port of upstream {@code utils/bigint.ts}, plus the unsigned sort helpers replacing
 * {@code BigUint64Array.prototype.sort()}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class BigInts {

    private BigInts() {
    }

    /**
     * Comparator for sorting bigint arrays.
     * Values are compared as unsigned 64 bits integers.
     */
    public static int compareBigint(long a, long b) {
        return Long.compareUnsigned(a, b);
    }

    /**
     * Sorts the array in ascending unsigned order, in place.
     * Equivalent of upstream {@code BigUint64Array.prototype.sort()}.
     *
     * @param array values to sort
     */
    public static void sortUnsigned(long[] array) {
        sortUnsigned(array, 0, array.length);
    }

    /**
     * Sorts the given range of the array in ascending unsigned order, in place.
     *
     * @param array values to sort
     * @param from index of the first element, inclusive
     * @param to index of the last element, exclusive
     */
    public static void sortUnsigned(long[] array, int from, int to) {
        // Flip the sign bit so that signed order equals unsigned order
        for (int i = from; i < to; i++) {
            array[i] ^= Long.MIN_VALUE;
        }
        Arrays.sort(array, from, to);
        for (int i = from; i < to; i++) {
            array[i] ^= Long.MIN_VALUE;
        }
    }
}
