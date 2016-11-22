/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.filter.binarycomparison;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.filter.DefaultPropertyName;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.MatchAction;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MatchActionTest {

    @Test
    public void matchAllTest() {

        final Map<String,Object> candidate = new HashMap<>();
        candidate.put("name", Arrays.asList("da vinci","polo","galileo"));

        final DefaultPropertyIsEqualTo filter = new DefaultPropertyIsEqualTo(
                new DefaultPropertyName("name"),
                new DefaultLiteral("copernic"),
                true, MatchAction.ALL);

        assertFalse(filter.evaluate(candidate));

        candidate.put("name", Arrays.asList("da vinci","copernic","galileo"));
        assertFalse(filter.evaluate(candidate));

        candidate.put("name", Arrays.asList("copernic","copernic","copernic"));
        assertTrue(filter.evaluate(candidate));
    }

    @Test
    public void matchAnyTest() {

        final Map<String,Object> candidate = new HashMap<>();
        candidate.put("name", Arrays.asList("da vinci","polo","galileo"));

        final DefaultPropertyIsEqualTo filter = new DefaultPropertyIsEqualTo(
                new DefaultPropertyName("name"),
                new DefaultLiteral("copernic"),
                true, MatchAction.ANY);

        assertFalse(filter.evaluate(candidate));

        candidate.put("name", Arrays.asList("copernic","copernic","copernic"));
        assertTrue(filter.evaluate(candidate));

        candidate.put("name", Arrays.asList("da vinci","copernic","galileo"));
        assertTrue(filter.evaluate(candidate));
    }

    @Test
    public void matchOneTest() {

        final Map<String,Object> candidate = new HashMap<>();
        candidate.put("name", Arrays.asList("da vinci","polo","galileo"));

        final DefaultPropertyIsEqualTo filter = new DefaultPropertyIsEqualTo(
                new DefaultPropertyName("name"),
                new DefaultLiteral("copernic"),
                true, MatchAction.ONE);

        assertFalse(filter.evaluate(candidate));

        candidate.put("name", Arrays.asList("copernic","copernic","copernic"));
        assertFalse(filter.evaluate(candidate));

        candidate.put("name", Arrays.asList("da vinci","copernic","galileo"));
        assertTrue(filter.evaluate(candidate));
    }

}
