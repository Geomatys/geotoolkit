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

import java.math.BigInteger;
import static org.geotoolkit.dggs.a5.internal.core.Constants.AUTHALIC_AREA_EARTH;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;

/**
 * Cell counts and sizes by resolution.
 *
 * <p>Port of upstream {@code core/cell-info.ts}. The TS {@code number} overloads take and return
 * {@code int}/{@code double}; the TS {@code bigint} overloads take and return {@link BigInteger}
 * (counts reach 60·4^29 &gt; 2^63).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CellInfo {

    private static final BigInteger B4 = BigInteger.valueOf(4);
    private static final BigInteger B12 = BigInteger.valueOf(12);
    private static final BigInteger B60 = BigInteger.valueOf(60);

    private CellInfo() {
    }

    /**
     * Returns the number of cells at a given resolution.
     *
     * @param resolution The resolution level (use the BigInteger variant for exact value for high resolutions, 28+)
     * @return Number of cells at the given resolution
     */
    public static double getNumCells(int resolution) {
        if (resolution < 0) return 0;
        if (resolution == 0) return 12;
        return 60 * Math.pow(4, resolution - 1);
    }

    /**
     * Returns the exact number of cells at a given resolution (TS bigint overload).
     *
     * @param resolution The resolution level
     * @return Number of cells at the given resolution
     */
    public static BigInteger getNumCells(BigInteger resolution) {
        if (resolution.signum() < 0) return BigInteger.ZERO;
        if (resolution.signum() == 0) return B12;
        return B60.multiply(B4.pow(resolution.intValueExact() - 1));
    }

    /**
     * Returns the number of descendants a cell has at a finer resolution.
     *
     * @param parentResolution The cell's resolution (-1 for the world cell)
     * @param childResolution The resolution of the descendants (use the BigInteger variant for
     *   exact values when the count exceeds 2^53)
     * @return Number of descendants (1 at the same resolution, 0 if coarser)
     */
    public static double getNumChildren(int parentResolution, int childResolution) {
        if (childResolution < parentResolution) return 0;
        if (childResolution == parentResolution) return 1;
        if (parentResolution >= FIRST_CURVE_RESOLUTION) {
            // Between levels of constant aperture of 4, relation simplifies
            return Math.pow(4, childResolution - parentResolution);
        }

        double parentCount = getNumCells(parentResolution);
        if (parentCount == 0) parentCount = 1; // TS: getNumCells(parentResolution) || 1
        final double childCount = getNumCells(childResolution);
        return childCount / parentCount;
    }

    /**
     * Returns the exact number of descendants a cell has at a finer resolution (TS bigint overload).
     *
     * @param parentResolution The cell's resolution (-1 for the world cell)
     * @param childResolution The resolution of the descendants
     * @return Number of descendants (1 at the same resolution, 0 if coarser)
     */
    public static BigInteger getNumChildren(BigInteger parentResolution, BigInteger childResolution) {
        final BigInteger parent = parentResolution;
        final BigInteger child = childResolution;
        if (child.compareTo(parent) < 0) return BigInteger.ZERO;
        if (child.equals(parent)) return BigInteger.ONE;
        if (parent.compareTo(BigInteger.valueOf(FIRST_CURVE_RESOLUTION)) >= 0) return B4.pow(child.subtract(parent).intValueExact());
        return getNumCells(child).divide(parent.signum() < 0 ? BigInteger.ONE : getNumCells(parent));
    }

    /**
     * Returns the area of a cell at a given resolution in square meters.
     *
     * @param resolution The resolution level
     * @return Area of a cell in square meters
     */
    public static double cellArea(int resolution) {
        if (resolution < 0) return AUTHALIC_AREA_EARTH;
        return AUTHALIC_AREA_EARTH / getNumCells(resolution);
    }

    // Mean cell edge length divided by sqrt(cellArea), measured exhaustively from the
    // cell boundaries. Resolution 0 cells (dodecahedron faces) have their own
    // geometry; from resolution 1 the pentagonal tiling refines self-similarly and
    // the ratio converges to ~0.8211, so a constant serves all higher resolutions.
    private static final double[] EDGE_LENGTH_RATIOS = {0.7131, 0.8037, 0.8164, 0.8198, 0.8208, 0.821};
    private static final double EDGE_LENGTH_RATIO = 0.8211;

    /**
     * Returns the average edge length of a cell at a given resolution in meters.
     * Individual edge lengths vary from the average by roughly ±10%, depending
     * on the cell's shape and its position on the globe.
     *
     * @param resolution The resolution level
     * @return Average edge length of a cell in meters
     */
    public static double cellEdgeLengthAvg(int resolution) {
        if (resolution < 0) resolution = 0;
        final double ratio = resolution < EDGE_LENGTH_RATIOS.length ? EDGE_LENGTH_RATIOS[resolution] : EDGE_LENGTH_RATIO;
        return ratio * Math.sqrt(cellArea(resolution));
    }
}
