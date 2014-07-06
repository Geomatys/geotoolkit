
package org.geotoolkit.pending.demo.clients.osmtms;

import java.net.URL;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.osmtms.OSMTMSClientFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;


public class OSMTMSClientDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createOSMTMSContext();

        JMap2DFrame.show(context,false,null);

    }

    public static MapContext createOSMTMSContext() throws Exception{
        final MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());

        final ParameterValueGroup params = OSMTMSClientFactory.PARAMETERS.createValue();
        Parameters.getOrCreate(OSMTMSClientFactory.URL, params).setValue(new URL("http://tile.openstreetmap.org"));
        Parameters.getOrCreate(OSMTMSClientFactory.IMAGE_CACHE, params).setValue(true);
        Parameters.getOrCreate(OSMTMSClientFactory.NIO_QUERIES, params).setValue(true);
        Parameters.getOrCreate(OSMTMSClientFactory.MAX_ZOOM_LEVEL, params).setValue(18);

        final CoverageStore store = CoverageStoreFinder.open(params);

        for(Name n : store.getNames()){
            final CoverageReference cr = store.getCoverageReference(n);
            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(
                    new SimpleInternationalString(n.getLocalPart()),
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
