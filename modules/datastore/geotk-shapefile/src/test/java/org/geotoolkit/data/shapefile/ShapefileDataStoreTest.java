/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
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
package org.geotoolkit.data.shapefile;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.test.TestData;

import static org.junit.Assert.*;

/**
 * 
 * @version $Id$
 * @author Ian Schneider
 * @module pending
 */
public class ShapefileDataStoreTest extends AbstractTestCaseSupport {

    static final String STATE_POP = "shapes/statepop.shp";
    static final String STREAM = "shapes/stream.shp";
    static final String DANISH = "shapes/danish_point.shp";
    static final String CHINESE = "shapes/chinese_poly.shp";
    static final FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    protected FeatureCollection<SimpleFeature> loadFeatures(final String resource, Query query)
            throws Exception {
        assertNotNull(query);

        URL url = ShapeTestData.url(resource);
        ShapefileDataStore s = new ShapefileDataStore(url,null,true,null);

        final QueryBuilder builder = new QueryBuilder(query);
        builder.setTypeName(s.getName());
        query = builder.buildQuery();

        return s.createSession(true).getFeatureCollection(query);
    }

    protected FeatureCollection<SimpleFeature> loadLocalFeaturesM2() throws IOException, DataStoreException {
        String target = "jar:file:/C:/Documents and Settings/jgarnett/.m2/repository/org/geotoolkit/gt2-sample-data/2.4-SNAPSHOT/gt2-sample-data-2.4-SNAPSHOT.jar!/org/geotoolkit/test-data/shapes/statepop.shp";
        URL url = new URL(target);
        ShapefileDataStore s = new ShapefileDataStore(url);
        return s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
    }

    protected FeatureCollection<SimpleFeature> loadFeatures(final String resource, final Charset charset, final Query q) throws Exception {

        URL url = ShapeTestData.url(resource);
        ShapefileDataStore s = new ShapefileDataStore(url, null, false, charset);

        if(q == null){
            return s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
        }else{
            return s.createSession(true).getFeatureCollection(q);
        }

    }

    protected FeatureCollection<SimpleFeature> loadFeatures(final ShapefileDataStore s)
            throws Exception {
        return s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
    }

    @Test
    public void testLoad() throws Exception {
        loadFeatures(STATE_POP, QueryBuilder.all(new DefaultName("statepop")));
    }

    @Test
    public void testLoadDanishChars() throws Exception {
        FeatureCollection<SimpleFeature> fc = loadFeatures(DANISH, QueryBuilder.all(new DefaultName("danish_point")));
        SimpleFeature first = firstFeature(fc);

        // Charlotte (but with the o is stroked)
        assertEquals("Charl\u00F8tte", first.getAttribute("TEKST1"));
    }

    @Test
    public void testLoadChineseChars() throws Exception {
        try {
            FeatureCollection<SimpleFeature> fc = loadFeatures(CHINESE, Charset
                    .forName("GB18030"), null);
            SimpleFeature first = firstFeature(fc);
            String s = (String) first.getAttribute("NAME");
            assertEquals("\u9ed1\u9f99\u6c5f\u7701", s);
        } catch (UnsupportedCharsetException notInstalledInJRE){
                // this just means you have not installed
                // chinese support into your JRE
                // (as such it represents a bad configuration
                //  rather than a test failure)
                // we only wanted to ensure that if you have Chinese support
                // available - GeotoolKit can use it
            }
        }

    @Test
    public void testNamespace() throws Exception {
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        Map map = new HashMap();

        String namespace = "http://jesse.com";

        map.put(ShapefileDataStoreFactory.NAMESPACE.getName().toString(), namespace);
        map.put(ShapefileDataStoreFactory.URLP.getName().toString(), ShapeTestData.url(STATE_POP));

        DataStore store = factory.createDataStore(map);
        FeatureType schema = store.getFeatureType(store.getTypeNames()[0]);
        assertEquals(namespace.toString(), schema.getName().getNamespaceURI());
    }

    @Test
    public void testSchema() throws Exception {
        URL url = ShapeTestData.url(STATE_POP);
        ShapefileDataStore shapeDataStore = new ShapefileDataStore(url);
        String typeName = shapeDataStore.getTypeNames()[0];
        SimpleFeatureType schema = (SimpleFeatureType) shapeDataStore.getFeatureType(typeName);
        List<AttributeDescriptor> attributes = schema.getAttributeDescriptors();
        assertEquals("Number of Attributes", 253, attributes.size());
    }

    @Test
    public void testSpacesInPath() throws Exception {
        URL u = TestData.url(AbstractTestCaseSupport.class, "folder with spaces/pointtest.shp");
        File f = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
        assertTrue(f.exists());
        ShapefileDataStore s = new ShapefileDataStore(u);
        loadFeatures(s);
    }

    /**
     * Test envelope versus old DataSource
     */
    @Test
    public void testEnvelope() throws Exception {
        FeatureCollection<SimpleFeature> features = loadFeatures(STATE_POP, QueryBuilder.all(new DefaultName("statepop")));
        ShapefileDataStore s = new ShapefileDataStore(ShapeTestData.url(STATE_POP));
        String typeName = s.getTypeNames()[0];
        FeatureCollection<SimpleFeature> all = s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));

        assertEquals(features.getEnvelope(), all.getEnvelope());
    }

    @Test
    public void testLoadAndVerify() throws Exception {
        FeatureCollection<SimpleFeature> features = loadFeatures(STATE_POP, QueryBuilder.all(new DefaultName("statepop")));
        // FeatureCollection<SimpleFeatureType, SimpleFeature> features = loadFeaturesM2();
        int count = features.size();

        assertTrue("Have features", count > 0);
        // assertEquals("Number of Features loaded",49,features.size()); // FILE
        // (correct value)
        // assertEquals("Number of Features loaded",3, count); // JAR

        SimpleFeature firstFeature = firstFeature(features);
        SimpleFeatureType schema = firstFeature.getFeatureType();
        assertNotNull(schema.getGeometryDescriptor());
        assertEquals("Number of Attributes", 253, schema.getAttributeCount());
        assertEquals("Value of statename is wrong", "Illinois", firstFeature
                .getAttribute("STATE_NAME"));
        assertEquals("Value of land area is wrong", 143986.61,
                ((Double) firstFeature.getAttribute("LAND_KM")).doubleValue(),
                0.001);
    }

    @Test
    public void testLoadAndCheckParentTypeIsPolygon() throws Exception {
        FeatureCollection<SimpleFeature> features = loadFeatures(STATE_POP, QueryBuilder.all(new DefaultName("statepop")));
        SimpleFeatureType schema = firstFeature(features).getFeatureType();

        assertTrue(FeatureTypeUtilities.isDecendedFrom(schema, BasicFeatureTypes.POLYGON));
        assertTrue(FeatureTypeUtilities.isDecendedFrom(schema, FeatureTypeUtilities.DEFAULT_NAMESPACE, "polygonFeature"));
    }

    @Test
    public void testCreateSchemaWithEmptyCRS() throws Exception {
        File file = new File("test.shp");
        URL toURL = file.toURI().toURL();
        ShapefileDataStore ds = new ShapefileDataStore(toURL);
        SimpleFeatureType toCreate = FeatureTypeUtilities.createType("test", "geom:MultiPolygon");
        ds.createSchema(toCreate.getName(),toCreate);

        assertEquals("test", ds.getTypeNames()[0]);

        file.deleteOnExit();
        file = new File("test.dbf");
        file.deleteOnExit();
        file = new File("test.shp");
        file.deleteOnExit();

        file = new File("test.prj");
        if (file.exists())
            file.deleteOnExit();

        file = new File("test.shx");
        if (file.exists()){
            file.deleteOnExit();
        }
    }

    @Test
    public void testCreateSchemaWithCRS() throws Exception {
        File file = new File("test.shp");
        URL toURL = file.toURI().toURL();
        ShapefileDataStore ds = new ShapefileDataStore(toURL);
        SimpleFeatureType featureType = FeatureTypeUtilities.createType("test", "geom:MultiPolygon:srid=32615");
        CoordinateReferenceSystem crs = featureType.getGeometryDescriptor().getCoordinateReferenceSystem();
        assertNotNull( crs );

        ds.createSchema(featureType.getName(),featureType);

        assertEquals("test", ds.getFeatureType(ds.getTypeNames()[0]).getName().getLocalPart());

        CoordinateReferenceSystem crs2 = ds.getFeatureType("test").getGeometryDescriptor().getCoordinateReferenceSystem();
        assertNotNull( crs2 );
        assertEquals( crs.getName(), crs2.getName() );

        file.deleteOnExit();
        file = new File("test.dbf");
        file.deleteOnExit();
        file = new File("test.shp");
        file.deleteOnExit();

        file = new File("test.prj");
        if (file.exists())
            file.deleteOnExit();

        file = new File("test.shx");
        if (file.exists()){
            file.deleteOnExit();
        }

        file = new File("test.prj");
        if( file.exists()){
            file.deleteOnExit();
        }
    }

    /**
     * Create a set of features, then remove every other one, updating the
     * remaining. Test for removal and proper update after reloading...
     */
    @Test
    public void testUpdating() throws Throwable {
        ShapefileDataStore sds = createDataStore();
        loadFeatures(sds);

        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
        try {
            writer = sds.getFeatureWriter(sds.getNames().iterator().next(), Filter.INCLUDE);
            while (writer.hasNext()) {
                SimpleFeature feat = writer.next();
                Byte b = (Byte) feat.getAttribute(1);
                if (b.byteValue() % 2 == 0) {
                    writer.remove();
                } else {
                    feat.setAttribute(1, new Byte((byte) -1));
                }
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        FeatureCollection<SimpleFeature> fc = loadFeatures(sds);

        assertEquals(10, fc.size());
        FeatureIterator<SimpleFeature> i = fc.iterator();
        for (; i.hasNext();) {
            assertEquals(-1, ((Byte) i.next().getAttribute(1)).byteValue());
        }
        i.close();
    }

    /**
     * Create a test file, then continue removing the first entry until there
     * are no features left.
     */
    @Test
    public void testRemoveFromFrontAndClose() throws Throwable {
        ShapefileDataStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;

            try {
                writer = sds.getFeatureWriter(sds.getNames().iterator().next(), Filter.INCLUDE);
                writer.next();
                writer.remove();
            } finally {
                if (writer != null) {
                    writer.close();
                    writer = null;
                }
            }
            assertEquals(--idx, loadFeatures(sds).size());
        }
    }

    /**
     * Create a test file, then continue removing the first entry until there
     * are no features left.
     */
    @Test
    public void testRemoveFromFrontAndCloseTransaction() throws Throwable {
        ShapefileDataStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;

            try {
                writer = sds.getFeatureWriter(sds.getName(),
                        Filter.INCLUDE);
//                System.out.println("classe : " + writer.getClass());
                writer.next();
                writer.remove();
            } finally {
                if (writer != null) {
                    writer.close();
                    writer = null;
                }
            }
            assertEquals(--idx, loadFeatures(sds).size());
        }
    }

    /**
     * Create a test file, then continue removing the last entry until there are
     * no features left.
     */
    @Test
    public void testRemoveFromBackAndClose() throws Throwable {
            ShapefileDataStore sds = createDataStore();

            int idx = loadFeatures(sds).size();

            while (idx > 0) {
                FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
                try {
                    writer = sds.getFeatureWriter(sds.getName(),
                            Filter.INCLUDE);
                    while (writer.hasNext()) {
                        writer.next();
                    }
                    writer.remove();
                } finally {
                    if (writer != null) {
                        writer.close();
                        writer = null;
                    }
                }
                assertEquals(--idx, loadFeatures(sds).size());
            }
    }

    @Test
    public void testWriteShapefileWithNoRecords() throws Exception {
        SimpleFeatureType featureType = FeatureTypeUtilities.createType("whatever",
                "a:Polygon,b:String");

        File tempFile = getTempFile();
        ShapefileDataStore shapefileDataStore = new ShapefileDataStore(tempFile.toURI().toURL());
        shapefileDataStore.createSchema(featureType.getName(),featureType);

        FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriter = shapefileDataStore.getFeatureWriter(
                shapefileDataStore.getName(),Filter.INCLUDE);

        // don't add any features to the data store....

        // this should create a shapefile with no records. Not sure about the
        // semantics of this,
        // but it's meant to be used in the context of a FeatureCollection
        // iteration,
        // where the FeatureCollection<SimpleFeatureType, SimpleFeature> has nothing in it.
        featureWriter.close();
    }

    @Test
    public void testAttributesWriting() throws Exception {
        Collection<SimpleFeature> features = createFeatureCollection();
        File tmpFile = getTempFile();
        tmpFile.createNewFile();
        ShapefileDataStore s = new ShapefileDataStore(tmpFile.toURI().toURL());
        writeFeatures(s, features);
    }

    @Test
    public void testWriteReadBigNumbers() throws Exception {
        // create feature type
        SimpleFeatureType type = FeatureTypeUtilities.createType("junk",
                "a:Point,b:java.math.BigDecimal,c:java.math.BigInteger");
        Collection<SimpleFeature> features = new ArrayList<SimpleFeature>();

        BigInteger bigInteger = new BigInteger("1234567890123456789");
        BigDecimal bigDecimal = new BigDecimal(bigInteger, 2);

        SimpleFeatureBuilder build = new SimpleFeatureBuilder(type);
        build.add(new GeometryFactory().createPoint(new Coordinate(1, -1)));
        build.add(bigDecimal);
        build.add(bigInteger);

        SimpleFeature feature = build.buildFeature(null);
        features.add(feature);

        // store features
        File tmpFile = getTempFile();
        tmpFile.createNewFile();
        ShapefileDataStore s = new ShapefileDataStore(tmpFile.toURI().toURL());
        writeFeatures(s, features);

        // read them back
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = s.getFeatureReader(QueryBuilder.all(type.getName()));
        try {
            SimpleFeature f = reader.next();

            assertEquals("big decimal", bigDecimal.doubleValue(), ((Number) f
                    .getAttribute("b")).doubleValue(), 0.00001);
            assertEquals("big integer", bigInteger.longValue(), ((Number) f
                    .getAttribute("c")).longValue(), 0.00001);
        } finally {
            reader.close();
        }
    }

    @Test
    public void testGeometriesWriting() throws Exception {

        String[] wktResources = new String[] { "point", "multipoint", "line",
                "multiline", "polygon", "multipolygon" };

        for (int i = 0; i < wktResources.length; i++) {
            Geometry geom = readGeometry(wktResources[i]);
            String testName = wktResources[i];
            try {

                runWriteReadTest(geom, false);
                make3D(geom);
                testName += "3d";
                runWriteReadTest(geom, true);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new Exception("Error in " + testName, e);
            }

        }

    }

    @Test
    public void testGetCount() throws Exception {
        assertTrue(copyShapefiles(STREAM).canRead()); // The following test
                                                        // seems to fail in the
                                                        // URL point into the
                                                        // JAR file.
        ShapefileDataStore store = (ShapefileDataStore) new ShapefileDataStoreFactory()
                .createDataStore(TestData.url(AbstractTestCaseSupport.class, STREAM));
        int count = 0;
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = store.getFeatureReader(QueryBuilder.all(store.getNames().iterator().next()));
        try {
            while (reader.hasNext()) {
                count++;
                reader.next();
            }
            assertEquals(count, store.getCount(QueryBuilder.all(store.getNames().iterator().next())));
        } finally {
            reader.close();
        }
    }

    /**
     * Checks if feature reading optimizations still allow to execute the
     * queries or not
     *
     * @throws Exception
     */
    @Test
    public void testGetReaderOptimizations() throws Exception {
        URL url = ShapeTestData.url(STATE_POP);
        ShapefileDataStore s = new ShapefileDataStore(url);

        // attributes other than geometry can be ignored here
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(s.getNames().iterator().next());
        builder.setFilter(Filter.INCLUDE);
        builder.setProperties(new String[]{"the_geom"});
        Query query = builder.buildQuery();

         FeatureReader<SimpleFeatureType, SimpleFeature> reader = s.getFeatureReader(query);
        assertEquals(1, reader.getFeatureType().getAttributeCount());
        assertEquals("the_geom", reader.getFeatureType().getDescriptor(0)
                .getLocalName());

        // here too, the filter is using the geometry only
        GeometryFactory gc = new GeometryFactory();
        LinearRing ring = gc.createLinearRing(new Coordinate[] {
                new Coordinate(0, 0), new Coordinate(10, 0),
                new Coordinate(10, 10), new Coordinate(0, 10),
                new Coordinate(0, 0) });
        Polygon polygon = gc.createPolygon(ring, null);

        JTSEnvelope2D bounds = new JTSEnvelope2D(polygon
                .getEnvelopeInternal(), null);
        Filter gf = ff.bbox(ff.property("the_geom"), bounds);

        builder.reset();
        builder.setTypeName(s.getNames().iterator().next());
        builder.setFilter(gf);
        builder.setProperties(new String[]{"the_geom"});
        query = builder.buildQuery();

        reader.close();
        reader = s.getFeatureReader(query);
        assertEquals(1, reader.getFeatureType().getAttributeCount());
        assertEquals("the_geom", reader.getFeatureType().getDescriptor(0)
                .getLocalName());

        reader.close();

        // here not, we need state_name in the feature type, so open the dbf
        // file please
        Filter cf = ff
                .equals(ff.property("STATE_NAME"), ff.literal("Illinois"));

        builder.reset();
        builder.setTypeName(s.getNames().iterator().next());
        builder.setFilter(cf);
        builder.setProperties(new String[]{"the_geom"});
        query = builder.buildQuery();

        reader = s.getFeatureReader(query);
        assertEquals(1, reader.getFeatureType().getAttributeCount());
        assertEquals("the_geom", reader.getFeatureType().getDescriptor(0)
                .getLocalName());
        reader.close();
    }

    @Test
    public void testWrite() throws Exception {
        // create feature type
        SimpleFeatureType type = FeatureTypeUtilities.createType("junk","a:Point,b:java.math.BigDecimal,c:java.math.BigInteger");

        BigInteger bigInteger = new BigInteger("1234567890123456789");
        BigDecimal bigDecimal = new BigDecimal(bigInteger, 2);

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
        builder.add(new GeometryFactory().createPoint(new Coordinate(1, -1)));
        builder.add(bigDecimal);
        builder.add(bigInteger);


        SimpleFeature feature = builder.buildFeature(null);;

        // store features
        File tmpFile = getTempFile();
        tmpFile.createNewFile();
        ShapefileDataStore s = new ShapefileDataStore(tmpFile.toURI().toURL());
        s.createSchema(type.getName(),type);

//        // was failing in GEOT-2427
//        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = s.getFeatureWriter(s.getNames().iterator().next(),Filter.INCLUDE);
//        SimpleFeature feature1 = writer.next();
//        writer.close();

    }

    /**
     * Creates feature collection with all the stuff we care about from simple
     * types, to Geometry and date.
     * <p>
     * As we care about supporting more stuff please add on to the end of this
     * list...
     *
     * @return FeatureCollection<SimpleFeature> For use in testing.
     * @throws Exception
     */
    private Collection<SimpleFeature> createFeatureCollection() throws Exception {
        SimpleFeatureType featureType = createExampleSchema();
        SimpleFeatureBuilder build = new SimpleFeatureBuilder(featureType);

        Collection<SimpleFeature> features = new ArrayList<SimpleFeature>();
        for (int i = 0, ii = 20; i < ii; i++) {

            build.add(new GeometryFactory().createPoint(new Coordinate(1, -1)));
            build.add(new Byte((byte) i));
            build.add(new Short((short) i));
            build.add(new Double(i));
            build.add(new Float(i));
            build.add(new String(i + " "));
            build.add(new Date(i));
            build.add(new Boolean(true));
            build.add(new Integer(22));
            build.add(new Long(1234567890123456789L));
            build.add(new BigDecimal(new BigInteger(
                    "12345678901234567890123456789"), 2));
            build.add(new BigInteger("12345678901234567890123456789"));

            SimpleFeature feature = build.buildFeature(null);
            features.add(feature);
        }
        return features;
    }

    private SimpleFeatureType createExampleSchema() {
        FeatureTypeBuilder build = new FeatureTypeBuilder();
        build.setName("junk");
        build.add("a", Point.class, DefaultGeographicCRS.WGS84);
        build.add("b", Byte.class);
        build.add("c", Short.class);
        build.add("d", Double.class);
        build.add("e", Float.class);
        build.add("f", String.class);
        build.add("g", Date.class);
        build.add("h", Boolean.class);
        build.add("i", Number.class);
        build.add("j", Long.class);
        build.add("k", BigDecimal.class);
        build.add("l", BigInteger.class);

        return build.buildSimpleFeatureType();
    }

    private void make3D(final Geometry g) {
        Coordinate[] c = g.getCoordinates();
        for (int i = 0, ii = c.length; i < ii; i++) {
            c[i].z = 42 + i;
        }
    }

    private void writeFeatures(final ShapefileDataStore s, final Collection<SimpleFeature> fc)
            throws Exception {

        final SimpleFeatureType sft = fc.iterator().next().getFeatureType();

        s.createSchema(sft.getName(), sft);
        FeatureWriter<SimpleFeatureType, SimpleFeature> fw = s.getFeatureWriter(sft.getName(),Filter.INCLUDE);
        Iterator<SimpleFeature> it = fc.iterator();
        while (it.hasNext()) {
            SimpleFeature feature = it.next();
            SimpleFeature newFeature = fw.next();
            newFeature.setAttributes(feature.getAttributes());

            fw.write();
        }
        fw.close();
    }

    private void runWriteReadTest(final Geometry geom, final boolean d3) throws Exception {
        // make features

        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Junk");
        ftb.add("a", geom.getClass(), DefaultGeographicCRS.WGS84);
        SimpleFeatureType type = ftb.buildSimpleFeatureType();

        Collection<SimpleFeature> features = new ArrayList<SimpleFeature>();
        SimpleFeatureBuilder build = new SimpleFeatureBuilder(type);
        for (int i = 0, ii = 20; i < ii; i++) {
            build.set(0, (Geometry) geom.clone());
            SimpleFeature feature = build.buildFeature(null);

            features.add(feature);
        }

        // set up file
        File tmpFile = getTempFile();
        tmpFile.delete();

        // write features
        ShapefileDataStore shapeDataStore = new ShapefileDataStore(tmpFile.toURI().toURL());
        shapeDataStore.createSchema(type.getName(),type);
        writeFeatures(shapeDataStore, features);

        // read features
        shapeDataStore = new ShapefileDataStore(tmpFile.toURI().toURL());
        FeatureCollection<SimpleFeature> fc = loadFeatures(shapeDataStore);
        FeatureIterator<SimpleFeature> fci = fc.iterator();
        // verify
        while (fci.hasNext()) {
            SimpleFeature f = fci.next();
            Geometry fromShape = (Geometry) f.getDefaultGeometry();

            if (fromShape instanceof GeometryCollection) {
                if (!(geom instanceof GeometryCollection)) {
                    fromShape = ((GeometryCollection) fromShape)
                            .getGeometryN(0);
                }
            }
            try {
                Coordinate[] c1 = geom.getCoordinates();
                Coordinate[] c2 = fromShape.getCoordinates();
                for (int cc = 0, ccc = c1.length; cc < ccc; cc++) {
                    if (d3)
                        assertTrue(c1[cc].equals3D(c2[cc]));
                    else
                        assertTrue(c1[cc].equals2D(c2[cc]));
                }
            } catch (Throwable t) {
                fail("Bogus : " + Arrays.asList(geom.getCoordinates()) + " : "
                        + Arrays.asList(fromShape.getCoordinates()));
            }
        }
        fci.close();
        tmpFile.delete();
    }

    private ShapefileDataStore createDataStore(final File f) throws Exception {
        Collection<SimpleFeature> fc = createFeatureCollection();
        ShapefileDataStore sds = new ShapefileDataStore(f.toURI().toURL());
        writeFeatures(sds, fc);
        return sds;
    }

    private ShapefileDataStore createDataStore() throws Exception {
        return createDataStore(getTempFile());
    }

    /**
     * This is useful to dump a UTF16 character to an UT16 escape sequence,
     * basically the only way to represent the chars we don't have on the
     * keyboard (such as chinese ones :))
     *
     * @param c
     * @return
     */
    static public String charToHex(final char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    static public String byteToHex(final byte b) {
        // Returns hex String representation of byte b
        char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
        return new String(array);
    }
}
