/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.wms;

import org.opengis.util.GenericName;
import java.util.Set;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;

import org.geotoolkit.wms.xml.WMSVersion;
import org.apache.sis.storage.DataStoreException;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * check proper namespace parsing.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSNamespaceTest extends org.geotoolkit.test.TestBase {

    private final WebMapClient SERVER_111;
    private final WebMapClient SERVER_130;

    public WMSNamespaceTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapClient(WMSVersion.v111);
        SERVER_130 = new MockWebMapClient(WMSVersion.v130);
    }

    /**
     * This test proper namespace parsing in v1.1.1
     */
    @Test
    public void test_v111_GetNames() throws DataStoreException {

        final Set<GenericName> names = SERVER_111.getNames();

        assertEquals(3, names.size());
        assertTrue(names.stream().anyMatch((GenericName t) -> t.toString().equals("ns1:Sample")));
        assertTrue(names.stream().anyMatch((GenericName t) -> t.toString().equals("ns2:Sample")));

    }

    /**
     * This test proper namespace parsing in v1.3.0
     */
    @Test
    public void test_v130_GetNames() throws DataStoreException {

        final Set<GenericName> names = SERVER_130.getNames();

        assertEquals(3, names.size());
        assertTrue(names.stream().anyMatch((GenericName t) -> t.toString().equals("ns1:Sample")));
        assertTrue(names.stream().anyMatch((GenericName t) -> t.toString().equals("ns2:Sample")));
    }

}
