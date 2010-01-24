/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.dimrange;

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;

import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedDimRangeSymbolizer extends CachedSymbolizer<DimRangeSymbolizer>{

    public CachedDimRangeSymbolizer(DimRangeSymbolizer sym,
            SymbolizerRenderer<DimRangeSymbolizer,? extends CachedSymbolizer<DimRangeSymbolizer>> renderer){
        super(sym,renderer);
    }

    @Override
    public float getMargin(Feature feature, float coeff) {
        return 0;
    }

    @Override
    protected void evaluate() {
    }

    @Override
    public boolean isVisible(Feature feature) {
        return false;
    }

}

