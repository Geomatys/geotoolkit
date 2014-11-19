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
package org.geotoolkit.image.io.metadata;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.NoSuchElementException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.measure.unit.Unit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.opengis.util.Enumerated;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.jdk8.JDK8;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.Classes;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.UnsupportedImplementationException;

import org.geotoolkit.metadata.Citations;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Convenience class for reading and writing attribute values from/to an {@link IIOMetadata} object.
 * This class is used by {@link SpatialMetadata} and usually don't need to be created explicitly.
 * It is available in public API for users who need more flexibility than what
 * {@code SpatialMetadata} provides.
 * <p>
 * The metadata object is specified at construction time, together with a path to the
 * {@linkplain Element element} of interest. Examples of valid paths:
 *
 * <blockquote><table cellspacing="0" cellpadding="0">
 * <tr>
 *   <td>{@code "RectifiedGridDomain/CRS/Datum"}</td>
 *   <td>&nbsp;&nbsp;(assuming the {@linkplain SpatialMetadataFormat#getImageInstance image} metadata format)</td>
 * </tr><tr>
 *   <td>{@code "RectifiedGridDomain/CRS/CoordinateSystem"}</td>
 *   <td>&nbsp;&nbsp;(assuming the {@linkplain SpatialMetadataFormat#getImageInstance image} metadata format)</td>
 * </tr><tr>
 *   <td>{@code "DiscoveryMetadata/Extent/GeographicElement"}</td>
 *   <td>&nbsp;&nbsp;(assuming the {@linkplain SpatialMetadataFormat#getStreamInstance stream} metadata format)</td>
 * </tr>
 * </table></blockquote>
 *
 * If no node exists for the given path, then the node will be created at {@code MetadataNodeAccessor}
 * construction time. For example the last line in the above list will ensure that the metadata
 * tree contains at least the nodes below, creating the missing ones if needed:
 *
 * {@preformat text
 *    <root>
 *    └───DiscoveryMetadata
 *        └───Extent
 *            └───GeographicElement
 * }
 *
 * <blockquote><font size="-1"><b>Note:</b> the value of {@code <root>} depends on the metadata
 * format, but is typically
 * {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME}</font></blockquote>
 *
 * After a {@code MetadataNodeAccessor} instance has been created, the {@code getAttributeAs<Type>(String)}
 * methods can be invoked for fetching any attribute values, taking care of conversions to
 * {@link String}, {@link Double}, {@link Integer} or {@link Date}. Corresponding setter
 * methods are also provided.
 *
 * {@section Accessing child elements}
 * If order to access a child element when the child policy is
 * {@link IIOMetadataFormat#CHILD_POLICY_ALL    CHILD_POLICY_ALL},
 * {@link IIOMetadataFormat#CHILD_POLICY_SOME   CHILD_POLICY_SOME} or
 * {@link IIOMetadataFormat#CHILD_POLICY_CHOICE CHILD_POLICY_CHOICE},
 * create a new {@code MetadataNodeAccessor} with the complete path to that element.
 * <p>
 * If the child policy of the node is {@link IIOMetadataFormat#CHILD_POLICY_REPEAT CHILD_POLICY_REPEAT},
 * then this class provides convenience methods for accessing the attributes of the childs.
 * The path to unique legal child elements shall be specified to the constructor, as in the
 * examples below:
 * <p>
 * <ul>
 *   <li>{@code new MetadataNodeAccessor(..., "RectifiedGridDomain/CRS/CoordinateSystem", "Axis")}</li>
 *   <li>{@code new MetadataNodeAccessor(..., "ImageDescription/Dimensions", "Dimension")}</li>
 * </ul>
 * <p>
 * The {@code get} and {@code set} methods defined in this class will operate on the
 * <cite>selected</cite> {@linkplain Element element}, which may be either the one
 * specified at construction time, or one of its childs. The element can be selected
 * by {@link #selectParent} (the default) or {@link #selectChild(int)}.
 * <p>
 * Note that this mechanism is not suitable to nested childs, i.e. {@code MetadataNodeAccessor} gives
 * access only to the attributes of child elements. If an access to the childs nested in a child
 * element is wanted, then the users may find more convenient to parse the XML tree by an other
 * way than this convenience class.
 *
 * {@section Example reading attributes}
 * The example below creates an accessor for a node called {@code "CoordinateSystem"}
 * which is expected to have an arbitrary amount of childs called {@code "Axis"}. The
 * name of the first axis is fetched.
 *
 * {@preformat java
 *     IIOMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
 *     MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null,
 *             "RectifiedGridDomain/CRS/CoordinateSystem", "Axis");
 *
 *     accessor.selectParent();
 *     String csName = accessor.getAttribute("name");
 *
 *     accessor.selectChild(0);
 *     String firstAxisName = accessor.getAttribute("name");
 * }
 *
 * {@section Example adding childs and writing attributes}
 * The example below uses the same accessor than above, but this time for adding a new
 * child under the {@code "CoordinateSystem"} node:
 *
 * {@preformat java
 *     accessor.selectChild(accessor.appendChild());
 *     accessor.setAttribute("name", "The name of a new axis");
 * }
 *
 * {@section Getting ISO 19115-2 instances}
 * This class can provide implementations of the ISO 19115-2 interfaces. Each getter method in
 * an interface is implemented as a call to a {@code getAttribute(String)} method, or as the
 * creation of a nested ISO 19115-2 object. See {@link #newProxyInstance(Class)} for more details.
 * <p>
 * While this mechanism is primarily targeted at ISO 19115-2 interfaces, it can be used with
 * other set of interfaces as well.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.20
 *
 * @see SpatialMetadata#getInstanceForType(Class)
 * @see SpatialMetadata#getListForType(Class)
 *
 * @since 3.20 (derived from 2.5)
 * @module
 */
public class MetadataNodeAccessor extends MetadataNodeParser {
    /**
     * Creates an accessor with the same parent and childs than the specified one. The two
     * accessors will share the same {@linkplain Node metadata nodes} (including the list
     * of childs), so change in one accessor will be immediately reflected in the other
     * accessor. However each accessor can {@linkplain #selectChild(int) select their child}
     * independently.
     * <p>
     * The initially {@linkplain #selectChild(int) selected child} and {@linkplain #getWarningLevel()
     * warnings level} are the same than the given accessor.
     * <p>
     * The main purpose of this constructor is to create many views over the same list
     * of childs, where each view can {@linkplain #selectChild(int) select} a different child.
     *
     * @param clone The accessor to clone.
     */
    public MetadataNodeAccessor(final MetadataNodeParser clone) {
        super(clone);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative
     * to the given parent. In the example below, the complete path to the child accessor
     * is {@code "DiscoveryMetadata/Extent/GeographicElement"}:
     *
     * {@preformat java
     *     MetadataNodeAccessor parent = new MetadataNodeAccessor(..., "DiscoveryMetadata/Extent", ...);
     *     MetadataNodeAccessor child  = new MetadataNodeAccessor(parent, "GeographicElement", null);
     * }
     *
     * {@section Auto-detection of children}
     * If the metadata node has no child, then {@code childPath} shall be {@code null}.
     * If the caller does not know whatever the node has childs or not, then the
     * {@code "#auto"} special value can be used. Note that this auto-detection may
     * throw an {@link IllegalArgumentException} if the node is not defined by the
     * {@link IIOMetadataFormat}. It is preferable to specify explicitly the child
     * element name when this name is known.
     *
     * @param parent    The accessor for which the {@code path} is relative.
     * @param path      The path to the {@linkplain Node node} of interest.
     * @param childPath The path to the child {@linkplain Element elements}, or {@code null}
     *                  if none, or {@code "#auto"} for auto-detection.
     *
     * @throws IllegalArgumentException if {@code childPath} is {@code "#auto"} but the childs
     *         can not be inferred from the metadata format.
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @since 3.06
     */
    public MetadataNodeAccessor(MetadataNodeParser parent, String path, String childPath)
            throws IllegalArgumentException, NoSuchElementException
    {
        super(parent, path, childPath);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} accepting a user object of the
     * given type. This method is convenient for fetching an object of some known type without
     * regards to its location in the sub-tree.
     *
     * @param  parent      The accessor from which to start the search for an element accepting
     *                     the given type.
     * @param  objectClass The {@linkplain IIOMetadataFormat#getObjectClass(String) class of user
     *                     object} to locate.
     *
     * @throws IllegalArgumentException If no element accepting the given type was found,
     *         or if more than one element accepting that type was found.
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @see #listPaths(IIOMetadataFormat, Class)
     *
     * @since 3.06
     */
    public MetadataNodeAccessor(MetadataNodeParser parent, Class<?> objectClass)
            throws IllegalArgumentException, NoSuchElementException
    {
        super(parent, objectClass);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative to
     * the {@linkplain IIOMetadataFormat#getRootName() root}. This is a convenience method for the
     * {@linkplain #MetadataNodeAccessor(IIOMetadata, String, String, String) constructor below}
     * with {@code formatName} and {@code childPath} argument set to {@code "#auto"} value.
     *
     * @param  metadata    The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                     sub-class is recommended, but not mandatory.
     * @param  parentPath  The path to the {@linkplain Node node} of interest, or {@code null}
     *                     if the {@code metadata} root node is directly the node of interest.
     *
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @since 3.06
     */
    public MetadataNodeAccessor(IIOMetadata metadata, String parentPath)
            throws NoSuchElementException
    {
        super(metadata, parentPath);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative to
     * the {@linkplain IIOMetadataFormat#getRootName() root}. The paths can contain many elements
     * separated by the {@code '/'} character.
     * See the <a href="#skip-navbar_top">class javadoc</a> for more details.
     *
     * {@section Auto-detection of children and format}
     * The {@code childPath} argument can be {@code "#auto"}, which is processed as documented
     * in the {@linkplain #MetadataNodeAccessor(MetadataNodeParser, String, String) above constructor}.
     * <p>
     * The {@code formatName} can be {@code null} or {@code "#auto"}, in which case a format
     * is selected automatically:
     * <p>
     * <ul>
     *   <li>If {@code metadata} is an instance of {@link SpatialMetadata}, then the
     *       {@linkplain SpatialMetadata#format format} given at {@code SpatialMetadata}
     *       construction time is used.</li>
     *   <li>Otherwise the first format returned by {@link IIOMetadata#getMetadataFormatNames()}
     *       is used. This is usually in preference order: the native format, the standard format
     *       or the first extra format.</li>
     * </ul>
     *
     * @param  metadata    The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                     sub-class is recommended, but not mandatory.
     * @param  formatName  The name of the {@linkplain IIOMetadata#getMetadataFormat(String) format
     *                     to use}, or {@code null} or {@code "#auto"} for an automatic selection.
     * @param  parentPath  The path to the {@linkplain Node node} of interest, or {@code null}
     *                     if the {@code metadata} root node is directly the node of interest.
     * @param  childPath   The path (relative to {@code parentPath}) to the child
     *                     {@linkplain Element elements}, or {@code null} if none,
     *                     or {@code "#auto"} for auto-detection.
     *
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     */
    public MetadataNodeAccessor(IIOMetadata metadata, String formatName, String parentPath,
            String childPath) throws NoSuchElementException
    {
        super(metadata, formatName, parentPath, childPath);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} accepting a user object of the
     * given type. This method is convenient for fetching an object of some known type without
     * regards to its location in the tree. For example if the metadata format stream format
     * documented in {@link SpatialMetadataFormat}, then:
     *
     * {@preformat java
     *     new MetadataNodeAccessor(metadata, formatName, GeographicElement.class);
     * }
     *
     * is equivalent to:
     *
     * {@preformat java
     *     new MetadataNodeAccessor(metadata, formatName, "DiscoveryMetadata/Extent/GeographicElement", "#auto");
     * }
     *
     * @param  metadata    The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                     sub-class is recommended, but not mandatory.
     * @param  formatName  The name of the {@linkplain IIOMetadata#getMetadataFormat(String) format
     *                     to use}, or {@code null} or {@code "#auto"} for an automatic selection.
     * @param  objectClass The {@linkplain IIOMetadataFormat#getObjectClass(String) class of user
     *                     object} to locate.
     *
     * @throws IllegalArgumentException If no element accepting the given type was found,
     *         or if more than one element accepting that type was found.
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @see #listPaths(IIOMetadataFormat, Class)
     *
     * @since 3.06
     */
    public MetadataNodeAccessor(IIOMetadata metadata, String formatName, Class<?> objectClass)
            throws IllegalArgumentException, NoSuchElementException
    {
        super(metadata, formatName, objectClass);
    }

    /**
     * Returns {@code true} if this accessor is read-only. The default implementation returns the
     * read-only state of the wrapped {@linkplain #metadata} object. Subclasses can override this
     * method if they need more control about whatever this accessor is allowed to add child
     * elements in the metadata object.
     * <p>
     * If this method returns {@code true}, then every <code>setFoo(&hellip;)</code> methods in
     * this class will thrown an {@link UnsupportedOperationException} when they are invoked.
     *
     * @return {@code true} if this accessor is read-only, or {@code false} if it allows
     *         write operations.
     *
     * @see IIOMetadata#isReadOnly()
     *
     * @since 3.19
     */
    @Override
    public boolean isReadOnly() {
        return metadata.isReadOnly();
    }

    /**
     * Adds a new child {@linkplain Element element} at the path given at construction time.
     * The {@linkplain #childCount child count} will be increased by 1.
     * <p>
     * The new child is <strong>not</strong> automatically selected. In order to select this
     * new child, the {@link #selectChild(int)} method must be invoked explicitly.
     *
     * @return The index of the new child element.
     * @throws UnsupportedOperationException If this accessor does not allow children.
     *
     * @see #childCount()
     * @see #selectChild(int)
     */
    public int appendChild() throws UnsupportedOperationException {
        if (isReadOnly()) {
            throw new UnsupportedOperationException(getErrorResources()
                    .getString(Errors.Keys.UNMODIFIABLE_METADATA));
        }
        final int size = childs.size();
        final Node child = appendChild(parent, childPath);
        if (child instanceof Element) {
            childs.add((Element) child);
            return size;
        } else {
            throw new UnsupportedImplementationException(child.getClass());
        }
    }

    /**
     * Remove a child
     * @param childIndex
     * @return
     * @throws UnsupportedOperationException
     * @throws IndexOutOfBoundsException
     */
    public Node removeChild(int childIndex) throws UnsupportedOperationException, IndexOutOfBoundsException {
        if (isReadOnly()) {
            throw new UnsupportedOperationException(getErrorResources()
                    .getString(Errors.Keys.UNMODIFIABLE_METADATA));
        }

        Node child = childs.get(childIndex);
        if (child instanceof Element) {
            return removeChild(parent, childPath, child);
        } else {
            throw new UnsupportedImplementationException(child.getClass());
        }
    }

    /**
     * Remove all child node of current element parent.
     * @throws UnsupportedOperationException
     */
    public void removeChildren() throws UnsupportedOperationException {
        if (isReadOnly()) {
            throw new UnsupportedOperationException(getErrorResources()
                    .getString(Errors.Keys.UNMODIFIABLE_METADATA));
        }

        if (childCount() > 0) {
            removeChildren(parent, childPath);
            childs.clear();
        }
    }

    /**
     * Returns {@code true} if values of the specified type can be formatted as a
     * text. We allows formatting only for reasonably cheap objects, for example
     * a Number but not a CoordinateReferenceSystem.
     */
    private static boolean isFormattable(final Class<?> type) {
        return (type != null) && (CharSequence.class.isAssignableFrom(type) ||
               Number.class.isAssignableFrom(Numbers.primitiveToWrapper(type)));
    }

    /**
     * Formats a sequence for {@link #setAttribute} implementations working on list arguments.
     *
     * @param  value The attribute value.
     * @return The formatted sequence.
     */
    private static String formatSequence(final Object values) {
        String text = null;
        if (values != null) {
            final StringBuilder buffer = new StringBuilder(48);
            final int length = Array.getLength(values);
            for (int i=0; i<length; i++) {
                if (i != 0) {
                    buffer.append(' ');
                }
                final Object value = Array.get(values, i);
                if (value != null) {
                    final String s = value.toString().trim();
                    final int sl = s.length();
                    for (int j=0; j<sl; j++) {
                        final char c = s.charAt(j);
                        buffer.append(Character.isWhitespace(c) ? NBSP : c);
                    }
                }
            }
            text = buffer.length() != 0 ? buffer.toString() : null;
        }
        return text;
    }

    /**
     * Sets the {@linkplain IIOMetadataNode#setUserObject user object} associated with the
     * {@linkplain #selectChild selected element}. At the difference of every {@code setAttribute}
     * methods defined in this class, this method does not delegate to
     * {@link #setAttribute(String, String)}.
     * <p>
     * If the specified value is formattable (i.e. is a {@linkplain CharSequence character
     * sequence}, a {@linkplain Number number} or an array of the above), then this method
     * also {@linkplain IIOMetadataNode#setNodeValue sets the node value} as a string. This
     * is mostly a convenience for formatting purpose since {@link IIOMetadataNode} don't
     * use the node value. But it may help some libraries that are not designed to work with
     * user objects, since they are particular to Image I/O metadata.
     *
     * @param  value The user object, or {@code null} if none.
     * @throws UnsupportedImplementationException if the selected element is not an instance of
     *         {@link IIOMetadataNode}.
     *
     * @see #getUserObject()
     */
    public void setUserObject(final Object value) throws UnsupportedImplementationException {
        final Element element = currentElement();
        String asText = null;
        if (value != null) {
            final Class<?> type = value.getClass();
            if (Date.class.isAssignableFrom(type)) {
                asText = JDK8.printDateTime((Date) value);
            } else if (isFormattable(type)) {
                asText = value.toString();
            } else if (isFormattable(type.getComponentType())) {
                asText = formatSequence(value);
            }
        }
        if (element instanceof IIOMetadataNode) {
            ((IIOMetadataNode) element).setUserObject(value);
        } else if (value!=null && asText==null) {
            throw new UnsupportedImplementationException(getErrorResources().getString(
                    Errors.Keys.ILLEGAL_CLASS_2, Classes.getClass(element), IIOMetadataNode.class));
        }
        element.setNodeValue(asText);
    }

    /**
     * Sets the attribute to the specified value, or remove the attribute if the value is null.
     * <p>
     * Every {@code setAttribute} methods in this class invoke this method last. Consequently,
     * this method provides a single overriding point for subclasses that want to process the
     * attribute after formatting.
     *
     * @param attribute The attribute name.
     * @param value The attribute value, or {@code null} for removing the attribute.
     *
     * @see #getAttribute(String)
     * @see IIOMetadataFormat#DATATYPE_STRING
     */
    public void setAttribute(final String attribute, String value) {
        ensureNonNull("attribute", attribute);
        if (isReadOnly()) {
            throw new UnsupportedOperationException(getErrorResources()
                    .getString(Errors.Keys.UNMODIFIABLE_METADATA));
        }
        final Element element = currentElement();
        if (value == null || (value=value.trim()).isEmpty()) {
            if (element.hasAttribute(attribute)) {
                element.removeAttribute(attribute);
            }
        } else {
            element.setAttribute(attribute, value);
        }
    }

    /**
     * Sets the attribute to the specified array of values, or remove the attribute if the array
     * is {@code null}. The given items are formatted in a single string with an ordinary space
     * used as the item separator, as mandated by {@link IIOMetadataFormat#VALUE_LIST}. If some
     * of the given items contain spaces, then those spaces are replaced by a no-break space
     * (<code>'\\u00A0'</code>) for avoiding confusion with the space separator.
     *
     * @param attribute The attribute name.
     * @param values The attribute values, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsStrings(String, boolean)
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final String... values) {
        setAttribute(attribute, formatSequence(values));
    }

    /**
     * Sets the attribute to the specified code value, or remove the attribute if the value is null.
     *
     * @param attribute The attribute name.
     * @param value The attribute value, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsCode(String, Class)
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final Enumerated value) {
        setAttribute(attribute, Types.getCodeName(value));
    }

    /**
     * Sets the attribute to the specified boolean value.
     *
     * @param attribute The attribute name.
     * @param value The attribute value.
     *
     * @see #getAttributeAsBoolean(String)
     * @see IIOMetadataFormat#DATATYPE_BOOLEAN
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final boolean value) {
        setAttribute(attribute, Boolean.toString(value));
    }

    /**
     * Sets the attribute to the specified integer value.
     *
     * @param attribute The attribute name.
     * @param value The attribute value.
     *
     * @see #getAttributeAsInteger(String)
     * @see IIOMetadataFormat#DATATYPE_INTEGER
     */
    public void setAttribute(final String attribute, final int value) {
        setAttribute(attribute, Integer.toString(value));
    }

    /**
     * Set the attribute to the specified array of values,
     * or remove the attribute if the array is {@code null}.
     *
     * @param attribute The attribute name.
     * @param values The attribute values, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsIntegers(String, boolean)
     */
    public void setAttribute(final String attribute, final int... values) {
        setAttribute(attribute, formatSequence(values));
    }

    /**
     * Sets the attribute to the specified floating point value,
     * or remove the attribute if the value is NaN or infinity.
     *
     * @param attribute The attribute name.
     * @param value The attribute value.
     *
     * @see #getAttributeAsFloat(String)
     * @see IIOMetadataFormat#DATATYPE_FLOAT
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final float value) {
        String text = null;
        if (!Float.isNaN(value) && !Float.isInfinite(value)) {
            text = Float.toString(value);
        }
        setAttribute(attribute, text);
    }

    /**
     * Set the attribute to the specified array of values,
     * or remove the attribute if the array is {@code null}.
     *
     * @param attribute The attribute name.
     * @param values The attribute values, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsDoubles(String, boolean)
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final float... values) {
        setAttribute(attribute, formatSequence(values));
    }

    /**
     * Sets the attribute to the specified floating point value,
     * or remove the attribute if the value is NaN or infinity.
     *
     * @param attribute The attribute name.
     * @param value The attribute values.
     *
     * @see #getAttributeAsDouble(String)
     * @see IIOMetadataFormat#DATATYPE_DOUBLE
     */
    public void setAttribute(final String attribute, final double value) {
        String text = null;
        if (!Double.isNaN(value) && !Double.isInfinite(value)) {
            text = Double.toString(value);
        }
        setAttribute(attribute, text);
    }

    /**
     * Set the attribute to the specified array of values,
     * or remove the attribute if the array is {@code null}.
     *
     * @param attribute The attribute name.
     * @param values The attribute values, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsDoubles(String, boolean)
     */
    public void setAttribute(final String attribute, final double... values) {
        setAttribute(attribute, formatSequence(values));
    }

    /**
     * Sets the attribute to the specified value, or remove the attribute if the value is null.
     *
     * @param attribute The attribute name.
     * @param value The attribute value, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsDate(String)
     */
    public void setAttribute(final String attribute, final Date value) {
        String text = null;
        if (value != null) {
            if (metadata instanceof SpatialMetadata) {
                text = ((SpatialMetadata) metadata).dateFormat().format(value);
            } else {
                // Inefficient fallback, but should usually not happen anyway.
                text = SpatialMetadata.format(Date.class, value);
            }
        }
        setAttribute(attribute, text);
    }

    /**
     * Sets the attribute to the specified range value.
     *
     * @param attribute The attribute name.
     * @param value The attribute value, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsRange(String)
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final NumberRange<?> value) {
        String text = null;
        if (value != null) {
            if (metadata instanceof SpatialMetadata) {
                text = ((SpatialMetadata) metadata).rangeFormat().format(value);
            } else {
                // Inefficient fallback, but should usually not happen anyway.
                text = SpatialMetadata.format(NumberRange.class, value);
            }
        }
        setAttribute(attribute, text);
    }

    /**
     * Sets the attribute to the specified unit value.
     *
     * @param attribute The attribute name.
     * @param value The attribute value.
     *
     * @see #getAttributeAsUnit(String, Class)
     *
     * @since 3.07
     */
    public void setAttribute(final String attribute, final Unit<?> value) {
        setAttribute(attribute, (value != null) ? value.toString() : null);
    }

    /**
     * Sets the attribute to the specified citation value,
     * or remove the attribute if the value is null.
     *
     * @param attribute The attribute name.
     * @param value The attribute value, or {@code null} for removing the attribute.
     *
     * @see #getAttributeAsCitation(String)
     *
     * @since 3.06
     */
    public void setAttribute(final String attribute, final Citation value) {
        setAttribute(attribute, Citations.getIdentifier(value));
    }
}
