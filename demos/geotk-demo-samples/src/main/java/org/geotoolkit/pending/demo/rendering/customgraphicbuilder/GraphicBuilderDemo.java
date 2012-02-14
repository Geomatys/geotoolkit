
package org.geotoolkit.pending.demo.rendering.customgraphicbuilder;

import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.JNavigationBar;
import org.geotoolkit.gui.swing.go2.decoration.JClassicNavigationDecoration;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.referencing.operation.TransformException;


public class GraphicBuilderDemo {


    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws DataStoreException, NoninvertibleTransformException,
                                                  TransformException, IOException {

        final MapContext context = createContext();

        final JMap2D jmap = new JMap2D();
        final JNavigationBar navBar = new JNavigationBar(jmap);
        jmap.getContainer().setContext(context);
        jmap.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.CLASSIC));
        jmap.getCanvas().getController().setVisibleArea(context.getBounds());
        jmap.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        //display map.
        final JFrame frm = new JFrame();
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,jmap);
        panel.add(BorderLayout.NORTH,navBar);
        frm.setContentPane(panel);
        frm.setSize(800, 600);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);

    }

    private static MapContext createContext() throws DataStoreException  {
        WorldFileImageReader.Spi.registerDefaults(null);
        
        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a feature layer
        Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put( "url", GraphicBuilderDemo.class.getResource("/data/weather/stations2.shp") );
        DataStore store = DataStoreFinder.get(params);
        FeatureCollection fs = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));
        MutableStyle style = SF.style();
        MapLayer layer = MapBuilder.createFeatureLayer(fs, style);
        layer.setDescription(SF.description("stations", ""));
        layer.setName("stations");

        //create a coverage layer
        final GridCoverageReader reader = CoverageIO.createSimpleReader(new File("data/clouds.jpg"));
        final MutableStyle coverageStyle = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, coverageStyle,"background");



        //set our graphic builder
        layer.graphicBuilders().add(new LinksGraphicBuilder());

        //add all layers in the context
        context.layers().add(coverageLayer);
        context.layers().add(layer);
        return context;
    }

}
