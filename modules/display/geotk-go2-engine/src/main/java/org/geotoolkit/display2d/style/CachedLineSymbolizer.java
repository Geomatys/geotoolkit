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

import java.awt.AlphaComposite;
import java.awt.Paint;
import java.awt.RenderingHints;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.LineSymbolizer;

/**
 * Cached line symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CachedLineSymbolizer extends CachedSymbolizer<LineSymbolizer>{

    //Cached values
    private float cachedOffset = Float.NaN;
    
    private final LineSymbolizer line;
    private final CachedStroke cachedStroke;
    
    public CachedLineSymbolizer(LineSymbolizer line){
        super(line);
        cachedStroke = new CachedStroke(line.getStroke());
        this.line = line;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void evaluate() {
        if(!isNotEvaluated) return;
        evaluateOffset();
        isNotEvaluated = false;
    }
    
    private void evaluateOffset(){
        final Expression offset = line.getPerpendicularOffset();

        if(GO2Utilities.isStatic(offset)){
            cachedOffset = GO2Utilities.evaluate(offset, null, Float.class, 0f);
        }else{
            isStatic = false;
            GO2Utilities.getRequieredAttributsName(offset,requieredAttributs);
        }

    }
    
    /**
     * 
     * @return offset of the given feature.
     */
    public float getOffset(Feature feature, float coeff){
        evaluate();

        if(cachedOffset == Float.NaN){
            //if offset is null it means it is dynamic
            final Expression offset = line.getPerpendicularOffset();
            return GO2Utilities.evaluate(offset, null, Float.class, 0f);
        }

        return cachedOffset*coeff;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Feature feature, float coeff) {
        return cachedStroke.getMargin(feature, coeff);
    }

    public float getStrokeWidth(Feature feature){
        return cachedStroke.getStrokeWidth(feature);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(Feature feature) {
        return cachedStroke.isVisible(feature);
    }

    /**
     * Get the java2D Composite for the given feature.
     * 
     * @param feature : evaluate paint with the given feature
     * @return Java2D Composite
     */
    public AlphaComposite getJ2DComposite(final Feature feature){
        return cachedStroke.getJ2DComposite(feature);
    }

    /**
     * Get the java2D Paint for the given feature.
     * 
     * @param feature : evaluate paint with the given feature
     * @param x : start X position of the fill area
     * @param y : start Y position of the fill area
     * @return Java2D Paint
     */
    public Paint getJ2DPaint(final Feature feature, final int x, final int y, final float coeff, final RenderingHints hints){
        return cachedStroke.getJ2DPaint(feature, x, y, coeff, hints);
    }

    /**
     * Get the java2D Stroke for the given feature.
     * 
     * @param feature : evaluate stroke with the given feature
     * @param coeff : use to adjust stroke size, if in display unit value equals 1
     * @return Java2D Stroke
     */
    public java.awt.Stroke getJ2DStroke(final Feature feature, final float coeff){
        return cachedStroke.getJ2DStroke(feature, coeff);
    }
    
}
