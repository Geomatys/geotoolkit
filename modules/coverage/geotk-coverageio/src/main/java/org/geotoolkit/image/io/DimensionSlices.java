/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.util.Iterator;
import java.util.Collections;
import java.util.AbstractSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import javax.imageio.IIOParam;

import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.internal.image.io.Warnings;

import static org.geotoolkit.image.io.DimensionSlice.API;


/**
 * A set of {@link DimensionSlice} instances, which contains also the implementation
 * of public {@link SpatialImageReadParam} and {@link SpatialImageWriteParam} API.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
final class DimensionSlices extends AbstractSet<DimensionSlice> {
    /**
     * The parameters that created this object.
     */
    final IIOParam parameters;

    /**
     * For <var>n</var>-dimensional images where <var>n</var>&gt;2, the data to
     * select in arbitrary dimensions. Will be created only when first needed.
     */
    Map<Object,DimensionSlice> identifiersMap;

    /**
     * For <var>n</var>-dimensional images, the standard Java API to use for setting the index.
     * Will be created only when first needed. The length of this array shall be equals to the
     * length of the {@link DimensionSlice.API#VALIDS} array.
     */
    DimensionSlice[] apiMapping;

    /**
     * The dimension slices, or {@code null} if not yet computed.
     */
    private Set<DimensionSlice> slices;

    /**
     * Creates a new {@code DimensionSlices} instance.
     *
     * @param parameters The parameters that created this object.
     */
    DimensionSlices(final IIOParam parameters) {
        this.parameters = parameters;
    }

    /**
     * Must be invoked every time the content of {@link #identifiersMap changed.
     * This method clears the internal set of slices in order to force a rebuild.
     */
    void refresh() {
        slices = null;
    }

    /**
     * Returns the dimension slices.
     */
    private Set<DimensionSlice> slices() {
        if (slices == null) {
            if (identifiersMap != null) {
                slices = new LinkedHashSet<DimensionSlice>(identifiersMap.values());
            } else {
                slices = Collections.emptySet();
            }
        }
        return slices;
    }

    /**
     * Returns {@code false} if there is at least one dimension slice.
     * This method avoid the creation of the backing set, so it should
     * be cheaper than testing {@code size() == 0}.
     */
    @Override
    public boolean isEmpty() {
        return (identifiersMap == null) || identifiersMap.isEmpty();
    }

    /**
     * Returns the number of dimension slices.
     */
    @Override
    public int size() {
        return slices().size();
    }

    /**
     * Returns an iterator over the dimension slices.
     */
    @Override
    public Iterator<DimensionSlice> iterator() {
        return slices().iterator();
    }

    /**
     * Tests if this set contains the given object.
     */
    @Override
    public boolean contains(final Object value) {
        return slices().contains(value);
    }

    /**
     * Returns the locale used for formatting error messages, or {@code null} if none.
     */
    public Locale getLocale() {
        return (parameters instanceof Localized) ? ((Localized) parameters).getLocale() : null;
    }

    /**
     * Adds an identifier for the dimension represented by the given {@code DimensionSlice}.
     *
     * @param  identifier The identifier to add.
     * @throws IllegalArgumentException If the given identifier is already assigned
     *         to an other {@code DimensionSlice} instance.
     */
    final void addDimensionId(final DimensionSlice dimension, final Object identifier)
            throws IllegalArgumentException
    {
        if (identifiersMap == null) {
            identifiersMap = new LinkedHashMap<Object,DimensionSlice>();
        }
        final DimensionSlice old = identifiersMap.put(identifier, dimension);
        if (old != null && !equals(old)) {
            identifiersMap.put(identifier, old); // Restore the previous value.
            throw new IllegalArgumentException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.VALUE_ALREADY_DEFINED_$1, identifier));
        }
        refresh();
    }

    /**
     * Implementation of {@link SpatialImageReadParam#getDimensionSliceForAPI(API)},
     * also shared with {@link SpatialImageWriteParam}.
     *
     * @param  caller The instance which is invoking this method.
     * @param  api The API for which to test if a dimension slice has been assigned.
     * @return The dimension slice assigned to the given API, or {@code null} if none.
     */
    DimensionSlice getDimensionSliceForAPI(final Localized caller, final API api) {
        if (api == null) {
            throw new NullArgumentException(Warnings.message(caller, Errors.Keys.NULL_ARGUMENT_$1, "api"));
        }
        if (!api.equals(API.NONE) && apiMapping != null) {
            return apiMapping[api.ordinal()];
        }
        return null;
    }

    /**
     * Implementation of {@link SpatialImageReadParam#getSliceIndex(Object[])},
     * also shared with {@link SpatialImageWriteParam}.
     *
     * @param  caller      The instance which is invoking this method.
     * @param  callerClass The class which is invoking this method, used for logging purpose.
     * @param  dimensionIds {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the slice is desired.
     * @return The index set in the first slice found for the given dimension identifiers,
     *         or 0 if none.
     */
    int getSliceIndex(final WarningProducer caller,
            final Class<? extends WarningProducer> callerClass, final Object... dimensionIds)
    {
        if (identifiersMap != null) {
            Map<Integer,Object> found = null;
            for (final Object id : dimensionIds) {
                final DimensionSlice source = identifiersMap.get(id);
                if (source != null) {
                    final Integer index = source.getSliceIndex();
                    if (found == null) {
                        found = new LinkedHashMap<Integer,Object>(4);
                    }
                    final Object old = found.put(index, id);
                    if (old != null) {
                        found.put(index, old); // Keep the old value.
                    }
                }
            }
            final Integer index = first(found, caller, callerClass, "getSliceIndex");
            if (index != null) {
                return index;
            }
        }
        return 0;
    }

    /**
     * Implementation of {@link SpatialImageReadParam#getDimensionSlice(Object[])},
     * also shared with {@link SpatialImageWriteParam}.
     *
     * @param  caller      The instance which is invoking this method.
     * @param  callerClass The class which is invoking this method, used for logging purpose.
     * @param  dimensionIds {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the slice is desired.
     * @return The first slice found for the given dimension identifiers, or {@code null} if none.
     */
    DimensionSlice getDimensionSlice(final WarningProducer caller,
            final Class<? extends WarningProducer> callerClass, final Object... dimensionIds)
    {
        if (identifiersMap != null) {
            Map<DimensionSlice,Object> found = null;
            for (final Object id : dimensionIds) {
                final DimensionSlice slice = identifiersMap.get(id);
                if (slice != null) {
                    if (found == null) {
                        found = new LinkedHashMap<DimensionSlice,Object>(4);
                    }
                    final Object old = found.put(slice, id);
                    if (old != null) {
                        found.put(slice, old); // Keep the old value.
                    }
                }
            }
            final DimensionSlice slice = first(found, caller, callerClass, "getDimensionSlice");
            if (slice != null) {
                return slice;
            }
        }
        return null;
    }

    /**
     * Returns the first key in the given map. If the map has more than one entry,
     * a warning is emitted. If the map is empty, {@code null} is returned. This
     * method is used for determining the {@link DimensionSlice} instance to use
     * after we have iterated over the properties of all axes in a coordinate system.
     *
     * @param  <T>         Either {@link Integer} or {@link API}.
     * @param  found       The map from which to extract the first key.
     * @param  caller      The instance which is invoking this method.
     * @param  callerClass The class which is invoking this method, used for logging purpose.
     * @param  methodName  The method which is invoking this method, used for logging purpose.
     * @return The first key in the given map, or {@code null} if none.
     */
    static <T> T first(final Map<T,?> found, final WarningProducer caller,
            final Class<? extends WarningProducer> callerClass, final String methodName)
    {
        if (found != null) {
            final int size = found.size();
            if (size != 0) {
                /*
                 * At least one (source, property) pair has been found.  We will return the
                 * index. However if we found more than one pair, we have an ambiguity. In
                 * the later case, we will log a warning before to return the first index.
                 */
                if (size > 1) {
                    final StringBuilder buffer = new StringBuilder();
                    for (final Object value : found.values()) {
                        if (buffer.length() != 0) {
                            buffer.append(" | ");
                        }
                        buffer.append(value);
                    }
                    String message = Warnings.message(caller, Errors.Keys.AMBIGIOUS_VALUE_$1, buffer);
                    buffer.setLength(0);
                    buffer.append(message);
                    for (final T source : found.keySet()) {
                        if (buffer.length() != 0) {
                            buffer.append(',');
                        }
                        buffer.append(' ').append(source);
                    }
                    message = buffer.toString();
                    Warnings.log(caller, null, callerClass, methodName, message);
                }
                return found.keySet().iterator().next();
            }
        }
        return null;
    }
}
