/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.contexttree.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.AbstractTreePopupItem;
import org.geotoolkit.gui.swing.propertyedit.JPropertyDialog;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;

/**
 * Default popup control for editing a given style of the layer.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class LayerStyleItem extends AbstractTreePopupItem {

    private WeakReference<Object> styleRef;
    private WeakReference<MapLayer> layerRef;
    private final List<PropertyPane> lst = new ArrayList<PropertyPane>();

    /** 
     * Creates a new instance of DefaultContextPropertyPop 
     */
    public LayerStyleItem() {
        super(MessageBundle.getString("contexttreetable_properties"));
        init();
    }

    private void init() {
        final JAdvancedStylePanel stylePanel = new JAdvancedStylePanel();        

        lst.add(stylePanel);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(layerRef == null) return;
                if(styleRef == null) return;

                final MapLayer layer = layerRef.get();
                if(layer == null) return;

                stylePanel.setLayer(layer);
                stylePanel.parse(styleRef.get());


                JPropertyDialog.showDialog(lst, null, false);

            }
        });
    }

    @Override
    public boolean isValid(final TreePath[] selection) {
        return uniqueAndType(selection,Rule.class)
            || uniqueAndType(selection,FeatureTypeStyle.class);
    }

    @Override
    public Component getComponent(final TreePath[] selection) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
        styleRef = new WeakReference<Object>(node.getUserObject());

        MapLayer layer = null;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        while(layer == null && parent != null){
            Object cdt = parent.getUserObject();
            if(cdt instanceof MapLayer){
                layer = (MapLayer) cdt;
            }else{
                parent = (DefaultMutableTreeNode) parent.getParent();
            }
        }
        layerRef = new WeakReference<MapLayer>(layer);

        return this;
    }
}
