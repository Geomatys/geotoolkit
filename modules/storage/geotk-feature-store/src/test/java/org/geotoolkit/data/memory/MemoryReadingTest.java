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
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.query.QueryBuilder;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryReadingTest extends AbstractReadingTests{

    private final MemoryFeatureStore store = new MemoryFeatureStore();
    private final Set<GenericName> names = new HashSet<>();
    private final List<ExpectedResult> expecteds = new ArrayList<>();

    public MemoryReadingTest() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        final GeometryFactory gf = new GeometryFactory();
        FeatureTypeBuilder builder = new FeatureTypeBuilder();

        //first schema----------------------------------------------------------
        GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("att1");
        final FeatureType type1 = builder.build();

        names.add(name);
        expecteds.add(new ExpectedResult(name,type1,0,null));

        store.createFeatureType(type1);

        //second schema --------------------------------------------------------
        name = NamesExt.create("http://test.com", "TestSchema2");
        builder = new FeatureTypeBuilder();
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("string");
        builder.addAttribute(Double.class).setName("double");
        builder.addAttribute(Date.class).setName("date");
        final FeatureType type2 = builder.build();
        store.createFeatureType(type2);

        //create a few features
        FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(name.toString(),Filter.EXCLUDE));
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
        name = NamesExt.create("http://test.com", "TestSchema3");
        builder = new FeatureTypeBuilder();
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Point.class).setName("geometry").setCRS(CRS.forCode("EPSG:27582")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        builder.addAttribute(String.class).setName("string");
        final FeatureType type3 = builder.build();
        store.createFeatureType(type3);

        //create a few features
        writer = store.getFeatureWriter(QueryBuilder.filtered(name.toString(),Filter.EXCLUDE));
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

        GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:27582"));
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
