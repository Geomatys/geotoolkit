package org.geotoolkit.pending.demo.processing;

import java.net.URL;
import org.geotoolkit.coverage.*;
import org.geotoolkit.style.DefaultStyleFactory;
import org.opengis.feature.type.Name;
import java.awt.Dimension;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.coverage.filestore.*;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gui.swing.ProgressWindow;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;

/**
 * Create a pyramid from a MapContext.
 */
public class MapTilingDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Throwable {
        Demos.init();


        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a feature layer
        final FeatureCollection features = openShapeFile();
        final MutableStyle featureStyle = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final FeatureMapLayer featureLayer = MapBuilder.createFeatureLayer(features, featureStyle);

        //add all layers in the context
        context.layers().add(featureLayer);



        //get the description of the process we want
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "mapcontextpyramid");
        System.out.println(desc.getInputDescriptor());

        //create a coverage store where the pyramid wil be stored
        final XMLCoverageStoreFactory factory = new XMLCoverageStoreFactory();
        final CoverageStore store = factory.createNew(Collections.singletonMap("path", new URL("file:/tmp/pyramid2")));
        final Name name = new DefaultName("countrietiles");
        final CoverageReference ref = store.create(name);


        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        Envelope env = context.getBounds();

        env = CRS.transform(env, CRS.decode("EPSG:3857"));
        final int nbscale = 6;
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
        
        //or plain output
//        p.addListener(new ProcessListener() {
//            
//            private final NumberFormat NF = NumberFormat.getPercentInstance();
//            
//            @Override
//            public void started(ProcessEvent event) {
//                showEvent(event, "Start :");
//            }
//            @Override
//            public void progressing(ProcessEvent event) {
//                showEvent(event, "");
//            }
//            @Override
//            public void completed(ProcessEvent event) {
//                showEvent(event, "Complete :");
//            }
//            @Override
//            public void failed(ProcessEvent event) {
//                showEvent(event, "Fail :");
//            }
//            
//            private void showEvent(ProcessEvent event, String message){
//                System.out.println(NF.format(event.getProgress()) +" "+message+" " + String.valueOf(event.getTask()));
//            }
//            
//        });

        //get the result
        final ParameterValueGroup result = p.call();
        System.out.println(result);

        //display the tiled image
        context.layers().clear();
        for(final Name n : store.getNames()){            
            final CoverageReference covref = store.getCoverageReference(n);
            final MapLayer layer = MapBuilder.createCoverageLayer(
                    covref, 
                    new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), 
                    n.getLocalPart());
            
            //display the generated pyramid
            final PyramidalModel model = (PyramidalModel) covref;
            System.out.println(model.getPyramidSet());
            
            layer.setDescription(SF.description(n.getLocalPart(), n.getLocalPart()));
            context.layers().add(layer);
        }
        
        JMap2DFrame.show(context);

    }
    
    private static FeatureCollection openShapeFile() throws DataStoreException, MalformedURLException {
        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", MapTilingDemo.class.getResource("/data/world/Countries.shp"));

        final DataStore store = DataStoreFinder.open(params);
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(store.getNames().iterator().next());
        final FeatureCollection collection = session.getFeatureCollection(query);
        return collection;
    }
}