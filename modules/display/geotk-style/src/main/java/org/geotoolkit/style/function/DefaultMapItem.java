/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.style.function;

import org.geotoolkit.filter.AbstractExpression;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultMapItem extends AbstractExpression implements MapItem{

    private Expression value;
    private double data;

    public DefaultMapItem(double data, Expression value){

    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public double getData() {
        return data;
    }

    @Override
    public Object evaluate(Object object) {
        if(object instanceof Number){
            final Number num = (Number) object;
            if(num.doubleValue() == data){
                return value;
            }
            return num;
        }else{
            return object;
        }
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return null;
    }

}
