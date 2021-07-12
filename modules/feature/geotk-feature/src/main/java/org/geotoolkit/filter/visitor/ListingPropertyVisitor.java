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
package org.geotoolkit.filter.visitor;

import java.util.Collection;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.filter.FunctionNames;
import org.apache.sis.internal.filter.Visitor;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.LogicalOperator;
import org.opengis.util.CodeList;

/**
 * Expression visitor that returns a list of all Feature attributs requiered by this expression.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ListingPropertyVisitor extends Visitor<Object,Collection<String>> {

    public static final ListingPropertyVisitor VISITOR = new ListingPropertyVisitor();

    protected ListingPropertyVisitor() {
        setLogicalHandlers((f, names) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            for (Filter<Object> child : filter.getOperands()) {
                visit(child, names);
            }
        });
        setFilterHandler(AbstractVisitor.RESOURCEID_NAME, (f, names) -> {
            names.add(AttributeConvention.IDENTIFIER);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_BETWEEN), (f, names) -> {
            final BetweenComparisonOperator<Object> filter = (BetweenComparisonOperator<Object>) f;
            visit(filter.getExpression(),    names);
            visit(filter.getLowerBoundary(), names);
            visit(filter.getUpperBoundary(), names);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_LIKE), (f, names) -> {
            final LikeOperator<Object> filter = (LikeOperator<Object>) f;
            visit(filter.getExpressions().get(0), names);
        });
        setExpressionHandler(FunctionNames.ValueReference, (e, names) -> {
            final ValueReference<Object,?> expression = (ValueReference<Object,?>) e;
            final String propName = expression.getXPath();
            if (!propName.trim().isEmpty()) {
                names.add(propName);
            }
        });
    }

    @Override
    protected void typeNotFound(final CodeList<?> type, final Filter<Object> filter, final Collection<String> names) {
        for (final Expression<? super Object, ?> f : filter.getExpressions()) {
            visit(f, names);
        }
    }

    @Override
    protected void typeNotFound(final String type, final Expression<Object, ?> expression, final Collection<String> names) {
        for (final Expression<? super Object, ?> p : expression.getParameters()) {
            visit(p, names);
        }
    }
}
