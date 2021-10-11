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

import java.util.Enumeration;
import org.geotoolkit.lang.Workaround;


/**
 * Defines the requirements for an object that can be used as a tree node in a
 * {@link javax.swing.JTree}. This interface adds the {@code getUserObject()} to
 * Swing interface, which seems to have been forgotten in J2SE.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.03
 *
 * @since 2.0
 * @module
 */
@Workaround(library="JDK", version="1.4")
public interface TreeNode extends javax.swing.tree.TreeNode {
    /**
     * Returns this node's user object.
     *
     * @return the Object stored at this node by the user
     */
    Object getUserObject();

    /**
     * Returns the children of this node as an {@code Enumeration}.
     *
     * @return The children.
     */
    @Override
    Enumeration<? extends javax.swing.tree.TreeNode> children();

    /**
     * Returns a string representation of this node suitable for a user interface.
     * {@link javax.swing.JTree} uses this method for formatting the text to display
     * in the widget.
     *
     * @return The text to display in the {@code JTree} for this node.
     */
    @Override
    String toString();
}
