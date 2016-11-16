/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2009, Johann Sorel
 *    (C) 2010 - 2013, Geomatys
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

package org.geotoolkit.gui.swing.render2d.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JPanel;

import javax.swing.SwingConstants;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;

import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel
 * @module
 */
public class JLayerBandMenu extends JMenu implements ContextListener{

    private final JNavigator navigator;
    private WeakReference<JMap2D> map = null;

    public JLayerBandMenu(final JNavigator navigator){
        super(MessageBundle.format("layers"));
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

        checkBands();
        if(map != null){
            map.getContainer().addPropertyChangeListener(this);
            final MapContext context = map.getContainer().getContext();
            if(context != null){
                context.addContextListener(new ContextListener.Weak(this));
                layerChange(new CollectionChangeEvent<MapLayer>(this, context.layers(), CollectionChangeEvent.ITEM_ADDED, null,null));
            }
        }
    }

    private void checkBands(){
        for(JNavigatorBand b : new ArrayList<JNavigatorBand>(navigator.getBands())){
            navigator.getBands().remove(b);
        }
    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {

        final JMap2D map2d = getMap();
        if(map2d == null){
            return;
        }

        final MapContext context = map2d.getContainer().getContext();
        if(context == null){
            return;
        }

        removeAll();
        final List<MapLayer> layers = new ArrayList<MapLayer>();
        parseItem(context, layers);

        for(final MapLayer layer : layers){
            add(new LayerPane(layer));
        }

        if(event == null){
            return;
        }

        final int type = event.getType();
        if(type == CollectionChangeEvent.ITEM_ADDED && navigator.getModel().getCRS() instanceof TemporalCRS){
            //automaticaly add temporal layer bands.
            for(MapLayer ml : event.getItems()){
                CoordinateReferenceSystem crs = null;
                if(ml instanceof CoverageMapLayer){
                    final CoverageMapLayer cml = (CoverageMapLayer) ml;
                    final CoverageReference ref = cml.getCoverageReference();
                    try {
                        final GridCoverageReader reader = ref.acquireReader();
                        crs = reader.getGridGeometry(ref.getImageIndex()).getCoordinateReferenceSystem();
                        ref.recycle(reader);
                    } catch (Exception ex) {
                        //we tryed ...
                    }
                }else if(ml instanceof FeatureMapLayer){
                    final FeatureMapLayer fml = (FeatureMapLayer) ml;
                    crs = FeatureExt.getCRS(fml.getCollection().getFeatureType());
                }
                if(crs != null){
                    final TemporalCRS tc = CRS.getTemporalComponent(ml.getBounds().getCoordinateReferenceSystem());
                    if(tc != null){
                        //add it in the bands
                        final JLayerBand band = new JLayerBand(ml);
                        navigator.getBands().add(band);
                        for(Component c : getMenuComponents()){
                            if(!(c instanceof LayerPane)){
                                continue;
                            }

                            final LayerPane lp = (LayerPane) c;
                            if(lp.layer == ml){
                                lp.update();
                                break;
                            }
                        }
                    }
                }
            }

        }else if(type == CollectionChangeEvent.ITEM_REMOVED){

            //update the navigator bands
            for(JNavigatorBand b : new ArrayList<JNavigatorBand>(navigator.getBands())){
                if(b instanceof JLayerBand){
                    final JLayerBand lb = (JLayerBand) b;
                    if(!layers.contains(lb.getLayer())){
                        navigator.getBands().remove(b);
                    }
                }
            }
        }

    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
        final Collection<MapItem> items = event.getItems();
        final List<MapLayer> layers = new ArrayList<MapLayer>();
        for (MapItem item : items) {
            parseItem(item, layers);
        }

        final CollectionChangeEvent newEvent = new CollectionChangeEvent(
                event.getSource(), layers, event.getType(), event.getRange(), event);

        layerChange(newEvent);
    }

    protected void parseItem (final MapItem source, final List<MapLayer> destination) {
        if (source instanceof MapLayer) {
            destination.add((MapLayer) source);
        } else {
            for (MapItem child : source.items()) {
                parseItem(child, destination);
            }
        }
    }

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
        private final JButton colorButton = new JButton();
        private final MapLayer layer;

        private LayerPane(final MapLayer layer){
            super(new FlowLayout(FlowLayout.LEFT,4,1));
            this.layer = layer;
            box.addActionListener(this);
            colorButton.setHorizontalTextPosition(SwingConstants.LEFT);
            colorButton.addActionListener(this);
            colorButton.setBorderPainted(true);
            colorButton.setContentAreaFilled(false);
            colorButton.setOpaque(true);
            colorButton.setPreferredSize(new Dimension(20, 20));
            colorButton.setBackground(RandomStyleBuilder.randomColor());
            add(colorButton);
            add(box);
            layer.addItemListener(new Weak(this));
            update();
        }

        private void update(){
            box.setText(getLayerName());

            for(final JNavigatorBand band : navigator.getBands()){
                if(band instanceof JLayerBand){
                    final JLayerBand lb = (JLayerBand) band;
                    if(lb.getLayer().equals(this.layer)){
                        final Color c = lb.getColor();
                        colorButton.setBackground(c);
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
                    band.setColor(colorButton.getBackground());
                    navigator.getBands().add(band);
                }else{
                    for(final Object band : navigator.getBands().toArray()){
                        if(band instanceof JLayerBand){
                            final JLayerBand lb = (JLayerBand) band;
                            if(lb.getLayer().equals(this.layer)){
                                navigator.getBands().remove(lb);
                            }
                        }
                    }
                }
            }else{
                Color c = colorButton.getBackground();
                c = JColorChooser.showDialog(this, "", c);
                if(c != null){
                    colorButton.setBackground(c);
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
