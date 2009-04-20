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


import java.util.List;

import org.opengis.filter.Filter;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.style.Description;
import org.opengis.style.GraphicLegend;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 * Mutable interface of geoAPI Rule. 
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface MutableRule extends Rule{

    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String LEGEND_PROPERTY = "legend";
    public static final String FILTER_PROPERTY = "filter";
    public static final String ISELSE_FILTER_PROPERTY = "iselse";
    public static final String MINIMUM_SCALE_PROPERTY = "minscale";
    public static final String MAXIMUM_SCALE_PROPERTY = "maxscale";
    public static final String ONLINE_PROPERTY = "online";
    
    /**
     * Set the name of the rule.
     */
    void setName(String name);
    
    /**
     * Set the Description of the rule.
     * @param desc : Description can't be null
     */
    void setDescription(Description desc);
    
    /**
     * Set the graphic legend of the rule.
     * @param legend : can be null.
     */
    void setLegendGraphic(GraphicLegend legend);
        
    /**
     * Set the feature filter of the rule.
     * The filter will limit the features that will be displayed
     * using the underneath symbolizers.
     * 
     * @param filter : can be null.
     */
    void setFilter(Filter filter);
    
    /**
     * Set the "else" flag of the filter.
     * If a ruma has this flag then it will used only for the
     * feature that no other rule handle.
     * 
     */
    void setElseFilter(boolean isElse);
    
    /**
     * Set the minimum scale on wich this rul apply.
     * if the display device is under this scale then this rule
     * will not be tested.
     */
    void setMinScaleDenominator(double minScale);
    
    /**
     * Set the maximum scale on wich this rul apply.
     * if the display device is above this scale then this rule
     * will not be tested.
     */
    void setMaxScaleDenominator(double maxScale);

    @Override
    List<Symbolizer> symbolizers();
    
    /**
     * Set the online resource of this Rule.
     * The onlineResource must be an xml file containing this rule.
     */
    void setOnlineResource(OnLineResource online);
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    void addListener(RuleListener listener);
    
    void removeListener(RuleListener listener);
    
}
