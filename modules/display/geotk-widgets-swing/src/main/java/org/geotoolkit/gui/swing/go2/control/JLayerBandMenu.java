/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gui.swing.go2.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JPanel;

import javax.swing.SwingConstants;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;

import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class JLayerBandMenu extends JMenu implements ContextListener{

    private final JNavigator navigator;
    private WeakReference<JMap2D> map = null;

    public JLayerBandMenu(final JNavigator navigator){
        super(MessageBundle.getString("layers"));      
        this.navigator = navigator;
    }
    
    @Override
    public boolean isEnabled() {
        return getMap() != null;
    }

    public JMap2D getMap() {
        if(map != null){
            return map.get();
        }
        return null;
    }

    public void setMap(final JMap2D map) {
        this.map = new WeakReference<JMap2D>(map);
        
        if(map != null){
            map.getContainer().addPropertyChangeListener(this);
            final MapContext context = map.getContainer().getContext();
            if(context != null){
                context.addContextListener(new ContextListener.Weak(this));
            }
        }
        checkBands();
        layerChange(null);
    }

    private void checkBands(){
        for(JNavigatorBand b : new ArrayList<JNavigatorBand>(navigator.getBands())){
            if(b instanceof JLayerBand){
                navigator.removeBand(b);
            }
        }        
        if(map == null){
            return;
        }    
    }
    
    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {
        removeAll();
                
        final JMap2D map2d = getMap();
        if(map2d == null){
            return;
        }
        
        final MapContext context = map2d.getContainer().getContext();
        if(context == null){
            return;
        }
        
        final List<MapLayer> layers = context.layers();
        
        for(final MapLayer layer : layers){
            add(new LayerPane(layer));
        }
        
        //update the navigator bands
        for(JNavigatorBand b : new ArrayList<JNavigatorBand>(navigator.getBands())){
            if(b instanceof JLayerBand){
                final JLayerBand lb = (JLayerBand) b;
                if(!layers.contains(lb.getLayer())){
                    navigator.removeBand(b);
                }
            }
        }
        
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final String propName = evt.getPropertyName();
        if(propName.equals(ContextContainer2D.CONTEXT_PROPERTY)){
            if(map == null){
               return;
            }
            final JMap2D map2d = map.get();
            if(map2d == null){
                return;
            }
            
            final MapContext context = map2d.getContainer().getContext();
            if(context != null){
                context.addContextListener(new ContextListener.Weak(this));
            }
            checkBands();
        }
    }

    private final class LayerPane extends JPanel implements ActionListener,org.geotoolkit.map.ItemListener{
    
        private final JCheckBox box = new JCheckBox();
        private final JButton label = new JButton(IconBundle.getIcon("16_style"));
        private final MapLayer layer;
        
        private LayerPane(final MapLayer layer){
            super(new BorderLayout());
            this.layer = layer;
            add(BorderLayout.WEST,box);
            box.addActionListener(this);
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.addActionListener(this);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
            add(BorderLayout.CENTER,label);
            layer.addItemListener(new Weak(this));
            update();
        }
        
        private void update(){
            label.setText(getLayerName());
            
            for(final JNavigatorBand band : navigator.getBands()){
                if(band instanceof JLayerBand){
                    final JLayerBand lb = (JLayerBand) band;
                    if(lb.getLayer().equals(this.layer)){
                        final Color c = lb.getColor();
                        label.setForeground(c);
                        box.setSelected(true);
                        return;
                    }
                }
            }
            
            box.setSelected(false);
        }
        
        private String getLayerName(){
            final Description desc = layer.getDescription();
            if(desc != null){
                final InternationalString title = desc.getTitle();
                if(title != null){
                    return title.toString();
                }            
            }
            final String name = layer.getName();
            return (name == null)? "" : name;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            if(e.getSource().equals(box)){
                if(box.isSelected()){
                    for(final Object band : navigator.getBands().toArray()){
                        if(band instanceof JLayerBand){
                            final JLayerBand lb = (JLayerBand) band;
                            if(lb.getLayer().equals(this.layer)){
                                //already exist
                                return;
                            }
                        }
                    }
                    final JLayerBand band = new JLayerBand(layer);
                    band.setColor(label.getForeground());
                    navigator.addBand(band);
                }else{
                    for(final Object band : navigator.getBands().toArray()){
                        if(band instanceof JLayerBand){
                            final JLayerBand lb = (JLayerBand) band;
                            if(lb.getLayer().equals(this.layer)){
                                navigator.removeBand(lb);
                            }
                        }
                    }
                }       
            }else{
                Color c = label.getForeground();
                c = JColorChooser.showDialog(this, "", c);
                if(c != null){
                    label.setForeground(c);
                    for(final JNavigatorBand band : navigator.getBands()){
                        if(band instanceof JLayerBand){
                            final JLayerBand lb = (JLayerBand) band;
                            if(lb.getLayer().equals(this.layer)){
                                lb.setColor(c);
                                return;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void itemChange(CollectionChangeEvent<MapItem> event) {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(MapItem.NAME_PROPERTY.equalsIgnoreCase(evt.getPropertyName())){
                update();
            }
        }
        
    }
    
}
