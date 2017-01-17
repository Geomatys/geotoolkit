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
import java.nio.file.Paths;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLFeatureStoreTest {
    
    @Test
    public void readPlacemarkTest() throws Exception {
        
        final URL path = KMLFeatureStoreTest.class.getResource("/org/geotoolkit/data/kml/placemark.kml");
        final KMLFeatureStore store = new KMLFeatureStore(Paths.get(path.toURI()));
        
        assertEquals(1,store.getNames().size());
        assertEquals("Placemark",store.getNames().iterator().next().tip().toString());
        
        final FeatureType type = store.getFeatureType(store.getNames().iterator().next());
        assertNotNull(type);
        
        final FeatureReader reader = store.getFeatureReader(QueryBuilder.all(NamesExt.create("Placemark")));
        assertTrue(reader.hasNext());
        final Feature feature = reader.next();
        assertEquals("Google Earth - New Placemark", feature.getProperty("name").getValue());
        assertEquals("Some Descriptive text.", feature.getProperty("description").getValue());
        assertFalse(reader.hasNext());
        
    }
    
    
}
