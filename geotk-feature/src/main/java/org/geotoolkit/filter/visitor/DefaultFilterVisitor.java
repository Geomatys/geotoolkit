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

import org.apache.sis.filter.visitor.FunctionNames;
import org.apache.sis.filter.visitor.Visitor;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.LogicalOperator;
import org.opengis.util.CodeList;


/**
 * Abstract implementation of FilterVisitor that simply walks the data structure.
 * <p>
 * This class implements the full FilterVisitor interface and will visit every Filter member of a
 * Filter object. This class performs no actions and is not intended to be used directly, instead
 * extend it and override the methods for the Filter type you are interested in. Remember to call the
 * super method if you want to ensure that the entire filter tree is still visited.
 *
 * <pre><code>
 * FilterVisitor allFids = new DefaultFilterVisitor(){
 *     public Object visit( Id filter, Object data ) {
 *         Set set = (Set) data;
 *         set.addAll(filter.getIDs());
 *         return set;
 *     }
 * };
 * Set set = (Set) myFilter.accept(allFids, new HashSet());
 * </code></pre>
 *
 * @author Jody
 */
public abstract class DefaultFilterVisitor<V> extends Visitor<Object,V> {
    protected DefaultFilterVisitor() {
        setLogicalHandlers((f, data) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            for (final Filter<? super Object> child : filter.getOperands()) {
                visit(child, data);
            }
        });
        setBinaryComparisonHandlers((f, data) -> {
            final BinaryComparisonOperator<Object> filter = (BinaryComparisonOperator<Object>) f;
            visit(filter.getOperand1(), data);
            visit(filter.getOperand2(), data);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_BETWEEN), (f, data) -> {
            final BetweenComparisonOperator<Object> filter = (BetweenComparisonOperator<Object>) f;
            visit(filter.getLowerBoundary(), data);
            visit(filter.getExpression(),    data);
            visit(filter.getUpperBoundary(), data);
        });
    }

    @Override
    protected void typeNotFound(final CodeList<?> type, final Filter<Object> filter, final V data) {
        filter.getExpressions().forEach(child -> visit(child, data));
    }

    @Override
    protected void typeNotFound(final String type, final Expression<Object,?> expression, final V data) {
        expression.getParameters().forEach(parameter -> visit(parameter, data));
    }
}
