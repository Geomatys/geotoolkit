/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;


/**
 * Performs conversions between instances of different classes.
 * <p>
 * <b>Implementation note:</b> The current design relies on parameterized types erasure.
 * For example a unique converter instance may be casted to any of the following:
 * <p>
 * <ul>
 *   <li>{@code ObjectConverter<Date,Long>} (the defining converter)</li>
 *   <li>{@code ObjectConverter<Date,Number>}</li>
 *   <li>{@code ObjectConverter<Timestamp,Long>}</li>
 *   <li>{@code ObjectConverter<Timestamp,Number>}</li>
 * </ul>
 * <p>
 * The parameter bounds of {@linkplain #getSourceClass source} and {@link #getTargetClass target}
 * classes are choosen consistently with the way in which a converter may be casted.
 * <p>
 * A side-effect of the above is that the converter interface can not safely provides a
 * {@code T inverseConvert(S)} method. An other converter needs to be obtained for inverse
 * conversions.
 *
 * @param <S> The base type of source objects.
 * @param <T> The base type of converted objects.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public interface ObjectConverter<S,T> {
    /**
     * Returns the base type of source objects.
     *
     * @return The base type of source objects.
     */
    Class<? super S> getSourceClass();

    /**
     * Returns the base type of target objects.
     *
     * @return The base type of target objects.
     */
    Class<? extends T> getTargetClass();

    /**
     * Returns {@code true} if this converter can convert only a subset {@code S} values. For
     * example a converter from {@link String} to {@link Number} can not convert every strings,
     * but only strings containing a well-formatted number.
     * <p>
     * If this method returns {@code false}, then no argument value can cause {@link #convert} to
     * fail. However it still possible for the conversion to fail because of external reasons,
     * for example failure to access a database.
     *
     * @return {@code true} if this converter accepts only a subset of {@code S} values.
     *
     * @since 3.00
     */
    boolean hasRestrictions();

    /**
     * Returns {@code true} if this converter preserves order. More specifically returns
     * {@code true} if for every {@linkplain Comparable comparable} values {@code A} and
     * {@code B}, the following relation hold:
     *
     * {@preformat java
     *     int s = sgn(A.compareTo(B));
     *     assert s == sgn(convert(A).compareTo(convert(B))) || s == 0;
     * }
     *
     * If the {@linkplain #getSourceClass source} or {@linkplain #getTargetClass target} classes
     * are not comparable, then this method returns {@code false}.
     *
     * @return {@code true} if this converter preserve order.
     *
     * @since 3.00
     */
    boolean isOrderPreserving();

    /**
     * Returns {@code true} if this converter reverses order. More specifically returns
     * {@code true} if for every {@linkplain Comparable comparable} values {@code A} and
     * {@code B}, the following relation hold (note the minus sign):
     *
     * {@preformat java
     *     int s = sgn(A.compareTo(B));
     *     assert s == -sgn(convert(A).compareTo(convert(B))) || s == 0;
     * }
     *
     * If the {@linkplain #getSourceClass source} or {@linkplain #getTargetClass target} classes
     * are not comparable, then this method returns {@code false}.
     * <p>
     * A converter mapping every source values to a constant target value is both order-preserving
     * and order-reversing. No other converter can be both.
     *
     * @return {@code true} if this converter reverse order.
     *
     * @since 3.00
     */
    boolean isOrderReversing();

    /**
     * Converts an object to an object of the target type.
     *
     * @param  source The original object, or {@code null}.
     * @return An instance of target, or {@code null} if the source was null.
     * @throws NonconvertibleObjectException If the conversion can not take place.
     */
    T convert(S source) throws NonconvertibleObjectException;

    // Do not provide inverseConvert(T) method. See class javadoc.
}
