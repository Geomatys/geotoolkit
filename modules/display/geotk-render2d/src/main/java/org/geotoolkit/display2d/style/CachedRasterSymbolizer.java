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
import java.awt.Composite;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.filter.Expression;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedRasterSymbolizer extends CachedSymbolizer<RasterSymbolizer> {

    //cached values
    private Composite j2dComposite = null;
    private final CachedSymbolizer cachedoutLine;


    public CachedRasterSymbolizer(final RasterSymbolizer symbol,
            final SymbolizerRendererService<RasterSymbolizer,? extends CachedSymbolizer<RasterSymbolizer>> renderer) {
        super(symbol,renderer);

        Symbolizer outline = styleElement.getImageOutline();
        if (outline != null) {
            if (outline instanceof LineSymbolizer) {
                cachedoutLine = GO2Utilities.getCached((LineSymbolizer)outline,null);
            } else if(outline instanceof PolygonSymbolizer) {
                cachedoutLine = GO2Utilities.getCached((PolygonSymbolizer)outline,null);
            } else {
                cachedoutLine = null;
            }
        } else {
            cachedoutLine = null;
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void evaluate() {
        if (!isNotEvaluated) return;

        if (!evaluateComposite()) {
            requieredAttributs = EMPTY_ATTRIBUTS;
            isStatic = false;
        }

        isNotEvaluated = false;
    }

    private boolean evaluateComposite() {
        final Expression opacity = styleElement.getOpacity();
        if (GO2Utilities.isStatic(opacity)) {
            Float j2dOpacity = GO2Utilities.evaluate(opacity, null, 1f,0f,1f);
            if (j2dOpacity <= 0) return false; //-------------------------------------------------OUT NO NEED TO PAINT
            j2dComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, j2dOpacity.floatValue());
        }

        return true;
    }

    public Composite getJ2DComposite() {
        evaluate();

        //if composite is null it means it is dynamic
        if (j2dComposite == null) {
            final Expression opacity = styleElement.getOpacity();
            Float j2dOpacity = GO2Utilities.evaluate(opacity, null, 1f,0f,1f);
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, j2dOpacity.floatValue());
        }

        return j2dComposite;
    }

    public CachedSymbolizer getOutLine(){
        return cachedoutLine;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(final Object candidate) {
        return true;
    }

    @Override
    public float getMargin(Object candidate, RenderingContext2D ctx) {
        evaluate();

        if (cachedoutLine == null) {
            return 0;
        } else {
            return cachedoutLine.getMargin(candidate,ctx);
        }
    }

}
