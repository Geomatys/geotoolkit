package org.geotoolkit.geometry.jts.distance;

import java.util.function.ToDoubleBiFunction;
import javax.measure.UnitConverter;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.GeodeticCalculator;
import org.apache.sis.util.ArgumentChecks;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Loxodromic distance calculus.
 *
 * Points given to this computer must all be on the coordinate system given at
 * build time.
 *
 * All results are in meter.
 *
 * @implNote NOT thread-safe.
 *
 * @author Alexis Manin (Geomatys)
 */
public class LoxodromicEngine implements ToDoubleBiFunction<Coordinate, Coordinate> {
    private final GeodeticCalculator engine;

    private final UnitConverter toMeters;

    /**
     * Prepare the engine to work with points in the specified coordinate
     * reference system.
     *
     * @param crs The coordinate reference system of future given points.
     */
    LoxodromicEngine(final CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureDimensionMatches("Geometry CRS", 2, crs);
        engine = GeodeticCalculator.create(crs);
        toMeters = engine.getDistanceUnit().getConverterTo(Units.METRE);
    }

    /**
     * Compute loxodromic distance between the given points.
     *
     * WARNING: Given coordinates must be expressed in the same CRS as the one
     * given at build time.
     *
     * @param start First point of the segment
     * @param end Last point of the segment.
     * @return The distance of the given segment, in meters.
     */
    @Override
    public double applyAsDouble(Coordinate start, Coordinate end) {
        engine.setStartPoint(new DirectPosition2D(start.x, start.y));
        engine.setEndPoint(new DirectPosition2D(end.x, end.y));
        return toMeters.convert(engine.getRhumblineLength());
    }
}
