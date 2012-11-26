
package org.geotoolkit.pending.demo.clients.osmtms;

import java.net.URL;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.osmtms.OSMTMSServerFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;


public class OSMTMSClientDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createOSMTMSContext();

        JMap2DFrame.show(context,true,null);

    }

    public static MapContext createOSMTMSContext() throws Exception{
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

        final ParameterValueGroup params = OSMTMSServerFactory.PARAMETERS.createValue();
        Parameters.getOrCreate(OSMTMSServerFactory.URL, params).setValue(new URL("http://tile.openstreetmap.org"));
        Parameters.getOrCreate(OSMTMSServerFactory.IMAGE_CACHE, params).setValue(true);
        Parameters.getOrCreate(OSMTMSServerFactory.NIO_QUERIES, params).setValue(true);
        Parameters.getOrCreate(OSMTMSServerFactory.MAX_ZOOM_LEVEL, params).setValue(18);

        final CoverageStore store = CoverageStoreFinder.open(params);

        for(Name n : store.getNames()){
            final CoverageReference cr = store.getCoverageReference(n);
            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr, RandomStyleBuilder.createDefaultRasterStyle(), "");
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
