
package org.geotoolkit.pending.demo.rendering.customdecoration;

import java.awt.BorderLayout;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.io.CoverageIO;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.JNavigationBar;
import org.geotoolkit.gui.swing.go2.decoration.JClassicNavigationDecoration;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.referencing.operation.TransformException;


public class MapDecorationDemo {


    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws DataStoreException, NoninvertibleTransformException,
                                                  TransformException, IOException {
        Demos.init();

        final MapContext context = createContext();

        final JMap2D jmap = new JMap2D();
        final JNavigationBar navBar = new JNavigationBar(jmap);
        jmap.getContainer().setContext(context);
        jmap.getCanvas().setVisibleArea(context.getBounds());


        //our custom decoration object
        jmap.addDecoration(new CellDecoration());
        jmap.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.NEO));


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
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(new File("data/clouds.jpg"));

        //add all layers in the context
        context.layers().add(coverageLayer);
        return context;
    }

}
