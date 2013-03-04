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

import org.apache.sis.metadata.NullValuePolicy;
import org.apache.sis.metadata.KeyNamePolicy;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.opengis.annotation.Obligation;
import org.opengis.metadata.acquisition.EnvironmentalRecord;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.metadata.iso.content.DefaultBand;
import org.geotoolkit.metadata.iso.acquisition.DefaultEnvironmentalRecord;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link RestrictionMap}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
public final strictfp class RestrictionMapTest {
    /**
     * Tests a simple case with no mandatory property.
     * Only a range of value restriction is expected.
     */
    @Test
    public void testValueRange() {
        final DefaultBand band = new DefaultBand();
        final Map<String,ValueRestriction> map = band.getStandard().asRestrictionMap(
                band, NullValuePolicy.NON_NULL, KeyNamePolicy.JAVABEANS_PROPERTY);
        /*
         * Test a record that doesn't violate any restriction.
         */
        assertTrue("Band has no mandatory property, so an empty Band " +
                   "should not violate any restriction.", map.isEmpty());
        band.setScaleFactor(1.0);
        band.setBitsPerValue(8);
        assertTrue("A band constructed with valid values should " +
                   "not violate any restriction.", map.isEmpty());
        /*
         * Now violate a restriction.
         */
        band.setBitsPerValue(0);
        assertEquals("The band should be recognized as invalid.", 1, map.size());
        Iterator<Map.Entry<String,ValueRestriction>> it = map.entrySet().iterator();
        assertTrue("Expected exactly one violated restriction.", it.hasNext());
        Map.Entry<String,ValueRestriction> entry = it.next();
        assertEquals("bitsPerValue", entry.getKey());
        ValueRestriction restriction = entry.getValue();
        assertNull("The band is not expected to violate the obligation.", restriction.obligation);
        assertEquals(new NumberRange<Integer>(Integer.class, 1, null), restriction.range);
        assertFalse("Expected exactly one violated restriction.", it.hasNext());
        /*
         * Reset the record to a valid value.
         */
        band.setBitsPerValue(null);
        assertTrue(map.isEmpty());
    }

    /**
     * Tests a simple case with mandatory elements and a range of values.
     */
    @Test
    public void testObligation() {
        final DefaultEnvironmentalRecord record = new DefaultEnvironmentalRecord();
        final Map<String,ValueRestriction> map = record.getStandard().asRestrictionMap(
                record, NullValuePolicy.NON_NULL, KeyNamePolicy.JAVABEANS_PROPERTY);
        /*
         * Test a record with 2 values specified, and 2 mandatory values left missing.
         */
        record.setAverageAirTemperature(20.0);
        record.setMaxRelativeHumidity(50.0);
        assertEquals("Missing mandatory values should be reported as violations.", 2, map.size());
        final ValueRestriction mandatory = ValueRestriction.create(Obligation.MANDATORY, null, null);
        final Map<String,ValueRestriction> expected = new HashMap<String,ValueRestriction>();
        assertNull(expected.put("meteorologicalConditions", mandatory));
        assertNull(expected.put("maxAltitude", mandatory));
        assertEquals(expected, map);
        /*
         * Now violate a ValueRange restriction.
         */
        record.setMaxRelativeHumidity(-5.0);
        assertEquals("Value out of range should be reported.", 3, map.size());
        final ValueRestriction range = ValueRestriction.create(null, NumberRange.create(0.0, 100.0), null);
        assertNull(expected.put("maxRelativeHumidity", range));
        assertEquals(expected, map);
        /*
         * Tests the "get" method.
         */
        assertSame(mandatory, map.get("maxAltitude"));
        assertSame(range, map.get("maxRelativeHumidity"));
        assertNull(map.get("averageAirTemperature"));
        /*
         * Remove the "out of range" violation.
         */
        record.setMaxRelativeHumidity(null);
        assertSame(range, expected.put("maxRelativeHumidity", mandatory));
        assertEquals(expected, map);
    }

    /**
     * Tests the restrictions reported for a {@link Class} argument.
     */
    @Test
    public void testClass() {
        /*
         * Creates the map of expected values.
         */
        final Map<String,ValueRestriction> expected = new HashMap<String,ValueRestriction>();
        assertNull(expected.put("averageAirTemperature",
                ValueRestriction.create(Obligation.MANDATORY, null, null)));
        assertNull(expected.put("maxRelativeHumidity",
                ValueRestriction.create(Obligation.MANDATORY, NumberRange.create(0.0, 100.0), null)));
        assertNull(expected.put("maxAltitude",
                ValueRestriction.create(Obligation.MANDATORY, null, null)));
        assertNull(expected.put("meteorologicalConditions",
                ValueRestriction.create(Obligation.MANDATORY, null, null)));
        /*
         * Compares with the map calculated.
         */
        final Map<String,ValueRestriction> map = MetadataStandard.ISO_19115.asRestrictionMap(
                EnvironmentalRecord.class, NullValuePolicy.NON_NULL, KeyNamePolicy.JAVABEANS_PROPERTY);
        assertEquals(expected, map);
    }
}
