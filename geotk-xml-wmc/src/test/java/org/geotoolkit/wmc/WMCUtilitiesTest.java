/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.wmc;

import org.apache.sis.portrayal.MapLayers;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WMCUtilitiesTest {

    public WMCUtilitiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getMapContext method, of class WMCUtilities.
     */
    @Test
    public void testGetMapContext() throws Exception {
        MapLayers context = WMCUtilities.getMapContext(this.getClass().getResourceAsStream("testWMC_wms.xml"));
        assertNotNull(context);
    }
}
