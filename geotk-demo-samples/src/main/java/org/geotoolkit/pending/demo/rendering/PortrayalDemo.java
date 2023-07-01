
package org.geotoolkit.pending.demo.rendering;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;

public class PortrayalDemo {

    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public static void main(String[] args) throws DataStoreException, IOException, PortrayalException, URISyntaxException {
        Demos.init();


        //generate a map context
        final MapLayers context = createContext();

        //prepare the rendering context
        final CanvasDef canvasdef = new CanvasDef(new Dimension(800, 600), context.getEnvelope().get());
        canvasdef.setBackground(Color.WHITE);
        final SceneDef scenedef = new SceneDef(context);

        //generate the image
        final RenderedImage img = DefaultPortrayalService.portray(canvasdef, scenedef);

        //show the image
        final JFrame frm = new JFrame();
        frm.setContentPane(new JLabel(new ImageIcon(BufferedImages.createImage(img.getWidth(),img.getHeight(),img))));
        frm.pack();
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);

    }

    private static MapLayers createContext() throws DataStoreException, URISyntaxException {
        //create a map context
        final MapLayers context = MapBuilder.createContext();

        //create a feature layer
        final FeatureSet features = openShapeFile();
        final MutableStyle featureStyle = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final MapLayer featureLayer = MapBuilder.createLayer(features);
        featureLayer.setStyle(featureStyle);

        //create a coverage layer
//        final GridCoverageResource reader = openWorldFile();
//        final MutableStyle coverageStyle = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
//        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, 0, coverageStyle,"background");

        //add all layers in the context
//        context.layers().add(coverageLayer);
        context.getComponents().add(featureLayer);

        return context;
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
