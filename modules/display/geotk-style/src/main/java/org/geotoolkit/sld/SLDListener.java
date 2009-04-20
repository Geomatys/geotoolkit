/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.sld;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;

import org.geotoolkit.style.CollectionChangeEvent;
import org.opengis.sld.SLDLibrary;

/**
 * Listener for style layer descriptor.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface SLDListener extends EventListener{

    /**
     * Called when a property change.
     * Same as a bean property change.
     */
    void propertyChange(PropertyChangeEvent event);
    
    /**
     * Called when a change occurs in the living layer collection.
     */
    void layerChange(CollectionChangeEvent<MutableLayer> event);
    
    /**
     * Called when a change occurs in the living SLDLibrary collection.
     */
    void libraryChange(CollectionChangeEvent<SLDLibrary> event);
    
}
