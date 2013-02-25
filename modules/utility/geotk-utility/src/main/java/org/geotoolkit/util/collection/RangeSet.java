/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedSet;
import java.util.Comparator;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;


import org.geotoolkit.util.Range;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.Numbers.*;
import static org.apache.sis.util.Classes.getShortClassName;
import static org.apache.sis.util.ArgumentChecks.ensureCanCast;


/**
 * An ordered set of ranges. {@code RangeSet} objects store an arbitrary number of
 * {@linkplain Range ranges} in any Java primitives ({@code int}, {@code float},
 * <i>etc.</i>) or any {@linkplain Comparable comparable} objects.
 * <p>
 * Ranges can be added in any order. When a range is added, {@code RangeSet} first looks for an
 * existing range overlapping the specified range. If an overlapping range is found,
 * ranges are merged as of {@link Range#union(Range)}. Consequently, ranges returned by
 * {@link #iterator} may not be the same than added ranges.
 *
 * @param <T> The type of range elements.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Andrea Aime (TOPP)
 * @version 3.00
 *
 * @since 2.0
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.collection.RangeSet}.
 */
@Deprecated
public class RangeSet<T extends Comparable<? super T>> extends AbstractSet<Range<T>>
        implements CheckedCollection<Range<T>>, SortedSet<Range<T>>, Cloneable, Serializable
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -6085227672036239981L;

    /**
     * The comparator for ranges. Defined only in order to comply to {@link #comparator}
     * contract, but not used for internal working in this class.
     */
    private static final Comparator<Range<?>> COMPARATOR = new Comparator<Range<?>>() {
        @Override @SuppressWarnings("unchecked")
        public int compare(final Range r1, final Range r2) {
            int cmin = r1.getMinValue().compareTo(r2.getMinValue());
            int cmax = r1.getMaxValue().compareTo(r2.getMaxValue());
            if (cmin == 0) cmin = (r1.isMinIncluded() ? -1 : 0) - (r2.isMinIncluded() ? -1 : 0);
            if (cmax == 0) cmax = (r1.isMaxIncluded() ? +1 : 0) - (r2.isMaxIncluded() ? +1 : 0);
            if (cmin == cmax) return cmax; // Easy case: min and max are both greater, smaller or eq.
            if (cmin == 0)    return cmax; // Easy case: only max value differ.
            if (cmax == 0)    return cmin; // Easy case: only min value differ.
            // One range is included in the other.
            throw new IllegalArgumentException("Unordered ranges");
        }
    };

    /**
     * The {@linkplain #getElementType element class} of ranges.
     */
    private final Class<T> elementClass;

    /**
     * The type of elements to be stored in the internal array. If {@code elementClass} is a
     * wrapper, then {@code arrayElementClass} will be the corresponding primitive type. It
     * may also be the primitive type if the {@code elementClass} is convertible to a number
     * (for example {@link java.util.Date} which are converted to {@code long}).
     * <p>
     * This field should be considered as final. It is not because it needs to be restaured
     * on deserialization.
     */
    private transient Class<?> arrayElementClass;

    /**
     * The primitive type, as one of {@code DOUBLE}, {@code FLOAT}, {@code LONG}, {@code INTEGER},
     * {@code SHORT}, {@code BYTE}, {@code CHARACTER} or {@code OTHER} enumeration.
     * <p>
     * This field should be considered as final. It is not because it needs to be restaured
     * on deserialization.
     */
    private transient byte arrayElementCode;

    /**
     * {@code true} if {@code elementClass} is a subclass of {@link Number}. In such case,
     * instances of {@link NumberRange} should be created instead of {@link Range}.
     * <p>
     * This field should be considered as final. It is not because it needs to be restaured
     * on deserialization.
     */
    private transient boolean isNumeric;

    /**
     * {@code true} if {@code elementClass} is a subclass of {@link Date}. In such case,
     * instances of {@link DateRange} should be created instead of {@link Range}.
     * <p>
     * This field should be considered as final. It is not because it needs to be restaured
     * on deserialization.
     */
    private transient boolean isDate;

    /**
     * The converter to use for converting comparable objects to numbers,
     * or {@code null} if no conversions are needed.
     * <p>
     * This field should be considered as final. It is not because it needs to be restaured
     * on deserialization.
     */
    private transient ObjectConverter<T,Number> converter;

    /**
     * The converter to use for converting numbers to comparable objects,
     * or {@code null} if no conversions are needed.
     * <p>
     * This field should be considered as final. It is not because it needs to be restaured
     * on deserialization.
     */
    private transient ObjectConverter<? extends Number,T> inverseConverter;

    /**
     * The array of intervals. It may be either an array of Java primitive type like {@code int[]}
     * or {@code float[]}, or an array of {@code Comparable[]}. Elements in this array must be
     * strictly increasing without duplicated values.
     */
    private Object array;

    /**
     * The length of valid elements in the array.
     */
    private transient int length;

    /**
     * The amount of modifications applied on the interval array.
     * Used for checking concurrent modifications.
     */
    private transient int modCount;

    /**
     * Constructs an empty set of range.
     *
     * @param type The class of the range elements.
     */
    public RangeSet(final Class<T> type) {
        if (!Comparable.class.isAssignableFrom(type)) {
            // Should never happen if parameterized type was checked.
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NOT_COMPARABLE_CLASS_$1, type));
        }
        elementClass = type;
        initialize();
    }

    /**
     * Initializes transient fields. Invoked on construction and on deserialization.
     */
    private void initialize() {
        Class<?> transferClass = elementClass;
        isDate = Date.class.isAssignableFrom(transferClass);
        isNumeric = Number.class.isAssignableFrom(transferClass);
        if (!isNumeric) try {
            final ObjectConverter<T,Number> direct;
            final ObjectConverter<? extends Number,T> inverse;
            final ConverterRegistry registry = ConverterRegistry.system();
            direct = registry.converter(elementClass, Number.class);
            if (isAcceptable(direct)) {
                inverse = registry.converter(direct.getTargetClass(), elementClass);
                if (isAcceptable(inverse)) {
                    converter = direct;
                    inverseConverter = inverse;
                    transferClass = direct.getTargetClass();
                }
            }
        } catch (NonconvertibleObjectException e) {
            // Ignore - it is perfectly legal if there is no converter to numbers.
        }
        arrayElementClass = wrapperToPrimitive(transferClass);
        arrayElementCode  = getEnumConstant(arrayElementClass);
    }

    /**
     * Returns {@code true} if the given converter is acceptable for the purpose
     * of {@link RangeSet}.
     */
    private static boolean isAcceptable(final ObjectConverter<?,?> converter) {
        return !converter.hasRestrictions() &&
                (converter.isOrderPreserving() || converter.isOrderReversing());
    }

    /**
     * Returns the type of elements in this collection. This is typically the {@link NumberRange}
     * or {@link DateRange} class, or failing that the plain {@link Range} class.
     *
     * @since 3.00
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public Class<? extends Range<T>> getElementType() {
        if (isNumeric) return (Class) NumberRange.class;
        if (isDate)    return (Class) DateRange.class;
        return (Class) Range.class;
    }

    /**
     * Returns the type of elements in the internal array.
     * this is used for testing purpose only.
     */
    final Class<?> getArrayElementType() {
        return arrayElementClass;
    }

    /**
     * Converts a value from an arbitrary type to the wrapper of {@link #arrayElementClass}.
     *
     * @param  value The value to convert.
     * @param  name The parameter name, for formatting error message.
     * @throws IllegalArgumentException if the given value is not of the expected class,
     *         or if the conversion failed.
     */
    private Comparable<?> toArrayElement(Comparable<?> value, final String name)
            throws IllegalArgumentException
    {
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
        ensureCanCast("value", isNumeric ? Number.class : elementClass, value);
        if (converter == null) {
            return value;
        }
        try {
            @SuppressWarnings({"unchecked","rawtypes"})
            final Comparable<?> result = (Comparable<?>) ((ObjectConverter) converter).convert(value);
            return result;
        } catch (ClassCastException cause) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_CLASS_$2, value.getClass(), elementClass), cause);
        } catch (NonconvertibleObjectException cause) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, value), cause);
        }
    }

    /**
     * Compares two object of unknown type.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static int compare(final Comparable<?> lower, final Comparable<?> upper) {
        return ((Comparable) lower).compareTo((Comparable) upper);
    }

    /**
     * Returns the comparator associated with this sorted set.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"}) // Because we share the same static COMPARATOR instance.
    public Comparator<Range<T>> comparator() {
        return (Comparator) COMPARATOR;
    }

    /**
     * Remove all elements from this set of ranges.
     */
    @Override
    public void clear() {
        if (array instanceof Object[]) {
            Arrays.fill((Object[]) array, 0, length, null);
        }
        length = 0;
        modCount++;
    }

    /**
     * Returns the number of ranges in this set.
     */
    @Override
    public int size() {
        return length / 2;
    }

    /**
     * Inserts two values at the given index.
     *
     * @param i0    The index where to insert the values.
     * @param lower The first value to insert.
     * @param upper The second value to insert.
     */
    private void insertAt(final int i0, final Comparable<?> lower, final Comparable<?> upper) {
        final Object old = array;
        final int capacity = Array.getLength(array);
        if (length + 2 > capacity) {
            array = Array.newInstance(arrayElementClass, 2*Math.max(capacity, 8));
            System.arraycopy(old, 0, array, 0, i0);
        }
        System.arraycopy(old, i0, array, i0+2, length-i0);
        Array.set(array, i0+0, lower);
        Array.set(array, i0+1, upper);
        length += 2;
    }

    /**
     * Removes the values in the given range.
     *
     * @param i0 First value to remove, inclusive.
     * @param i1 Last value to remove, exclusive.
     */
    private void removeAt(final int i0, final int i1) {
        final int old = length;
        System.arraycopy(array, i1, array, i0, old-i1);
        length -= (i1 - i0);
        if (array instanceof Object[]) {
            // Clear references so the garbage collector can do its job.
            Arrays.fill((Object[]) array, length, old, null);
        }
    }

    /**
     * Adds a range to this set. Ranges may be added in any order. If the specified range
     * overlaps an existing range, the two ranges will be merged as of {@link Range#union(Range)}.
     * <p>
     * <b>Note:</b> current version do not support open intervals
     * (i.e. {@code Range.is[Min/Max]Included()} must return {@code true}).
     *
     * @param  range The range to add.
     * @return {@code true} if this set changed as a result of the call.
     *
     * @todo Support open intervals.
     */
    @Override
    public boolean add(final Range<T> range) {
        if (!range.isMinIncluded() || !range.isMaxIncluded()) {
            throw new UnsupportedOperationException("Open interval not yet supported");
        }
        return addRange(range.getMinValue(), range.getMaxValue());
    }

    /**
     * Adds a range of values to this set. Ranges may be added in any order.
     * If the specified range overlaps an existing range, the two ranges will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(final T min, final T max) throws IllegalArgumentException {
        return addRange(min, max);
    }

    /**
     * Implementation of {@code add} methods.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    private boolean addRange(final Comparable<?> min, final Comparable<?> max)
            throws IllegalArgumentException
    {
        Comparable<?> lower = toArrayElement(min, "min");
        Comparable<?> upper = toArrayElement(max, "max");
        if (compare(lower, upper) > 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_RANGE_$2, min, max));
        }
        if (array == null) {
            modCount++;
            array = Array.newInstance(arrayElementClass, 32);
            Array.set(array, 0, lower);
            Array.set(array, 1, upper);
            length = 2;
            return true;
        }
        final int modCountChk = modCount;
        int i0 = binarySearch(lower);
        int i1;
        if (i0 < 0) {
            /*
             * Si le début de la plage ne correspond pas à une des dates en
             * mémoire, il faudra l'insérer à quelque part dans le tableau.
             * Si la date tombe dans une des plages déjà existantes (si son
             * index est impair), on étend la date de début pour prendre le
             * début de la plage. Visuellement, on fait:
             *
             *   0   1     2      3     4   5    6     7
             *   #####     ########     #####    #######
             *             <---^           ^
             *             lower(i=3)   upper(i=5)
             */
            if (((i0 = ~i0) & 1) != 0) { // Attention: c'est ~ et non -
                lower = (Comparable<?>) Array.get(array, --i0);
                i1 = binarySearch(upper);
            } else {
                /*
                 * Si la date de début ne tombe pas dans une plage déjà
                 * existante, il faut étendre la valeur de début qui se
                 * trouve dans le tableau. Visuellement, on fait:
                 *
                 *   0   1     2      3     4   5    6     7
                 *   #####  ***########     #####    #######
                 *          ^                 ^
                 *       lower(i=2)        upper(i=5)
                 */
                modCount++;
                if (i0 != length && (i1 = binarySearch(upper)) != ~i0) {
                    Array.set(array, i0, lower);
                } else {
                    /*
                     * Un cas particulier se produit si la nouvelle plage
                     * est à insérer à la fin du tableau. Dans ce cas, on
                     * n'a qu'à agrandir le tableau et écrire les valeurs
                     * directement à la fin. Ce traitement est nécessaire
                     * pour eviter les 'ArrayIndexOutOfBoundsException'.
                     * Un autre cas particulier se produit si la nouvelle
                     * plage est  entièrement  comprise entre deux plages
                     * déjà existantes.  Le même code ci-dessous insèrera
                     * la nouvelle plage à l'index 'i0'.
                     */
                    insertAt(i0, lower, upper);
                    return true;
                }
            }
        } else {
            i0 &= ~1;
            i1 = binarySearch(upper);
        }
        /*
         * A ce stade, on est certain que 'i0' est pair et pointe vers le début
         * de la plage dans le tableau. Fait maintenant le traitement pour 'i1'.
         */
        if (i1 < 0) {
            /*
             * Si la date de fin tombe dans une des plages déjà existantes
             * (si son index est impair), on l'étend pour pendre la fin de
             * la plage trouvée dans le tableau. Visuellement, on fait:
             *
             *   0   1     2      3     4   5    6     7
             *   #####     ########     #####    #######
             *             ^             ^-->
             *          lower(i=2)     upper(i=5)
             */
            if (((i1 = ~i1) & 1) != 0) { // Attention: c'est ~ et non -
                upper = (Comparable<?>) Array.get(array, i1);
            } else {
                /*
                 * Si la date de fin ne tombe pas dans une plage déjà
                 * existante, il faut étendre la valeur de fin qui se
                 * trouve dans le tableau. Visuellement, on fait:
                 *
                 *   0   1     2      3     4   5    6     7
                 *   #####     ########     #####**  #######
                 *             ^                  ^
                 *          lower(i=2)         upper(i=6)
                 */
                modCount++;
                Array.set(array, --i1, upper);
            }
        } else {
            i1 |= 1;
        }
        /*
         * At this point, 'lower' and 'upper' are the bounds of the range in this
         * RangeSet which contains the range given in argument to this method. For
         * now we do nothing with this range. A future version could notify some
         * listeners...
         */
        assert compare(lower, upper) <= 0;
        /*
         * A ce stade, on est certain que 'i1' est impair et pointe vers la fin
         * de la plage dans le tableau. On va maintenant supprimer tout ce qui
         * se trouve entre 'i0' et 'i1', à l'exclusion de 'i0' et 'i1'.
         */
        assert (i0 & 1) == 0 : i0;
        assert (i1 & 1) != 0 : i1;
        final int n = i1 - (++i0);
        if (n > 0) {
            modCount++;
            removeAt(i0, i1);
        }
        assert (Array.getLength(array) >= length) && (length & 1) == 0 : length;
        return modCountChk != modCount;
    }

    /**
     * Adds a range of values to this set. Range may be added in any order.
     * If the specified range overlaps an existing range, the two ranges
     * will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(byte min, byte max) throws IllegalArgumentException {
        return addRange(Byte.valueOf(min), Byte.valueOf(max));
    }

    /**
     * Adds a range of values to this set. Range may be added in any order.
     * If the specified range overlaps an existing range, the two ranges
     * will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(short min, short max) throws IllegalArgumentException {
        return addRange(Short.valueOf(min), Short.valueOf(max));
    }

    /**
     * Adds a range of values to this set. Range may be added in any order.
     * If the specified range overlaps an existing range, the two ranges
     * will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(int min, int max) throws IllegalArgumentException {
        return addRange(Integer.valueOf(min), Integer.valueOf(max));
    }

    /**
     * Adds a range of values to this set. Range may be added in any order.
     * If the specified range overlaps an existing range, the two ranges
     * will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(long min, long max) throws IllegalArgumentException {
        return addRange(Long.valueOf(min), Long.valueOf(max));
    }

    /**
     * Adds a range of values to this set. Range may be added in any order.
     * If the specified range overlaps an existing range, the two ranges
     * will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(float min, float max) throws IllegalArgumentException {
        return addRange(Float.valueOf(min), Float.valueOf(max));
    }

    /**
     * Adds a range of values to this set. Range may be added in any order.
     * If the specified range overlaps an existing range, the two ranges
     * will be merged.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, inclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean add(double min, double max) throws IllegalArgumentException {
        return addRange(Double.valueOf(min), Double.valueOf(max));
    }

    /**
     * Removes a range to this set.
     * <p>
     * <b>Note:</b> current version do not support closed intervals
     * (i.e. {@code Range.is[Min/Max]Included()} must return {@code false}).
     *
     * @param  object The range to remove.
     * @return {@code true} if this set changed as a result of the call.
     *
     * @todo Support closed intervals.
     */
    @Override
    public boolean remove(final Object object) {
        if (!(object instanceof Range<?>)) {
            return false;
        }
        final Range<?> range = (Range<?>) object;
        if (range.isMinIncluded() || range.isMaxIncluded()) {
            throw new UnsupportedOperationException("Closed interval not yet supported");
        }
        return removeRange(range.getMinValue(), range.getMaxValue());
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(final T min, final T max) throws IllegalArgumentException {
        return removeRange(min, max);
    }

    /**
     * Implementation of {@code remove} methods.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    private boolean removeRange(final Comparable<?> min, final Comparable<?> max)
            throws IllegalArgumentException
    {
        Comparable<?> lower = toArrayElement(min, "min");
        Comparable<?> upper = toArrayElement(max, "max");
        if (compare(lower, upper) >= 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_RANGE_$2, min, max));
        }
        // if already empty, or range outside the current set, nothing to change
        if (length == 0) {
            return false;
        }
        final int modCountChk = modCount;
        int i0 = binarySearch(lower);
        int i1 = binarySearch(upper);
        if (i0 < 0) {
            if (((i0 = ~i0) & 1) != 0) { // Attention: c'est ~ et non -
                /*
                 * Si le début de la plage ne correspond pas à une des dates en mémoire,
                 * il faudra faire un trou à quelque part dans le tableau. Si la date tombe
                 * dans une des plages déjà existantes (si son index est impair), on change
                 * la date de fin de la plage existante. Visuellement, on fait:
                 *
                 *   0   1     2      3     4   5    6     7
                 *   #####     #####---     --###    #######
                 *                 ^          ^
                 *             lower(i=3)   upper(i=5)
                 */
                modCount++;
                if (i1 != ~i0) {
                    Array.set(array, i0, lower);
                } else {
                    /*
                     * Special case if the upper index is inside the same range than the lower one:
                     *
                     *   0   1     2                3     4   5
                     *   #####     ####---------#####     #####
                     *                ^         ^
                     *           lower(i=3)   upper(i=3)
                     */
                    insertAt(i0, lower, upper);
                    return true;
                }
            } else {
                /*
                 * Si la date de début ne tombe pas dans une plage déjà
                 * existante, il faut prendre la date de fin de la plage
                 * précédente. Visuellement, on fait:
                 *
                 *   0   1     2      3     4   5    6     7
                 *   #####     ########     #####    #######
                 *       <---^                  ^
                 *       lower(i=2)        upper(i=5)
                 */
                i0--;
            }
        } else {
            if ((i0 & 1) == 0) {
                i0--;
            }
        }
        /*
         * A ce stade, on est certain que 'i0' est impair et pointe vers la fin
         * d'une plage dans le tableau. Fait maintenant le traitement pour 'i1'.
         */
        if (i1 < 0) {
            /*
             * Si la date de fin tombe dans une des plages déjà existantes
             * (si son index est impair), on change la date de début de la
             * plage existante. Visuellement, on fait:
             *
             *   0   1     2      3     4   5    6     7
             *   #####     ########     --###    #######
             *                    ^       ^
             *            lower(i=3)    upper(i=5)
             */
            if (((i1 = ~i1) & 1) != 0) { // Attention: c'est ~ et non -
                modCount++;
                Array.set(array, --i1, upper);
            } else {
                /*
                 * Si la date de fin ne tombe pas dans une plage déjà existante, il
                 * faudra (plus tard) supprimer les éventuelles plages qui le précède.
                 *
                 *   0   1     2      3        4     5        6         7
                 *   #####     ########        #######        ###########
                 *                    ^                  ^
                 *            lower(i=3)         upper(i=6)
                 */
                // nothing to do
            }
        } else {
            i1 &= ~1;
        }
        /*
         * A ce stade, on est certain que 'i1' est pair et pointe vers la début
         * de la plage dans le tableau. On va maintenant supprimer tout ce qui
         * se trouve entre 'i0' et 'i1', à l'exclusion de 'i0' et 'i1'.
         */
        assert (i0 & 1) != 0 : i0;
        assert (i1 & 1) == 0 : i1;
        final int n = i1 - (++i0);
        if (n > 0) {
            modCount++;
            removeAt(i0, i1);
        }
        assert (Array.getLength(array) >= length) && (length & 1) == 0;
        return modCountChk != modCount;
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(byte min, byte max) throws IllegalArgumentException {
        return removeRange(Byte.valueOf(min), Byte.valueOf(max));
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(short min, short max) throws IllegalArgumentException {
        return removeRange(Short.valueOf(min), Short.valueOf(max));
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(int min, int max) throws IllegalArgumentException {
        return removeRange(Integer.valueOf(min), Integer.valueOf(max));
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(long min, long max) throws IllegalArgumentException {
        return removeRange(Long.valueOf(min), Long.valueOf(max));
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(float min, float max) throws IllegalArgumentException {
        return removeRange(Float.valueOf(min), Float.valueOf(max));
    }

    /**
     * Removes a range of values from this set. Range may be removed in any order.
     *
     * @param  min The lower value to remove, exclusive.
     * @param  max The upper value to remove, exclusive.
     * @return {@code true} if this set changed as a result of the call.
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}.
     */
    public boolean remove(double min, double max) throws IllegalArgumentException {
        return removeRange(Double.valueOf(min), Double.valueOf(max));
    }

    /**
     * Returns the index of {@code value} in {@link #array}. This method delegates to
     * one of {@code Arrays.binarySearch} methods, depending on element primary type.
     *
     * @param value The value to search. If a conversion to a {@link Number} was required,
     *        then it must have been performed prior this method call.
     */
    @SuppressWarnings("unchecked")
    private int binarySearch(final Comparable<?> value) {
        switch (arrayElementCode) {
            case DOUBLE:   return Arrays.binarySearch((double[]) array, 0, length, ((Number)    value).doubleValue());
            case FLOAT:    return Arrays.binarySearch((float []) array, 0, length, ((Number)    value).floatValue ());
            case LONG:     return Arrays.binarySearch((long  []) array, 0, length, ((Number)    value).longValue  ());
            case INTEGER:  return Arrays.binarySearch((int   []) array, 0, length, ((Number)    value).intValue   ());
            case SHORT:    return Arrays.binarySearch((short []) array, 0, length, ((Number)    value).shortValue ());
            case BYTE:     return Arrays.binarySearch((byte  []) array, 0, length, ((Number)    value).byteValue  ());
            case CHARACTER:return Arrays.binarySearch((char  []) array, 0, length, ((Character) value).charValue  ());
            default:       return Arrays.binarySearch((Object[]) array, 0, length,              value);
        }
    }

    /**
     * Returns a new {@link Range} object initialized with the given values.
     *
     * @param lower The lower value, inclusive.
     * @param upper The upper value, inclusive.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private Range<T> newRange(final T lower, final T upper) {
        if (isNumeric) {
            return new NumberRange(elementClass, (Number) lower, (Number) upper);
        } else if (isDate) {
            return (Range) new DateRange((Date) (Comparable) lower, (Date) (Comparable) upper);
        } else {
            return new Range<>(elementClass, lower, upper);
        }
    }

    /**
     * Returns the value at the specified index. Even index are lower bounds, while odd index
     * are upper bounds. The index validity must have been checked before this method is invoked.
     */
    private T get(final int index) {
        assert index >= 0 && index < length : index;
        Object value = Array.get(array, index);
        /*
         * The inverse converter is declared as <? extends Number,T> which can not be used directly
         * (we could if the declaration was <Number,T> - without wildcard). Given that every values
         * stored in this RangeSet were converted using this.converter and given that the target of
         * the later is the source of this.inverseConverter, then the inverse converter is expected
         * to be useable in all cases. The cast to <Object,T> is false - we are cheating - but will
         * work given the way generic types are implemented in Java (by erasure). If it fails, then
         * we have a programming error in this class or in ConverterRegistry.
         */
        @SuppressWarnings({"unchecked","rawtypes"})
        final ObjectConverter<Object,T> inverseConverter = (ObjectConverter) this.inverseConverter;
        if (inverseConverter != null) {
            assert inverseConverter.getSourceClass().isInstance(value) : value;
            try {
                return inverseConverter.convert(value);
            } catch (NonconvertibleObjectException exception) {
                // Should not happen, since class type should
                // have been checked by all 'add(...)' methods
                throw new IllegalStateException(exception);
            }
        }
        return elementClass.cast(value);
    }

    /**
     * Returns a {@linkplain Range#getMinValue range's minimum value} as a {@code double}.
     * The {@code index} can be any value from 0 inclusive to the set's {@link #size size}
     * exclusive. The returned values always increase with {@code index}.
     *
     * @param  index The range index, from 0 inclusive to {@link #size size} exclusive.
     * @return The minimum value for the range at the specified index.
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     * @throws ClassCastException if range elements are not convertible to numbers.
     */
    public final double getMinValueAsDouble(int index)
            throws IndexOutOfBoundsException, ClassCastException
    {
        if ((index *= 2) >= length) {
            throw new IndexOutOfBoundsException();
        }
        return Array.getDouble(array, index);
    }

    /**
     * Returns a {@linkplain Range#getMaxValue range's maximum value} as a {@code double}.
     * The {@code index} can be any value from 0 inclusive to the set's {@link #size size}
     * exclusive. The returned values always increase with {@code index}.
     *
     * @param  index The range index, from 0 inclusive to {@link #size size} exclusive.
     * @return The maximum value for the range at the specified index.
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     * @throws ClassCastException if range elements are not convertible to numbers.
     */
    public final double getMaxValueAsDouble(int index)
            throws IndexOutOfBoundsException, ClassCastException
    {
        if ((index *= 2) >= length) {
            throw new IndexOutOfBoundsException();
        }
        return Array.getDouble(array, index + 1);
    }

    /**
     * If the specified value is inside a range, returns the index of this range.
     * Otherwise, returns {@code -1}.
     *
     * @param  value The value to search.
     * @return The index of the range which contains this value, or -1 if there is no such range.
     */
    public int indexOfRange(final Comparable<?> value) {
        int index = binarySearch(toArrayElement(value, "value"));
        if (index < 0) {
            // Found an insertion point. Make sure that the insertion
            // point is inside a range (i.e. before the maximum value).
            index = ~index; // Tild sign, not minus.
            if ((index & 1) == 0) {
                return -1;
            }
        }
        index /= 2; // Round toward 0 (odd index are maximum values).
        assert newRange(get(2*index), get(2*index+1)).contains(value) : value;
        return index;
    }

    /**
     * Returns {@code true} if this set contains the specified element.
     *
     * @param object The object to compare to this set.
     * @return {@code true} if the given object is equal to this set.
     */
    @Override
    public boolean contains(final Object object) {
        @SuppressWarnings("unchecked") // We are going to check just the line after.
        final Range<T> range = (Range<T>) object;
        if (range.getElementType() == elementClass) {
            if (range.isMinIncluded() && range.isMaxIncluded()) {
                final int index = binarySearch(toArrayElement(range.getMinValue(), "object"));
                if (index >= 0 && (index & 1)==0) {
                    final int c = get(index+1).compareTo(range.getMaxValue());
                    return c == 0;
                }
            }
        }
        return false;
    }

    /**
     * Returns the first (lowest) range currently in this sorted set.
     *
     * @throws NoSuchElementException if the set is empty.
     */
    @Override
    public Range<T> first() throws NoSuchElementException {
        if (length != 0) {
            return newRange(get(0), get(1));
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the last (highest) range currently in this sorted set.
     *
     * @throws NoSuchElementException if the set is empty.
     */
    @Override
    public Range<T> last() throws NoSuchElementException {
        if (length != 0) {
            return newRange(get(length-2), get(length-1));
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns a view of the portion of this sorted set whose elements range
     * from {@code lower}, inclusive, to {@code upper}, exclusive.
     *
     * @param  lower Low endpoint (inclusive) of the sub set.
     * @param  upper High endpoint (exclusive) of the sub set.
     * @return A view of the specified range within this sorted set.
     */
    @Override
    public SortedSet<Range<T>> subSet(final Range<T> lower, final Range<T> upper) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly less than {@code upper}.
     *
     * @param  upper High endpoint (exclusive) of the headSet.
     * @return A view of the specified initial range of this sorted set.
     */
    @Override
    public SortedSet<Range<T>> headSet(final Range<T> upper) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * greater than or equal to {@code lower}.
     *
     * @param  lower Low endpoint (inclusive) of the tailSet.
     * @return A view of the specified final range of this sorted set.
     */
    @Override
    public SortedSet<Range<T>> tailSet(final Range<T> lower) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns an iterator over the elements in this set of ranges.
     * All elements are {@link Range} objects.
     */
    @Override
    public java.util.Iterator<Range<T>> iterator() {
        return new Iterator();
    }


    /**
     * An iterator for iterating through ranges in a {@link RangeSet}.
     * All elements are {@link Range} objects.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     */
    private final class Iterator implements java.util.Iterator<Range<T>> {
        /**
         * Modification count at construction time.
         */
        private int modCount = RangeSet.this.modCount;

        /**
         * The array length.
         */
        private final int length = RangeSet.this.length;

        /**
         * Current position in {@link RangeSet#array}.
         */
        private int position;

        /**
         * Returns {@code true} if the iteration has more elements.
         */
        @Override
        public boolean hasNext() {
            return position < length;
        }

        /**
         * Returns the next element in the iteration.
         */
        @Override
        public Range<T> next() {
            if (hasNext()) {
                final T lower = get(position++);
                final T upper = get(position++);
                if (RangeSet.this.modCount != modCount) {
                    // Check it last, in case a change occurred
                    // while we was constructing the element.
                    throw new ConcurrentModificationException();
                }
                return newRange(lower, upper);
            }
            throw new NoSuchElementException();
        }

        /**
         * Removes from the underlying collection the
         * last element returned by the iterator.
         */
        @Override
        public void remove() {
            if (position != 0) {
                if (RangeSet.this.modCount == modCount) {
                    removeAt(position-2, position);
                    modCount = ++RangeSet.this.modCount;
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Trims this set to the minimal amount of memory required for holding its data.
     * This method may be invoked after a set construction is completed in order to
     * free unused memory.
     *
     * @since 3.00
     */
    public final void trimToSize() {
        // This method is final because equals(Object) and other relies on this behavior.
        if (array != null && Array.getLength(array) != length) {
            copyArray();
        }
    }

    /**
     * Returns a hash value for this set of ranges.
     * This value need not remain consistent between
     * different implementations of the same class.
     */
    @Override
    public int hashCode() {
        int code = elementClass.hashCode();
        for (int i=length; (i -= 8) >= 0;) {
            code = Utilities.hash(Array.get(array, i), code);
        }
        return code;
    }

    /**
     * Compares the specified object with this set of ranges for equality.
     *
     * @param object The object to compare with this range.
     * @return {@code true} if the given object is equal to this range.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final RangeSet<?> that = (RangeSet<?>) object;
            if (length == that.length && Objects.equals(elementClass, that.elementClass)) {
                this.trimToSize();
                that.trimToSize();
                final Object a1 = this.array;
                final Object a2 = that.array;
                switch (arrayElementCode) {
                    case DOUBLE:   return Arrays.equals((double[]) a1, (double[]) a2);
                    case FLOAT:    return Arrays.equals((float []) a1, ( float[]) a2);
                    case LONG:     return Arrays.equals((long  []) a1, (  long[]) a2);
                    case INTEGER:  return Arrays.equals((int   []) a1, (   int[]) a2);
                    case SHORT:    return Arrays.equals((short []) a1, ( short[]) a2);
                    case BYTE:     return Arrays.equals((byte  []) a1, (  byte[]) a2);
                    case CHARACTER:return Arrays.equals((char  []) a1, (  char[]) a2);
                    default:       return Arrays.equals((Object[]) a1, (Object[]) a2);
                }
            }
        }
        return false;
    }

    /**
     * Returns a clone of this range set.
     *
     * @return A clone of this range set.
     */
    @Override
    @SuppressWarnings("unchecked")
    public RangeSet<T> clone() {
        final RangeSet<T> set;
        try {
            set = (RangeSet<T>) super.clone();
        } catch (CloneNotSupportedException exception) {
            // Should not happen, since we are cloneable.
            throw new AssertionError(exception);
        }
        set.copyArray();
        return set;
    }

    /**
     * Copies the internal array. The new array will use only the required length.
     */
    private void copyArray() {
        if (length == 0) {
            array = null;
        } else switch (arrayElementCode) {
            case DOUBLE:    array = Arrays.copyOf((double[]) array, length); break;
            case FLOAT:     array = Arrays.copyOf((float []) array, length); break;
            case LONG:      array = Arrays.copyOf((long  []) array, length); break;
            case INTEGER:   array = Arrays.copyOf((int   []) array, length); break;
            case SHORT:     array = Arrays.copyOf((short []) array, length); break;
            case BYTE:      array = Arrays.copyOf((byte  []) array, length); break;
            case CHARACTER: array = Arrays.copyOf((char  []) array, length); break;
            default:        array = Arrays.copyOf((Object[]) array, length); break;
        }
    }

    /**
     * Returns a string representation of this set of ranges. The returned string
     * is implementation dependent and should be used for debugging purposes only.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(getShortClassName(this));
        buffer.append('[');
        boolean first = true;
        for (final Range<T> range : this) {
            if (!first) {
                buffer.append(',');
            }
            buffer.append('{') .append(range.getMinValue())
                  .append('\u2026').append(range.getMaxValue()).append('}');
            first = false;
        }
        return buffer.append(']').toString();
    }

    /**
     * Invoked before serialization. Trims the internal array to the minimal size
     * in order to reduce the size of the object to be serialized.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        trimToSize();
        out.defaultWriteObject();
    }

    /**
     * Invoked after deserialization. Initializes the transient fields.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initialize();
        if (array != null) {
            length = Array.getLength(array);
        }
    }
}
