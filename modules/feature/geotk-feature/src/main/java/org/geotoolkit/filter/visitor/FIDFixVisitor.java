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

import java.util.Collections;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.apache.sis.internal.feature.AttributeConvention;

/**
 * Used to clean PropertyEqualsTo on identifiers.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FIDFixVisitor extends DuplicatingFilterVisitor{

    @Override
    public Object visit(PropertyIsEqualTo filter, Object extraData) {

        //check if it's an id filter
        Expression exp1 = filter.getExpression1();
        Expression exp2 = filter.getExpression2();
        if(exp2 instanceof PropertyName){
            final Expression exp = exp1;
            exp1 = exp2;
            exp2 = exp;
        }

        if(exp1 instanceof PropertyName && exp2 instanceof Literal
                && ((PropertyName)exp1).getPropertyName().trim().equalsIgnoreCase(AttributeConvention.IDENTIFIER_PROPERTY.toString())) {
            //it's an id filter
            final Id idfilter = ff.id(Collections.singleton(ff.featureId( String.valueOf( ((Literal)exp2).getValue()))));
            return visit(idfilter,extraData);
        }

        return super.visit(filter, extraData);
    }

}
