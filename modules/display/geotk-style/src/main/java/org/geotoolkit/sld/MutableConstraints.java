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

import org.geotoolkit.style.CollectionChangeListener;
import org.opengis.sld.Constraint;
import org.opengis.sld.Constraints;

/**
 * Super class for mutable constraints : MutableLayerFeatureConstraints and 
 * MutableLayerCoverageConstraints
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface MutableConstraints extends Constraints{

    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    void addListener(CollectionChangeListener<? extends Constraint> listener);
    
    void removeListener(CollectionChangeListener<? extends Constraint> listener);
    
}
