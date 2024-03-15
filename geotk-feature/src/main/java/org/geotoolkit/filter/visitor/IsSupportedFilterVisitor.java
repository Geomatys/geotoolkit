/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.visitor;

import java.util.function.Function;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Filter;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.Expression;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.filter.capability.AvailableFunction;
import org.opengis.filter.capability.FilterCapabilities;


/**
 * This visitor will return Boolean.TRUE if the provided filter
 * is supported by the the FilterCapabilities.
 * <p>
 * This method will look up the right information in the provided
 * FilterCapabilities instance for you depending on the type of filter
 * provided.
 * <p>
 * Example:<pre><code>
 * boolean yes = filter.accepts( IsSupportedFilterVisitor( capabilities ), null );
 * </code></pre>
 *
 * Please consider IsSupportedFilterVisitor if you need to be sure of the
 * entire Filter.
 *
 * @author Jody Garnett (Refractions Research)
 */
public class IsSupportedFilterVisitor extends AbstractVisitor<Object,Boolean> {
    private final FilterCapabilities capabilities;

    public IsSupportedFilterVisitor(final FilterCapabilities capabilities) {
        this.capabilities = capabilities;
        final Function<Filter<Object>,Boolean> no = (f) -> Boolean.FALSE;
        setFilterHandler(null, no);
        setIncludeExcludeHandlers(no);
        final Function<Filter<Object>,Boolean> hasLogicalOperators = (f) ->
            capabilities.getScalarCapabilities()
                        .map(ScalarCapabilities::hasLogicalOperators)
                        .orElse(Boolean.FALSE);
        setFilterHandler(LogicalOperatorName.AND, hasLogicalOperators);
        setFilterHandler(LogicalOperatorName.NOT, hasLogicalOperators);
        setFilterHandler(LogicalOperatorName.OR,  hasLogicalOperators);
        for (final ComparisonOperatorName op : ComparisonOperatorName.values()) {
            setFilterHandler(op, (f) -> capabilities.getScalarCapabilities()
                    .map((c) -> c.getComparisonOperators().contains(op))
                    .orElse(Boolean.FALSE));
        }
        for (final SpatialOperatorName op : SpatialOperatorName.values()) {
            setFilterHandler(op, (f) -> capabilities.getSpatialCapabilities()
                    .map((c) -> c.getSpatialOperators().containsKey(op))
                    .orElse(Boolean.FALSE));
        }
        for (final DistanceOperatorName op : DistanceOperatorName.values()) {
            setFilterHandler(op, (f) -> capabilities.getSpatialCapabilities() != null);
        }
        for (final TemporalOperatorName op : TemporalOperatorName.values()) {
            setFilterHandler(op, (f) -> capabilities.getTemporalCapabilities()
                    .map((c) -> c.getTemporalOperators().containsKey(op))
                    .orElse(Boolean.FALSE));
        }
        setExpressionHandler("Literal",        (e) -> Boolean.TRUE);
        setExpressionHandler("ValueReference", (e) -> Boolean.TRUE);
    }

    @Override
    protected Boolean typeNotFound(final String name, final Expression<Object,?> expression) {
        for (final AvailableFunction f : capabilities.getFunctions().values()) {
            if (name.equals(f.getName().toString())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
