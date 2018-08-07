package org.geotoolkit.geometry.jts.distance;

import javax.measure.UnitConverter;
import org.apache.sis.measure.Units;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.EllipsoidalCS;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
class Utilities {

    static final double PI_4 = Math.PI / 4;

    static final UnitConverter DEGREE_TO_RADIAN = Units.DEGREE.getConverterTo(Units.RADIAN);

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
            } else if (AxisDirection.EAST.equals(i)) {
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
}
