package org.geotoolkit.geometry.jts.distance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.function.UnaryOperator;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public abstract class EngineTest {

    static final double EPSI = 1;

    /**
     * @deprecated to be removed (replaced by {@link #EPSI}) after {@link GeodeticCalculator} implements ellipsoidal formulas.
     */
    @Deprecated
    static final double SPHERICAL_ERROR = 40;       // In metres.

    abstract ToDoubleBiFunction<Coordinate, Coordinate> getEngine(final CoordinateReferenceSystem target);

    abstract boolean isOrthodromic();

    @Test
    public void testLonLat() {
        test(CommonCRS.defaultGeographic(), UnaryOperator.identity());
    }

    @Test
    public void testLatLon() {
        test(CommonCRS.WGS84.geographic(), c -> new Coordinate(c.y, c.x));
    }

    @Test
    public void testMercator() throws FactoryException {
        testProjection(CRS.forCode("EPSG:3395"));
    }

    private void testProjection(final CoordinateReferenceSystem proj) throws FactoryException {
        final CoordinateOperation op = CRS.findOperation(CommonCRS.defaultGeographic(), proj, null);
        final MathTransform geoToProj = op.getMathTransform();

        test(proj, c -> Utilities.transform(c, geoToProj, false));
    }

    private void test(final CoordinateReferenceSystem crs, final UnaryOperator<Coordinate> pointTransform) {
        final ToDoubleBiFunction<Coordinate, Coordinate> engine = getEngine(crs);
        final List<Segment> segments;
        try {
            segments = Segment.loadTestData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        for (final Segment s : segments) {
            final double distance = engine.applyAsDouble(pointTransform.apply(s.start), pointTransform.apply(s.end));
            assertDistance(s, isOrthodromic(), distance);
        }
    }

    static void assertDistance(final Segment source, final boolean isOrthodromic, final double computedValue) {
        final String msgFormat = (isOrthodromic ? "Orthodromic" : "Loxodromic") + " distance error for %s";
        Assert.assertEquals(
                String.format(msgFormat, source.title),
                isOrthodromic ? source.orthodromicDistance : source.loxodromicDistance,
                computedValue / 1000, // meter to kilometer
                isOrthodromic ? SPHERICAL_ERROR : EPSI
        );
    }
}
