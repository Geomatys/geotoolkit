/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.wmc;

import org.geotoolkit.map.MapContext;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WMCUtilitiesTest extends org.geotoolkit.test.TestBase {

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
        MapContext context = WMCUtilities.getMapContext(this.getClass().getResourceAsStream("testWMC_wms.xml"));
        assertNotNull(context);
    }
}
