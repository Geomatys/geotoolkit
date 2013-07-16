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

import java.util.Locale;
import java.util.Enumeration;
import org.opengis.util.InternationalString;
import org.apache.sis.util.Localized;
import org.geotoolkit.lang.Workaround;


/**
 * General-purpose node in a tree data structure. This default implementation implements
 * the Geotk {@link MutableTreeNode} interface, which inherits a {@code getUserObject()}
 * method. This method is provided in the Swing {@link javax.swing.tree.DefaultMutableTreeNode}
 * implementation but seems to have been forgotten in all Swing interfaces.
 * <p>
 * In addition, the {@link #toString()} method has been overridden in order to process
 * {@link InternationalString} specially: if the value returned by {@link #getLocale()}
 * is non-null, then that value is used for getting a {@link String} from the
 * {@code InternationalString} object.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.17
 *
 * @since 2.0
 * @module
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
@Workaround(library="JDK", version="1.4")
public class DefaultMutableTreeNode extends javax.swing.tree.DefaultMutableTreeNode
        implements MutableTreeNode, Localized
{
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -8782548896062360341L;

    /**
     * Creates a tree node that has no parent and no children, but which allows children.
     */
    public DefaultMutableTreeNode() {
        super(); // NOSONAR: Class name intentionally shadow superclass name.
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

    /**
     * Returns the children of this node as an {@code Enumeration}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<? extends javax.swing.tree.TreeNode> children() {
        return super.children();
    }

    /**
     * Returns the locale to use for the value returned by {@link #toString()}. The default
     * implementation returns the locale of the {@linkplain #getParent() parent}, if any,
     * or {@code null} otherwise.
     *
     * @since 3.17
     */
    @Override
    public Locale getLocale() {
        javax.swing.tree.TreeNode parent = getParent();
        while (parent != null) {
            if (parent instanceof org.apache.sis.util.Localized) {
                return ((org.apache.sis.util.Localized) parent).getLocale();
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Returns the string value of the {@linkplain #userObject user object}, or {@code null}
     * if none. This method is invoked by {@link javax.swing.JTree} for populating the tree
     * widget. The string can be localized if the following conditions are meet:
     * <p>
     * <ul>
     *   <li>the user object is an instance of {@link InternationalString},</li>
     *   <li>the locale returned by {@link #getLocale()} is non-null.</li>
     * </ul>
     * <p>
     * In such case, this method returns the value of {@link InternationalString#toString(Locale)}.
     *
     * @return The localized string value of the {@linkplain #userObject user object},
     *         or {@code null} if none.
     */
    @Override
    public String toString() {
        if (userObject instanceof InternationalString) {
            final Locale locale = getLocale();
            if (locale != null) {
                return ((InternationalString) userObject).toString(locale);
            }
        }
        return super.toString();
    }
}
