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

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.filter.Expression;
import org.opengis.style.LineSymbolizer;

/**
 * Cached line symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedLineSymbolizer extends CachedSymbolizer<LineSymbolizer>{

    //Cached values
    private float cachedOffset = Float.NaN;

    private final LineSymbolizer line;
    private final CachedStroke cachedStroke;

    public CachedLineSymbolizer(final LineSymbolizer line,
            final SymbolizerRendererService<LineSymbolizer, ? extends CachedSymbolizer<LineSymbolizer>> renderer){
        super(line,renderer);
        cachedStroke = CachedStroke.cache(line.getStroke());
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
    public float getOffset(final Object candidate, final float coeff){
        evaluate();

        if(Float.isNaN(cachedOffset)){
            //if offset is null it means it is dynamic
            final Expression offset = line.getPerpendicularOffset();
            return GO2Utilities.evaluate(offset, null, Float.class, 0f);
        }

        return cachedOffset*coeff;
    }

    public CachedStroke getCachedStroke(){
        return cachedStroke;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Object candidate, RenderingContext2D ctx) {
        return cachedStroke.getMargin(candidate,
           ctx.getUnitCoefficient(this.styleElement.getUnitOfMeasure()));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(final Object candidate) {
        return cachedStroke.isVisible(candidate);
    }

}
