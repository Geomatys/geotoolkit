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

import java.awt.AlphaComposite;
import java.awt.Paint;
import java.awt.RenderingHints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.filter.Expression;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedPolygonSymbolizer extends CachedSymbolizer<PolygonSymbolizer>{

    //cached values
    private float[] cachedDisps = null;
    private float cachedDispX = Float.NaN;
    private float cachedDispY = Float.NaN;
    private float cachedOffset = Float.NaN;

    private final CachedStroke cacheStroke;
    private final CachedFill cacheFill;


    public CachedPolygonSymbolizer(final PolygonSymbolizer poly,
            final SymbolizerRendererService<PolygonSymbolizer,? extends CachedSymbolizer<PolygonSymbolizer>> renderer){
        super(poly,renderer);

        final Stroke str = poly.getStroke();
        if(str == null){
            cacheStroke = null;
        }else{
            cacheStroke = CachedStroke.cache(poly.getStroke());
        }

        final Fill fill = poly.getFill();
        if(fill == null){
            cacheFill = null;
        }else{
            cacheFill = CachedFill.cache(poly.getFill());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        //call this first seens more evaluation may clear the cache
        evaluateOffset();
        evaluateDisplacement();

        if(cacheStroke != null){
            cacheStroke.getRequieredAttributsName(requieredAttributs);
        }
        if(cacheFill != null){
            cacheFill.getRequieredAttributsName(requieredAttributs);
        }

        if(requieredAttributs.isEmpty()) requieredAttributs = EMPTY_ATTRIBUTS;

        isNotEvaluated = false;
    }

    private void evaluateOffset(){
        final Expression offset = styleElement.getPerpendicularOffset();

        if(GO2Utilities.isStatic(offset)){
            cachedOffset = GO2Utilities.evaluate(offset, null, Float.class, 0f);
        }else{
            GO2Utilities.getRequieredAttributsName(offset,requieredAttributs);
        }

    }

    private void evaluateDisplacement(){
        Displacement disp = styleElement.getDisplacement();

        if(disp != null){

            final Expression dispX = disp.getDisplacementX();
            final Expression dispY = disp.getDisplacementY();

            if(GO2Utilities.isStatic(dispX)){
                cachedDispX = GO2Utilities.evaluate(dispX, null, Float.class, 0f);
            }else{
                GO2Utilities.getRequieredAttributsName(dispX,requieredAttributs);
            }

             if(GO2Utilities.isStatic(dispY)){
                cachedDispY = GO2Utilities.evaluate(dispY, null, Float.class, 0f);
            }else{
                GO2Utilities.getRequieredAttributsName(dispY,requieredAttributs);
            }

            if(!Float.isNaN(cachedDispX) && !Float.isNaN(cachedDispY)){
                cachedDisps = new float[]{cachedDispX,cachedDispY};
            }

        }else{
            //we can a disp X and Y of 0
            cachedDispX = 0f;
            cachedDispY = 0f;
            cachedDisps = new float[]{0,0};
        }

    }

    public CachedStroke getCachedStroke(){
        return cacheStroke;
    }

    public boolean isMosaic(){
        return  (cacheFill != null && cacheFill.isMosaic())
                ||
                (cacheStroke instanceof CachedStrokeSimple && ((CachedStrokeSimple)cacheStroke).isMosaicPaint() );
    }

    public boolean isStrokeVisible(final Object candidate){
        return cacheStroke != null && cacheStroke.isVisible(candidate);
    }

    public boolean isFillVisible(final Object candidate){
        return cacheFill != null && cacheFill.isVisible(candidate);
    }

    public AlphaComposite getJ2DFillComposite(final Object candidate){
        return cacheFill.getJ2DComposite(candidate);
    }

    public Paint getJ2DFillPaint(final Object candidate, final int x, final int y, final float coeff, final RenderingHints hints){
        return cacheFill.getJ2DPaint(candidate, x,y, coeff,hints);
    }

    /**
     * @return a float value of this style offset
     */
    public float getOffset(final Object candidate, final float coeff){
        evaluate();

        if(Float.isNaN(cachedOffset)){
            //if offset is null it means it is dynamic
            final Expression offset = styleElement.getPerpendicularOffset();
            return GO2Utilities.evaluate(offset, null, Float.class, 0f);
        }

        return cachedOffset*coeff;
    }

    /**
     * @return an Array of 2 floats always in display unit.
     */
    public float[] getDisplacement(final Object candidate){
        evaluate();

        if(cachedDisps != null){
            return cachedDisps;
        }

        final float[] disps = new float[2];

        if(Float.isNaN(cachedDispX)){
            //if dispX is Float.NaN it means it is dynamic
            final Expression dispX = styleElement.getDisplacement().getDisplacementX();
            disps[0] = GO2Utilities.evaluate(dispX, null, Float.class, 0f);
        } else {
            disps[0] = cachedDispX;
        }

        if(Float.isNaN(cachedDispY)){
            //if dispY is Float.NaN it means it is dynamic
            final Expression dispY = styleElement.getDisplacement().getDisplacementY();
            disps[1] = GO2Utilities.evaluate(dispY, null, Float.class, 0f);
        } else {
            disps[1] = cachedDispY;
        }


        return disps;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(final Object candidate){
        return (cacheStroke == null || cacheStroke.isVisible(candidate))
                || (cacheFill == null || cacheFill.isVisible(candidate));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isStatic(){
        return (cacheStroke == null || cacheStroke.isStatic())
                && (cacheFill == null || cacheFill.isStatic());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public VisibilityState isStaticVisible(){
        VisibilityState v1 = (cacheStroke==null)?VisibilityState.UNVISIBLE : cacheStroke.isStaticVisible();
        VisibilityState v2 = (cacheFill==null)?VisibilityState.UNVISIBLE : cacheFill.isStaticVisible();

        if(v1 == VisibilityState.UNVISIBLE && v2 == VisibilityState.UNVISIBLE) return VisibilityState.UNVISIBLE ;
        else if(v1 == VisibilityState.DYNAMIC || v2 == VisibilityState.DYNAMIC) return VisibilityState.DYNAMIC ;
        else return VisibilityState.VISIBLE ;

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(final Object candidate, final RenderingContext2D ctx){
        return getMargin(candidate, ctx.getUnitCoefficient(this.styleElement.getUnitOfMeasure()));
    }

    public float getMargin(final Object candidate, final float coeff) {
        if(cacheStroke == null){
            return 0f;
        }else{
            return cacheStroke.getMargin(candidate,coeff);
        }

    }

}
