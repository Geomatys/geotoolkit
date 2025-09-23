/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.filter.ValueReference;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.feature.internal.shared.FeatureExpression;
import org.apache.sis.feature.internal.shared.FeatureProjectionBuilder;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.Expression;
import org.opengis.util.CodeList;

/**
 *
 * @author Guilhem Legal (Geomatys)
 *
 * @deprecated Not used anymore.
 */
@Deprecated
public class IsValidSpatialFilterVisitor extends AbstractVisitor<Object,Boolean> {
    public IsValidSpatialFilterVisitor(final FeatureType ft) {
        setBinarySpatialHandlers((f) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            final Expression<Object,?> e = filter.getOperand1();
            if (e instanceof FeatureExpression<?,?> property && e instanceof ValueReference<?,?> pt) {
                //for the bbox filter the propertyName can be empty
                if (!pt.getXPath().isEmpty()) try {
                    var item = property.expectedType(new FeatureProjectionBuilder(ft, null));
                    if (item != null) {
                        return AttributeConvention.isGeometryAttribute(item.builder().build());
                    }
                } catch (PropertyNotFoundException ex) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    @Override
    protected Boolean typeNotFound(final CodeList<?> type, final Filter<Object> filter) {
        return Boolean.TRUE;
    }

    @Override
    protected Boolean typeNotFound(final String type, final Expression<Object,?> expression) {
        return Boolean.TRUE;
    }
}
