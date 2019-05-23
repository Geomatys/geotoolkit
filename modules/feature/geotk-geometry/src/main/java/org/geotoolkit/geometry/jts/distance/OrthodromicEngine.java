package org.geotoolkit.geometry.jts.distance;

import java.util.function.ToDoubleBiFunction;
import javax.measure.UnitConverter;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * A simple wrapper around {@link GeodeticCalculator} to simplify orthodromic
 * distance computing for JTS data.
 *
 * All results are in meter.
 *
 * @implNote NOT thread-safe.
 *
 * @author Alexis Manin (Geomatys)
 */
class OrthodromicEngine implements ToDoubleBiFunction<Coordinate, Coordinate> {
    private final GeodeticCalculator engine;

    private final UnitConverter toMeters;

    /**
     * Prepare a calculator for points expressed in input coordinate system.
     *
     * @param crs The coordinate reference system of future given points.
     */
    OrthodromicEngine(final CoordinateReferenceSystem crs) {
        engine = GeodeticCalculator.create(crs);
        toMeters = engine.getDistanceUnit().getConverterTo(Units.METRE);
    }

    /**
     * Compute orthodromic distance between two points whose CRS is the one
     * given at built.
     *
     * @param start The point representing the start of the segment.
     * @param end The point representing the end of the segment.
     * @return The orthodromic distance between two points, in meter.
     */
    @Override
    public double applyAsDouble(Coordinate start, Coordinate end) {
        try {
            engine.setStartPoint(new DirectPosition2D(start.x, start.y));
            engine.setEndPoint(new DirectPosition2D(end.x, end.y));
        } catch (TransformException e) {
            throw new IllegalArgumentException(e);
        }
        return toMeters.convert(engine.getGeodesicDistance());
    }
}
