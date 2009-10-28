/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/geometry/GeometryFactoryImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSEnvelope;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPrimitive;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.Arc;
import org.opengis.geometry.coordinate.ArcByBulge;
import org.opengis.geometry.coordinate.ArcString;
import org.opengis.geometry.coordinate.ArcStringByBulge;
import org.opengis.geometry.coordinate.BSplineCurve;
import org.opengis.geometry.coordinate.BSplineSurface;
import org.opengis.geometry.coordinate.Geodesic;
import org.opengis.geometry.coordinate.GeodesicString;
import org.opengis.geometry.coordinate.GeometryFactory;
import org.opengis.geometry.coordinate.KnotType;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.coordinate.Tin;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;


/**
 * The {@code GeometryFactoryImpl} class/interface...
 * 
 * @author SYS Technologies
 * @author crossley
 * @version $Revision $
 * @module pending
 */
public class JTSGeometryFactory extends Factory implements GeometryFactory {
    
    private final CoordinateReferenceSystem crs;
    
    /**
     * No argument constructor for FactorySPI
     */
    public JTSGeometryFactory(){
        this( DefaultGeographicCRS.WGS84);
    }

    /**
     * Direct constructor for test cases
     */
    public JTSGeometryFactory( CoordinateReferenceSystem crs ) {
        this.crs = crs;
    }

    //*************************************************************************
    //  implement the GeometryFactory interface
    //*************************************************************************
    
    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }
    

    public Position createPosition( DirectPosition point ) {
        return new GeneralDirectPosition( point );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition createDirectPosition() {
        return new GeneralDirectPosition(crs);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition createDirectPosition(final double[] coordinates) {
        GeneralDirectPosition pos = new GeneralDirectPosition(coordinates);
        pos.setCoordinateReferenceSystem(crs);
        return pos;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope createEnvelope(
            final DirectPosition lowerCorner, 
            final DirectPosition upperCorner) {
        return new JTSEnvelope(lowerCorner, upperCorner);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LineSegment createLineSegment(final Position startPoint, final Position endPoint) {
        JTSLineSegment line = new JTSLineSegment();
        line.getControlPoints().add( startPoint );
        line.getControlPoints().add( endPoint );
        
        return line;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LineString createLineString(final List/*<Position>*/ points) {
        LineString result = new JTSLineString();
        PointArray pa = result.getControlPoints();
        List list = pa;
        Iterator it = points.iterator();
        while (it.hasNext()) {
//            Object o = it.next();
//            if (o instanceof DirectPosition) {
//                list.add(o);
//            } else if (o instanceof Position) {
//                Position p = (Position) o;
//                DirectPosition dp = p.getPosition();
//                /*if (dp == null) {
//                    dp = p.getIndirect().getPosition();
//                }*/
//                list.add(dp);
//            }
            Position position = (Position) it.next();
            DirectPosition directPosition = position.getDirectPosition();
            list.add(directPosition);
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Geodesic createGeodesic(final Position startPoint, final Position endPoint) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeodesicString createGeodesicString(final List/*<Position>*/ points) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Arc createArc(
            final Position startPoint, 
            final Position midPoint, 
            final Position endPoint) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Arc createArc(
            final Position startPoint, 
            final Position endPoint, 
            final double bulge, 
            final double[] normal) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ArcString createArcString(final List/*<Position>*/ points) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ArcByBulge createArcByBulge(
            final Position startPoint, 
            final Position endPoint, 
            final double bulge, 
            final double[] normal) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ArcStringByBulge createArcStringByBulge(
            final List/*<Position>*/ points, 
            final double[] bulges,
            final List/*<double[]>*/ normals) {
        return null;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public BSplineCurve createBSplineCurve(int arg0, PointArray arg1, List arg2, KnotType arg3)
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Polygon createPolygon(SurfaceBoundary boundary) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        JTSPolygon result = new JTSPolygon(boundary);
        return result;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Polygon createPolygon(SurfaceBoundary boundary, Surface spanningSurface)
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        JTSPolygon result = new JTSPolygon(boundary, Collections.singletonList(spanningSurface));
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Tin createTin(Set arg0, Set arg1, Set arg2, double arg3)
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param exterior
     * @param interiors
     * @return SurfaceBoundary
     * @throws MismatchedReferenceSystemException
     * @see org.opengis.geometry.coordinate.Factory#createSurfaceBoundary(org.opengis.geometry.primitive.Ring, java.util.List)
     */
    public SurfaceBoundary createSurfaceBoundary(Ring exterior, List interiors) throws MismatchedReferenceSystemException {
        return new JTSSurfaceBoundary(crs, exterior, (Ring []) interiors.toArray(new Ring[interiors.size()]));
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public MultiPrimitive createMultiPrimitive() {
        return new JTSMultiPrimitive();
    }
    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public PolyhedralSurface createPolyhedralSurface(final List<Polygon> polygons)
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        JTSPolyhedralSurface result = new JTSPolyhedralSurface(crs);
        List<?> cast = (List<?>) polygons;
        result.getPatches().addAll( (List<JTSPolygon>) cast);
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BSplineSurface createBSplineSurface( List arg0, int[] arg1, List[] arg2, KnotType arg3 ) throws MismatchedReferenceSystemException, MismatchedDimensionException {
        throw new UnsupportedOperationException(
            "This is the JTS Wrapper Factory which only supports implementations that align with the Simple Feature for SQL Specification.");
    }

}
