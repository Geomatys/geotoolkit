/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Johann Sorel
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


package org.geotoolkit.gui.swing.propertyedit;

import java.util.Collection;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 * Construct a Tree model for a feature/complexAttribut/property.
 * The tree is build with MutableTreeNode which can contain
 * property or propertyDescriptor objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FeatureTreeModel extends DefaultTreeModel{

    public FeatureTreeModel(Feature property){
        super(new javax.swing.tree.DefaultMutableTreeNode());
        final ValueNode node = new ValueNode(property);
        setRoot(node);
        refresh();
    }

    public void refresh(){
        ((ValueNode)getRoot()).refresh();
    }

    public void removeProperty(final TreePath path){
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
    }

    public void createProperty(final TreePath path){
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
    }

    public Feature getParent(MutableTreeNode node){
        final MutableTreeNode parentNode = getParentNode((ValueNode) node);
        if(parentNode != null){
            return (Feature) parentNode.getUserObject();
        }
        return null;
    }

    public ValueNode getParentNode(ValueNode node){
        node = (ValueNode) node.getParent();
        if(node == null){
            return null;
        }

        final Object userObject = node.getUserObject();
        if(userObject instanceof Feature){
            return node;
        }else{
            return getParentNode(node);
        }
    }

    private class ValueNode extends DefaultMutableTreeNode{

        public ValueNode(Object obj) {
            super(obj);
        }

        public synchronized void refresh(){
            //TODO editor obsolete with new feature api
            throw new RuntimeException("Editor do not support new feature API yet.");
        }

    }

}
