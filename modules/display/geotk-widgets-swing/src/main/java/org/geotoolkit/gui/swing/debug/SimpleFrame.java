/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.debug;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.go.J2DMap;
import org.geotoolkit.gui.swing.go.J2DMapVolatile;
import org.geotoolkit.gui.swing.go.control.JNavigationBar;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.RandomStyleFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author sorel
 */
public class SimpleFrame {

    public static void main(String[] args) throws IOException {

        final String shapefile = "file:///home/sorel/GIS_DATA/JEU_VILLE/ALTI_LIGNE_ISO.SHP";

        //load the shapefile
        final Map<String,Object> params = new HashMap<String,Object>();
        params.put( "url", shapefile ); //url is a magic key for the datastore finder
        final DataStore store           = DataStoreFinder.getDataStore(params);
        final FeatureSource source      = store.getFeatureSource(store.getTypeNames()[0]);

        //make the map
        final MutableStyle style    = new RandomStyleFactory().createRandomVectorStyle(source);
        final MapLayer layer        = MapBuilder.createFeatureLayer(source, style);
        final MapContext context    = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        context.layers().add(layer);

        //make the frame
        final JFrame frame = new JFrame();
        final J2DMapVolatile map = new J2DMapVolatile();
        map.getContainer().setContext(context);
        final JNavigationBar bar = new JNavigationBar(map);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.NORTH,bar);
        panel.add(BorderLayout.CENTER,map);
        frame.setContentPane(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
