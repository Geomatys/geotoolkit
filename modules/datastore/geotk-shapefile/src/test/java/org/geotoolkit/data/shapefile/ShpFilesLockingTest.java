/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import org.junit.After;
import org.junit.Before;


public class ShpFilesLockingTest {

    @Before
    public void setUp() throws Exception {
        getClass().getClassLoader().setDefaultAssertionStatus(true);        
    }
    
    @After
    public void tearDown() throws Exception {
        Runtime.getRuntime().runFinalization();
    }

    //TODO : rewrite this test with the new lock model

}
