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

package org.geotoolkit.data.osm.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.client.Server;
import org.geotoolkit.data.osm.client.v060.GetCapabilities060;
import org.geotoolkit.data.osm.model.Api;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OpenStreetMapServer implements Server{

    private static final Logger LOGGER = Logging.getLogger(OpenStreetMapServer.class);

    private Api capabilities = null;

    private final OSMVersion version;
    private final URL serverURL;

    public OpenStreetMapServer(URL serverURL, String version){
        this(serverURL, OSMVersion.getVersion(version));
    }

    public OpenStreetMapServer(URL url, OSMVersion version){
        this.serverURL = url;
        this.version = version;
    }

    public Api getCapabilities(){
        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {
            @Override
            public void run() {
                OSMXMLReader reader = null;
                try {
                    URL url = createGetCapabilities().getURL();
                    reader = new OSMXMLReader(url.openStream());
                    capabilities = (Api) reader.next();
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.SEVERE, "Wrong URL, the server doesn't answer : " +
                                createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.SEVERE, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                } finally{
                    if(reader != null){
                        try {
                            reader.close();
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } catch (XMLStreamException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
        thread.start();
        final long start = System.currentTimeMillis();
        try {
            thread.join(10000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "The thread to obtain Capabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > 10000) {
            LOGGER.log(Level.SEVERE, "TimeOut error, the server takes too much time to answer. ");
        }

        return capabilities;
    }

    public GetCapabilitiesRequest createGetCapabilities(){
        switch (version) {
            case v060:
                return new GetCapabilities060(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

}
