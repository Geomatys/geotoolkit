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
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.mapinfo.mif.MIFFeatureStoreFactory;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.apache.sis.referencing.CommonCRS;
import org.junit.After;
import org.junit.Test;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.DataStores;
import static org.junit.Assert.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MIFFeatureStoreTest extends org.geotoolkit.test.TestBase {

    private final File tempDir;

    public MIFFeatureStoreTest() throws IOException {
        tempDir = Files.createTempDirectory("mifMidTests").toFile();
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
     * Test store creation.
     *
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception{

        final GeometryFactory GF = new GeometryFactory();
        final File f = File.createTempFile("test", ".mif", tempDir);

        final FeatureStoreFactory ff = (FeatureStoreFactory) DataStores.getFactoryById("MIF-MID");
        final ParameterValueGroup params = ff.getParametersDescriptor().createValue();
        ParametersExt.getOrCreateValue(params, MIFFeatureStoreFactory.PATH.getName().getCode()).setValue(f.toURI());

        //create new store from scratch
        final FeatureStore ds = (FeatureStore) ff.create(params);
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
        assertEquals(1, ds.getNames().size());

        GenericName name = ds.getNames().iterator().next();


        for(GenericName n : ds.getNames()){
            FeatureType ft = ds.getFeatureType(n.toString());
            for(PropertyType desc : featureType.getProperties(true)){
                PropertyType td = ft.getProperty(desc.getName().tip().toString());
                assertNotNull(td);
                assertEquals(((AttributeType)td).getValueClass(), ((AttributeType)desc).getValueClass());
            }
        }

        try (final FeatureWriter fw = ds.getFeatureWriter(QueryBuilder.filtered(name.toString(),Filter.EXCLUDE))) {
            Feature feature = fw.next();
            feature.setPropertyValue("integerProp",8);
            feature.setPropertyValue("doubleProp",3.12);
            feature.setPropertyValue("stringProp","hello");
            feature.setPropertyValue("geometryProp",GF.createPoint(new Coordinate(10.3, 15.7)));
            fw.write();
            feature = fw.next();
            feature.setPropertyValue("integerProp",-15);
            feature.setPropertyValue("doubleProp",-7.1);
            feature.setPropertyValue("stringProp","world");
            feature.setPropertyValue("geometryProp",GF.createPoint(new Coordinate(-1.6, -5.4)));
            fw.write();
        }

        int number = 0;
        try (final FeatureReader reader = ds.getFeatureReader(QueryBuilder.all(name.toString()))) {
            while(reader.hasNext()){
                number++;
                reader.next();
            }
        }

        assertEquals(2, number);

        //test with hint
        QueryBuilder qb = new QueryBuilder(name.toString());
        qb.setHints(new Hints(HintsPending.FEATURE_DETACHED, Boolean.FALSE));
        number = 0;
        try (final FeatureReader reader = ds.getFeatureReader(qb.buildQuery())) {
            while(reader.hasNext()){
                number++;
                reader.next();
            }
        }

        assertEquals(2, number);
    }
}
