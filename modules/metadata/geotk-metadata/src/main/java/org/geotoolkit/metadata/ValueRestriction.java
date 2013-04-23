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
package org.geotoolkit.metadata;

import java.util.Set;
import java.io.Serializable;
import java.lang.reflect.Method;
import net.jcip.annotations.Immutable;

import org.opengis.annotation.UML;
import org.opengis.annotation.Obligation;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.collection.WeakHashSet;
import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.resources.Errors;


/**
 * Restrictions that apply on a metadata property. Instances of {@code ValueRestriction}
 * are created only for properties having at least one non-null {@linkplain #obligation},
 * {@linkplain #range} or {@linkplain #validValues valid values enumeration} restriction.
 * <p>
 * For a given metadata instances (typically an {@link AbstractMetadata} subclasses, but
 * other types are allowed), instances of {@code ValueRestriction} are obtained indirectly
 * by the {@link MetadataStandard#asRestrictionMap MetadataStandard.asRestrictionMap(...)}
 * method.
 * <p>
 * {@code ValueRestriction} objects do not contain the type of values
 * (except indirectly through {@link NumberRange#getElementType()} or
 * {@link org.geotoolkit.util.collection.CheckedCollection#getElementType()})
 * because this particular requirement is specified by other means, like
 * {@link MetadataStandard#asTypeMap MetadataStandard.asTypeMap(...)}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @see MetadataStandard#asRestrictionMap(Object, NullValuePolicy, KeyNamePolicy)
 *
 * @since 3.04
 * @module
 */
@Immutable
public class ValueRestriction implements Serializable {
    /**
     * For cross-versions compatibility.
     */
    private static final long serialVersionUID = 888961503200860655L;

    /**
     * A sentinel value meaning that the restriction has not yet been calculated.
     */
    static final ValueRestriction PENDING = new ValueRestriction(null, null, null);

    /**
     * The instances created in this JVM. In many case, the same restriction will be shared
     * by many attributes (e.g. restricting the range of values to 0 .. 100 for percentage).
     */
    private static final WeakHashSet<ValueRestriction> POOL = WeakHashSet.newInstance(ValueRestriction.class);

    /**
     * Whatever the property is {@linkplain Obligation#MANDATORY mandatory} or
     * {@linkplain Obligation#FORBIDDEN forbidden}, or {@code null} if there is
     * no known restriction.
     * <p>
     * The {@linkplain Obligation#OPTIONAL optional} obligation is considered
     * equivalent to an absence of restriction and is replaced by {@code null}
     * if {@link MetadataStandard#asRestrictionMap MetadataStandard.asRestrictionMap(...)}
     * has been invoked with a metadata instance, since the metadata is not violating this
     * obligation. If {@code asRestrictionMap(...)} has been invoked with a {@link Class}
     * argument instead, then the obligation is provided verbatism since we are not testing
     * violation of restrictions.
     */
    public final Obligation obligation;

    /**
     * The range of valid values, or {@code null} if the values are not restricted by a range.
     * This restriction is typically exclusive with the {@linkplain #validValues enumeration
     * of valid values} (i.e. only one of {@code range} or {@code validValues} is non-null),
     * but this is not enforced by this {@code ValueRestriction} class.
     */
    public final NumberRange<?> range;

    /**
     * An enumeration of valid values, or {@code null} if the values are not restricted that way.
     * This restriction is typically exclusive with the {@linkplain #range range of valid values}
     * (i.e. only one of {@code range} or {@code validValues} is non-null), but this is not
     * enforced by this {@code ValueRestriction} class.
     *
     * @since 3.05
     */
    public final Set<?> validValues;

    /**
     * Creates a new {@code Restriction} instance. This constructor does not clone any
     * argument; this is caller responsibility to provide immutable instance of them,
     * especially {@code validValues}.
     *
     * {@note This constructor is not public in order to force subclassing. Subclasses
     *        shall choose a policy regarding whatever the arguments are cloned. This
     *        base class does not clone them because the same set of valid values (for
     *        example) is often shared for many metdata attributes.}
     *
     * @param obligation  Whatever the property is mandatory or forbidden, or {@code null} if unknown.
     * @param range       The range of valid values, or {@code null} if none.
     * @param validValues An enumeration of valid values, or {@code null} if none.
     *
     * @since 3.05
     */
    protected ValueRestriction(final Obligation obligation, final NumberRange<?> range, final Set<?> validValues) {
        this.obligation  = obligation;
        this.range       = range;
        this.validValues = validValues;
    }

    /**
     * Creates a new {@code ValueRestriction} instance. If all arguments are {@code null},
     * then this method returns {@code null} meaning "<cite>no restriction</cite>".
     *
     * @param  obligation Whatever the property is mandatory or forbidden, or {@code null} if unknown.
     * @param  range      The range of valid values, or {@code null} if none.
     * @param  validValues An enumeration of valid values, or {@code null} if none.
     * @return The restriction, or {@code null} if none.
     */
    static ValueRestriction create(final Obligation obligation, final NumberRange<?> range, final Set<?> validValues) {
        if (range == null && validValues == null && obligation != Obligation.MANDATORY && obligation != Obligation.FORBIDDEN) {
            return null;
        }
        return POOL.unique(new ValueRestriction(obligation, range, validValues));
    }

    /**
     * Creates a new {@code ValueRestriction} instance from the annotations on the given
     * getter method. If there is no restriction, then this method returns {@code null}.
     *
     * @param  type The return type if it is not a collection, or the type of elements
     *         if the return type is a collection.
     * @param  getter The getter method defined in the interface.
     * @param  impl The getter method defined in the implementation.
     * @return The restriction, or {@code null} if none.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    static ValueRestriction create(final Class<?> type, Method getter, final Method impl) {
        Obligation obligation = null;
        NumberRange<?>  range = null;
        final UML uml = getter.getAnnotation(UML.class);
        while (true) {
            if (uml != null) {
                obligation = uml.obligation();
            }
            final ValueRange vr = getter.getAnnotation(ValueRange.class);
            if (vr != null) {
                Class<?> required;
                if ((required = Number.class).isAssignableFrom(type) &&
                    (required = Comparable.class).isAssignableFrom(type))
                {
                    range = new NumberRange((Class) type, vr);
                } else {
                    throw new ClassCastException(Errors.format(Errors.Keys.ILLEGAL_CLASS_2, type, required));
                }
            }
            if (getter == impl) {
                break;
            }
            getter = impl;
        }
        return create(obligation, range, null);
    }

    /**
     * If the given value violate at least one restriction, returns the restrictions
     * that are violated. Otherwise returns {@code null}.
     *
     * @param  value The value to test (may be {@code null}).
     * @return {@code null} if the given value does not violate the restrictions.
     */
    final ValueRestriction violation(final Object value) {
        Obligation obligation = this.obligation;
        NumberRange<?>  range = this.range;
        Set<?>    validValues = this.validValues;
        boolean       changed = false;
        /*
         * If the value does not violate the obligation, set the obligation to null.
         */
        if (obligation != ((value == null) ? Obligation.MANDATORY : Obligation.FORBIDDEN)) {
            obligation = null;
            changed = true;
        }
        /*
         * If the value is not outside the range, set the range to null.
         */
        if (value == null || range == null || (value instanceof Number && range.contains((Number) value))) {
            range = null;
            changed = true;
        }
        /*
         * If the value is a member of the set of valid values, set the valid values to null.
         */
        if (value == null || validValues == null || validValues.contains(value)) {
            validValues = null;
            changed = true;
        }
        return changed ? create(obligation, range, validValues) : this;
    }

    /**
     * Compares the given object with this restriction for equality.
     *
     * @param  other The object to compare with this restriction for equality.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object other) {
        if (other != null && other.getClass() == getClass()) {
            final ValueRestriction that = (ValueRestriction) other;
            return (this.obligation == that.obligation) &&
                   Utilities.equals(this.range,       that.range) &&
                   Utilities.equals(this.validValues, that.validValues);
        }
        return false;
    }

    /**
     * Returns a hash code value for this restriction.
     */
    @Override
    public int hashCode() {
        int code = (int) serialVersionUID;
        if (obligation != null) {
            code ^= obligation.hashCode();
        }
        if (range != null) {
            code ^= range.hashCode();
        }
        if (validValues != null) {
            code ^= validValues.hashCode();
        }
        return code;
    }

    /**
     * Returns a string representation of this restriction.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append('[');
        String separator = "";
        if (obligation != null) {
            buffer.append(obligation.name());
            separator = ", ";
        }
        if (range != null) {
            buffer.append(separator).append("range=").append(range);
            separator = ", ";
        }
        if (validValues != null) {
            buffer.append(separator).append("validValues=").append(validValues);
        }
        return buffer.append(']').toString();
    }
}
