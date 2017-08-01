/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.mapinfo.mif.MIFFeatureStoreFactory;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.mif.MIFFeatureStore;
import org.geotoolkit.data.query.Query;
import org.junit.After;
import org.junit.Test;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.DataStores;
import static org.junit.Assert.*;
import org.junit.Before;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MIFFeatureStoreTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();
    private Path tempDir;

    @Before
    public void prepareDirectory() throws IOException {
        tempDir = Files.createTempDirectory("mifMidTests");
    }

    @After
    public void tearDown() throws IOException {
        Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    /**
     * Test store creation.
     *
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        final Path f = Files.createTempFile(tempDir, "test", ".mif");

        final FeatureStoreFactory ff = (FeatureStoreFactory) DataStores.getFactoryById("MIF-MID");
        final Parameters params = Parameters.castOrWrap(ff.getParametersDescriptor().createValue());
        params.getOrCreate(MIFFeatureStoreFactory.PATH).setValue(f.toUri());

        //create new store from scratch
        try (final FeatureStore ds = (FeatureStore) ff.create(params)) {
            assertNotNull(ds);

            //create a feature type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("test");
            ftb.addAttribute(Integer.class).setName("integerProp");
            ftb.addAttribute(Double.class).setName("doubleProp");
            ftb.addAttribute(String.class).setName("stringProp");
            ftb.addAttribute(Point.class).setName("geometryProp").setCRS(CommonCRS.WGS84.normalizedGeographic());
            final FeatureType featureType = ftb.build();

            ds.createFeatureType(featureType);
            final Set<GenericName> names = ds.getNames();
            assertEquals(1, names.size());

            GenericName name = names.iterator().next();

            for (GenericName n : names) {
                FeatureType ft = ds.getFeatureType(n.toString());
                for (PropertyType desc : featureType.getProperties(true)) {
                    if (!(desc instanceof AttributeType))
                        continue; // ignore conventions and operations
                    PropertyType td = ft.getProperty(desc.getName().toString());
                    assertNotNull(td);
                    assertTrue(((AttributeType) td).getValueClass().isAssignableFrom(((AttributeType) desc).getValueClass()));
                }
            }

            final List<Feature> expectedFeatures = new ArrayList<>(2);
            try (final FeatureWriter fw = ds.getFeatureWriter(QueryBuilder.filtered(name.toString(), Filter.EXCLUDE))) {
                Feature feature = fw.next();
                feature.setPropertyValue("integerProp", 8);
                feature.setPropertyValue("doubleProp", 3.12);
                feature.setPropertyValue("stringProp", "hello");
                feature.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(10.3, 15.7)));
                fw.write();
                expectedFeatures.add(feature);

                feature = fw.next();
                feature.setPropertyValue("integerProp", -15);
                feature.setPropertyValue("doubleProp", -7.1);
                feature.setPropertyValue("stringProp", "world");
                feature.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(-1.6, -5.4)));
                fw.write();
                expectedFeatures.add(feature);
            }

            checkFeatures(expectedFeatures, ds, QueryBuilder.all(name.toString()));

            //test with hint
            QueryBuilder qb = new QueryBuilder(name.toString());
            qb.setHints(new Hints(HintsPending.FEATURE_DETACHED, Boolean.FALSE));
            checkFeatures(expectedFeatures, ds, qb.buildQuery());
        }
    }

    /**
     * Check that we're capable of writing then read back features with
     * different type of geometries (point, polygon, etc.) into a single MIF-MID
     * file.
     */
    @Test
    public void multipleGeometryTypes() throws Exception {
        final Path tmpFile = Files.createTempFile(tempDir, "multiple", ".mif");
        try (final MIFFeatureStore store = new MIFFeatureStore(tmpFile.toUri())) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("multi");
            ftb.addAttribute(String.class).setName("otherProperty");
            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
            final FeatureType featureType = ftb.build();

            store.createFeatureType(featureType);
            final Set<GenericName> names = store.getNames();
            assertEquals(1, names.size());

            GenericName name = names.iterator().next();
            final List<Feature> expectedFeatures = new ArrayList<>(4);
            try (final FeatureWriter fw = store.getFeatureWriter(QueryBuilder.filtered(name.toString(), Filter.EXCLUDE))) {
                Feature feature = fw.next();
                feature.setPropertyValue("otherProperty", "here is a point");
                final Point point = GF.createPoint(new Coordinate(10.3, 15.7));
                feature.setPropertyValue("geometry", point);
                fw.write();
                expectedFeatures.add(feature);

                feature = fw.next();
                feature.setPropertyValue("otherProperty", "here is a line");
                final Coordinate startPoint = new Coordinate(-1.6, -5.4);
                Coordinate[] coords = {startPoint, new Coordinate(0, 0), new Coordinate(3.1, 3.4)};
                final MultiLineString line = GF.createMultiLineString(new LineString[]{GF.createLineString(coords)});
                feature.setPropertyValue("geometry", line);
                fw.write();
                expectedFeatures.add(feature);

                feature = fw.next();
                feature.setPropertyValue("otherProperty", "here is a polygon");
                coords = new Coordinate[]{startPoint, new Coordinate(-6, -6), new Coordinate(-3.1, -3.4), startPoint};
                final MultiPolygon polygon = GF.createMultiPolygon(new Polygon[]{GF.createPolygon(coords)});
                feature.setPropertyValue("geometry", polygon);
                fw.write();
                expectedFeatures.add(feature);

                feature = fw.next();
                feature.setPropertyValue("otherProperty", "here is a collection of geometries.");
                final Geometry[] geometries = new Geometry[]{polygon, line, GF.createMultiPoint(new Point[]{point})};
                feature.setPropertyValue("geometry", GF.createGeometryCollection(geometries));
                fw.write();
                expectedFeatures.add(feature);
            }

            checkFeatures(expectedFeatures, store, QueryBuilder.all(name.toString()));
        }
    }

    /**
     * Check that we can filter data properties at read time (using {@link Query#getPropertyNames() }).
     * @throws Exception
     */
    @Test
    public void filterProperties() throws Exception {
        final Path tmpFile = Files.createTempFile(tempDir, "filtered", ".mif");
        try (final MIFFeatureStore store = new MIFFeatureStore(tmpFile.toUri())) {

            //create a feature type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("test");
            ftb.addAttribute(Integer.class).setName("integerProp");
            ftb.addAttribute(Double.class).setName("doubleProp");
            ftb.addAttribute(String.class).setName("stringProp");
            ftb.addAttribute(Point.class).setName("geometryProp").setCRS(CommonCRS.WGS84.normalizedGeographic());
            final FeatureType featureType = ftb.build();

            store.createFeatureType(featureType);
            final Set<GenericName> names = store.getNames();
            assertEquals(1, names.size());

            GenericName name = names.iterator().next();

            List<Feature> expectedFeatures = new ArrayList<>(2);
            try (final FeatureWriter fw = store.getFeatureWriter(QueryBuilder.filtered(name.toString(), Filter.EXCLUDE))) {
                Feature feature = fw.next();
                feature.setPropertyValue("integerProp", 8);
                feature.setPropertyValue("doubleProp", 3.12);
                feature.setPropertyValue("stringProp", "hello");
                feature.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(10.3, 15.7)));
                fw.write();
                expectedFeatures.add(feature);

                feature = fw.next();
                feature.setPropertyValue("integerProp", -15);
                feature.setPropertyValue("doubleProp", -7.1);
                feature.setPropertyValue("stringProp", "world");
                feature.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(-1.6, -5.4)));
                fw.write();
                expectedFeatures.add(feature);
            }

            // filter output
            final QueryBuilder queryBuilder = new QueryBuilder();
            final String[] filteredProps = new String[]{"stringProp", "integerProp"};
            queryBuilder.setProperties(filteredProps);
            queryBuilder.setTypeName(name);
            ftb.getProperty("doubleProp").remove();
            ftb.getProperty("geometryProp").remove();

            // Modify original dataset to contain only properties kept in above query.
            final FeatureType filteredType = ftb.build();
            expectedFeatures = expectedFeatures.stream()
                    .map(f -> {
                        final Feature result = filteredType.newInstance();
                        for (final String prop : filteredProps) {
                            result.setPropertyValue(prop, f.getPropertyValue(prop));
                        }
                        return result;
                    })
                    .collect(Collectors.toList());
            checkFeatures(expectedFeatures, store, queryBuilder.buildQuery());
        }
    }

    private static void checkFeatures(final List<Feature> expected, final FeatureStore source, final Query readQuery) throws DataStoreException {
        int number = 0;
        try (final FeatureReader reader = source.getFeatureReader(readQuery)) {
            while (reader.hasNext()) {
                assertSame(expected.get(number), reader.next());
                number++;
            }
        }

        assertEquals(expected.size(), number);
    }

    /**
     * a comparison method ensuring two features have got the same properties
     * with the same values. If it's not the case, an assertion error is thrown.
     *
     * @param expected The feature containing expected data
     * @param candidate The feature to test against "witness" feature.
     */
    private static void assertSame(final Feature expected, final Feature candidate) {
        expected.getType().getProperties(true).stream()
                .map(PropertyType::getName)
                .map(GenericName::toString)
                .forEach(name
                        -> assertEquals(
                                "Property " + name + " differs",
                                expected.getPropertyValue(name),
                                candidate.getPropertyValue(name)
                            )
                );
    }
}
