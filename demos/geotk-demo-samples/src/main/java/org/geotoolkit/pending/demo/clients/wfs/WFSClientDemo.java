

package org.geotoolkit.pending.demo.clients.wfs;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.wfs.GetCapabilitiesRequest;
import org.geotoolkit.data.wfs.GetFeatureRequest;
import org.geotoolkit.data.wfs.WFSFeatureStore;
import org.geotoolkit.data.wfs.WebFeatureServer;
import org.geotoolkit.pending.demo.Demos;

public class WFSClientDemo {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        Demos.init();
        
        
        final WebFeatureServer wfsServer = new WebFeatureServer(new URL("http://demo.geomatys.com/constellation/WS/wfs?"), "1.1.0");
        final GetCapabilitiesRequest getCapa = wfsServer.createGetCapabilities();
        final GetFeatureRequest getFeature = wfsServer.createGetFeature();

        //simplify WFS like a datastore
        final FeatureStore store = new WFSFeatureStore(wfsServer);

    }

}
