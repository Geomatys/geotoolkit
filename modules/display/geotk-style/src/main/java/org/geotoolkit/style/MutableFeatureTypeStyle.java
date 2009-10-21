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

import org.opengis.filter.Id;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.style.Description;
import org.opengis.style.FeatureTypeStyle;

/**
 * Mutable interface of geoAPI FeatureTypeStyle.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface MutableFeatureTypeStyle extends FeatureTypeStyle{

    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String IDS_PROPERTY = "ids";
    public static final String ONLINE_PROPERTY = "online";
    
    /**
     * Set the name of the feature type style.
     * This method is thread safe.
     */
    void setName(String name);
    
    /**
     * Set the Description of the feature type style.
     * @param desc : Description can't be null
     */
    void setDescription(Description desc);

    /**
     * Set the feature ids.
     */
    void setFeatureInstanceIDs(Id id);
    
    /**
     * {@inheritDoc }
     * @return List<Rule> : This is the "living" List.
     */
    @Override
    List<MutableRule> rules();

    /**
     * Set the online resource of this fts.
     * The onlineResource must be an xml file containing this fts.
     */
    void setOnlineResource(OnLineResource online);
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    void addListener(FeatureTypeStyleListener listener);
    
    void removeListener(FeatureTypeStyleListener listener);
    
}
