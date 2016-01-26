
package org.geotoolkit.pending.demo.coverage;

import java.nio.file.Path;

import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.coverage.filestore.FileCoverageStoreFactory;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;


public class CoverageStoreDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();
        WorldFileImageReader.Spi.registerDefaults(null);
        WorldFileImageWriter.Spi.registerDefaults(null);

        Path dataResources = IOUtilities.getResourceAsPath("data");

        final ParameterValueGroup params = FileCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(FileCoverageStoreFactory.PATH, params).setValue(dataResources.toUri());
        Parameters.getOrCreate(FileCoverageStoreFactory.TYPE, params).setValue("jpg-wf");

        final CoverageStore store = (CoverageStore) DataStores.open(params);

        //create a mapcontext
        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            final CoverageReference ref = store.getCoverageReference(n);
            final CoverageMapLayer cl = MapBuilder.createCoverageLayer(ref);
            context.layers().add(cl);
        }

        //display it
        JMap2DFrame.show(context);

    }

}
