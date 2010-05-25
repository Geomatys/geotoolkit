package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Coordinates extends SimpleType {

    public List<Coordinate> getCoordinates();
    public Coordinate getCoordinate(int i);
    public String getCoordinatesString();
}
