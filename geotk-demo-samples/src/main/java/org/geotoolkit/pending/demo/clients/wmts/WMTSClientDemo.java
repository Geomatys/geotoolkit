
package org.geotoolkit.pending.demo.clients.wmts;

import java.net.URL;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.Resource;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wmts.WebMapTileClient;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.multires.TiledResource;


public class WMTSClientDemo {

    public static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapLayers context = createContext();

//        FXMapFrame.show(context);

    }

    public static MapLayers createContext() throws Exception{
        final MapLayers context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());

        final WebMapTileClient server = new WebMapTileClient(
                new URL("http://localhost:8080/constellation/WS/wmts/test"),
                WMTSVersion.v100);

        for(final Resource ref : DataStores.flatten(server, false)){
            final GenericName n = ref.getIdentifier().get();
            System.out.println(n);

            final MapLayer layer = MapBuilder.createCoverageLayer(
                    ref,
                    SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));

            TiledResource model = (TiledResource) ref;
            System.out.println(model);

            layer.setTitle(n.tip().toString());
            context.getComponents().add(layer);

        }

        return context;
    }

}
