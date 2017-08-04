
package org.geotoolkit.pending.demo.clients.osmtms;

import java.net.URL;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.osmtms.OSMTMSClientFactory;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.opengis.util.GenericName;


public class OSMTMSClientDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createOSMTMSContext();

        JMap2DFrame.show(context,false,null);

    }

    public static MapContext createOSMTMSContext() throws Exception{
        final MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());

        final Parameters params = Parameters.castOrWrap(OSMTMSClientFactory.PARAMETERS.createValue());
        params.getOrCreate(OSMTMSClientFactory.URL).setValue(new URL("http://tile.openstreetmap.org"));
        params.getOrCreate(OSMTMSClientFactory.IMAGE_CACHE).setValue(true);
        params.getOrCreate(OSMTMSClientFactory.NIO_QUERIES).setValue(true);
        params.getOrCreate(OSMTMSClientFactory.MAX_ZOOM_LEVEL).setValue(18);

        final CoverageStore store = (CoverageStore) DataStores.open(params);

        for(GenericName n : store.getNames()){
            final CoverageResource cr = store.findResource(n);
            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(
                    new SimpleInternationalString(n.tip().toString()),
                    new SimpleInternationalString("")));
            context.layers().add(cml);
        }

        //Other available OSM TMS
        // http://a.tah.openstreetmap.org/Tiles/tile/   17
        // http://tile.opencyclemap.org/cycle/ 18
        // http://tile.cloudmade.com/fd093e52f0965d46bb1c6c6281022199/3/256/ 18

        return context;
    }

}
