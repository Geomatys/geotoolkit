

package org.geotoolkit.pending.demo.mapmodel;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageReaders;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;


public class MapContextDemo {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws Exception{
        //preloading parameters --------------
        WorldFileImageReader.Spi.registerDefaults(null);
        Registry.setDefaultCodecPreferences();
        //------------------------------------


        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a feature layer
        final FeatureCollection features = openShapeFile();
        final MutableStyle featureStyle = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final FeatureMapLayer featureLayer = MapBuilder.createFeatureLayer(features, featureStyle);

        //create a coverage layer
        final GridCoverageReader reader = openWorldFile();
        final MutableStyle coverageStyle = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, coverageStyle,"background");

        //create a WMS layer
//        final WebMapServer server = new WebMapServer(new URL("http://demo.geomatys.com/constellation/WS/wms"), WMSVersion.v130);
//        final WMSMapLayer wmsLayer = new WMSMapLayer(server, "BlueMarble");

        //add all layers in the context
//        context.layers().add(wmsLayer);
        context.layers().add(coverageLayer);
        context.layers().add(featureLayer);

        //quickly test if it works
        JMap2DFrame.show(context);
    }

    private static FeatureCollection openShapeFile() throws Exception{
        final Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put("url", MapContextDemo.class.getResource("/data/world/Countries.shp"));

        final DataStore store = DataStoreFinder.getDataStore(params);
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(store.getNames().iterator().next());
        final FeatureCollection collection = session.getFeatureCollection(query);
        return collection;
    }

    private static GridCoverageReader openWorldFile() throws Exception{
        return GridCoverageReaders.createSimpleReader(new File("data/clouds.jpg"));
    }


}
