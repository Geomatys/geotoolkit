/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.opengis.filter.ComparisonOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.Literal;
import org.opengis.filter.ResourceId;
import org.opengis.filter.ValueReference;

/**
 * Used to clean PropertyEqualsTo on identifiers.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FIDFixVisitor extends DuplicatingFilterVisitor {
    public static final FIDFixVisitor INSTANCE = new FIDFixVisitor();

    protected FIDFixVisitor() {
        final Function<Filter<Object>, Object> previous  = getFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO);
        final Function<Filter<Object>, Object> idVisitor = getFilterHandler(AbstractVisitor.RESOURCEID_NAME);
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO, (f) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;

            // check if it's an id filter
            Expression<Object,?> exp1 = filter.getExpressions().get(0);
            Expression<Object,?> exp2 = filter.getExpressions().get(1);
            if (exp2 instanceof ValueReference) {
                final Expression<Object,?> exp = exp1;
                exp1 = exp2;
                exp2 = exp;
            }
            if (exp1 instanceof ValueReference && exp2 instanceof Literal
                    && ((ValueReference<Object,?>) exp1).getXPath().trim().equalsIgnoreCase(AttributeConvention.IDENTIFIER))
            {
                // it's an id filter
                final ResourceId<Object> idfilter = ff.resourceId(String.valueOf(((Literal<Object,?>) exp2).getValue()));
                return idVisitor.apply(idfilter);
            }
            return previous.apply(filter);
        });
    }
}
