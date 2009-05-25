/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.isowrapper.geometries;

import java.util.List;
import java.util.Set;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.CompositeCurve;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.ParamForPoint;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveBoundary;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOJTSCurve extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.LineString> implements Curve{

    public ISOJTSCurve(com.vividsolutions.jts.geom.LineString ln, CoordinateReferenceSystem crs) {
        super(ln,crs);
    }

    @Override
    public List<? extends CurveSegment> getSegments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrientableCurve[] getProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CurveBoundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Curve getPrimitive() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CompositeCurve getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getOrientation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Primitive> getContainedPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Primitive> getContainingPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Complex> getComplexes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getStartPoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getEndPoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] getTangent(double s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getStartParam() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getEndParam() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getStartConstructiveParam() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getEndConstructiveParam() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition forConstructiveParam(double cp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition forParam(double s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ParamForPoint getParamForPoint(DirectPosition p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double length(Position point1, Position point2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double length(double cparam1, double cparam2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LineString asLineString(double maxSpacing, double maxOffset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
