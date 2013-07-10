/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.lang.Workaround;


/**
 * Defines the requirements for a tree node object that can change. It may changes by adding or
 * removing child nodes, or by changing the contents of a user object stored in the node.
 * <p>
 * This interface inherits the {@link #getUserObject getUserObject()} method from Geotk
 * {@link TreeNode}. This is needed because the Swing {@link javax.swing.tree.MutableTreeNode}
 * interface defines a {@link #setUserObject(Object) setUserObject(Object)} method but doesn't
 * define or inherit any {@code getUserObject()}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
@Workaround(library="JDK", version="1.4")
public interface MutableTreeNode extends javax.swing.tree.MutableTreeNode, TreeNode {
}
