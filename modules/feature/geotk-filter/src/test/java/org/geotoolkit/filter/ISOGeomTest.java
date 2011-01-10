/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.referencing.CRS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.spatial.Equals;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOGeomTest {

    public ISOGeomTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void hello() throws NoSuchAuthorityCodeException, FactoryException {

         GeometryFactory factory = new GeometryFactory();
         Point[] points = new Point[3];
         points[0] = factory.createPoint(new Coordinate(70.83, 29.86));
         points[1] = factory.createPoint(new Coordinate(68.87, 31.08));
         points[2] = factory.createPoint(new Coordinate(71.96, 32.19));
         Geometry jtsGeom = factory.createMultiPoint(points);


         CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

         JTSMultiPoint isoGeom = new JTSMultiPoint();
         isoGeom.getElements().add(new JTSPoint(new GeneralDirectPosition(70.83, 29.86), crs));
         isoGeom.getElements().add(new JTSPoint(new GeneralDirectPosition(68.87, 31.08), crs));
         isoGeom.getElements().add(new JTSPoint(new GeneralDirectPosition(71.96, 32.19), crs));
         Equals filter = FF.equal(FF.literal(jtsGeom), FF.literal(isoGeom));
         boolean match = filter.evaluate(null);

         assertTrue(match);

     }

}
