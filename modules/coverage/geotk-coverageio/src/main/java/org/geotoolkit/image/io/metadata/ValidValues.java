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

import javax.imageio.metadata.IIOMetadataFormat;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.metadata.ValueRestriction;


/**
 * The values that are valid for a given element or attribute. A {@code ValidValue}
 * may describe a range or an enumeration. Instances of this class are used by
 * {@link MetadataTreeNode} only.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
final class ValidValues extends ValueRestriction {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -148744234616697972L;

    /**
     * Placeholder meaning that we have determined that there is no restriction on valid values.
     */
    static final ValidValues UNRESTRICTED = new ValidValues();

    /**
     * The constructor for the {@link #UNRESTRICTED} singleton.
     */
    private ValidValues() {
        super(null, null, null);
    }

    /**
     * Creates a new {@code ValidValues} instance for the given enumeration.
     */
    ValidValues(final Object[] values) {
        super(null, null, XCollections.immutableSet(values));
    }

    /**
     * Creates a new {@code ValidValues} for a range with the given type, minimum and maximum
     * values. The inclusion status of minimum and maximum values are inferred from the given
     * {@code rangeType} argument.
     * <p>
     * Do no use this constructor directly; use one of the {@code range(...)} method instead.
     *
     * @param  <T>       The type of elements in the range.
     * @param  type      The type of elements in the range.
     * @param  rangeType One of {@code IIOMetadataFormat.VALUE_RANGE_*} constants.
     * @param  min       The minimal value.
     * @param  max       The maximal value.
     */
    private <T extends Number & Comparable<? super T>> ValidValues(
            final Class<T> type, final int rangeType, final T min, final T max)
    {
        super(null, new NumberRange<>(type,
                min, (rangeType & IIOMetadataFormat.VALUE_RANGE_MIN_INCLUSIVE_MASK) != 0,
                max, (rangeType & IIOMetadataFormat.VALUE_RANGE_MAX_INCLUSIVE_MASK) != 0), null);
    }

    /**
     * Casts the given minimum and maximum values to the expected type and invoke {@link #create}.
     * This is an helper method for playing with parameterized type.
     */
    private static <T extends Number & Comparable<? super T>> ValidValues cast(final Class<T> type,
            final int rangeType, final Comparable<?> min, final Comparable<?> max)
    {
        return new ValidValues(type, rangeType, type.cast(min), type.cast(max));
    }

    /**
     * Creates a new {@code ValidValue} for a range with the given type, minimum and maximum
     * values. The inclusion status of minimum and maximum values are inferred from the given
     * {@code rangeType} argument.
     *
     * @param  <T>       The type of elements in the range.
     * @param  type      The type of elements in the range.
     * @param  rangeType One of {@code IIOMetadataFormat.VALUE_RANGE_*} constants.
     * @param  min       The minimal value.
     * @param  max       The maximal value.
     * @return The range.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    static ValidValues range(final Class<?> type, final int rangeType,
            final Comparable<?> min, final Comparable<?> max)
    {
        if (Number.class.isAssignableFrom(type) && Comparable.class.isAssignableFrom(type)) {
            return cast((Class) type, rangeType, min, max);
        }
        return UNRESTRICTED;
    }

    /**
     * Creates a new instance for the given attribute range. Note that in the particular case
     * of node attributes, the range values are always represented as {@link String} even if
     * the underlying type is a number.
     *
     * @param  datatype  One of {@code IIOMetadataFormat.DATATYPE_*} constants.
     * @param  rangeType One of {@code IIOMetadataFormat.VALUE_RANGE_*} constants.
     * @param  min       The minimal value.
     * @param  max       The maximal value.
     * @return The range of values, or {@link #UNRESTRICTED} if the given type is not supported.
     * @throws NumberFormatException If a string can not be parsed as a number of the given type.
     */
    static ValidValues range(final int datatype, final int rangeType, final String min, final String max) {
        switch (datatype) {
            case IIOMetadataFormat.DATATYPE_DOUBLE: {
                return new ValidValues(Double.class, rangeType,
                        (min == null) ? null : Double.valueOf(min),
                        (max == null) ? null : Double.valueOf(max));
            }
            case IIOMetadataFormat.DATATYPE_FLOAT: {
                return new ValidValues(Float.class, rangeType,
                        (min == null) ? null : Float.valueOf(min),
                        (max == null) ? null : Float.valueOf(max));
            }
            case IIOMetadataFormat.DATATYPE_INTEGER: {
                return new ValidValues(Integer.class, rangeType,
                        (min == null) ? null : Integer.valueOf(min),
                        (max == null) ? null : Integer.valueOf(max));
            }
            default: {
                return UNRESTRICTED;
            }
        }
    }

    /**
     * Returns a string representation of this {@code ValidValues} to be used in widgets.
     * In the {@link #UNRESTRICTED} case, returns an empty string meaning "no restriction".
     */
    @Override
    public String toString() {
        if (validValues != null) {
            final StringBuilder buffer = new StringBuilder();
            for (final Object code : validValues) {
                if (buffer.length() != 0) {
                    buffer.append(", ");
                }
                buffer.append(CharSequences.camelCaseToWords(code.toString(), true));
            }
            return buffer.toString();
        }
        return (range != null) ? range.toString() : "";
    }
}
