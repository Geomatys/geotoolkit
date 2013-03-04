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

import org.apache.sis.metadata.KeyNamePolicy;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Locale;

import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.acquisition.EnvironmentalRecord;

import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link DescriptionMap}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
@Depend(MetadataStandard.class)
public final strictfp class DescriptionMapTest {
    /**
     * Tests descriptions in the English locale.
     */
    @Test
    public void testEnglish() {
        final Map<String,String> descriptions = MetadataStandard.ISO_19115.asDescriptionMap(
                EnvironmentalRecord.class, Locale.ENGLISH, KeyNamePolicy.UML_IDENTIFIER);
        /*
         * Compares a few specific resources.
         */
        assertEquals("Mismatch in class description.",
                "Information about the environmental conditions during the acquisition.",
                descriptions.get("class"));
        assertEquals("Mismatch in the description of an attribute.",
                "Average air temperature along the flight pass during the photo flight.",
                descriptions.get("averageAirTemperature"));
        /*
         * Compares the key set.
         */
        final Set<String> keys = descriptions.keySet();
        final Set<String> expected = new HashSet<String>(Arrays.asList(new String[] {
            "class", "averageAirTemperature", "maxAltitude", "maxRelativeHumidity", "meteorologicalConditions"
        }));
        assertFalse(keys.isEmpty());
        assertEquals(expected, keys);
        assertEquals("The first key should be for the class description.", "class", keys.iterator().next());
    }

    /**
     * Tests descriptions inherited from a super-class.
     */
    @Test
    public void testInheritance() {
        final Map<String,String> descriptions = MetadataStandard.ISO_19115.asDescriptionMap(
                ImageDescription.class, Locale.ENGLISH, KeyNamePolicy.UML_IDENTIFIER);
        /*
         * Compares the description of an attribute declared directly in ImageDescription.
         */
        assertEquals("Mismatch in class description.",
                "Information about an image's suitability for use.",
                descriptions.get("class"));
        assertEquals("Mismatch in the description of an attribute.",
                "Area of the dataset obscured by clouds, expressed as a percentage of the spatial extent.",
                descriptions.get("cloudCoverPercentage"));
        /*
         * The attribute below is inherited from the CoverageDescription super-class.
         */
        assertEquals("Mismatch in the description of an attribute.",
                "Type of information represented by the cell value.",
                descriptions.get("contentType"));
    }
}
