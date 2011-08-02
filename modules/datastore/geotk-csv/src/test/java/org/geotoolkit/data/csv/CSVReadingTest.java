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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVReadingTest extends AbstractReadingTests{

    private final CSVDataStore store;
    private final Set<Name> names = new HashSet<Name>();
    private final List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();

    public CSVReadingTest() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, IOException{

        final File file = File.createTempFile("temp2", "csv");
        file.deleteOnExit();
        store = new CSVDataStore(file, "http://test.com", "csvstore", ';');

        final GeometryFactory gf = new GeometryFactory();
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        final String namespace = "http://test.com";
        
        Name name = new DefaultName("http://test.com", "TestSchema3");
        builder.reset();
        builder.setName(name);
        builder.add(new DefaultName(namespace,"geometry"), Geometry.class, CRS.decode("EPSG:27582"));
        builder.add(new DefaultName(namespace,"stringProp"), String.class);
        builder.add(new DefaultName(namespace,"intProp"), Integer.class);
        builder.add(new DefaultName(namespace,"doubleProp"), Double.class);
        final SimpleFeatureType type3 = builder.buildSimpleFeatureType();
        store.createSchema(name,type3);

        //create a few features
        FeatureWriter writer = store.getFeatureWriterAppend(name);
        try{
            SimpleFeature f = (SimpleFeature) writer.next();
            f.setAttribute("geometry", gf.createPoint(new Coordinate(10, 11)));
            f.setAttribute("stringProp", "hop1");
            f.setAttribute("intProp", 15);
            f.setAttribute("doubleProp", 32.2);
            writer.write();

            f = (SimpleFeature) writer.next();
            f.setAttribute("geometry", gf.createPoint(new Coordinate(-5, -1)));
            f.setAttribute("stringProp", "hop3");
            f.setAttribute("intProp", 18);
            f.setAttribute("doubleProp", 412.10);
            writer.write();


        }finally{
            writer.close();
        }

        GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:27582"));
        env.setRange(0, -5, 10);
        env.setRange(1, -1, 11);
        
        names.add(name);
        expecteds.add(new ExpectedResult(name,type3,2,env));
    }

    @Override
    protected synchronized DataStore getDataStore() {
        return store;
    }

    @Override
    protected Set<Name> getExpectedNames() {
        return names;
    }

    @Override
    protected List<ExpectedResult> getReaderTests() {
        return expecteds;
    }

}
