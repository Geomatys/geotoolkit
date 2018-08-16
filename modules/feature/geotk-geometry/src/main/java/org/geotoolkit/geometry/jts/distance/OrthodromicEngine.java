package org.geotoolkit.geometry.jts.distance;

import java.util.function.ToDoubleBiFunction;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
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

    private final ToDoubleBiFunction<Coordinate, Coordinate> strategy;

    /**
     * Prepare a calculator for points expressed in input coordinate system.
     *
     * @param crs The coordinate reference system of future given points.
     */
    OrthodromicEngine(final CoordinateReferenceSystem crs) {
        engine = new GeodeticCalculator(crs);
        if (crs instanceof GeographicCRS) {
            if (Utilities.isLatLon((GeographicCRS)crs)) {
                strategy = this::computeGeoLatLon;
            } else {
                strategy = this::computeGeo;
            }
        } else strategy = this::compute;
    }

    /**
     * Compute distance between two points in non-geographic CRS.
     *
     * @param start The point representing the start of the segment.
     * @param end The point representing the end of the segment.
     * @return The orthodromic distance between two points, in meter.
     */
    private double compute(final Coordinate start, final Coordinate end) {
        try {
            engine.setStartingPosition(new DirectPosition2D(engine.getCoordinateReferenceSystem(), start.x, start.y));
            engine.setDestinationPosition(new DirectPosition2D(engine.getCoordinateReferenceSystem(), end.x, end.y));
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
        return engine.getOrthodromicDistance();
    }

    /**
     * Same aim as {@link #compute(org.locationtech.jts.geom.Coordinate, org.locationtech.jts.geom.Coordinate)
     * }, but specialized for geographic longitude/latitude CRS.
     *
     * @param start The geographic point representing the start of the segment.
     * @param end The geographic point representing the end of the segment.
     * @return The orthodromic distance between two points, in meter.
     */
    private double computeGeo(final Coordinate start, final Coordinate end) {
        engine.setStartingGeographicPoint(start.x, start.y);
        engine.setDestinationGeographicPoint(end.x, end.y);
        return engine.getOrthodromicDistance();
    }

    /**
     * Same aim as {@link #computeGeo(org.locationtech.jts.geom.Coordinate, org.locationtech.jts.geom.Coordinate)
     * }, but specialized for latitude/longitude CRS.
     *
     * @param start The geographic point representing the start of the segment.
     * @param end The geographic point representing the end of the segment.
     * @return The orthodromic distance between two points, in meter.
     */
    private double computeGeoLatLon(final Coordinate start, final Coordinate end) {
        engine.setStartingGeographicPoint(start.y, start.x);
        engine.setDestinationGeographicPoint(end.y, end.x);
        return engine.getOrthodromicDistance();
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
        return strategy.applyAsDouble(start, end);
    }
}
