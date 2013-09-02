/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.symbolizer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52SymbolizerRendererService extends AbstractSymbolizerRendererService<S52Symbolizer, S52CachedSymbolizer> {

    @Override
    public Class<S52Symbolizer> getSymbolizerClass() {
        return S52Symbolizer.class;
    }

    @Override
    public Class<S52CachedSymbolizer> getCachedSymbolizerClass() {
        return S52CachedSymbolizer.class;
    }

    @Override
    public S52CachedSymbolizer createCachedSymbolizer(S52Symbolizer symbol) {
        return new S52CachedSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(S52CachedSymbolizer symbol, RenderingContext2D context) {
        return new S52SymbolizerRenderer(this, symbol, context);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, S52CachedSymbolizer symbol, MapLayer layer) {
        //no glyph
    }

    @Override
    public boolean isGroupSymbolizer() {
        return true;
    }

}
