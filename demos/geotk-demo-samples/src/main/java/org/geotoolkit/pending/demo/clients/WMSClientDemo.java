

package org.geotoolkit.pending.demo.clients;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.wms.GetCapabilitiesRequest;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;

public class WMSClientDemo {

    public static void main(String[] args) throws MalformedURLException {

        final WebMapServer wmsServer = new WebMapServer(new URL("http://demo.geomatys.com/constellation/Ws/wms?"), WMSVersion.v130);
        final GetCapabilitiesRequest getCapa = wmsServer.createGetCapabilities();
        final GetMapRequest getMap = wmsServer.createGetMap();
        final GetLegendRequest getLegend = wmsServer.createGetLegend();

        //simplify usage for map layer
        final WMSMapLayer layer = new WMSMapLayer(wmsServer, "BlueMarble");



    }

}
