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
package org.geotoolkit.dggs.a5.internal.core;

import static org.geotoolkit.dggs.a5.internal.core.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.QUINTANT_SHIFT;

/**
 * The compaction marker: the value a covering carries as its last
 * element, recording the resolution its cells stand for. It is a value no cell
 * can take: quintant 60 (only 0-59 exist), the resolution in bits 55-48, and the
 * marker tag 1000000 in bits 6-0. Cell IDs end in a 1 followed by an odd number
 * of zeros, or in one of the res-30 patterns ...1, ...100, ...10000, never in a 1
 * followed by 6 zeros. The other bits carry no meaning yet: they are written as
 * 0 and ignored when read.
 *
 * <pre>
 *   63-58    57-56   55-48        47-7          6-0
 *   111100   00      resolution   reserved (0)  1000000
 * </pre>
 *
 * <p>Port of upstream {@code core/compaction-marker.ts}. Values are unsigned 64 bits patterns
 * (markers are above 2^63, negative as signed longs).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CompactionMarker {

    private static final long COMPACTION_MARKER_PREFIX = 60L << QUINTANT_SHIFT;
    private static final long COMPACTION_MARKER_END = 61L << QUINTANT_SHIFT;
    private static final int COMPACTION_MARKER_RESOLUTION_SHIFT = 48;
    private static final long COMPACTION_MARKER_TAG = 0b1000000L;
    private static final long LOW_7_BITS = 0b1111111L;

    private CompactionMarker() {
    }

    /**
     * The compaction marker recording {@code resolution}.
     *
     * @param resolution covering resolution
     * @return marker value (unsigned)
     */
    public static long compactionMarker(int resolution) {
        return COMPACTION_MARKER_PREFIX | (((long) resolution) << COMPACTION_MARKER_RESOLUTION_SHIFT) | COMPACTION_MARKER_TAG;
    }

    /**
     * The resolution a compaction marker records.
     *
     * @param value marker value (unsigned)
     * @return recorded resolution
     */
    public static int compactionMarkerResolution(long value) {
        return (int) ((value >>> COMPACTION_MARKER_RESOLUTION_SHIFT) & 0xFFL);
    }

    /**
     * Check whether a value is a compaction marker: the value a covering
     * carries, as its last element, to record its resolution. It is not a cell.
     *
     * @param value value to test (unsigned)
     * @return whether the value is a compaction marker
     */
    public static boolean isCompactionMarker(long value) {
        return (
            Long.compareUnsigned(value, COMPACTION_MARKER_PREFIX) >= 0 &&
            Long.compareUnsigned(value, COMPACTION_MARKER_END) < 0 &&
            (value & LOW_7_BITS) == COMPACTION_MARKER_TAG &&
            compactionMarkerResolution(value) <= MAX_RESOLUTION
        );
    }
}
