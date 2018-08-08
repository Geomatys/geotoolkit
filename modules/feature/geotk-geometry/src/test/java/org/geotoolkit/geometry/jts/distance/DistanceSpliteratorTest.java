package org.geotoolkit.geometry.jts.distance;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.StreamSupport;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.test.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DistanceSpliteratorTest {

    @Test
    public void testOrthodromicSegment() throws Exception {
        testSegments(CommonCRS.defaultGeographic(), true);
    }

    @Test
    public void testLoxodromicSegment() throws Exception {
        testSegments(CommonCRS.defaultGeographic(), false);
    }

    @Test
    public void testOrthodromicPolyline() throws Exception {
        testPolyline(CommonCRS.defaultGeographic(), true, true);
    }

    @Test
    public void testLoxodromicPolyline() throws Exception {
        testPolyline(CommonCRS.defaultGeographic(), false, true);
    }

    private void testSegments(final CoordinateReferenceSystem crs, final boolean isOrthodromic) throws IOException, FactoryException {
        final DistanceSpliterator.Builder builder = DistanceSpliterator.builder()
                .setCrs(crs);

        final UnaryOperator<Coordinate> pointTransformer = getTransformer(crs);
        final List<Segment> segments = Segment.loadTestData();
        for (Segment s : segments) {
            builder.setPolyline(new CoordinateArraySequence(new Coordinate[] {
                pointTransformer.apply(s.start),
                pointTransformer.apply(s.end)
            }, 2));
            final Spliterator.OfDouble ds = isOrthodromic? builder.buildOrthodromic() : builder.buildLoxodromic();
            Assert.assertEquals("Returned spliterator is not of expected type", DistanceSpliterator.class, ds.getClass());

            boolean segmentComputed = ds.tryAdvance((double val)
                    -> EngineTest.assertDistance(s, isOrthodromic, val));

            Assert.assertTrue("Segment length has not been computed", segmentComputed);

            ds.tryAdvance((double val)
                    -> org.junit.Assert.fail("A second computing has been done, despite the fact that we've got only one segment."));
        }
    }

    private void testPolyline(final CoordinateReferenceSystem crs, final boolean isOrthodromic, final boolean parallel) throws IOException, FactoryException {
        final DistanceSpliterator.Builder builder = DistanceSpliterator.builder()
                .setCrs(crs);

        final Travel t = fromSegments(crs);
        builder.setPolyline(t.polyline);
        final Spliterator.OfDouble ds = isOrthodromic? builder.buildOrthodromic() : builder.buildLoxodromic();

        final int ptNumber = t.polyline.size();
        final AtomicInteger count = new AtomicInteger();
        final double totalDistance = StreamSupport.doubleStream(ds, parallel)
                .peek(whatever -> count.incrementAndGet())
                .sum();
        Assert.assertEquals("Number of treated segments", ptNumber - 1, count.get());

        org.junit.Assert.assertEquals(
                "Computed distance",
                isOrthodromic? t.orthodromicDistance : t.loxodromicDistance,
                totalDistance / 1000, // meter to kilometer
                EngineTest.EPSI
        );
    }

    private static UnaryOperator<Coordinate> getTransformer(final CoordinateReferenceSystem target) throws FactoryException {
        if (org.apache.sis.util.Utilities.equalsApproximatively(target, CommonCRS.defaultGeographic())) {
            return UnaryOperator.identity();
        } else {
            final CoordinateOperation op = CRS.findOperation(CommonCRS.defaultGeographic(), target, null);
            final MathTransform geoToTarget = op.getMathTransform();
            return c -> Utilities.transform(c, geoToTarget, false);
        }
    }

    private static Travel fromSegments(final CoordinateReferenceSystem crs) throws IOException, FactoryException {
        final List<Segment> segments = Segment.loadTestData();
        final UnaryOperator<Coordinate> transformer = getTransformer(crs);
        final Coordinate[] polyline = new Coordinate[segments.size() + 1];
        polyline[0] = transformer.apply(segments.get(0).start);
        double od = 0, ld = 0;
        for (int i = 0 ; i < segments.size(); i++) {
            final Segment s = segments.get(i);
            if (!transformer.apply(s.start).equals2D(polyline[i], 1e-4)) {
                throw new IllegalStateException("Cannot use the segment file to build a polyline. The sequence of segments is disjointed.");
            }
            od += s.orthodromicDistance;
            ld += s.loxodromicDistance;
            polyline[i + 1] = transformer.apply(s.end);
        }

        return new Travel(new CoordinateArraySequence(polyline), od, ld);
    }

    private static class Travel {
        final CoordinateSequence polyline;
        final double orthodromicDistance;
        final double loxodromicDistance;

        public Travel(CoordinateSequence polyline, double orthodromicDistance, double loxodromicDistance) {
            this.polyline = polyline;
            this.orthodromicDistance = orthodromicDistance;
            this.loxodromicDistance = loxodromicDistance;
        }
    }
}
