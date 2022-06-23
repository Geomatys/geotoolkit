

package org.geotoolkit.pending.demo.mapmodel;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.pending.demo.rendering.PortrayalDemo;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.StyleFactory;


public class MapContextDemo {

    private static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);

    public static void main(String[] args) throws Exception{
        Demos.init();


        //create a map context
        final MapLayers context = MapBuilder.createContext();

        //create a feature layer
        final FeatureSet features = openShapeFile();
        final MutableStyle featureStyle = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final MapLayer featureLayer = MapBuilder.createLayer(features);
        featureLayer.setStyle(featureStyle);

        //create a coverage layer
        File cloudFile = new File(MapContextDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        final MapLayer coverageLayer = MapBuilder.createCoverageLayer(cloudFile);

        //add all layers in the context
        context.getComponents().add(coverageLayer);
        context.getComponents().add(featureLayer);

        //quickly test if it works
//        FXMapFrame.show(context);


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

    private static FeatureSet openShapeFile() throws DataStoreException, URISyntaxException {
        final Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put("path", PortrayalDemo.class.getResource("/data/world/Countries.shp").toURI());
        final DataStore store = DataStores.open(params);
        return DataStores.flatten(store, true, FeatureSet.class).iterator().next();
    }

    private static GridCoverageResource openWorldFile() throws DataStoreException, URISyntaxException {
        DataStore store = org.apache.sis.storage.DataStores.open(PortrayalDemo.class.getResource("/data/coverage/clouds.jpg"));
        return DataStores.flatten(store, true, GridCoverageResource.class).iterator().next();
    }


}
