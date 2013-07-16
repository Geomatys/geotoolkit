/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.gui.swing.tree;

import javax.swing.tree.TreeNode;


/**
 * A default tree model with the {@link #toString()} method overridden.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
public class DefaultTreeModel extends javax.swing.tree.DefaultTreeModel {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -2060236329590860790L;

    /**
     * Creates a tree in which any node can have children.
     *
     * @param root The root of the tree.
     */
    public DefaultTreeModel(final TreeNode root) {
        super(root);
    }

    /**
     * Creates a tree specifying whether any node can have children,
     * or whether only certain nodes can have children.
     *
     * @param root The root of the tree.
     * @param asksAllowsChildren {@code false} if any node can have children, or
     *        {@code true} if each node is asked to see if it can have children.
     */
    public DefaultTreeModel(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    /**
     * Returns a string representation of this tree as defined by {@link Trees#toString(TreeModel)}.
     */
    @Override
    public String toString() {
        return Trees.toString(this);
    }
}
