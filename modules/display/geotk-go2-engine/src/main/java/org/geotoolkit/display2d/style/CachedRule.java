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

import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 * A cached rule is a container for cachedSymbolizer. thoses objects act
 * as temporary cache while rendering.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedRule extends Cache<Rule>{

    private final CachedSymbolizer[] symbols;

    public CachedRule(Rule source){
        super(source);

        List<CachedSymbolizer> cacheds = new ArrayList<CachedSymbolizer>();

        for(Symbolizer symbol : source.symbolizers()){
            cacheds.add(GO2Utilities.getCached(symbol));
        }

        symbols = cacheds.toArray(new CachedSymbolizer[cacheds.size()]);
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
    public CachedSymbolizer[] symbolizers(){
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
            filter.accept(ListingPropertyVisitor.VISITOR, requieredAttributs);
        }
        
        for(CachedSymbolizer cached : symbols){
            requieredAttributs.addAll(cached.getRequieredAttributsName());
        }

        isNotEvaluated = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(Feature feature) {

        for(CachedSymbolizer cached : symbols){
            if(cached.isVisible(feature)) return true;
        }

        return false;
    }

}
