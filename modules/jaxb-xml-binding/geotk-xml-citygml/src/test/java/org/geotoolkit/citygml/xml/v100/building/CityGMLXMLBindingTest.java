/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.citygml.xml.v100.building;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//Junit dependencies
import org.geotoolkit.xml.MarshallerPool;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class CityGMLXMLBindingTest {

    private Unmarshaller   Unmarshaller;
    private Marshaller     Marshaller;
    private MarshallerPool pool;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        pool         = new MarshallerPool(
                "org.geotoolkit.citygml.xml.v100:" +
                "org.geotoolkit.gml.xml.v311:" +
                ":org.geotoolkit.internal.jaxb.geometry:"+
                "org.geotoolkit.citygml.xml.v100.building");
        Unmarshaller = pool.acquireUnmarshaller();
        Marshaller   = pool.acquireMarshaller();
        
    }

    @After
    public void tearDown() throws Exception {
        if (Marshaller != null) {
            pool.release(Marshaller);
        }

        if (Unmarshaller != null) {
            pool.release(Unmarshaller);
        }
    }
    
    /**
     * Test simple Record Marshalling. 
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void marshalingTest() throws Exception {
    }



}
