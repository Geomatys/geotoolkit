/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.filter.function.*;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;


/**
 * Takes an AttributeExpression, and computes the length of the data for the attribute.
 *
 * @author dzwiers
 *
 * @module pending
 */
public class LengthFunction extends AbstractFunction {

    private static final String NAME = "length";

    public LengthFunction(final PropertyName prop) {
        super(NAME, new Expression[]{prop}, null);
    }

    @Override
    public Object evaluate(final Object feature) {

        if(feature instanceof String){
            return ((String)feature).length();
        }

        final Expression ae = getParameters().get(0);
        final String value = ae.evaluate(feature, String.class);
        if (value == null) {
            return 0;
        }
        return value.length();
    }
}
