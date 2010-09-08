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
import java.util.logging.LogRecord;
import javax.imageio.IIOParam;

import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.internal.image.io.Warnings;

import static org.geotoolkit.image.io.DimensionSlice.API;


/**
 * The set of {@link DimensionIdentification} instances managed by a given
 * {@link MultidimensionalImageStore} instance. This class is provided for
 * {@code MultidimensionalImageStore} implementations and usually don't need
 * to be accessed directly by users.
 * <p>
 * {@code DimensionSet} is not modifiable through the {@link Set} interface, since it doesn't
 * support the {@link Map#add add} method. However new elements can be created by calls to the
 * {@link #getOrCreate(DimensionSlice.API)} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15 (derived from 3.08)
 * @module
 */
public class DimensionSet extends AbstractSet<DimensionIdentification> implements WarningProducer {
    /**
     * The {@link MultidimensionalImageStore} or {@link IIOParam} instance that created this
     * set, or {@code null} if none. This is used for implementation of {@link #getLocale()}.
     * The {@link DimensionSlice} class also uses this reference for fetching (indirectly)
     * information from the image store.
     */
    final Object owner;

    /**
     * The identifiers assigned to {@link DimensionIdentification} instances. Keys are
     * identifiers as {@link Integer} (dimension index), {@link String} (dimension names)
     * or {@link AxisDirection} (dimension directions). Many keys can be defined for the
     * same dimension value.
     * <p>
     * This map is created only when first needed.
     */
    private Map<Object,DimensionIdentification> identifiersMap;

    /**
     * For <var>n</var>-dimensional images, the standard Java API to use for setting the index.
     * Will be created only when first needed. The length of this array shall be equals to the
     * length of the {@link DimensionSlice.API#VALIDS} array.
     */
    DimensionIdentification[] apiMapping;

    /**
     * The dimensions set, or {@code null} if not yet computed.
     */
    private transient Set<DimensionIdentification> dimensions;

    /**
     * Creates a new {@code DimensionSet} instance for the given image reader or writer.
     *
     * @param store The image reader or writer for which this instance is created, or {@code null}.
     */
    public DimensionSet(final MultidimensionalImageStore store) {
        owner = store;
    }

    /**
     * Creates a new {@code DimensionSet} instance for the given parameters. This constructor
     * is not public because {@link SpatialImageReadParam} and {@link SpatialImageWriteParam}
     * leverage this class in an opportunist but undocumented way.
     *
     * @param owner The parameters that created this object.
     */
    DimensionSet(final IIOParam parameters) {
        owner = parameters;
    }

    /**
     * Returns the identifiers assigned to {@link DimensionIdentification} instances.
     * This map should be considered read-only; callers are not allowed to change anything.
     * <p>
     * Keys are identifiers as {@link Integer} (dimension index), {@link String}
     * (dimension names) or {@link AxisDirection} (dimension directions). Many
     * keys can be defined for the same dimension.
     *
     * @return The identifiers mapping, or en empty map if none.
     */
    final Map<Object,DimensionIdentification> identifiersMap() {
        return (identifiersMap != null) ? identifiersMap : Collections.<Object,DimensionIdentification>emptyMap();
    }

    /**
     * Returns {@code true} if this set doesn't contains any dimension.
     */
    @Override
    public boolean isEmpty() {
        return (identifiersMap == null) || identifiersMap.isEmpty();
    }

    /**
     * Returns the number of dimensions contained in this set.
     */
    @Override
    public int size() {
        return dimensions().size();
    }

    /**
     * Returns an iterator over all dimensions contained in this set.
     */
    @Override
    public Iterator<DimensionIdentification> iterator() {
        return dimensions().iterator();
    }

    /**
     * Returns {@code true} if this set contains the given dimension.
     *
     * @param value The dimension to test for inclusion.
     * @return {@code true} if this set contains the given dimension.
     */
    @Override
    public boolean contains(final Object value) {
        return dimensions().contains(value);
    }

    /**
     * Returns all dimensions contained in this set. The returned set is unmodifiable
     * in order to ensure that {@link #iterator()} does not support element removal.
     * A new set will need to be created if the {@link #identifiersMap} change.
     */
    private Set<DimensionIdentification> dimensions() {
        if (dimensions == null) {
            if (identifiersMap != null) {
                dimensions = Collections.unmodifiableSet(
                        new LinkedHashSet<DimensionIdentification>(identifiersMap.values()));
            } else {
                dimensions = Collections.emptySet();
            }
        }
        return dimensions;
    }

    /**
     * Returns the dimension which has been assigned to the given API, or {@code null} if none.
     * <p>
     * This method is typically invoked by {@link SpatialImageReader} implementations together
     * with {@link DimensionIdentification#findDimensionIndex(Iterable)} in order to locate the
     * index of the dimension to read as bands. See <cite>Assigning a third dimension to bands</cite>
     * in the {@link MultidimensionalImageStore} class javadoc.
     *
     * @param  api The API for which to test if a dimension slice has been assigned.
     * @return The dimension slice assigned to the given API, or {@code null} if none.
     */
    public DimensionIdentification get(final API api) {
        if (api == null) {
            throw new NullArgumentException(Warnings.message(this, Errors.Keys.NULL_ARGUMENT_$1, "api"));
        }
        if (!api.equals(API.NONE) && apiMapping != null) {
            return apiMapping[api.ordinal()];
        }
        return null;
    }

    /**
     * Returns the dimension assigned to the given API. If a dimension has been previously created
     * for the given API, it is returned. Otherwise a new dimension is created and returned.
     * <p>
     * In order to check if a dimension exists without creating a new one, use the
     * {@link #get(DimensionSlice.API)} method instead.
     *
     * @param  api The API for which to return a dimension.
     * @return The dimension assigned to the given API.
     */
    public DimensionIdentification getOrCreate(final API api) {
        DimensionIdentification dimension = get(api);
        if (dimension == null) {
            dimension = new DimensionIdentification(this, api);
        }
        return dimension;
    }

    /**
     * Returns the API assigned to the given dimension identifiers. If more than one dimension
     * is found for the given identifiers, then a {@linkplain SpatialImageReader#warningOccurred
     * warning is emitted} and this method returns the first dimension matching the given
     * identifiers. If no dimension is found, {@code null} is returned.
     *
     * @param  identifiers The identifiers of the dimension to query.
     * @return The API assigned to the given dimension, or {@link API#NONE} if none.
     */
    public API getAPI(Object... identifiers) {
        if (apiMapping != null) {
            final DimensionIdentification dimension = find(DimensionSet.class, "getAPI", identifiers);
            if (dimension != null) {
                for (int i=apiMapping.length; --i>=0;) {
                    if (apiMapping[i] == dimension) {
                        return API.VALIDS[i];
                    }
                }
            }
        }
        return API.NONE;
    }

    /**
     * Returns the locale used for formatting error messages, or {@code null} if none.
     * The default implementation delegates to the store given at construction time,
     * it it implements the {@link Localized} interface.
     */
    @Override
    public Locale getLocale() {
        return (owner instanceof Localized) ? ((Localized) owner).getLocale() : null;
    }

    /**
     * Invoked when a warning occurred. The default implementation forwards the warning to
     * the store given at construction time if possible, or logs the warning otherwise.
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        return Warnings.log(owner, record);
    }

    /**
     * Adds an identifier for the dimension represented by the given {@code DimensionIdentification}.
     *
     * @param  dimension The dimension for which to add identifiers.
     * @param  identifier The identifier to add.
     * @throws IllegalArgumentException If the given identifier is already assigned
     *         to an other {@code DimensionIdentification} instance.
     */
    final void addDimensionId(final DimensionIdentification dimension, final Object identifier)
            throws IllegalArgumentException
    {
        if (identifiersMap == null) {
            identifiersMap = new LinkedHashMap<Object,DimensionIdentification>();
        }
        final DimensionIdentification old = identifiersMap.put(identifier, dimension);
        if (old != null && !old.equals(dimension)) {
            identifiersMap.put(identifier, old); // Restore the previous value.
            throw new IllegalArgumentException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.VALUE_ALREADY_DEFINED_$1, identifier));
        }
        dimensions = null; // Will need to be recomputed.
    }

    /**
     * Removes identifiers for the given dimension. The {@code identifiers} argument can contain
     * the identifiers given to any {@code addDimensionId(...)} method. Unknown identifiers are
     * silently ignored.
     *
     * @param dimension The dimension from which to remove identifiers.
     * @param identifiers The identifiers to remove.
     */
    final void removeDimensionId(final DimensionIdentification dimension, final Object[] identifiers) {
        if (identifiersMap != null) {
            for (final Object identifier : identifiers) {
                final DimensionIdentification old = identifiersMap.remove(identifier);
                if (old != null && !old.equals(dimension)) {
                    identifiersMap.put(identifier, old); // Restore the previous state.
                }
            }
            dimensions = null; // Will need to be recomputed.
        }
    }

    /**
     * Returns the index of the slice in the dimension identified by at least one of the given
     * identifiers. This method is equivalent to the code below, except that a warning is emitted
     * only if index values are ambiguous:
     *
     * {@preformat java
     *     DimensionSlice slice = getDimensionSlice(callerClass, dimensionIds);
     *     return (slice != null) ? slice.getSliceIndex() : 0;
     * }
     *
     * This method is used for {@link SpatialImageReadParam} and {@link SpatialImageWriteParam}
     * implementations only. In this case, all {@link #identifiersMap} values shall be instances
     * of {@link DimensionSlice}.
     *
     * @param  callerClass The class which is invoking this method, used for logging purpose.
     * @param  dimensionIds {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the slice is desired.
     * @return The index set in the first slice found for the given dimension identifiers,
     *         or 0 if none.
     */
    final int getSliceIndex(final Class<? extends WarningProducer> callerClass, final Object[] dimensionIds) {
        if (identifiersMap != null) {
            Map<Integer,Object> found = null;
            for (final Object id : dimensionIds) {
                final DimensionSlice source = (DimensionSlice) identifiersMap.get(id);
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
            final Integer index = first(found, this, callerClass, "getSliceIndex");
            if (index != null) {
                return index;
            }
        }
        return 0;
    }

    /**
     * Returns the dimension slice identified by at least one of the given identifiers. The
     * dimension can be identified by a zero-based index as an {@link Integer}, a dimension
     * name as a {@link String}, or an axis direction as an {@link AxisDirection}. More than
     * one identifier can be specified in order to increase the chance to get the index.
     * <p>
     * This method is used for {@link SpatialImageReadParam} and {@link SpatialImageWriteParam}
     * implementations only. In this case, all {@link #identifiersMap} values shall be instances
     * of {@link DimensionSlice}.
     *
     * @param  callerClass The class which is invoking this method, used for logging purpose.
     * @param  dimensionIds {@link Integer}, {@link String} or {@link AxisDirection}
     *         that identify the dimension for which the slice is desired.
     * @return The first slice found for the given dimension identifiers, or {@code null} if none.
     */
    final DimensionIdentification getDimensionSlice(
            final Class<? extends WarningProducer> callerClass, final Object[] dimensionIds)
    {
        return find(callerClass, "getDimensionSlice", dimensionIds);
    }

    /**
     * Returns the dimension identified by at least one of the given identifier.
     *
     * @param caller       The object which is invoking this method (for logging purpose).
     * @param callerClass  The class  which is invoking this method (for logging purpose).
     * @param callerMethod The method which is invoking this method (for logging purpose).
     * @param dimensionIds The integers, strings or axis directions identifying a dimension.
     * @return The first dimension found for the given identifiers, or {@code null} if none.
     */
    private DimensionIdentification find(
            final Class<? extends WarningProducer> callerClass,
            final String callerMethod, final Object[] dimensionIds)
    {
        if (identifiersMap != null) {
            Map<DimensionIdentification,Object> found = null;
            for (final Object id : dimensionIds) {
                final DimensionIdentification slice = identifiersMap.get(id);
                if (slice != null) {
                    if (found == null) {
                        found = new LinkedHashMap<DimensionIdentification,Object>(4);
                    }
                    final Object old = found.put(slice, id);
                    if (old != null) {
                        found.put(slice, old); // Keep the old value.
                    }
                }
            }
            final DimensionIdentification slice = first(found, this, callerClass, callerMethod);
            if (slice != null) {
                return slice;
            }
        }
        return null;
    }

    /**
     * Returns the first key in the given map. If the map has more than one entry,
     * a warning is emitted. If the map is empty, {@code null} is returned. This
     * method is used for determining the {@link DimensionIdentification} instance to use
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
