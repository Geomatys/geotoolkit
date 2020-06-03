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

import java.net.URISyntaxException;
import java.net.URL;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.geojson.GeoJSONProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GeoJSONProviderTest {

    @Test
    public void readFeatureExtraAttibute2Test() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/VilleMTP_MTP_Enseignements.json");
        GeoJSONProvider provider = new GeoJSONProvider();
        ProbeResult pr = provider.probeContent(new StorageConnector(pointFile));
        Assert.assertTrue(pr.isSupported());
    }
}
