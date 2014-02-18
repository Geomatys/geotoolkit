/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.geotoolkit.factory.HintsPending;
import org.junit.Test;

import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SRIDTest extends TestCase{

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testSRID(){

        final String epsg4326 = "EPSG:4326";

        final int srid = SRIDGenerator.toSRID(epsg4326, Version.V1);
        assertEquals(srid, 4326);
        String retour = SRIDGenerator.toSRS(srid, Version.V1);
        assertEquals(epsg4326, retour);

        final byte[] bytes = SRIDGenerator.toBytes(srid, Version.V1);
        retour = SRIDGenerator.toSRS(bytes, 0);
        assertEquals(epsg4326, retour);

    }

    @Test
    public void testCRSAccessSRID() throws FactoryException{

        final Point geom = GF.createPoint(new Coordinate(50, 27));

        //should not raise a log
        geom.setSRID(-1);
        CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(geom);
        assertNull(crs);

        //should not raise a log
        geom.setSRID(0);
        crs = JTS.findCoordinateReferenceSystem(geom);
        assertNull(crs);

        geom.setSRID(4326);
        crs = JTS.findCoordinateReferenceSystem(geom);
        assertNotNull(crs);
        assertEquals(CRS.decode("EPSG:4326"), crs);

    }

    @Test
    public void testCRSAccessDirect() throws FactoryException{

        final CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");

        final Point geom = GF.createPoint(new Coordinate(50, 27));

        //should not raise a log
        CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(geom);
        assertNull(crs);

        geom.setUserData(epsg4326);
        crs = JTS.findCoordinateReferenceSystem(geom);
        assertEquals(epsg4326, crs);
        
    }

    @Test
    public void testCRSAccessMap() throws NoSuchAuthorityCodeException, FactoryException{

        final CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");

        final Point geom = GF.createPoint(new Coordinate(50, 27));

        //should not raise a log
        CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(geom);
        assertNull(crs);

        final Map<String,Object> map = new HashMap<String, Object>();
        map.put(HintsPending.JTS_GEOMETRY_CRS, epsg4326);

        geom.setUserData(map);
        crs = JTS.findCoordinateReferenceSystem(geom);
        assertEquals(epsg4326, crs);

    }

}
