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
package org.geotoolkit.gui.swing.tree;

import javax.swing.tree.TreeNode;


/**
 * A filter used during the {@linkplain Trees#copy(TreeNode, TreeNodeFilter) copy} of a tree.
 * This filter can be used for including only a subset of the original nodes, and to change
 * the {@linkplain org.geotoolkit.gui.swing.tree.TreeNode#getUserObject() user object}
 * assigned to that node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @see Trees#copy(TreeNode, TreeNodeFilter)
 *
 * @since 3.04
 * @module
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
public interface TreeNodeFilter {
    /**
     * Returns {@code true} if the given node should be copied.
     *
     * @param  node The tree node to test for inclusion.
     * @return {@code true} if the given node should be included in the copy.
     */
    boolean accept(TreeNode node);

    /**
     * Returns the user object to assign to the copied node.
     *
     * @param  node       The original node to copy.
     * @param  userObject The user object of the original node.
     * @return The user object to assign to the copied node, or {@code null} if none.
     */
    Object convertUserObject(TreeNode node, Object userObject);
}
