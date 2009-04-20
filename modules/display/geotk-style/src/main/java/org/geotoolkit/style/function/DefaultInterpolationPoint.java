/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.style.function;

import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultInterpolationPoint implements InterpolationPoint{

    private final Expression value;
    private final double data;
    
    public DefaultInterpolationPoint(Expression value, double data){
        if(value == null){
           throw new NullPointerException();
        }
        this.value = value;
        this.data = data;
    }
    
    public Expression getValue() {
        return value;
    }

    public double getData() {
        return data;
    }

}
