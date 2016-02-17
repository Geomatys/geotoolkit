/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.data.shapefile.cpg;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.feature.Feature;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CPGFileTest extends org.geotoolkit.test.TestBase {

    /**
     * Test reading a shapefile with an UTF-8 cpg file.
     *
     * @throws DataStoreException
     */
    @Test
    public void testReadUTF8() throws DataStoreException, MalformedURLException{

        final URL url = CPGFileTest.class.getResource("/org/geotoolkit/test-data/shapes/utf8.shp");
        final ShapefileFeatureStore store = new ShapefileFeatureStore(url);

        try(final FeatureReader reader = store.getFeatureReader(QueryBuilder.all(store.getName()))){
            Assert.assertTrue(reader.hasNext());
            final Feature feature = reader.next();
            Assert.assertEquals("&éè\"'(-_çà)=@%$*:test",feature.getProperty("text").getValue());
        }

    }

}
