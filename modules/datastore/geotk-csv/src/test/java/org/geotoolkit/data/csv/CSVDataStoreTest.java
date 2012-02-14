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

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVDataStoreTest {

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

        final DataStore ds = DataStoreFinder.get(AbstractFileDataStoreFactory.URLP.getName().getCode(),
                f.toURL());
        assertNotNull(ds);
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("integerProp", Integer.class);
        ftb.add("doubleProp", Double.class);
        ftb.add("stringProp", String.class);
        ftb.add("geometryProp", Geometry.class, DefaultGeographicCRS.WGS84);
        SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        ds.createSchema(sft.getName(), sft);
        Name name = ds.getNames().iterator().next();

        assertEquals(1, ds.getNames().size());

        for(Name n : ds.getNames()){
            FeatureType ft = ds.getFeatureType(n);
            for(PropertyDescriptor desc : sft.getDescriptors()){
                PropertyDescriptor td = ft.getDescriptor(desc.getName().getLocalPart());
                assertNotNull(td);
                assertEquals(td.getType().getBinding(), desc.getType().getBinding());
            }
        }

        FeatureWriter fw = ds.getFeatureWriterAppend(name);
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

        FeatureReader reader = ds.getFeatureReader(QueryBuilder.all(name));
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
        QueryBuilder qb = new QueryBuilder(name);
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

}
