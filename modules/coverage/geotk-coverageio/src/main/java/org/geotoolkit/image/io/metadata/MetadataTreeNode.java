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
import javax.imageio.metadata.IIOMetadataFormat;
import org.w3c.dom.Node;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.gui.swing.tree.NamedTreeNode;
import org.geotoolkit.gui.swing.tree.TreeTableNode;

import static org.geotoolkit.image.io.metadata.MetadataTreeTable.COLUMN_COUNT;


/**
 * A node in the tree produced by {@link MetadataTreeTable}. The value returned by
 * {@link #toString()} is the programmatic name of the element or attribute, but this
 * is used only for debugging purpose. The columns of this node to be displayed in
 * {@code TreeTable} widget are documented in {@link MetadataTreeTable} javadoc.
 * <p>
 * All those information can be accessed programmatically with the getter methods
 * defined in this class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 */
@SuppressWarnings("serial")
final class MetadataTreeNode extends NamedTreeNode implements TreeTableNode {
    /**
     * The tree which is the owner of this node.
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
    private transient ValidValues validValues;

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
     */
    public String getLabel() {
        if (label == null) {
            final Locale locale = tree.getLocale();
            final IIOMetadataFormat format = tree.format;
            if (attribute == null){
                label = format.getElementDescription(element, locale);
            } else {
                label = format.getAttributeDescription(element, attribute, locale);
            }
            if (label == null) {
                final StringBuilder buffer = StringUtilities.separateWords(toString());
                if (buffer.length() != 0) {
                    buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
                }
                label = buffer.toString();
            }
        }
        return label;
    }

    /**
     * Returns the range of occurences that are valid for this node.
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
     * {@link java.util.Collection} types are converted to array type. If the node is not
     * allowed to store any object, then this method returns {@link Void#TYPE}.
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
        return type;
    }

    /**
     * Returns the range or the enumeration of valid values. If there is no restriction
     * on the valid values, then this method returns null.
     */
    public ValidValues getValidValues() {
        ValidValues valids = validValues;
        if (valids == null) {
            valids = ValidValues.UNRESTRICTED; // Will be the default.
            final IIOMetadataFormat format = tree.format;
            if (attribute == null) {
                final int type = format.getObjectValueType(element);
                switch (type & ~(IIOMetadataFormat.VALUE_RANGE_MIN_INCLUSIVE_MASK |
                                 IIOMetadataFormat.VALUE_RANGE_MAX_INCLUSIVE_MASK))
                {
                    case IIOMetadataFormat.VALUE_RANGE: {
                        valids = new ValidValues.Range(type,
                                format.getObjectMinValue(element),
                                format.getObjectMaxValue(element));
                        break;
                    }
                    case IIOMetadataFormat.VALUE_ENUMERATION: {
                        valids = new ValidValues.Enumeration(
                                format.getObjectEnumerations(element));
                        break;
                    }
                }
            } else {
                final int type = format.getAttributeValueType(element, attribute);
                switch (type & ~(IIOMetadataFormat.VALUE_RANGE_MIN_INCLUSIVE_MASK |
                                 IIOMetadataFormat.VALUE_RANGE_MAX_INCLUSIVE_MASK))
                {
                    case IIOMetadataFormat.VALUE_RANGE: {
                        valids = new ValidValues.Range(type,
                                format.getAttributeMinValue(element, attribute),
                                format.getAttributeMaxValue(element, attribute));
                        break;
                    }
                    case IIOMetadataFormat.VALUE_ENUMERATION: {
                        valids = new ValidValues.Enumeration(
                                format.getAttributeEnumerations(element, attribute));
                        break;
                    }
                }
            }
            validValues = valids;
        }
        return valids.equals(ValidValues.UNRESTRICTED) ? null : valids;
    }

    /**
     * The default value, or {@code null} if not yet determined. If there is no default
     * value, then this method returns {@code null}.
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
            if (value == null) {
                value = "";
            } else {
                value = convert(value);
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
            if (node != null) {
                // TODO
            }
            super.setUserObject(value);
        }
        return value.equals("") ? null : value;
    }

    /**
     * Converts the given object to the type expected by this node.
     * Returns the object unchanged if no converter is found.
     */
    private Object convert(Object value) {
        if (value != null) {
            final Class<?> target = getValueType();
            if (target != null && !target.equals(Void.TYPE)) {
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
        if (column != 0 && tree.getRootIIO() == null) {
            column++; // Skip the "value" column if it doesn't exist.
        }
        return column;
    }

    /**
     * Returns the number of columns supported by this {@code TreeTableNode}.
     * The number of columns shall include the column used for displaying the node itself.
     *
     * @return The number of columns this node supports.
     */
    @Override
    public int getColumnCount() {
        return tree.getRootIIO() != null ? COLUMN_COUNT : COLUMN_COUNT-1;
    }

    /**
     * Returns the most specific superclass of values that can be stored in the given column.
     *
     * @param  column The column to query.
     * @return The most specific superclass of legal values in the queried column.
     */
    @Override
    public Class<?> getColumnClass(final int column) {
        switch (canonical(column)) {
            case 0:  return String.class;       // The label.
            case 2:  return Class.class;        // The base type of values.
            case 3:  return NumberRange.class;  // The range of occurences
            case 4:  return ValidValues.class;  // The restrictions on valid values.
            case 5:                             // The default value.
            case 1:  return getValueType();     // The actual value.
            default: return Object.class;
        }
    }

    /**
     * Gets the value for this node that corresponds to a particular tabular column.
     * Same values are calculated when first requested and cached for future reuse.
     *
     * {@note If the behavior of this method is changed, then <code>IIOMetadatapanel</code>
     *        implementation needs to be modified accordingly.}
     *
     * @param  column The column to query.
     * @return The value for the queried column.
     */
    @Override
    public Object getValueAt(final int column) {
        switch (canonical(column)) {
            case 0: return getLabel();
            case 1: return getUserObject();
            case 2: return getValueType();
            case 3: return getOccurences();
            case 4: return getValidValues();
            case 5: return getDefaultValue();
            case COLUMN_COUNT:
            // The later is added only for making sure at compile-time that
            // we are not declaring more columns than the expected number.
            default: return null;
        }
    }

    /**
     * Sets the value for the given column.
     *
     * @param value The value to set.
     * @param column The column to set the value on.
     */
    @Override
    public void setValueAt(Object value, int column) {
    }

    /**
     * Determines whether the specified column is editable.
     * By default there is no editable column.
     *
     * @param  column The column to query.
     * @return {@code true} if the column is editable, false otherwise.
     */
    @Override
    public boolean isEditable(int column) {
        return false;
    }
}
