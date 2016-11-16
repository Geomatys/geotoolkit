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

package org.geotoolkit.ncwms;

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
public class MockWebMapClient extends NcWebMapClient{

    private final AbstractWMSCapabilities capa111;
    private final AbstractWMSCapabilities capa130;

    public MockWebMapClient(final WMSVersion version) throws MalformedURLException, JAXBException{
        super(new URL("http://localhost/mock/wms?"), version);

        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        capa111 = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource("/org/geotoolkit/wms/wms111.xml"));
        capa130 = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource("/org/geotoolkit/wms/wms130.xml"));
        pool.recycle(unmarshaller);

    }

    @Override
    public AbstractWMSCapabilities getCapabilities() {
        if(getVersion() == WMSVersion.v111){
            return capa111;
        }else{
            return capa130;
        }
    }

}
