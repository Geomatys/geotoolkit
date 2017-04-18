

package org.geotoolkit.pending.demo.mapmodel;

import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
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

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.gui.javafx.render2d.FXMapFrame;
import org.geotoolkit.storage.DataStores;


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
        File cloudFile = new File(MapContextDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(cloudFile);

        //add all layers in the context
        context.layers().add(coverageLayer);
        context.layers().add(featureLayer);

        //quickly test if it works
        FXMapFrame.show(context);


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
        params.put("path", MapContextDemo.class.getResource("/data/world/Countries.shp").toURI());

        final FeatureStore store = (FeatureStore) DataStores.open(params);
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(store.getNames().iterator().next());
        final FeatureCollection collection = session.getFeatureCollection(query);
        return collection;
    }

    public static GridCoverageReader openWorldFile() throws Exception{
        File cloudFile = new File(MapContextDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        return CoverageIO.createSimpleReader(cloudFile);
    }


}
