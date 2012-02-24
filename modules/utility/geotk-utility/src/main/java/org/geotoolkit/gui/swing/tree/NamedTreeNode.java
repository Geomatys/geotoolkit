/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Locale;
import org.opengis.util.InternationalString;


/**
 * A tree node with a name which may be different than the {@linkplain #userObject user object}.
 * Because the {@link javax.swing.JTree} component invokes the {@link #toString()} method for
 * populating the tree widget, this class is useful when the label to display is different than
 * the value of {@code userObject.toString()}.
 *
 * {@section Localization}
 * Every constructors in this class expect a name of type {@link CharSequence}. The names are
 * typically instances of {@link String}, but instances of {@link InternationalString} are
 * also accepted. In the later case, the {@link #toString()} method may return a localized
 * string depending on the return value of the {@link #getLocale() getLocale()} method. By
 * default, the later is the locale of the {@linkplain #getParent() parent} node.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.17
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
     * The node label to be returned by {@link #toString()}.
     */
    private CharSequence name;

    /**
     * Creates a tree node that has no parent and no children, but which allows children.
     *
     * @param name The node name to be returned by {@link #toString()}.
     */
    public NamedTreeNode(final CharSequence name) {
        super();
        this.name = freeze(name);
    }

    /**
     * Creates a tree node with no parent, no children, but which allows
     * children, and initializes it with the specified user object.
     *
     * @param name The node name to be returned by {@link #toString()}.
     * @param userObject an Object provided by the user that constitutes the node's data
     */
    public NamedTreeNode(final CharSequence name, final Object userObject) {
        super(userObject);
        this.name = freeze(name);
    }

    /**
     * Creates a tree node with no parent, no children, initialized with
     * the specified user object, and that allows children only if specified.
     *
     * @param name The node name to be returned by {@link #toString()}.
     * @param userObject an Object provided by the user that constitutes the node's data
     * @param allowsChildren if true, the node is allowed to have child nodes. Otherwise,
     *        it is always a leaf node
     */
    public NamedTreeNode(final CharSequence name, Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
        this.name = freeze(name);
    }

    /**
     * Ensures that the given name is either an instance of {@link InternationalString},
     * {@link String} or {@code null}. This is mostly a safety against change of
     * {@link StringBuilder} content.
     */
    private static CharSequence freeze(final CharSequence name) {
        if (name == null || name instanceof InternationalString) {
            return name;
        }
        return name.toString();
    }

    /**
     * Returns the name of this node as an instance of {@link String} or {@link InternationalString}.
     *
     * @return The name, or {@code null} if none.
     *
     * @since 3.17
     */
    public CharSequence getName() {
        return name;
    }

    /**
     * Sets the name of this node. While this method accepts null value (because Swing
     * {@link javax.swing.tree.DefaultMutableTreeNode#toString()} is designed that way),
     * it is highly recommended to set only non-null values.
     *
     * @param name The new name of this node.
     *
     * @since 3.17
     */
    public void setName(final CharSequence name) {
        this.name = freeze(name);
    }

    /**
     * Returns the name given at construction time. If that name is an instance of
     * {@link InternationalString} and the {@link #getLocale() getLocale()} method
     * returns a non-null value, then the {@link InternationalString#toString(Locale)}
     * value is returned.
     */
    @Override
    public String toString() {
        if (name instanceof InternationalString) {
            final Locale locale = getLocale();
            return (locale != null) ? ((InternationalString) name).toString(locale) : name.toString();
        }
        return (String) name; // May be null.
    }
}
