package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * <pre>
 * &lt;simpleType name="coordinatesType">
 *  &lt;list itemType="string"/>
 * &lt;/simpleType>
 * </pre>
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
