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
package org.geotoolkit.dggs.a5.internal.coverings;

import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.compactionMarkerResolution;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.RES30_TAG_BITS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.RESOLUTION_TAGS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;

/**
 * The resolution of a set of cells.
 *
 * <p>Port of upstream {@code coverings/resolution.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Resolution {

    private Resolution() {
    }

    /**
     * The resolution of a set of cells: the resolution of its compaction marker, or of
     * its finest cell when it has none. A covering stands for all its
     * cells at this resolution. Returns -1 for an empty set (or the world cell).
     *
     * @param cells Cells, as returned by {@code compact}, {@code polygonToCells} etc. (unsigned)
     * @return Resolution (-1 to 30)
     */
    public static int coveringResolution(long[] cells) {
        // A covering ends in its compaction marker, which records the resolution
        if (cells.length > 0) {
            final long last = cells[cells.length - 1];
            if (isCompactionMarker(last)) return compactionMarkerResolution(last);
        }

        // Otherwise the finest cell: finer cells have smaller resolution tags (the
        // lowest set bit), except at res 30, whose tags are recognised separately
        long finestTag = 0L;
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            final long tag;
            if (isCompactionMarker(cell)) {
                final int resolution = compactionMarkerResolution(cell);
                if (resolution == MAX_RESOLUTION) return MAX_RESOLUTION;
                tag = RESOLUTION_TAGS[resolution];
            } else {
                tag = cell & -cell;
                if ((tag & RES30_TAG_BITS) != 0L) return MAX_RESOLUTION;
            }
            // unsigned: the tag of a cell can be 2^63
            if (tag != 0L && (finestTag == 0L || Long.compareUnsigned(tag, finestTag) < 0)) finestTag = tag;
        }
        return finestTag == 0L ? -1 : getResolution(finestTag);
    }
}
