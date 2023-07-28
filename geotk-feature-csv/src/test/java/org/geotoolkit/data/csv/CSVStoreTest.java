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

package org.geotoolkit.data.csv;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.nio.IOUtilities;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CSVStoreTest {

    public CSVStoreTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreate() throws Exception{

        File f = File.createTempFile("test", ".csv");
        f.deleteOnExit();

        final CSVStore ds = (CSVStore) DataStores.open(new StorageConnector(f));
        assertNotNull(ds);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Integer.class).setName("integerProp");
        ftb.addAttribute(Double.class).setName("doubleProp");
        ftb.addAttribute(String.class).setName("stringProp");
        ftb.addAttribute(Geometry.class).setName("geometryProp").setCRS(CommonCRS.WGS84.normalizedGeographic());
        FeatureType sft = ftb.build();
        ds.updateType(sft);
        GenericName name = ds.getType().getName();

        FeatureType ft = ds.getType();
        for (PropertyType desc : sft.getProperties(true)) {
            PropertyType td = ft.getProperty(desc.getName().toString());
            assertNotNull(td);
            assertEquals( ((AttributeType)td).getValueClass(), ((AttributeType)desc).getValueClass());
        }

        ds.add(Arrays.asList(ft.newInstance(), ft.newInstance(), ft.newInstance()).iterator());

        long number = ds.features(false).count();
        assertEquals(3l, number);

    }

    @Test
    public void testReadEscape() throws Exception {
        try (final CSVStore store = new CSVStore(Paths.get("./src/test/resources/org/geotoolkit/csv/escaped.csv"), ';')) {

            try (Stream<Feature> stream = store.features(false)) {
                Iterator<Feature> ite = stream.iterator();
                Feature next = ite.next();
                assertEquals("hubert", next.getPropertyValue("name"));
                assertEquals("someone from the \"big fisher\" corp,\na good guy and;\na ;family \"best\" friend", next.getPropertyValue("comment"));
                assertEquals(36, next.getPropertyValue("age"));
                next = ite.next();
                assertEquals("marc", next.getPropertyValue("name"));
                assertEquals("lucky luck", next.getPropertyValue("comment"));
                assertEquals(22, next.getPropertyValue("age"));
            }
        }
    }

    @Test
    public void testWriteEscape() throws Exception {

        final Path file = Files.createTempFile("test", ".csv");
        try (final CSVStore store = new CSVStore(file, ';')) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("test");
            ftb.addAttribute(String.class).setName("name");
            ftb.addAttribute(String.class).setName("comment");
            ftb.addAttribute(Integer.class).setName("age");
            final FeatureType ft = ftb.build();

            store.updateType(ft);

            final Feature f = ft.newInstance();
            f.setPropertyValue("name", "hubert");
            f.setPropertyValue("comment", "someone from the \"big fisher\" corp,\na good guy and\na family \"best\" friend");
            f.setPropertyValue("age", 36);
            store.add(Collections.singleton(f).iterator());

            final String str = IOUtilities.toString(file);
            assertEquals("name(String);comment(String);age(Integer)\n"
                    + "hubert;\"someone from the \"\"big fisher\"\" corp,\n"
                    + "a good guy and\n"
                    + "a family \"\"best\"\" friend\";36\n", str);
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
