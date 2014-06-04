

package org.geotoolkit.pending.demo.clients.wms;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.wms.GetCapabilitiesRequest;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;

public class WMSClientDemo {

    public static void main(String[] args) throws MalformedURLException {
        Demos.init();
        
        final WebMapClient wmsServer = new WebMapClient(new URL("http://localhost:9090/guyamapa-cstl/WS/wms/default?"), WMSVersion.v130);
        final GetCapabilitiesRequest getCapa = wmsServer.createGetCapabilities();
        final GetMapRequest getMap = wmsServer.createGetMap();
        final GetLegendRequest getLegend = wmsServer.createGetLegend();

        //simplify usage for map layer
        final WMSMapLayer layer = new WMSMapLayer(wmsServer, "layerTest");
        final WMSMapLayer layer2 = new WMSMapLayer(wmsServer, "Countries");

        final MapContext context = MapBuilder.createContext();        
        context.layers().add(layer);
        context.layers().add(layer2);
        JMap2DFrame.show(context);
    }

}
