/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.dimrange;

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
public class DimRangeRendererService extends AbstractSymbolizerRendererService<DimRangeSymbolizer,CachedDimRangeSymbolizer>{

    @Override
    public Class<DimRangeSymbolizer> getSymbolizerClass() {
        return DimRangeSymbolizer.class;
    }

    @Override
    public Class<CachedDimRangeSymbolizer> getCachedSymbolizerClass() {
        return CachedDimRangeSymbolizer.class;
    }

    @Override
    public CachedDimRangeSymbolizer createCachedSymbolizer(DimRangeSymbolizer symbol) {
        return new CachedDimRangeSymbolizer(symbol,this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedDimRangeSymbolizer symbol, RenderingContext2D context) {
        return new DimRangeRenderer(symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedDimRangeSymbolizer symbol,MapLayer layer) {
        return null;
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedDimRangeSymbolizer symbol, MapLayer layer) {
        //no glyph
    }

}
