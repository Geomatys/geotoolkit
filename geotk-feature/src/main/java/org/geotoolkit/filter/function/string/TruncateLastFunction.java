/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.filter.function.string;

import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.filter.function.other.StaticUtils;
import org.opengis.filter.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TruncateLastFunction extends AbstractFunction {

    public TruncateLastFunction(final Expression expression, final Expression lenghtExp) {
        super(StringFunctionFactory.TRUNCATE_LAST, expression, lenghtExp);
    }

    @Override
    public Object apply(final Object feature) {
        final String[] args = stringValues(feature, 1);
        final int length = (Integer) parameters.get(1).apply(feature);
        return StaticUtils.strTruncateLast(args[0], length);
    }
}
