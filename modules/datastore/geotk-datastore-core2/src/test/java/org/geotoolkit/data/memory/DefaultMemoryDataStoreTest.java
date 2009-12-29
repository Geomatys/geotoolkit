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
import org.geotoolkit.data.AbstractDataStoreTests;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultMemoryDataStoreTest extends AbstractDataStoreTests{

    private final MemoryDataStore store = new MemoryDataStore();
    private final Set<Name> names = new HashSet<Name>();
    private final List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();

    public DefaultMemoryDataStoreTest() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        final GeometryFactory gf = new GeometryFactory();
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

        //first schema----------------------------------------------------------
        Name name = new DefaultName("http://test.com", "TestSchema1");
        builder.reset();
        builder.setName(name);
        builder.add("att1", String.class);
        final SimpleFeatureType type1 = builder.buildFeatureType();

        names.add(name);
        expecteds.add(new ExpectedResult(name,type1,0,null));

        store.createSchema(name,type1);

        //second schema --------------------------------------------------------
        name = new DefaultName("http://test.com", "TestSchema2");
        builder.reset();
        builder.setName(name);
        builder.add("string", String.class);
        builder.add("double", Double.class);
        builder.add("date", Date.class);
        final SimpleFeatureType type2 = builder.buildFeatureType();
        store.createSchema(name,type2);

        //create a few features
        FeatureWriter writer = store.getFeatureWriterAppend(name);
        try{
            SimpleFeature f = (SimpleFeature) writer.next();
            f.setAttribute("string", "hop3");
            f.setAttribute("double", 3d);
            f.setAttribute("date", new Date(1000L));
            writer.write();

            f = (SimpleFeature) writer.next();
            f.setAttribute("string", "hop1");
            f.setAttribute("double", 1d);
            f.setAttribute("date", new Date(100000L));
            writer.write();

            f = (SimpleFeature) writer.next();
            f.setAttribute("string", "hop2");
            f.setAttribute("double", 2d);
            f.setAttribute("date", new Date(10000L));
            writer.write();

        }finally{
            writer.close();
        }

        names.add(name);
        expecteds.add(new ExpectedResult(name,type2,3,null));

        //third schema ---------------------------------------------------------
        name = new DefaultName("http://test.com", "TestSchema3");
        builder.reset();
        builder.setName(name);
        builder.add("geometry", Point.class, CRS.decode("EPSG:27582"));
        builder.add("string", String.class);
        final SimpleFeatureType type3 = builder.buildFeatureType();
        store.createSchema(name,type3);

        //create a few features
        writer = store.getFeatureWriterAppend(name);
        try{
            SimpleFeature f = (SimpleFeature) writer.next();
            f.setAttribute("geometry", gf.createPoint(new Coordinate(10, 11)));
            f.setAttribute("string", "hop1");
            writer.write();

            f = (SimpleFeature) writer.next();
            f.setAttribute("geometry", gf.createPoint(new Coordinate(-5, -1)));
            f.setAttribute("string", "hop3");
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
