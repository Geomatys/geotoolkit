/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.util.Locale;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import org.w3c.dom.Node;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.AnyConverter;


/**
 * A view of an {@link IIOMetadata} instance as a tree table. The tree structure is determined by
 * a {@link IIOMetadataFormat}, which must be provided to the constructor. After the construction,
 * different instances of {@code IIOMetadata} can be given to this {@code MetadataTreeTable} in
 * order to generate tables with different values. If no {@code IIOMetadata} instance is given,
 * then this object represents only the structure of the format with its restrictions (expected
 * type, range of values, <cite>etc.</cite>) but no values.
 * <p>
 * The root of the tree is obtained by {@link #getRootNode()}. The table contains at most
 * {@value #COLUMN_COUNT} columns, described below:
 * <ul>
 *   <li>A human-readeable name of the nodes</li>
 *   <li>A description of the node, or the above name if none.</li>
 *   <li>The node value (this column is omitted if the tree is for
 *       {@link IIOMetadataFormat} instead than {@link IIOMetadata})</li>
 *   <li>The simple class names of value types</li>
 *   <li>The range of occurences (how many time the node can be repeated)</li>
 *   <li>A description of valid values (either as a range or as an enumeration)</li>
 *   <li>The default value</li>
 * </ul>
 * <p>
 * This class works with arbitrary implementations of {@code IIOMetadata};
 * it doesn't need to be the specialized implementations defined in Geotk.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @see org.geotoolkit.gui.swing.image.IIOMetadataPanel
 *
 * @since 3.04
 * @module
 */
public class MetadataTreeTable {
    /**
     * The number of columns in the table, when every columns are present. Note that the
     * <cite>value</cite> column is omitted if this {@code MetadataTreeTable} is given only
     * a {@link IIOMetadataFormat} without any {@link IIOMetadata} instance for providing
     * the actual values.
     */
    public static final int COLUMN_COUNT = 7;

    /**
     * The column which contains the values. This is the column which is ommited if this
     * table is for {@link IIOMetadataFormat} instead than {@link IIOMetadata}.
     *
     * @since 3.05
     */
    public static final int VALUE_COLUMN = 2;

    /**
     * The expected format of {@code IIOMetadata} instances.
     */
    final IIOMetadataFormat format;

    /**
     * The current metadata, or {@code null} if none.
     */
    private IIOMetadata metadata;

    /**
     * The root node of the current {@linkplain #metadata}.
     */
    private Node root;

    /**
     * The root of the tree table. Will be created only when first needed.
     */
    private transient TreeTableNode tree;

    /**
     * The Locale for which localization will be attempted.
     */
    private Locale locale;

    /**
     * The converter from {@link String} to values.
     */
    final AnyConverter converters;

    /**
     * Creates a new metadata tree for the given format.
     *
     * @param format The expected format of {@code IIOMetadata} instances.
     */
    public MetadataTreeTable(final IIOMetadataFormat format) {
        ensureNonNull("format", format);
        this.format = format;
        locale = Locale.getDefault();
        converters = new AnyConverter();
    }

    /**
     * Makes sure that an argument is non-null.
     */
    private static void ensureNonNull(final String name, final Object object) throws NullArgumentException {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Returns the locale for which localization will be attempted.
     *
     * @return The locale for which localization will be attempted.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale for which localization will be attempted. This method invalidates
     * any {@linkplain #getRootNode() root node} obtained before the call to this method.
     *
     * @param locale The locale for which localization will be attempted.
     */
    public void setLocale(final Locale locale) {
        ensureNonNull("locale", locale);
        tree = null; // Will force new calculation.
        this.locale = locale;
    }

    /**
     * Returns the metadata format specified at construction time.
     *
     * @return The metadata format.
     *
     * @since 3.05
     */
    public IIOMetadataFormat getMetadataFormat() {
        return format;
    }

    /**
     * Returns the metadata to be formatted as a tree table.
     *
     * @return The current metadata, or {@code null} if none.
     */
    public IIOMetadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata to be formatted as a tree table. This method invalidates any
     * {@linkplain #getRootNode() root node} obtained before the call to this method.
     *
     * @param  metadata The new metadata, or {@code null} if none.
     * @throws IllegalArgumentException If the given metadata does not support
     *         the format given to the {@code MetadataTreeTable} constructor.
     */
    public void setMetadata(final IIOMetadata metadata) throws IllegalArgumentException {
        root = (metadata != null) ? metadata.getAsTree(format.getRootName()) : null;
        tree = null; // Will force new calculation.
        this.metadata = metadata;
    }

    /**
     * Returns the root Image I/O node, or {@code null} if none.
     */
    final Node getRootIIO() {
        return root;
    }

    /**
     * Returns the root of the Tree Table representation of the metadata. If there is
     * no metadata currently set, then returns a representation of the metadata format.
     * <p>
     * The state of this {@code MetadataTreeNode} should not be modified after the call
     * to this method (i.e. {@code setLocale} and {@code setMetadata} methods should not
     * be invoked), otherwise the content of the returned node may become invalid.
     *
     * @return The root of a tree representation of the metadata.
     */
    public TreeTableNode getRootNode() {
        if (tree == null) {
            final MetadataTreeNode root = new MetadataTreeNode(this, format.getRootName());
            addChilds(root);
            tree = root;
        }
        return tree;
    }

    /**
     * Adds attributes and child elements to the given parent.
     * This method invokes itself recursively.
     *
     * @param parent The parent into which the childs need to be added.
     */
    private void addChilds(final DefaultMutableTreeNode parent) {
        final String name = parent.toString();
        /*
         * Adds the attributes first. They will be children of the parent node.
         */
        String[] childs = format.getAttributeNames(name);
        if (childs != null) {
            for (final String childName : childs) {
                parent.add(new MetadataTreeNode(this, name, childName));
            }
        }
        /*
         * Adds the child elements after the attribute.
         */
        childs = format.getChildNames(name);
        if (childs != null) {
            for (final String childName : childs) {
                if (childName != null) {
                    final MetadataTreeNode child = new MetadataTreeNode(this, childName);
                    addChilds(child);
                    parent.add(child);
                }
            }
        }
    }
}
