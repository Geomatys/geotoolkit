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
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.storage.DataStoreException;
import static org.junit.Assert.assertNotNull;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel
 * @module
 */
public class DBFReadingTest extends AbstractReadingTests{

    private final DbaseFileFeatureStore store;
    private final Set<GenericName> names = new HashSet<>();
    private final List<ExpectedResult> expecteds = new ArrayList<>();

    public DBFReadingTest() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, IOException{

        final File file = new File("src/test/resources/org/geotoolkit/data/dbf/sample.dbf");
        store = new DbaseFileFeatureStore(file.toPath());

        for(GenericName n : store.getNames()){
            FeatureType ft = store.getFeatureType(n.toString());
            assertNotNull(ft);
        }

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        final GenericName name = NamesExt.create("sample");
        builder.setName(name);
        builder.addAttribute(Double.class).setName(NamesExt.create("N1")).setMaximalLength(5);
        builder.addAttribute(Double.class).setName(NamesExt.create("N2")).setMaximalLength(5);
        builder.addAttribute(String.class).setName(NamesExt.create("N3")).setMaximalLength(6);
        final FeatureType type3 = builder.build();

        names.add(name);
        expecteds.add(new ExpectedResult(name,type3,3,null));
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
