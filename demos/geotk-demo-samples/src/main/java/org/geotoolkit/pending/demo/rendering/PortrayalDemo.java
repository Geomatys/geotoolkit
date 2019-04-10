
package org.geotoolkit.pending.demo.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.StyleFactory;

public class PortrayalDemo {

    private static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);

    public static void main(String[] args) throws DataStoreException, IOException, PortrayalException, URISyntaxException {
        Demos.init();


        //generate a map context
        final MapContext context = createContext();

        //prepare the rendering context
        final CanvasDef canvasdef = new CanvasDef(new Dimension(800, 600), Color.WHITE);
        final SceneDef scenedef = new SceneDef(context);
        final ViewDef viewdef = new ViewDef(context.getBounds());

        //generate the image
        final BufferedImage img = DefaultPortrayalService.portray(canvasdef, scenedef, viewdef);

        //show the image
        final JFrame frm = new JFrame();
        frm.setContentPane(new JLabel(new ImageIcon(img)));
        frm.pack();
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);

    }

    private static MapContext createContext() throws DataStoreException, URISyntaxException {
        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a feature layer
        final FeatureCollection features = openShapeFile();
        final MutableStyle featureStyle = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final FeatureMapLayer featureLayer = MapBuilder.createFeatureLayer(features, featureStyle);

        //create a coverage layer
//        final GridCoverageReader reader = openWorldFile();
//        final MutableStyle coverageStyle = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
//        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, 0, coverageStyle,"background");

        //add all layers in the context
//        context.layers().add(coverageLayer);
        context.layers().add(featureLayer);

        return context;
    }

    private static FeatureCollection openShapeFile() throws DataStoreException, URISyntaxException {
        final Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put("path", PortrayalDemo.class.getResource("/data/world/Countries.shp").toURI());
        final FeatureStore store = (FeatureStore) DataStores.open(params);
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(store.getNames().iterator().next());
        final FeatureCollection collection = session.getFeatureCollection(query);
        return collection;
    }

    private static GridCoverageReader openWorldFile() throws DataStoreException, URISyntaxException {
        WorldFileImageReader.Spi.registerDefaults(null);
        File cloudFile = new File(PortrayalDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        return CoverageIO.createSimpleReader(cloudFile);
    }

}
