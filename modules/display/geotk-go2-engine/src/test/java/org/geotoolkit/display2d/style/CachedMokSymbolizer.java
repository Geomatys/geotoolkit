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

import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;

/**
 * Test that symbolizer renderer are properly called and only once.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedMokSymbolizer extends CachedSymbolizer<MokSymbolizer>{

    public CachedMokSymbolizer(final MokSymbolizer point,
            final SymbolizerRendererService<MokSymbolizer,? extends CachedSymbolizer<MokSymbolizer>> renderer){
        super(point,renderer);
    }

    @Override
    public float getMargin(final Object candidate, final float coeff) {
        return 0;
    }

    @Override
    protected void evaluate() {
    }

    @Override
    public boolean isVisible(final Object candidate) {
        return true;
    }

}
