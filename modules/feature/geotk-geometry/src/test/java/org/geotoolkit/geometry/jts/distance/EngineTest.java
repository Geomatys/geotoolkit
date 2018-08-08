package org.geotoolkit.geometry.jts.distance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public abstract class EngineTest {

    private static final double EPSI = 1;

    abstract ToDoubleBiFunction<Coordinate, Coordinate> getEngine(final CoordinateReferenceSystem target);

    abstract boolean isOrthodromic();

    @Test
    public void testLonLat() {
        test(CommonCRS.defaultGeographic(), UnaryOperator.identity());
    }

    private void test(final CoordinateReferenceSystem crs, final UnaryOperator<Coordinate> pointTransform) {
        final ToDoubleBiFunction<Coordinate, Coordinate> engine = getEngine(crs);
        final List<Segment> segments;
        try {
            segments = Segment.loadTestData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        final ToDoubleFunction<Segment> expected = isOrthodromic()?
                s -> s.orthodromicDistance : s -> s.loxodromicDistance;
        final String msgFormat = (isOrthodromic()? "Orthodromic" : "Loxodromic") + " distance error for %s";

        for (final Segment s : segments) {
            Assert.assertEquals(
                    String.format(msgFormat, s.title),
                    expected.applyAsDouble(s),
                    engine.applyAsDouble(
                            pointTransform.apply(s.start),
                            pointTransform.apply(s.end)
                    ),
                    EPSI
            );
        }
    }
}
