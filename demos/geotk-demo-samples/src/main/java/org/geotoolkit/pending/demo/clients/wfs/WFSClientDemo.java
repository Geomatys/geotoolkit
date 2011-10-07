

package org.geotoolkit.pending.demo.clients.wfs;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.wfs.GetCapabilitiesRequest;
import org.geotoolkit.data.wfs.GetFeatureRequest;
import org.geotoolkit.data.wfs.WFSDataStore;
import org.geotoolkit.data.wfs.WebFeatureServer;

public class WFSClientDemo {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {

        final WebFeatureServer wmsServer = new WebFeatureServer(new URL("http://demo.geomatys.com/constellation/WS/wfs?"), "1.1.0");
        final GetCapabilitiesRequest getCapa = wmsServer.createGetCapabilities();
        final GetFeatureRequest getFeature = wmsServer.createGetFeature();

        //simplify WFS like a datastore
        final DataStore store = new WFSDataStore(new URI("http://demo.geomatys.com/constellation/WS/wfs?"), true);

    }

}
