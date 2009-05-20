/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author sorel
 */
public class ISOJTSCurve extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.LineString> implements Curve{

    public ISOJTSCurve(com.vividsolutions.jts.geom.LineString ln) {
        super(ln);
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
