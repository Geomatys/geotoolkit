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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.style.Rule;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedCellSymbolizer extends CachedSymbolizer<CellSymbolizer>{

    private CachedRule[] cached = null;
    
    public CachedCellSymbolizer(final CellSymbolizer symbol,
            final SymbolizerRendererService<CellSymbolizer,? extends CachedSymbolizer<CellSymbolizer>> renderer){
        super(symbol,renderer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(final Object feature, final float coeff) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        final List<? extends Rule> rules = styleElement.getRules();
        cached = new CachedRule[rules.size()];
        for(int i=0;i<cached.length;i++){
            cached[i] = new CachedRule(rules.get(i), null);
        }
        
        isNotEvaluated = false;
    }

    @Override
    public boolean isVisible(final Object feature) {
        return true;
    }

    public CachedRule[] getCachedRules() {
        evaluate();
        return cached;
    }
    
}
