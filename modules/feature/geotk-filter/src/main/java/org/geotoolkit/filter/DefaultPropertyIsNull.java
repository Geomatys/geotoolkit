/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;

/**
 * Immutable "is null" filter.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultPropertyIsNull implements PropertyIsNull {

    private final Expression exp;

    public DefaultPropertyIsNull(Expression exp) {
        if(exp == null){
            throw new NullPointerException("Expression can not be null");
        }
        this.exp = exp;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getExpression() {
        return exp;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(Object object) {
        Object obj = exp.evaluate(object);
        return obj == null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
