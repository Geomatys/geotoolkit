/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.style;


import org.opengis.feature.Feature;
import org.opengis.style.GraphicStroke;
import org.opengis.style.Stroke;


/**
 * The cached stroke is like other cached symbol a class
 * that evaluate the given symbol and cache every possible value.
 * It also provide Java2D methods to grab a Stroke,Paint and Composite
 * for a given feature.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class CachedStroke extends Cache<Stroke>{

    protected CachedStroke(Stroke stroke){
        super(stroke);
    }

    public static CachedStroke cache(Stroke stroke){
        final GraphicStroke gs = stroke.getGraphicStroke();
        if(gs == null){
            //simple stroke
            return new CachedStrokeSimple(stroke);
        }else{
            return new CachedStrokeGraphic(stroke);
        }
    }

    /**
     * Get the maximum size of the symbol for the given feature.
     * This is used to calculate the display boundingbox of a feature.
     *
     * @param feature : evaluate symbol margin with the given feature
     * @param coeff : use to adjust stroke size, if in display unit value equals 1
     * @return max width of this symbol with the given feature
     */
    public abstract float getMargin(Feature feature, float coeff);


}
