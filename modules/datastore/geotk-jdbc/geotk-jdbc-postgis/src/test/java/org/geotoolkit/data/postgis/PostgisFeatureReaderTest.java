/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.postgis;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.jdbc.JDBCFeatureReaderTest;
import org.geotoolkit.jdbc.JDBCTestSetup;
import org.opengis.feature.simple.SimpleFeatureType;


public class PostgisFeatureReaderTest extends JDBCFeatureReaderTest {

    @Override
    protected JDBCTestSetup createTestSetup() {
        return new PostGISTestSetup();
    }

    public void testRefresh() throws Exception {
//        int sizeDatastore1 = dataStore.getNames().size();
//        System.out.println("###################size1 "+sizeDatastore1+"###########");
//        int sizeDatastore2 = dataStore2.getNames().size();
//        System.out.println("###################size2 "+sizeDatastore2+"###########");
        assertEquals(featureStore.getNames().size(), featureStore2.getNames().size());
        
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        DefaultName name3 = new DefaultName("http://www.geotoolkit.org/test", "Type3");
        builder.reset();
        builder.setName(name3);
        builder.add(new DefaultName("http://type3.com", "att1"), String.class);
        builder.add(new DefaultName("http://type3.com", "att2"), Integer.class);
        final SimpleFeatureType sft1 = builder.buildSimpleFeatureType();
        featureStore.createSchema(name3,sft1);
        assertFalse(featureStore.getNames().size()==featureStore2.getNames().size());
//        sizeDatastore1 = dataStore.getNames().size();
//        System.out.println("###################size1 "+sizeDatastore1+"###########");
//        sizeDatastore2 = dataStore2.getNames().size();
//        System.out.println("###################size2 "+sizeDatastore2+"###########");
        featureStore2.refreshMetaModel();
        assertEquals(featureStore.getNames().size(), featureStore2.getNames().size());
        featureStore.deleteSchema(name3);
    }

}
