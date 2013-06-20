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
import java.util.Map;
import java.util.Collections;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.parameter.ParametersExt;
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
import org.junit.Ignore;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MIFFeatureStoreTest {

    public MIFFeatureStoreTest() {
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

    /**
     * Tet store creation.
     * 
     * @throws Exception 
     */
    @Test
    public void testCreate() throws Exception{

        final GeometryFactory GF = new GeometryFactory();
        final File f = File.createTempFile("test", ".mif");
        f.deleteOnExit();

        final FeatureStoreFactory ff = FeatureStoreFinder.getFactoryById("MIF-MID");
        final ParameterValueGroup params = ff.getParametersDescriptor().createValue();
        ParametersExt.getOrCreateValue(params, "url").setValue(f.toURI().toURL());
        
        //create new store from scratch
        final FeatureStore ds = ff.create(params);
        assertNotNull(ds);
        
        //create a feature type
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("integerProp", Integer.class);
        ftb.add("doubleProp", Double.class);
        ftb.add("stringProp", String.class);
        ftb.add("geometryProp", Point.class, DefaultGeographicCRS.WGS84);
        final SimpleFeatureType featureType = ftb.buildSimpleFeatureType();
        
        ds.createFeatureType(featureType.getName(), featureType);
        assertEquals(1, ds.getNames().size());
        
        Name name = ds.getNames().iterator().next();


        for(Name n : ds.getNames()){
            FeatureType ft = ds.getFeatureType(n);
            for(PropertyDescriptor desc : featureType.getDescriptors()){
                PropertyDescriptor td = ft.getDescriptor(desc.getName().getLocalPart());
                assertNotNull(td);
                assertEquals(td.getType().getBinding(), desc.getType().getBinding());
            }
        }

        FeatureWriter fw = ds.getFeatureWriterAppend(name);
        try{
            Feature feature = fw.next();
            feature.getProperty("integerProp").setValue(8);
            feature.getProperty("doubleProp").setValue(3.12);
            feature.getProperty("stringProp").setValue("hello");
            feature.getProperty("geometryProp").setValue(GF.createPoint(new Coordinate(10.3, 15.7)));
            fw.write();
            feature = fw.next();
            feature.getProperty("integerProp").setValue(-15);
            feature.getProperty("doubleProp").setValue(-7.1);
            feature.getProperty("stringProp").setValue("world");
            feature.getProperty("geometryProp").setValue(GF.createPoint(new Coordinate(-1.6, -5.4)));
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

        assertEquals(2, number);


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

        assertEquals(2, number);

    }

}
