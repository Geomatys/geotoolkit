
package org.geotoolkit.pending.demo.coverage;

import java.net.URL;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.storage.DataStore;
import org.geotoolkit.map.MapBuilder;
import org.apache.sis.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;


public class CoverageStoreDemo {

    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public static void main(String[] args) throws Exception {
        URL dataResources = CoverageStoreDemo.class.getClassLoader().getResource("data.coverage/clouds.jpg");

        final DataStore store = DataStores.open(dataResources);

        // create a mapcontext
        final MapLayers context = MapBuilder.createContext();

        final MapLayer cl = MapBuilder.createCoverageLayer(store);
        context.getComponents().add(cl);

        //display it
//        FXMapFrame.show(context);

        store.close();
    }
}
