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

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedTileDebugSymbolizer extends CachedSymbolizer<TileDebugSymbolizer>{

    public CachedTileDebugSymbolizer(TileDebugSymbolizer styleElement, SymbolizerRendererService<TileDebugSymbolizer, ? extends CachedSymbolizer<TileDebugSymbolizer>> renderer) {
        super(styleElement, renderer);
    }

    @Override
    public float getMargin(Object candidate, RenderingContext2D ctx) {
        return 0;
    }

    @Override
    protected void evaluate() {
    }

    @Override
    public boolean isVisible(Object candidate) {
        return true;
    }

}
