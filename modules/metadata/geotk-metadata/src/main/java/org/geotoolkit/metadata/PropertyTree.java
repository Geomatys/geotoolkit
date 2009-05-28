/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.tree.TreeNode;

import org.opengis.util.CodeList;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.collection.CheckedCollection;
import org.geotoolkit.gui.swing.tree.NamedTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.resources.Errors;


/**
 * Represents the metadata property as a tree made from {@linkplain TreeNode tree nodes}.
 * Note that while {@link TreeNode} is defined in the {@link javax.swing.tree} package,
 * it can be seen as a data structure independent of Swing.
 * <p>
 * This class is called {@code PropertyTree} because it may implements
 * {@link javax.swing.tree.TreeModel} in some future Geotoolkit implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
final class PropertyTree {
    /**
     * The default number of significant digits (may or may not be fraction digits).
     */
    private static final int PRECISION = 12;

    /**
     * The expected standard implemented by the metadata.
     */
    private final MetadataStandard standard;

    /**
     * The locale to use for {@linkplain Date date}, {@linkplain Number number}
     * and {@linkplain InternationalString international string} formatting.
     */
    private final Locale locale;

    /**
     * The object to use for formatting numbers.
     * Will be created only when first needed.
     */
    private transient NumberFormat numberFormat;

    /**
     * The object to use for formatting dates.
     * Will be created only when first needed.
     */
    private transient DateFormat dateFormat;

    /**
     * Creates a new tree builder using the default locale.
     *
     * @param standard The expected standard implemented by the metadata.
     */
    public PropertyTree(final MetadataStandard standard) {
        this(standard, Locale.getDefault());
    }

    /**
     * Creates a new tree builder.
     *
     * @param standard The expected standard implemented by the metadata.
     * @param locale   The locale to use for {@linkplain Date date}, {@linkplain Number number}
     *                 and {@linkplain InternationalString international string} formatting.
     */
    public PropertyTree(final MetadataStandard standard, final Locale locale) {
        this.standard = standard;
        this.locale   = locale;
    }


    // ---------------------- PARSING -------------------------------------------------------------

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * the given metadata object. The value of the root node is ignored (it is typically
     * just the name of the metadata class).
     *
     * @param  node     The node from which to fetch the values.
     * @param  metadata The metadata where to store the values.
     * @throws ParseException If a value can not be stored in the given metadata object.
     */
    final void parse(final TreeNode node, final Object metadata) throws ParseException {
        final Class<?> type = metadata.getClass();
        final PropertyAccessor accessor = standard.getAccessorOptional(type);
        if (accessor == null) {
            throw new ParseException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, type), 0);
        }
        parse(node, metadata, accessor, null);
    }

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * the given metadata object. This method invokes itself recursively.
     *
     * @param  node       The node from which to fetch the values.
     * @param  metadata   The metadata where to store the values.
     * @param  accessor   The object to use for writing in the metadata object.
     * @param  additional Non-null if multiple metadata instances are allowed, in which
     *                    case additional instances will be added to that map.
     * @throws ParseException If a value can not be stored in the given metadata object.
     */
    private void parse(final TreeNode node, Object metadata, final PropertyAccessor accessor,
            final Collection<Object> additional) throws ParseException
    {
        final Set<String> done = new HashSet<String>();
        final int childCount = node.getChildCount();
        for (int i=0; i<childCount; i++) {
            final TreeNode child = node.getChildAt(i);
            final String name = child.toString();
            if (!done.add(name)) {
                /*
                 * If a child of the same name occurs one more time, assume that it is starting
                 * a new metadata object. For example if "Individual Name" appears one more time
                 * inside a "Responsible Party" metadata, then what we are building is actually
                 * a collection of Responsible Parties instead than a single instance, and we
                 * are starting a new sentence.
                 *
                 * Note that this is not confused with the case where a single "Responsible Party"
                 * has many "Individual Name" (if it was allowed by ISO), since in such case every
                 * names would be childs under the same "Individual Name" child.
                 *
                 * This work only if, for each "Responsible Party" instance, the first child in
                 * the tree is a mandatory attribute (or some attribute garanteed to be in each
                 * child). Otherwise this approach is actually ambiguous.
                 */
                done.clear();  // Starts a new cycle.
                done.add(name);
                metadata = newInstance(metadata.getClass());
                additional.add(metadata);
            }
            final int index = accessor.indexOf(name);
            if (index < 0) {
                throw new ParseException(Errors.format(Errors.Keys.UNKNOW_PARAMETER_NAME_$1, name), 0);
            }
            /*
             * If a value already exists in the metadata object for the current child,
             * gets the old value. If no value is given, then an implementation type
             * will be inferred from the return value type.
             */
            Object value = accessor.get(index, metadata);
            final Class<?> type;
            if (value != null) {
                if (value instanceof CheckedCollection) {
                    type = standard.getImplementation(((CheckedCollection) value).getElementType());
                } else {
                    type = value.getClass();
                }
            } else {
                type = standard.getImplementation(accessor.type(index));
                // Note: we could check if the type is assignable to Collection.class,
                // and in such case fetch the parameterized type for the same raisons
                // then we fetched CheckedCollection.getElementType() above. However
                // this is not needed in Geotoolkit implementation since we never return
                // a null collection (the collection can be empty but never null).
            }
            /*
             * If the type is an other metadata implementation, invokes this method
             * recursively for every childs of the current child, to be stored in
             * the metadata object we just found (creating a new one if necessary).
             */
            final PropertyAccessor ca = standard.getAccessorOptional(type);
            if (ca != null) {
                if (value instanceof Collection) {
                    final Collection<Object> childs;
                    if (value instanceof List) {
                        childs = new ArrayList<Object>(4);
                    } else {
                        childs = new LinkedHashSet<Object>(4);
                    }
                    childs.add(value = newInstance(type));
                    parse(child, value, ca, childs);
                    value = childs;
                } else {
                    // Singleton: create the instance if necessary.
                    if (value == null) {
                        value = newInstance(type);
                    }
                    parse(child, value, ca, null);
                }
            } else {
                /*
                 * Otherwise if the type is not an other metadata implementation, we assume that
                 * this is some primitive (numbers, etc.), a date or a string. Stores them in the
                 * current metadata object using the accessor.
                 */
                value = getUserObject(child);
                if (value == null) {
                    final int n = child.getChildCount();
                    if (n == 0) {
                        continue;
                    }
                    final TreeNode cn = child.getChildAt(0);
                    value = getUserObject(cn);
                    if (value == null) {
                        value = cn.toString();
                    }
                    // TODO: needs to build a collection with other elements.
                }
                if (value instanceof CharSequence) {
                    if (Number.class.isAssignableFrom(type)) {
                        value = numberFormat.parse(value.toString());
                    } else if (Date.class.isAssignableFrom(type)) {
                        value = dateFormat.parse(value.toString());
                    }
                }
            }
            accessor.set(index, metadata, value, false);
        }
    }

    /**
     * Returns the user object of the given node if possible, or {@code null} otherwise.
     */
    private static Object getUserObject(final TreeNode node) {
        if (false && node instanceof org.geotoolkit.gui.swing.tree.TreeNode) {
            return ((org.geotoolkit.gui.swing.tree.TreeNode) node).getUserObject();
        }
        return null;
    }

    /**
     * Creates a new instance of the given type, wrapping the {@code java.lang.reflect}
     * exceptions in the exception using by our parsing API.
     */
    private static <T> T newInstance(final Class<T> type) throws ParseException {
        try {
            return type.newInstance();
        } catch (Exception cause) { // InstantiationException & IllegalAccessException
            ParseException exception = new ParseException(Errors.format(
                    Errors.Keys.CANT_CREATE_FROM_TEXT_$1, type), 0);
            exception.initCause(cause);
            throw exception;
        }
    }


    // ---------------------- FORMATING -----------------------------------------------------------


    /**
     * Creates a tree for the specified metadata.
     */
    public MutableTreeNode asTree(final Object metadata) {
        final String name = Classes.getShortName(standard.getInterface(metadata.getClass()));
        final DefaultMutableTreeNode root = new NamedTreeNode(localize(name), metadata, true);
        append(root, metadata);
        return root;
    }

    /**
     * Appends the specified value to a branch. The value may be a metadata
     * (treated {@linkplain AbstractMetadata#asMap as a Map} - see below),
     * a collection or a singleton.
     * <p>
     * Map or metadata are constructed as a sub tree where every nodes is a
     * property name, and the childs are the value(s) for that property.
     */
    private void append(final DefaultMutableTreeNode branch, final Object value) {
        if (value instanceof Map) {
            appendMap(branch, (Map<?,?>) value);
            return;
        }
        if (value instanceof AbstractMetadata) {
            appendMap(branch, ((AbstractMetadata) value).asMap());
            return;
        }
        if (value != null) {
            final PropertyAccessor accessor = standard.getAccessorOptional(value.getClass());
            if (accessor != null) {
                appendMap(branch, new PropertyMap(value, accessor));
                return;
            }
        }
        if (value instanceof Collection) {
            for (final Object element : (Collection<?>) value) {
                if (!PropertyAccessor.isEmpty(element)) {
                    append(branch, element);
                }
            }
            return;
        }
        final String asText;
        if (value instanceof CodeList) {
            asText = localize((CodeList<?>) value);
        } else if (value instanceof Date) {
            asText = format((Date) value);
        } else if (value instanceof Number) {
            asText = format((Number) value);
        } else if (value instanceof InternationalString) {
            asText = ((InternationalString) value).toString(locale);
        } else {
            asText = String.valueOf(value);
        }
        branch.add(new NamedTreeNode(asText, value, false));
    }

    /**
     * Appends the specified map (usually a metadata) to a branch. Each map keys
     * is a child in the specified {@code branch}, and each value is a child of
     * the map key. There is often only one value for a map key, but not always;
     * some are collections, which are formatted as many childs for the same key.
     */
    private void appendMap(final DefaultMutableTreeNode branch, final Map<?,?> asMap) {
        for (final Map.Entry<?,?> entry : asMap.entrySet()) {
            final Object value = entry.getValue();
            if (!PropertyAccessor.isEmpty(value)) {
                final String name = localize((String) entry.getKey());
                final DefaultMutableTreeNode child = new NamedTreeNode(name, value, true);
                append(child, value);
                branch.add(child);
            }
        }
    }

    /**
     * Formats the specified number.
     */
    private String format(final Number value) {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getNumberInstance(locale);
            numberFormat.setMinimumFractionDigits(0);
        }
        int precision = 0;
        if (!Classes.isInteger(value.getClass())) {
            precision = PRECISION;
            final double v = Math.abs(value.doubleValue());
            if (v > 0) {
                final int digits = (int) Math.log10(v);
                if (Math.abs(digits) >= PRECISION) {
                    // TODO: Switch to exponential notation when a convenient API will be available in J2SE.
                    return value.toString();
                }
                if (digits >= 0) {
                    precision -= digits;
                }
                precision = Math.max(0, PRECISION - precision);
            }
        }
        numberFormat.setMaximumFractionDigits(precision);
        return numberFormat.format(value);
    }

    /**
     * Formats the specified date.
     */
    private String format(final Date value) {
        if (dateFormat == null) {
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        }
        return dateFormat.format(value);
    }

    /**
     * Localize the specified property name. In current version, this is merely
     * a hook for future development. For now we reformat the programatic name.
     * <p>
     * NOTE: If we localize the name, then we must find some way to allow the reverse
     * association in the {@link #parse(AbstractMetadata, TreeNode)} method.
     */
    private String localize(String name) {
        name = name.trim();
        final int length = name.length();
        if (length != 0) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(Character.toUpperCase(name.charAt(0)));
            boolean previousIsUpper = true;
            int base = 1;
            for (int i=1; i<length; i++) {
                final boolean currentIsUpper = Character.isUpperCase(name.charAt(i));
                if (currentIsUpper != previousIsUpper) {
                    /*
                     * When a case change is detected (lower case to upper case as in "someName",
                     * or "someURL", or upper case to lower case as in "HTTPProxy"), then insert
                     * a space just before the upper case letter.
                     */
                    int split = i;
                    if (previousIsUpper) {
                        split--;
                    }
                    if (split > base) {
                        buffer.append(name.substring(base, split)).append(' ');
                        base = split;
                    }
                }
                previousIsUpper = currentIsUpper;
            }
            final String candidate = buffer.append(name.substring(base)).toString();
            if (!candidate.equals(name)) {
                // Holds a reference to this new String object only if it worth it.
                name = candidate;
            }
        }
        return name;
    }

    /**
     * Localize the specified property name. In current version, this is merely
     * a hook for future development.  For now we reformat the programatic name.
     */
    private String localize(final CodeList<?> code) {
        return code.name().trim().replace('_', ' ').toLowerCase(locale);
    }

    /**
     * Returns a string representation of the specified tree node.
     */
    public static String toString(final TreeNode node) {
        final StringBuilder buffer = new StringBuilder();
        toString(node, buffer, 0, System.getProperty("line.separator", "\n"));
        return buffer.toString();
    }

    /**
     * Append a string representation of the specified node to the specified buffer.
     */
    private static void toString(final TreeNode      node,
                                 final StringBuilder buffer,
                                 final int           indent,
                                 final String        lineSeparator)
    {
        final int count = node.getChildCount();
        if (count == 0) {
            if (node.isLeaf()) {
                /*
                 * If the node has no child and is a leaf, then it is some value like a number,
                 * a date or a string.  We just display this value, which is usually part of a
                 * collection. If the node has no child and is NOT a leaf, then it is an empty
                 * metadata and we just ommit it.
                 */
                buffer.append(Utilities.spaces(indent)).append(node).append(lineSeparator);
            }
            return;
        }
        buffer.append(Utilities.spaces(indent)).append(node).append(':');
        if (count == 1) {
            final TreeNode child = node.getChildAt(0);
            if (child.isLeaf()) {
                buffer.append(' ').append(child).append(lineSeparator);
                return;
            }
        }
        for (int i=0; i<count; i++) {
            final TreeNode child = node.getChildAt(i);
            if (i == 0) {
                buffer.append(lineSeparator);
            }
            toString(child, buffer, indent+2, lineSeparator);
        }
    }
}
