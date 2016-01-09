/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.util.Set;
import java.util.AbstractSet;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLDataException;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.opengis.referencing.operation.Projection;
import org.opengis.util.NoSuchIdentifierException;

import org.apache.sis.util.logging.Logging;


/**
 * A set of EPSG authority codes. This set requires a living connection to the EPSG database.
 * All {@link #iterator()} method calls create a new {@link ResultSet} holding the codes.
 * However, calls to {@link #contains(Object)} map directly to a SQL call.
 * <p>
 * Serialization of this class stores a copy of all authority codes. The serialization
 * do not preserve any connection to the database.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.2
 * @module
 */
@Deprecated
final class AuthorityCodes extends AbstractSet<String> implements Serializable {
    /**
     * For compatibility with different versions.
     */
    private static final long serialVersionUID = 7105664579449680562L;

    /**
     * The factory which is the owner of this set. One purpose of this field (even if it were not
     * used directly by this class) is to avoid garbage collection of the factory as long as this
     * set is in use. This is required because {@link DirectEpsgFactory#finalize} closes the JDBC
     * connections.
     */
    private final DirectEpsgFactory factory;

    /**
     * The type for this code set. This is translated to the most appropriate
     * interface type even if the user supplied an implementation type.
     */
    final Class<?> type;

    /**
     * {@code true} if {@link #type} is assignable to {@link Projection}.
     */
    private final boolean isProjection;

    /**
     * A view of this set as a map with object's name as values, or {@code null} if none.
     * Will be created only when first needed.
     */
    private transient java.util.Map<String,String> asMap;

    /**
     * The SQL command to use for creating the {@code queryAll} statement.
     * Used for iteration over all codes.
     */
    final String sqlAll;

    /**
     * The SQL command to use for creating the {@code querySingle} statement.
     * Used for fetching the description from a code.
     */
    private final String sqlSingle;

    /**
     * The statement to use for querying a single code.
     * Will be created only when first needed.
     */
    private transient PreparedStatement querySingle;

    /**
     * The connection to the underlying database. This set should never close
     * this connection. Closing it is {@link DirectEpsgFactory}'s job.
     */
    private final Connection connection;

    /**
     * The collection's size, or a negative value if not yet computed. The records will be counted
     * only when first needed. The special value -2 if set by {@link #isEmpty} if the size has not
     * yet been computed, but we know that the set is not empty.
     */
    private int size = -1;

    /**
     * Creates a new set of authority codes for the specified type.
     *
     * @param  connection The connection to the EPSG database.
     * @param  table      The table to query.
     * @param  type       The type to query.
     * @param  factory    The factory originator.
     */
    AuthorityCodes(final Connection connection, final TableInfo table,
                   final Class<?> type, final DirectEpsgFactory factory)
    {
        this.factory    = factory;
        this.connection = connection;
        final StringBuilder buffer = new StringBuilder("SELECT ").append(table.codeColumn);
        if (table.nameColumn != null) {
            buffer.append(", ").append(table.nameColumn);
        }
        buffer.append(" FROM ").append(table.table);
        boolean hasWhere = false;
        Class<?> tableType = table.type;
        if (table.typeColumn != null) {
            for (int i=0; i<table.subTypes.length; i++) {
                final Class<?> candidate = table.subTypes[i];
                if (candidate.isAssignableFrom(type)) {
                    buffer.append(" WHERE (").append(table.typeColumn)
                          .append(" LIKE '").append(table.typeNames[i]).append("%'");
                    hasWhere = true;
                    tableType = candidate;
                    break;
                }
            }
            if (hasWhere) {
                buffer.append(')');
            }
        }
        this.type = tableType;
        isProjection = Projection.class.isAssignableFrom(tableType);
        final int length = buffer.length();
        buffer.append(" ORDER BY ").append(table.codeColumn);
        sqlAll = factory.adaptSQL(buffer.toString());
        buffer.setLength(length);
        buffer.append(hasWhere ? " AND " : " WHERE ").append(table.codeColumn).append(" = ?");
        sqlSingle = factory.adaptSQL(buffer.toString());
    }

    /**
     * Returns a single code, or {@code null} if none.
     */
    private ResultSet getSingle(final Object code) throws SQLException {
        assert Thread.holdsLock(this);
        if (querySingle == null) {
            querySingle = connection.prepareStatement(sqlSingle);
        }
        if (code instanceof Number) {
            querySingle.setInt(1, ((Number) code).intValue());
            return querySingle.executeQuery();
        } else try {
            return DirectEpsgFactory.executeQuery(querySingle, code.toString());
        } catch (NoSuchIdentifierException e) {
            return null;
        }
    }

    /**
     * Returns {@code true} if the code in the specified result set is acceptable.
     * This method handle projections in a special way.
     */
    private boolean isAcceptable(final ResultSet results) throws SQLException {
        if (!isProjection) {
            return true;
        }
        final String code = results.getString(1);
        try {
            synchronized (factory) {
                return factory.isProjection(code);
            }
        } catch (NoSuchIdentifierException e) {
            throw new SQLDataException(e);
        }
    }

    /**
     * Returns {@code true} if the code in the specified code is acceptable.
     * This method handle projections in a special way.
     */
    final boolean isAcceptable(final String code) throws SQLException {
        if (!isProjection) {
            return true;
        }
        try {
            synchronized (factory) {
                return factory.isProjection(code);
            }
        } catch (NoSuchIdentifierException e) {
            throw new SQLDataException(e);
        }
    }

    /**
     * Returns {@code true} if this collection contains no elements.
     * This method fetch at most one row instead of counting all rows.
     */
    @Override
    public synchronized boolean isEmpty() {
        if (size == -1) {
            size = count(true);
            if (size != 0) {
                size = -2; // Remember that we have not fully counted the elements.
            }
        }
        return size == 0;
    }

    /**
     * Counts the number of elements in the underlying result set.
     */
    @Override
    public synchronized int size() {
        if (size < 0) {
            size = count(false);
        }
        return size;
    }

    /**
     * Counts the number of elements in the underlying result set. This method stops after the
     * first record if the {@code first} argument is {@code true}. This method is used only for
     * implementation of {@link #isEmpty()} and {@link #size()}.
     */
    private int count(final boolean first) {
        int count = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet results = stmt.executeQuery(sqlAll))
        {
            while (results.next()) {
                if (isAcceptable(results)) {
                    count++;
                    if (first) break;
                }
            }
        } catch (SQLException exception) {
            unexpectedException(first ? "isEmpty" : "size", exception);
        }
        return count;
    }

    /**
     * Returns {@code true} if this collection contains the specified element.
     */
    @Override
    public synchronized boolean contains(final Object code) {
        boolean exists = false;
        if (code != null) try {
            final ResultSet results = getSingle(code);
            if (results != null) try {
                while (results.next()) {
                    if (isAcceptable(results)) {
                        exists = true;
                        break;
                    }
                }
            } finally {
                results.close();
            }
        } catch (SQLException exception) {
            unexpectedException("contains", exception);
        }
        return exists;
    }

    /**
     * Returns an iterator over the codes. The iterator is backed by a living {@link ResultSet},
     * which will be closed as soon as the iterator reaches the last element.
     */
    @Override
    public synchronized java.util.Iterator<String> iterator() {
        try {
            return new Iterator(connection.createStatement().executeQuery(sqlAll));
        } catch (SQLException exception) {
            unexpectedException("iterator", exception);
            return Collections.<String>emptySet().iterator();
        }
    }

    /**
     * Returns a serializable copy of this set. This method is invoked automatically during
     * serialization. The serialised set of authority code is disconnected from the underlying
     * database.
     */
    protected Object writeReplace() throws ObjectStreamException {
        return new LinkedHashSet<>(this);
    }

    /**
     * Closes the underlying statements. Note: this method is also invoked directly
     * by {@link DirectEpsgFactory#dispose}, which is okay in this particular case since
     * the implementation of this method can be executed an arbitrary amount of times.
     */
    @Override
    protected synchronized void finalize() throws SQLException {
        if (querySingle != null) {
            querySingle.close();
            querySingle = null;
        }
    }

    /**
     * Invoked when an exception occurred. This method just logs a warning.
     */
    private static void unexpectedException(final String method, final SQLException exception) {
        unexpectedException(AuthorityCodes.class, method, exception);
    }

    /**
     * Invoked when an exception occurred. This method just logs a warning.
     */
    static void unexpectedException(final Class<?>     classe,
                                    final String       method,
                                    final SQLException exception)
    {
        Logging.unexpectedException(null, classe, method, exception);
    }

    /**
     * The iterator over the codes. This inner class must kept a reference toward the enclosing
     * {@link AuthorityCodes} in order to prevent a call to {@link AuthorityCodes#finalize()}
     * before the iteration is finished.
     */
    private final class Iterator implements java.util.Iterator<String> {
        /** The result set, or {@code null} if there is no more elements. */
        private ResultSet results;

        /** The next code. */
        private transient String next;

        /** Creates a new iterator for the specified result set. */
        Iterator(final ResultSet results) throws SQLException {
            assert Thread.holdsLock(AuthorityCodes.this);
            this.results = results;
            toNext();
        }

        /** Moves to the next element. */
        private void toNext() throws SQLException {
            while (results.next()) {
                next = results.getString(1);
                if (isAcceptable(next)) {
                    return;
                }
            }
            finalize();
        }

        /** Returns {@code true} if there is more elements. */
        @Override
        public boolean hasNext() {
            return results != null;
        }

        /** Returns the next element. */
        @Override
        public String next() {
            if (results == null) {
                throw new NoSuchElementException();
            }
            final String current = next;
            try {
                toNext();
            } catch (SQLException exception) {
                try {
                    finalize();
                } catch (SQLException e) {
                    exception.addSuppressed(e);
                }
                unexpectedException(Iterator.class, "next", exception);
            }
            return current;
        }

        /** Always throws an exception, since this iterator is read-only. */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Closes the underlying result set. */
        @Override
        protected void finalize() throws SQLException {
            next = null;
            if (results != null) {
                try (Statement owner = results.getStatement()) {
                    results.close();
                    results = null;
                }
            }
        }
    }

    /**
     * Returns a view of this set as a map with object's name as value, or {@code null} if none.
     */
    final java.util.Map<String,String> asMap() {
        if (asMap == null) {
            asMap = new Map();
        }
        return asMap;
    }

    /**
     * A view of {@link AuthorityCodes} as a map, with authority codes as key and
     * object names as values.
     */
    private final class Map extends AbstractMap<String,String> {
        /**
         * Returns the number of key-value mappings in this map.
         */
        @Override
        public int size() {
            return AuthorityCodes.this.size();
        }

        /**
         * Returns {@code true} if this map contains no key-value mappings.
         */
        @Override
        public boolean isEmpty() {
            return AuthorityCodes.this.isEmpty();
        }

        /**
         * Returns the description to which this map maps the specified EPSG code.
         */
        @Override
        public String get(final Object code) {
            String value = null;
            if (code != null) try {
                synchronized (AuthorityCodes.this) {
                    final ResultSet results = getSingle(code);
                    if (results != null) try {
                        while (results.next()) {
                            if (isAcceptable(results)) {
                                value = results.getString(2);
                                break;
                            }
                        }
                    } finally {
                        results.close();
                    }
                }
            } catch (SQLException exception) {
                unexpectedException("get", exception);
            }
            return value;
        }

        /**
         * Returns {@code true} if this map contains a mapping for the specified EPSG code.
         */
        @Override
        public boolean containsKey(final Object key) {
            return contains(key);
        }

        /**
         * Returns a set view of the keys contained in this map.
         */
        @Override
        public Set<String> keySet() {
            return AuthorityCodes.this;
        }

        /**
         * Returns a set view of the mappings contained in this map.
         *
         * @todo Not yet implemented.
         */
        @Override
        public Set<java.util.Map.Entry<String,String>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }
}
