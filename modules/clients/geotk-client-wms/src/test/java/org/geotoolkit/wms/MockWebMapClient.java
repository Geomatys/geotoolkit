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

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSMarshallerPool;
import org.geotoolkit.wms.xml.WMSVersion;
import org.apache.sis.xml.MarshallerPool;

/**
 * For testing purpose.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MockWebMapClient extends WebMapClient{

    private final AbstractWMSCapabilities capa;

    public MockWebMapClient(final WMSVersion version) throws MalformedURLException, JAXBException{
        super(new URL("http://localhost/mock/wms?"), version);
        final String capaLocation;
        switch (version) {
            case v100:
                capaLocation = "/org/geotoolkit/wms/wms100.xml";
                break;
            case v111:
                capaLocation = "/org/geotoolkit/wms/wms111.xml";
                break;
            case v130:
                capaLocation = "/org/geotoolkit/wms/wms130.xml";
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Version: " + getVersion());
        }

        final MarshallerPool pool = WMSMarshallerPool.getInstance(version);
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(capaLocation));
        pool.recycle(unmarshaller);
    }

    @Override
    public AbstractWMSCapabilities getCapabilities() {
        return capa;
    }
}
