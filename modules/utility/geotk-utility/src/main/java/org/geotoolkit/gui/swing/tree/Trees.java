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

import java.io.PrintWriter;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.lang.reflect.Array;
import javax.swing.JTree;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.nio.IOUtilities;


/**
 * Convenience static methods for trees operations. This class provides methods for performing
 * a {@linkplain #copy copy} of a tree and for {@linkplain #xmlToSwing converting from XML}.
 * It can also {@linkplain #print print} a tree in a format like the following example:
 *
 * {@preformat text
 *   Node #1
 *   ├───Node #2
 *   │   └───Node #4
 *   └───Node #3
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.0
 * @module
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
public final class Trees extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private Trees() {
    }

    /**
     * Returns the user object from the given tree node. If the given node is an
     * instance of Geotk {@link org.geotoolkit.gui.swing.tree.TreeNode}, then its
     * {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject() getUserObject()}
     * method is invoked. Otherwise if the given node is an instance of Java
     * {@link javax.swing.tree.DefaultMutableTreeNode}, then its {@link
     * javax.swing.tree.DefaultMutableTreeNode#getUserObject() getUserObject()}
     * method is invoked. Otherwise this method returns {@code null}.
     *
     * @param node The node for which to get the user object, or {@code null}.
     * @return The user object, or {@code null} if none.
     *
     * @since 3.00
     */
    public static Object getUserObject(final TreeNode node) {
        if (node instanceof org.geotoolkit.gui.swing.tree.TreeNode) {
            return ((org.geotoolkit.gui.swing.tree.TreeNode) node).getUserObject();
        }
        if (node instanceof javax.swing.tree.DefaultMutableTreeNode) {
            return ((javax.swing.tree.DefaultMutableTreeNode) node).getUserObject();
        }
        return null;
    }

    /**
     * Returns the path to the specified
     * {@linkplain org.geotoolkit.gui.swing.tree.TreeNode#getUserObject user object}. For each tree
     * node which are actually instance of Geotk {@link org.geotoolkit.gui.swing.tree.TreeNode},
     * this method compares the specified {@code value} against the user object returned by the
     * {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject} method.
     *
     * @param  model The tree model to inspect.
     * @param  value User object to compare to {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject}.
     * @return The paths to the specified value, or an empty array if none.
     */
    public static TreePath[] getPathsToUserObject(final TreeModel model, final Object value) {
        final List<TreePath> paths = new ArrayList<>(8);
        final Object[] path = new Object[8];
        path[0] = model.getRoot();
        getPathsToUserObject(model, value, path, 1, paths);
        return paths.toArray(new TreePath[paths.size()]);
    }

    /**
     * Implementation of the path search. This method invokes itself recursively.
     *
     * @param  model  The tree model in which to search for a path.
     * @param  value  The expected {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject()}.
     * @param  path   The path found up to date.
     * @param  length The number of valid elements in the {@code path} array.
     * @param  list   Where to add new {@link TreePath} as they are found.
     * @return {@code path}, or a new array if it was necessary to increase its size.
     */
    private static Object[] getPathsToUserObject(final TreeModel model, final Object value,
            Object[] path, final int length, final List<TreePath> list)
    {
        final Object parent = path[length-1];
        if (parent instanceof org.geotoolkit.gui.swing.tree.TreeNode) {
            final Object nodeValue = ((org.geotoolkit.gui.swing.tree.TreeNode) parent).getUserObject();
            if (nodeValue == value || (value!=null && value.equals(nodeValue))) {
                list.add(new TreePath(ArraysExt.resize(path, length)));
            }
        }
        final int count = model.getChildCount(parent);
        for (int i=0; i<count; i++) {
            if (length >= path.length) {
                path = Arrays.copyOf(path, length << 1);
            }
            path[length] = model.getChild(parent, i);
            path = getPathsToUserObject(model, value, path, length+1, list);
        }
        return path;
    }

    /**
     * Returns a tree representation of the given object.
     * This method processes the following types especially:
     * <p>
     * <ul>
     *   <li>If the given object is an instance of {@link Node}, then this method delegates
     *       to {@link #xmlToSwing(Node)}.</li>
     *   <li>If the given object is an instance of {@link Iterable}, {@link Map} or an array,
     *       then a node is created with the name of the implemented interface (for example
     *       {@code "List"} or {@code "Set"}) and each iterated elements is added as a child
     *       of the above-cited node.</li>
     *   <li>If the given object is an instance of {@link java.util.Map.Entry}, then this
     *       method returns a {@link NamedTreeNode} which contain the entry key as the
     *       {@linkplain NamedTreeNode#getName() name} and the entry value as
     *       {@linkplain TreeNode#getUserObject() user object}.</li>
     *   <li>Otherwise this method returns a single {@link DefaultMutableTreeNode} which contain
     *       the given object as {@linkplain TreeNode#getUserObject() user object}.</li>
     * </ul>
     * <p>
     * Together with {@link #toString(TreeNode)}, this method provides a convenient way to print
     * the content of a XML document for debugging purpose.
     *
     * @param  object The array, collection or single object to format.
     * @return The given object as a Swing tree.
     *
     * @since 3.17
     */
    public static MutableTreeNode objectToSwing(final Object object) {
        final DefaultMutableTreeNode node;
        Iterator<?> iterator = null;
        Class<?> baseInterface = null;
        if (object instanceof Iterable<?>) {
            baseInterface = Iterable.class;
            iterator = ((Iterable<?>) object).iterator();
        } else if (object instanceof Map<?,?>) {
            baseInterface = Map.class;
            iterator = ((Map<?,?>) object).entrySet().iterator();
        }
        if (iterator != null) {
            final Class<?>[] types = Classes.getLeafInterfaces(object.getClass(), baseInterface);
            node = new DefaultMutableTreeNode(Classes.getShortName(types.length != 0 ? types[0] : null));
            while (iterator.hasNext()) {
                node.add(objectToSwing(iterator.next()));
            }
        } else {
            if (object.getClass().isArray()) {
                node = new DefaultMutableTreeNode("Array");
                final int length = Array.getLength(object);
                for (int i=0; i<length; i++) {
                    node.add(objectToSwing(Array.get(object, i)));
                }
            } else if (object instanceof Node) {
                return xmlToSwing((Node) object);
            } else {
                if (object instanceof Map.Entry<?,?>) {
                    final Map.Entry<?,?> entry = (Map.Entry<?,?>) object;
                    node = new NamedTreeNode(String.valueOf(entry.getKey()), entry.getValue());
                } else {
                    node = new DefaultMutableTreeNode(object);
                }
                node.setAllowsChildren(false);
            }
        }
        return node;
    }

    /**
     * Creates a Swing root tree node from a XML root tree node. Together with
     * {@link #toString(TreeNode)}, this method provides a convenient way to print
     * the content of a XML document for debugging purpose.
     *
     * @param  node The XML root node.
     * @return The given XML node as a Swing node.
     */
    public static MutableTreeNode xmlToSwing(final Node node) {
        String label = node.getNodeName();
        final String value = node.getNodeValue();
        if (value != null) {
            label += "=\"" + value + '"';
        }
        final DefaultMutableTreeNode root = new NamedTreeNode(label, node);
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final int length = attributes.getLength();
            for (int i=0; i<length; i++) {
                final Node attribute = attributes.item(i);
                if (attribute != null) {
                    label = attribute.getNodeName() + "=\"" + attribute.getNodeValue() + '"';
                    root.add(new NamedTreeNode(label, attribute, false));
                }
            }
        }
        for (Node child=node.getFirstChild(); child!=null; child=child.getNextSibling()) {
            root.add(xmlToSwing(child));
        }
        return root;
    }

    /**
     * Returns a copy of the tree starting at the given node. The references to the
     * {@linkplain org.geotoolkit.gui.swing.tree.TreeNode#getUserObject() user objects}
     * (if any) and the {@linkplain org.geotoolkit.gui.swing.tree.TreeNode#toString()
     * string representations} are copied verbalism.
     *
     * @param  node The tree to copy (may be {@code null}).
     * @return A mutable copy of the given tree, or {@code null} if the given tree was null.
     *
     * @since 2.5
     */
    public static MutableTreeNode copy(final TreeNode node) {
        return copy(node, null);
    }

    /**
     * Returns a copy of a subset of the tree starting at the given node. The references to the
     * {@linkplain org.geotoolkit.gui.swing.tree.TreeNode#getUserObject() user objects} in the
     * copied nodes can be modified by the filter.
     *
     * @param  node The tree to copy (may be {@code null}).
     * @param  filter An object filtering the node to copy, or {@code null} if none.
     * @return A mutable copy of the given tree, or {@code null} if the given tree was null or if
     *         the given node is not {@linkplain TreeNodeFilter#accept accepted} by the filter.
     *
     * @since 3.04
     */
    public static MutableTreeNode copy(final TreeNode node, final TreeNodeFilter filter) {
        if (node == null || (filter != null && !filter.accept(node))) {
            return null;
        }
        final String label = node.toString();
        Object userObject = getUserObject(node);
        if (filter != null) {
            userObject = filter.convertUserObject(node, userObject);
        }
        final boolean allowsChildren = node.getAllowsChildren();
        final DefaultMutableTreeNode target;
        if (userObject == null || userObject == label) {
            target = new DefaultMutableTreeNode(label, allowsChildren);
        } else {
            target = new NamedTreeNode(label, userObject, allowsChildren);
        }
        @SuppressWarnings("unchecked")
        final Enumeration<? extends TreeNode> children = node.children();
        if (children != null) {
            while (children.hasMoreElements()) {
                final MutableTreeNode child = copy(children.nextElement(), filter);
                if (child != null) {
                    target.add(child);
                }
            }
        }
        return target;
    }

    /**
     * Returns a graphical representation of the specified tree model. This representation can
     * be printed to the {@linkplain System#out standard output stream} (for example) if it uses
     * a monospaced font and supports unicode.
     *
     * @param  tree The tree to format.
     * @return A string representation of the tree.
     */
    public static String toString(final TreeModel tree) {
        final TreeFormat tf = new TreeFormat();
        tf.setTableFormatEnabled(true);
        final StringBuilder buffer = new StringBuilder();
        tf.format(tree, buffer);
        return buffer.toString();
    }

    /**
     * Returns a graphical representation of the specified tree. This representation can be
     * printed to the {@linkplain System#out standard output stream} (for example) if it uses
     * a monospaced font and supports unicode.
     *
     * @param  node The root node of the tree to format.
     * @return A string representation of the tree.
     */
    public static String toString(final TreeNode node) {
        final TreeFormat tf = new TreeFormat();
        tf.setTableFormatEnabled(true);
        final StringBuilder buffer = new StringBuilder();
        tf.format(node, buffer);
        return buffer.toString();
    }

    /**
     * Prints the specified tree model to the {@linkplain System#out standard output stream}.
     * This method is mostly a convenience for debugging purpose.
     *
     * @param tree The tree to print.
     *
     * @since 2.4
     */
    @Debug
    public static void print(final TreeModel tree) {
        print(toString(tree));
    }

    /**
     * Prints the specified tree to the {@linkplain System#out standard output stream}.
     * This method is mostly a convenience for debugging purpose.
     *
     * @param  node The root node of the tree to print.
     *
     * @since 2.4
     */
    @Debug
    public static void print(final TreeNode node) {
        print(toString(node));
    }

    /**
     * Prints the given text to the console.
     */
    @Debug
    private static void print(final String text) {
        final PrintWriter out = IOUtilities.standardPrintWriter();
        out.println(text);
        out.flush();
    }

    /**
     * Display the given tree in a Swing frame. This is a convenience
     * method for debugging purpose only.
     *
     * @param node The root of the tree to display in a Swing frame.
     * @param title The frame title, or {@code null} if none.
     *
     * @since 2.5
     */
    @Debug
    public static void show(final TreeNode node, final String title) {
        show(new DefaultTreeModel(node, true), title);
    }

    /**
     * Display the given tree in a Swing frame. This is a convenience
     * method for debugging purpose only.
     *
     * @param tree The tree to display in a Swing frame.
     * @param title The frame title, or {@code null} if none.
     *
     * @since 2.5
     */
    @Debug
    public static void show(final TreeModel tree, final String title) {
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JScrollPane(new JTree(tree)));
        frame.pack();
        frame.setVisible(true);
    }
}
