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
package org.geotoolkit.xacml.xml;

import java.io.File;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @version $Id: XacmlAnnotationsTest.java 1542 2009-04-20 14:57:56Z glegal $
 * @author Cédric Briançon
 */
public class XacmlAnnotationsTest {
     private Logger logger = Logging.getLogger("org.geotoolkit.xacml.xml");

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(
                "org.geotoolkit.xacml.xml.policy:" +
                "org.geotoolkit.xacml.xml.policy:" +
                "org.geotoolkit.xacml.xml.context");
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
    }

    @Test
    public void testPolicyAnnotation() throws JAXBException {
        File f = new File("/home/cedr/Bureau/GeoXACML/example/policy.xml");
        if (f.exists())  {
            final Object policy = unmarshaller.unmarshal(f);
            assertNotNull(policy);
        } else {
            logger.info("unable to find file skipping test");
        }

    }

    @Test
    public void testRequestAnnotation() throws JAXBException {
        File f = new File("/home/cedr/Bureau/GeoXACML/example/request.xml");
        if (f.exists()) {
            final Object request = unmarshaller.unmarshal(f);
            assertNotNull(request);
        } else {
            logger.info("unable to find file skipping test");
        }
    }

    @Test
    public void testResponseAnnotation() throws JAXBException {
        File f = new File("/home/cedr/Bureau/GeoXACML/example/response.xml");
        if (f.exists()) {
            final Object response = unmarshaller.unmarshal(f);
            assertNotNull(response);
        } else {
            logger.info("unable to find file skipping test");
        }
    }
}
