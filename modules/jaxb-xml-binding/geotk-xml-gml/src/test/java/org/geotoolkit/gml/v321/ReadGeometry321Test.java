package org.geotoolkit.gml.v321;

import org.geotoolkit.gml.GeometryTransformer;
import org.geotoolkit.gml.ReadGeometryTest;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class ReadGeometry321Test extends ReadGeometryTest {

    @Test
    public void testMultiPolygon() throws Exception {
        testCollection(ReadGeometry321Test.class.getResource("polygons.gml"),
                MultiPolygon.class,
                71,
                new Envelope(99038, 399714, 6704170, 6879408)
        );
    }

    @Test
    public void testMultiLine() throws Exception {
        testCollection(ReadGeometry321Test.class.getResource("lines.gml"),
                MultiLineString.class,
                20,
                new Envelope(181899.675179, 197243.635526, 6850863.655278, 6869540.566522)
        );
    }

    @Test
    public void testMultiPoint() throws Exception {
        testCollection(ReadGeometry321Test.class.getResource("points.gml"),
                MultiPoint.class,
                3,
                new Envelope(223735.707508, 223735.707508, 6785396.537226, 6785396.537226)
        );
    }

    @Test
    public void testCurve() throws Exception {
        final AbstractGeometry geom = read(ReadGeometry321Test.class.getResource("curve.gml"));
        final GeometryTransformer tr = new GeometryTransformer(geom);
        final Geometry result = tr.get();
        Assert.assertNotNull("Read geometry is null", result);
        Assert.assertTrue(
                String.format(
                        "Bad geometry type.%nExpected: %s%nBut was: %s",
                        LineString.class, result.getClass()
                ),
                LineString.class.isAssignableFrom(result.getClass())
        );

        final LineString col = (LineString) result;

        Envelope expectedEnvelope = new Envelope(12.98241111111111, 13.462391666666665, -87.81824444444445, -87.25221944444445);
        final Envelope actual = col.getEnvelopeInternal();
        Assert.assertEquals(expectedEnvelope.getMinX(), actual.getMinX(), 0.01);
        Assert.assertEquals(expectedEnvelope.getMinY(), actual.getMinY(), 0.01);
        Assert.assertEquals(expectedEnvelope.getMaxX(), actual.getMaxX(), 0.01);
        Assert.assertEquals(expectedEnvelope.getMaxY(), actual.getMaxY(), 0.01);
    }

    @Test
    public void testSurface() throws Exception {
        testCollection(ReadGeometry321Test.class.getResource("surface.gml"),
                MultiPolygon.class,
                1,
                new Envelope(17.529157027777778, 17.58207613888889, -88.31997997222221, -88.14625286111112)
        );
    }

    @Test
    public void testArcSurface() throws Exception {
        testCollection(ReadGeometry321Test.class.getResource("surface_arc.gml"),
                MultiPolygon.class,
                1,
                new Envelope(13.760713201775618, 14.172372222222222, -87.03219444444444, -86.80048600888165)
        );
    }

    @Test
    public void testCircleSurface() throws Exception {
        testCollection(ReadGeometry321Test.class.getResource("surface_circle.gml"),
                MultiPolygon.class,
                1,
                new Envelope(13.849904545642142, 13.95021755003295, -87.18470851079748, -87.08195815586919)
        );
    }
}
