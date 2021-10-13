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

import org.locationtech.jts.geom.Geometry;
import javax.measure.Unit;
import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.filter.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.apache.sis.measure.Units;

/**
 * Geographic buffer.
 * Same as a buffer, expect it uses a buffer unit.
 *
 * @author Johann Sorel (Geomatys)
 */
public class BufferGeoFunction extends AbstractFunction {

    public BufferGeoFunction(final Expression expr1, final Expression expr2, final Expression expr3) {
        super(GeometryFunctionFactory.BUFFERGEO, expr1, expr2, expr3);
    }

    @Override
    public Object apply(final Object feature) {
        final Geometry geom = geometryValue(feature);
        double width = doubleValue(feature, 1);
        final String unit = stringValue(feature, 2);
        if(width==0) return geom;

        final CoordinateReferenceSystem crs;
        try {
            crs = JTS.findCoordinateReferenceSystem(geom);
        } catch (FactoryException ex) {
            throw new IllegalArgumentException("Geometry crs is not set");
        }

        //adjust width based on crs unit
        final Unit crsUnit = crs.getCoordinateSystem().getAxis(0).getUnit();
        final Unit refUnit = Units.valueOf(unit);

        //TODO find a more accurate method for use in all crs
        width = refUnit.getConverterTo(crsUnit).convert(width);

        final Geometry geomBuf = geom.buffer(width);
        JTS.setCRS(geomBuf, crs);
        return geomBuf;
    }
}
