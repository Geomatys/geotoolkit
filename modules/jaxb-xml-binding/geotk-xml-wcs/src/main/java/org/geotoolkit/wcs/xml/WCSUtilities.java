/*
 *    Puzzle GIS - Desktop GIS Platform
 *    http://puzzle-gis.codehaus.org
 *
 *    (C) 2010, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.wcs.xml;

import java.io.InputStream;
import java.net.URL;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.lang.Static;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
@Static
public final class WCSUtilities {

    private WCSUtilities(){}

    public static GetCapabilitiesResponse unmarshallCapabilities(final InputStream stream) throws JAXBException{
        final Unmarshaller unmarshal = WCSMarshallerPool.getInstance().acquireUnmarshaller();
        Object obj;
        try{
            obj = unmarshal.unmarshal(stream);
        }finally{
            WCSMarshallerPool.getInstance().release(unmarshal);
        }

        if(obj instanceof JAXBElement){
            final JAXBElement ele = (JAXBElement) obj;
            obj = ele.getValue();
        }

        if(obj instanceof GetCapabilitiesResponse){
            return (GetCapabilitiesResponse)obj;
        }

        throw new IllegalArgumentException("Object returned is not a GetCapabilites, found : "+obj);
    }

}
