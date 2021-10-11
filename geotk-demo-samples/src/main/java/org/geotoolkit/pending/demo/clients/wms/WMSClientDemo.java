

package org.geotoolkit.pending.demo.clients.wms;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.sis.portrayal.MapLayers;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.wms.GetCapabilitiesRequest;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WMSResource;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.wms.xml.WMSVersion;

public class WMSClientDemo {

    public static void main(String[] args) throws MalformedURLException {
        Demos.init();

        final WebMapClient wmsServer = new WebMapClient(new URL("http://localhost:9090/guyamapa-cstl/WS/wms/default?"), WMSVersion.v130);
        final GetCapabilitiesRequest getCapa = wmsServer.createGetCapabilities();
        final GetMapRequest getMap = wmsServer.createGetMap();
        final GetLegendRequest getLegend = wmsServer.createGetLegend();

        //simplify usage for map layer
        final WMSResource layer = new WMSResource(wmsServer, "spot2");
        //final WMSMapLayer layer3 = new WMSMapLayer(wmsServer, "layerTest2");
        final WMSResource layer2 = new WMSResource(wmsServer, "Countries");

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(MapBuilder.createCoverageLayer(layer));
        context.getComponents().add(MapBuilder.createCoverageLayer(layer2));
        //context.layers().add(layer3);
//        FXMapFrame.show(context);
    }

}
