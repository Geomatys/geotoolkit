/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
 */
package org.geotoolkit.map;

import java.io.IOException;
import java.util.List;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Store context information about a map display.
 * This class does not store information about the map view.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface MapContext extends MapItem{

    public static final String AREA_OF_INTEREST_PROPERTY = "areaOfInterest";
    public static final String BOUNDS_PROPERTY = "bounds";

    /**
     * Set the context crs. This is used when asking for the bounds property.
     * This reproject the area of interest to the new crs.
     */
    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs);

    /**
     * Get the context crs. This is used when asking for the bounds property.
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();

    /**
     * Get the favorite visible area.
     */
    Envelope getAreaOfInterest();

    /**
     * Set the favorite visible area.
     */
    void setAreaOfInterest(Envelope aoi);

    /**
     * Returns the living list of all layers. You may add, remove or change layers
     * of this list.
     * @return the live list
     */
    List<MapLayer> layers();

    /**
     * convinient method to move a layer in the list from indexes.
     */
    void moveLayer(int begin,int end);

    /**
     * Return the enveloppe of all layers.
     */
    Envelope getBounds() throws IOException;

    /**
     * Register a context listener, this listener will be registered
     * also as a property change listener.
     * @param listener Context listener to register
     */
    void addContextListener(ContextListener listener);

    /**
     * Unregister a context listener, this listener will be registered
     * also as a property change listener.
     * @param listener Context listener to unregister
     */
    void removeContextListener(ContextListener listener);

}
