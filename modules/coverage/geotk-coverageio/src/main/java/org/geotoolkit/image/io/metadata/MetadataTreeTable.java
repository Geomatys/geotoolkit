/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.sis.util.Localized;
import org.geotoolkit.gui.swing.tree.Trees;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A view of an {@link IIOMetadata} instance as a tree table. The tree structure is determined by
 * an {@link IIOMetadataFormat}, which must be provided to the constructor. After the construction,
 * different instances of {@code IIOMetadata} can be given to this {@code MetadataTreeTable} in
 * order to generate tables with different values. If no {@code IIOMetadata} instance is given,
 * then this object represents only the structure of the format with its restrictions (expected
 * type, range of values, <i>etc.</i>) but no values.
 * <p>
 * The root of the tree is obtained by {@link #getRootNode()}. The table contains at most
 * {@value #COLUMN_COUNT} columns, described below:
 * <ol>
 *   <li>A human-readable name of the nodes.</li>
 *   <li>A description of the node.</li>
 *   <li>The {@linkplain Class#getSimpleName() simple class names} of values.</li>
 *   <li>The range of occurrences (how many time the node can be repeated).</li>
 *   <li>The node value (this column is omitted if the tree is for
 *       {@link IIOMetadataFormat} instead than {@link IIOMetadata}).</li>
 *   <li>The default value.</li>
 *   <li>A description of valid values (either as a range or as an enumeration).</li>
 * </ol>
 * <p>
 * This class works with arbitrary implementations of {@code IIOMetadata};
 * it doesn't need to be the specialized implementations defined in Geotk.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @see org.geotoolkit.gui.swing.image.IIOMetadataPanel
 *
 * @since 3.04
 * @module
 */
public class MetadataTreeTable implements Localized {
    /**
     * The number of columns in the table ({@value}), when every columns are present. Note that
     * the {@linkplain #VALUE_COLUMN value column} is omitted if this {@code MetadataTreeTable}
     * is given only an {@link IIOMetadataFormat} without any {@link IIOMetadata} instance for
     * providing the actual values.
     */
    public static final int COLUMN_COUNT = 7;

    /**
     * The column which contains the values. This is the column which is omitted if this
     * table describe only an {@link IIOMetadataFormat} without {@link IIOMetadata}.
     *
     * @since 3.05
     */
    public static final int VALUE_COLUMN = 4;

    /**
     * The expected format of {@code IIOMetadata} instances.
     */
    final IIOMetadataFormat format;

    /**
     * The current metadata, or {@code null} if none.
     */
    private IIOMetadata metadata;

    /**
     * The Locale for which localization will be attempted.
     */
    private Locale locale;

    /**
     * {@code true} if the tree returned by {@link #getRootNode()} is allowed to prune empty
     * nodes and merge singleton attributes with the parent node. This parameter has no effect
     * if this {@code MetatataTreeTable} is used for an {@link IIOMetadataFormat} only.
     */
    private boolean simplificationAllowed;

    /**
     * The root of the tree table. This is the result of {@link #getRootNode()}, which use all
     * the above fields. It will be created only when first needed and reset to {@code null} if
     * one of the above field change, in order to force the creation of a new tree.
     */
    private transient MetadataTreeNode tree;

    /**
     * Creates a new metadata tree for the given format.
     *
     * @param format The expected format of {@code IIOMetadata} instances.
     */
    public MetadataTreeTable(final IIOMetadataFormat format) {
        ensureNonNull("format", format);
        this.format = format;
        locale = Locale.getDefault(Locale.Category.DISPLAY);
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
        this.metadata = metadata;
        tree = null; // Will force new calculation.
    }

    /**
     * Returns the locale for which localization will be attempted.
     *
     * @return The locale for which localization will be attempted.
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale for which localization will be attempted. This change applies to
     * future {@link MetadataTreeNode} instances to be created by {@link #getRootNode()}.
     * The effect on previous instances (if any) is undefined - some will take the change
     * in account, other will ignore.
     *
     * @param locale The locale for which localization will be attempted.
     */
    public void setLocale(final Locale locale) {
        ensureNonNull("locale", locale);
        if (!locale.equals(this.locale)) {
            this.locale = locale;
            tree = null; // Will force new calculation.
        }
    }

    /**
     * Returns {@code true} if the tree returned by {@link #getRootNode()} can be simplified.
     * Simplification are convenient for GUI purpose, but usually not appropriate for
     * programmatic purpose. The simplifications, if allowed, are:
     * <p>
     * <ul>
     *   <li>If a node has only one attribute, do not add that attribute to the tree.
     *       Instead, move its {@linkplain MetadataTreeNode#getUserObject() value} in
     *       the parent element.</li>
     *   <li>Prune empty nodes.</li>
     * </ul>
     * </p>
     * The default value is {@code false}.
     *
     * @return {@code true} if the tree can be simplified.
     *
     * @since 3.05
     */
    public boolean getSimplificationAllowed() {
        return simplificationAllowed;
    }

    /**
     * Sets whatever the tree returned by {@link #getRootNode()} can be simplified. This
     * parameter is ignored if there is no {@link IIOMetadata} instance associated with
     * this {@code MetadataTreeTable}.
     *
     * @param allowed {@code true} if the tree can be simplified.
     *
     * @since 3.05
     */
    public void setSimplificationAllowed(final boolean allowed) {
        if (allowed != simplificationAllowed) {
            simplificationAllowed = allowed;
            tree = null; // Will force new calculation.
        }
    }

    /**
     * Returns the root of the Tree Table representation of the metadata. If there is
     * no {@link IIOMetadata} instance currently set, then returns a representation of
     * the {@link IIOMetadataFormat}.
     *
     * @return The root of a tree representation of the metadata.
     */
    public MetadataTreeNode getRootNode() {
        if (tree == null) {
            final boolean hasValue = (metadata != null);
            final Node xmlNode = hasValue ? metadata.getAsTree(format.getRootName()) : null;
            final MetadataTreeNode root = new MetadataTreeNode(this, format.getRootName(), xmlNode, hasValue);
            addChilds(root, new HashMap<String,Object>());
            tree = root;
        }
        return tree;
    }

    /**
     * Adds attributes and child elements to the given parent.
     * This method invokes itself recursively.
     *
     * @param  addTo The parent into which the childs need to be added.
     * @param  done  A safety against infinite recursivity. The keys are the name of childs
     *               in the process of being added. The value are the node for which t
     * @return {@code true} if at least one element or attribute has been added.
     */
    private boolean addChilds(final MetadataTreeNode addTo, final Map<String,Object> done) {
        boolean added = false;
        final boolean includeEmpty = !addTo.hasValue || !simplificationAllowed;
        /*
         * Adds the attributes first. They will be children of the parent node.
         */
        final String name = addTo.getName();
        String[] childs = format.getAttributeNames(name);
        if (childs != null) {
            for (final String childName : childs) {
                final MetadataTreeNode child = new MetadataTreeNode(addTo, childName);
                if (includeEmpty || child.getUserObject() != null) {
                    added = true;
                    if (simplificationAllowed && childs.length == 1) {
                        /*
                         * Attempt a simplification as documented in the getSimplificationAllowed()
                         * method. If the simplification succeed, do not add the attribute as a child.
                         */
                        if (child.copyToParent(addTo)) {
                            break;
                        }
                    }
                    addTo.add(child);
                }
            }
        }
        /*
         * Adds the child elements after the attributes. This method does not verify if the
         * elements are childs of this node; they could be childs of an unrelated node.
         * However the IIOMetadataFormat API is defined in such a way that we shall not
         * define different elements with the same name.
         */
        childs = format.getChildNames(name);
        if (childs != null) {
            for (final String childName : childs) {
                if (childName != null) {
                    /*
                     * If there is some values associated to the metadata, count the number of
                     * occurrence of the child element (may be greater than 1 if the policy is
                     * CHILD_POLICY_REPEAT). Otherwise (if we are formatting the metadata format
                     * instead than the values) add the child exactly once.
                     */
                    int count = 1;
                    NodeList elements = null;
                    if (addTo.xmlNode instanceof Element) {
                        elements = ((Element) addTo.xmlNode).getElementsByTagName(childName);
                        if (elements != null) {
                            count = elements.getLength();
                        }
                    }
                    for (int i=0; i<count; i++) {
                        final Node xmlChild = (elements != null) ? elements.item(i) : null;
                        /*
                         * Check for recursive invocation with the same child name. I'm not sure
                         * if this is legal, but it happen with the TIFF format provided by JAI.
                         * If the child to add is already in the process of being added, we will
                         * skip the second occurrence.
                         */
                        final Object newNode = (xmlChild != null) ? xmlChild : Void.TYPE;
                        final Object oldNode = done.put(childName, newNode);
                        if (newNode != oldNode) {
                            final MetadataTreeNode child = new MetadataTreeNode(this, childName, xmlChild, addTo.hasValue);
                            if (addChilds(child, done) || includeEmpty || child.getUserObject() != null) {
                                addTo.add(child);
                                added = true;
                            }
                        }
                        if ((oldNode != null ? done.put(childName, oldNode) : done.remove(childName)) != newNode) {
                            throw new AssertionError(childName);
                        }
                    }
                }
            }
        }
        return added;
    }

    /**
     * Returns a string representation of this tree table. The current implementation
     * formats a tree, but it could change in future Geotk version. Consequently this
     * method should be used for debugging purpose only.
     *
     * @since 3.16
     */
    @Override
    public String toString() {
        return Trees.toString(getRootNode());
    }
}
