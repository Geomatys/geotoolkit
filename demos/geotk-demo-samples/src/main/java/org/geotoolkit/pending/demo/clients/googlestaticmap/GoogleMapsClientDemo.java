
package org.geotoolkit.pending.demo.clients.googlestaticmap;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.util.GenericName;


public class GoogleMapsClientDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        //Caution, Google Maps static api is limited to 1000 queries per day
        //and has not been build for real GIS applications
        //when moving to fast around, google considere seems to consider it as abusing it's service
        //and return http 403 errors.
        //We recommand using OSM TMS client.
        //OSM maps are nearly as fast but is free, without ads and suffer no service limits or constraints.

        final MapContext context = MapBuilder.createContext();

        final Map parameters = new HashMap();
        parameters.put("identifier", "googleStaticMaps");
        parameters.put("url", new URL("http://maps.google.com/maps/api/staticmap"));

        final CoverageStore store = (CoverageStore) DataStores.open(parameters);

        for(GenericName name : store.getNames()){
            final CoverageMapLayer layer = MapBuilder.createCoverageLayer(store.findResource(name));
            layer.setDescription(SF.description(name.tip().toString(), ""));
            context.layers().add(layer);
        }

        JMap2DFrame.show(context);

    }

}
