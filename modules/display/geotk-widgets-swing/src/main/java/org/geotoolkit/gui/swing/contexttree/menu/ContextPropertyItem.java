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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.AbstractTreePopupItem;
import org.geotoolkit.gui.swing.propertyedit.ContextCRSPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.ContextGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.JPropertyDialog;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapContext;

/**
 * Default popup control for property page of MapContext, use for JContextTreePopup 
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ContextPropertyItem extends AbstractTreePopupItem{
    
    private WeakReference<MapContext> contextRef;
    
    /** 
     * Creates a new instance of DefaultContextPropertyPop 
     */
    public ContextPropertyItem() {
        super( MessageBundle.getString("contexttreetable_properties")  );
        init();
    }
    
    private void init(){
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(contextRef == null) return;

                MapContext context = contextRef.get();
                if(context == null) return;

                ArrayList<PropertyPane> lst = new ArrayList<PropertyPane>();
                lst.add(new ContextGeneralPanel());
                lst.add(new ContextCRSPropertyPanel());
                JPropertyDialog.showDialog(lst, context);
            }
        }
        );
    }
            
    @Override
    public boolean isValid(TreePath[] selection) {
        return uniqueAndType(selection,MapContext.class);
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
        contextRef = new WeakReference<MapContext>((MapContext) node.getUserObject()) ;
        return this;
    }
    
}
