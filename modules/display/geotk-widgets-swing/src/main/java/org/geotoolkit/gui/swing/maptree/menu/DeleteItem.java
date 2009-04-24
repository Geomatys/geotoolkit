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
import java.awt.event.KeyEvent;

import java.lang.ref.WeakReference;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.maptree.JContextTree;
import org.geotoolkit.gui.swing.maptree.TreePopupItem;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;

/**
 * delete item for JContextTree
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DeleteItem extends JMenuItem implements TreePopupItem{

    private WeakReference<MapLayer> layerRef;

    private final JContextTree tree;
    
    /**
     * delete item for jcontexttree
     */
    public DeleteItem(final JContextTree tree){
        super( MessageBundle.getString("contexttreetable_delete") );
        init();
        this.tree = tree;
        
    }

    private void init(){
        setIcon( IconBundle.getInstance().getIcon("16_delete") );
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(layerRef == null) return;

                MapLayer layer = layerRef.get();
                if(layer == null) return;

                tree.getContext().layers().remove(layer);
            }
        }
        );
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
