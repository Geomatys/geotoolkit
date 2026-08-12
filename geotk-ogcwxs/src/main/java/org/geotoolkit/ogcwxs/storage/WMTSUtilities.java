/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.storage;

import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixSet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;

/**
 * Scale related helpers for WMTS tile matrices.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
final class WMTSUtilities {

    private static final double STANDARD_PIXEL_SIZE = 0.00028d;
    private static final double EARTH_PERIMETER = 40075016d;

    /**
     * Used for WMTS predefined scale : GlobalCRS84Scale
     */
    private static final double SCALE = 500e6;
    private static final double PIXEL_SIZE = 1.25764139776733;

    private WMTSUtilities() {
    }

    static double unitsByPixel(final TileMatrixSet set, final CoordinateReferenceSystem setCrs, final TileMatrix matrix) {

        //predefined scales
        if ("GlobalCRS84Scale".equalsIgnoreCase(set.getIdentifier().getValue())) {
            return getGlobalCRS84PixelScale(matrix.getScaleDenominator());
        }

        // Specification default calculation method :
        // pixelSpan = scaleDenominator * 0.28 10-3 / metersPerUnit(crs)
        final double meterByUnit = metersPerUnit(setCrs);
        return matrix.getScaleDenominator() * STANDARD_PIXEL_SIZE / meterByUnit;
    }

    /**
     * @param crs CoordinateReferenceSystem
     * @return size in meter of one unit along CRS first axis
     */
    private static double metersPerUnit(final CoordinateReferenceSystem crs) {
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final Unit axi0Unit = cs.getAxis(0).getUnit();

        //in case axis in not in a meter compatible unit
        if (!Units.METRE.isCompatible(axi0Unit)) {
            if (Units.DEGREE.equals(axi0Unit)) {
                //axis is in degree, likely a geographic crs
                return EARTH_PERIMETER / 360d;
            } else {
                throw new IllegalArgumentException("Unsupported unit : " + axi0Unit);
            }
        }

        //convert 1 unit of the crs unit in meter
        return axi0Unit.getConverterTo(Units.METRE).convert(1);
    }

    private static double getGlobalCRS84PixelScale(double scaleDenominator) {
        return (PIXEL_SIZE * 1.118164528) * scaleDenominator / SCALE;
    }

}
