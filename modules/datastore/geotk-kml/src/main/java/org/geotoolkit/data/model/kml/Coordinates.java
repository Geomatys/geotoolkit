package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <br />&lt;simpleType name="coordinatesType">
 * <br />&lt;list itemType="string"/>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public interface Coordinates extends SimpleType {

    /**
     *
     * @return the lis of coordinates.
     */
    public List<Coordinate> getCoordinates();

    /**
     *
     * @param i the potition of the required coordinate in the list.
     * @return a specific coordinate.
     */
    public Coordinate getCoordinate(int i);

    /**
     *
     * @return the coordinate String in KML (XML) format.
     */
    public String getCoordinatesString();
}
