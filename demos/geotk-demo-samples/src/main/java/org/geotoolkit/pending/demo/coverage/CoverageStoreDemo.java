
package org.geotoolkit.pending.demo.coverage;

import java.io.File;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.filestore.FileCoverageStoreFactory;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;


public class CoverageStoreDemo {
    
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    
    public static void main(String[] args) throws Exception {
        Demos.init();
        WorldFileImageReader.Spi.registerDefaults(null);
        WorldFileImageWriter.Spi.registerDefaults(null);
                                
        final File input = new File("data");
        
        final ParameterValueGroup params = FileCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(FileCoverageStoreFactory.PATH, params).setValue(input.toURI().toURL());
        Parameters.getOrCreate(FileCoverageStoreFactory.TYPE, params).setValue("jpg-wf");
        
        final CoverageStore store = CoverageStoreFinder.open(params);
        
        //create a mapcontext
        final MapContext context = MapBuilder.createContext();     
        
        for(Name n : store.getNames()){
            final CoverageReference ref = store.getCoverageReference(n);
            final CoverageMapLayer cl = MapBuilder.createCoverageLayer(
                    ref, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "raster");
            context.layers().add(cl);
        }
        
        //display it
        JMap2DFrame.show(context);
        
    }
    
}
