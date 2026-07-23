/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.net.URL;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.opengis.feature.Feature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test OGC API - Records {@code /collections/{catalogId}/items} resource implementation.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class RecordResourceTest {

    /**
     * TODO : need a way to have a local server for tests.
     *
     */
    @Disabled
    @Test
    public static void readTest() throws Exception {

        final Provider p = new Provider();
        final Store store = (Store) p.open(new StorageConnector(new URL("server url")));

        for (Resource r : store.components()) {
            if (r instanceof Aggregate agg) {
                for (Resource c : agg.components()) {
                    if (c instanceof RecordResource rs) {
                        System.out.println(rs.getIdentifier().get());
                        System.out.println(rs.getType());
                        try (var stream = rs.features(false)) {
                            for (Feature f : stream.limit(5).toList()) {
                                System.out.println(f);
                            }
                        }
                    }
                }
            }
        }

    }

}