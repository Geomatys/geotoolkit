

package org.geotoolkit.pending.demo.clients.wms;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.wms.GetCapabilitiesRequest;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;

public class WMSClientDemo {

    public static void main(String[] args) throws MalformedURLException {

        final WebMapServer wmsServer = new WebMapServer(new URL("http://demo.geomatys.com/constellation/WS/wms?"), WMSVersion.v130);
        final GetCapabilitiesRequest getCapa = wmsServer.createGetCapabilities();
        final GetMapRequest getMap = wmsServer.createGetMap();
        final GetLegendRequest getLegend = wmsServer.createGetLegend();

        //simplify usage for map layer
        final WMSMapLayer layer = new WMSMapLayer(wmsServer, "BlueMarble");

        final MapContext context = MapBuilder.createContext();        
        context.layers().add(layer);
        JMap2DFrame.show(context);
    }

}
