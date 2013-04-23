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
package org.geotoolkit.image.io;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.logging.LogRecord;

import org.opengis.util.CodeList;
import org.opengis.referencing.cs.AxisDirection;
import org.apache.sis.util.ArraysExt;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.image.io.Warnings;

import static org.geotoolkit.image.io.DimensionSlice.API;


/**
 * Identifies a domain dimension by its index, its name or its axis direction.
 * This class is relevant mostly for <var>n</var>-dimensional datasets where <var>n</var>&gt;2.
 * The dimension can be identified in any of the following ways:
 *
 * <ul>
 *   <li><p>As a zero-based index using an {@link Integer}. This is the most straightforward approach
 *     when the set of dimensions is known.</p>
 *
 *     <blockquote><font size="-1"><b>Example:</b> If the dimensions are known to be (<var>x</var>,
 *     <var>y</var>, <var>z</var>, <var>t</var>), then the <var>t</var> dimension can be identified
 *     by the index 3.</font></blockquote></li>
 *
 *   <li><p>As a dimension name using a {@link String}. This is a better approach than indexes when
 *     dimensions have known names, because it is insensitive to dimension order and whatever other
 *     dimensions exist or not.</p>
 *
 *     <blockquote><font size="-1"><b>Example:</b> If the list of dimensions can be either
 *     ({@code "longitude"}, {@code "latitude"}, {@code "depth"}, {@code "time"}) or
 *     ({@code "latitude"}, {@code "longitude"}, {@code "time"}), then the index of the time
 *     dimension can be either 3 or 2. It is better to identify the time dimension by its
 *     name: {@code "time"}.</font></blockquote></li>
 *
 *   <li><p>As a direction using an {@link AxisDirection}. This provides similar benefit to using
 *     a named dimension, but can work without knowledge of the actual name. It can also be used
 *     with file formats that don't support named dimensions.</p></li>
 * </ul>
 *
 * More than one identifier can be used in order to increase the chance of finding the dimension.
 * For example in order to fetch the <var>z</var> dimension, it may be necessary to specify two
 * names: {@code "height"} and {@code "depth"}. In case of ambiguity, a {@linkplain #warningOccurred
 * warning will be emitted} to any registered listeners at image reading or writing time.
 * <p>
 * See the {@link DimensionSlice} javadoc for usage examples.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see MultidimensionalImageStore
 * @see IllegalImageDimensionException
 *
 * @since 3.15
 * @module
 */
public class DimensionIdentification implements WarningProducer {
    /**
     * The collection that created this object.
     */
    final DimensionSet owner;

    /**
     * Creates a new {@code DimensionIdentification} instance for the given API. This
     * constructor is protected for subclasses usage only. The public API for fetching
     * new instances is the {@link DimensionSet#getOrCreate(DimensionSlice.API)} method.
     *
     * @param  owner The collection that created this object.
     * @param  api The API to assign to this dimension.
     * @throws IllegalArgumentException If an other dimension is already assigned to the given API.
     */
    protected DimensionIdentification(final DimensionSet owner, final API api)
            throws IllegalArgumentException
    {
        if (owner == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_1, "owner"));
        }
        this.owner = owner;
        if (api != API.NONE) {
            final int ordinal = api.ordinal();
            final DimensionIdentification[] apiMapping = owner.apiMapping();
            if (apiMapping[ordinal] != null) {
                throw new IllegalArgumentException(getErrorResources()
                        .getString(Errors.Keys.VALUE_ALREADY_DEFINED_1, api));
            }
            apiMapping[ordinal] = this;
        }
    }

    /**
     * Creates a new instance which is not assigned to any API.
     * This constructor is for {@link DimensionSlice} creation.
     *
     * @param  owner The collection that created this object.
     */
    DimensionIdentification(final DimensionSet owner) {
        this.owner = owner;
    }

    /**
     * Creates a new instance initialized to the same values than the given instance.
     * This method is not public on intend because it is unsafe ({@code original.owner}
     * could be incompatible).
     *
     * @param original The instance to copy.
     */
    DimensionIdentification(final DimensionIdentification original) {
        this.owner = original.owner;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    private IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getLocale());
    }

    /**
     * Adds one or many identifiers for the dimension represented by this object.
     *
     * @param  argName The argument name, used for producing an error message if needed.
     * @param  identifiers The identifiers to add.
     * @throws IllegalArgumentException If an identifier is already
     *         assigned to an other {@code DimensionSlice} instance.
     */
    private void addDimensionId(final String argName, final Object[] identifiers)
            throws IllegalArgumentException
    {
        for (int i=0; i<identifiers.length; i++) {
            final Object identifier = identifiers[i];
            if (identifier == null) {
                throw new NullArgumentException(getErrorResources().getString(
                        Errors.Keys.NULL_ARGUMENT_1, argName + '[' + i + ']'));
            }
            owner.addDimensionId(this, identifier);
        }
    }

    /**
     * Declares the index for the dimension represented by this object. For example in a 4-D
     * dataset having (<var>x</var>, <var>y</var>, <var>z</var>, <var>t</var>) dimensions where
     * the time dimension is known to be the dimension #3 (0-based numbering), users may want to
     * read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>. This can be
     * done by invoking:
     *
     * {@preformat java
     *     addDimensionId(3);
     *     setSliceIndex(25);
     * }
     *
     * {@note The <code>setSliceIndex(int)</code> method is available only if this object is
     * actually an instance of <code>DimensionSlice</code>.}
     *
     * @param  index The index of the dimension. Must be non-negative.
     * @throws IllegalArgumentException If the given dimension index is negative
     *         or already assigned to an other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final int index) throws IllegalArgumentException {
        if (index < 0) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "index", index));
        }
        owner.addDimensionId(this, index);
    }

    /**
     * Adds an identifier for the dimension represented by this object. The dimension is identified
     * by name, which works only with file formats that provide support for named dimensions (e.g.
     * NetCDF). For example in a dataset having either (<var>x</var>, <var>y</var>, <var>z</var>,
     * <var>t</var>) or (<var>x</var>, <var>y</var>, <var>t</var>) dimensions, users may want to
     * read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub> without knowing
     * if the time dimension is the third or the fourth one. This can be done by invoking:
     *
     * {@preformat java
     *     addDimensionId("time");
     *     setSliceIndex(25);
     * }
     *
     * {@note The <code>setSliceIndex(int)</code> method is available only if this object is
     * actually an instance of <code>DimensionSlice</code>.}
     *
     * More than one name can be specified if they should be considered as possible identifiers
     * for the same dimension. For example in order to set the index for the <var>z</var> dimension,
     * it may be necessary to specify both the {@code "height"} and {@code "depth"} names.
     *
     * @param  names The names of the dimension.
     * @throws IllegalArgumentException If a name is already assigned to an
     *         other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final String... names) throws IllegalArgumentException {
        addDimensionId("names", names);
    }

    /**
     * Adds an identifier for the dimension represented by this object. The dimension is identified
     * by axis direction. For example in a dataset having either (<var>x</var>, <var>y</var>,
     * <var>z</var>, <var>t</var>) or (<var>x</var>, <var>y</var>, <var>t</var>) dimensions, users
     * may want to read the (<var>x</var>, <var>y</var>) plane at time <var>t</var><sub>25</sub>
     * without knowing if the time dimension is the third or the fourth one. This can be done by
     * invoking:
     *
     * {@preformat java
     *     addDimensionId(AxisDirection.FUTURE);
     *     setSliceIndex(25);
     * }
     *
     * {@note The <code>setSliceIndex(int)</code> method is available only if this object is
     * actually an instance of <code>DimensionSlice</code>.}
     *
     * More than one direction can be specified if they should be considered as possible identifiers
     * for the same dimension. For example in order to set the index for the <var>z</var> dimension,
     * it may be necessary to specify both the {@link AxisDirection#UP UP} and
     * {@link AxisDirection#DOWN DOWN} directions.
     *
     * @param  axes The axis directions of the dimension.
     * @throws IllegalArgumentException If a name is already assigned to an
     *         other {@code DimensionSlice} instance.
     */
    public void addDimensionId(final AxisDirection... axes) throws IllegalArgumentException {
        addDimensionId("axes", axes);
    }

    /**
     * Removes identifiers for the dimension represented by this object. The {@code identifiers}
     * argument can contain the identifiers given to any {@code addDimensionId(...)} method.
     * Unknown identifiers are silently ignored.
     *
     * @param identifiers The identifiers to remove.
     */
    public void removeDimensionId(final Object... identifiers) {
        owner.removeDimensionId(this, identifiers);
    }

    /**
     * Returns all identifiers for this dimension. The array returned by this method contains
     * the arguments given to any {@code addDimensionId(...)} method call on this instance.
     *
     * @return All identifiers for this dimension.
     */
    public Object[] getDimensionIds() {
        final Map<Object,DimensionIdentification> identifiersMap = owner.identifiersMap();
        final Object[] identifiers = new Object[identifiersMap.size()];
        int count = 0;
        for (final Map.Entry<Object,DimensionIdentification> entry : identifiersMap.entrySet()) {
            if (equals(entry.getValue())) {
                identifiers[count++] = entry.getKey();
            }
        }
        return ArraysExt.resize(identifiers, count);
    }

    /**
     * Returns {@code true} if this dimension contains at least one identifier. Invoking
     * this method is equivalent to the code below, but is potentially more efficient:
     *
     * {@preformat java
     *     boolean hasDimensionIds = (getDimensionIds().length != 0);
     * }
     *
     * @return {@code true} if this dimension contains at least one identifier.
     */
    public boolean hasDimensionIds() {
        return owner.identifiersMap().values().contains(this);
    }

    /**
     * Returns the index of this dimension in a data file. This method is invoked by some
     * {@link SpatialImageReader} or {@link SpatialImageWriter} subclasses when a dataset
     * is about to be read from a file, or written to a file. The caller needs to know the
     * set of dimensions that exist in the file dataset, which may not be identical to the set
     * of dimensions declared in {@link SpatialImageReadParam} or {@link SpatialImageWriteParam}.
     * <p>
     * The default implementation makes the following choice:
     *
     * <ol>
     *   <li><p>If {@link #addDimensionId(int)} has been invoked, then the value specified to
     *       that method is returned regardless the {@code properties} argument value.</p></li>
     *   <li><p>Otherwise if an other {@link #addDimensionId(String[]) addDimensionId(...)} method
     *       has been invoked and the {@code properties} argument is non-null, then this method
     *       iterates over the given properties. The iteration must return exactly one element
     *       for each dimension, in order. If an element is equals to a value specified to a
     *       {@code addDimensionId(...)} method, then the position of that element in the
     *       {@code properties} iteration is returned.</p></li>
     *   <li><p>Otherwise this method returns -1.</p></li>
     * </ol>
     *
     * If more than one dimension match, then a {@linkplain SpatialImageReader#warningOccurred
     * warning is emitted} and this method returns the index of the first property matching an
     * identifier.
     *
     * @param  properties Contains one property (the dimension name as a {@link String} or the axis
     *         direction as an {@link AxisDirection}) for each dimension in the dataset being read
     *         or written. The iteration order shall be the order of dimensions in the dataset. This
     *         argument can be {@code null} if no such properties are available.
     * @return The index of the dimension, or -1 if none.
     */
    public int findDimensionIndex(final Iterable<?> properties) {
        /*
         * Get all identifiers for the slice. If an explicit dimension
         * index is found in the process, it will be returned immediately.
         */
        Set<Object> identifiers = null;
        for (final Map.Entry<Object,DimensionIdentification> entry : owner.identifiersMap().entrySet()) {
            if (equals(entry.getValue())) {
                final Object key = entry.getKey();
                if (key instanceof Integer) {
                    return (Integer) key;
                }
                if (identifiers == null) {
                    identifiers = new HashSet<Object>(8);
                }
                identifiers.add(key);
            }
        }
        /*
         * No explicit dimension found. Now searches for an element from the
         * given iterator which would be one of the declared identifiers.
         */
        if (properties != null && identifiers != null) {
            Map<Integer,Object> found = null;
            int position = 0;
            for (Object property : properties) {
                /*
                 * Undocumented (for now) feature: if we have Map.Entry<?,Integer>,
                 * the value will be the dimension. This allow us to pass more than
                 * one property per dimension.
                 */
                if (property instanceof Map.Entry<?,?>) {
                    final Map.Entry<?,?> entry = (Map.Entry<?,?>) property;
                    property = entry.getKey();
                    position = (Integer) entry.getValue();
                }
                if (identifiers.contains(property)) {
                    if (found == null) {
                        found = new LinkedHashMap<Integer,Object>(4);
                    }
                    final Object old = found.put(position, property);
                    if (old != null) {
                        found.put(position, old); // Keep the first value.
                    }
                }
                position++;
            }
            final Integer dimension = DimensionSet.first(found, this, DimensionIdentification.class, "findDimensionIndex");
            if (dimension != null) {
                return dimension;
            }
        }
        return -1;
    }

    /**
     * Returns the locale used for formatting error messages, or {@code null} if none.
     */
    @Override
    public Locale getLocale() {
        return owner.getLocale();
    }

    /**
     * Invoked when a warning occurred. The default implementation
     * {@linkplain SpatialImageReader#warningOccurred forwards the warning to the image reader} or
     * {@linkplain SpatialImageWriter#warningOccurred writer} if possible, or logs the warning
     * otherwise.
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        return Warnings.log(this, record);
    }

    /**
     * Returns a string representation of this object. The default implementation
     * formats on a single line the class name and the list of dimension identifiers.
     *
     * @see SpatialImageReadParam#toString()
     */
    @Override
    public String toString() {
        return toStringBuilder().append("}]").toString();
    }

    /**
     * Partial implementation of the {@link #toString()} method, to be leveraged by
     * the {@link DimensionSlice} subclass.
     */
    final StringBuilder toStringBuilder() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append("[id={");
        boolean addSeparator = false;
        for (final Map.Entry<Object,DimensionIdentification> entry : owner.identifiersMap().entrySet()) {
            if (entry.getValue() == this) {
                Object key = entry.getKey();
                final boolean addQuotes = (key instanceof CharSequence);
                if (key instanceof CodeList<?>) {
                    key = ((CodeList<?>) key).name();
                }
                if (addSeparator) {
                    buffer.append(", ");
                }
                if (addQuotes) {
                    buffer.append('"');
                }
                buffer.append(key);
                if (addQuotes) {
                    buffer.append('"');
                }
                addSeparator = true;
            }
        }
        return buffer;
    }
}
