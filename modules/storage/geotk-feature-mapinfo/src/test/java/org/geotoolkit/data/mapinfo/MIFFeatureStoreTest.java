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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
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
import org.apache.sis.referencing.CommonCRS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MIFFeatureStoreTest {

    private final File tempDir;

    public MIFFeatureStoreTest() throws IOException {
        tempDir = Files.createTempDirectory("mifMidTests").toFile();
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
    public void tearDown() throws IOException {
        Files.walkFileTree(tempDir.toPath(), new SimpleFileVisitor<Path>() {
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
     * Tet store creation.
     * 
     * @throws Exception 
     */
    @Test
    public void testCreate() throws Exception{

        final GeometryFactory GF = new GeometryFactory();
        final File f = File.createTempFile("test", ".mif", tempDir);

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
        ftb.add("geometryProp", Point.class, CommonCRS.WGS84.normalizedGeographic());
        final FeatureType featureType = ftb.buildFeatureType();
        
        ds.createFeatureType(featureType.getName(), featureType);
        assertEquals(1, ds.getNames().size());
        
        GenericName name = ds.getNames().iterator().next();


        for(GenericName n : ds.getNames()){
            FeatureType ft = ds.getFeatureType(n);
            for(PropertyDescriptor desc : featureType.getDescriptors()){
                PropertyDescriptor td = ft.getDescriptor(desc.getName().tip().toString());
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
