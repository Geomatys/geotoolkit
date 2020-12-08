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

import org.geotoolkit.test.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/**
 * Tests for GridAlignedFilter.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridAlignedFilterTest {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testPoint() {

        final Point geometry = GF.createPoint(new Coordinate(0.6, 1.4));

        geometry.apply(new GridAlignedFilter(0.5, -0.5, 1, 1));

        Assert.assertEquals(0.5, geometry.getX(), 0.0);
        Assert.assertEquals(1.5, geometry.getY(), 0.0);
    }

    @Test
    public void testLineString() {

        final LineString geometry = GF.createLineString(new Coordinate[]{
            new Coordinate(0.6, 1.4),
            new Coordinate(-2.3, -7.8),
            new Coordinate(6.1, 8.1),
        });

        geometry.apply(new GridAlignedFilter(0.5, -0.5, 1, 1));

        Coordinate c0 = geometry.getCoordinateN(0);
        Coordinate c1 = geometry.getCoordinateN(1);
        Coordinate c2 = geometry.getCoordinateN(2);

        Assert.assertEquals(0.5, c0.x, 0.0);
        Assert.assertEquals(1.5, c0.y, 0.0);
        Assert.assertEquals(-2.5, c1.x, 0.0);
        Assert.assertEquals(-7.5, c1.y, 0.0);
        Assert.assertEquals(6.5, c2.x, 0.0);
        Assert.assertEquals(8.5, c2.y, 0.0);
    }

}
