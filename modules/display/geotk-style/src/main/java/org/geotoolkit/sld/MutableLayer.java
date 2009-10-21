/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sld;

import org.opengis.sld.Layer;
import org.opengis.style.Description;

/**
 * Common mutable interface for MutableNamedLayer and MutableUserLayer
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface MutableLayer extends Layer{

    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    
    /**
     * Set the name of the sld.
     * @param name : new name
     */
    void setName(String name);
    
    /**
     * Set the description of the sld.
     * @param description : new description
     */
    void setDescription(Description description);
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    void addListener(LayerListener listener);
    
    void removeListener(LayerListener listener);
    
}
