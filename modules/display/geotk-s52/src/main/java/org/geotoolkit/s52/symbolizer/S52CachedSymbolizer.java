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

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52CachedSymbolizer extends CachedSymbolizer<S52Symbolizer>{

    public S52CachedSymbolizer(S52Symbolizer styleElement,
            SymbolizerRendererService<S52Symbolizer, ? extends CachedSymbolizer<S52Symbolizer>> renderer) {
        super(styleElement, renderer);
    }

    @Override
    public float getMargin(Object candidate, float coeff) {
        return 10f;
    }

    @Override
    protected void evaluate() {

    }

    @Override
    public boolean isVisible(Object candidate) {
        return true;
    }

}
