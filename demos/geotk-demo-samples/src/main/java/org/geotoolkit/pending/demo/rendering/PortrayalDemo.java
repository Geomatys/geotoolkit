
package org.geotoolkit.pending.demo.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageReaders;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;

public class PortrayalDemo {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws DataStoreException, IOException, PortrayalException {
        


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

    private static MapContext createContext() throws DataStoreException  {
        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a feature layer
        final FeatureCollection features = openShapeFile();
        final MutableStyle featureStyle = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        final FeatureMapLayer featureLayer = MapBuilder.createFeatureLayer(features, featureStyle);

        //create a coverage layer
        final GridCoverageReader reader = openWorldFile();
        final MutableStyle coverageStyle = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, coverageStyle,"background");

        //add all layers in the context
        context.layers().add(coverageLayer);
        context.layers().add(featureLayer);

        return context;
    }

    private static FeatureCollection openShapeFile() throws DataStoreException {
        final Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put("url", PortrayalDemo.class.getResource("/data/world/Countries.shp"));

        final DataStore store = DataStoreFinder.getDataStore(params);
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(store.getNames().iterator().next());
        final FeatureCollection collection = session.getFeatureCollection(query);
        return collection;
    }

    private static GridCoverageReader openWorldFile() throws CoverageStoreException {
        WorldFileImageReader.Spi.registerDefaults(null);
        return GridCoverageReaders.createSimpleReader(new File("data/clouds.jpg"));
    }

}
