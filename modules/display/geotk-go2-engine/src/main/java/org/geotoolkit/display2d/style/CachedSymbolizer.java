/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
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
package org.geotoolkit.display2d.style;


import org.opengis.feature.Feature;
import org.opengis.style.Symbolizer;

/**
 * This is a general interface for cached symbolizers.<br>
 * - CachedLineSymbolizer<br>
 * - CachedPointSymbolizer<br>
 * - CachedPolygonSymbolizer<br>
 * - CachedRasterSymbolizer<br>
 * - CachedTextSymbolizer<br>
 * This interface provide commun methods.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class CachedSymbolizer<T extends Symbolizer> extends Cache<T>{
       
    public CachedSymbolizer(T styleElement){
        super(styleElement);
    }
            
    /**
     * Get the maximum size of the symbol for the given feature.
     * This is used to calculate the display boundingbox of a feature.
     * 
     * @param feature : feature to evaluate
     * @param coeff : use to adjust symbolizer size, if in display unit value equals 1
     * @return max width of this symbol with the given feature
     */
    public abstract float getMargin(Feature feature, float coeff);
    
}
