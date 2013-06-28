

package org.geotoolkit.pending.demo.mapmodel;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
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
import org.geotoolkit.pending.demo.Demos;
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
        Demos.init();
        
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
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, 0, coverageStyle,"coverage");

        //add all layers in the context
        context.layers().add(coverageLayer);
        context.layers().add(featureLayer);

        //quickly test if it works
        JMap2DFrame.show(context);
        
        
        //Build the context in a tree structure
        //context.items().clear();
        //
        //MapItem bggroup = MapBuilder.createItem();
        //bggroup.setDescription(SF.description("Background", ""));
        //MapItem datagroup = MapBuilder.createItem();
        //datagroup.setDescription(SF.description("Datas", ""));
        //
        //bggroup.items().add(wmsLayer);
        //datagroup.items().add(coverageLayer);
        //datagroup.items().add(featureLayer);
        //
        //context.items().add(bggroup);
        //context.items().add(datagroup);
        //
        //JMap2DFrame.show(context);
        
    }

    public static FeatureCollection openShapeFile() throws Exception{
        final Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put("url", MapContextDemo.class.getResource("/data/world/Countries.shp"));

        final FeatureStore store = FeatureStoreFinder.open(params);
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(store.getNames().iterator().next());
        final FeatureCollection collection = session.getFeatureCollection(query);
        return collection;
    }

    public static GridCoverageReader openWorldFile() throws Exception{
        return CoverageIO.createSimpleReader(new File("data/clouds.jpg"));
    }


}
