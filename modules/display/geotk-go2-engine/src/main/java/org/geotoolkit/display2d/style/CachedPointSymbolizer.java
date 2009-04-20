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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.opengis.feature.Feature;
import org.opengis.style.PointSymbolizer;

/**
 * Cached point symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CachedPointSymbolizer extends CachedSymbolizer<PointSymbolizer>{

    private final PointSymbolizer point;
    private final CachedGraphic cachedGraphic;
        
    public CachedPointSymbolizer(PointSymbolizer point){
        super(point);
        cachedGraphic = new CachedGraphic(point.getGraphic());
        this.point = point;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Feature feature, float coeff) {
        return cachedGraphic.getMargin(feature, coeff);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        cachedGraphic.evaluate();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(Feature feature) {
        return cachedGraphic.isVisible(feature);
    }
    
    /**
     * 
     * @return BufferedImage for a feature 
     */
    public BufferedImage getImage(Feature feature, final float coeff, RenderingHints hints) {
        return cachedGraphic.getImage(feature, coeff, hints);
    }

    /**
     * return an Array of 2 floats always in display unit.
     */
    public float[] getDisplacement(Feature feature){
        return cachedGraphic.getDisplacement(feature);
    }
    
    /**
     * return an Array of 2 floats.
     */
    public float[] getAnchor(Feature feature){
        return cachedGraphic.getAnchor(feature);
    }
    
}
