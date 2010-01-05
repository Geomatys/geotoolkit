/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import java.util.HashSet;
import java.util.List;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.opengis.feature.Feature;

/**
 * Tests the ability of the datastore to cope with 3D data
 * 
 * @author Andrea Aime - OpenGeo
 * @module pending
 */
public abstract class JDBC3DTest extends JDBCTestSupport {

    protected static final String LINE3D = "line3d";

    protected static final String POLY3D = "poly3d";

    protected static final String POINT3D = "point3d";

    protected static final String ID = "id";

    protected static final String GEOM = "geom";

    protected static final String NAME = "name";

    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    protected SimpleFeatureType poly3DType;

    protected SimpleFeatureType line3DType;

    protected SingleCRS horizontal;

    @Override
    protected abstract JDBC3DTestSetup createTestSetup();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // we use 4359 because it's using degrees, 4327 uses dms
        line3DType = FeatureTypeUtilities.createType(dataStore.getNamespaceURI() + "." + tname(LINE3D),
                aname(ID) + ":0," + aname(GEOM) + ":LineString:srid=4359," + aname(NAME)
                        + ":String");
        poly3DType = FeatureTypeUtilities.createType(dataStore.getNamespaceURI() + "." + tname(POLY3D),
                aname(ID) + ":0," + aname(GEOM) + ":Polygon:srid=4359," + aname(NAME) + ":String");

        // get the horizontal component of 4359
        horizontal = CRS.getHorizontalCRS(CRS.decode("EPSG:4359"));
    }

    public void testSchema() throws Exception {
        SimpleFeatureType schema = (SimpleFeatureType) dataStore.getFeatureType(tname(LINE3D));
        CoordinateReferenceSystem crs = schema.getGeometryDescriptor()
                .getCoordinateReferenceSystem();
        assertEquals(new Integer(4359), CRS.lookupEpsgCode(crs, false));
        assertEquals(new Integer(4359), schema.getGeometryDescriptor().getUserData().get(
                JDBCDataStore.JDBC_NATIVE_SRID));
    }

    public void testReadPoint() throws Exception {
        FeatureCollection fc = dataStore.createSession(false).getFeatureCollection(QueryBuilder.all(nsname(POINT3D)));
        FeatureIterator<SimpleFeature> fr = fc.iterator();
        assertTrue(fr.hasNext());
        Point p = (Point) fr.next().getDefaultGeometry();
        assertEquals(new Coordinate(1, 1, 1), p.getCoordinate());
        fr.close();
    }

    public void testReadLine() throws Exception {
        FeatureCollection fc = dataStore.createSession(false).getFeatureCollection(QueryBuilder.all(nsname(LINE3D)));
        FeatureIterator<SimpleFeature> fr = fc.iterator();
        assertTrue(fr.hasNext());
        LineString ls = (LineString) fr.next().getDefaultGeometry();
        // 1 1 0, 2 2 0, 4 2 1, 5 1 1
        assertEquals(4, ls.getCoordinates().length);
        assertEquals(new Coordinate(1, 1, 0), ls.getCoordinateN(0));
        assertEquals(new Coordinate(2, 2, 0), ls.getCoordinateN(1));
        assertEquals(new Coordinate(4, 2, 1), ls.getCoordinateN(2));
        assertEquals(new Coordinate(5, 1, 1), ls.getCoordinateN(3));
        fr.close();
    }

    public void testWriteLine() throws Exception {
        // build a 3d line
        GeometryFactory gf = new GeometryFactory();
        LineString ls = gf.createLineString(new Coordinate[] { new Coordinate(0, 0, 0),
                new Coordinate(1, 1, 1) });

        // build a feature around it
        SimpleFeature newFeature = SimpleFeatureBuilder.build(line3DType, new Object[] { 2, ls,
                "l3" }, null);

        // insert it
        FeatureCollection col = new DefaultFeatureCollection("", null, Feature.class);
        col.add(newFeature);

        List<FeatureId> fids = DataUtilities.write(dataStore.getFeatureWriterAppend(nsname(LINE3D)), col);


        // retrieve it back
        FeatureIterator<SimpleFeature> fi = dataStore.getFeatureReader(
                QueryBuilder.filtered(nsname(LINE3D), FF.id(new HashSet<FeatureId>(fids))));
        assertTrue(fi.hasNext());
        SimpleFeature f = fi.next();
        assertTrue(ls.equals((Geometry) f.getDefaultGeometry()));
        fi.close();
    }

    /**
     * Creates the polygon schema and then inserts a 3D geometry into the
     * datastore and retrieves it back to make sure 3d data is really handled as
     * such
     * 
     * @throws Exception
     */
    public void testCreateSchemaAndInsert() throws Exception {
        dataStore.createSchema(poly3DType.getName(),poly3DType);
        SimpleFeatureType actualSchema = (SimpleFeatureType) dataStore.getFeatureType(nsname(POLY3D));
        assertFeatureTypesEqual(poly3DType, actualSchema);
        assertEquals(new Integer(4359), actualSchema.getGeometryDescriptor().getUserData().get(
                JDBCDataStore.JDBC_NATIVE_SRID));

        // build a 3d polygon (ordinates in ccw order)
        GeometryFactory gf = new GeometryFactory();
        LinearRing shell = gf.createLinearRing(new Coordinate[] { new Coordinate(0, 0, 0),
                new Coordinate(1, 1, 1), new Coordinate(1, 0, 1), new Coordinate(0, 0, 0) });
        Polygon poly = gf.createPolygon(shell, null);

        // insert it
        FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
                nsname(POLY3D));
        SimpleFeature f = fw.next();
        f.setAttribute(aname(ID), 0);
        f.setAttribute(aname(GEOM), poly);
        f.setAttribute(aname(NAME), "3dpolygon!");
        fw.write();
        fw.close();

        // read id back and compare
        FeatureReader<SimpleFeatureType, SimpleFeature> fr = dataStore.getFeatureReader(
                QueryBuilder.all(dataStore.getFeatureType(tname(POLY3D)).getName()));
        assertTrue(fr.hasNext());
        f = fr.next();
        assertTrue(poly.equals((Geometry) f.getDefaultGeometry()));
        fr.close();
    }

    /**
     * Make sure we can properly retrieve the bounds of 3d layers
     * 
     * @throws Exception
     */
    public void testBounds() throws Exception {
        JTSEnvelope2D env = (JTSEnvelope2D) dataStore.getEnvelope(QueryBuilder.all(nsname(LINE3D)));

        // check we got the right 2d component
        Envelope expected = new Envelope(1, 5, 0, 4);
        assertEquals(expected, env);

        // check the srs is the 2d part of the native one
        assertEquals(horizontal, env.getCoordinateReferenceSystem());
    }

}
