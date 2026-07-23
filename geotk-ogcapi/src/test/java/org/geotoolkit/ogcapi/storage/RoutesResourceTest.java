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
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test Routes API resource implementation.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class RoutesResourceTest {

    /**
     * TODO : need a way to have a local server for tests.
     */
    @Disabled
    @Test
    public static void readTest() throws Exception {

        final Provider p = new Provider();
        final Store store = (Store) p.open(new StorageConnector(new URL("server url")));

        for (Resource r : store.components()) {
            System.out.println(r);

            if (r instanceof RoutesResource routes) {
                for (Resource c : routes.components()) {
                    System.out.println(c.getIdentifier().get());

                    if (c instanceof FeatureSet route) {
                        System.out.println(route.getType());
                        route.features(false).forEach(System.out::println);
                    }
                }
            }
        }

    }

}
