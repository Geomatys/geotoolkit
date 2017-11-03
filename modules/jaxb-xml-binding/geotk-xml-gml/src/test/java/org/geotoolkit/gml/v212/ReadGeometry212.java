package org.geotoolkit.gml.v212;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotoolkit.gml.ReadGeometryTest;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class ReadGeometry212 extends ReadGeometryTest {

    @Test
    public void testMultiPolygon() throws Exception {
        testCollection(
                ReadGeometry212.class.getResource("polygons.gml"),
                MultiPolygon.class,
                108,
                new Envelope(99038, 399714, 6704170, 6879408)
        );
    }

    @Test
    public void testMultiLine() throws Exception {
        testCollection(
                ReadGeometry212.class.getResource("lines.gml"),
                MultiLineString.class,
                20,
                new Envelope(181899.6752, 197243.6355, 6850863.6553, 6869540.5665)
        );
    }

    @Test
    public void testMultiPoint() throws Exception {
        testCollection(
                ReadGeometry212.class.getResource("points.gml"),
                MultiPoint.class,
                3,
                new Envelope(223735.7075, 223735.7075, 6785396.5372, 6785396.5372)
        );
    }
}