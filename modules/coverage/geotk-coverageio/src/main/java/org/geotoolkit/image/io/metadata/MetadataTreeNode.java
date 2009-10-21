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

import java.util.Set;
import java.util.Locale;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.metadata.ValueRestriction;
import org.geotoolkit.gui.swing.tree.NamedTreeNode;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.image.io.metadata.MetadataTreeTable.COLUMN_COUNT;
import static org.geotoolkit.image.io.metadata.MetadataTreeTable.VALUE_COLUMN;


/**
 * A node in the tree produced by {@link MetadataTreeTable}. The value returned by the
 * {@link #toString()} methods is the programmatic name of the element or attribute represented
 * by this node. The values returned by the {@link #getValueAt(int)} method are the values for
 * the columns documented in the {@link MetadataTreeTable} javadoc. Those values are also
 * accessible by specific getter methods:
 * <p>
 * <ol>
 *   <li>{@link #getLabel()}</li>
 *   <li>{@link #getDescription()}</li>
 *   <li>{@link #getUserObject()} (this column may be omitted - see {@link MetadataTreeTable})</li>
 *   <li>{@link #getValueType()}</li>
 *   <li>{@link #getOccurences()}</li>
 *   <li>{@link #getValueRestriction()}</li>
 *   <li>{@link #getDefaultValue()}</li>
 * </ol>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
public final class MetadataTreeNode extends NamedTreeNode implements TreeTableNode {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3458235875074371134L;

    /**
     * The tree which is the owner of this node.
     * This is not allowed to be null.
     */
    private final MetadataTreeTable tree;

    /**
     * The name of the element to be represented as a node.
     * This field is not allowed to be {@code null}.
     */
    private final String element;

    /**
     * If this node is actually an attribute that belong to the above element,
     * the name of that attribute. Otherwise {@code null}.
     */
    private final String attribute;

    /**
     * The label to show in the first column.
     */
    private transient String label;

    /**
     * The description. Will be fetched only when first needed.
     */
    private transient String description;

    /**
     * The base type of values, or {@code null} if not yet determined. If the value type is a
     * collection, then it will be represented as an array type. If the node is not allowed to
     * store any object, then this method returns {@link Void#TYPE}.
     */
    private transient Class<?> valueType;

    /**
     * The minimum and maximum occurences of children in this node,
     * or {@code null} if not yet determined.
     */
    private transient NumberRange<Integer> occurences;

    /**
     * The valid values as a range or a comma-separated list, or {@code null} if not yet
     * determined. If we have determined that there is no list of valid values, then this
     * will be set to {@link ValidValues#UNRESTRICTED}.
     */
    private transient ValueRestriction validValues;

    /**
     * The default value, or {@code null} if not yet determined. If we have determined
     * that there is no default value, then this will be set to an empty string.
     */
    private transient Object defaultValue;

    /**
     * Creates a new node for a metadata element.
     *
     * @param tree The tree which is the owner of this node.
     * @param element The element name.
     */
    MetadataTreeNode(final MetadataTreeTable tree, final String element) {
        super(element);
        this.tree      = tree;
        this.element   = element;
        this.attribute = null;
        setAllowsChildren(true);
    }

    /**
     * Creates a new node for a metadata attribute.
     *
     * @param tree The tree which is the owner of this node.
     * @param element The element name which own the attribute.
     * @param attribute The attribute name.
     */
    MetadataTreeNode(final MetadataTreeTable tree, final String element, final String attribute) {
        super(attribute);
        this.tree      = tree;
        this.element   = element;
        this.attribute = attribute;
        setAllowsChildren(false);
    }

    /**
     * Returns the parent of this node.
     *
     * @return The parent of this node, or {@code null} if none.
     */
    @Override
    public MetadataTreeNode getParent() {
        return (MetadataTreeNode) super.getParent();
    }

    /**
     * Returns the display label. It will be constructed from the programmatic name
     * (usually the UML identifier) when first needed and cached for future reuse.
     *
     * @return The label inferred from the programmatic node name (never null).
     */
    public String getLabel() {
        if (label == null) {
            final StringBuilder buffer = StringUtilities.separateWords(toString());
            if (buffer.length() != 0) {
                buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
            }
            label = buffer.toString();
        }
        return label;
    }

    /**
     * Returns the description.
     *
     * @return The description, or the {@linkplain #getLabel label}
     *         if there is no explicit description.
     */
    public String getDescription() {
        String description = this.description;
        if (description == null) {
            final Locale locale = tree.getLocale();
            final IIOMetadataFormat format = tree.format;
            if (attribute == null){
                description = format.getElementDescription(element, locale);
            } else {
                description = format.getAttributeDescription(element, attribute, locale);
            }
            if (description == null) {
                description = "";
            }
            this.description = description;
        }
        return description.equals("") ? null : description;
    }

    /**
     * Returns the range of occurences that are valid for this node. This method never returns
     * {@code null} since the {@linkplain NumberRange#getMinValue() minimum value} of occurences
     * is at least 0.
     *
     * @return The range of occurences (never null).
     */
    public NumberRange<Integer> getOccurences() {
        if (occurences == null) {
            Integer min=0, max=1;
            final IIOMetadataFormat format = tree.format;
            if (attribute == null) {
                if (parent != null) {
                    final String parent = getParent().element;
                    switch (format.getChildPolicy(parent)) {
                        case IIOMetadataFormat.CHILD_POLICY_REPEAT: {
                            min = format.getElementMinChildren(parent);
                            max = format.getElementMaxChildren(parent);
                            break;
                        }
                        case IIOMetadataFormat.CHILD_POLICY_ALL: {
                            min = 1;
                            break;
                        }
                        case IIOMetadataFormat.CHILD_POLICY_EMPTY: {
                            max = 0;
                            break;
                        }
                    }
                }
            } else {
                switch (format.getAttributeValueType(element, attribute)) {
                    case IIOMetadataFormat.VALUE_LIST: {
                        min = format.getAttributeListMinLength(element, attribute);
                        max = format.getAttributeListMaxLength(element, attribute);
                        break;
                    }
                    default: {
                        if (format.isAttributeRequired(element, attribute)) {
                            min = 1;
                        }
                        break;
                    }
                }
            }
            // Consider MIN|MAX_VALUE as unbounded.
            if (min == Integer.MIN_VALUE) min = null;
            if (max == Integer.MAX_VALUE) max = null;
            occurences = new NumberRange<Integer>(Integer.class, min, max);
        }
        return occurences;
    }

    /**
     * Returns the type of user object that can be associated to the element or attribute.
     * {@link java.util.Collection} types are converted to array types. If the node is not
     * allowed to store any object, then this method returns {@code null}.
     *
     * @return The type of user object, or {@code null} if this node does not allow value.
     */
    @SuppressWarnings("fallthrough")
    public Class<?> getValueType() {
        Class<?> type = valueType;
        if (type == null) {
            type = Void.TYPE; // The default value.
            boolean isArray = false;
            final IIOMetadataFormat format = tree.format;
            if (attribute == null) {
                switch (format.getObjectValueType(element)) {
                    case IIOMetadataFormat.VALUE_NONE: break;
                    case IIOMetadataFormat.VALUE_LIST: isArray = true; // Fall through
                    default: type = format.getObjectClass(element); break;
                }
            } else {
                switch (format.getAttributeValueType(element, attribute)) {
                    case IIOMetadataFormat.VALUE_LIST: isArray = true; break;
                }
                switch (format.getAttributeDataType(element, attribute)) {
                    case IIOMetadataFormat.DATATYPE_STRING:  type = String.class;  break;
                    case IIOMetadataFormat.DATATYPE_INTEGER: type = Integer.class; break;
                    case IIOMetadataFormat.DATATYPE_DOUBLE:  type = Double.class;  break;
                    case IIOMetadataFormat.DATATYPE_FLOAT:   type = Float.class;   break;
                    case IIOMetadataFormat.DATATYPE_BOOLEAN: type = Boolean.class; break;
                }
            }
            if (isArray) {
                type = Classes.changeArrayDimension(type, 1);
            }
            valueType = type;
        }
        return Void.TYPE.equals(type) ? null : type;
    }

    /**
     * Returns the range or the enumeration of valid values. If there is no restriction
     * on the valid values, then this method returns {@code null}.
     *
     * @return A description of the valid values, or {@code null} if none.
     */
    public ValueRestriction getValueRestriction() {
        ValueRestriction valids = validValues;
        if (valids == null) {
            valids = ValidValues.UNRESTRICTED; // Will be the default.
            final IIOMetadataFormat format = tree.format;
            if (attribute == null) {
                final int type = format.getObjectValueType(element);
                switch (type & ~(IIOMetadataFormat.VALUE_RANGE_MIN_INCLUSIVE_MASK |
                                 IIOMetadataFormat.VALUE_RANGE_MAX_INCLUSIVE_MASK))
                {
                    case IIOMetadataFormat.VALUE_RANGE: {
                        final Class<?> datatype = format.getObjectClass(element);
                        valids = ValidValues.range(datatype, type,
                                format.getObjectMinValue(element),
                                format.getObjectMaxValue(element));
                        break;
                    }
                    case IIOMetadataFormat.VALUE_ENUMERATION: {
                        valids = new ValidValues(format.getObjectEnumerations(element));
                        break;
                    }
                }
            } else {
                final int type = format.getAttributeValueType(element, attribute);
                switch (type & ~(IIOMetadataFormat.VALUE_RANGE_MIN_INCLUSIVE_MASK |
                                 IIOMetadataFormat.VALUE_RANGE_MAX_INCLUSIVE_MASK))
                {
                    case IIOMetadataFormat.VALUE_RANGE: {
                        final int datatype = format.getAttributeDataType(element, attribute);
                        valids = ValidValues.range(datatype, type,
                                format.getAttributeMinValue(element, attribute),
                                format.getAttributeMaxValue(element, attribute));
                        break;
                    }
                    case IIOMetadataFormat.VALUE_ENUMERATION: {
                        valids = new ValidValues(format.getAttributeEnumerations(element, attribute));
                        break;
                    }
                }
            }
            validValues = valids;
        }
        return valids.equals(ValidValues.UNRESTRICTED) ? null : valids;
    }

    /**
     * Returns the default value, or {@code null} if none.
     *
     * @return The default value, or {@code null} if none.
     */
    public Object getDefaultValue() {
        Object value = defaultValue;
        if (value == null) {
            final IIOMetadataFormat format = tree.format;
            if (attribute == null) {
                switch (format.getObjectValueType(element)) {
                    case IIOMetadataFormat.VALUE_NONE: break;
                    default: value = format.getObjectDefaultValue(element); break;
                }
            } else {
                value = format.getAttributeDefaultValue(element, attribute);
            }
            value = convert(value);
            if (value == null) {
                value = "";
            }
            defaultValue = value;
        }
        return value.equals("") ? null : value;
    }

    /**
     * Returns the value of this node, or {@code null} if none.
     */
    @Override
    public Object getUserObject() {
        Object value = super.getUserObject();
        if (value == null) {
            Node node = tree.getRootIIO();
            if (node instanceof Element) {
                final NodeList elements = ((Element) node).getElementsByTagName(element);
                if (elements != null && elements.getLength() != 0) {
                    node = elements.item(0);
                    if (attribute != null) {
                        if (node instanceof Element) {
                            value = ((Element) node).getAttribute(attribute);
                        }
                    } else {
                        if (node instanceof IIOMetadataNode) {
                            value = ((IIOMetadataNode) node).getUserObject();
                        }
                        if (value == null) {
                            value = node.getNodeValue();
                        }
                    }
                }
            }
            value = convert(value);
            if (value == null) {
                value = ""; // Means "no value".
            }
            super.setUserObject(value);
        }
        return value.equals("") ? null : value;
    }

    /**
     * Sets the value of this node. The given value must be compliant with the restrictions
     * specified by {@link #getValueType()} and {@link #getValueRestriction()}.
     *
     * @param  value The value to give to this node (can be null).
     * @throws IllegalArgumentException if the given value is not an instance of the
     *         {@linkplain #getValueType expected type} or violates a
     *         {@linkplain #getValueRestriction() value restriction}.
     *
     * @see #setValueAt(Object, int)
     */
    @Override
    public void setUserObject(Object value) throws IllegalArgumentException {
        final Class<?> type = getValueType();
        if (type == null) {
            throw new IllegalArgumentException(error(Errors.Keys.BAD_PARAMETER_$2, value));
        }
        if (value != null) {
            if (!type.isInstance(value)) {
                throw new IllegalArgumentException(error(Errors.Keys.BAD_PARAMETER_TYPE_$2, value.getClass()));
            }
            final ValueRestriction r = getValueRestriction();
            if (r != null) {
                final Set<?> validValues = r.validValues;
                if (validValues != null && !validValues.contains(value)) {
                    throw new IllegalArgumentException(error(Errors.Keys.BAD_PARAMETER_$2, value));
                }
                final NumberRange<?> range = r.range;
                // We know we can cast to Comparable since 'value' is an instance of 'type'.
                if (range != null && !range.contains((Comparable<?>) value)) {
                    throw new IllegalArgumentException(Errors.getResources(tree.getLocale())
                            .getString(Errors.Keys.VALUE_OUT_OF_BOUNDS_$3,
                            value, range.getMinimum(true), range.getMaximum(true)));
                }
            }
        } else {
            value = ""; // Sentinal value meaning "evaluated to null".
        }
        super.setUserObject(value);
    }

    /**
     * Formats a localized error message. This method is used only for the error messages
     * where the first argument is the parameter name.
     */
    private String error(final int key, final Object argument) {
        return Errors.getResources(tree.getLocale()).getString(key, getLabel(), argument);
    }

    /**
     * Converts the given object to the type expected by this node.
     * Returns the object unchanged if no converter is found.
     */
    private Object convert(Object value) {
        if (value != null) {
            final Class<?> target = getValueType();
            if (target != null) {
                value = tree.converters.tryConvert(value, target);
            }
        }
        return value;
    }

    /**
     * Returns the column number to use in {@code switch} statements.
     *
     * @param  column The column visible in public API.
     * @return The column number to use in {@code switch} statements.
     */
    private int canonical(int column) {
        if (column >= VALUE_COLUMN && tree.getRootIIO() == null) {
            column++; // Skip the "value" column if it doesn't exist.
        }
        return column;
    }

    /**
     * Returns the number of columns supported by this {@code TreeTableNode}. This method returns
     * {@value org.geotoolkit.image.io.metadata.MetadataTreeTable#COLUMN_COUNT} if the tree table
     * contains the data of an {@link IIOMetadata} object, or the above value minus one otherwise.
     *
     * @return The number of columns this node supports.
     */
    @Override
    public int getColumnCount() {
        return (tree.getRootIIO() != null) ? COLUMN_COUNT : COLUMN_COUNT-1;
    }

    /**
     * Returns the most specific superclass of values that can be stored in the given column.
     * The columns are numbered from 0 inclusive to {@link #getColumnCount()} exclusive. They
     * are the same numbers than the ones used for the {@link #getValueAt(int)} method.
     *
     * @param  column The column to query.
     * @return The most specific superclass of legal values in the queried column.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public Class<?> getColumnClass(final int column) {
        switch (canonical(column)) {
            case 0:                                 // The label.
            case 1:  return String.class;           // The description.
            case 3:  return Class.class;            // The base type of values.
            case 4:  return NumberRange.class;      // The range of occurences
            case 5:  return ValueRestriction.class; // The restrictions on valid values.
            case 6:                                 // The default value.
            case 2: {                               // The actual value.
                final Class<?> type = getValueType();
                if (type != null) {
                    return type;
                }
                // fallthrough
            }
            default: return Object.class;
        }
    }

    /**
     * Gets the value for this node that corresponds to a particular tabular column.
     * The columns are numbered from 0 inclusive to {@link #getColumnCount()} exclusive.
     * Each column maps to a getter methods of this class, in this order:
     * <p>
     * <ol>
     *   <li>{@link #getLabel()}</li>
     *   <li>{@link #getDescription()}</li>
     *   <li>{@link #getUserObject()} (this column may be omitted - see below)</li>
     *   <li>{@link #getValueType()}</li>
     *   <li>{@link #getOccurences()}</li>
     *   <li>{@link #getValueRestriction()}</li>
     *   <li>{@link #getDefaultValue()}</li>
     * </ol>
     * <p>
     * Note that if the tree table does not map a {@link IIOMetadata} object, then there is
     * no column for {@code getUserObject()} and the number of all following columns are
     * shifted by one.
     *
     * {@note If the behavior of this method is changed, then <code>IIOMetadataPanel</code>
     *        implementation needs to be modified accordingly.}
     *
     * @param  column The column to query.
     * @return The value for the queried column.
     */
    @Override
    public Object getValueAt(final int column) {
        switch (canonical(column)) {
            case 0: return getLabel();
            case 1: return getDescription();
            case 2: return getUserObject();
            case 3: return getValueType();
            case 4: return getOccurences();
            case 5: return getValueRestriction();
            case 6: return getDefaultValue();
            case COLUMN_COUNT:
            // The later is added only for making sure at compile-time that
            // we are not declaring more columns than the expected number.
            default: return null;
        }
    }

    /**
     * Sets the value for the given column. This method {@linkplain #setUserObject(Object) set
     * the user object} to the given value only if the all the following conditions are meet:
     * <p>
     * <ul>
     *   <li>The given column is the {@link MetadataTreeTable#VALUE_COLUMN VALUE_COLUMN} and
     *       that column exists (i.e. an instance of {@link IIOMetadata} has been specified
     *       to {@link MetadataTreeTable}).</li>
     *   <li>This node accepts values (i.e. the value type is not {@link IIOMetadataFormat#VALUE_NONE}).</li>
     *   <li>The given value is an instance of the {@linkplain #getValueType() expected type}.</li>
     *   <li>The given value, if non-null, is compliant with the {@linkplain #getValueRestriction()
     *       value restrictions}.</li>
     * </ul>
     * <p>
     * Otherwise this method does nothing.
     *
     * @param value The value to set.
     * @param column The column to set the value on.
     */
    @Override
    public void setValueAt(final Object value, final int column) {
        if (column == VALUE_COLUMN && tree.getRootIIO() != null) {
            try {
                setUserObject(value);
            } catch (IllegalArgumentException e) {
                /*
                 * Ignoring the exception is conform to the specification of this method. This
                 * is typically a consequence of the user having edited a cell in a JTable with
                 * an invalid value. The JTable behavior is to discart the user edition and restore
                 * the previous value. If we want a more sophesticated behavior with a warning that
                 * the user provided an invalid value, then we need a custom TableCellEditor.
                 */
                Logging.recoverableException(MetadataTreeNode.class, "setValueAt", e);
            }
        }
    }

    /**
     * Determines whether the specified column is editable. By default only the
     * {@link MetadataTreeTable#VALUE_COLUMN} is editable, and only if that column exists.
     * This column does not exist if no {@link IIOMetadata} instance was specified
     * to {@link MetadataTreeTable}.
     *
     * @param  column The column to query.
     * @return {@code true} if the column is editable, false otherwise.
     */
    @Override
    public boolean isEditable(final int column) {
        return column == VALUE_COLUMN && tree.getRootIIO() != null;
    }
}
