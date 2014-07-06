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

package org.geotoolkit.geometry.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.GeometricUtilities;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeometricUtilitiesTest {
    
    private static final GeometryFactory GF = new GeometryFactory();
    
    @Test
    public void envelopeToJTSSmallTest(){
        GeneralEnvelope env;
        Geometry expected;
        Geometry result;
        
        //we should have the same result whatever method used
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -30, 50);
        env.setRange(1, -5, 20);
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-30, -5),
                    new Coordinate(-30, 20),
                    new Coordinate( 50, 20),
                    new Coordinate( 50, -5),
                    new Coordinate(-30, -5) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.NONE);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.EXPAND);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.SPLIT);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.CONTIGUOUS);
        assertTrue(result.equalsExact(expected));
    }
    
    @Test
    public void envelopeToJTSLargeTest(){
        
        GeneralEnvelope env;
        Geometry expected;
        Geometry result;
        
        //we should have the same result whatever method used
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -160, 170);
        env.setRange(1, -70, 80);
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-160, -70),
                    new Coordinate(-160,  80),
                    new Coordinate( 170,  80),
                    new Coordinate( 170, -70),
                    new Coordinate(-160, -70) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.NONE);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-160, -70),
                    new Coordinate(-160,  80),
                    new Coordinate(   5,  80),
                    new Coordinate( 170,  80),
                    new Coordinate( 170, -70),
                    new Coordinate(   5, -70),
                    new Coordinate(-160, -70) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.EXPAND);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.SPLIT);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.CONTIGUOUS);
        assertTrue(result.equalsExact(expected));
    }
    
    @Test
    public void envelopeToJTSWorldTest(){
        
        GeneralEnvelope env;
        Geometry expected;
        Geometry result;
        
        //we should have the same result whatever method used
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, -90),
                    new Coordinate(-180,  90),
                    new Coordinate( 180,  90),
                    new Coordinate( 180, -90),
                    new Coordinate(-180, -90) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.NONE);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, -90),
                    new Coordinate(-180,  90),
                    new Coordinate(- 60,  90),
                    new Coordinate(  60,  90),
                    new Coordinate( 180,  90),
                    new Coordinate( 180, -90),
                    new Coordinate(  60, -90),
                    new Coordinate(- 60, -90),
                    new Coordinate(-180, -90) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.EXPAND);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.SPLIT);
        assertTrue(result.equalsExact(expected));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.CONTIGUOUS);
        assertTrue(result.equalsExact(expected));
    }
        
    @Test
    public void envelopeToJTSMeridianOverlapsTest(){
        
        GeneralEnvelope env;
        Geometry expected;
        Geometry result;
        
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 170, 190);
        env.setRange(1, 30, 40);
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate( 170, 30),
                    new Coordinate( 170, 40),
                    new Coordinate( 190, 40),
                    new Coordinate( 190, 30),
                    new Coordinate( 170, 30) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.NONE);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, 30),
                    new Coordinate(-180, 40),
                    new Coordinate(- 60, 40),
                    new Coordinate(  60, 40),
                    new Coordinate( 180, 40),
                    new Coordinate( 180, 30),
                    new Coordinate(  60, 30),
                    new Coordinate(- 60, 30),
                    new Coordinate(-180, 30) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.EXPAND);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createMultiPolygon(new Polygon[]{
            GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, 30),
                    new Coordinate(-180, 40),
                    new Coordinate(-170, 40),
                    new Coordinate(-170, 30),
                    new Coordinate(-180, 30) })),
            GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate( 170, 30),
                    new Coordinate( 170, 40),
                    new Coordinate( 180, 40),
                    new Coordinate( 180, 30),
                    new Coordinate( 170, 30) }))
            });
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.SPLIT);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate( 170, 30),
                    new Coordinate( 170, 40),
                    new Coordinate( 190, 40),
                    new Coordinate( 190, 30),
                    new Coordinate( 170, 30) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.CONTIGUOUS);
        assertTrue(result.equalsExact(expected));
    }
        
    @Test
    public void envelopeToJTSMeridianWrapAroundTest(){
        
        GeneralEnvelope env;
        Geometry expected;
        Geometry result;
        
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 170, -170);
        env.setRange(1, 30, 40);
        
        //geotoolkit makes an automatic correction here when using the getMin/getMax methods
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, 30),
                    new Coordinate(-180, 40),
                    new Coordinate( 180, 40),
                    new Coordinate( 180, 30),
                    new Coordinate(-180, 30) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.NONE);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, 30),
                    new Coordinate(-180, 40),
                    new Coordinate(- 60, 40),
                    new Coordinate(  60, 40),
                    new Coordinate( 180, 40),
                    new Coordinate( 180, 30),
                    new Coordinate(  60, 30),
                    new Coordinate(- 60, 30),
                    new Coordinate(-180, 30) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.EXPAND);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createMultiPolygon(new Polygon[]{
            GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate(-180, 30),
                    new Coordinate(-180, 40),
                    new Coordinate(-170, 40),
                    new Coordinate(-170, 30),
                    new Coordinate(-180, 30) })),
            GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate( 170, 30),
                    new Coordinate( 170, 40),
                    new Coordinate( 180, 40),
                    new Coordinate( 180, 30),
                    new Coordinate( 170, 30) }))
            });
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.SPLIT);
        assertTrue(result.equalsExact(expected));
        
        expected = GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                    new Coordinate( 170, 30),
                    new Coordinate( 170, 40),
                    new Coordinate( 190, 40),
                    new Coordinate( 190, 30),
                    new Coordinate( 170, 30) }));
        result = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.CONTIGUOUS);
        assertTrue(result.equalsExact(expected));
        
    }
    
    
}
