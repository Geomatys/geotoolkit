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
package org.geotoolkit.csw.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXBContextTest {

    @Before
    public void setUp() throws JAXBException {

    }

    @After
    public void tearDown() {

    }

    /**
     * Test context creation.
     */
    @Test
    public void contextTest() {

        try {
            JAXBContext ctx = JAXBContext.newInstance("org.geotoolkit.csw.xml.v202:org.apache.sis.internal.jaxb.geometry");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("error while creating CSW context:" + ex.getMessage());
        }

        try {
            JAXBContext ctx = JAXBContext.newInstance(org.geotoolkit.csw.xml.v202.ObjectFactory.class,
                                                      org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("error while creating CSW context:" + ex.getMessage());
        }

        try {
            JAXBContext ctx = JAXBContext.newInstance(org.geotoolkit.csw.xml.v202.LimitedObjectFactory.class,
                                                      org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("error while creating CSW context:" + ex.getMessage());
        }

        // why not failing ?
        try {
            JAXBContext ctx = JAXBContext.newInstance(org.geotoolkit.csw.xml.v202.ObjectFactory.class,
                                                      org.geotoolkit.csw.xml.v202.LimitedObjectFactory.class,
                                                      org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("error while creating CSW context:" + ex.getMessage());
        }
    }
}
