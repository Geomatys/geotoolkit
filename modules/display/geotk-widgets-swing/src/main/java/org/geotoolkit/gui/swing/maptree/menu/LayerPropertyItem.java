/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.maptree.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.maptree.TreePopupItem;
import org.geotoolkit.gui.swing.propertyedit.JPropertyDialog;
import org.geotoolkit.gui.swing.propertyedit.LayerCRSPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;

/**
 * Default popup control for property page of MapLayer, use for JContextTreePopup
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class LayerPropertyItem extends JMenuItem implements TreePopupItem {

    private WeakReference<MapLayer> layerRef;
    private final List<PropertyPane> lst = new ArrayList<PropertyPane>();

    /** 
     * Creates a new instance of DefaultContextPropertyPop 
     */
    public LayerPropertyItem() {
        super(MessageBundle.getString("contexttreetable_properties"));
        init();
    }

    /**
     * set the list of PropertyPanel to use
     * @param liste
     */
    public void setPropertyPanels(List<PropertyPane> liste) {
        lst.clear();
        lst.addAll(liste);
    }

    private void init() {
        lst.add(new LayerGeneralPanel());
        lst.add(new LayerCRSPropertyPanel());
        lst.add(new LayerFilterPropertyPanel());
        lst.add(new LayerStylePropertyPanel());

        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(layerRef == null) return;

                MapLayer layer = layerRef.get();
                if(layer == null) return;


                JPropertyDialog.showDialog(lst, layer);

            }
        });
    }

    @Override
    public boolean isValid(TreePath[] selection) {
        if (selection.length == 1 && selection[0].getLastPathComponent() instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
            return ( node.getUserObject() instanceof MapLayer ) ;
        }
        return false;
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
        layerRef = new WeakReference<MapLayer>((MapLayer) node.getUserObject());
        return this;
    }
}
