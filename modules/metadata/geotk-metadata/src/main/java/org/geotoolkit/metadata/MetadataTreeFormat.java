/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collection;
import java.util.Locale;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.text.Format;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.ParseException;
import javax.swing.tree.TreeNode;

import org.opengis.util.CodeList;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.content.Band;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.GridSpatialRepresentation;

import org.geotoolkit.util.Strings;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.util.collection.CheckedContainer;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.gui.swing.tree.NamedTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.TreeFormat;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.internal.InternalUtilities;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.metadata.AbstractMetadata.LOGGER;


/**
 * Builds tree or tree-table representations of metadata. The root of the tree and each children
 * are {@link TreeNode} or {@link TreeTableNode} objects. The node labels are formatted according
 * the {@link Locale} argument given at construction time.
 * <p>
 * This class offers two families of methods, listed in two rows below. The {@code Object} type
 * stands for any metadata object compliant with the {@link MetadataStandard} specified at
 * construction time.
 * <p>
 * <table border="1">
 *   <tr><th></th><th>Parsing</th><th>Formatting</th></tr>
 *   <tr><td>Between {@link String} and metadata:</td>
 *     <td>{@link #parseObject(String)} ⇒ {@link Object}</td>
 *     <td>{@link #format(Object)} ⇒ {@link String}</td></tr>
 *   <tr><td>Between {@link TreeNode} and metadata:</td>
 *     <td>{@link #parse(TreeNode)} ⇒ {@link Object}</td>
 *     <td>{@link #asTree(Object)} ⇒ {@link TreeNode}</td></tr>
 * </table>
 * <p>
 * The methods performing conversions between {@link String} and metadata objects are part of the
 * {@link Format} contract. They work by converting between {@link String} and {@link TreeNode}
 * using {@link TreeFormat}, then delegating to the second family of methods defined in this class
 * for the conversions between {@link TreeNode} and metadata object. This second family of methods
 * is specific to this {@code MetadataTreeFormat} class.
 *
 * {@note The <code>TreeNode</code> interface is defined in the <code>javax.swing.tree</code>
 *        package. However it can be used as a data structure independent of Swing.}
 *
 * {@section Subclassing}
 * This class provides some protected methods that subclasses can override in order to control
 * the parsing and formatting processes:
 * <p>
 * <table>
 * <tr><th>Formating</th><th>Parsing</th></tr>
 * <tr><td><ul>
 *   <li>{@link #formatElementName(Class, String)}</li>
 *   <li>{@link #formatCodeList(CodeList)}</li>
 *   <li>{@link #formatNumber(Number)}</li>
 * </ul></td>
 * <td><ul>
 *   <li>{@link #getTypeForName(String)}</li>
 * </ul></td></tr></table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Mehdi Sidhoum (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
public class MetadataTreeFormat extends Format {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3603011614118221049L;

    /**
     * The character used for formating number when enumerating the elements of a collection.
     */
    static final char OPEN_BRACKET = '[', CLOSE_BRACKET = ']';

    /**
     * Maximum number of characters to use in the heuristic titles.
     * This is an arbitrary limit.
     *
     * @since 3.19
     */
    private static final int TITLE_LIMIT = 40;

    /**
     * The kind of objects to use for inferring a title, in preference order.
     * {@code Object.class} must exist and be last.
     *
     * @since 3.19
     */
    private static final Class<?>[] TITLE_TYPES = {
        GenericName.class, InternationalString.class, CharSequence.class, CodeList.class, Object.class
    };

    /**
     * The standard implemented by the metadata to represent as trees.
     */
    protected final MetadataStandard standard;

    /**
     * The locale to use for {@linkplain InternationalString international string} formatting.
     * By default, this is the locale for the {@linkplain java.util.Locale.Category#DISPLAY display}
     * category.
     */
    protected final Locale displayLocale;

    /**
     * The locale to use for {@linkplain Date date} and {@linkplain Number number} formatting.
     * By default, this is the locale for the {@linkplain java.util.Locale.Category#FORMAT format}
     * category.
     */
    protected final Locale formatLocale;

    /**
     * The object to use for formatting numbers, dates and trees. Will be created only when first
     * needed. We declare the generic {@link Format} type in order to avoid too early class loading.
     */
    private transient Format numberFormat, dateFormat, treeFormat;

    /**
     * Creates a new tree builder using the default locale.
     *
     * @param standard The expected standard implemented by the metadata.
     */
    public MetadataTreeFormat(final MetadataStandard standard) {
        this.standard      = standard;
        this.displayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        this.formatLocale  = Locale.getDefault(Locale.Category.FORMAT);
    }

    /**
     * Creates a new tree builder.
     *
     * @param standard The expected standard implemented by the metadata.
     * @param locale   The locale to use for {@linkplain Date date}, {@linkplain Number number}
     *                 and {@linkplain InternationalString international string} formatting.
     */
    public MetadataTreeFormat(final MetadataStandard standard, final Locale locale) {
        this.standard      = standard;
        this.displayLocale = locale;
        this.formatLocale  = locale;
    }


    // ---------------------- PARSING -------------------------------------------------------------

    /**
     * Creates a metadata from the given string representation, or returns {@code null} if an
     * error occurred while parsing the tree.
     * <p>
     * The default implementation delegates to {@link #parseObject(String)}.
     *
     * @param  text The string representation of the tree to parse.
     * @param  pos  The position when to start the parsing.
     * @return The parsed tree as a metadata object, or {@code null} if the given tree can not be parsed.
     */
    @Override
    public Object parseObject(String text, final ParsePosition pos) {
        final int base = pos.getIndex();
        text = text.substring(base);
        try {
            final Object metadata = parseObject(text);
            pos.setIndex(base + text.length());
            return metadata;
        } catch (ParseException e) {
            pos.setErrorIndex(base + e.getErrorOffset());
            return null;
        }
    }

    /**
     * Creates a metadata from the given string representation. The default implementation
     * {@linkplain TreeFormat#parseObject(String) converts the given string representation
     * to tree nodes}, then invokes {@link #parse(TreeNode)}.
     * <p>
     * Note that in current implementation, there is a very limited amount of names that the
     * {@link #getTypeForName(String)} method can recognize. Users may need to override the
     * {@code getTypeForName} method in order to parse their metadata.
     *
     * @param  text The string representation of the tree to parse.
     * @return The parsed tree as a metadata object.
     * @throws ParseException If an error occurred while parsing the tree.
     */
    @Override
    public Object parseObject(final String text) throws ParseException {
        return parse(getFormat(TreeFormat.class).parseObject(text));
    }

    /**
     * Creates a metadata from the values in every nodes of the given tree. The metadata object
     * type is inferred by the {@linkplain NamedTreeNode name} of the given root node.
     * For example if the current {@linkplain #standard} is {@link MetadataStandard#ISO_19115}
     * and the name of the given root node is {@code "Metadata"}, then the default implementation
     * will instantiate a new {@link org.geotoolkit.metadata.iso.DefaultMetadata} object.
     * <p>
     * Note that in current implementation, there is a very limited amount of names that the
     * {@link #getTypeForName(String)} method can recognize. Users may need to override the
     * {@code getTypeForName} method in order to parse their metadata.
     *
     * @param  node The node from which to fetch the values.
     * @return The parsed tree as a metadata object.
     * @throws ParseException If the given {@code metadata} argument is null and its type can not
     *         be inferred, or if a value can not be stored in the metadata object.
     */
    public Object parse(final TreeNode node) throws ParseException {
        Object name = node;
        if (node instanceof NamedTreeNode) {
            name = ((NamedTreeNode) node).getName();
        }
        final Object metadata = newInstance(standard.getImplementation(getTypeForName(name.toString())));
        parse(node, metadata);
        return metadata;
    }

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * the given metadata object. The value of the root node is ignored.
     * <p>
     * If the given metadata object already contains property values, then the parsing will be
     * merged with the existing values: attributes not defined in the tree will be left unchanged,
     * and collections will be augmented with new entries without change in the previously existing
     * entries.
     *
     * @param  node The node from which to fetch the values.
     * @param  destination The metadata where to store the values, or {@code null} for creating a new one.
     * @throws ParseException If the given {@code metadata} argument is null and its type can not
     *         be inferred, or if a value can not be stored in the metadata object.
     */
    final void parse(final TreeNode node, final Object destination) throws ParseException {
        final Class<?> type = destination.getClass();
        final PropertyAccessor accessor = standard.getAccessorOptional(type);
        if (accessor == null) {
            throw new ParseException(Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, type), 0);
        }
        final int duplicated = parse(node, type, destination, null, accessor);
        if (duplicated != 0) {
            final LogRecord record = Errors.getResources(displayLocale).getLogRecord(
                    Level.WARNING, Errors.Keys.DUPLICATED_VALUES_COUNT_$1, duplicated);
            record.setSourceClassName("MetadataTreeFormat");
            record.setSourceMethodName("parse");
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
    }

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * the given metadata object. This method invokes itself recursively.
     *
     * @param  node       The node from which to fetch the values.
     * @param  type       The implementation class of the given {@code metadata}.
     * @param  metadata   The metadata where to store the values, or {@code null} if it should
     *                    be created from the given {@code type} when first needed.
     * @param  addTo      If non-null, then metadata objects (including the one provided to this
     *                    method) are added to this collection after they have been parsed.
     * @param  accessor   The object to use for writing in the metadata object.
     * @return The number of duplicated elements, for logging purpose.
     * @throws ParseException If a value can not be stored in the given metadata object.
     */
    private <T> int parse(final TreeNode node, final Class<? extends T> type, T metadata,
            final Collection<? super T> addTo, final PropertyAccessor accessor) throws ParseException
    {
        int duplicated = 0;
        final Set<String> done = new HashSet<>();
        final int childCount = node.getChildCount();
        for (int i=0; i<childCount; i++) {
            final TreeNode child = node.getChildAt(i);
            final String name = child.toString().trim();
            /*
             * If the node label is something like "[2] A title", then the node should be the
             * container of the element of a collection. This rule is based on the assumption
             * that '[' and ']' are not valid characters for UML or Java identifiers.
             */
            if (addTo != null && isCollectionElement(name)) {
                duplicated += parse(child, type, null, addTo, accessor);
                continue;
            }
            if (!done.add(name)) {
                /*
                 * If a child of the same name occurs one more time, assume that it is starting
                 * a new metadata object. For example if "Individual Name" appears one more time
                 * inside a "Responsible Party" metadata, then what we are building is actually
                 * a collection of Responsible Parties instead than a single instance, and we
                 * are starting a new instance.
                 *
                 * Note that this is not confused with the case where a single "Responsible Party"
                 * has many "Individual Name" (if it was allowed by ISO), since in such case every
                 * names would be childs under the same "Individual Name" child.
                 *
                 * This approach is ambiguous (unless the first child is always a mandatory property)
                 * and actually not needed for parsing the trees formatted by this class. However this
                 * method applies this lenient approach anyway in order to still be able to parse trees
                 * that are not properly formatted, but still understandable in some way.
                 */
                if (addTo == null) {
                    throw new ParseException(Errors.format(Errors.Keys.DUPLICATED_VALUES_FOR_KEY_$1, name), 0);
                }
                // Something would be wrong with the 'done' map if the following assertion fails.
                assert (metadata != null) : done;
                if (!addTo.add(metadata)) {
                    duplicated++;
                }
                done.clear();  // Starts a new cycle.
                done.add(name);
                metadata = null;
            }
            /*
             * Get the type of the child from the method signature. If the type is a collection,
             * get the type of collection element instead than the type of the collection itself.
             */
            final int index = accessor.indexOf(name);
            if (index < 0) {
                throw new ParseException(Errors.format(Errors.Keys.UNKNOWN_PARAMETER_NAME_$1, name), 0);
            }
            Class<?> childType = accessor.type(index, TypeValuePolicy.ELEMENT_TYPE);
            if (childType == null) {
                // The type of the parameter is unknown (actually the message is a bit
                // misleading since it doesn't said that only the type is unknown).
                throw new ParseException(Errors.format(Errors.Keys.UNKNOWN_PARAMETER_$1, name), 0);
            }
            childType = standard.getImplementation(childType);
            /*
             * If the type is an other metadata implementation, invokes this method
             * recursively for every childs of the metadata object we just found.
             */
            Object value;
            final PropertyAccessor ca = standard.getAccessorOptional(childType);
            if (ca != null) {
                value = accessor.get(index, metadata);
                if (value instanceof Collection<?>) {
                    @SuppressWarnings("unchecked") // We will rely on CheckedCollection checks.
                    final Collection<Object> childs = (Collection<Object>) value;
                    duplicated += parse(child, childType, null, childs, ca);
                } else {
                    duplicated += parse(child, childType, value, null, ca);
                }
            } else {
                /*
                 * Otherwise if the type is not an other metadata implementation, we assume that
                 * this is some primitive (numbers, etc.), a date or a string. Stores them in the
                 * current metadata object using the accessor.
                 */
                final int n = child.getChildCount();
                final Object[] values = new Object[n];
                for (int j=0; j<n; j++) {
                    final TreeNode c = child.getChildAt(j);
                    value = getUserObject(c, childType);
                    if (value == null) {
                        final String s = c.toString();
                        if (Number.class.isAssignableFrom(childType)) {
                            value = getFormat(NumberFormat.class).parse(s);
                        } else if (Date.class.isAssignableFrom(childType)) {
                            value = getFormat(DateFormat.class).parse(s);
                        } else {
                            value = s;
                        }
                    }
                    values[j] = value;
                }
                value = Arrays.asList(values);
            }
            /*
             * Now create a new metadata instance if we need to.
             */
            if (metadata == null) {
                metadata = newInstance(type);
            }
            accessor.set(index, metadata, value, false);
        }
        if (addTo != null && metadata != null) {
            if (!addTo.add(metadata)) {
                duplicated++;
            }
        }
        return duplicated;
    }

    /**
     * Returns the user object of the given node if applicable, or {@code null} otherwise. This method
     * returns the user object only if it is of the expected type. We intentionally don't allow object
     * conversion. This is necessary because the user object of a node is often its {@code String} label
     * ({@link DefaultMutableTreeNode#toString()} is implemented as {@code return userObject.toString()}).
     * Consequently this user object is often <strong>not</strong> an object encapsulating the information
     * provided in child nodes. We want to distinguish those cases.
     */
    private static Object getUserObject(final TreeNode node, final Class<?> childType) {
        final Object value = Trees.getUserObject(node);
        return childType.isInstance(value) ? value : null;
    }

    /**
     * Creates a new instance of the given type, wrapping the {@code java.lang.reflect}
     * exceptions in the exception using by our parsing API.
     */
    private static <T> T newInstance(final Class<? extends T> type) throws ParseException {
        try {
            return type.newInstance();
        } catch (RuntimeException cause) {
            throw cause;
        } catch (Exception cause) {
            /*
             * We catch all Exceptions because Class.newInstance() propagates all of them,
             * including the checked ones (it bypass the compile-time exception checking).
             */
            ParseException exception = new ParseException(Errors.format(
                    Errors.Keys.CANT_CREATE_OBJECT_FROM_TEXT_$1, type), 0);
            exception.initCause(cause);
            throw exception;
        }
    }


    // ---------------------- FORMATING -----------------------------------------------------------


    /**
     * Writes a graphical representation of the specified metadata in the given buffer. The default
     * implementation {@linkplain #asTree(Object) gets a tree representation} of the given metadata,
     * then {@linkplain TreeFormat#format(Object, StringBuffer, FieldPosition) formats the tree}.
     *
     * @param  metadata    The metadata to format.
     * @param  toAppendTo  Where to format the metadata.
     * @param  pos         Ignored in current implementation.
     * @return             The given buffer, returned for convenience.
     */
    @Override
    public StringBuffer format(final Object metadata, final StringBuffer toAppendTo, final FieldPosition pos) {
        ArgumentChecks.ensureNonNull("metadata", metadata);
        return getFormat(TreeFormat.class).format(asTree(metadata), toAppendTo, pos);
    }

    /**
     * Creates a tree for the specified metadata.
     *
     * @param  metadata The metadata to format.
     * @return A tree representation of the given metadata.
     */
    public MutableTreeNode asTree(final Object metadata) {
        final Class<?> type = standard.getInterface(metadata.getClass());
        final DefaultMutableTreeNode root = new NamedTreeNode(formatElementName(type, null), metadata);
        append(root, metadata, 0);
        return root;
    }

    /**
     * Creates a tree-table for the specified metadata.
     *
     * @param  metadata The metadata to format.
     * @return A tree-table representation of the given metadata.
     *
     * @since 3.19
     */
    public TreeTableNode asTreeTable(final Object metadata) {
        final Class<?> type = standard.getInterface(metadata.getClass());
        final PropertyTreeNode root = new PropertyTreeNode(formatElementName(type, null), metadata);
        append(root, metadata, 0);
        return root;
    }

    /**
     * Appends the specified value to a branch. The value may be a metadata
     * (treated {@linkplain AbstractMetadata#asMap() as a Map} - see below),
     * a collection or a singleton.
     * <p>
     * Map or metadata are constructed as a sub tree where every nodes is a
     * property name, and the children are the value(s) for that property.
     *
     * @param  branch The node where to add children.
     * @param  value the value to add as a child.
     * @param  number Greater than 0 if the given value is a numbered metadata elements.
     *         Element are numbered if the value is an element of a collection having a
     *         size greater than 1.
     */
    private void append(final DefaultMutableTreeNode branch, final Object value, final int number) {
        if (value == null) {
            return;
        }
        final Map<?,?> asMap;
        final PropertyAccessor accessor;
        if (value instanceof Map<?,?>) {
            /*
             * The value is a Map derived from a metadata object (usually).
             */
            asMap = (Map<?,?>) value;
        } else if (value instanceof AbstractMetadata) {
            /*
             * The value is a metadata object (Geotk implementation).
             */
            asMap = ((AbstractMetadata) value).asMap();
        } else if ((accessor = standard.getAccessorOptional(value.getClass())) != null) {
            /*
             * The value is a metadata object (unknown implementation).
             */
            asMap = new PropertyMap(value, accessor, NullValuePolicy.NON_EMPTY, KeyNamePolicy.JAVABEANS_PROPERTY);
        } else if (value instanceof Collection<?>) {
            /*
             * The value is a collection of any other cases. Add all the childs recursively,
             * putting them in a numbered element if this is needed for avoiding ambiguity.
             */
            final Object[] values = ((Collection<?>) value).toArray();
            int count = 0; // Will be the count of non-ignored elements.
            for (final Object element : values) {
                if (!ignore(element)) {
                    values[count++] = element;
                }
            }
            int n = (count > 1) ? 1 : 0;
            for (int i=0; i<count; i++) {
                append(branch, values[i], n);
                if (n != 0) n++;
            }
            return;
        } else {
            /*
             * The value is anything else, to be converted to String.
             */
            final String valueAsText;
            if (value instanceof CodeList<?>) {
                valueAsText = formatCodeList((CodeList<?>) value);
            } else if (value instanceof Date) {
                valueAsText = getFormat(DateFormat.class).format((Date) value);
            } else if (value instanceof Number) {
                valueAsText = formatNumber((Number) value);
            } else if (value instanceof InternationalString) {
                valueAsText = ((InternationalString) value).toString(displayLocale);
            } else if (value instanceof Locale) {
                // We have no way to determine if the Locale is for a language, a script, a country
                // or all of them. However in ISO 19115, most Locale instances stand for a language.
                // The Locale.getDisplayName(...) method gives precedence to the language, with the
                // script, country and variant between parentheses if present.
                valueAsText = ((Locale) value).getDisplayName(displayLocale);
            } else {
                valueAsText = String.valueOf(value);
            }
            /*
             * If we are creating a TreeTable, write the value in the second column of current
             * node. Otherwise (for an ordinary tree), create a leaf node with only the value.
             */
            final DefaultMutableTreeNode child;
            if (branch instanceof PropertyTreeNode) {
                if (number == 0) {
                    ((PropertyTreeNode) branch).valueAsText = valueAsText;
                    branch.setUserObject(value);
                    return;
                }
                child = new PropertyTreeNode(formatNumber(number), valueAsText, value);
            } else {
                // TODO: actually we could have an AssertionError here if the user value is a
                // CharSequence begining with [number]. This is an issue only for the parser.
                assert !isCollectionElement(valueAsText) : valueAsText;
                child = new NamedTreeNode(valueAsText, value, false);
            }
            branch.add(child);
            return;
        }
        /*
         * Appends the specified map (usually a metadata) to a branch. Each map keys
         * is a child in the specified {@code branch}, and each value is a child of
         * the map key. There is often only one value for a map key, but not always;
         * some are collections, which are formatted as many childs for the same key.
         */
        Class<?> type = value.getClass();
        if (standard.isMetadata(type)) {
            type = standard.getInterface(type);
        }
        DefaultMutableTreeNode addTo = branch;
        if (number != 0) {
            // String formatted below must comply with the isCollectionElement(...) condition.
            final StringBuilder buffer = new StringBuilder(32);
            buffer.append(OPEN_BRACKET).append(formatNumber(number)).append(CLOSE_BRACKET).append(' ');
            buffer.append(Strings.camelCaseToSentence(Classes.getShortName(type)));
            String title = getTitleForSpecialCases(value);
            if (title == null) {
                title = getTitle(asMap.values());
            }
            if (title != null) {
                boolean exceed = title.length() > TITLE_LIMIT;
                if (exceed) {
                    title = title.substring(0, TITLE_LIMIT);
                }
                buffer.append(" \u2012 ").append(title);
                if (exceed) {
                    buffer.append('\u2026'); // "..."
                }
            }
            title = buffer.toString();
            assert isCollectionElement(title) : title;
            addTo = new NamedTreeNode(title, value);
            branch.add(addTo);
        }
        for (final Map.Entry<?,?> entry : asMap.entrySet()) {
            final Object element = entry.getValue();
            if (!ignore(element)) {
                final CharSequence name = formatElementName(type, (String) entry.getKey());
                assert !isCollectionElement(name.toString()) : name;
                final DefaultMutableTreeNode child = (branch instanceof PropertyTreeNode) ?
                        new PropertyTreeNode(name, element) : new NamedTreeNode(name, element);
                append(child, element, 0);
                addTo.add(child);
            }
        }
    }

    /**
     * Returns {@code true} if a node having the given label is the container of an element
     * of a collection. The rule is to return {@code true} if the label contains a pattern
     * like {@code "[any characters]"}, and if the characters before that pattern are not
     * valid identifier part.
     * <p>
     * The search for {@code '['} and {@code ']'} characters are okay if we assume that
     * those characters are not allowed in a UML or Java identifiers.
     *
     * @param  label The label of the node to test.
     * @return {@code true} if a node having the given label is an element of a collection.
     */
    private static boolean isCollectionElement(final String label) {
        int start = label.indexOf(OPEN_BRACKET);
        if (start >= 0) {
            final int end = label.indexOf(CLOSE_BRACKET, start);
            if (end > start) {
                while (--start >= 0) {
                    if (Character.isJavaIdentifierPart(label.charAt(start))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to figure out a title for the given metadata object, handling only special cases.
     * This method is invoked before the generic {@link #getTitle(Collection)} method below.
     *
     * @param  metadata The metadata object to examine.
     * @return A string representation, or {@code null} if there is no special case for this object.
     *
     * @see #getTitle(Collection)
     *
     * @since 3.20
     */
    private static String getTitleForSpecialCases(final Object metadata) {
        if (metadata instanceof Band) {
            final Band band = (Band) metadata;
            if (band.getSequenceIdentifier() == null) {
                // If the band contains an identifier, we will let the default 'getTitle'
                // method do its work. Only if there is no band identifier, we will try
                // to produce something better than the default 'getTitle' work.
                final Double min = band.getMinValue();
                final Double max = band.getMaxValue();
                if (min != null || max != null) {
                    return new MeasurementRange<>(Double.class, min, true, max, true, band.getUnits()).toString();
                }
            }
        }
        if (metadata instanceof GridSpatialRepresentation) {
            StringBuilder buffer = null;
            for (final Dimension dim : ((GridSpatialRepresentation) metadata).getAxisDimensionProperties()) {
                final Integer size = dim.getDimensionSize();
                if (size != null) {
                    if (buffer == null) {
                        buffer = new StringBuilder(16);
                    } else {
                        buffer.append('×');
                    }
                    buffer.append(size);
                }
            }
            if (buffer != null) {
                return buffer.toString();
            }
        }
        return null;
    }

    /**
     * Tries to figure out a title for the given metadata (represented as a the values
     * of a Map) and appends that title to the given buffer. If no title can be found,
     * return {@code null}.
     *
     * {@note An other strategy would be to define a <code>toShortString()</code> method
     * in abstract metadata and let subclasses implement the behavior they want. However
     * I'm not yet sure if feature deserves public API, since it is specific to the tree
     * formating. Furthermore we may want to ensure the same formatting for every metadata
     * implementation.}
     *
     * @param  values The values of the metadata for which to append a title.
     * @return The title, or {@code null} if none were found.
     *
     * @see #getTitleForSpecialCases(Object)
     */
    private String getTitle(final Collection<?> values) {
        String shortestName = null;
        for (int i=0; i<TITLE_TYPES.length-1; i++) { // Exclude the last type, which is Object.class
            final Class<?> baseType = TITLE_TYPES[i];
            for (final Object element : values) {
                if (baseType.isInstance(element)) {
                    String name;
                    if (element instanceof GenericName) {
                        final InternationalString i18n = ((GenericName) element).toInternationalString();
                        name = (i18n != null) ? i18n.toString(displayLocale) : element.toString();
                    } else if (element instanceof InternationalString) {
                        name = ((InternationalString) element).toString(displayLocale);
                    } else if (element instanceof CodeList<?>) {
                        name = Types.getCodeTitle((CodeList<?>) element, displayLocale);
                    } else {
                        name = element.toString();
                    }
                    if (name != null && !(name = name.trim()).isEmpty()) {
                        if (shortestName == null || name.length() < shortestName.length()) {
                            shortestName = name;
                        }
                    }
                }
            }
            if (shortestName != null) {
                return shortestName;
            }
        }
        /*
         * If no title were found, search if any element is a collection and search again in
         * collection elements. Return the shortest suitable element found, if any. We do this
         * check after the above loop because we want singleton element to have precedence.
         */
        final Collection<?>[] collections = new Collection<?>[values.size()];
        int count = 0;
        for (final Object element : values) {
            if (element instanceof Collection<?>) {
                collections[count++] = (Collection<?>) element;
            }
        }
        // Following loop will search for 'baseType' in the 'collections' array only.
        // Elements will be removed from the 'collections' array after being examined.
        for (final Class<?> baseType : TITLE_TYPES) {
            if (count == 0) { // May happen at any time because of the above-cited removal.
                break;
            }
            int newCount = 0;
            for (int i=0; i<count; i++) {
                final Collection<?> collection = collections[i];
                if (baseType.isAssignableFrom((collection instanceof CheckedContainer<?>) ?
                        ((CheckedContainer<?>) collection).getElementType() : Object.class))
                {
                    final String name = getTitle(collection);
                    if (name != null && (shortestName == null || name.length() < shortestName.length())) {
                        shortestName = name;
                    }
                } else {
                    collections[newCount++] = collection;
                }
            }
            count = newCount;
            if (shortestName != null) {
                return shortestName;
            }
        }
        return null;
    }

    /**
     * Returns a format of the given type.
     *
     * @param  <T>  The compile type of the {@code type} argument.
     * @param  type The formatter type.
     * @return A formatter of the given type.
     */
    @SuppressWarnings("unchecked")
    private <T extends Format> T getFormat(final Class<T> type) {
        final String name = type.getSimpleName();
        Format format;
        switch (name) {
            case "DateFormat":   format = dateFormat;   break;
            case "NumberFormat": format = numberFormat; break;
            case "TreeFormat":   format = treeFormat;   break;
            default: throw new AssertionError(type);
        }
        if (format == null) {
            switch (name) {
                case "DateFormat":   dateFormat   = format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, formatLocale); break;
                case "NumberFormat": numberFormat = format = NumberFormat.getNumberInstance(formatLocale); break;
                case "TreeFormat":   treeFormat   = format = new TreeFormat(); break;
                default: throw new AssertionError(type);
            }
        }
        return (T) format;
    }

    /**
     * Formats a human-readable string from the given number.
     *
     * @param  value The number to format.
     * @return A string representation of the given value for the {@linkplain #formatLocale}.
     */
    protected String formatNumber(final Number value) {
        final NumberFormat format = getFormat(NumberFormat.class);
        if (Numbers.isInteger(value.getClass())) {
            format.setMaximumFractionDigits(0);
        } else {
            InternalUtilities.configure(format, value.doubleValue(), 12);
        }
        return format.format(value);
    }

    /**
     * Implementation of {@link #ignore(Object)} without the recursive checks in collections.
     * We usually don't need this recursivity, and omitting it allows us to omit the checks
     * against infinite recursivity.
     */
    private static boolean ignoreElement(final Object value) {
        return PropertyAccessor.isEmpty(value) || (value instanceof Identifier &&
                ((Identifier) value).getAuthority() instanceof NonMarshalledAuthority<?>);
    }

    /**
     * Returns {@code true} if the given element should be ignored. An element should be ignored
     * if empty, or if it is a XML identifier (because we marshal them in a special way), or a
     * collection containing only elements to omit.
     *
     * @since 3.19
     */
    private static boolean ignore(final Object value) {
        if (ignoreElement(value)) {
            return true;
        }
        if (!(value instanceof Iterable<?>)) {
            return false;
        }
        for (final Object element : (Iterable<?>) value) {
            if (!ignoreElement(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Formats a human-readable string from given code list. The default implementation
     * derives the string from the programmatic name.
     *
     * @param  code The code to format.
     * @return A string representation of the given code.
     */
    protected String formatCodeList(final CodeList<?> code) {
        return code.name().trim().replace('_', ' ').toLowerCase(displayLocale);
    }

    /**
     * Formats a human-readable string from the given class or property name. The given string is
     * either a {@linkplain Classes#getShortName(Class) short class name}, or a property name
     * using the {@linkplain KeyNamePolicy#JAVABEANS_PROPERTY Java Beans convention}.
     * <p>
     * The default implementation converts the given string to a sentence by inserting spaces
     * before the upper case letters. Subclasses can override this method for customizing the
     * output, including localization.
     * <p>
     * <b>NOTE:</b> Names converted in a way different than the default implementation may not
     * be recognized by the {@link #parse(TreeNode, Object)} method.
     *
     * @param  parent The interface that contain the property to format.
     * @param  name The <cite>Java beans</cite> name of the property to format,
     *              or {@code null} for providing a label for the interface itself.
     * @return The label, as a {@link String} or {@link InternationalString} instance.
     */
    protected CharSequence formatElementName(final Class<?> parent, String name) {
        if (name == null) {
            name = Classes.getShortName(parent);
        }
        name = name.trim();
        final int length = name.length();
        if (length != 0) {
            final StringBuilder buffer = new StringBuilder(name.length() + 8);
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
                        buffer.append(name, base, split).append(' ');
                        base = split;
                    }
                }
                previousIsUpper = currentIsUpper;
            }
            final String candidate = buffer.append(name, base, name.length()).toString();
            if (!candidate.equals(name)) {
                // Holds a reference to this new String object only if it worth it.
                name = candidate;
            }
        }
        return name;
    }

    /**
     * Returns the interface type for the given name. This method is invoked by
     * {@link #parse(TreeNode)} for inferring the metadata type from the name of the root node.
     * For example if the current {@linkplain #standard} is {@link MetadataStandard#ISO_19115}
     * and the name of the given root node is {@code "Metadata"}, then this method returns
     * the {@link org.opengis.metadata.Metadata} interface.
     *
     * @param  name The root node name.
     * @return The interface of the root node.
     * @throws ParseException If the given name is unknown.
     *
     * @since 3.20
     */
    protected Class<?> getTypeForName(final String name) throws ParseException {
        if (standard == MetadataStandard.ISO_19115) {
            switch (name) {
                case "Metadata": return org.opengis.metadata.Metadata.class;
                case "Citation": return org.opengis.metadata.citation.Citation.class;
            }
        }
        throw new ParseException(Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, name), 0);
    }
}
