/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.go.control.creation;

import java.beans.PropertyChangeEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.misc.Render.LayerListRenderer;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.CollectionChangeEvent;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author eclesia
 */
public class JContextCombobox extends JComboBox{

    private GoMap2D map = null;
    private MapContext context = null;

    private final ContextListener listener = new ContextListener() {

        @Override
        public void propertyChange(PropertyChangeEvent arg0) {
        }

        @Override
        public void layerChange(CollectionChangeEvent<MapLayer> arg0) {
            setModel(new ListComboBoxModel(context.layers()));
        }
    };

    public JContextCombobox() {
        setRenderer(new LayerListRenderer());
    }

    public void setMap(GoMap2D map) {
        this.map = map;
        refresh();
    }

    public GoMap2D getMap() {
        return map;
    }

    private void refresh(){

        context.removeContextListener(listener);

        if(map == null){
            setModel(new DefaultComboBoxModel());
        }else{
            AbstractContainer2D container = map.getCanvas().getContainer();

            if(container instanceof ContextContainer2D){
                ContextContainer2D cc = (ContextContainer2D) container;
                context = cc.getContext();

                if(context != null){
                    setModel(new ListComboBoxModel(context.layers()));
                    context.addContextListener(listener);
                }else{
                    setModel(new DefaultComboBoxModel());
                }

            }else{
                setModel(new DefaultComboBoxModel());
            }
        }
    }

}
