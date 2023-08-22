/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.ogc.xml;

import java.util.Arrays;
import java.util.List;
import org.apache.sis.geometry.wrapper.jts.JTS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.gml.xml.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.Expression;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author guilhem
 */
@Deprecated
public interface BBOX extends SpatialOperator {
    public static BBOX wrap(final BinarySpatialOperator<Object> filter) {
        if (filter instanceof BBOX) {
            return (BBOX) filter;
        }
        return new BBOX() {
            @Override public Expression getOperand1() {return filter.getOperand1();}
            @Override public Expression getOperand2() {return filter.getOperand2();}
            @Override public String getPropertyName() {return ((ValueReference) getOperand1()).getXPath();}
            @Override public String getOperator()     {throw new UnsupportedOperationException();}
            @Override public Envelope getEnvelope()   {throw new UnsupportedOperationException();}
            @Override public double getMinX()         {return envelope().getMinX();}
            @Override public double getMinY()         {return envelope().getMinY();}
            @Override public double getMaxX()         {return envelope().getMaxX();}
            @Override public double getMaxY()         {return envelope().getMaxY();}
            @Override public String getSRS()          {envelope(); return crsName;}

            private org.locationtech.jts.geom.Envelope envelope;
            private String crsName;
            private synchronized org.locationtech.jts.geom.Envelope envelope() {
                if (envelope == null) try {
                    Geometry geometry = ((Geometry) getOperand2().apply(null));
                    envelope = geometry.getEnvelopeInternal();
                    CoordinateReferenceSystem crs = JTS.getCoordinateReferenceSystem(geometry);
                    if (crs != null) crsName = IdentifiedObjects.getIdentifierOrName(crs);
                } catch (FactoryException e) {
                }
                return envelope;
            }

            @Override public Class getResourceClass() {return null;}
            @Override public SpatialOperatorName getOperatorType() {return SpatialOperatorName.BBOX;}
            @Override public List getExpressions() {return Arrays.asList(getOperand1(), getOperand2());}
            @Override public boolean test(Object object) {throw new UnsupportedOperationException();}
        };
    }

    Expression getOperand1();

    Expression getOperand2();

    Envelope getEnvelope();

    String getPropertyName();

    String getSRS();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the minimum value for the first coordinate.
     *
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMinimum(0)
     */
    double getMinX();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the minimum value for the second ordinate.
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMinimum(1)
     */
    double getMinY();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the maximum value for the first ordinate.
     *
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMaximum(0)
     */
    double getMaxX();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the maximum value for the second coordinate.
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMaximum(1)
     */
    double getMaxY();
}
