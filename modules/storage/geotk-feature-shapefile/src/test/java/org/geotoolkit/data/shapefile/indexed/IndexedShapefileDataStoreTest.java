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
package org.geotoolkit.data.shapefile.indexed;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.FeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.geotoolkit.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.Collection;
import java.util.Iterator;

import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.type.FeatureType;

import org.geotoolkit.test.TestData;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;

import org.geotoolkit.geometry.DefaultBoundingBox;
import static org.junit.Assert.*;

/**
 *
 * @version $Id$
 * @author Ian Schneider
 * @module pending
 */
public class IndexedShapefileDataStoreTest extends AbstractTestCaseSupport {
    static final String STATE_POP = "shapes/statepop.shp";

    static final String STREAM = "shapes/stream.shp";

    static final String DANISH = "shapes/danish_point.shp";

    static final String CHINESE = "shapes/chinese_poly.shp";

    protected FeatureCollection loadFeatures(final String resource, final Query q)
            throws Exception {

        URL url = ShapeTestData.url(resource);
        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(url);

        final FeatureCollection features;
        if(q == null){
            features = s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
        }else{
            features = s.createSession(true).getFeatureCollection(q);
        }

        s.close();
        return features;
    }

    protected FeatureCollection loadFeatures(final String resource, final Charset charset,
            final Query q) throws Exception {

        URL url = ShapeTestData.url(resource);
        ShapefileFeatureStore s = new IndexedShapefileFeatureStore(url, null, false, true, IndexType.QIX, charset);

        final FeatureCollection features;
        if(q == null){
            features = s.createSession(true).getFeatureCollection(QueryBuilder.all(s.getName()));
        }else{
            features = s.createSession(true).getFeatureCollection(q);
        }

        s.close();
        return features;
    }

    protected FeatureCollection loadFeatures(final IndexedShapefileFeatureStore s)
            throws Exception {
        return s.createSession(false).getFeatureCollection(QueryBuilder.all(s.getName()));
    }

    @Test
    public void testLoad() throws Exception {
        loadFeatures(STATE_POP, null);
    }

    @Test
    public void testLoadDanishChars() throws Exception {
        FeatureCollection fc = loadFeatures(DANISH, null);
        Feature first = firstFeature(fc);
        // Charlï¿½tte, if you can read it with your OS charset
        assertEquals("Charl\u00F8tte", first.getPropertyValue("TEKST1"));
    }

    @Test
    public void testLoadChineseChars() throws Exception {
        try {
            FeatureCollection fc = loadFeatures(CHINESE, Charset.forName("GB18030"), null);
            Feature first = firstFeature(fc);
            String name = (String) first.getPropertyValue("NAME");
            assertEquals("\u9ed1\u9f99\u6c5f\u7701", name);
        } catch (UnsupportedCharsetException no) {
            // this JDK has not been installed with the required
            // lanaguage
        }
    }

    @Test
    public void testSchema() throws Exception {
        URL url = ShapeTestData.url(STATE_POP);
        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(url);
        FeatureType schema = s.getFeatureType(s.getTypeNames()[0]);
        Collection<PropertyDescriptor> types = schema.getDescriptors();
        assertEquals("Number of Attributes", 253, types.size());
        assertNotNull(schema.getCoordinateReferenceSystem());
    }

    @Test
    public void testSpacesInPath() throws Exception {
        URL u = TestData.url(AbstractTestCaseSupport.class, "folder with spaces/pointtest.shp");
        File f = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
        assertTrue(f.exists());

        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(u);
        loadFeatures(s);
        s.close();
    }

    /**
     * Test envelope versus old DataSource
     */
    @Test
    public void testEnvelope() throws Exception {
        FeatureCollection features = loadFeatures(STATE_POP, null);
        testEnvelope(features, IndexType.QIX);
        testEnvelope(features, IndexType.NONE);
    }

    private void testEnvelope(final FeatureCollection features, final IndexType treeType)
            throws MalformedURLException, IOException, DataStoreException {
        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(ShapeTestData
                .url(STATE_POP), null, true, true, treeType,null);
        GenericName typeName = s.getName();
        FeatureCollection all = s.createSession(true).getFeatureCollection(QueryBuilder.all(typeName));

        assertEquals(features.getEnvelope(), all.getEnvelope());
        s.close();
    }

    @Test
    public void testCreateAndReadQIX() throws Exception {
        File shpFile = copyShapefiles(STATE_POP);
        URL url = shpFile.toURI().toURL();
        String filename = url.getFile();
        filename = filename.substring(0, filename.lastIndexOf("."));

        File file = new File(filename + ".qix");

        if (file.exists()) {
            file.delete();
        }
        file.deleteOnExit();

        IndexedShapefileFeatureStore ds = new IndexedShapefileFeatureStore(url, null, true, true, IndexType.QIX,null);
        FeatureIterator indexIter = ds.getFeatureReader(QueryBuilder.all(ds.getName()));

        GeometryFactory factory = new GeometryFactory();
        double area = Double.MAX_VALUE;
        Feature smallestFeature = null;
        while (indexIter.hasNext()) {
            Feature newFeature = indexIter.next();

            BoundingBox bounds = DefaultBoundingBox.castOrCopy(newFeature.getBounds());
            Geometry geometry = factory.toGeometry(new JTSEnvelope2D(
                    bounds));
            double newArea = geometry.getArea();

            if (smallestFeature == null || newArea < area) {
                smallestFeature = newFeature;
                area = newArea;
            }
        }
        indexIter.close();

        IndexedShapefileFeatureStore ds2 = new IndexedShapefileFeatureStore(url,
                null, false, false, IndexType.NONE,null);

        Envelope newBounds = (JTSEnvelope2D)ds.getEnvelope(QueryBuilder.all(ds2.getNames().iterator().next()));
        double dx = newBounds.getWidth() / 4;
        double dy = newBounds.getHeight() / 4;
        newBounds = new Envelope(newBounds.getMinX() + dx, newBounds.getMaxX()
                - dx, newBounds.getMinY() + dy, newBounds.getMaxY() - dy);

        CoordinateReferenceSystem crs = ds.getFeatureType().getCoordinateReferenceSystem();

        performQueryComparison(ds, ds2, new JTSEnvelope2D(newBounds, crs));
        performQueryComparison(ds, ds2, new JTSEnvelope2D(smallestFeature.getBounds()));

        assertTrue(file.exists());
        ds.close();
        ds2.close();
    }

    @Test
    public void testFidFilter() throws Exception {
        File shpFile = copyShapefiles(STATE_POP);
        URL url = shpFile.toURI().toURL();
        IndexedShapefileFeatureStore ds = new IndexedShapefileFeatureStore(url, null, true, true, IndexType.NONE,null);
        FeatureCollection features = ds.createSession(true).getFeatureCollection(QueryBuilder.all(ds.getName()));
        FeatureIterator indexIter = features.iterator();

        Set<String> expectedFids = new HashSet<String>();
        final Filter fidFilter;
        try {
            FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);
            Set<FeatureId> fids = new HashSet<FeatureId>();
            while (indexIter.hasNext()) {
                Feature newFeature = indexIter.next();
                String id = newFeature.getIdentifier().getID();
                expectedFids.add(id);
                fids.add(ff.featureId(id));
            }
            fidFilter = ff.id(fids);
        } finally {
            indexIter.close();
        }

        Set<String> actualFids = new HashSet<String>();
        {
            indexIter = ds.getFeatureReader(QueryBuilder.filtered(ds.getName(), fidFilter));
            while (indexIter.hasNext()) {
                Feature next = indexIter.next();
                String id = next.getIdentifier().getID();
                actualFids.add(id);
            }
            indexIter.close();
        }

        TreeSet<String> lackingFids = new TreeSet<String>(expectedFids);
        lackingFids.removeAll(actualFids);

        TreeSet<String> unexpectedFids = new TreeSet<String>(actualFids);
        unexpectedFids.removeAll(expectedFids);

        String lacking = String.valueOf(lackingFids);
        String unexpected = String.valueOf(unexpectedFids);
        String failureMsg = "lacking fids: " + lacking + ". Unexpected ones: " + unexpected;
        assertEquals(failureMsg, expectedFids.size(), actualFids.size());
        assertEquals(failureMsg, expectedFids, actualFids);
    }

    private ArrayList performQueryComparison(
            final IndexedShapefileFeatureStore indexedDS,
            final IndexedShapefileFeatureStore baselineDS, final JTSEnvelope2D newBounds)
            throws FactoryRegistryException,
            IOException, DataStoreException {
        FeatureCollection features;
        FeatureIterator indexIter;
        FilterFactory2 fac = (FilterFactory2) FactoryFinder.getFilterFactory(null);
        String geometryName = indexedDS.getFeatureType().getGeometryDescriptor().getLocalName();

        Filter filter = fac.bbox(fac.property(geometryName), newBounds);

        features = indexedDS.createSession(true).getFeatureCollection(QueryBuilder.filtered(indexedDS.getName(),filter));
        FeatureCollection features2 = baselineDS.createSession(true).getFeatureCollection(QueryBuilder.filtered(baselineDS.getName(),filter));

        FeatureIterator baselineIter = features2.iterator();
        indexIter = features.iterator();

        ArrayList baselineFeatures = new ArrayList();
        ArrayList indexedFeatures = new ArrayList();

        try {
            while (baselineIter.hasNext()) {
                baselineFeatures.add(baselineIter.next());
            }
            while (indexIter.hasNext()) {
                indexedFeatures.add(indexIter.next());
            }
            assertFalse(indexIter.hasNext());
            assertFalse(baselineIter.hasNext());
            assertTrue(baselineFeatures.containsAll(indexedFeatures));
            assertTrue(indexedFeatures.containsAll(baselineFeatures));
        } finally {
            indexIter.close();
            baselineIter.close();
        }
        return indexedFeatures;
    }

    @Test
    public void testLoadAndVerify() throws Exception {
        FeatureCollection features = loadFeatures(STATE_POP, null);

        int count = features.size();
        assertTrue("Got Features", count > 0);
        // assertEquals("Number of Features loaded", 49, count); // FILE CORRECT
        // assertEquals("Number of Features loaded", 3, count); // JAR WRONG

        FeatureType schema = firstFeature(features).getType();
        assertNotNull(schema.getGeometryDescriptor());
        assertEquals("Number of Attributes", 253, schema.getDescriptors().size());
        assertEquals("Value of statename is wrong", firstFeature(features)
                .getPropertyValue("STATE_NAME"), "Illinois");
        assertEquals("Value of land area is wrong", ((Double) firstFeature(
                features).getPropertyValue("LAND_KM")).doubleValue(), 143986.61,
                0.001);
    }

    private IndexedShapefileFeatureStore createDataStore(final File f) throws Exception {
        Collection<Feature> fc = createFeatureCollection();
        f.createNewFile();

        IndexedShapefileFeatureStore sds = new IndexedShapefileFeatureStore(f.toURI().toURL());
        writeFeatures(sds, fc);

        return sds;
    }

    private IndexedShapefileFeatureStore createDataStore() throws Exception {
        return createDataStore(getTempFile());
    }

    /**
     * Create a set of features, then remove every other one, updating the
     * remaining. Test for removal and proper update after reloading...
     */
    @Test
    public void testUpdating() throws Throwable {
        IndexedShapefileFeatureStore sds = createDataStore();
        loadFeatures(sds);

        FeatureWriter writer = null;

        try {
            writer = sds.getFeatureWriter(sds.getName(), Filter.INCLUDE);

            while (writer.hasNext()) {
                SimpleFeature feat = (SimpleFeature) writer.next();
                Byte b = (Byte) feat.getAttribute(1);

                if ((b.byteValue() % 2) == 0) {
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

        FeatureCollection fc = loadFeatures(sds);

        assertEquals(10, fc.size());

        FeatureIterator i = fc.iterator();
        for (; i.hasNext();) {
            assertEquals(-1, ((Byte) ((SimpleFeature)i.next()).getAttribute(1)).byteValue());
        }
        i.close();
        sds.close();
    }

    /**
     * Create a test file, then continue removing the first entry until there
     * are no features left.
     */
    @Test
    public void testRemoveFromFrontAndClose() throws Throwable {
        IndexedShapefileFeatureStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter writer = null;

            try {
                writer = sds.getFeatureWriter(sds.getName(), Filter.INCLUDE);
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
        sds.close();
    }

    /**
     * Create a test file, then continue removing the first entry until there
     * are no features left.
     */
    @Test
    public void testRemoveFromFrontAndCloseTransaction() throws Throwable {
        IndexedShapefileFeatureStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter writer = null;

            try {
                writer = sds.getFeatureWriter(sds.getName(), Filter.INCLUDE);
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
        IndexedShapefileFeatureStore sds = createDataStore();

        int idx = loadFeatures(sds).size();

        while (idx > 0) {
            FeatureWriter writer = null;

            try {
                writer = sds.getFeatureWriter(sds.getName(), Filter.INCLUDE);

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
        sds.close();
    }

    @Test
    public void testTestTransaction() throws Exception {
        final IndexedShapefileFeatureStore sds = createDataStore();
        final long idx = sds.getCount(QueryBuilder.all(sds.getName()));
        final Session session = sds.createSession(true);

        Feature[] newFeatures1 = new Feature[1];
        Feature[] newFeatures2 = new Feature[2];
        GeometryFactory fac = new GeometryFactory();
        newFeatures1[0] = FeatureTypeUtilities.template(sds.getFeatureType());
        newFeatures1[0].setPropertyValue("a",fac.createPoint(new Coordinate(0, 0)));
        newFeatures2[0] = FeatureTypeUtilities.template(sds.getFeatureType());
        newFeatures2[0].setPropertyValue("a",fac.createPoint(new Coordinate(0, 0)));
        newFeatures2[1] = FeatureTypeUtilities.template(sds.getFeatureType());
        newFeatures2[1].setPropertyValue("a",fac.createPoint(new Coordinate(0, 0)));

        session.addFeatures(sds.getName(),FeatureStoreUtilities.collection(newFeatures1));
        session.addFeatures(sds.getName(),FeatureStoreUtilities.collection(newFeatures2));
        session.commit();
        assertEquals(idx + 3, sds.getCount(QueryBuilder.all(sds.getName())));
        sds.close();

    }

    private FeatureType createExampleSchema() {
        FeatureTypeBuilder build = new FeatureTypeBuilder();
        build.setName("junk");
        build.add("a", Point.class, CommonCRS.WGS84.normalizedGeographic());
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

    private Collection<Feature> createFeatureCollection() throws Exception {
        FeatureType featureType = createExampleSchema();
        FeatureBuilder build = new FeatureBuilder(featureType);

        Collection<Feature> features = new ArrayList<>();
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

            Feature feature = build.buildFeature(null);
            features.add(feature);
        }
        return features;
    }

    @Test
    public void testAttributesWriting() throws Exception {
        Collection<Feature> features = createFeatureCollection();
        File tmpFile = getTempFile();
        tmpFile.createNewFile();

        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(tmpFile.toURI().toURL());
        writeFeatures(s, features);
        s.close();
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
                throw new Exception("Error in " + testName, e);
            }
        }
    }

    private void make3D(final Geometry g) {
        Coordinate[] c = g.getCoordinates();

        for (int i = 0, ii = c.length; i < ii; i++) {
            c[i].z = 42 + i;
        }
    }

    private void writeFeatures(final IndexedShapefileFeatureStore s, final Collection<Feature> fc)
            throws Exception {
        final FeatureType type = fc.iterator().next().getType();
        s.createFeatureType(type.getName(),type);

        final FeatureWriter fw = s.getFeatureWriter(type.getName(),Filter.INCLUDE);
        final Iterator<Feature> it = fc.iterator();

        while (it.hasNext()) {
            Feature feature = it.next();
            Feature newFeature = fw.next();
            FeatureUtilities.copy(feature, newFeature, false);
            fw.write();
        }

        fw.close();
        assertEquals(20,  s.getCount(QueryBuilder.all(type.getName())));
    }

    private void runWriteReadTest(final Geometry geom, final boolean d3) throws Exception {
        // make features
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Junk");
        ftb.add("a", geom.getClass(), CommonCRS.WGS84.normalizedGeographic());
        final FeatureType type = ftb.buildSimpleFeatureType();

        final Collection<Feature> features = new ArrayList<>();

        for (int i = 0, ii = 20; i < ii; i++) {
            Feature feature = FeatureBuilder.build(type,
                    new Object[] { geom.clone() }, null);
            features.add(feature);
        }

        // set up file
        File tmpFile = getTempFile();
        tmpFile.delete();

        // write features
        IndexedShapefileFeatureStore s = new IndexedShapefileFeatureStore(tmpFile
                .toURI().toURL());
        s.createFeatureType(type.getName(),type);
        writeFeatures(s, features);

        s.close();

        // read features
        s = new IndexedShapefileFeatureStore(tmpFile.toURI().toURL());

        FeatureCollection fc = loadFeatures(s);
        FeatureIterator fci = fc.iterator();

        // verify
        try{
            while (fci.hasNext()) {
                SimpleFeature f = (SimpleFeature) fci.next();
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
                        if (d3) {
                            assertTrue(c1[cc].equals3D(c2[cc]));
                        } else {
                            assertTrue(c1[cc].equals2D(c2[cc]));
                        }
                    }
                } catch (Throwable t) {
                    fail("Bogus : " + Arrays.asList(geom.getCoordinates()) + " : "
                            + Arrays.asList(fromShape.getCoordinates()));
                }
            }
        }finally{
            fci.close();
            s.close();
            tmpFile.delete();
        }
    }

    @Test
    public void testIndexOutOfDate() throws Exception {
        File shpFile = copyShapefiles(STATE_POP);
        ShpFileType fix = ShpFileType.FIX;
        File fixFile = sibling(shpFile, fix.extension);
        fixFile.delete();
        IndexedShapefileFeatureStore ds = new IndexedShapefileFeatureStore(shpFile.toURI().toURL());

        assertFalse(ds.needsGeneration(fix));
        long fixMod = fixFile.lastModified();
        shpFile.setLastModified(fixMod+1000);
        assertTrue(ds.needsGeneration(fix));
        fixFile.setLastModified(shpFile.lastModified());
        assertFalse(ds.needsGeneration(fix));
        assertTrue(fixFile.delete());
        assertTrue(ds.needsGeneration(fix));
        ds.close();
    }


    /**
     * Issueing a request, whether its a query, update or delete, with a fid filter where feature
     * ids match the {@code <typeName>.<number>} structure but the {@code <typeName>} part does not
     * match the actual typeName, shoud ensure the invalid fids are ignored
     *
     * @throws FileException
     */
    @Test
    public void testWipesOutInvalidFidsFromFilters() throws Exception {
        final IndexedShapefileFeatureStore ds = createDataStore();
        final Session session = ds.createSession(true);

        final String validFid1, validFid2, invalidFid1, invalidFid2;
        {
            FeatureIterator features = ds.getFeatureReader(QueryBuilder.all(ds.getName()));
            validFid1 = features.next().getIdentifier().getID();
            validFid2 = features.next().getIdentifier().getID();
            invalidFid1 = "_" + features.next().getIdentifier().getID();
            invalidFid2 = features.next().getIdentifier().getID()+ "abc";
            features.close();
        }
        FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);
        Set<Identifier> ids = new HashSet<Identifier>();
        ids.add(ff.featureId(validFid1));
        ids.add(ff.featureId(validFid2));
        ids.add(ff.featureId(invalidFid1));
        ids.add(ff.featureId(invalidFid2));
        Filter fidFilter = ff.id(ids);

        final FeatureType schema = ds.getFeatureType();
        final String typeName = schema.getName().tip().toString();
        //get a property of type String to update its value by the given filter
        final AttributeDescriptor attribute = (AttributeDescriptor) schema.getDescriptor("f");

        assertEquals(2, count(ds, typeName, fidFilter));

        session.updateFeatures(ds.getName(),fidFilter,attribute, "modified");
        session.commit();
        Filter modifiedFilter = ff.equals(ff.property("f"), ff.literal("modified"));
        assertEquals(2, count(ds, typeName, modifiedFilter));

        final long initialCount = ds.getCount(QueryBuilder.all(ds.getName()));
        session.removeFeatures(ds.getName(),fidFilter);
        session.commit();
        final long afterCount = ds.getCount(QueryBuilder.all(ds.getName()));
        assertEquals(initialCount - 2, afterCount);
    }

    private int count(final FeatureStore ds, final String typeName, final Filter filter) throws Exception {
        FeatureReader reader;
        reader = ds.getFeatureReader(QueryBuilder.filtered(ds.getFeatureType(typeName).getName(), filter));
        int count = 0;
        try {
            while (reader.hasNext()) {
                reader.next();
                count++;
            }
        } finally {
            reader.close();
        }
        return count;
    }

}
