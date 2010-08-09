/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.data.dbf;


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

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class DBFReadingTest extends AbstractReadingTests{

    private final DbaseFileDataStore store;
    private final Set<Name> names = new HashSet<Name>();
    private final List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();

    public DBFReadingTest() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, IOException{

        final File file = new File("src/test/resources/org/geotoolkit/data/dbf/sample.dbf");
        final String ns = "http://test.com";
        store = new DbaseFileDataStore(file, ns, "dbfstore");

        for(Name n : store.getNames()){
            FeatureType ft = store.getFeatureType(n);
            assertNotNull(ft);
        }

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        
        Name name = new DefaultName("http://test.com", "dbfstore");
        builder.reset();
        builder.setName(name);
        builder.add(new DefaultName(ns, "N1"), Double.class);
        builder.add(new DefaultName(ns, "N2"), Double.class);
        builder.add(new DefaultName(ns, "N3"), String.class);
        final SimpleFeatureType type3 = builder.buildSimpleFeatureType();
        
        names.add(name);
        expecteds.add(new ExpectedResult(name,type3,3,null));
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
