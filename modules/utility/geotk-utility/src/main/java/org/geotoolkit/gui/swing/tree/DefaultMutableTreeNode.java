/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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


/**
 * General-purpose node in a tree data structure. This default implementation implements
 * Geotoolkit {@link MutableTreeNode} interface, which inherits a {@code getUserObject()}
 * method. This method is provided in Swing {@link javax.swing.tree.DefaultMutableTreeNode}
 * implementation but seems to have been forgotten in all Swing interfaces.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class DefaultMutableTreeNode extends javax.swing.tree.DefaultMutableTreeNode
        implements MutableTreeNode
{
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -8782548896062360341L;

    /**
     * Creates a tree node that has no parent and no children, but which allows children.
     */
    public DefaultMutableTreeNode() {
        super();
    }

    /**
     * Creates a tree node with no parent, no children, but which allows
     * children, and initializes it with the specified user object.
     *
     * @param userObject an Object provided by the user that constitutes the node's data
     */
    public DefaultMutableTreeNode(Object userObject) {
        super(userObject);
    }

    /**
     * Creates a tree node with no parent, no children, initialized with
     * the specified user object, and that allows children only if specified.
     *
     * @param userObject an Object provided by the user that constitutes the node's data
     * @param allowsChildren if true, the node is allowed to have child nodes -- otherwise,
     *        it is always a leaf node
     */
    public DefaultMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }
}
