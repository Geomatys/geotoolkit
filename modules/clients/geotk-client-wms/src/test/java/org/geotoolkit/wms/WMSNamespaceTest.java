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

import org.opengis.feature.type.Name;
import java.util.Set;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.wms.xml.WMSVersion;
import org.apache.sis.storage.DataStoreException;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * check proper namespace parsing.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSNamespaceTest {

    private final WebMapServer SERVER_111;
    private final WebMapServer SERVER_130;

    public WMSNamespaceTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapServer(WMSVersion.v111);
        SERVER_130 = new MockWebMapServer(WMSVersion.v130);
    }

    /**
     * This test proper namespace parsing in v1.1.1
     */
    @Test
    public void test_v111_GetNames() throws DataStoreException{
        
        final Set<Name> names = SERVER_111.getNames();
        
        assertEquals(3, names.size());
        assertTrue(names.contains(new DefaultName("ns1","Sample")));
        assertTrue(names.contains(new DefaultName("ns2","Sample")));
        
    }
    
    /**
     * This test proper namespace parsing in v1.3.0
     */
    @Test
    public void test_v130_GetNames() throws DataStoreException{
        
        final Set<Name> names = SERVER_130.getNames();
        
        assertEquals(3, names.size());
        assertTrue(names.contains(new DefaultName("ns1","Sample")));
        assertTrue(names.contains(new DefaultName("ns2","Sample")));
        
    }


}
