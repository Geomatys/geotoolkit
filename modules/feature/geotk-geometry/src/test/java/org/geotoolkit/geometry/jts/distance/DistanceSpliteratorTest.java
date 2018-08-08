package org.geotoolkit.geometry.jts.distance;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.test.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DistanceSpliteratorTest {

    @Test
    public void testOrthodromicSegment() throws IOException {
        testSegments(CommonCRS.defaultGeographic(), true);
    }

    @Test
    public void testLoxodromicSegment() throws IOException {
        testSegments(CommonCRS.defaultGeographic(), false);
    }

    private void testSegments(final CoordinateReferenceSystem crs, final boolean isOrthodromic) throws IOException {
        final DistanceSpliterator.Builder builder = DistanceSpliterator.builder()
                .setCrs(crs);

        final List<Segment> segments = Segment.loadTestData();
        for (Segment s : segments) {
            builder.setPolyline(new CoordinateArraySequence(new Coordinate[]{s.start, s.end}, 2));
            final Spliterator.OfDouble ds = isOrthodromic? builder.buildOrthodromic() : builder.buildLoxodromic();
            Assert.assertEquals("Returned spliterator is not of expected type", DistanceSpliterator.class, ds.getClass());

            boolean segmentComputed = ds.tryAdvance((double val)
                    -> EngineTest.assertDistance(s, isOrthodromic, val));

            Assert.assertTrue("Segment length has not been computed", segmentComputed);

            ds.tryAdvance((double val)
                    -> org.junit.Assert.fail("A second computing has been done, despite the fact that we've got only one segment."));
        }
    }

    private void testPolyline(final CoordinateReferenceSystem crs, final boolean isOrthodromic, final boolean parallel) {
        final DistanceSpliterator.Builder builder = DistanceSpliterator.builder()
                .setCrs(crs);

        final CoordinateSequence seq = null; // TODO
        builder.setPolyline(seq);
        final Spliterator.OfDouble ds = isOrthodromic? builder.buildOrthodromic() : builder.buildLoxodromic();

        final int ptNumber = seq.size();
        final AtomicInteger count = new AtomicInteger();
        final double totalDistance = StreamSupport.doubleStream(ds, parallel)
                .peek(whatever -> count.incrementAndGet())
                .sum();
        Assert.assertEquals("Number of treated segments", ptNumber - 1, count.get());

        org.junit.Assert.assertEquals(
                "Computed distance",
                0, // TODO
                totalDistance,
                EngineTest.EPSI
        );
    }
}
