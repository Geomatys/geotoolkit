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

import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.client.Request;
import org.geotoolkit.data.osm.client.v060.CloseChangeSet060;
import org.geotoolkit.data.osm.client.v060.CreateChangeSet060;
import org.geotoolkit.data.osm.client.v060.ChangeElement060;
import org.geotoolkit.data.osm.client.v060.DownloadChangeSet060;
import org.geotoolkit.data.osm.client.v060.DownloadGPSTraceData060;
import org.geotoolkit.data.osm.client.v060.DownloadGPSTraceDetails060;
import org.geotoolkit.data.osm.client.v060.ExpandChangeSet060;
import org.geotoolkit.data.osm.client.v060.GetCapabilities060;
import org.geotoolkit.data.osm.client.v060.GetChangeSet060;
import org.geotoolkit.data.osm.client.v060.GetChangeSets060;
import org.geotoolkit.data.osm.client.v060.GetData060;
import org.geotoolkit.data.osm.client.v060.GetGPSTraces060;
import org.geotoolkit.data.osm.client.v060.ReadElement060;
import org.geotoolkit.data.osm.client.v060.ReadElementFull060;
import org.geotoolkit.data.osm.client.v060.ReadElementHistory060;
import org.geotoolkit.data.osm.client.v060.ReadElementRelations060;
import org.geotoolkit.data.osm.client.v060.ReadElements060;
import org.geotoolkit.data.osm.client.v060.ReadNodeWays060;
import org.geotoolkit.data.osm.client.v060.UpdateChangeSet060;
import org.geotoolkit.data.osm.client.v060.Upload060;
import org.geotoolkit.data.osm.model.Api;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.logging.Logging;

/**
 * Represent an open street map server.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OpenStreetMapServer extends AbstractServer{

    private static final Logger LOGGER = Logging.getLogger(OpenStreetMapServer.class);

    private Api capabilities = null;

    private final OSMVersion version;

    public OpenStreetMapServer(final URL serverURL, final String version){
        this(serverURL, OSMVersion.getVersion(version));
    }

    public OpenStreetMapServer(final URL url, final OSMVersion version){
        this(url,null,version);
    }
    
    public OpenStreetMapServer(final URL url, final ClientSecurity security, final OSMVersion version){
        super(url,security);
        ArgumentChecks.ensureNonNull("version", version);
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
                    reader = new OSMXMLReader();
                    reader.setInput(url.openStream());
                    capabilities = (Api) reader.next();
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " +
                                createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.WARNING, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                } finally{
                    if(reader != null){
                        try {
                            reader.dispose();
                        } catch (IOException ex) {
                            LOGGER.log(Level.WARNING, null, ex);
                        } catch (XMLStreamException ex) {
                            LOGGER.log(Level.WARNING, null, ex);
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
            LOGGER.log(Level.WARNING, "The thread to obtain Capabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > 10000) {
            LOGGER.log(Level.WARNING, "TimeOut error, the server takes too much time to answer. ");
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
                return new GetCapabilities060(this);
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
                return new GetData060(this);
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
                return new GetChangeSet060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">OSM API 0.6</a>}
     */
    public GetChangeSetsRequest createGetChangeSets(){
        switch (version) {
            case v060:
                return new GetChangeSets060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Update:_PUT_.2Fapi.2F0.6.2Fchangeset.2F.23id">OSM API 0.6</a>}
     */
    public CreateChangeSetRequest createCreateChangeSet(){
        switch (version) {
            case v060:
                return new CreateChangeSet060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Update:_PUT_.2Fapi.2F0.6.2Fchangeset.2F.23id">OSM API 0.6</a>}
     */
    public UpdateChangeSetRequest createUpdateChangeSet(){
        switch (version) {
            case v060:
                return new UpdateChangeSet060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Close:_PUT_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fclose">OSM API 0.6</a>}
     */
    public CloseChangeSetRequest createCloseChangeSet(){
        switch (version) {
            case v060:
                return new CloseChangeSet060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload">OSM API 0.6</a>}
     */
    public DownloadChangeSetRequest createDownloadChangeSet(){
        switch (version) {
            case v060:
                return new DownloadChangeSet060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Expand_Bounding_Box:_POST_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fexpand_bbox">OSM API 0.6</a>}
     */
    public ExpandChangeSetRequest createExpandChangeSet(){
        switch (version) {
            case v060:
                return new ExpandChangeSet060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Diff_upload:_POST_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fupload">OSM API 0.6</a>}
     */
    public UploadRequest createUploadChangeSet(){
        switch (version) {
            case v060:
                return new Upload060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Create:_PUT_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2Fcreate">OSM API 0.6</a>}
     */
    public ChangeElementRequest createCreateElement(){
        switch (version) {
            case v060:
                return new ChangeElement060(this,ChangeElementRequest.Type.CREATE);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Read:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">OSM API 0.6</a>}
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Version:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id.2F.23version">OSM API 0.6</a>}
     */
    public ReadElementRequest createReadElement(){
        switch (version) {
            case v060:
                return new ReadElement060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Multi_fetch:_GET_.2Fapi.2F0.6.2F.5Bnodes.7Cways.7Crelations.5D">OSM API 0.6</a>}
     */
    public ReadElementsRequest createReadElements(){
        switch (version) {
            case v060:
                return new ReadElements060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Full:_GET_.2Fapi.2F0.6.2F.5Bway.7Crelation.5D.2F.23id.2Ffull">OSM API 0.6</a>}
     */
    public ReadElementFullRequest createReadFullElement(){
        switch (version) {
            case v060:
                return new ReadElementFull060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Update:_PUT_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">OSM API 0.6</a>}
     */
    public ChangeElementRequest createUpdateElement(){
        switch (version) {
            case v060:
                return new ChangeElement060(this,ChangeElementRequest.Type.UPDATE);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Delete:_DELETE_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">OSM API 0.6</a>}
     */
    public ChangeElementRequest createDeleteElement(){
        switch (version) {
            case v060:
                return new ChangeElement060(this,ChangeElementRequest.Type.DELETE);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#History:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id.2Fhistory">OSM API 0.6</a>}
     */
    public ReadElementHistoryRequest createHistoryElement(){
        switch (version) {
            case v060:
                return new ReadElementHistory060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Relations_for_Element:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id.2Frelations">OSM API 0.6</a>}
     */
    public ReadElementRelationsRequest createRelatedRelationElement(){
        switch (version) {
            case v060:
                return new ReadElementRelations060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Ways_for_Node:_GET_.2Fapi.2F0.6.2Fnode.2F.23id.2Fways">OSM API 0.6</a>}
     */
    public ReadNodeWaysRequest createRelatedWayElement(){
        switch (version) {
            case v060:
                return new ReadNodeWays060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    //GPS ----------------------------------------------------------------------

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Retrieving_GPS_points">OSM API 0.6</a>}
     */
    public GetGPSTraceRequest createGetGPSTraces(){
        switch (version) {
            case v060:
                return new GetGPSTraces060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Uploading_Traces">OSM API 0.6</a>}
     */
    public Request createUploadGPSTrace(){
        //todo need GPX parser
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Downloading_Trace_Metadata">OSM API 0.6</a>}
     */
    public DownloadGPSTraceDetail createDownloadGPSTraceDetails(){
        switch (version) {
            case v060:
                return new DownloadGPSTraceDetails060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Downloading_Trace_Metadata">OSM API 0.6</a>}
     */
    public DownloadGPSTraceData createDownloadGPSTraceData(){
        switch (version) {
            case v060:
                return new DownloadGPSTraceData060(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    //User ---------------------------------------------------------------------

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Details">OSM API 0.6</a>}
     */
    public Request createGetUserMetaData(){
        //todo need auhtentification system on server/request
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * {@see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6#Preferences">OSM API 0.6</a>}
     */
    public Request createGetUserPreference(){
        //todo need auhtentification system on server/request
        throw new UnsupportedOperationException("Not implemented yet.");
    }


}
