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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.util.collection.UnSynchronizedCache;

import org.opengis.feature.Feature;
import org.opengis.style.PointSymbolizer;

/**
 * Cached point symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedPointSymbolizer extends CachedSymbolizer<PointSymbolizer>{

    private final CachedGraphic cachedGraphic;
    private UnSynchronizedCache<Float,BufferedImage> cache = null;
        
    public CachedPointSymbolizer(PointSymbolizer point,
            SymbolizerRenderer<PointSymbolizer,? extends CachedSymbolizer<PointSymbolizer>> renderer){
        super(point,renderer);
        cachedGraphic = CachedGraphic.cache(point.getGraphic());
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
        if(!isNotEvaluated) return;

        //will call an evaluate indirectly
        if(cachedGraphic.isStatic()){
            //we can make a cache
            cache = new UnSynchronizedCache<Float, BufferedImage>(5);
        }

        cachedGraphic.getRequieredAttributsName(requieredAttributs);
        isStatic = cachedGraphic.isStatic();
        isStaticVisible = cachedGraphic.isStaticVisible();

        isNotEvaluated = false;
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
        evaluate();

        if(cache != null){
            //means the graphic is static, so we can cache fixed size images
            BufferedImage buffer = cache.get(coeff);
            if(buffer == null){
                buffer = cachedGraphic.getImage(feature, coeff, hints);
                cache.put(coeff, buffer);
            }

            return buffer;
        }

        //no cache recalculate image
        return cachedGraphic.getImage(feature, coeff, hints);
    }

    /**
     * return an Array of 2 floats always in display unit.
     */
    public float[] getDisplacement(Feature feature, float[] buffer){
        return cachedGraphic.getDisplacement(feature, buffer);
    }
    
    /**
     * return an Array of 2 floats.
     */
    public float[] getAnchor(Feature feature, float[] buffer){
        return cachedGraphic.getAnchor(feature,buffer);
    }
    
}
