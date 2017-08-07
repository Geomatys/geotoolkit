/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.util.collection;

import java.util.Map;
import org.apache.sis.util.NullArgumentException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class MapUtilitiesTest extends org.geotoolkit.test.TestBase {

    public MapUtilitiesTest() {
    }

    @Test
    public void testBuildMap() {
        final Integer[] testInt = new Integer[1000];
        for (int i = 0; i < testInt.length; i++) {
            testInt[i] = i;
        }

        final String[] testString = new String[100];
        for (int i = 0; i < testString.length;) {
            testString[i] = "key" + i++;
            testString[i] = "value" + i++;
        }

        final Object[] testBadNumber = new Object[51];

        try {
            MapUtilities.buildMap((Object[])null);
            fail("Should have raised an exception");
        } catch (NullArgumentException e) {
        }

        try {
            MapUtilities.buildMap(testBadNumber);
            fail("Should have raised an exception");
        } catch (IllegalArgumentException e) {
        }

        final Map resInt = MapUtilities.buildMap(testInt);

        final Map resString = MapUtilities.buildMap(testString);

        assertNotNull("null result", testInt);
        assertNotNull("null result", testString);
        assertEquals("Wrong size for result map", resInt.size(), testInt.length / 2);
        assertEquals("Wrong size for result map", resString.size(), testString.length / 2);

        for (int i = 0; i < testInt.length;) {
            assertTrue("Expected key not found in result map", resInt.containsKey(i++));
            assertTrue("Expected key not found in result map", resInt.containsValue(i++));
        }

        for (int i = 0; i < testString.length;) {
            assertTrue("Expected key not found in result map : iteration " + i, resString.containsKey("key" + i++));
            assertTrue("Expected value not found in result map : iteration " + i, resString.containsValue("value" + i++));
        }
    }
}
