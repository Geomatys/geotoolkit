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
package org.geotoolkit.display2d.ext.pie;

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;

/**
 * Pie cached symbolizer.
 *
 * @author Johann Sorel (Geomays)
 * @author Cédric Briançon (Geomatys)
 */
public class CachedPieSymbolizer extends CachedSymbolizer<PieSymbolizer> {

    public CachedPieSymbolizer(PieSymbolizer symbol, SymbolizerRendererService service){
        super(symbol, service);
    }

    @Override
    protected void evaluate() {
        //load resources, make them ready to be used plenty of times
    }

    @Override
    public float getMargin(Object feature, float coeff) {
        return 0;
    }

    @Override
    public boolean isVisible(Object feature) {
        return true;
    }

}
