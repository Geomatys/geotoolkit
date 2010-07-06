package org.geotoolkit.data.kml.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCoordinates extends CoordinateArraySequence implements Coordinates{

    /**
     * 
     * @param coordinates
     */
    public DefaultCoordinates(Coordinate[] coordinates){
        super(coordinates);
    }
}
