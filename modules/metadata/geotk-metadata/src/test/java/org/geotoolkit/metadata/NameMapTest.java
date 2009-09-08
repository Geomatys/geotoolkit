/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.HashMap;

import org.opengis.metadata.acquisition.EnvironmentalRecord;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link NameMap}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
public final class NameMapTest {
    /**
     * Tests the formatting of sentences.
     */
    @Test
    public void testSentence() {
        final Map<String,String> map, expected = new HashMap<String,String>();
        map = MetadataStandard.ISO_19115.asNameMap(EnvironmentalRecord.class,
                KeyNamePolicy.SENTENCE, KeyNamePolicy.JAVABEANS_PROPERTY);
        assertNull(expected.put("averageAirTemperature",    "Average air temperature"));
        assertNull(expected.put("maxAltitude",              "Max altitude"));
        assertNull(expected.put("maxRelativeHumidity",      "Max relative humidity"));
        assertNull(expected.put("meteorologicalConditions", "Meteorological conditions"));
        assertEquals(expected, map);
    }
}
