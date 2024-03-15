/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.filter.function.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.feature.privy.FeatureExpression;
import org.apache.sis.util.privy.CollectionsExt;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;

/**
 * Create a list of values.
 * If the expression is null, this function will create a singleton list of evaluated object.
 * If the evaluated object is already a list, the evaluated object is returned.
 * If the expression is defined, this function will evaluate it of each of the evaluated objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ListFunction extends AbstractFunction implements FeatureExpression<Object,Object> {

    public ListFunction(final Expression expression) {
        super(OtherFunctionFactory.LIST, expression == null ? new Expression[0] : new Expression[]{expression}, null);
    }

    @Override
    public Object apply(Object candidate) {

        if (candidate == null) return null;

        if (!(candidate instanceof Iterable)) {
            candidate = Arrays.asList(candidate);
        }

        if (parameters.isEmpty()) {
            return candidate;
        } else {
            //evaluate each candidate
            final Expression exp = parameters.get(0);
            final Iterator ite = ((Iterable) candidate).iterator();
            final List lst = new ArrayList();
            while (ite.hasNext()) {
                Object f = ite.next();
                lst.add(exp.apply(f));
            }
            return lst;
        }
    }

    @Override
    public PropertyTypeBuilder expectedType(FeatureType valueType, FeatureTypeBuilder addTo) {
        PropertyTypeBuilder type;
        if (parameters.isEmpty()) {
            type = addTo.addAssociation(valueType);
        } else {
            Expression<?,?> expression = parameters.get(0);
            if (expression instanceof FeatureExpression<?,?>) {
                type = ((FeatureExpression<?,?>) expression).expectedType(valueType, addTo);
            } else {
                final PropertyType pt = CollectionsExt.singletonOrNull(valueType.getProperties(true));
                if (pt == null) return null;
                type = addTo.addProperty(pt);
            }
        }
        return type.setMinimumOccurs(0)
                   .setMaximumOccurs(Integer.MAX_VALUE)
                   .setName(OtherFunctionFactory.LIST);
    }
}
