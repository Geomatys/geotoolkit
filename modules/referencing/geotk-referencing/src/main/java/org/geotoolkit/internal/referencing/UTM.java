/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains formulas from the PROJ package of USGS.
 *    USGS's work is fully acknowledged here. This derived work has
 *    been relicensed under LGPL with Frank Warmerdam's permission.
 */
package org.geotoolkit.internal.referencing;

import org.geotoolkit.resources.Errors;

import static java.lang.Math.*;


/**
 * <cite>Transverse Mercator</cite> convenience methods.
 */
public final class UTM {
    /**
     * Maximum difference allowed when comparing longitudes or latitudes in radians.
     */
    private static final double ANGLE_TOLERANCE = 1E-6;

    private UTM() {
    }

    /**
     * Convenience method computing the zone code from the central meridian.
     * Information about zones convention must be specified in argument. Two
     * widely set of arguments are of Universal Transverse Mercator (UTM) and
     * Modified Transverse Mercator (MTM) projections:
     * <p>
     * UTM projection (zones numbered from 1 to 60):
     *
     * {@preformat java
     *     getZone(-177, 6);
     * }
     *
     * MTM projection (zones numbered from 1 to 120):
     *
     * {@preformat java
     *     getZone(-52.5, -3);
     * }
     *
     * @param  centralLongitudeZone1 Longitude in the middle of zone 1, in decimal degrees
     *         relative to Greenwich. Positive longitudes are toward east, and negative
     *         longitudes toward west.
     * @param  zoneWidth Number of degrees of longitudes in one zone. A positive value
     *         means that zones are numbered from west to east (i.e. in the direction of
     *         positive longitudes). A negative value means that zones are numbered from
     *         east to west.
     * @return The zone number. First zone is numbered 1.
     */
    private static int computeZone(final double centralMeridian, final double centralLongitudeZone1, final double zoneWidth) {
        final double zoneCount = abs(360 / zoneWidth);
        double t;
        t  = centralLongitudeZone1 - 0.5*zoneWidth; // Longitude at the beginning of the first zone.
        t  = toDegrees(centralMeridian) - t;        // Degrees of longitude between the central longitude and longitude 1.
        t  = floor(t/zoneWidth + ANGLE_TOLERANCE);  // Number of zones between the central longitude and longitude 1.
        t -= zoneCount*floor(t/zoneCount);          // If negative, bring back to the interval 0 to (zoneCount-1).
        return ((int) t)+1;
    }

    /**
     * Convenience method returning the meridian in the middle of current zone. This meridian is
     * typically the central meridian. This method may be invoked to make sure that the central
     * meridian is correctly set.
     *
     * @param  centralLongitudeZone1 Longitude in the middle of zone 1, in decimal degrees
     *         relative to Greenwich. Positive longitudes are toward east, and negative
     *         longitudes toward west.
     * @param  zoneWidth Number of degrees of longitudes in one zone. A positive value
     *         means that zones are numbered from west to east (i.e. in the direction of
     *         positive longitudes). A negative value means that zones are numbered from
     *         east to west.
     * @return The central meridian.
     */
    private static double computeCentralMedirian(final double centralMeridian, final double centralLongitudeZone1, final double zoneWidth) {
        double t;
        t  = centralLongitudeZone1 + (getZone(centralMeridian, centralLongitudeZone1, zoneWidth)-1)*zoneWidth;
        t -= 360 * floor((t+180) / 360); // Bring back into [-180..+180] range.
        return t;
    }

    /**
     * Convenience method computing the zone code from the central meridian. This method uses
     * the {@linkplain #scaleFactor scale factor} and {@linkplain #falseEasting false easting}
     * to decide if this is a UTM or MTM case.
     *
     * @return The zone number. Numbering starts at 1.
     * @throws IllegalStateException if the case of the projection cannot be determined.
     */
    private static int getZone(final double centralMeridian, final double scaleFactor, final double falseEasting) throws IllegalStateException {
        // UTM
        if (scaleFactor == 0.9996 && falseEasting == 500000) {
            return computeZone(centralMeridian, -177, 6);
        }
        // MTM
        if (scaleFactor == 0.9999 && falseEasting == 304800){
            return computeZone(centralMeridian, -52.5, -3);
        }
        // unknown
        throw new IllegalStateException(Errors.format(Errors.Keys.UnknownProjectionType));
    }

    /**
     * Convenience method returning the meridian in the middle of current zone. This meridian is
     * typically the central meridian. This method may be invoked to make sure that the central
     * meridian is correctly set.
     * <p>
     * This method uses the {@linkplain #scaleFactor scale factor} and {@linkplain #falseEasting
     * false easting} to decide if this is a UTM or MTM case.
     *
     * @return The central meridian, in decimal degrees.
     * @throws IllegalStateException if the case of the projection cannot be determined.
     */
    private static double getCentralMeridian(final double centralMeridian, final double scaleFactor, final double falseEasting) throws IllegalStateException {
        // UTM
        if (scaleFactor == 0.9996 && falseEasting == 500000) {
            return computeCentralMedirian(centralMeridian, -177, 6);
        }
        // MTM
        if (scaleFactor == 0.9999 && falseEasting == 304800){
            return computeCentralMedirian(centralMeridian, -52.5, -3);
        }
        // unknown
        throw new IllegalStateException(Errors.format(Errors.Keys.UnknownProjectionType));
    }
}
