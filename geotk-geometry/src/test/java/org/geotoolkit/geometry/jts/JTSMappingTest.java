/*
 * (C) 2021, Geomatys
 */
package org.geotoolkit.geometry.jts;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JTSMappingTest {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testLineStringToPolygon() {
        LineString line;
        Polygon polygon;

        //closed polygon should be ok
        line = GF.createLineString(new Coordinate[]{new Coordinate(0,0), new Coordinate(1,0), new Coordinate(1,1), new Coordinate(0,0)});
        polygon = JTSMapping.convertType(line, Polygon.class);
        Assert.assertNotNull(polygon);
        Assert.assertEquals(4, polygon.getExteriorRing().getCoordinateSequence().size());

        //empty line should produce an empty polygon
        line = GF.createLineString();
        polygon = JTSMapping.convertType(line, Polygon.class);
        Assert.assertNotNull(polygon);
        Assert.assertTrue(polygon.isEmpty());

        //unclosed line should result in an empty polygon
        line = GF.createLineString(new Coordinate[]{new Coordinate(0,0), new Coordinate(1,0), new Coordinate(1,1)});
        polygon = JTSMapping.convertType(line, Polygon.class);
        Assert.assertNotNull(polygon);
        Assert.assertTrue(polygon.isEmpty());


    }
}
