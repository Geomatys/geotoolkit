/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.util.collection.UnSynchronizedCache;
import org.opengis.style.PointSymbolizer;

/**
 * Cached point symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedPointSymbolizer extends CachedSymbolizer<PointSymbolizer>{

    private final CachedGraphic cachedGraphic;
    private UnSynchronizedCache<Float,BufferedImage> cacheWithRotation = null;
    private UnSynchronizedCache<Float,BufferedImage> cacheWithoutRotation = null;

    public CachedPointSymbolizer(final PointSymbolizer point,
            final SymbolizerRendererService<PointSymbolizer,? extends CachedSymbolizer<PointSymbolizer>> renderer){
        super(point,renderer);
        cachedGraphic = CachedGraphic.cache(point.getGraphic());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(final Object candidate, final float coeff) {
        return cachedGraphic.getMargin(candidate, coeff);
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
            cacheWithoutRotation = new UnSynchronizedCache<Float, BufferedImage>(5);
            cacheWithRotation = new UnSynchronizedCache<Float, BufferedImage>(5);
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
    public boolean isVisible(final Object candidate) {
        return cachedGraphic.isVisible(candidate);
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final float coeff, final RenderingHints hints) {
        return getImage(candidate, coeff, true, hints);
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final float coeff, boolean withRotation, final RenderingHints hints) {
        evaluate();

        if(cacheWithoutRotation != null){
            if(withRotation){
                //means the graphic is static, so we can cache fixed size images
                BufferedImage buffer = cacheWithRotation.get(coeff);
                if(buffer == null){
                    buffer = cachedGraphic.getImage(candidate, null, coeff, withRotation, hints);
                    cacheWithRotation.put(coeff, buffer);
                }
                return buffer;
            }else{
                //means the graphic is static, so we can cache fixed size images
                BufferedImage buffer = cacheWithoutRotation.get(coeff);
                if(buffer == null){
                    buffer = cachedGraphic.getImage(candidate, null, coeff, withRotation, hints);
                    cacheWithoutRotation.put(coeff, buffer);
                }
                return buffer;
            }
        }

        //no cache recalculate image
        return cachedGraphic.getImage(candidate, null, coeff, withRotation, hints);
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final Float forcedSize, final float coeff, final RenderingHints hints) {
        return getImage(candidate, forcedSize, coeff, true, hints);
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final Float forcedSize,
            final float coeff, boolean withRotation, final RenderingHints hints) {
        evaluate();
        //no cache recalculate image
        return cachedGraphic.getImage(candidate, forcedSize, coeff, withRotation,hints);
    }

    /**
     * return an Array of 2 floats always in display unit.
     */
    public float[] getDisplacement(final Object candidate, final float[] buffer){
        return cachedGraphic.getDisplacement(candidate, buffer);
    }

    /**
     * return an Array of 2 floats.
     */
    public float[] getAnchor(final Object candidate, final float[] buffer){
        return cachedGraphic.getAnchor(candidate,buffer);
    }

    public float getRotation(final Object candidate){
        return cachedGraphic.getRotation(candidate);
    }

}
