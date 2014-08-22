/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.display2d.ext.isoline.symbolizer;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.map.MapLayer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class IsolineSymbolizerRendererService extends AbstractSymbolizerRendererService<IsolineSymbolizer, CachedIsolineSymbolizer> {

    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    @Override
    public Class<IsolineSymbolizer> getSymbolizerClass() {
        return IsolineSymbolizer.class;
    }

    @Override
    public Class<CachedIsolineSymbolizer> getCachedSymbolizerClass() {
        return CachedIsolineSymbolizer.class;
    }

    @Override
    public CachedIsolineSymbolizer createCachedSymbolizer(IsolineSymbolizer symbol) {
        return new CachedIsolineSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedIsolineSymbolizer symbol, RenderingContext2D context) {
        return new IsolineSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedIsolineSymbolizer symbol, MapLayer layer) {
        SymbolizerRendererService rasterRenderer = GO2Utilities.findRenderer(symbol.getCachedRasterSymbolizer());
        return rasterRenderer.glyphPreferredSize(symbol.getCachedRasterSymbolizer(), layer);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedIsolineSymbolizer symbol, MapLayer layer) {
        DefaultGlyphService.render(symbol.getCachedRasterSymbolizer().getSource(), rect, g, layer);
        //TODO add lines
    }
}
