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
package org.geotools.gui.swing.contexttree.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import org.geotools.gui.swing.contexttree.ContextTreeNode;
import org.geotools.gui.swing.propertyedit.JPropertyDialog;
import org.geotools.gui.swing.propertyedit.LayerCRSPropertyPanel;
import org.geotools.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotools.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotools.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotools.gui.swing.propertyedit.PropertyPane;
import org.geotools.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;

/**
 * Default popup control for property page of MapLayer, use for JContextTreePopup
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class LayerPropertyItem extends JMenuItem implements TreePopupItem {

    private MapLayer layer;
    private List<PropertyPane> lst = new ArrayList<PropertyPane>();

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

                JPropertyDialog.showDialog(lst, layer);

            }
        });
    }

    @Override
    public boolean isValid(TreePath[] selection) {
        if (selection.length == 1) {
            ContextTreeNode node = (ContextTreeNode) selection[0].getLastPathComponent();            
            return ( node.getUserObject() instanceof MapLayer ) ;
        }
        return false;
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        ContextTreeNode node = (ContextTreeNode) selection[0].getLastPathComponent();  
        layer = (MapLayer) node.getUserObject() ;
        return this;
    }
}
