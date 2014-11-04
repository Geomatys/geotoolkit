/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.filter.function.geometry;

import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.Unit;
import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Geographic buffer. 
 * Same as a buffer, expect it uses a buffer unit.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class BufferGeoFunction extends AbstractFunction {
    
    public BufferGeoFunction(final Expression expr1, final Expression expr2, final Expression expr3) {
        super(GeometryFunctionFactory.BUFFERGEO, new Expression[] {expr1, expr2, expr3}, null);
    }

    @Override
    public Object evaluate(final Object feature) {
        final Geometry geom;
        double width;
        final String unit;

        try {
            geom = parameters.get(0).evaluate(feature,Geometry.class);
            width = parameters.get(1).evaluate(feature,Number.class).doubleValue();
            unit = parameters.get(2).evaluate(feature,String.class);
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid function parameter."+
                    parameters.get(0)+" "+parameters.get(1)+" "+parameters.get(2));
        }

        if(width==0) return geom;
        
        final CoordinateReferenceSystem crs;
        try {
            crs = JTS.findCoordinateReferenceSystem(geom);
        } catch (FactoryException ex) {
            throw new IllegalArgumentException("Geometry crs is not set");
        }
        
        //adjust width based on crs unit
        final Unit crsUnit = crs.getCoordinateSystem().getAxis(0).getUnit();
        final Unit refUnit = Unit.valueOf(unit);
        
        //TODO find a more accurate method for use in all crs
        width = refUnit.getConverterTo(crsUnit).convert(width);
        
        final Geometry geomBuf = StaticGeometry.buffer(geom, width);
        JTS.setCRS(geomBuf, crs);
        return geomBuf;
    }
}
