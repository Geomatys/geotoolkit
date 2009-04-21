/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Console;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;


/**
 * Convenience static methods for trees operations.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
@Static
public final class Trees {
    /**
     * Do not allows instantiation of this class.
     */
    private Trees() {
    }

    /**
     * Returns the user object from the given tree node. If the given node is an
     * instance of Geotoolkit's {@link org.geotoolkit.gui.swing.tree.TreeNode}, then its
     * {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject() getUserObject()}
     * method is invoked. Otherwise if the given node is an instance of Java's
     * {@link javax.swing.tree.DefaultMutableTreeNode}, then its {@link
     * javax.swing.tree.DefaultMutableTreeNode#getUserObject() getUserObject()}
     * method is invoked. Otherwise this method returns {@code null}.
     *
     * @param node The node for which to get the user object, or {@code null}.
     * @return The user object, or {@code null} if none.
     *
     * @since 3.0
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
     * node which are actually instance of Geotoolkit {@link org.geotoolkit.gui.swing.tree.TreeNode},
     * this method compares the specified {@code value} against the user object returned by the
     * {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject} method.
     *
     * @param  model The tree model to inspect.
     * @param  value User object to compare to {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject}.
     * @return The paths to the specified value, or an empty array if none.
     */
    public static TreePath[] getPathsToUserObject(final TreeModel model, final Object value) {
        final List<TreePath> paths = new ArrayList<TreePath>(8);
        final Object[] path = new Object[8];
        path[0] = model.getRoot();
        getPathsToUserObject(model, value, path, 1, paths);
        return paths.toArray(new TreePath[paths.size()]);
    }

    /**
     * Implémentation de la recherche des chemins. Cette
     * méthode s'appele elle-même d'une façon récursive.
     *
     * @param  model  Modèle dans lequel rechercher le chemin.
     * @param  value  Objet à rechercher dans {@link org.geotoolkit.gui.swing.tree.TreeNode#getUserObject}.
     * @param  path   Chemin parcouru jusqu'à maintenant.
     * @param  length Longueur valide de {@code path}.
     * @param  list   Liste dans laquelle ajouter les {@link TreePath} trouvés.
     * @return {@code path}, ou un nouveau tableau s'il a fallu l'agrandir.
     */
    private static Object[] getPathsToUserObject(final TreeModel model, final Object value,
            Object[] path, final int length, final List<TreePath> list)
    {
        final Object parent = path[length-1];
        if (parent instanceof org.geotoolkit.gui.swing.tree.TreeNode) {
            final Object nodeValue = ((org.geotoolkit.gui.swing.tree.TreeNode) parent).getUserObject();
            if (nodeValue == value || (value!=null && value.equals(nodeValue))) {
                list.add(new TreePath(XArrays.resize(path, length)));
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
        final DefaultMutableTreeNode root = new NamedTreeNode(label, node, true);
        final NamedNodeMap attributes = node.getAttributes();
        final int length = attributes.getLength();
        for (int i=0; i<length; i++) {
            final Node attribute = attributes.item(i);
            if (attribute != null) {
                label = attribute.getNodeName() + "=\"" + attribute.getNodeValue() + '"';
                root.add(new NamedTreeNode(label, attribute, false));
            }
        }
        for (Node child=node.getFirstChild(); child!=null; child=child.getNextSibling()) {
            root.add(xmlToSwing(child));
        }
        return root;
    }

    /**
     * Returns a copy of the tree starting at the given node.
     *
     * @param  node The tree to copy (may be {@code null}).
     * @return A mutable copy of the given tree, or {@code null} if the tree was null.
     *
     * @since 2.5
     */
    public static MutableTreeNode copy(final TreeNode node) {
        if (node == null) {
            return null;
        }
        final String label = node.toString();
        final Object userObject = getUserObject(node);
        final boolean allowsChildren = node.getAllowsChildren();
        final DefaultMutableTreeNode target;
        if (userObject == null || userObject == label) {
            target = new DefaultMutableTreeNode(label, allowsChildren);
        } else {
            target = new NamedTreeNode(label, userObject, allowsChildren);
        }
        @SuppressWarnings("unchecked")
        final Enumeration<TreeNode> children = node.children();
        if (children != null) {
            while (children.hasMoreElements()) {
                target.add(copy(children.nextElement()));
            }
        }
        return target;
    }

    /**
     * Construit une chaîne de caractères qui contiendra le
     * noeud spécifié ainsi que tous les noeuds enfants.
     *
     * @param model  Arborescence à écrire.
     * @param node   Noeud de l'arborescence à écrire.
     * @param buffer Buffer dans lequel écrire le noeud.
     * @param level  Niveau d'indentation (à partir de 0).
     * @param last   Indique si les niveaux précédents sont en train d'écrire leurs derniers items.
     * @return       Le tableau {@code last}, qui peut éventuellement avoir été agrandit.
     */
    private static boolean[] format(final TreeModel model, final Object node,
                                    final Appendable buffer, final int level, boolean[] last,
                                    final String lineSeparator) throws IOException
    {
        for (int i=0; i<level; i++) {
            if (i != level-1) {
                buffer.append(last[i] ? '\u00A0' : '\u2502').append("\u00A0\u00A0\u00A0");
            } else {
                buffer.append(last[i] ? '\u2514' : '\u251C').append("\u2500\u2500\u2500");
            }
        }
        buffer.append(String.valueOf(node)).append(lineSeparator);
        if (level >= last.length) {
            last = Arrays.copyOf(last, level*2);
        }
        final int count = model.getChildCount(node);
        for (int i=0; i<count; i++) {
            last[level] = (i == count-1);
            last = format(model, model.getChild(node,i), buffer, level+1, last, lineSeparator);
        }
        return last;
    }

    /**
     * Writes a graphical representation of the specified tree model in the given buffer.
     *
     * @param  tree          The tree to format.
     * @param  buffer        Where to format the tree.
     * @param  lineSeparator The line separator, or {@code null} for the system default.
     * @throws IOException if an error occured while writting in the given buffer.
     *
     * @since 2.5
     */
    public static void format(final TreeModel tree, final Appendable buffer, String lineSeparator)
            throws IOException
    {
        final Object root = tree.getRoot();
        if (root != null) {
            if (lineSeparator == null) {
                lineSeparator = System.getProperty("line.separator", "\n");
            }
            format(tree, root, buffer, 0, new boolean[64], lineSeparator);
        }
    }

    /**
     * Writes a graphical representation of the specified tree in the given buffer.
     *
     * @param  node          The root node of the tree to format.
     * @param  buffer        Where to format the tree.
     * @param  lineSeparator The line separator, or {@code null} for the system default.
     * @throws IOException if an error occured while writting in the given buffer.
     *
     * @since 2.5
     */
    public static void format(final TreeNode node, final Appendable buffer, String lineSeparator)
            throws IOException
    {
        format(new DefaultTreeModel(node, true), buffer, lineSeparator);
    }

    /**
     * Returns a graphical representation of the specified tree model. This representation can
     * be printed to the {@linkplain System#out standard output stream} (for example) if it uses
     * a monospaced font and supports unicode.
     *
     * @param  tree The tree to format.
     * @return A string representation of the tree, or {@code null} if it doesn't contain any node.
     */
    public static String toString(final TreeModel tree) {
        final Object root = tree.getRoot();
        if (root == null) {
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator", "\n");
        try {
            format(tree, root, buffer, 0, new boolean[64], lineSeparator);
        } catch (IOException e) {
            // Should never happen when writting into a StringBuilder.
            throw new AssertionError(e);
        }
        return buffer.toString();
    }

    /**
     * Returns a graphical representation of the specified tree. This representation can be
     * printed to the {@linkplain System#out standard output stream} (for example) if it uses
     * a monospaced font and supports unicode.
     *
     * @param  node The root node of the tree to format.
     * @return A string representation of the tree, or {@code null} if it doesn't contain any node.
     */
    public static String toString(final TreeNode node) {
        return toString(new DefaultTreeModel(node, true));
    }

    /**
     * Prints the specified tree model to the {@linkplain System#out standard output stream}.
     * This method is mostly a convenience for debugging purpose.
     *
     * @param tree The tree to print.
     *
     * @since 2.4
     */
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
    public static void print(final TreeNode node) {
        print(toString(node));
    }

    /**
     * Prints the given text to the console.
     */
    private static void print(final String text) {
        final PrintWriter out;
        final Console console = System.console();
        if (console != null) {
            out = console.writer();
        } else {
            out = new PrintWriter(System.out);
        }
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
    public static void show(final TreeModel tree, final String title) {
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JScrollPane(new JTree(tree)));
        frame.pack();
        frame.setVisible(true);
    }
}
