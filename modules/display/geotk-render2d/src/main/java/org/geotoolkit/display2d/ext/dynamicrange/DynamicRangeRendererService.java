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
package org.geotoolkit.display2d.ext.dynamicrange;

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
public class DynamicRangeRendererService extends AbstractSymbolizerRendererService<DynamicRangeSymbolizer,CachedDynamicRangeSymbolizer>{

    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    @Override
    public Class<DynamicRangeSymbolizer> getSymbolizerClass() {
        return DynamicRangeSymbolizer.class;
    }

    @Override
    public Class<CachedDynamicRangeSymbolizer> getCachedSymbolizerClass() {
        return CachedDynamicRangeSymbolizer.class;
    }

    @Override
    public CachedDynamicRangeSymbolizer createCachedSymbolizer(DynamicRangeSymbolizer symbol) {
        return new CachedDynamicRangeSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedDynamicRangeSymbolizer symbol, RenderingContext2D context) {
        return new DynamicRangeSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedDynamicRangeSymbolizer symbol, MapLayer layer) {
        //TODO
    }
    
}
