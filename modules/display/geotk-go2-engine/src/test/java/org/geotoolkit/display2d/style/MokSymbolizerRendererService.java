/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

/**
 * Test that symbolizer renderer are properly called and only once.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MokSymbolizerRendererService extends AbstractSymbolizerRendererService<MokSymbolizer, CachedMokSymbolizer>{

    @Override
    public Class<MokSymbolizer> getSymbolizerClass() {
        return MokSymbolizer.class;
    }

    @Override
    public Class<CachedMokSymbolizer> getCachedSymbolizerClass() {
        return CachedMokSymbolizer.class;
    }

    @Override
    public CachedMokSymbolizer createCachedSymbolizer(final MokSymbolizer symbol) {
        return new CachedMokSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(final CachedMokSymbolizer symbol, final RenderingContext2D context) {
        return new MokSymbolizerRenderer(symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(final CachedMokSymbolizer symbol, final MapLayer layer) {
        return null;
    }

    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rect, final CachedMokSymbolizer symbol, final MapLayer layer) {
    }

}
