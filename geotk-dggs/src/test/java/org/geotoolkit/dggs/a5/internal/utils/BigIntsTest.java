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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests of the Java specific unsigned helpers of {@link BigInts} (no upstream equivalent).
 *
 * @author Johann Sorel (Geomatys)
 */
public class BigIntsTest {

    @Test
    void compareBigint_is_unsigned() {
        assertTrue(BigInts.compareBigint(1L, -1L) < 0);
        assertTrue(BigInts.compareBigint(-1L, 1L) > 0);
        assertTrue(BigInts.compareBigint(5L, 5L) == 0);
    }

    @Test
    void sortUnsigned_sorts_in_unsigned_ascending_order() {
        final long[] values = {-1L, 3L, Long.MIN_VALUE, 0L, Long.MAX_VALUE, 1L};
        BigInts.sortUnsigned(values);
        assertArrayEquals(new long[]{0L, 1L, 3L, Long.MAX_VALUE, Long.MIN_VALUE, -1L}, values);
    }

    @Test
    void sortUnsigned_sorts_a_range_only() {
        final long[] values = {-1L, -2L, 7L, 0L, -5L};
        BigInts.sortUnsigned(values, 1, 4);
        assertArrayEquals(new long[]{-1L, 0L, 7L, -2L, -5L}, values);
    }
}
