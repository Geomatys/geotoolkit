/**
 * Copyright (C) 2025 Geomatys and Felix Palmer
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
package org.geotoolkit.dggs.a5.internal;

import java.util.ArrayList;
import java.util.List;
import static org.geotoolkit.dggs.a5.internal.Orig.*;
import org.geotoolkit.dggs.a5.internal.Utils.A5Cell;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class Serialization {

    public static final int FIRST_HILBERT_RESOLUTION = 3;
    public static final int MAX_RESOLUTION = 31;
    public static final long HILBERT_START_BIT = 58; // 64 - 6 bits for origin & segment

    // First 6 bits 0, remaining 58 bits 1
    public static final long REMOVAL_MASK = 0x3ffffffffffffffl;

    // First 6 bits 1, remaining 58 bits 0
    public static final long ORIGIN_SEGMENT_MASK = 0xfc00000000000000l;

    // All 64 bits 1
    public static final long ALL_ONES = 0xffffffffffffffffl;

    public static int getResolution(long index) {
        // Find resolution from position of first non-00 bits from the right
        int resolution = MAX_RESOLUTION - 1;
        long shifted = index >> 1; // TODO check if non-zero for point level
        while (resolution > 0 && ((shifted & 1l) == 0l)) {
            resolution -= 1;
            // For non-Hilbert resolutions, resolution marker moves by 1 bit per resolution
            // For Hilbert resolutions, resolution marker moves by 2 bits per resolution
            shifted = shifted >> (resolution < FIRST_HILBERT_RESOLUTION ? 1 : 2);
        }

        return resolution;
    }

    public static A5Cell deserialize(long index) {
        final int resolution = getResolution(index);

        if (resolution == 0) {
            return new A5Cell(origins.get(0), 0, 0, resolution);
        }

        // Extract origin*segment from top 6 bits
        final int top6Bits = (int) (index >>> 58l);

        // Find origin and segment that multiply to give this product
        final Utils.Origin origin;
        int segment;

        if (resolution == 1) {
            final int originId = top6Bits;
            origin = origins.get(originId);
            segment = 0;
        } else {
            final int originId = (int) Math.floor(top6Bits / 5);
            origin = origins.get(originId);
            segment = (top6Bits + origin.firstQuintant) % 5;
        }

//        if (!origin) { //can't happen in java, error will be raised sooner
//            throw new IllegalArgumentException("Could not parse origin: " + top6Bits);
//        }

        if (resolution < FIRST_HILBERT_RESOLUTION) {
            return new A5Cell(origin, segment, 0, resolution);
        }

        // Mask away origin & segment and shift away resolution and 00 bits
        final int hilbertLevels = resolution - FIRST_HILBERT_RESOLUTION + 1;
        final long hilbertBits = 2l * hilbertLevels;
        final long shift = HILBERT_START_BIT - hilbertBits;
        final long S = (index & REMOVAL_MASK) >> shift;
        return new A5Cell(origin, segment, S, resolution);
    }

    public static long serialize(A5Cell cell) {
        final Origin origin = cell.origin;
        final int segment = cell.segment;
        final long S = cell.S;
        final int resolution = cell.resolution;

        if (resolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException("Resolution " + resolution + " is too large");
        }

        if (resolution == 0) return 0;

        // Position of resolution marker as bit shift from LSB
        long R;
        if (resolution < FIRST_HILBERT_RESOLUTION) {
            // For non-Hilbert resolutions, resolution marker moves by 1 bit per resolution
            R = (long)resolution;
        } else {
            // For Hilbert resolutions, resolution marker moves by 2 bits per resolution
            final int hilbertResolution = 1 + resolution - FIRST_HILBERT_RESOLUTION;
            R = 2l * hilbertResolution + 1;
        }

        // First 6 bits are the origin id and the segment
        int segmentN = (segment - origin.firstQuintant + 5) % 5;

        long index;
        if (resolution == 1) {
            index = (long)origin.id << 58l;
        } else {
            index = (long)(5 * origin.id + segmentN) << 58l;
        }

        if (resolution >= FIRST_HILBERT_RESOLUTION) {
            // Number of bits required for S Hilbert curve
            final int hilbertLevels = resolution - FIRST_HILBERT_RESOLUTION + 1;
            final long hilbertBits = 2l * hilbertLevels;
            if (S >= (1l << hilbertBits)) {
                throw new IllegalArgumentException("S " + S + " is too large for resolution level " + resolution);
            }
            // Next (2 * hilbertResolution) bits are S (hilbert index within segment)
            index += ((long)S) << (HILBERT_START_BIT - hilbertBits);
        }

        // Resolution is encoded by position of the least significant 1
        index |= 1l << (HILBERT_START_BIT - R);

        return index;
    }

    public static List<Long> cellToChildren(long index) {
        return cellToChildren(index, null);
    }

    public static List<Long> cellToChildren(long index, Integer childResolution) {
        final A5Cell cell = deserialize(index);
        final Origin origin = cell.origin;
        final int segment = cell.segment;
        final long S = cell.S;
        final int currentResolution = cell.resolution;

        final int newResolution = (childResolution != null) ? childResolution : currentResolution + 1;

        if (newResolution <= currentResolution) {
            throw new IllegalArgumentException("Target resolution " + newResolution + " must be greater than current resolution " + currentResolution);
        }

        if (newResolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException("Target resolution " + newResolution + " exceeds maximum resolution " + MAX_RESOLUTION);
        }


        List<Origin> newOrigins = List.of(origin);
        List<Integer> newSegments = List.of(segment);
        if (currentResolution == 0) {
            newOrigins = origins;
        }
        if ((currentResolution == 0 && newResolution > 1) || currentResolution == 1) {
            newSegments = List.of(0, 1, 2, 3, 4);
        }

        final int resolutionDiff = newResolution - Math.max(currentResolution, FIRST_HILBERT_RESOLUTION - 1);
        final double childrenCount = Math.pow(4, resolutionDiff); //this can have a decimal value if resolution under FIRST_HILBERT_RESOLUTION
        final List<Long> children = new ArrayList<>();
        final long shiftedS = S << (2l * resolutionDiff);
        for (Origin newOrigin : newOrigins) {
            for (Integer newSegment : newSegments) {
                for (long i = 0; i < childrenCount; i++) {
                    long newS = shiftedS + i;
                    children.add(serialize(new A5Cell(newOrigin, newSegment, newS, newResolution)));
                }
            }
        }

        return children;
    }

    public static long cellToParent(long index) {
        return cellToParent(index, null);
    }

    public static long cellToParent(long index, Integer parentResolution) {
        final A5Cell cell = deserialize(index);
        final Origin origin = cell.origin;
        final int segment = cell.segment;
        final long S = cell.S;
        final int currentResolution = cell.resolution;

        int newResolution = (parentResolution != null) ? parentResolution : currentResolution - 1;

        if (newResolution < 0) {
            throw new IllegalArgumentException("Target resolution " + newResolution + " cannot be negative");
        }

        if (newResolution >= currentResolution) {
            throw new IllegalArgumentException("Target resolution " + newResolution + " must be less than current resolution " + currentResolution);
        }

        int resolutionDiff = currentResolution - newResolution;
        long shiftedS = S >> (2l * resolutionDiff);
        return serialize( new A5Cell(origin, segment, shiftedS, newResolution));
    }
}
