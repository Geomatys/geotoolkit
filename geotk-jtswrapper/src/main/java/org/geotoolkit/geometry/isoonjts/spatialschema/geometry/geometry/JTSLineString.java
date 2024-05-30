/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/geometry/LineStringImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry;

import org.locationtech.jts.geom.Geometry;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurveBoundary;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.internal.jaxb.DirectPositionAdapter;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.ParamForPoint;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveBoundary;
import org.opengis.geometry.primitive.CurveInterpolation;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.geometry.SampledByPoints;

/**
 * The {@code LineStringImpl} class implements the {@link LineString}
 * interface.
 *
 * @author SYS Technologies
 * @author crossley
 * @version $Revision $
 * @module
 */
public class JTSLineString extends AbstractJTSGenericCurve
    implements LineString, Primitive, SampledByPoints {

    /**
     * Points comprising this geometry.
     */
    private PointArray controlPoints;

    //*************************************************************************
    //  Constructors
    //*************************************************************************

    /**
     * Creates a new {@code LineStringImpl}.
     */
    public JTSLineString() {
        controlPoints = new JTSPointArray();
        ((JTSPointArray)controlPoints).setJTSParent(this);
    }

    /**
     * Creates a new {@code LineStringImpl}.
     */
    public JTSLineString(final CoordinateReferenceSystem crs) {
        super(crs);
        controlPoints = new JTSPointArray();
        ((JTSPointArray)controlPoints).setJTSParent(this);
    }

    //*************************************************************************
    //  implement the *** interface
    //*************************************************************************

    /**
     * {@inheritDoc }
     */
    @Override
    public PointArray getControlPoints() {
        return controlPoints;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List asLineSegments() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CurveBoundary getBoundary() {
        return new JTSCurveBoundary(null, new JTSPoint(getStartPoint()), new JTSPoint(getEndPoint()));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Curve getCurve() {

        /*if (parent instanceof Curve)
            return (Curve) parent;
        else*/
            return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CurveInterpolation getInterpolation() {
            return CurveInterpolation.LINEAR;
    }

    /**
     * A line string doesn't have any continuous derivatives since the
     * derivative has dicontinuities at the vertices.
     */
    public int getNumDerivativeInterior() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getNumDerivativesAtEnd() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getNumDerivativesAtStart() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PointArray getSamplePoints() {
        return controlPoints;
    }

    /**
     * {@inheritDoc }
     */
    public CurveSegment reverse() {
        JTSLineString result = new JTSLineString();
        PointArray pa = result.getSamplePoints();
        List list = pa;
        int n = controlPoints.size();
        for (int i=n-1; i>=0; i--) {
            list.add(new GeneralDirectPosition(controlPoints.get(i)));
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition getStartPoint() {
        return (DirectPosition) controlPoints.get(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition getEndPoint() {
        return (DirectPosition) controlPoints.get(controlPoints.size() - 1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double [] getTangent(final double s) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getStartParam() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getEndParam() {
        return 1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getStartConstructiveParam() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getEndConstructiveParam() {
        return 1;
    }

    /**
     * Not implemented.  Returns null.
     */
    /*public DirectPosition getConstructiveParam(double cp) {
        return null;
    }*/

    /**
     * Not implemented.  Returns null.
     */
    /*public DirectPosition getParam(double s) {
        return null;
    }*/

    /**
     * {@inheritDoc }
     */
    @Override
    public ParamForPoint getParamForPoint(final DirectPosition p) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double length(final DirectPosition point1, final DirectPosition point2) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double length(final double cparam1, final double cparam2) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LineString asLineString(final double maxSpacing, final double maxOffset) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Geometry computeJTSPeer() {
        int n = controlPoints.size();
        org.locationtech.jts.geom.Coordinate [] coords =
            new org.locationtech.jts.geom.Coordinate[n];
        for (int i=0; i<n; i++) {
            coords[i] = JTSUtils.directPositionToCoordinate(
                (DirectPosition) controlPoints.get(i));
        }
        final org.locationtech.jts.geom.LineString result = JTSUtils.GEOMETRY_FACTORY.createLineString(coords);
        CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        if (crs != null) {
            final int srid = SRIDGenerator.toSRID(crs, Version.V1);
            result.setSRID(srid);
        }
        return result;
    }

    /**
     * We'd like to return "1", but the first derivative is undefined at the
     * corners.  The subclass, LineSegment, can override this to return 1.
     */
    @Override
    public int getNumDerivativesInterior() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition forConstructiveParam(final double cp) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition forParam(final double s) {
        return null;
    }

    public void applyCRSOnChild() {
        if (controlPoints != null) {
            var newPositions = new ArrayList<DirectPosition>();
            for (DirectPosition pos : controlPoints) {
                if (pos instanceof GeneralDirectPosition) {
                    ((GeneralDirectPosition) pos).setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                    newPositions.add(pos);
                }
            }
            controlPoints.clear();
            controlPoints.addAll(newPositions);
        }
    }

    @XmlElement(name="pos", namespace="http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(DirectPositionAdapter.class)
    public List<DirectPosition> getPositions() {
        return controlPoints;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JTSLineString{");
        if(!controlPoints.isEmpty()){
            sb.append("\n");
            for(DirectPosition pos : controlPoints){
                sb.append("\t").append(pos.toString()).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;

        if (object instanceof JTSLineString & super.equals(object)) {
            JTSLineString that = (JTSLineString) object;
            return Objects.equals(this.controlPoints, that.controlPoints);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.controlPoints != null ? this.controlPoints.hashCode() : 0);
        return hash;
    }

    public Set<Primitive> getContainedPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Primitive> getContainingPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Complex> getComplexes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OrientablePrimitive[] getProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
