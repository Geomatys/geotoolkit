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
import java.awt.event.KeyEvent;
import java.lang.ref.WeakReference;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.AbstractTreePopupItem;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;

/**
 * delete item for JContextTree
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DeleteItem extends AbstractTreePopupItem{

    private WeakReference<MapLayer> layerRef;
    
    /**
     * delete item for jcontexttree
     */
    public DeleteItem(){
        super( MessageBundle.getString("contexttreetable_delete") );
        init();
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
        return uniqueAndType(selection,MapLayer.class);
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
        layerRef = new WeakReference<MapLayer>((MapLayer) node.getUserObject());
        return this;
    }

}
