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

import java.util.List;
import org.opengis.sld.SLDLibrary;
import org.opengis.sld.StyledLayerDescriptor;
import org.opengis.style.Description;
import org.opengis.util.Cloneable;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface MutableStyledLayerDescriptor extends StyledLayerDescriptor, Cloneable{
    
    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String VERSION_PROPERTY = "version";
    
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
            
    List<SLDLibrary> libraries();
    
    List<MutableLayer> layers();
    
    /**
     * Set the version of the sld
     * @param version : new version
     */
    void setVersion(String version);

    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    void addListener(SLDListener listener);
    
    void removeListener(SLDListener listener);
    
}
