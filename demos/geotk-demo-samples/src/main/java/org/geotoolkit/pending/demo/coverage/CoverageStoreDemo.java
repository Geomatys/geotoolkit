
package org.geotoolkit.pending.demo.coverage;

import java.nio.file.Path;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.geotoolkit.coverage.worldfile.FileCoverageProvider;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;


public class CoverageStoreDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();
        WorldFileImageReader.Spi.registerDefaults(null);
        WorldFileImageWriter.Spi.registerDefaults(null);

        Path dataResources = IOUtilities.getResourceAsPath("data");

        final Parameters params = Parameters.castOrWrap(FileCoverageProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(FileCoverageProvider.PATH).setValue(dataResources.toUri());
        params.getOrCreate(FileCoverageProvider.TYPE).setValue("jpg-wf");

        final DataStore store = DataStores.open(params);

        //create a mapcontext
        final MapContext context = MapBuilder.createContext();

        for (Resource ref : DataStores.flatten(store, true, GridCoverageResource.class)) {
            final MapLayer cl = MapBuilder.createCoverageLayer(ref);
            context.getComponents().add(cl);
        }

        //display it
//        FXMapFrame.show(context);

    }

}
