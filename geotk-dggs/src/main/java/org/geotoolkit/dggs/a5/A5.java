/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.dggs.a5;

import java.math.BigInteger;
import java.util.List;
import org.apache.sis.geometries.math.Vector2D;
import org.geotoolkit.dggs.a5.internal.Cell;
import org.geotoolkit.dggs.a5.internal.Serialization;

/**
 * Shortcuts for A5.
 *
 * @author Johann Sorel (Geomatys)
 */
final class A5 {

    private A5(){}

    public static Vector2D.Double[] cellToBoundary(long cellId) {
        return Cell.cellToBoundary(cellId, new Cell.CellToBoundaryOptions());
    }

    public static Vector2D.Double cellToLonLat(long cellId) {
        return Cell.cellToLonLat(cellId);
    }

    public static long lonLatToCell(Vector2D.Double lonLat, int resolution) {
        return Cell.lonLatToCell(lonLat, resolution);
    }

    public static BigInteger hexToBigInt(String hex) {
        return new BigInteger(hex, 16);
    }

    public static String bigIntToHex(BigInteger index) {
        return index.toString(16);
    }

    public static long cellToParent(long index, Integer parentResolution) {
        return Serialization.cellToParent(index, parentResolution);
    }

    public static List<Long> cellToChildren(long index, Integer childResolution) {
        return Serialization.cellToChildren(index, childResolution);
    }

    public static int getResolution(long index) {
        return Serialization.getResolution(index);
    }

}
