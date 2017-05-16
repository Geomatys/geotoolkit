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


/**
 * A parameter in a SQL {@linkplain Query query}. A collection of {@code Parameter} instances
 * allows the creation of SQL fragment like below (this example assumes 3 {@code Parameter}
 * instances associated to columns named {@code c1}, {@code c2} and {@code c3}):
 *
 * {@preformat sql
 *     WHERE c1=? AND c2=? AND c3=?;
 * }
 *
 * The {@code =} operator can be replaced by {@code LIKE} or any other comparison operation
 * using the {@link #setComparator(String)} method. The default comparator is {@code =}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public final class Parameter extends ColumnOrParameter {
    /**
     * Binary operators to be processed in a special way if present in the comparator.
     */
    private static final String[] BINARY_OP = {" AND ", " OR "};

    /**
     * The column on which this parameter applies.
     */
    final Column column;

    /**
     * The comparison operator to put in the prepared statement.
     */
    private String comparator = "=";

    /**
     * Creates a new parameter for the specified query.
     * This constructor is not public on intend: instances of {@code Parameter}
     * can be created only by {@link Query#addParameter addParameter(...)}.
     * <p>
     * Instances of this class can be created only by the {@link Query#addParameter addParameter(...)}
     * method. This class is not designed for subclassing.
     *
     * @param query  The query for which the parameter is created.
     * @param column The column on which the parameter is applied.
     * @param types Types of the queries where the parameter shall appears.
     */
    Parameter(final Query query, final Column column, final QueryType... types) {
        super(query, types);
        this.column = column;
    }

    /**
     * Sets the function for this parameter when used in a query of the given type. The function
     * shall contain exactly one question mark for the parameter value. For example instead of
     * searching the record for which the column value is equals to {@code ?}, the caller way
     * want to search for record for which the parameter value is equals to
     * {@code GeometryFromText(?,4326)}.
     * <p>
     * If this method is never invoked, the default value is {@code ?}.
     *
     * @param function The function to use with this column for the given types.
     * @param types The type of the queries for which to use the given function.
     */
    public void setSearchValue(final String function, final QueryType... types) {
        setFunction(1, function, types);
    }

    /**
     * Sets the comparison operator to put in the prepared statement. This method
     * is typically invoked for setting the comparison operator to {@code "LIKE"}.
     * If this method is never invoked, then the default value is {@code "="}.
     * <p>
     * This method accepts {@code AND} and {@code OR} operations provided that the question
     * mark apply only to the very last condition. For example the {@code "IS NULL OR >="}
     * comparator is legal and will be translated in SQL as:
     *
     * {@preformat sql
     *     SELECT column WHERE column IS NULL OR column >= ?
     * }
     *
     * @param comparator The new comparison operator to use.
     */
    public void setComparator(final String comparator) {
        this.comparator = comparator.trim();
    }

    /**
     * Appends a {@code c1=?} condition where {@code c1} is the column name, and {@code =} may
     * be replaced by a more elaborated comparator if {@link #setComparator(String)} has been
     * invoked.
     *
     * @param buffer The buffer in which to write the condition.
     * @param quote  The database-dependent identifier quote.
     * @param type   The type of the SQL query being built.
     */
    final void appendCondition(final StringBuilder buffer, final String quote, final QueryType type) {
        final String variable = column.name;
        String condition = comparator;
        String binaryOp;
        do {
            binaryOp = null;
            int split = 0;
            String fragment = condition;
            for (final String op : BINARY_OP) {
                final int i = fragment.indexOf(op);
                if (i >= 0) {
                    fragment = fragment.substring(0, split=i);
                    binaryOp = op;
                }
            }
            // Reminder: aggregate functions are not allowed in a WHERE clause.
            buffer.append(quote).append(variable).append(quote).append(' ').append(fragment);
            if (binaryOp != null) {
                buffer.append(binaryOp);
                condition = condition.substring(split + binaryOp.length());
            }
        } while (binaryOp != null);
        buffer.append(' ');
        final String function = getFunction(type);
        if (function != null) {
            buffer.append(function);
        } else {
            buffer.append('?');
        }
    }

    /**
     * Returns a string representation of this parameter.
     */
    @Override
    public String toString() {
        return "Parameter[" + column.name + ']';
    }
}
