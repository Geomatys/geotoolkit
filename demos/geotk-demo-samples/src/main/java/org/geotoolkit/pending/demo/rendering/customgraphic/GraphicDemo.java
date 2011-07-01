
package org.geotoolkit.pending.demo.rendering.customgraphic;

import java.awt.BorderLayout;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.io.CoverageIO;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.JNavigationBar;
import org.geotoolkit.gui.swing.go2.decoration.JClassicNavigationDecoration;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.referencing.operation.TransformException;


public class GraphicDemo {


    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws DataStoreException, NoninvertibleTransformException,
                                                  TransformException, IOException {

        final MapContext context = createContext();


        final JMap2D jmap = new JMap2D();
        final JNavigationBar navBar = new JNavigationBar(jmap);
        final J2DCanvas canvas = jmap.getCanvas();
        jmap.getContainer().setContext(context);
        jmap.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.CLASSIC));
        jmap.getCanvas().getController().setVisibleArea(context.getBounds());


        //our custom graphic object
        final SquaresGraphic graphic = new SquaresGraphic(canvas);

        //add the graphic in the scene
        canvas.getContainer().add(graphic);


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

        //create a coverage layer
        final GridCoverageReader reader = CoverageIO.createSimpleReader(new File("data/clouds.jpg"));
        final MutableStyle coverageStyle = SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(reader, coverageStyle,"background");

        //add all layers in the context
        context.layers().add(coverageLayer);
        return context;
    }

}
