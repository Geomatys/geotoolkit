/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

import org.geotoolkit.internal.sql.Ordering;


/**
 * A column in a SQL {@linkplain Query query}. A collection of {@code Column} instances
 * allows the creation of SQL fragment like below (this example assumes 3 {@code Column}
 * instances named {@code c1}, {@code c2} and {@code c3}):
 *
 * {@preformat sql
 *     SELECT c1, c2, c3 FROM table;
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public final class Column extends ColumnOrParameter {
    /**
     * The name of the table in which this column is declared. This is usually the same
     * than {@link Query#table}, except if we are building a more complex query with
     * {@code JOIN} clauses.
     */
    public final String table;

    /**
     * The name of the column, as declared in the database.
     */
    public final String name;

    /**
     * The default value, or {@link #MANDATORY} if the value is mandatory.
     * Default value can be a {@link Number}, a {@link String} or {@code null}.
     */
    final Object defaultValue;

    /**
     * The ordering for each column. This field is the same reference than {@link Query#ordering}
     * and must be shared by every columns in the same query. We retain the map instead of the
     * whole {@link Query} object in order to avoid the retention of more objects than needed by
     * the garbage collector.
     */
    private final Map<Column,Ordering> ordering;

    /**
     * The types for which the ordering is applicable. Will be created only when first needed.
     * In the majority of cases, this set is never created.
     */
    private EnumSet<QueryType> orderUsage;

    /**
     * Creates a new column without query. This is used only for temporary columns to be
     * referenced by {@link CrossReference}. It should not be used for any other usage.
     *
     * @param table The name of the table in which this column is declared.
     * @param name  The name of the column, as declared in the database.
     */
    Column(final String table, final String name) {
        this.ordering     = null;
        this.table        = table;
        this.name         = name;
        this.defaultValue = null;
    }

    /**
     * Creates a column from the specified table with the specified name.
     * The new column is automatically added to the specified query.
     * <p>
     * Instances of this class can be created only one of the {@code Query.addColumn(...)}
     * method. This class is not designed for subclassing.
     *
     * @param query The query for which the column is created.
     * @param table The name of the table in which this column is declared.
     * @param name  The name of the column, as declared in the database.
     * @param defaultValue The default value if the column is not present in the database.
     *              It can be a {@link Number}, a {@link String} or {@code null}.
     * @param types The query types for which the column applies.
     */
    Column(final Query query, final String table, final String name,
           final Object defaultValue, final QueryType[] types)
    {
        super(query, types);
        this.ordering     = query.ordering;
        this.table        = table.trim();
        this.name         = name .trim();
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the function for this column when used in a query of the given type. The function is
     * used in the {@code SELECT} part of the SQL statement. This function is <strong>not</strong>
     * used in the {@code WHERE} part of the SQL statement.
     *
     * @param function The function to use with this column for the given types.
     * @param types The type of the queries for which to use the given function.
     */
    public void setFunction(final String function, final QueryType... types) {
        setFunction(0, function, types);
    }

    /**
     * Sets the ordering of this column to the specified value.
     *
     * @param order The ordering to set for this column.
     * @param types The query types for which the given ordering apply.
     */
    public void setOrdering(final Ordering order, final QueryType... types) {
        if (orderUsage == null) {
            orderUsage = EnumSet.noneOf(QueryType.class);
        }
        orderUsage.clear();
        orderUsage.addAll(Arrays.asList(types));
        ordering.put(this, order);
    }

    /**
     * Returns the ordering of this column for the given query type.
     * The returned ordering can be {@code "ASC"}, {@code "DESC"} or {@code null} if none.
     *
     * @param  type The type of the query for which to get the ordering.
     * @return The ordering for this column in a query of the given type, or {@code null} if none.
     */
    public Ordering getOrdering(final QueryType type) {
        return (orderUsage != null && orderUsage.contains(type)) ? ordering.get(this) : null;
    }

    /**
     * Formats the name of this column.
     *
     * @param buffer The buffer in which to write the name.
     * @param quote  The database-dependent identifier quote.
     */
    final void appendName(final StringBuilder buffer, final String quote) {
        buffer.append(quote).append(name).append(quote);
    }

    /**
     * Formats this column as a fully qualified name.
     *
     * @param buffer The buffer in which to write the name.
     * @param quote  The database-dependent identifier quote.
     */
    final void appendFullName(final StringBuilder buffer, final String quote) {
        buffer.append(quote).append(table).append(quote).append('.')
              .append(quote).append(name).append(quote);
    }

    /**
     * Returns a hash code value for this column.
     */
    @Override
    public int hashCode() {
        return name.hashCode() + super.hashCode();
    }

    /**
     * Compares this column with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            final Column that = (Column) object;
            return Objects.equals(this.table,        that.table) &&
                   Objects.equals(this.name,         that.name ) &&
                   Objects.equals(this.defaultValue, that.defaultValue) &&
                   Objects.equals(this.ordering,     that.ordering);
        }
        return false;
    }

    /**
     * Returns a string representation of this column.
     */
    @Override
    public String toString() {
        return "Column[" + table + '.' + name + ']';
    }
}
