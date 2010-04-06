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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.client.Request;

import org.geotoolkit.client.Server;
import org.geotoolkit.data.osm.client.v060.GetCapabilities060;
import org.geotoolkit.data.osm.client.v060.GetChangeSet060;
import org.geotoolkit.data.osm.client.v060.GetData060;
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

    @Override
    public URI getURI() {
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            Logger.getLogger(OpenStreetMapServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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

    // general queries ---------------------------------------------------------

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Capabilities:_GET_.2Fapi.2Fcapabilities">OSM API 0.6</a>}
     */
    public GetCapabilitiesRequest createGetCapabilities(){
        switch (version) {
            case v060:
                return new GetCapabilities060(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Retrieving_map_data_by_bounding_box:_GET_.2Fapi.2F0.6.2Fmap">OSM API 0.6</a>}
     */
    public GetDataRequest createGetData() {
        switch (version) {
            case v060:
                return new GetData060(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    //changeset ----------------------------------------------------------------

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Create:_PUT_.2Fapi.2F0.6.2Fchangeset.2Fcreate">OSM API 0.6</a>}
     */
    public GetChangeSetRequest createGetChangeSet(){
        switch (version) {
            case v060:
                return new GetChangeSet060(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">OSM API 0.6</a>}
     */
    public Request createGetChangeSets(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Update:_PUT_.2Fapi.2F0.6.2Fchangeset.2F.23id">OSM API 0.6</a>}
     */
    public Request createPutChangeSet(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Update:_PUT_.2Fapi.2F0.6.2Fchangeset.2F.23id">OSM API 0.6</a>}
     */
    public Request createUpdateChangeSet(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Close:_PUT_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fclose">OSM API 0.6</a>}
     */
    public Request createCloseChangeSet(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload">OSM API 0.6</a>}
     */
    public Request createDownloadChangeSet(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Expand_Bounding_Box:_POST_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fexpand_bbox">OSM API 0.6</a>}
     */
    public Request createExpandChangeSet(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Diff_upload:_POST_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fupload">OSM API 0.6</a>}
     */
    public Request createUploadChangeSet(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Create:_PUT_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2Fcreate">OSM API 0.6</a>}
     */
    public Request createCreateElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Read:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">OSM API 0.6</a>}
     */
    public Request createReadElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Multi_fetch:_GET_.2Fapi.2F0.6.2F.5Bnodes.7Cways.7Crelations.5D">OSM API 0.6</a>}
     */
    public Request createReadElements(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Full:_GET_.2Fapi.2F0.6.2F.5Bway.7Crelation.5D.2F.23id.2Ffull">OSM API 0.6</a>}
     */
    public Request createReadFullElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Update:_PUT_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">OSM API 0.6</a>}
     */
    public Request createUpdateElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Delete:_DELETE_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">OSM API 0.6</a>}
     */
    public Request createDeleteElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#History:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id.2Fhistory">OSM API 0.6</a>}
     */
    public Request createHistoryElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Version:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id.2F.23version">OSM API 0.6</a>}
     */
    public Request createVersionElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Relations_for_Element:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id.2Frelations">OSM API 0.6</a>}
     */
    public Request createRelatedRelationElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Ways_for_Node:_GET_.2Fapi.2F0.6.2Fnode.2F.23id.2Fways">OSM API 0.6</a>}
     */
    public Request createRelatedWayElement(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    //GPS ----------------------------------------------------------------------

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Retrieving_GPS_points">OSM API 0.6</a>}
     */
    public Request createGetTrackPoints(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Uploading_Traces">OSM API 0.6</a>}
     */
    public Request createUploadTrackPoints(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Downloading_Trace_Metadata">OSM API 0.6</a>}
     */
    public Request createGetTrackPointsMetaData(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    //User ---------------------------------------------------------------------

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Details">OSM API 0.6</a>}
     */
    public Request createGetUserMetaData(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Preferences">OSM API 0.6</a>}
     */
    public Request createGetUserPreference(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }


}
