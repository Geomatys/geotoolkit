/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import org.opengis.filter.expression.Expression;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultInterpolationPoint implements InterpolationPoint{

    private final Expression value;
    private final double data;
    
    public DefaultInterpolationPoint(final double data, final Expression value){
        ensureNonNull("value", value);
        this.value = value;
        this.data = data;
    }
    
    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public double getData() {
        return data;
    }

}
