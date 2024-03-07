/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.filter.Filter;
import org.opengis.filter.Expression;
import org.opengis.util.CodeList;
import org.apache.sis.filter.privy.Visitor;
import org.opengis.filter.LogicalOperator;

/**
 * Abstract implementation of FilterVisitor simple returns the provided data.
 * <p>
 * This class can be used as is as a placeholder that does nothing:<pre><code>
 * Integer one = (Integer) filter.accepts( NullFilterVisitor.NULL_VISITOR, 1 );
 * </code></pre>
 *
 * The class can also be used as an alternative to DefaultFilterVisitor if
 * you want to only walk part of the data structure:
 * <pre><code>
 * FilterVisitor allFids = new NullFilterVisitor(){
 *     public Object visit( Id filter, Object data ) {
 *         if( data == null) return null;
 *         Set set = (Set) data;
 *         set.addAll(filter.getIDs());
 *         return set;
 *     }
 * };
 * Set set = (Set) myFilter.accept(allFids, new HashSet());
 * Set set2 = (Set) myFilter.accept(allFids, null ); // set2 will be null
 * </code></pre>
 * The base class provides implementations for:
 * <ul>
 * <li>walking And, Or, and Not data structures, returning null at any point will exit early
 * <li>a default implementation for every other construct that will return the provided data
 * </ul>
 *
 * @author Jody Garnett (Refractions Research)
 *
 * @deprecated Unnecessarily complicated for the new filter API.
 */
@Deprecated
public abstract class NullFilterVisitor<V> extends Visitor<Object, NullFilterVisitor.Container<V>> {
    protected static final class Container<V> {
        public V data;
    }

    protected NullFilterVisitor() {
        setLogicalHandlers((f,c) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            if (c.data == null) return;
            for (final Filter<? super Object> child : filter.getOperands()) {
                visit(child, c);
                if (c.data == null) return;
            }
        });
    }

    @Override
    protected void typeNotFound(final CodeList<?> type, final Filter<Object> filter, final NullFilterVisitor.Container<V> data) {
    }

    @Override
    protected void typeNotFound(final String type, final Expression<Object, ?> expression, final NullFilterVisitor.Container<V> data) {
    }
}
