
package org.geotoolkit.pending.demo.clients.wmts;

import java.net.URL;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wmts.WebMapTileClient;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.geotoolkit.feature.type.Name;


public class WMTSClientDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createContext();

        JMap2DFrame.show(context);

    }

    public static MapContext createContext() throws Exception{
        final MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());

        final WebMapTileClient server = new WebMapTileClient(
                new URL("http://localhost:8080/constellation/WS/wmts/test"),
                WMTSVersion.v100);
        
        for(final Name n : server.getNames()){
            System.out.println(n);

            final CoverageReference ref = server.getCoverageReference(n);
            final MapLayer layer = MapBuilder.createCoverageLayer(
                    ref,
                    new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));

            PyramidalCoverageReference model = (PyramidalCoverageReference) ref;
            System.out.println(model.getPyramidSet());

            layer.setDescription(SF.description(n.tip().toString(), n.tip().toString()));
            context.layers().add(layer);

        }

        return context;
    }

}
