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
package org.geotoolkit.wmsc;

import java.util.Iterator;
import org.geotoolkit.client.ClientFactory;
import org.geotoolkit.client.ClientFinder;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Server tests.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCClientFactoryTest extends org.geotoolkit.test.TestBase {

    public WMSCClientFactoryTest() {
    }

    @Test
    public void testFactory() {

        final Iterator<ClientFactory> ite = ClientFinder.getAllFactories(null).iterator();

        boolean found = false;
        while (ite.hasNext()){
            if(ite.next() instanceof WMSCClientFactory){
                found = true;
            }
        }

        if(!found){
            fail("Factory not found");
        }
    }
}
