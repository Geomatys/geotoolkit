/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wmts.xml.v100;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.xml.MarshallerPool;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Marshalling and unmarshalling methods using the OGC WMTS v1.0.0 bindings.
 *
 * @version $Id: TestGetCapabilities.java 1542 2009-04-20 14:57:56Z glegal $
 *
 * @author Cédric Briançon (Geomatys)
 */
public class TestGetCapabilities {
    /**
     * The context to use for marshalling/unmarshalling processes.
     */
    private final MarshallerPool pool;

    public TestGetCapabilities() throws JAXBException {
        pool = new MarshallerPool("org.geotoolkit.wmts.xml.v100:" +
                                  "org.geotoolkit.ows.xml.v110:" +
                                  "org.geotoolkit.gml.xml.v311");
    }

    @Test
    public void testUnmarshalling() throws JAXBException, IOException {
        final Unmarshaller unmarsh =  pool.acquireUnmarshaller();
        final InputStream getCapsResponse = this.getClass().getResourceAsStream("wmtsGetCapabilities_response.xml");
        assertFalse("The getCapabilities response in the resources folder was not found !",
                    getCapsResponse.available() <= 0);
        final Object objResp = unmarsh.unmarshal(getCapsResponse);
        getCapsResponse.close();
        assertTrue("The unmarshalled object is not an instance of wmts Capabilities. Response class:"+
                   objResp.getClass().getName(), 
                   objResp instanceof Capabilities);
        final Capabilities capsResp = (Capabilities) objResp;
        final List<LayerType> layers = capsResp.getContents().getLayers();
        assertNotNull("Layer list is null", layers);
        assertFalse("Layer list is empty.", layers.isEmpty());
        final LayerType firstLayer = layers.get(0);
        assertEquals(firstLayer.getTitle().get(0).getValue(), "Coastlines");
        //System.out.println(firstLayer.toString());
        pool.release(unmarsh);
    }

}
