/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.geometry.jts.coordinatesequence;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFilter;

/**
 * Used to align geometry coordinates on a regular grid.
 * An instance of this class is thread-safe and concurrent.
 *
 * Coordinates of the geometry will be rounded to the nearest grid column
 * and row intersection.
 * And example of usage is integer rounding in mapbox vector tiles.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GridAlignedFilter implements CoordinateSequenceFilter {

    private final double offsetx;
    private final double offsety;
    private final double stepx;
    private final double stepy;

    /**
     * Grid aligned filter constructor.
     * X and Y origin are the coordinate of any point of the grid where
     * a column and a row intersect.
     * X and Y spacing define the regular distance between each column and row.
     *
     * @param originx alignement grid X origin
     * @param originy alignement grid Y origin
     * @param stepx alignement grid column spacing
     * @param stepy  alignement grid row spacing
     */
    public GridAlignedFilter(double originx, double originy, double stepx, double stepy) {
        this.offsetx = originx;
        this.offsety = originy;
        this.stepx = stepx;
        this.stepy = stepy;
    }

    @Override
    public void filter(CoordinateSequence seq, int i) {
        final Coordinate coordinate = seq.getCoordinate(i);
        final double x = Math.rint((coordinate.x - offsetx) / stepx);
        final double y = Math.rint((coordinate.y - offsety) / stepy);
        seq.setOrdinate(i, 0, stepx * x + offsetx);
        seq.setOrdinate(i, 1, stepy * y + offsety);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isGeometryChanged() {
        return true;
    }
}
