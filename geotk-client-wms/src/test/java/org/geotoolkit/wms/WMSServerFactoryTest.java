/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.junit.Test;

/**
 * Server tests.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSServerFactoryTest {

    public WMSServerFactoryTest() {
    }

    @Test
    public void testFactory() {

        final Iterator<DataStoreProvider> ite = DataStores.providers().iterator();
        boolean found = false;
        while (ite.hasNext()){
            if(ite.next() instanceof WMSProvider){
                found = true;
            }
        }

        if(!found){
            fail("Factory not found");
        }
    }

    @Test
    public void no_error_on_unsupported_input() throws DataStoreException {
        final ProbeResult probe = new WMSProvider().probeContent(new StorageConnector(new ByteArrayInputStream(new byte[0])));
        assertEquals(ProbeResult.UNSUPPORTED_STORAGE, probe);
    }
}
