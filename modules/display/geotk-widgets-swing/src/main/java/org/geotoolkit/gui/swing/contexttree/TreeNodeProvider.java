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
package org.geotoolkit.gui.swing.contexttree;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;

/**
 * Provider for ContextTree to render TreeColumn
 * 
 * @author Johann Sorel
 */
public final class TreeNodeProvider extends ComponentProvider<JLabel> {

    private final JContextTree tree;

    
    /**
     * Provider for ContextTree to render TreeColumn
     * 
     * @param tree related JContextTree
     */
    public TreeNodeProvider(JContextTree tree) {
        this.tree = tree;
        rendererComponent = new JLabel();
    }

    /**
     * {@inheritDoc}
     * @param arg0 
     */
    @Override
    protected void configureState(CellContext arg0) {
    }

    /** 
     * {@inheritDoc}
     * @return 
     */
    @Override
    protected JLabel createRendererComponent() {
        return new JLabel();
    }

    /**
     * {@inheritDoc}
     * @param arg0 
     */
    @Override
    protected void format(CellContext arg0) {
        
        if (arg0.getValue() instanceof ContextTreeNode) {
            ContextTreeNode node = (ContextTreeNode) arg0.getValue();
            
            Icon ico = node.getIcon();
            Object value = node.getValue();
            
            rendererComponent.setIcon( (ico == null)? arg0.getIcon(): ico);           
            rendererComponent.setText( (value == null)? "" : value.toString() );
            
            rendererComponent.setToolTipText(node.getToolTip());
                    
            
//            if (node.getUserObject() instanceof MapContext) {
//                if (node.getUserObject().equals(tree.getActiveContext())) {
//                    rendererComponent.setIcon(ICON_CONTEXT_ACTIVE);
//                    rendererComponent.setFont(new Font("Tahoma", Font.BOLD, 10));
//                } else {
//                    rendererComponent.setIcon(ICON_CONTEXT_DESACTIVE);
//                    rendererComponent.setFont(new Font("Tahoma", Font.PLAIN, 10));
//                }
//                rendererComponent.setText(((MapContext) node.getUserObject()).getTitle());
//            } else if (node.getUserObject() instanceof MapLayer) {
//                MapLayer layer = (MapLayer) node.getUserObject();
//
//                rendererComponent.setFont(new Font("Arial", Font.PLAIN, 9));
//
//                //choose icon from datastoretype
//                DataStore ds = layer.getFeatureSource().getDataStore();
//
//                if (layer.getFeatureSource().getSchema().getName().getLocalPart().equals("GridCoverage")) {
//                    rendererComponent.setIcon((layer.isVisible()) ? ICON_LAYER_FILE_RASTER_VISIBLE : ICON_LAYER_FILE_RASTER_UNVISIBLE);
//                } else if (AbstractFileDataStore.class.isAssignableFrom(ds.getClass())) {
//                    rendererComponent.setIcon((layer.isVisible()) ? ICON_LAYER_FILE_VECTOR_VISIBLE : ICON_LAYER_FILE_VECTOR_UNVISIBLE);
//                } else if (JDBC1DataStore.class.isAssignableFrom(ds.getClass())) {
//                    rendererComponent.setIcon((layer.isVisible()) ? ICON_LAYER_DB_VISIBLE : ICON_LAYER_DB_UNVISIBLE);
//                } else {
//                    rendererComponent.setIcon((layer.isVisible()) ? ICON_LAYER_VISIBLE : ICON_LAYER_UNVISIBLE);
//                }
//
//                rendererComponent.setText(layer.getTitle());
//            } else {
//                rendererComponent.setText(arg0.getValue().toString());
//            }

        }
        //should never happen
        else{
            rendererComponent.setIcon(arg0.getIcon());
            rendererComponent.setText(arg0.getValue().toString());
            rendererComponent.setFont(new Font("Tahoma", Font.BOLD, 8));
        }
    }
}
