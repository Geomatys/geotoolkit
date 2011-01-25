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

import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Construct a Tree model for a feature/complexAttribut/property.
 * The tree is build with MutableTreeNode which can contain 
 * property or propertyDescriptor objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureTreeModel extends DefaultTreeModel{

    public FeatureTreeModel(Property property){
        super(new javax.swing.tree.DefaultMutableTreeNode());
        final ValueNode node = new ValueNode(property);
        setRoot(node);
        refresh();
    }

    public void refresh(){
        ((ValueNode)getRoot()).refresh();
    }

    public void removeProperty(final TreePath path){
        final ValueNode node = (ValueNode) path.getLastPathComponent();
        final Object userObject = node.getUserObject();

        //only works if the last node is a property
        if(!(userObject instanceof Property)){
            return;
        }

        final Property prop = (Property) userObject;
        final ComplexAttribute att = getParent(node);
        ((Collection)att.getValue()).remove(prop);

        //update the treenode
        final ValueNode parentNode = (ValueNode) node.getParent();
        final Object parentObject = parentNode.getUserObject();

        if(parentObject instanceof ComplexAttribute){
            //we must replace this node user object by it's propertydescriptor
            node.setUserObject(prop.getDescriptor());
            nodeChanged(node); //fires event
        }else if(parentObject instanceof PropertyDescriptor){
            removeNodeFromParent(node); //fires event
        }

    }

    public void createProperty(final TreePath path){
        final ValueNode node = (ValueNode) path.getLastPathComponent();
        final Object userObject = node.getUserObject();

        //only works if the last node is a property descriptor
        if(!(userObject instanceof PropertyDescriptor)){
            return;
        }

        final PropertyDescriptor desc = (PropertyDescriptor) userObject;
        final int max = desc.getMaxOccurs();
        if(max == 1){
            //we must replace the descriptor by a real property
            final Property prop = FeatureUtilities.defaultProperty(desc);
            node.setUserObject(prop);
            nodeChanged(node);

        }else{

            //we must add a new child if there is space left
            if(node.getChildCount() < max){
                final ComplexAttribute parent = getParent(node);
                final Property prop = FeatureUtilities.defaultProperty(desc);
                //add in the feature
                ((Collection)parent.getValue()).add(prop);
                //insert the node
                final ValueNode n = new ValueNode(prop);
                insertNodeInto(n, node, node.getChildCount());
                n.refresh();
            }
        }
    }

    public ComplexAttribute getParent(MutableTreeNode node){
        final MutableTreeNode parentNode = getParentNode((ValueNode) node);
        if(parentNode != null){
            return (ComplexAttribute) parentNode.getUserObject();
        }
        return null;
    }

    public ValueNode getParentNode(ValueNode node){
        node = (ValueNode) node.getParent();
        if(node == null){
            return null;
        }

        final Object userObject = node.getUserObject();
        if(userObject instanceof ComplexAttribute){
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

            //todo not the best way but at least it's properly refreshed
            //remove all children
            for(int i=getChildCount()-1;i>=0;i--){
                removeNodeFromParent((ValueNode)getChildAt(i));
            }
            

            //create all children
            if(userObject instanceof ComplexAttribute){
                final ComplexAttribute catt = (ComplexAttribute) userObject;
                final ComplexType type = catt.getType();

                for(PropertyDescriptor desc : type.getDescriptors()){
                    final Collection<Property> values = catt.getProperties(desc.getName());

                    final boolean arrayType = desc.getMaxOccurs() > 1;

                    if(values.isEmpty()){
                        final ValueNode n = new ValueNode(desc);
                        insertNodeInto(n, this, getChildCount()); //fires event
                    }else{
                        if(arrayType){
                            final ValueNode n = new ValueNode(desc);
                            insertNodeInto(n, this, getChildCount()); //fires event
                            for(Property val : values){
                                final ValueNode nc = new ValueNode(val);
                                insertNodeInto(nc, n, n.getChildCount()); //fires event
                                nc.refresh();
                            }
                        }else{
                            final ValueNode n = new ValueNode(values.iterator().next());
                            insertNodeInto(n, this, getChildCount()); //fires event
                            n.refresh();
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {

            if(userObject instanceof Property){
                final Name name = ((Property)getUserObject()).getName();
                return String.valueOf(name);
            }else if(userObject instanceof PropertyDescriptor){
                final Name name = ((PropertyDescriptor)getUserObject()).getName();
                return String.valueOf(name);
            }

            return super.toString();
        }

    }

}
