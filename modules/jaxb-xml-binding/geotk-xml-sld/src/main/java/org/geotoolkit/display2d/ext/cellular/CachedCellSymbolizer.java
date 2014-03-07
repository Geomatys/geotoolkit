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

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableRule;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedCellSymbolizer extends CachedSymbolizer<CellSymbolizer>{

    private CachedPointSymbolizer cps = null;
    private CachedTextSymbolizer cs = null;
    private CachedRule cr = null;
    
    public CachedCellSymbolizer(final CellSymbolizer symbol,
            final SymbolizerRendererService<CellSymbolizer,? extends CachedSymbolizer<CellSymbolizer>> renderer){
        super(symbol,renderer);
    }

    public CachedPointSymbolizer getCachedPointSymbolizer() {
        evaluate();
        return cps;
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

        final MutableRule r = new DefaultStyleFactory().rule();
        
        
        if(styleElement.getPointSymbolizer()!=null){
            r.symbolizers().add(styleElement.getPointSymbolizer());
            cps = (CachedPointSymbolizer) GO2Utilities.getCached(styleElement.getPointSymbolizer(), null);
        }
        
        if(styleElement.getTextSymbolizer()!=null){
            r.symbolizers().add(styleElement.getTextSymbolizer());
        }
        
        if(styleElement.getFilter()!=null){
            r.setFilter(styleElement.getFilter());
        }
        
        cr = new CachedRule(r, null);
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
