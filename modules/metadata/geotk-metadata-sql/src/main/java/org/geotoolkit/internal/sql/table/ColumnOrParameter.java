/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;

import org.apache.sis.util.CharSequences;


/**
 * Base class for {@link Column} and {@link Parameter}. Those two sub-classes
 * represent a SQL element which can be identified by an index:
 * <p>
 * <ul>
 *   <li>{@link java.sql.ResultSet#getString(int)} for {@link Column}</li>
 *   <li>{@link java.sql.PreparedStatement#setString(int, String)} for {@link Parameter}</li>
 * </ul>
 * <p>
 * The index for a given {@code ColumnOrParameter} is computed from the index of previous
 * elements in the {@link Query} for which this instance is created.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public abstract class ColumnOrParameter {
    /**
     * The parameter index for each {@linkplain query type}. If a query type is not
     * supported by this element, then the corresponding index will be 0. Otherwise
     * any valid index shall be equal or greater than 1.
     */
    private final short[] index;

    /**
     * The functions to apply on this column or parameter. Will be created only if needed.
     * It a majority of cases, this map is never created.
     *
     * @see #getFunction(QueryType)
     */
    private EnumMap<QueryType,String> functions;

    /**
     * Creates a new language element without query. This is used only for temporary
     * columns to be referenced by {@link CrossReference}. It should not be used for
     * any other usage.
     */
    ColumnOrParameter() {
        index = null;
    }

    /**
     * Creates a new language element for the specified query.
     *
     * @param query  The query for which the element is created.
     * @param types  The query types for which the element applies.
     */
    ColumnOrParameter(final Query query, final QueryType... types) {
        this(query, (types != null && types.length != 0) ?
            EnumSet.copyOf(Arrays.asList(types)) : EnumSet.noneOf(QueryType.class));
    }

    /**
     * Creates a new language element for the specified query.
     *
     * @param query  The query for which the element is created.
     * @param types  The query types for which the element applies.
     */
    private ColumnOrParameter(final Query query, final EnumSet<QueryType> types) {
        /*
         * Computes the length of the 'index' array, which must be sufficient
         * for holding the last query type (in ordinal order).
         */
        int length = 0;
        for (final QueryType type : types) {
            final int ordinal = type.ordinal();
            if (ordinal >= length) {
                length = ordinal + 1;
            }
        }
        /*
         * Computes the index. For each QueryType supported by this language element, we scan
         * the previous elements until we find one supporting the same QueryType. The index
         * is then the previous index + columnSpan.
         */
        index = new short[length];
        final ColumnOrParameter[] existingElements = query.add(this);
search: for (final QueryType type : types) {
            final int typeOrdinal = type.ordinal();
            for (int i=existingElements.length; --i>=0;) {
                final ColumnOrParameter previous = existingElements[i];
                if (typeOrdinal < previous.index.length) {
                    short position = previous.index[typeOrdinal];
                    if (position != 0) {
                        if (++position < 0) {
                            throw new ArithmeticException("Overflow");
                        }
                        index[typeOrdinal] = position;
                        continue search;
                    }
                }
            }
            index[typeOrdinal] = 1;
        }
    }

    /**
     * Returns the element ({@linkplain Column column} or {@linkplain Parameter parameter}) index
     * when used in a query of the given type. Valid index numbers start at 1. This method returns
     * 0 if this language element is not applicable to a query of the specified type.
     *
     * @param  type The query type.
     * @return The element index in the SQL prepared statment, or 0 if none.
     */
    public final int indexOf(final QueryType type) {
        final int ordinal = type.ordinal();
        if (ordinal >= 0 && ordinal < index.length) {
            return index[ordinal];
        }
        return 0;
    }

    /**
     * Returns the function for this column or parameter when used in a query of the given type,
     * or {@code null} if none. The purpose of this method depends on the subclass:
     *
     * <ul>
     *   <li><p>If this class is an instance of {@link Column}, then the function returned by this
     *     method is an <cite>aggregate functions</cite> like {@code "MIN"} or {@code "MAX"} to be
     *     used in the {@code SELECT} part of the SQL statement. This function is <strong>not</strong>
     *     used in the {@code WHERE} part of the SQL statement.</p></li>
     *
     *   <li><p>If this class is an instance of {@link Parameter}, then the function returned by
     *     this method is applied on the parameter value. For example instead of {@code column=?},
     *     the caller way want {@code column=GeometryFromText(?,4326)}.</p></li>
     * </ul>
     *
     * @param  type The type of the query for which to get the function.
     * @return The function for the given query type, or {@code null} if none.
     */
    final String getFunction(final QueryType type) {
        return (functions != null) ? functions.get(type) : null;
    }

    /**
     * Sets a function for this column or parameter when used in a query of the given type.
     * See {@link #getFunction(QueryType)} for a meaning of what functions can be.
     *
     * @param numQuestionMarks The expected number of question marks in the {@code function}
     *        argument. This is 0 for the {@link Column} class and 1 for {@link Parameter}.
     * @param function The function to use with this column or parameter.
     * @param types The type of the queries for which to use the given function.
     */
    final void setFunction(final int numQuestionMarks, final String function, final QueryType... types) {
        if (CharSequences.count(function, '?') != numQuestionMarks) {
            throw new IllegalArgumentException(function);
        }
        if (functions == null) {
            functions = new EnumMap<QueryType,String>(QueryType.class);
        }
        for (final QueryType type : types) {
            functions.put(type, function);
        }
    }

    /**
     * Returns a hash code value for this language element.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(index);
    }

    /**
     * Compares this language element with the specified object for equality.
     *
     * @param object The object to compare with this column or parameter.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final ColumnOrParameter that = (ColumnOrParameter) object;
            return Arrays.equals(this.index, that.index);
        }
        return false;
    }
}
