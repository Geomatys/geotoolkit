/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.data.kml;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author samuel
 */
public class BooleanTest {

    public BooleanTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testBoolean() {

        assertTrue(parseBoolean("true"));
        assertTrue(parseBoolean("1"));
        assertFalse(parseBoolean("false"));
        assertFalse(parseBoolean("0"));

    }

    protected static boolean parseBoolean(String candidate) {
        if (candidate.length() == 1){
             return !candidate.equals("0");
        }
        return Boolean.parseBoolean(candidate);
    }
}
