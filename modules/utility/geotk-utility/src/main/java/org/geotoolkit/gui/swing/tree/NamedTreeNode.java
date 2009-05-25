/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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

import javax.swing.JTree;


/**
 * A tree node with a name which may be different than the user object. The {@link JTree}
 * component invokes the {@link #toString} method for populating the tree widget. This class
 * overrides the default implementation (<code>{@linkplain #getUserObject userObject}.toString</code>)
 * with a custom label.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class NamedTreeNode extends DefaultMutableTreeNode {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -5052321314347001298L;

    /**
     * The node label to be returned by {@link #toString}.
     */
    private final String name;

    /**
     * Creates a tree node that has no parent and no children, but which allows children.
     *
     * @param name The node name to be returned by {@link #toString}.
     */
    public NamedTreeNode(final String name) {
        super();
        this.name = name;
    }

    /**
     * Creates a tree node with no parent, no children, but which allows
     * children, and initializes it with the specified user object.
     *
     * @param name The node name to be returned by {@link #toString}.
     * @param userObject an Object provided by the user that constitutes the node's data
     */
    public NamedTreeNode(final String name, final Object userObject) {
        super(userObject);
        this.name = name;
    }

    /**
     * Creates a tree node with no parent, no children, initialized with
     * the specified user object, and that allows children only if specified.
     *
     * @param name The node name to be returned by {@link #toString}.
     * @param userObject an Object provided by the user that constitutes the node's data
     * @param allowsChildren if true, the node is allowed to have child nodes -- otherwise,
     *        it is always a leaf node
     */
    public NamedTreeNode(final String name, Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
        this.name = name;
    }

    /**
     * Returns this node label. This method is invoked by {@link JTree} for populating
     * the tree widget.
     */
    @Override
    public String toString() {
        return name;
    }
}
