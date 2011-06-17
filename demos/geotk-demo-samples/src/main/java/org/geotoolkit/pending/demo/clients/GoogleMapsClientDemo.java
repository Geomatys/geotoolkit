
package org.geotoolkit.pending.demo.clients;

import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.StaticGoogleMapsServer;
import org.geotoolkit.googlemaps.map.GoogleMapsMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;


public class GoogleMapsClientDemo {
    
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    
    public static void main(String[] args) throws Exception {
        
        //Caution, Google Maps static api is limited to 1000 queries per day
        //and has not been build for real GIS applications
        //when moving to fast around, google considere seems to consider it as abusing it's service
        //and return http 403 errors. 
        //We recommand using OSM TMS client.
        //OSM maps are nearly as fast but is free, without ads and suffer no service limits or constraints.
        
        final MapContext context = createGoogleMapsContext();
        
        JMap2DFrame.show(context);
        
    }
 
    public static MapContext createGoogleMapsContext() throws Exception{
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

        final StaticGoogleMapsServer server = new StaticGoogleMapsServer(
                StaticGoogleMapsServer.DEFAULT_GOOGLE_STATIC_MAPS,null);
        final GoogleMapsMapLayer layer = new GoogleMapsMapLayer(server);
        layer.setMapType(GetMapRequest.TYPE_TERRAIN);
        
        context.layers().add(layer);
        
        return context;
    }
    
}
