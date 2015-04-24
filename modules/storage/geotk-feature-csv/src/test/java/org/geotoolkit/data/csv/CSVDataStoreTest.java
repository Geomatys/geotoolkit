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

import java.util.Map;
import java.util.Collections;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.nio.IOUtilities;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.util.GenericName;
import static org.junit.Assert.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVDataStoreTest extends org.geotoolkit.test.TestBase {

    public CSVDataStoreTest() {
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

        final FeatureStore ds = (FeatureStore) DataStores.open(
                (Map)Collections.singletonMap(AbstractFileFeatureStoreFactory.PATH.getName().getCode(),
                f.toURI().toURL()));
        assertNotNull(ds);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Integer.class).setName("integerProp");
        ftb.addAttribute(Double.class).setName("doubleProp");
        ftb.addAttribute(String.class).setName("stringProp");
        ftb.addAttribute(Geometry.class).setName("geometryProp").setCRS(CommonCRS.WGS84.normalizedGeographic());
        FeatureType sft = ftb.build();
        ds.createFeatureType(sft);
        GenericName name = ds.getNames().iterator().next();

        assertEquals(1, ds.getNames().size());

        for(GenericName n : ds.getNames()){
            FeatureType ft = ds.getFeatureType(n.toString());
            for(PropertyType desc : sft.getProperties(true)){
                PropertyType td = ft.getProperty(desc.getName().tip().toString());
                assertNotNull(td);
                assertEquals( ((AttributeType)td).getValueClass(), ((AttributeType)desc).getValueClass());
            }
        }

        FeatureWriter fw = ds.getFeatureWriter(
                QueryBuilder.filtered(name.toString(),Filter.EXCLUDE));
        try{
            Feature feature = fw.next();
            fw.write();
            feature = fw.next();
            fw.write();
            feature = fw.next();
            fw.write();
        }finally{
            fw.close();
        }

        FeatureReader reader = ds.getFeatureReader(QueryBuilder.all(name.toString()));
        int number = 0;
        try{
            while(reader.hasNext()){
                number++;
                reader.next();
            }
        }finally{
            reader.close();
        }

        assertEquals(3, number);


        //test with hint
        QueryBuilder qb = new QueryBuilder(name.toString());
        qb.setHints(new Hints(HintsPending.FEATURE_DETACHED, Boolean.FALSE));
        reader = ds.getFeatureReader(qb.buildQuery());
        number = 0;
        try{
            while(reader.hasNext()){
                number++;
                reader.next();
            }
        }finally{
            reader.close();
        }

        assertEquals(3, number);

    }

    @Test
    public void testReadEscape() throws Exception{

        final FeatureStore store = new CSVFeatureStore(new File("./src/test/resources/org/geotoolkit/csv/escaped.csv"), null, ';');

        assertEquals(1, store.getNames().size());

        FeatureCollection col = store.createSession(false).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));

        final FeatureIterator ite = col.iterator();
        Feature next = ite.next();
        assertEquals("hubert", next.getPropertyValue("name"));
        assertEquals("someone from the \"big fisher\" corp,\na good guy and;\na ;family \"best\" friend", next.getPropertyValue("comment"));
        assertEquals(36, next.getPropertyValue("age"));
        next = ite.next();
        assertEquals("marc", next.getPropertyValue("name"));
        assertEquals("lucky luck", next.getPropertyValue("comment"));
        assertEquals(22, next.getPropertyValue("age"));


    }

    @Test
    public void testWriteEscape() throws Exception{

        final File file = File.createTempFile("test", ".csv");
        file.deleteOnExit();

        final FeatureStore store = new CSVFeatureStore(file, null, ';');

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("comment");
        ftb.addAttribute(Integer.class).setName("age");
        final FeatureType ft = ftb.build();

        store.createFeatureType(ft);

        final Feature f = ft.newInstance();
        f.setPropertyValue("name", "hubert");
        f.setPropertyValue("comment", "someone from the \"big fisher\" corp,\na good guy and\na family \"best\" friend");
        f.setPropertyValue("age", 36);
        store.addFeatures(ft.getName().toString(), Collections.singleton(f));

        String str = IOUtilities.toString(file.toPath());
        assertEquals("name(String);comment(String);age(Integer)\n" +
                    "hubert;\"someone from the \"\"big fisher\"\" corp,\n" +
                    "a good guy and\n" +
                    "a family \"\"best\"\" friend\";36\n", str);


    }

}
