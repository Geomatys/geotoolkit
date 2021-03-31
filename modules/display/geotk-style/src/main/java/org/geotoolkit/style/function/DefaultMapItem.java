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

import java.util.Collections;
import java.util.List;
import org.geotoolkit.filter.AbstractExpression;
import org.opengis.filter.Expression;
import org.opengis.util.ScopedName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultMapItem extends AbstractExpression implements MapItem{

    private Expression value;
    private double data;

    public DefaultMapItem(final double data, final Expression value){
    }

    @Override
    public ScopedName getFunctionName() {
        return null;    // TODO: which implementation?
    }

    @Override
    public List getParameters() {
        return Collections.singletonList(value);
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
    public Object apply(final Object object) {
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
}
