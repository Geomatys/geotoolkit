/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.ext.tiledebug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

/**
 * Rendering service for Tile debug symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TileDebugRendererService extends AbstractSymbolizerRendererService<TileDebugSymbolizer,CachedTileDebugSymbolizer>{

    @Override
    public boolean isGroupSymbolizer() {
        return true;
    }

    @Override
    public Class<TileDebugSymbolizer> getSymbolizerClass() {
        return TileDebugSymbolizer.class;
    }

    @Override
    public Class<CachedTileDebugSymbolizer> getCachedSymbolizerClass() {
        return CachedTileDebugSymbolizer.class;
    }

    @Override
    public CachedTileDebugSymbolizer createCachedSymbolizer(TileDebugSymbolizer symbol) {
        return new CachedTileDebugSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedTileDebugSymbolizer symbol, RenderingContext2D context) {
        return new TileDebugSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rectangle, CachedTileDebugSymbolizer symbol, MapLayer layer) {
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));
        g.draw(rectangle);
    }

}
