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
import org.opengis.feature.PropertyType;
import org.opengis.filter.ValueReference;
import org.apache.sis.feature.internal.AttributeConvention;
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
            if (e instanceof ValueReference<?,?>) {
                final ValueReference<Object,?> pt = (ValueReference<Object,?>) e;
                //for the bbox filter the propertyName can be empty
                if (!pt.getXPath().isEmpty()) try {
                    final PropertyType desc = (PropertyType) pt.apply(ft);
                    return AttributeConvention.isGeometryAttribute(desc);
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
