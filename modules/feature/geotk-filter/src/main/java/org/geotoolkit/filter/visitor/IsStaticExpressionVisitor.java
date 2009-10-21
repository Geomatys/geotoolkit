/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.List;

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
 * Check if an expression is static (ie does not contain a PropertyName expression).
 * <p>
 * This visitor will "short-circuit" the moment it finds a PropertyName expression
 * and will not need to visit the entire data structure.
 * <p>
 * Example:<pre><code>
 * if( filter.accepts( IsStaticExpressionVisitor.VISITOR, null ) ){
 *     Color color = expression.evaulate( null, Color.class );
 *     ...
 * }
 * </code></pre>
 * 
 * @author Jody
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IsStaticExpressionVisitor implements ExpressionVisitor {

    public static final IsStaticExpressionVisitor VISITOR = new IsStaticExpressionVisitor();

    /**
     * visit each expression and check that they are static
     */
    protected IsStaticExpressionVisitor() {
    }

    /**
     * visit each expression and check that they are static
     */
    @Override
    public Boolean visit(NilExpression expression, Object data) {
        return true;
    }

    /**
     * visit each expression and check that they are static
     */
    @Override
    public Boolean visit(Add expression, Object data) {
        return (Boolean)expression.getExpression1().accept(this, data) &&
               (Boolean)expression.getExpression2().accept(this, data);
    }

    /**
     * visit each expression and check that they are static
     */
    @Override
    public Boolean visit(Divide expression, Object data) {
        return (Boolean)expression.getExpression1().accept(this, data) &&
               (Boolean)expression.getExpression2().accept(this, data);
    }

    /**
     * Visit each parameter and check if they are static
     */
    @Override
    public Boolean visit(Function expression, Object data) {
        final List<Expression> parameters = expression.getParameters();
        if (parameters != null) {
            for (final Expression parameter : parameters) {
                if(!(Boolean)parameter.accept(this, data)) return false;
            }
        }
        return true;
    }

    /**
     * Literal expressions are always static.
     * @return true
     */
    @Override
    public Boolean visit(Literal expression, Object data) {
        return true;
    }

    /**
     * visit each expression and check that they are static.
     * @return true if getExpression1 and getExpression2 are static
     */
    @Override
    public Boolean visit(Multiply expression, Object data) {
        return (Boolean)expression.getExpression1().accept(this, data) &&
               (Boolean)expression.getExpression2().accept(this, data);
    }

    /**
     * If even a single PropertyName is found in the expression
     * the expression is not static.
     * @return false
     */
    @Override
    public Boolean visit(PropertyName expression, Object data) {
        return false;
    }

    /**
     * visit each expression and check that they are static.
     * @return true if getExpression1 and getExpression2 are static
     */
    @Override
    public Boolean visit(Subtract expression, Object data) {
        return (Boolean)expression.getExpression1().accept(this, data) &&
               (Boolean)expression.getExpression2().accept(this, data);
    }
}
