/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.debug;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotoolkit.gui.swing.go.J2DMapVolatile;
import org.geotoolkit.gui.swing.go.control.JCoordinateBar;
import org.geotoolkit.gui.swing.go.control.JNavigationBar;
import org.geotoolkit.util.RandomStyleFactory;

import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author eclesia
 */
public class CanvasDebug extends JFrame{

    public static final MutableStyleFactory STYLE_FACTORY = new DefaultStyleFactory();
    public static final RandomStyleFactory RANDOM_FACTORY = new RandomStyleFactory();
    
    private final J2DMapVolatile map = new J2DMapVolatile();
    private final JNavigationBar bar = new JNavigationBar();
    private final JCoordinateBar coord = new JCoordinateBar();
    private final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
    
    
    public CanvasDebug(){
        
        bar.setMap(map);
        coord.setMap(map);
        
        final JToolBar pane = new JToolBar();
        
        pane.add( new AbstractAction("SET CONTEXT") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
//                try {
                    map.getContainer().setContext(context);
//                } catch (IOException ex) {
//                    Logger.getLogger(CanvasDebug.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (TransformException ex) {
//                    Logger.getLogger(CanvasDebug.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        });
        
        pane.add(new AbstractAction("ADD LAYER") {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try{
                    Map params = new HashMap<String,Object>();
                    File shape = new File("/home/eclesia/gis_data/normandy/TRONCON_ROUTE.SHP");
                    params.put( "url", shape.toURI().toURL() );
                    DataStore store = DataStoreFinder.getDataStore(params);
                    FeatureSource<SimpleFeatureType, SimpleFeature> fs = store.getFeatureSource(store.getTypeNames()[0]);
                    MutableStyle style = STYLE_FACTORY.style(STYLE_FACTORY.polygonSymbolizer());
                    MapLayer layer = MapBuilder.createFeatureLayer(fs, style);
                    layer.setDescription(STYLE_FACTORY.description("depts", ""));
                    context.layers().add(layer);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        
        
        
        final JPanel content = new JPanel(new BorderLayout());
        
        
        content.add(BorderLayout.NORTH,bar);
        content.add(BorderLayout.SOUTH,coord);
        content.add(BorderLayout.CENTER,map);
        content.add(BorderLayout.EAST,pane);
        
        
        
        setContentPane(content);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    
    
    public static void main(String[] args){
        new CanvasDebug();
    }
    
}
