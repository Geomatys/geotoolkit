/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.awt.geom.Point2D;
import java.time.Instant;
import java.util.Optional;
import javax.vecmath.Vector2d;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

/**
 * Describes a data providing 2D vectors describing a force along {@link AxisDirection#EAST} and
 * {@link AxisDirection#NORTH}.
 *
 * The aim is to provide a step by step approach, to be simple to use by drift predictor, and allow to provide proper
 * separation of concerns to implementors.
 *
 * @author Alexis Manin (Geomatys)
 */
public interface UVSource {

    /**
     *
     * @return Coordinate system of the data providing vectors. Should at least contain an horizontal 2D component, and
     * also a temporal axis.
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();

    /**
     * Configure a subset with data available for given position surrounding. The aim is to prepare data to be able to
     * fetch values from the instant described by given point until an unknown future, and providing data around the
     * area described by the point, as the drift will start from there.
     *
     * @param origin The point from which the drift will start. Could be 2D, or 3D with time, or 4D with elevation.
     * @return If any data is available for given area and (optionally) time, return an object ready to transmit data.
     * Otherwise, give back an empty optional to notify that no data can be acquired from this data source for the given
     * location.
     */
    Optional<TimeSet> atOrigin(final DirectPosition origin);

    /**
     * Gives back slices of data for specified instants in time.
     */
    public static interface TimeSet {
        /**
         * Queries data at a given time.
         * @param target The time we want to extract data for.
         * @return An object giving 2D data at a fix time, or an empty optional to notify that no data is available for
         * time given as input.
         */
        Optional<Calibration2D> setTime(final Instant target);
    }

    public static interface Calibration2D {
        /**
         * Focus view on specified area. The aim is to consider only the horizontal part of input envelope, as time and
         * elevation components should have been fixed by previous calls to {@link TimeSet#setTime(java.time.Instant) }
         * and {@link UVSource#atOrigin(org.opengis.geometry.DirectPosition) } respectively.
         *
         * @param target The envelope describing the zone to focus on for data acquisition.
         *
         * @return A 2D dataset. Should never be null, because if no data is available, it should have been notified
         * through {@link TimeSet } and {@link UVSource}.
         */
        Snapshot setHorizontalComponent(final Envelope target);
    }

    public static interface Snapshot {
        Optional<Vector2d> evaluate(final Point2D.Double location);
    }
}
