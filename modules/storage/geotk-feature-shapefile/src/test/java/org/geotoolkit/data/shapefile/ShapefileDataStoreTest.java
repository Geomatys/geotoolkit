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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.test.TestData;

import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;

/**
 *
 * @version $Id$
 * @author Ian Schneider
 * @module
 */
public class ShapefileDataStoreTest extends AbstractTestCaseSupport {

    static final String STATE_POP = "shapes/statepop.shp";
    static final String STREAM = "shapes/stream.shp";
    static final String DANISH = "shapes/danish_point.shp";
    static final String CHINESE = "shapes/chinese_poly.shp";
    static final FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    protected FeatureCollection loadFeatures(final String resource, Query query)
            throws Exception {
        assertNotNull(query);

        URL url = ShapeTestData.url(resource);
        ShapefileFeatureStore s = new ShapefileFeatureStore(url.toURI(),null,true,null);

        final QueryBuilder builder = new QueryBuilder(query);
        builder.setTypeName(s.getName());
        query = builder.buildQuery();

        return s.createSession(true).getFeatureCollection(query);
    }

    protected FeatureCollection loadFeatures(final String resource, final Charset charset, final Query q) throws Exception {

        URL url = ShapeTestData.url(resource);
        ShapefileFeatureStore s = new ShapefileFeatureStore(url.toURI(), null, false, charset);

        if(q == null){
            return s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
        }else{
            return s.createSession(true).getFeatureCollection(q);
        }

    }

    protected FeatureCollection loadFeatures(final ShapefileFeatureStore s)
            throws Exception {
        return s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
    }

    @Test
    public void testLoad() throws Exception {
        loadFeatures(STATE_POP, QueryBuilder.all(NamesExt.create("statepop")));
    }

    @Test
    public void testLoadDanishChars() throws Exception {
        FeatureCollection fc = loadFeatures(DANISH, QueryBuilder.all(NamesExt.create("danish_point")));
        Feature first = firstFeature(fc);

        // Charlotte (but with the o is stroked)
        assertEquals("Charl\u00F8tte", first.getPropertyValue("TEKST1"));
    }

    @Test
    public void testLoadChineseChars() throws Exception {
        try {
            FeatureCollection fc = loadFeatures(CHINESE, Charset
                    .forName("GB18030"), null);
            Feature first = firstFeature(fc);
            String s = (String) first.getPropertyValue("NAME");
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
        ShapefileFeatureStoreFactory factory = new ShapefileFeatureStoreFactory();
        Map map = new HashMap();

        String namespace = "http://jesse.com";

        map.put(ShapefileFeatureStoreFactory.NAMESPACE.getName().toString(), namespace);
        map.put(ShapefileFeatureStoreFactory.PATH.getName().toString(), ShapeTestData.url(STATE_POP));

        FeatureStore store = (FeatureStore) factory.open(map);
        FeatureType schema = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(namespace, NamesExt.getNamespace(schema.getName()));
    }

    @Test
    public void testSchema() throws Exception {
        URL url = ShapeTestData.url(STATE_POP);
        ShapefileFeatureStore shapeFeatureStore = new ShapefileFeatureStore(url.toURI());
        String typeName = shapeFeatureStore.getNames().iterator().next().toString();
        FeatureType schema = shapeFeatureStore.getFeatureType(typeName);
        Collection<? extends PropertyType> attributes = schema.getProperties(true);
        assertEquals("Number of Attributes", 256, attributes.size());
    }

    @Test
    public void testSpacesInPath() throws Exception {
        URL u = TestData.url(AbstractTestCaseSupport.class, "folder with spaces/pointtest.shp");
        File f = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
        assertTrue(f.exists());
        ShapefileFeatureStore s = new ShapefileFeatureStore(u.toURI());
        loadFeatures(s);
    }

    /**
     * Test envelope versus old DataSource
     */
    @Test
    public void testEnvelope() throws Exception {
        FeatureCollection features = loadFeatures(STATE_POP, QueryBuilder.all(NamesExt.create("statepop")));
        ShapefileFeatureStore s = new ShapefileFeatureStore(ShapeTestData.url(STATE_POP).toURI());
        String typeName = s.getName().toString();
        FeatureCollection all = s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));

        assertEquals(features.getEnvelope(), all.getEnvelope());
    }

    @Test
    public void testLoadAndVerify() throws Exception {
        FeatureCollection features = loadFeatures(STATE_POP, QueryBuilder.all(NamesExt.create("statepop")));
        // FeatureCollection<SimpleFeatureType, SimpleFeature> features = loadFeaturesM2();
        int count = features.size();

        assertTrue("Have features", count > 0);
        // assertEquals("Number of Features loaded",49,features.size()); // FILE
        // (correct value)
        // assertEquals("Number of Features loaded",3, count); // JAR

        Feature firstFeature = firstFeature(features);
        FeatureType schema = firstFeature.getType();
        assertNotNull(FeatureExt.getDefaultGeometryAttribute(schema));
        assertEquals("Number of Attributes", 256, schema.getProperties(true).size());
        assertEquals("Value of statename is wrong", "Illinois", firstFeature
                .getPropertyValue("STATE_NAME"));
        assertEquals("Value of land area is wrong", 143986.61,
                ((Double) firstFeature.getPropertyValue("LAND_KM")).doubleValue(),
                0.001);
    }

    @Test
    public void testCreateSchemaWithEmptyCRS() throws Exception {
        File file = new File("test.shp");
        final URI toURI = file.toURI();
        final ShapefileFeatureStore ds = new ShapefileFeatureStore(toURI);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(MultiPolygon.class).setName("geom").addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType toCreate = ftb.build();
        ds.createFeatureType(toCreate);

        assertEquals("test", ds.getName().toString());

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

        file = new File("test.cpg");
        if (file.exists()){
            file.deleteOnExit();
        }
    }

    @Test
    public void testCreateSchemaWithCRS() throws Exception {
        File file = new File("test.shp");
        URI toURI = file.toURI();
        ShapefileFeatureStore ds = new ShapefileFeatureStore(toURI);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(MultiPolygon.class).setName("geom").setCRS(CRS.forCode("EPSG:32615")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        FeatureType featureType = ftb.build();
        CoordinateReferenceSystem crs = FeatureExt.getCRS(featureType);
        assertNotNull( crs );

        ds.createFeatureType(featureType);

        assertEquals("test", ds.getName().tip().toString());

        CoordinateReferenceSystem crs2 = FeatureExt.getCRS(ds.getFeatureType("test"));
        assertNotNull( crs2 );
        assertTrue(Utilities.equalsIgnoreMetadata(crs, crs2));

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
        file = new File("test.cpg");
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
        ShapefileFeatureStore sds = createDataStore();
        loadFeatures(sds);

        FeatureWriter writer = null;
        try {
            writer = sds.getFeatureWriter(QueryBuilder.all(sds.getNames().iterator().next().toString()));
            while (writer.hasNext()) {
                Feature feat = writer.next();
                Byte b = (Byte) feat.getPropertyValue("b");
                if (b.byteValue() % 2 == 0) {
                    writer.remove();
                } else {
                    feat.setPropertyValue("b", new Byte((byte) -1));
                }
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        FeatureCollection fc = loadFeatures(sds);

        assertEquals(10, fc.size());
        FeatureIterator i = fc.iterator();
        for (; i.hasNext();) {
            assertEquals(-1, ((Byte) (i.next()).getPropertyValue("b")).byteValue());
        }
        i.close();
    }

    /**
     * Create a test file, then continue removing the first entry until there
     * are no features left.
     */
    @Test
    public void testRemoveFromFrontAndClose() throws Throwable {
        ShapefileFeatureStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter writer = null;

            try {
                writer = sds.getFeatureWriter(QueryBuilder.all(sds.getNames().iterator().next().toString()));
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
        ShapefileFeatureStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter writer = null;

            try {
                writer = sds.getFeatureWriter(QueryBuilder.all(sds.getName().toString()));
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
            ShapefileFeatureStore sds = createDataStore();

            int idx = loadFeatures(sds).size();

            while (idx > 0) {
                FeatureWriter writer = null;
                try {
                    writer = sds.getFeatureWriter(QueryBuilder.all(sds.getName().toString()));
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
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder().setName("whatever");
        ftb.addAttribute(Polygon.class).setName("a").addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("b");
        FeatureType featureType = ftb.build();

        File tempFile = getTempFile();
        ShapefileFeatureStore shapefileFeatureStore = new ShapefileFeatureStore(tempFile.toURI());
        shapefileFeatureStore.createFeatureType(featureType);

        FeatureWriter featureWriter = shapefileFeatureStore.getFeatureWriter(QueryBuilder.all(shapefileFeatureStore.getName().toString()));

        // don't add any features to the data store....

        // this should open a shapefile with no records. Not sure about the
        // semantics of this,
        // but it's meant to be used in the context of a FeatureCollection
        // iteration,
        // where the FeatureCollection<SimpleFeatureType, SimpleFeature> has nothing in it.
        featureWriter.close();
    }

    @Test
    public void testAttributesWriting() throws Exception {
        Collection<Feature> features = createFeatureCollection();
        File tmpFile = getTempFile();
        tmpFile.createNewFile();
        ShapefileFeatureStore s = new ShapefileFeatureStore(tmpFile.toURI());
        writeFeatures(s, features);
    }

    @Test
    public void testWriteReadBigNumbers() throws Exception {
        // open feature type
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder().setName("junk");
        ftb.addAttribute(Point.class).setName("a").addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(BigDecimal.class).setName("b");
        ftb.addAttribute(BigInteger.class).setName("c");
        FeatureType type = ftb.build();


        Collection<Feature> features = new ArrayList<>();

        BigInteger bigInteger = new BigInteger("1234567890123456789");
        BigDecimal bigDecimal = new BigDecimal(bigInteger, 2);

        final Feature feature = type.newInstance();
        feature.setPropertyValue("a", new GeometryFactory().createPoint(new Coordinate(1, -1)));
        feature.setPropertyValue("b", bigDecimal);
        feature.setPropertyValue("c", bigInteger);

        features.add(feature);

        // store features
        File tmpFile = getTempFile();
        tmpFile.createNewFile();
        ShapefileFeatureStore s = new ShapefileFeatureStore(tmpFile.toURI());
        writeFeatures(s, features);

        // read them back
        FeatureReader reader = s.getFeatureReader(QueryBuilder.all(type.getName()));
        try {
            Feature f = reader.next();

            assertEquals("big decimal", bigDecimal.doubleValue(), ((Number) f
                    .getPropertyValue("b")).doubleValue(), 0.00001);
            assertEquals("big integer", bigInteger.longValue(), ((Number) f
                    .getPropertyValue("c")).longValue(), 0.00001);
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
        ShapefileFeatureStore store = (ShapefileFeatureStore) new ShapefileFeatureStoreFactory()
                .createDataStore(TestData.url(AbstractTestCaseSupport.class, STREAM).toURI());
        int count = 0;
        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(store.getNames().iterator().next()));
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
        ShapefileFeatureStore s = new ShapefileFeatureStore(url.toURI());

        // attributes other than geometry can be ignored here
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(s.getNames().iterator().next());
        builder.setFilter(Filter.INCLUDE);
        builder.setProperties(new String[]{"the_geom"});
        Query query = builder.buildQuery();

         FeatureReader reader = s.getFeatureReader(query);
        assertEquals(1, reader.getFeatureType().getProperties(true).size());
        assertEquals("the_geom", reader.getFeatureType().getProperties(true).iterator().next().getName().tip().toString());

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
        assertEquals(1, reader.getFeatureType().getProperties(true).size());
        assertEquals("the_geom", reader.getFeatureType().getProperties(true).iterator().next().getName().tip().toString());

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
        assertEquals(1, reader.getFeatureType().getProperties(true).size());
        assertEquals("the_geom", reader.getFeatureType().getProperties(true).iterator().next().getName().tip().toString());
        reader.close();
    }

    @Test
    public void testWrite() throws Exception {
        // open feature type
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("junk");
        ftb.addAttribute(Point.class).setName("a").addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(BigDecimal.class).setName("b");
        ftb.addAttribute(BigInteger.class).setName("c");
        FeatureType type = ftb.build();

        BigInteger bigInteger = new BigInteger("1234567890123456789");
        BigDecimal bigDecimal = new BigDecimal(bigInteger, 2);

        Feature feature = type.newInstance();
        feature.setPropertyValue("a", new GeometryFactory().createPoint(new Coordinate(1, -1)));
        feature.setPropertyValue("b",bigDecimal);
        feature.setPropertyValue("c",bigInteger);



        // store features
        File tmpFile = getTempFile();
        tmpFile.createNewFile();
        ShapefileFeatureStore s = new ShapefileFeatureStore(tmpFile.toURI());
        s.createFeatureType(type);

//        // was failing in GEOT-2427
//        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = s.getFeatureWriter(s.getNames().iterator().next(),Filter.INCLUDE);
//        SimpleFeature feature1 = writer.next();
//        writer.close();

    }

    @Test
    public void testReadQPJ() throws Exception {
        final URL shpUrl = this.getClass().getResource("/org/geotoolkit/test-data/shapes/utf8.shp");
        final FeatureType ft = new ShapefileFeatureStore(shpUrl.toURI()).getFeatureType();
        assertNotNull("No feature type loaded !", ft);
        final CoordinateReferenceSystem crs = FeatureExt.getCRS(ft);
        assertNotNull("No CRS loaded !", crs);
        final URL qpjUrl = this.getClass().getResource("/org/geotoolkit/test-data/shapes/utf8.qpj");
        assertEquals("CRS loaded by shapefile store is not the one contained in qpj !", crs, PrjFiles.read(qpjUrl));
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
    private Collection<Feature> createFeatureCollection() throws Exception {
        FeatureType featureType = createExampleSchema();

        Collection<Feature> features = new ArrayList<>();
        for (int i = 0, ii = 20; i < ii; i++) {
            final Feature feature = featureType.newInstance();
            feature.setPropertyValue("a",new GeometryFactory().createPoint(new Coordinate(1, -1)));
            feature.setPropertyValue("b",new Byte((byte) i));
            feature.setPropertyValue("c",new Short((short) i));
            feature.setPropertyValue("d",new Double(i));
            feature.setPropertyValue("e",new Float(i));
            feature.setPropertyValue("f",new String(i + " "));
            feature.setPropertyValue("g",new Date(i));
            feature.setPropertyValue("h",new Boolean(true));
            feature.setPropertyValue("i",new Integer(22));
            feature.setPropertyValue("j",new Long(1234567890123456789L));
            feature.setPropertyValue("k",new BigDecimal(new BigInteger("12345678901234567890123456789"), 2));
            feature.setPropertyValue("l",new BigInteger("12345678901234567890123456789"));
            features.add(feature);
        }
        return features;
    }

    private FeatureType createExampleSchema() {
        FeatureTypeBuilder build = new FeatureTypeBuilder();
        build.setName("junk");
        build.addAttribute(Point.class).setName("a").setCRS(CommonCRS.WGS84.normalizedGeographic());
        build.addAttribute(Byte.class).setName("b");
        build.addAttribute(Short.class).setName("c");
        build.addAttribute(Double.class).setName("d");
        build.addAttribute(Float.class).setName("e");
        build.addAttribute(String.class).setName("f");
        build.addAttribute(Date.class).setName("g");
        build.addAttribute(Boolean.class).setName("h");
        build.addAttribute(Number.class).setName("i");
        build.addAttribute(Long.class).setName("j");
        build.addAttribute(BigDecimal.class).setName("k");
        build.addAttribute(BigInteger.class).setName("l");
        return build.build();
    }

    private void make3D(final Geometry g) {
        Coordinate[] c = g.getCoordinates();
        for (int i = 0, ii = c.length; i < ii; i++) {
            c[i].z = 42 + i;
        }
    }

    private void writeFeatures(final ShapefileFeatureStore s, final Collection<Feature> fc)
            throws Exception {

        final FeatureType sft = fc.iterator().next().getType();

        s.createFeatureType(sft);
        FeatureWriter fw = s.getFeatureWriter(QueryBuilder.all(sft.getName().toString()));
        Iterator<Feature> it = fc.iterator();
        while (it.hasNext()) {
            Feature feature = it.next();
            Feature newFeature = fw.next();
            FeatureExt.copy(feature, newFeature, false);
            fw.write();
        }
        fw.close();
    }

    private void runWriteReadTest(final Geometry geom, final boolean d3) throws Exception {
        // make features

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Junk");
        ftb.addAttribute(geom.getClass()).setName("a").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        Collection<Feature> features = new ArrayList<>();
        for (int i = 0, ii = 20; i < ii; i++) {
            final Feature feature = type.newInstance();
            feature.setPropertyValue("a", (Geometry) geom.clone());
            features.add(feature);
        }

        // set up file
        File tmpFile = getTempFile();
        tmpFile.delete();

        // write features
        ShapefileFeatureStore shapeFeatureStore = new ShapefileFeatureStore(tmpFile.toURI());
        shapeFeatureStore.createFeatureType(type);
        writeFeatures(shapeFeatureStore, features);

        // read features
        shapeFeatureStore = new ShapefileFeatureStore(tmpFile.toURI());
        FeatureCollection fc = loadFeatures(shapeFeatureStore);
        FeatureIterator fci = fc.iterator();
        // verify
        while (fci.hasNext()) {
            Feature f = fci.next();
            Geometry fromShape = (Geometry) FeatureExt.getDefaultGeometryAttributeValue(f);

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

    private ShapefileFeatureStore createDataStore(final File f) throws Exception {
        Collection<Feature> fc = createFeatureCollection();
        ShapefileFeatureStore sds = new ShapefileFeatureStore(f.toURI());
        writeFeatures(sds, fc);
        return sds;
    }

    private ShapefileFeatureStore createDataStore() throws Exception {
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
