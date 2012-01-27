/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.osmtms;

import java.util.Iterator;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author jsorel
 */
public class OSMTMSCoverageStoreFactoryTest {
    
    public OSMTMSCoverageStoreFactoryTest() {
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
    public void testFactory() {
        
        
        final Iterator<CoverageStoreFactory> ite = CoverageStoreFinder.getAllCoverageStores();
        
        boolean found = false;
        while (ite.hasNext()){
            if(ite.next() instanceof OSMTMSCoverageStoreFactory){
                found = true;
            }
        }
        
        if(!found){
            fail("Factory not found");
        }
    }
}
