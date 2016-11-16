/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.render2d.control.edition;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JList;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.util.LayerListRenderer;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * ComboBox displaying the feature layers available in the map.
 *
 * @author Johann Sorel
 * @module
 */
public class JLayerComboBox extends JList implements ContextListener{

    private final ContextListener.Weak weaklistener = new ContextListener.Weak(this);
    private JMap2D map = null;

    public JLayerComboBox() {
        this(null);
    }

    public JLayerComboBox(final JMap2D map){
        setCellRenderer(new LayerListRenderer());
        setMap(map);
    }

    public void setMap(JMap2D map) {
        if(map == this.map){
            return;
        }

        unregisterListener();
        this.map = map;
        registerListener();

        if(map != null){
            map.getContainer().addPropertyChangeListener(this);
        }

        reloadModel();
    }

    public JMap2D getMap() {
        return map;
    }

    private void registerListener(){
        final MapContext context = getContext();
        if(context != null){
            weaklistener.registerSource(context);
        }
    }

    private void unregisterListener(){
        weaklistener.unregisterAll();
    }

    private MapContext getContext(){
        if(map != null){
            final ContextContainer2D cc = map.getContainer();
            if(cc != null){
                return cc.getContext();
            }
        }
        return null;
    }

    private void reloadModel(){
        final MapContext context = getContext();
        final List<Object> objects = new ArrayList<Object>();

        if(context != null){
            for (MapLayer mapLayer : context.layers()) {
                if (mapLayer.isVisible() || mapLayer.isSelectable()) {
                    objects.add(mapLayer);
                }
            }
        }

        setModel(new ListComboBoxModel(objects));

        final Dimension minSize = getMinimumSize();
        if(minSize.width>150){
            minSize.width = 150;
            setMinimumSize(minSize);
        }

    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {
        reloadModel();
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
        reloadModel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(ContextContainer2D.CONTEXT_PROPERTY.equals(evt.getPropertyName())){
            //map context changed
            unregisterListener();
            reloadModel();
            registerListener();
        }
    }


}
