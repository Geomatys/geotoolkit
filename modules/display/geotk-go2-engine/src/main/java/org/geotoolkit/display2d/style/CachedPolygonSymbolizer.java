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

import org.geotoolkit.display2d.GO2Utilities;
import java.awt.AlphaComposite;
import java.awt.Paint;
import java.awt.RenderingHints;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Displacement;
import org.opengis.style.PolygonSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedPolygonSymbolizer extends CachedSymbolizer<PolygonSymbolizer>{
    
    //cached values
    private float cachedDispX = Float.NaN;
    private float cachedDispY = Float.NaN;
    private float cachedOffset = Float.NaN;
    
    private final CachedStroke cacheStroke;
    private final CachedFill cacheFill;
        
    
    public CachedPolygonSymbolizer(PolygonSymbolizer poly){
        super(poly);
        
        cacheStroke = new CachedStroke(poly.getStroke());
        cacheFill = new CachedFill(poly.getFill());
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
        
        requieredAttributs.addAll(cacheStroke.getRequieredAttributsName());
        requieredAttributs.addAll(cacheFill.getRequieredAttributsName());
        
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
            
        }else{
            //we can a disp X and Y of 0
            cachedDispX = 0f;
            cachedDispY = 0f;
        }
        
    }

    public boolean isStrokeVisible(Feature feature){
        return cacheStroke.isVisible(feature);
    }

    public java.awt.Stroke getJ2DStroke(Feature feature,float coeff){
        return cacheStroke.getJ2DStroke(feature,coeff);
    }
    
    public AlphaComposite getJ2DStrokeComposite(Feature feature){
        return cacheStroke.getJ2DComposite(feature);
    }
    
    public Paint getJ2DStrokePaint(Feature feature, int x, int y, float coeff, RenderingHints hints){
        return cacheStroke.getJ2DPaint(feature,x,y,coeff,hints);
    }
    
    public AlphaComposite getJ2DFillComposite(Feature feature){
        return cacheFill.getJ2DComposite(feature);
    }
    
    public Paint getJ2DFillPaint(Feature feature, int x, int y, float coeff, final RenderingHints hints){
        return cacheFill.getJ2DPaint(feature, x,y, coeff,hints);
    }

    /**
     * @return a float value of this style offset
     */
    public float getOffset(Feature feature, float coeff){
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
    public float[] getDisplacement(Feature feature){
        evaluate();
        
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
    public boolean isVisible(Feature feature){
        return cacheStroke.isVisible(feature) || cacheFill.isVisible(feature);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isStatic(){
        return cacheStroke.isStatic() && cacheFill.isStatic();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public VisibilityState isStaticVisible(){
        VisibilityState v1 = cacheStroke.isStaticVisible();
        VisibilityState v2 = cacheFill.isStaticVisible();
        
        if(v1 == VisibilityState.UNVISIBLE && v2 == VisibilityState.UNVISIBLE) return VisibilityState.UNVISIBLE ;
        else if(v1 == VisibilityState.DYNAMIC || v2 == VisibilityState.DYNAMIC) return VisibilityState.DYNAMIC ;
        else return VisibilityState.VISIBLE ;
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Feature feature, float coeff) {
        return cacheStroke.getMargin(feature,coeff);
    }
    
}
