/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.image.io.metadata.MetadataTreeTable;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * A metadata format that describe an existing tree of metadata. The element names and attribute
 * names are extracted from the node names. The type of attribute node ({@link #DATATYPE_STRING},
 * {@link #DATATYPE_DOUBLE}, <i>etc.</i>) can optionally be stored.
 * <p>
 * Subclasses can override the {@link #getDataType(Node, int)} method in order to specify the
 * {@code DATATYPE_*} constant to associate to an attribute.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08 (derived from 2.4)
 * @module
 */
public abstract class SampleMetadataFormat implements IIOMetadataFormat {
    /**
     * The name of the root element of the format.
     */
    private final String rootName;

    /**
     * The elements, inferred from the nodes.
     * Will be created when first needed.
     */
    private Map<String, Element> elements;

    /**
     * A description of an element. the inherited {@code HashMap} is the set of attributes
     * (keys), where the values are the data type.
     */
    @SuppressWarnings("serial")
    private static final class Element extends LinkedHashMap<String,Integer> {
        /**
         * The name of child elements, or {@code null} if none.
         */
        String[] childs;
    }

    /**
     * Creates a new {@code SampleMetadataFormat} instance.
     *
     * @param rootName The name of the root node.
     */
    public SampleMetadataFormat(final String rootName) {
        this.rootName = rootName;
    }

    /**
     * Returns the root node of metadata data. This will be invoked when first needed
     * for inferring the metadata format.
     *
     * @return The root node of metadata.
     */
    protected abstract Node getDataRootNode();

    /**
     * Adds the given node and all its children. This method is first invoked by
     * the constructor, then invokes itself recursively for building the tree.
     *
     * @param  node The root of the tree to append.
     * @return The name of the given node.
     */
    private String addNode(final Node node) {
        final String name = node.getNodeName();
        Element element = elements.get(name);
        if (element == null) {
            element = new Element();
            elements.put(name, element);
        }
        /*
         * Add the attributes.
         */
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final int length = attributes.getLength();
            for (int i=0; i<length; i++) {
                final Node attr = attributes.item(i);
                final String attrName = attr.getNodeName();
                final int type = getDataType(attr, i);
                final Integer old = element.put(attrName, type);
                if (old != null && old.intValue() != type) {
                    // In case of mismatch, use the most generic type.
                    element.put(attrName, DATATYPE_STRING);
                }
            }
        }
        /*
         * Add the child elements.
         */
        final NodeList childs = node.getChildNodes();
        if (childs != null) {
            final int length = childs.getLength();
            if (length != 0) {
                final Set<String> childNames;
                if (element.childs != null) {
                    childNames = new LinkedHashSet<>(Arrays.asList(element.childs));
                } else {
                    childNames = new LinkedHashSet<>(hashMapCapacity(length));
                }
                for (int i=0; i<length; i++) {
                    childNames.add(addNode(childs.item(i)));
                }
                element.childs = childNames.toArray(new String[childNames.size()]);
            }
        }
        return name;
    }

    /**
     * Returns the element of the given name. The metadata format will be created
     * when this method is first invoked.
     *
     * @throws IllegalArgumentException If there is no element for the given name.
     */
    private synchronized Element getElement(final String elementName) throws IllegalArgumentException {
        if (elements == null) {
            elements = new LinkedHashMap<>();
            addNode(getDataRootNode());
        }
        final Element element = elements.get(elementName);
        if (element == null) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NO_SUCH_ELEMENT_NAME_$1, elementName));
        }
        return element;
    }

    /**
     * Invoked by the constructor for determining the data type of the given attribute.
     * The default implementation returns {@link #DATATYPE_STRING} in all cases.
     * Subclasses can override this method if they can determine a more accurate type.
     *
     * @param  attribute The attribute node.
     * @param  index The attribute index in the parent element.
     * @return One of the {@code DATATYPE_*} constants.
     */
    protected int getDataType(final Node attribute, final int index) {
        return DATATYPE_STRING;
    }

    /**
     * Returns the name of the root element of the format.
     */
    @Override
    public String getRootName() {
        return rootName;
    }

    /**
     * Returns {@code true} if the given element is allowed to appear.
     * The default implementation returns unconditionally {@code true}.
     */
    @Override
    public boolean canNodeAppear(final String elementName, final ImageTypeSpecifier imageType) {
        return true;
    }

    /**
     * Returns the minimum number of children.
     * The default implementation returns {@code 0} (no minimum).
     */
    @Override
    public int getElementMinChildren(final String elementName) {
        return 0;
    }

    /**
     * Returns the minimum number of children.
     * The default implementation returns {@code MAX_VALUE} (no maximum).
     */
    @Override
    public int getElementMaxChildren(final String elementName) {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns a description of the element.
     * The default implementation returns {@code null} (no description).
     */
    @Override
    public String getElementDescription(final String elementName, final Locale locale) {
        return null;
    }

    /**
     * Returns the legal pattern of children.
     * The default implementation returns {@link #CHILD_POLICY_SOME}.
     */
    @Override
    public int getChildPolicy(final String elementName) {
        return CHILD_POLICY_SOME;
    }

    /**
     * Returns the names of the element which are allowed to be children of the named element.
     */
    @Override
    public String[] getChildNames(final String elementName) {
        final Element element = getElement(elementName);
        return (element.childs != null) ? element.childs.clone() : null;
    }

    /**
     * Returns the names of the attributes that may be associated with the named element.
     */
    @Override
    public String[] getAttributeNames(final String elementName) {
        final Set<String> attributes = getElement(elementName).keySet();
        return attributes.toArray(new String[attributes.size()]);
    }

    /**
     * Returns whether the values of the given attribute within the named element are arbitrary.
     * The default implementation returns {@link #VALUE_ARBITRARY}.
     */
    @Override
    public int getAttributeValueType(final String elementName, final String attrName) {
        return VALUE_ARBITRARY;
    }

    /**
     * Returns the format and interpretation of the value of the given attribute within the
     * named element.
     */
    @Override
    public int getAttributeDataType(final String elementName, final String attrName) {
        final Integer type = getElement(elementName).get(attrName);
        if (type == null) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NO_SUCH_ATTRIBUTE_$1, attrName));
        }
        return type;
    }

    /**
     * Returns true if the named attribute must be present within the named element.
     * The default implementation returns {@code false}.
     */
    @Override
    public boolean isAttributeRequired(final String elementName, final String attrName) {
        return false;
    }

    /**
     * Returns the default value of the named attribute.
     * The default implementation returns {@code null}.
     */
    @Override
    public String getAttributeDefaultValue(final String elementName, final String attrName) {
        return null;
    }

    /**
     * Returns the legal enumerated values for the given attribute within the named element.
     * The default implementation throws {@link IllegalArgumentException} since no attribute
     * are enumerated values.
     */
    @Override
    public String[] getAttributeEnumerations(final String elementName, final String attrName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the minimum legal value for the attribute. The default implementation
     * throws {@link IllegalArgumentException} since no attribute are defined as range.
     */
    @Override
    public String getAttributeMinValue(final String elementName, final String attrName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the maximum legal value for the attribute. The default implementation
     * throws {@link IllegalArgumentException} since no attribute are defined as range.
     */
    @Override
    public String getAttributeMaxValue(final String elementName, final String attrName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the minimum number of items in the attribute.
     * The default implementation returns {@code 0} (no minimum).
     */
    @Override
    public int getAttributeListMinLength(final String elementName, final String attrName) {
        return 0;
    }

    /**
     * Returns the maximum number of items in the attribute.
     * The default implementation returns {@code MAX_VALUE} (no maximum).
     */
    @Override
    public int getAttributeListMaxLength(final String elementName, final String attrName) {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns a description of the named attribute.
     * The default implementation returns {@code null}.
     */
    @Override
    public String getAttributeDescription(final String elementName, final String attrName, final Locale locale) {
        return null;
    }

    /**
     * Returns the type of values (enumeration, range, or array) that are allowed for the
     * object reference. The default implementation returns {@link #VALUE_NONE}.
     */
    @Override
    public int getObjectValueType(final String elementName) {
        return VALUE_NONE;
    }

    /**
     * Returns the type of the object reference stored within the element.
     * The default implementation throws {@link IllegalArgumentException}.
     */
    @Override
    public Class<?> getObjectClass(final String elementName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the default value for the object reference within the named element.
     * The default implementation returns {@code null}.
     */
    @Override
    public Object getObjectDefaultValue(final String elementName) {
        return null;
    }

    /**
     * Returns the legal enumeration for the object reference within the named element.
     * The default implementation throws {@link IllegalArgumentException}.
     */
    @Override
    public Object[] getObjectEnumerations(final String elementName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the minimal for the object reference within the named element.
     * The default implementation throws {@link IllegalArgumentException}.
     */
    @Override
    public Comparable<?> getObjectMinValue(String elementName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the minimal for the object reference within the named element.
     * The default implementation throws {@link IllegalArgumentException}.
     */
    @Override
    public Comparable<?> getObjectMaxValue(final String elementName) {
        throw new IllegalArgumentException();
    }

    /**
     * Returns the minimum number of items in the object reference.
     * The default implementation returns {@code 0} (no minimum).
     */
    @Override
    public int getObjectArrayMinLength(final String elementName) {
        return 0;
    }

    /**
     * Returns the maximum number of items in the object reference.
     * The default implementation returns {@code MAX_VALUE} (no maximum).
     */
    @Override
    public int getObjectArrayMaxLength(final String elementName) {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns a string representation of this format.
     * The default implementation formats this object as a tree.
     */
    @Override
    public String toString() {
        return Trees.toString(new MetadataTreeTable(this).getRootNode());
    }
}
