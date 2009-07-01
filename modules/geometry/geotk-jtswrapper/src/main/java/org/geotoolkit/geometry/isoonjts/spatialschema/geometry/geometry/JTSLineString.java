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

import com.vividsolutions.jts.geom.Geometry;

import java.util.List;

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurveBoundary;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.ParamForPoint;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveBoundary;
import org.opengis.geometry.primitive.CurveInterpolation;
import org.opengis.geometry.primitive.CurveSegment;

/**
 * The {@code LineStringImpl} class implements the {@link LineString}
 * interface.
 * 
 * @author SYS Technologies
 * @author crossley
 * @version $Revision $
 */
public class JTSLineString extends AbstractJTSGenericCurve
	implements LineString, JTSGeometry {

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
        if (parent instanceof Curve)
            return (Curve) parent;
        else
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
    @Override
    public CurveSegment reverse() {
        JTSLineString result = new JTSLineString();
        PointArray pa = result.getSamplePoints();
        List list = pa.positions();
        int n = controlPoints.length();
        for (int i=n-1; i>=0; i--) {
            list.add(new GeneralDirectPosition(controlPoints.positions().get(i).getDirectPosition()));
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition getStartPoint() {
        return (DirectPosition) controlPoints.positions().get(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition getEndPoint() {
        return (DirectPosition) controlPoints.positions().get(controlPoints.length() - 1);
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
    public double length(final Position point1, final Position point2) {
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
        int n = controlPoints.length();
        com.vividsolutions.jts.geom.Coordinate [] coords =
            new com.vividsolutions.jts.geom.Coordinate[n];
        for (int i=0; i<n; i++) {
            coords[i] = JTSUtils.directPositionToCoordinate(
                (DirectPosition) controlPoints.positions().get(i));
        }
        return JTSUtils.GEOMETRY_FACTORY.createLineString(coords);
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
    public DirectPosition forConstructiveParam(double cp) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition forParam(double s) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JTSLineString{");
        if(!controlPoints.isEmpty()){
            sb.append("\n");
            for(Position pos : controlPoints){
                sb.append("\t").append(pos.toString()).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }

}
