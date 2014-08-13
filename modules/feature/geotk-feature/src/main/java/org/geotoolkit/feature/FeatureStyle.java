/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.feature;

import java.util.Arrays;
import java.util.List;
import org.opengis.style.Symbolizer;

/**
 * Data representation can be achieved in many ways.
 * 
 * Following the OGC SLD/SE specification the styling informations should be separate
 * from the 'features', this is rule based styling where features do not know anything
 * about their rendering.
 * Example : Shapefile, Postgis, CSV, GPX ...
 * 
 * On the other hand some formats define a style for each feature, those should use this
 * interface.
 * Example : KML, MIF/MID, DGN, DXF, DWG, ...
 *  
 * TODO : this is stored in the feature user data but it is not the right place.
 * Should it be a property ?
 * Should it be a sub-interface ?
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureStyle {
    
    public static final String PROPERY_KEY = "featureStyle";
    
    private final List<Symbolizer> symbolizers;

    public FeatureStyle() {
        this.symbolizers = null;
    }

    public FeatureStyle(List<Symbolizer> symbolizers) {
        this.symbolizers = symbolizers;
    }
    
    public FeatureStyle(Symbolizer ... symbols){
        this.symbolizers = Arrays.asList(symbols);
    }
    
    /**
     * Get the list of symbolizers applied to this feature.
     * 
     * @return List of symbolizer, never null, can be empty.
     */
    public List<Symbolizer> getSymbolizers(){
        return symbolizers;
    }
    
}
