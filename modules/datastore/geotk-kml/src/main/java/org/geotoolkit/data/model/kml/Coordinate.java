package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public interface Coordinate {

    public double getGeodeticLongitude();
    public double getGeodeticLatitude();
    public double getAltitude();
    public String getCoordinateString();

}
