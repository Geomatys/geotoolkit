/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.memory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.referencing.CRS;
import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryReadingTest extends AbstractReadingTests{

    private final MemoryFeatureStore store = new MemoryFeatureStore();
    private final Set<GenericName> names = new HashSet<GenericName>();
    private final List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();

    public MemoryReadingTest() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        final GeometryFactory gf = new GeometryFactory();
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        //first schema----------------------------------------------------------
        GenericName name = DefaultName.create("http://test.com", "TestSchema1");
        builder.reset();
        builder.setName(name);
        builder.add("att1", String.class);
        final FeatureType type1 = builder.buildSimpleFeatureType();

        names.add(name);
        expecteds.add(new ExpectedResult(name,type1,0,null));

        store.createFeatureType(name,type1);

        //second schema --------------------------------------------------------
        name = DefaultName.create("http://test.com", "TestSchema2");
        builder.reset();
        builder.setName(name);
        builder.add("string", String.class);
        builder.add("double", Double.class);
        builder.add("date", Date.class);
        final FeatureType type2 = builder.buildSimpleFeatureType();
        store.createFeatureType(name,type2);

        //create a few features
        FeatureWriter writer = store.getFeatureWriterAppend(name);
        try{
            Feature f = writer.next();
            f.setPropertyValue("string", "hop3");
            f.setPropertyValue("double", 3d);
            f.setPropertyValue("date", new Date(1000L));
            writer.write();

            f = writer.next();
            f.setPropertyValue("string", "hop1");
            f.setPropertyValue("double", 1d);
            f.setPropertyValue("date", new Date(100000L));
            writer.write();

            f = writer.next();
            f.setPropertyValue("string", "hop2");
            f.setPropertyValue("double", 2d);
            f.setPropertyValue("date", new Date(10000L));
            writer.write();

        }finally{
            writer.close();
        }

        names.add(name);
        expecteds.add(new ExpectedResult(name,type2,3,null));

        //third schema ---------------------------------------------------------
        name = DefaultName.create("http://test.com", "TestSchema3");
        builder.reset();
        builder.setName(name);
        builder.add("geometry", Point.class, CRS.decode("EPSG:27582"));
        builder.add("string", String.class);
        final FeatureType type3 = builder.buildSimpleFeatureType();
        store.createFeatureType(name,type3);

        //create a few features
        writer = store.getFeatureWriterAppend(name);
        try{
            Feature f = writer.next();
            f.setPropertyValue("geometry", gf.createPoint(new Coordinate(10, 11)));
            f.setPropertyValue("string", "hop1");
            writer.write();

            f = writer.next();
            f.setPropertyValue("geometry", gf.createPoint(new Coordinate(-5, -1)));
            f.setPropertyValue("string", "hop3");
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
    protected synchronized FeatureStore getDataStore() {
        return store;
    }

    @Override
    protected Set<GenericName> getExpectedNames() {
        return names;
    }

    @Override
    protected List<ExpectedResult> getReaderTests() {
        return expecteds;
    }

}
