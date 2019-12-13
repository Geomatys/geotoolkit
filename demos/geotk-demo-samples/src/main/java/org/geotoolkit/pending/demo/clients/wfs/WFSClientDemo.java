

package org.geotoolkit.pending.demo.clients.wfs;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.geotoolkit.data.wfs.GetCapabilitiesRequest;
import org.geotoolkit.data.wfs.GetFeatureRequest;
import org.geotoolkit.data.wfs.WFSStore;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.wfs.xml.WFSVersion;

public class WFSClientDemo {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        Demos.init();

        //simplify WFS like a datastore
        final WFSStore wfsServer = new WFSStore(new URL("http://demo.geomatys.com/constellation/WS/wfs?"), null, WFSVersion.v110, true);
        final GetCapabilitiesRequest getCapa = wfsServer.createGetCapabilities();
        final GetFeatureRequest getFeature = wfsServer.createGetFeature();


    }

}
