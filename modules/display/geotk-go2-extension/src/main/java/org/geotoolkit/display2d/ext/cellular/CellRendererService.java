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
package org.geotoolkit.display2d.ext.cellular;

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
public class CellRendererService extends AbstractSymbolizerRendererService<CellSymbolizer,CachedCellSymbolizer>{

    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    @Override
    public Class<CellSymbolizer> getSymbolizerClass() {
        return CellSymbolizer.class;
    }

    @Override
    public Class<CachedCellSymbolizer> getCachedSymbolizerClass() {
        return CachedCellSymbolizer.class;
    }

    @Override
    public CachedCellSymbolizer createCachedSymbolizer(CellSymbolizer symbol) {
        return new CachedCellSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedCellSymbolizer symbol, RenderingContext2D context) {
        return new CellSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedCellSymbolizer symbol, MapLayer layer) {
        //TODO
    }
    
}
