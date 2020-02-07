/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

import java.util.Arrays;
import java.util.List;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 * A cached rule is a container for cachedSymbolizer. those objects act as
 * temporary cache while rendering.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedRule extends Cache<Rule> {

    private final CachedSymbolizer[] symbols;
    private final Filter preparedFilter;

    public CachedRule(final Rule source, final FeatureType expected) {
        super(source);

        final List<? extends Symbolizer> ruleSymbols = source.symbolizers();
        final CachedSymbolizer[] array = new CachedSymbolizer[ruleSymbols.size()];
        int i = 0;
        for (Symbolizer symbol : ruleSymbols) {
            final CachedSymbolizer cs = GO2Utilities.getCached(symbol, expected);
            if (cs != null) {
                array[i] = cs;
                i++;
            }
        }

        if (i == array.length) {
            //we found a cached symbol for each symbol
            this.symbols = array;
        } else {
            //we could not find a cache for each symbol, we must resize our array.
            this.symbols = Arrays.copyOf(array, i);
        }

        this.preparedFilter = FilterUtilities.prepare(source.getFilter(), Feature.class, expected);
    }

    /**
     * @return Rule optimized filter.
     */
    public Filter getFilter() {
        return preparedFilter;
    }

    /**
     * @return the live list of all cached Symbolizers.
     */
    public CachedSymbolizer[] symbolizers() {
        return symbols;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if (!isNotEvaluated) {
            return;
        }

        Filter filter = styleElement.getFilter();
        if (filter != null) {
            filter.accept(ListingPropertyVisitor.VISITOR, requieredAttributs);
        }

        for (CachedSymbolizer cached : symbols) {
            cached.getRequieredAttributsName(requieredAttributs);
        }

        isNotEvaluated = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(final Object candidate) {

        for (CachedSymbolizer cached : symbols) {
            if (cached.isVisible(candidate)) {
                return true;
            }
        }

        return false;
    }

    public float getMargin(Object candidate, final RenderingContext2D ctx) {
        float f = 0f;
        for (CachedSymbolizer cs : symbols) {
            f = Math.max(f, cs.getMargin(candidate, ctx));
        }
        return f;
    }

}
