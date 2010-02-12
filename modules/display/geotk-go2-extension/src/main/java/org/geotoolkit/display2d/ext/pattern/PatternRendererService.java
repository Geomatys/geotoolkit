/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.display2d.ext.pattern;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PatternRendererService extends AbstractSymbolizerRendererService<PatternSymbolizer,CachedPatternSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<PatternSymbolizer> getSymbolizerClass() {
        return PatternSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedPatternSymbolizer> getCachedSymbolizerClass() {
        return CachedPatternSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedPatternSymbolizer createCachedSymbolizer(PatternSymbolizer symbol) {
        return new CachedPatternSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(CachedPatternSymbolizer symbol, RenderingContext2D context) {
        return new PatternRenderer(symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D glyphPreferredSize(CachedPatternSymbolizer symbol,MapLayer layer) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedPatternSymbolizer symbol,MapLayer layer) {
        //todo glyph
    }

}
