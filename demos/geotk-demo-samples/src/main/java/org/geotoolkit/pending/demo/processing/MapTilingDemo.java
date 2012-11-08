package org.geotoolkit.pending.demo.processing;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.filestore.*;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.ProgressWindow;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Create a pyramid from a MapContext.
 */
public class MapTilingDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Throwable {
        Demos.init();
        
        //reset values, only allow pure java readers
        for(String jn : ImageIO.getReaderFormatNames()){
            if(jn.toLowerCase().contains("png")){
                Registry.setNativeCodecAllowed(jn, ImageReaderSpi.class, false);
            }
        }
        
        //reset values, only allow pure java writers
        for(String jn : ImageIO.getWriterFormatNames()){
            if(jn.toLowerCase().contains("png")){
                Registry.setNativeCodecAllowed(jn, ImageWriterSpi.class, false);
            }
        }
        
        
        
        //create a map context
        final MapContext context = openData();


        //get the description of the process we want
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "mapcontextpyramid");
        System.out.println(desc.getInputDescriptor());

        //create a coverage store where the pyramid wil be stored
        final XMLCoverageStoreFactory factory = new XMLCoverageStoreFactory();
        final CoverageStore store = factory.create(Collections.singletonMap("path", new URL("file:/media/terra/GIS_DATA/wmts_bluemarble")));
        final Name name = new DefaultName("bluemarble");
        final CoverageReference ref = store.create(name);


        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
//        Envelope env = context.getBounds();

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, +180);
        env.setRange(1, -90, 90);
        final int nbscale = 20;
        double[] scales = new double[nbscale];
        scales[0] = env.getSpan(0) / 256 ;
        for(int i=1;i<nbscale;i++){
            scales[i] = scales[i-1] /2;
        }
        

        input.parameter("context").setValue(context);
        input.parameter("extent").setValue(env);
        input.parameter("tilesize").setValue(new Dimension(256, 256));
        input.parameter("scales").setValue(scales);
        input.parameter("container").setValue(ref);
        final org.geotoolkit.process.Process p = desc.createProcess(input);
        
        //use a small predefined dialog
        final ProgressWindow pw = new ProgressWindow(null);
        p.addListener(pw);
        
        //get the result
        final ParameterValueGroup result = p.call();

//        //display the tiled image
//        context.layers().clear();
//        for(final Name n : store.getNames()){            
//            final CoverageReference covref = store.getCoverageReference(n);
//            final MapLayer layer = MapBuilder.createCoverageLayer(
//                    covref, 
//                    new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), 
//                    n.getLocalPart());
//            
//            //display the generated pyramid
//            final PyramidalModel model = (PyramidalModel) covref;
//            System.out.println(model.getPyramidSet());
//            
//            layer.setDescription(SF.description(n.getLocalPart(), n.getLocalPart()));
//            context.layers().add(layer);
//        }
//        
//        JMap2DFrame.show(context);

    }
    
    private static MapContext openData() throws DataStoreException, MalformedURLException {
        
        final ParameterValueGroup params = FileCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        params.parameter(FileCoverageStoreFactory.PATH.getName().getCode()).setValue(new URL("file:/home/jsorel/temp/bluemarble/bluemarble"));
        
        final CoverageStore store = CoverageStoreFinder.open(params);
        
        final MapContext context = MapBuilder.createContext();
        
        for(Name n : store.getNames()){
            final CoverageMapLayer layer = MapBuilder.createCoverageLayer(store.getCoverageReference(n), RandomStyleBuilder.createDefaultRasterStyle(), "n");
            context.layers().add(layer);
        }
        
        return context;
    }
}