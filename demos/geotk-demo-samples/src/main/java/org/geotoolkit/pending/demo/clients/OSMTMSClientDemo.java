
package org.geotoolkit.pending.demo.clients;

import java.net.URL;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.osmtms.OSMTileMapServer;
import org.geotoolkit.osmtms.map.OSMTMSMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;


public class OSMTMSClientDemo {
    
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    
    public static void main(String[] args) throws Exception {
        
        final MapContext context = createOSMTMSContext();
        
        JMap2DFrame.show(context);
        
    }
 
    public static MapContext createOSMTMSContext() throws Exception{
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

        OSMTileMapServer server = new OSMTileMapServer(
                new URL("http://tile.openstreetmap.org"),CRS.decode("EPSG:3857"),18);
        OSMTMSMapLayer layer = new OSMTMSMapLayer(server);
        layer.setDescription(SF.description("Mapnik", ""));
        context.layers().add(layer);
        
        
        //Other available OSM TMS
        
//        server = new OSMTileMapServer(
//                new URL("http://a.tah.openstreetmap.org/Tiles/tile/"),CRS.decode("EPSG:3857"),17);
//        layer = new OSMTMSMapLayer(server);
//        layer.setDescription(SF.description("Osmarender", ""));
//        context.layers().add(layer);
//        
//        server = new OSMTileMapServer(
//                new URL("http://tile.opencyclemap.org/cycle/"),CRS.decode("EPSG:3857"),18);
//        layer = new OSMTMSMapLayer(server);
//        layer.setDescription(SF.description("Carte cyclable", ""));
//        context.layers().add(layer);
//        
//        server = new OSMTileMapServer(
//                new URL("http://tile.cloudmade.com/fd093e52f0965d46bb1c6c6281022199/3/256/"),CRS.decode("EPSG:3857"),18);
//        layer = new OSMTMSMapLayer(server);
//        layer.setDescription(SF.description("SansNom", ""));
//        context.layers().add(layer);
        
        
        
        return context;
    }
    
}
