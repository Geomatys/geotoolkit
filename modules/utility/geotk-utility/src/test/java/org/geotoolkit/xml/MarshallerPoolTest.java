/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.xml;

import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MarshallerPool}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class MarshallerPoolTest {
    /**
     * Tests a marshaller which is acquired, then released.
     * The marshaller should be reset to its initial state
     * despite the setter method we may have invoked on it.
     *
     * @throws JAXBException Should not happen.
     */
    @Test
    public void testAcquireRelease() throws JAXBException {
        final MarshallerPool pool = new MarshallerPool(new Class<?>[0]);
        final Marshaller marshaller = pool.acquireMarshaller();
        assertNotNull(marshaller);
        /*
         * PooledMarshaller should convert the property name from "com.sun.xml.bind.xmlHeaders" to
         * "com.sun.xml.internal.bind.xmlHeaders" if we are running JDK 6 implementation of JAXB.
         */
        assertNull(marshaller.getProperty("com.sun.xml.bind.xmlHeaders"));
        marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<DTD ...>");
        assertEquals("<DTD ...>", marshaller.getProperty("com.sun.xml.bind.xmlHeaders"));
        /*
         * MarshallerPool should reset the properties to their initial state.
         */
        pool.release(marshaller);
        assertSame(marshaller, pool.acquireMarshaller());
        // Following should be null, but has been replaced by "" under the hood
        // for avoiding a NullPointerException in current JAXB implementation.
        assertEquals("", marshaller.getProperty("com.sun.xml.bind.xmlHeaders"));
        pool.release(marshaller);
    }
}
