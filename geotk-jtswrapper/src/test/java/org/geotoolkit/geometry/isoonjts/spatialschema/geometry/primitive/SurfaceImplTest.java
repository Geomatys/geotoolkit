/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import java.util.ArrayList;
import java.util.List;

import org.apache.sis.referencing.CommonCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;

import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.JTSPositionFactory;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSGeometryFactory;
import org.junit.Before;
import org.junit.Test;

public class SurfaceImplTest {
    private JTSPositionFactory postitionFactory;
    private JTSPrimitiveFactory primitiveFactory;
    private JTSGeometryFactory geometryFactory;

    @Before
    public void setUp() throws Exception {
        postitionFactory = new JTSPositionFactory( CommonCRS.WGS84.normalizedGeographic() );
        primitiveFactory = new JTSPrimitiveFactory( CommonCRS.WGS84.normalizedGeographic() );
        geometryFactory = new JTSGeometryFactory( CommonCRS.WGS84.normalizedGeographic() );
    }

    /**
     * We need to create a large surface with 7000 points
     */
    @Test
    public void testLargeSurface(){
         int NUMBER = 100000;
         double delta = 360.0 / (double) NUMBER;
         PointArray points = postitionFactory.createPointArray();
         for( double angle = 0.0; angle < 360.0; angle += delta ){
             double coordinates[] = new double[] {
                     Math.sin( Math.toRadians(angle) ),
                     Math.cos( Math.toRadians(angle) )
             };
             DirectPosition point = postitionFactory.createDirectPosition( coordinates );
             points.add( point );
         }
         List<OrientableCurve> curves = new ArrayList<OrientableCurve>();
         // A curve will be created
         // - The curve will be set as parent curves for the Curve segments
         // - Start and end params for the CurveSegments will be set
         List<CurveSegment> segmentList = new ArrayList<CurveSegment>();
         for( int i=0; i<points.size();i++){
             int start = i;
             int end = (i+1) % points.size();
             DirectPosition point1 = points.getDirectPosition( start, null );
             DirectPosition point2 = points.getDirectPosition( end, null );
             LineSegment segment = geometryFactory.createLineSegment( point1, point2 );
             segmentList.add( segment );
         }
         Curve curve = primitiveFactory.createCurve( segmentList );
         curves.add( curve);
         Ring ring = primitiveFactory.createRing( curves );
         SurfaceBoundary boundary = primitiveFactory.createSurfaceBoundary(ring,new ArrayList());
         JTSSurface surface = (JTSSurface) primitiveFactory.createSurface(boundary);

         Geometry peer = surface.computeJTSPeer();
    }
}
