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

import org.apache.sis.filter.privy.FunctionNames;
import org.opengis.filter.Expression;

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
 */
public class IsStaticExpressionVisitor extends AbstractVisitor<Object,Boolean> {

    public static final IsStaticExpressionVisitor VISITOR = new IsStaticExpressionVisitor();

    protected IsStaticExpressionVisitor() {
        super(false, true);
        setExpressionHandler(FunctionNames.Literal,        (e) -> Boolean.TRUE);
        setExpressionHandler(FunctionNames.ValueReference, (e) -> Boolean.FALSE);
    }

    @Override
    protected Boolean typeNotFound(final String name, final Expression<Object,?> expression) {
        for (final Expression<? super Object, ?> parameter : expression.getParameters()) {
            if (!visit(parameter)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
