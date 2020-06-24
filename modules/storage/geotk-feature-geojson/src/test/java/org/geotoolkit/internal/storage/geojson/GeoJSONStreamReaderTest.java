/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.storage.geojson;

import java.io.IOException;
import java.io.InputStream;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.geojson.GeoJSONStreamReader;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeoJSONStreamReaderTest {

    @Test
    public void readTest() throws IOException, DataStoreException {

        InputStream file = GeoJSONReadTest.class.getResourceAsStream("/org/apache/sis/internal/storage/geojson/featurecollection.json");

        GeoJSONStreamReader reader = new GeoJSONStreamReader(file, null, "test", null);

        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertTrue(reader.hasNext()); Assert.assertNotNull(reader.next());
        Assert.assertFalse(reader.hasNext());
    }

}
