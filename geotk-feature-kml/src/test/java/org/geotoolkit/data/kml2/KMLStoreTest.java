/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.data.kml2;

import java.net.URL;
import java.util.Iterator;
import java.util.stream.Stream;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import static org.geotoolkit.data.kml2.KMLStore.PLACEMARK_NAME;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLStoreTest {

    @Test
    public void readPlacemarkTest() throws Exception {

        final URL path = KMLStoreTest.class.getResource("/org/geotoolkit/data/kml/placemark.kml");
        final KMLStore store = new KMLStore(path.toURI());

        final FeatureType type = store.getType();
        assertEquals(PLACEMARK_NAME, type.getName().toString());


        try (Stream<Feature> stream = store.features(false)) {
            final Iterator<Feature> ite = stream.iterator();
            assertTrue(ite.hasNext());
            final Feature feature = ite.next();
            assertEquals("Google Earth - New Placemark", feature.getPropertyValue("name"));
            assertEquals("Some Descriptive text.", feature.getPropertyValue("description"));
            assertFalse(ite.hasNext());
        }
    }
}
