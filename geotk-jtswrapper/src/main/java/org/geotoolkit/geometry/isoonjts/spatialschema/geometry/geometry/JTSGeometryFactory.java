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

import org.apache.sis.referencing.CommonCRS;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSEnvelope;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPrimitive;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.coordinate.MismatchedCoordinateMetadataException;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;


/**
 * The {@code GeometryFactoryImpl} class/interface...
 *
 * @author SYS Technologies
 * @author crossley
 */
public class JTSGeometryFactory {

    private final CoordinateReferenceSystem crs;

    /**
     * No argument constructor for FactorySPI
     */
    public JTSGeometryFactory(){
        this( CommonCRS.WGS84.normalizedGeographic());
    }

    /**
     * Direct constructor for test cases
     */
    public JTSGeometryFactory( final CoordinateReferenceSystem crs ) {
        this.crs = crs;
    }

    //*************************************************************************
    //  implement the GeometryFactory interface
    //*************************************************************************

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    public DirectPosition createPosition( final DirectPosition point ) {
        return new GeneralDirectPosition( point );
    }

    public DirectPosition createDirectPosition() {
        return new GeneralDirectPosition(crs);
    }

    public DirectPosition createDirectPosition(final double[] coordinates) {
        GeneralDirectPosition pos = new GeneralDirectPosition(coordinates);
        pos.setCoordinateReferenceSystem(crs);
        return pos;
    }

    public Envelope createEnvelope(
            final DirectPosition lowerCorner,
            final DirectPosition upperCorner) {
        return new JTSEnvelope(lowerCorner, upperCorner);
    }

    public LineSegment createLineSegment(final DirectPosition startPoint, final DirectPosition endPoint) {
        JTSLineSegment line = new JTSLineSegment();
        line.getControlPoints().add( startPoint );
        line.getControlPoints().add( endPoint );

        return line;
    }

    public LineString createLineString(final List/*<DirectPosition>*/ points) {
        LineString result = new JTSLineString(crs);
        PointArray pa = result.getControlPoints();
        List list = pa;
        Iterator it = points.iterator();
        while (it.hasNext()) {
//            Object o = it.next();
//            if (o instanceof DirectPosition) {
//                list.add(o);
//            } else if (o instanceof DirectPosition) {
//                DirectPosition p = (DirectPosition) o;
//                DirectPosition dp = p.getPosition();
//                /*if (dp == null) {
//                    dp = p.getIndirect().getPosition();
//                }*/
//                list.add(dp);
//            }
            DirectPosition position = (DirectPosition) it.next();
            list.add(position);
        }
        return result;
    }

    public Polygon createPolygon(final SurfaceBoundary boundary) throws MismatchedCoordinateMetadataException,
            MismatchedDimensionException {
        JTSPolygon result = new JTSPolygon(boundary);
        return result;
    }

    public Polygon createPolygon(final SurfaceBoundary boundary, final Surface spanningSurface)
            throws MismatchedCoordinateMetadataException, MismatchedDimensionException {
        JTSPolygon result = new JTSPolygon(boundary, Collections.singletonList(spanningSurface));
        return result;
    }

    public SurfaceBoundary createSurfaceBoundary(final Ring exterior, final List interiors) throws MismatchedCoordinateMetadataException {
        return new JTSSurfaceBoundary(crs, exterior, (Ring []) interiors.toArray(new Ring[interiors.size()]));
    }

    public MultiPrimitive createMultiPrimitive() {
        return new JTSMultiPrimitive();
    }

    public PolyhedralSurface createPolyhedralSurface(final List<Polygon> polygons)
            throws MismatchedCoordinateMetadataException, MismatchedDimensionException {
        JTSPolyhedralSurface result = new JTSPolyhedralSurface(crs);
        List<?> cast = (List<?>) polygons;
        result.getPatches().addAll( (List<JTSPolygon>) cast);
        return result;
    }
}
