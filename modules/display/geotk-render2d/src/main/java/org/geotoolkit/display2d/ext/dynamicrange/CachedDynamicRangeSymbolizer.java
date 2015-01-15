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

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedDynamicRangeSymbolizer extends CachedSymbolizer<DynamicRangeSymbolizer>{

    public CachedDynamicRangeSymbolizer(DynamicRangeSymbolizer styleElement, SymbolizerRendererService<DynamicRangeSymbolizer, ? extends CachedSymbolizer<DynamicRangeSymbolizer>> renderer) {
        super(styleElement, renderer);
    }

    @Override
    public float getMargin(Object candidate, float coeff) {
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
