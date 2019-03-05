
package org.geotoolkit.pending.demo.coverage;

import java.nio.file.Path;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Resource;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.coverage.filestore.FileCoverageProvider;
import org.geotoolkit.gui.javafx.render2d.FXMapFrame;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.util.GenericName;


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

        final CoverageStore store = (CoverageStore) DataStores.open(params);

        //create a mapcontext
        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            final Resource ref = store.findResource(n.toString());
            final CoverageMapLayer cl = MapBuilder.createCoverageLayer(ref);
            context.layers().add(cl);
        }

        //display it
        FXMapFrame.show(context);

    }

}
