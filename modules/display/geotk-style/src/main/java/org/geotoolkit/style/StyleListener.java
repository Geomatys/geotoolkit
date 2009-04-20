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

package org.geotoolkit.style;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;


/**
 * Listener for Style.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface StyleListener extends EventListener{

    /**
     * Called when a property change.
     * Same as a bean property change.
     */
    void propertyChange(PropertyChangeEvent event);
    
    /**
     * Called when a change occures in the living feature type style collection.
     */
    void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event);
    
}
