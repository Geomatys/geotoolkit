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
package org.geotoolkit.metadata;

import org.geotoolkit.gui.swing.tree.NamedTreeNode;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import static org.geotoolkit.metadata.PropertyTree.*;


/**
 * A node in a {@link PropertyTree}, used only if the user asked for a tree table instead than
 * an ordinary tree. The tree table is made of two columns:
 * <p>
 * <ul>
 *   <li>The metadata property name, also accessible by {@link #getName()}.</li>
 *   <li>The metadata property value, as the string representation of the {@link #getUserObject()}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
final class PropertyTreeNode extends NamedTreeNode implements TreeTableNode {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3252053429799823479L;

    /**
     * The string representation of {@link #getUserObject()}, or {@code null} if none.
     */
    String valueAsText;

    /**
     * Creates a tree node with no parent, no children, but which allows
     * children, and initializes it with the specified user object.
     *
     * @param name The node name to be returned by {@link #toString()}.
     * @param userObject an Object provided by the user that constitutes the node's data
     */
    PropertyTreeNode(final String name, final Object value) {
        super(name, value);
    }

    /**
     * Creates a new leaf node with a name derived from the given number, and the given value.
     * This new node does not allow children.
     */
    PropertyTreeNode(final String number, final String valueAsText, final Object value) {
        super(OPEN_BRACKET + number + CLOSE_BRACKET, value, false);
        this.valueAsText = valueAsText;
    }

    /**
     * Returns the number of columns in this node.
     */
    @Override
    public int getColumnCount() {
        return (valueAsText != null) ? 2 : 1;
    }

    /**
     * Returns the type of values in the given column.
     */
    @Override
    public Class<?> getColumnClass(final int column) {
        return String.class;
    }

    /**
     * Returns the value in the given column.
     */
    @Override
    public Object getValueAt(final int column) {
        switch (column) {
            case 0:  return getName();
            case 1:  return valueAsText;
            default: return null;
        }
    }

    /**
     * Unsupported operation for now, since the nodes are not editable.
     */
    @Override
    public void setValueAt(final Object value, final int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Declares that this node is not editable.
     */
    @Override
    public boolean isEditable(final int column) {
        return false;
    }
}
