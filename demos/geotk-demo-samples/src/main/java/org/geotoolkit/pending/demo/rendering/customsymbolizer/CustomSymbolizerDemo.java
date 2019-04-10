

package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import java.io.File;
import java.net.URISyntaxException;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gui.javafx.render2d.FXMapFrame;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.style.StyleFactory;

public class CustomSymbolizerDemo {

     private static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createContext();

        FXMapFrame.show(context);

    }

    private static MapContext createContext() throws DataStoreException, URISyntaxException {
        WorldFileImageReader.Spi.registerDefaults(null);

        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a coverage layer
        File cloudFile = new File(CustomSymbolizerDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        final MutableStyle coverageStyle = SF.style(new CrystallizeSymbolizer(2));
        final MapLayer coverageLayer = MapBuilder.createCoverageLayer(cloudFile);
        coverageLayer.setStyle(coverageStyle);

        //add all layers in the context
        context.layers().add(coverageLayer);
        return context;
    }

}
