/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import org.geotoolkit.wms.v111.GetCapabilities111;
import org.geotoolkit.wms.v111.GetMap111;
import org.geotoolkit.wms.v130.GetCapabilities130;
import org.geotoolkit.wms.v130.GetMap130;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSBindingUtilities;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WebMapServer {

    private final WMSVersion version;
    private final URL serverURL;
    private AbstractWMSCapabilities capabilities;

    public WebMapServer(URL serverURL, String version) {
        final WMSVersion vers;
        if(version.equals("1.1.1")){
            vers = WMSVersion.v111;
        }else if(version.equals("1.3.0")){
            vers = WMSVersion.v130;
        }else{
            throw new IllegalArgumentException("Unknowned version : "+ version);
        }

        this.version = vers;
        this.serverURL = serverURL;
    }

    public WebMapServer(URL serverURL, WMSVersion version) {
        this.version = version;
        this.serverURL = serverURL;
    }

    public AbstractWMSCapabilities getCapabilities() throws MalformedURLException, JAXBException {

        if(capabilities != null){
            return capabilities;
        }

        GetCapabilitiesRequest request = createGetCapabilities();
        capabilities = WMSBindingUtilities.unmarshall(request.getURL(), version);

        return capabilities;
    }

    public WMSVersion getVersion() {
        return version;
    }

    public GetMapRequest createGetMap(){
        switch(version){
            case v111 : return new GetMap111(serverURL.toString());
            case v130 : return new GetMap130(serverURL.toString());
            default: throw new IllegalArgumentException("Version was not defined");
        }
    }

    public GetCapabilitiesRequest createGetCapabilities(){
        switch(version){
            case v111 : return new GetCapabilities111(serverURL.toString());
            case v130 : return new GetCapabilities130(serverURL.toString());
            default: throw new IllegalArgumentException("Version was not defined");
        }
    }

}
