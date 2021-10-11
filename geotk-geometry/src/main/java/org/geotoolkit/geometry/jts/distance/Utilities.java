package org.geotoolkit.geometry.jts.distance;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Various utilities needed only for current package classes.
 *
 * @author Alexis Manin (Geomatys)
 */
final class Utilities {

    private Utilities() {
    }

    static final double PI_4 = Math.PI / 4;

    /**
     * Test if given CRS is longitude/latitude or latitude/longitude. Works
     * with ND geographic systems, as long as longitude and latitude axes can
     * be found.
     *
     * @param in The coordinate reference system
     * @return True if the first geographical axis of the given CRS is latitude.
     * False if the longitude appears before the latitude.
     * @throws IllegalArgumentException If neither latitude nor longitude axis
     * is found in input system.
     */
    static boolean isLatLon(final GeographicCRS in) throws IllegalArgumentException {
        final EllipsoidalCS cs = in.getCoordinateSystem();
        for (int i = 0 ; i < cs.getDimension() ; i++) {
            final AxisDirection axis = cs.getAxis(i).getDirection();
            // Most common cases
            if (AxisDirection.NORTH.equals(axis)) {
                return true;
            } else if (AxisDirection.EAST.equals(axis)) {
                return false;
                // less common
            } else if (AxisDirection.SOUTH.equals(axis)) {
                return true;
            } else if (AxisDirection.WEST.equals(axis)) {
                return false;
            }
        }

        throw new IllegalArgumentException("Given geographic CRS use neither north nor east axis directions.");
    }

    /**
     * Prepare the given coordinate in a geographic, longitude first CRS.
     *
     * @param base The coordinate to reproject.
     * @param tr The transform to apply (should be projected -> geographic).
     * @param flipAxes A flag indicating if the given transform produces
     * latitude first coordinates. If true, we'll inverse coordinates obtained
     * from the input transform.
     *
     * @return A longitude/latitude coordinate, never null.
     */
    static Coordinate transform(final Coordinate base, final MathTransform tr, final boolean flipAxes) {
        final double[] coords = {base.x, base.y};
        try {
            tr.transform(coords, 0, coords, 0, 1);
        } catch (TransformException ex) {
            throw new RuntimeException(ex);
        }

        return flipAxes
                ? new Coordinate(coords[1], coords[0])
                : new Coordinate(coords[0], coords[1]);
    }
}
