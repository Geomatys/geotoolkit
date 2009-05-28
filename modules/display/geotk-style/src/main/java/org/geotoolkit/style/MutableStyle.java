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
package org.geotoolkit.style;

import java.util.List;

import org.geotoolkit.sld.MutableLayerStyle;
import org.opengis.style.Description;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;

/**
 * Mutable interface of geoAPI Style.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface MutableStyle extends MutableLayerStyle,Style{

    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String DEFAULT_SPECIFICATION_PROPERTY = "defaultSpecification";
    public static final String ISDEFAULT_PROPERTY = "isDefault";
    
    /**
     * @return live list
     */
    @Override
    List<MutableFeatureTypeStyle> featureTypeStyles();

    void setDefaultSpecification(Symbolizer symbol);

    /**
     * Set the name of the style.
     * This method is thread safe.
     */
    void setName(String name);
    
    /**
     * Set the Description of the style.
     * @param desc : Description can't be null
     */
    void setDescription(Description desc);

    void setDefault(boolean isDefault);
    

    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    void addListener(StyleListener listener);
    
    void removeListener(StyleListener listener);
    
}
