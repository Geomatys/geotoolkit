/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;


/**
 * Abstract implementation of ExpressionVisitor that simply walks the data structure.
 * <p>
 * This class implements the full ExpressionVisitor interface and will visit every expression member of a
 * Expression object. This class performs no actions and is not intended to be used directly, instead
 * extend it and override the methods for the Expression type you are interested in. Remember to call the
 * super method if you want to ensure that the entire expression tree is still visited.
 *
 * @author jody
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public abstract class DefaultExpressionVisitor implements ExpressionVisitor {

    public DefaultExpressionVisitor() {
    }

    @Override
    public Object visit(final NilExpression expression, final Object data) {
        return data;
    }

    @Override
    public Object visit(final Add expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Divide expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Function expression, Object data) {
        if (expression.getParameters() != null) {
            for (Expression parameter : expression.getParameters()) {
                data = parameter.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final Literal expression, final Object data) {
        return data;
    }

    @Override
    public Object visit(final Multiply expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final PropertyName expression, final Object data) {
        return data;
    }

    @Override
    public Object visit(final Subtract expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        data = expression.getExpression2().accept(this, data);
        return data;
    }
}
