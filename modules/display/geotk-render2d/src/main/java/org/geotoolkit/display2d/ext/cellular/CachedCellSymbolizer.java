/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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

import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.style.Rule;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedCellSymbolizer extends CachedSymbolizer<CellSymbolizer>{

    private CachedRule cr = null;

    public CachedCellSymbolizer(final CellSymbolizer symbol,
            final SymbolizerRendererService<CellSymbolizer,? extends CachedSymbolizer<CellSymbolizer>> renderer){
        super(symbol,renderer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Object candidate, RenderingContext2D ctx) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        final Rule r = styleElement.getRule();
        if(r != null){
            cr = new CachedRule(r, null);

            //the cached rules refer to cell properties name
            final Set<String> properties = new HashSet<>();
            cr.getRequieredAttributsName(properties);
            for(String s : properties){
                s = CellSymbolizer.cellToBasePropertyName(s);
                if(s!=null){
                    requieredAttributs.add(s);
                }
            }
        }

        isNotEvaluated = false;
    }

    @Override
    public boolean isVisible(final Object feature) {
        return true;
    }

    public CachedRule getCachedRule() {
        evaluate();
        return cr;
    }

}
