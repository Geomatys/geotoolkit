/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;
import static org.apache.sis.metadata.KeyNamePolicy.*;


/**
 * Tests {@link NameMap}.
 *
 * @todo Apparently, String literals in annotation are not interned. We should check if this
 *       behavior changes in a future JDK version, and if so enable the commented-out test.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
@Depend(MetadataStandard.class)
public final strictfp class NameMapTest {
    /**
     * Tests the formatting of sentences.
     */
    @Test
    public void testSentence() {
        final Map<String,String> map, expected = new HashMap<String,String>();
        map = MetadataStandard.ISO_19115.asNameMap(EnvironmentalRecord.class, SENTENCE, JAVABEANS_PROPERTY);
        assertNull(expected.put("averageAirTemperature",    "Average air temperature"));
        assertNull(expected.put("maxAltitude",              "Max altitude"));
        assertNull(expected.put("maxRelativeHumidity",      "Max relative humidity"));
        assertNull(expected.put("meteorologicalConditions", "Meteorological conditions"));
        assertEquals(expected, map);
    }

    /**
     * Ensures that the string are interned. Note that the library will not break if strings
     * are not interned; it would just consume more memory than needed. We want to intern those
     * strings because they usually match method names or field names, which are already interned
     * by the JVM.
     *
     * @see String#intern()
     */
    @Test
    public void testIntern() {
        String name;
        Map<String,String> map;
        /*
         * Tests explicit intern.
         */
        map = MetadataStandard.ISO_19115.asNameMap(EnvironmentalRecord.class, JAVABEANS_PROPERTY, SENTENCE);
        name = map.get("Average air temperature");
        assertEquals("averageAirTemperature", name);
        assertSame  ("averageAirTemperature", name);
        /*
         * Tests implicit intern.
         */
        map = MetadataStandard.ISO_19115.asNameMap(EnvironmentalRecord.class, METHOD_NAME, SENTENCE);
        name = map.get("Average air temperature");
        assertEquals("getAverageAirTemperature", name);
        assertSame  ("getAverageAirTemperature", name);
        /*
         * Tests an other implicit intern.
         */
        map = MetadataStandard.ISO_19115.asNameMap(EnvironmentalRecord.class, UML_IDENTIFIER, SENTENCE);
        name = map.get("Average air temperature");
        assertEquals("averageAirTemperature", name);
//      assertSame  ("averageAirTemperature", name);
//      TODO: Apprently, annotation literals are not intern as of Java 1.6.0_13
    }
}
