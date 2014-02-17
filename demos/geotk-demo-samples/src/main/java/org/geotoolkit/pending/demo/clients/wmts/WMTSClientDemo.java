
package org.geotoolkit.pending.demo.clients.wmts;

import java.net.URL;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.osmtms.OSMTileMapServer;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wmts.WebMapTileServer;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.opengis.feature.type.Name;


public class WMTSClientDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createContext();

        JMap2DFrame.show(context);

    }

    public static MapContext createContext() throws Exception{
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

        final WebMapTileServer server = new WebMapTileServer(
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

            layer.setDescription(SF.description(n.getLocalPart(), n.getLocalPart()));
            context.layers().add(layer);

        }

        return context;
    }

}
