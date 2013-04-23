/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.gui.swing;

import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.gui.swing.tree.TreeTableNode;


/**
 * A {@link org.jdesktop.swingx.treetable.TreeTableModel} using the Geotk
 * {@link org.geotoolkit.gui.swing.tree.TreeTableNode}. This is called
 * "<cite>Adapter</cite>" because it adapts a Geotk {@code TreeTableNode} for use
 * with <cite>SwingX</cite> {@link org.jdesktop.swingx.treetable.TreeTableNode}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 */
public class TreeTableModelAdapter extends AbstractTreeTableModel {
    /**
     * The most specific superclass for all cell values in the columns, or {@code null}
     * if not yet determined. The length of this array is the number of columns.
     */
    private Class<?>[] types;

    /**
     * Creates a new tree table model with no root. The {@link #setRoot setRoot}
     * method must be invoked before this instance can be used.
     */
    public TreeTableModelAdapter() {
    }

    /**
     * Creates a new tree table model having the given root.
     *
     * @param root The root of the tree table model.
     */
    public TreeTableModelAdapter(final TreeTableNode root) {
        super(root);
    }

    /**
     * Returns the root of the tree.
     *
     * @return The root of the tree.
     */
    @Override
    public TreeTableNode getRoot() {
        return (TreeTableNode) super.getRoot();
    }

    /**
     * Sets a new root for this table model.
     *
     * @param root The new root.
     */
    public void setRoot(final TreeTableNode root) {
        if (root != this.root) {
            this.root = root;
            types = null;
            modelSupport.fireNewRoot();
        }
    }

    /**
     * Returns {@code true} if the given node belong to this tree model.
     */
    private boolean isOwner(TreeNode node) {
        while (node != null) {
            if (node == root) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    /**
     * Returns the most specific superclass for all cell values in the columns.
     * The length of this array is the number of columns.
     *
     * @return The types. This array is not cloned - modify only if the changes
     *         are aimed to be permanent.
     */
    private Class<?>[] types() {
        if (types == null) {
            if ((types = types(getRoot(), null)) == null) {
                types = new Class<?>[0];
            }
        }
        return types;
    }

    /**
     * Returns the most specific superclass for all cell values in the columns, or {@code null}
     * if there is no node. This method invokes itself recursively for every children.
     *
     * @param node  The parent node.
     * @param types The array to update.
     * @return      The updated array.
     */
    private static Class<?>[] types(final TreeTableNode node, Class<?>[] types) {
        if (node != null) {
            final int nc = node.getColumnCount();
            if (types == null) {
                types = new Class<?>[nc];
            } else if (types.length < nc) {
                types = Arrays.copyOf(types, nc);
            }
            for (int i=0; i<nc; i++) {
                types[i] = Classes.findCommonClass(types[i], node.getColumnClass(i));
            }
            for (final Enumeration<? extends TreeNode> it=node.children(); it.hasMoreElements();) {
                types = types((TreeTableNode) it.nextElement(), types);
            }
        }
        return types;
    }

    /**
     * Returns the number of columns in the model. The number of columns shall include
     * the column used for displaying the tree.
     *
     * @return The number of columns in the model.
     */
    @Override
    public int getColumnCount() {
        return types().length;
    }

    /**
     * Returns the most specific superclass for all the cell values in the column.
     *
     * @param  column The index of the column.
     * @return The common ancestor class of the object values in the model.
     */
    @Override
    public Class<?> getColumnClass(int column) {
        return types()[column];
    }

    /**
     * Casts the given object to a {@code TreeTableNode}, or thrown an exception if it is
     * not of the expected type.
     */
    private static TreeTableNode cast(final Object node) throws IllegalArgumentException {
        if (node instanceof TreeTableNode) {
            return (TreeTableNode) node;
        }
        throw new IllegalArgumentException(node == null ?
            Errors.format(Errors.Keys.NULL_ARGUMENT_1, "node") :
            Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_CLASS_3, "node", node.getClass(), TreeTableNode.class));
    }

    /**
     * Gets the value for the given node at given column.
     *
     * @param  node   The node whose value is to be queried.
     * @param  column The column whose value is to be queried.
     * @return The value at the specified cell, or {@code null} if none.
     * @throws IllegalArgumentException If the given node is not an instance of {@link TreeTableNode}.
     */
    @Override
    public Object getValueAt(final Object node, final int column) {
        final TreeTableNode tn = cast(node);
        try {
            return tn.getValueAt(column);
        } catch (IndexOutOfBoundsException e) {
            if (column >= 0 && column < getColumnCount()) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Sets the value for the given node at the given column index.
     *
     * @param  value  The new value.
     * @param  node   The node whose value is to be changed.
     * @param  column The column whose value is to be changed.
     * @throws IllegalArgumentException If the given node is not an instance of {@link TreeTableNode}.
     */
    @Override
    public void setValueAt(final Object value, final Object node, final int column) {
        cast(node).setValueAt(value, column);
    }

    /**
     * Returns {@code true} if the given node is a leaf.
     *
     * @param  node A node in the tree.
     * @return {@code true} if the given node is a leaf.
     * @throws IllegalArgumentException If the given node is not an instance of {@link TreeTableNode}.
     */
    @Override
    public boolean isLeaf(final Object node) {
        return cast(node).isLeaf();
    }

    /**
     * Returns the number of children of parent. Returns 0 if the node is a leaf or if
     * it has no children.
     *
     * @param  node The parent node in the tree.
     * @return The number of children of the given node.
     * @throws IllegalArgumentException If the given node is not an instance of {@link TreeTableNode}.
     */
    @Override
    public int getChildCount(final Object node) {
        return cast(node).getChildCount();
    }

    /**
     * Returns the child of the given node at index index in the node's child array.
     *
     * @param  node  The parent node in the tree.
     * @param  index Index of the child to get.
     * @return The child at the given index.
     * @throws IllegalArgumentException If the given node is not an instance of {@link TreeTableNode}.
     */
    @Override
    public TreeNode getChild(final Object node, int index) {
        return cast(node).getChildAt(index);
    }

    /**
     * Returns the index of the given child in the given node. If either the node or the child
     * is null, or if the node or the child don't belong to this tree model, returns {@code -1}.
     *
     * @param  node  A note in the tree.
     * @param  child The node we are interested in.
     * @return The index of the child in the node, or -1.
     */
    @Override
    public int getIndexOfChild(final Object node, final Object child) {
        if (node instanceof TreeTableNode && child instanceof TreeNode) {
            final TreeTableNode tn = (TreeTableNode) node;
            if (isOwner(tn)) {
                return tn.getIndex((TreeNode) child);
            }
        }
        return -1;
    }
}
