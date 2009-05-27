/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import org.geotoolkit.display2d.GO2Utilities;
import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 * A cached rule is a container for cachedSymbolizer. thoses objects act
 * as temporary cache while rendering.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedRule extends Cache<Rule>{

    private final List<CachedSymbolizer> symbols = new ArrayList<CachedSymbolizer>();

    public CachedRule(Rule source){
        super(source);
        for(Symbolizer symbol : source.symbolizers()){
            symbols.add(GO2Utilities.getCached(symbol));
        }
    }

    /**
     * @return Rule filter.
     */
    public Filter getFilter(){
        return styleElement.getFilter();
    }

    /**
     * @return the live list of all cached Symbolizers.
     */
    public List<CachedSymbolizer> symbolizers(){
        return symbols;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        Filter filter = styleElement.getFilter();
        if(filter != null){
            ListingPropertyVisitor visitor = new ListingPropertyVisitor();
            filter.accept(visitor, requieredAttributs);
        }
        
        for(int i=0,n=symbols.size();i<n;i++){
            symbols.get(i).evaluate();
            requieredAttributs.addAll(symbols.get(i).getRequieredAttributsName());
        }

        isNotEvaluated = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(Feature feature) {

        for(int i=0,n=symbols.size();i<n;i++){
            if(symbols.get(i).isVisible(feature)) return true;
        }

        return false;
    }

}
