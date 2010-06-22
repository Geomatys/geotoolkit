package org.geotoolkit.data.kml.model;

/**
 *
 * <p>This interface do not map an element.</p>
 *
 * <p>A corodinate is a part of a String that may contain
 * lots of coordinates. This interface maps a part of the string
 * between two spaces (a space character represents an XML list
 * separator).</p>
 *
 * @author Samuel Andrés
 */
public interface Coordinate {

    /**
     *
     * @return the geodetic longitude.
     */
    public double getGeodeticLongitude();

    /**
     *
     * @return the geodetic latitude.
     */
    public double getGeodeticLatitude();

    /**
     *
     * @return the altitude.
     */
    public double getAltitude();

    /**
     * <p>This method transforms a coordinate object
     * into a string. Dimensions are coma separated.</p>
     *
     * @return the coordinate string.
     */
    public String getCoordinateString();

}
