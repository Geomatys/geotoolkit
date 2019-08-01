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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.data.DefiningFeatureSet;
import org.geotoolkit.data.mapinfo.mif.MIFProvider;
import org.geotoolkit.data.mapinfo.mif.MIFStore;
import org.geotoolkit.storage.DataStores;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MIFStoreTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final FilterFactory FF = DefaultFactories.forClass(FilterFactory.class);
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

        final MIFProvider ff = (MIFProvider) DataStores.getProviderById("MIF-MID");
        final Parameters params = Parameters.castOrWrap(ff.getOpenParameters().createValue());
        params.getOrCreate(MIFProvider.PATH).setValue(f.toUri());

        //create new store from scratch
        try (final MIFStore store = (MIFStore) ff.open(params)) {
            assertNotNull(store);

            //create a feature type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("test");
            ftb.addAttribute(Integer.class).setName("integerProp");
            ftb.addAttribute(Double.class).setName("doubleProp");
            ftb.addAttribute(String.class).setName("stringProp");
            ftb.addAttribute(Point.class).setName("geometryProp").setCRS(CommonCRS.WGS84.normalizedGeographic());
            final FeatureType featureType = ftb.build();

            store.add(new DefiningFeatureSet(featureType, null));
            assertEquals(1, store.components().size());

            WritableFeatureSet resource = (WritableFeatureSet) store.components().iterator().next();
            FeatureType ft = resource.getType();
            for (PropertyType desc : featureType.getProperties(true)) {
                if (!(desc instanceof AttributeType))
                    continue; // ignore conventions and operations
                PropertyType td = ft.getProperty(desc.getName().toString());
                assertNotNull(td);
                assertTrue(((AttributeType) td).getValueClass().isAssignableFrom(((AttributeType) desc).getValueClass()));
            }


            Feature feature1 = ft.newInstance();
            feature1.setPropertyValue("integerProp", 8);
            feature1.setPropertyValue("doubleProp", 3.12);
            feature1.setPropertyValue("stringProp", "hello");
            feature1.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(10.3, 15.7)));

            Feature feature2 = ft.newInstance();
            feature2.setPropertyValue("integerProp", -15);
            feature2.setPropertyValue("doubleProp", -7.1);
            feature2.setPropertyValue("stringProp", "world");
            feature2.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(-1.6, -5.4)));

            final List<Feature> expectedFeatures = new ArrayList<>(2);
            expectedFeatures.add(feature1);
            expectedFeatures.add(feature2);

            resource.add(Arrays.asList(feature1, feature2).iterator());

            checkFeatures(expectedFeatures, resource, new SimpleQuery());
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
        try (final MIFStore store = new MIFStore(tmpFile.toUri())) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("multi");
            ftb.addAttribute(String.class).setName("otherProperty");
            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
            final FeatureType featureType = ftb.build();

            store.add(new DefiningFeatureSet(featureType, null));
            assertEquals(1, store.components().size());

            final WritableFeatureSet resource = (WritableFeatureSet) store.components().iterator().next();
            final FeatureType ft = resource.getType();

            final Feature feature1 = ft.newInstance();
            final Point point = GF.createPoint(new Coordinate(10.3, 15.7));
            feature1.setPropertyValue("otherProperty", "here is a point");
            feature1.setPropertyValue("geometry", point);

            final Feature feature2 = ft.newInstance();
            final Coordinate startPoint = new Coordinate(-1.6, -5.4);
            Coordinate[] coords = {startPoint, new Coordinate(0, 0), new Coordinate(3.1, 3.4)};
            final MultiLineString line = GF.createMultiLineString(new LineString[]{GF.createLineString(coords)});
            feature2.setPropertyValue("otherProperty", "here is a line");
            feature2.setPropertyValue("geometry", line);

            final Feature feature3 = ft.newInstance();
            coords = new Coordinate[]{startPoint, new Coordinate(-6, -6), new Coordinate(-3.1, -3.4), startPoint};
            final MultiPolygon polygon = GF.createMultiPolygon(new Polygon[]{GF.createPolygon(coords)});
            feature3.setPropertyValue("otherProperty", "here is a polygon");
            feature3.setPropertyValue("geometry", polygon);

            final Feature feature4 = ft.newInstance();
            final Geometry[] geometries = new Geometry[]{polygon, line, GF.createMultiPoint(new Point[]{point})};
            feature4.setPropertyValue("otherProperty", "here is a collection of geometries.");
            feature4.setPropertyValue("geometry", GF.createGeometryCollection(geometries));

            final List<Feature> expectedFeatures = new ArrayList<>(4);
            expectedFeatures.add(feature1);
            expectedFeatures.add(feature2);
            expectedFeatures.add(feature3);
            expectedFeatures.add(feature4);

            resource.add(expectedFeatures.iterator());

            checkFeatures(expectedFeatures, resource, new SimpleQuery());
        }
    }

    /**
     * Check that we can filter data properties at read time (using {@link Query#getPropertyNames() }).
     * @throws Exception
     */
    @Test
    public void filterProperties() throws Exception {
        final Path tmpFile = Files.createTempFile(tempDir, "filtered", ".mif");
        try (final MIFStore store = new MIFStore(tmpFile.toUri())) {

            //create a feature type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("test");
            ftb.addAttribute(Integer.class).setName("integerProp");
            ftb.addAttribute(Double.class).setName("doubleProp");
            ftb.addAttribute(String.class).setName("stringProp");
            ftb.addAttribute(Point.class).setName("geometryProp").setCRS(CommonCRS.WGS84.normalizedGeographic());
            final FeatureType featureType = ftb.build();

            store.add(new DefiningFeatureSet(featureType, null));
            assertEquals(1, store.components().size());

            final WritableFeatureSet resource = (WritableFeatureSet) store.components().iterator().next();
            final FeatureType ft = resource.getType();

            final Feature feature1 = ft.newInstance();
            feature1.setPropertyValue("integerProp", 8);
            feature1.setPropertyValue("doubleProp", 3.12);
            feature1.setPropertyValue("stringProp", "hello");
            feature1.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(10.3, 15.7)));

            final Feature feature2 = ft.newInstance();
            feature2.setPropertyValue("integerProp", -15);
            feature2.setPropertyValue("doubleProp", -7.1);
            feature2.setPropertyValue("stringProp", "world");
            feature2.setPropertyValue("geometryProp", GF.createPoint(new Coordinate(-1.6, -5.4)));

            List<Feature> expectedFeatures = new ArrayList<>(2);
            expectedFeatures.add(feature1);
            expectedFeatures.add(feature2);

            resource.add(expectedFeatures.iterator());

            // filter output
            final SimpleQuery query = new SimpleQuery();
            final String[] filteredProps = new String[]{"stringProp", "integerProp"};
            query.setColumns(
                    new SimpleQuery.Column(FF.property("stringProp")),
                    new SimpleQuery.Column(FF.property("integerProp")));
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
            checkFeatures(expectedFeatures, resource, query);
        }
    }

    private static void checkFeatures(final List<Feature> expected, final FeatureSet source, final SimpleQuery readQuery) throws DataStoreException {

        FeatureSet subset = source.subset(readQuery);
        int number = 0;
        try (final Stream<Feature> stream = subset.features(false)) {
            final Iterator<Feature> reader = stream.iterator();
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
