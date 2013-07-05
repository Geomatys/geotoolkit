/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import org.apache.sis.util.Classes;


/**
 * Pool of tables. The length of the array given to the constructor is the maximal
 * number of tables to be cached.
 *
 * {@section Example}
 * {@preformat java
 *     TablePool<LayerTable> pool = ...
 *     LayerTable table = pool.acquire();
 *     LayerEntry entry = table.getEntry(id);
 *     pool.release(table);
 * }
 *
 * @param  <T> The type of tables to be stored in the pool.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
public final class TablePool<T extends Table> {
    /**
     * The database to use for creating new tables.
     */
    private final Database database;

    /**
     * Kind of tables in the pool.
     */
    private final Class<T> type;

    /**
     * Tables available for use. The length of this array is the pool capacity.
     */
    private final T[] tables;

    /**
     * Number of valid entries in the {@link #tables} array.
     */
    private int count;

    /**
     * Creates a new pool of tables.
     *
     * @param database The database to use for creating new tables.
     * @param type     Kind of tables in the pool.
     * @param tables   An initially empty array to be used for storing the tables.
     *                 The length of this array is the pool capacity.
     */
    public TablePool(final Database database, final Class<T> type, final T[] tables) {
        this.database = database;
        this.type     = type;
        this.tables   = tables;
    }

    /**
     * Returns a table from the pool if possible, or creates a new table if the pool is empty.
     *
     * @return A table available or use.
     * @throws NoSuchTableException If the table can not be created.
     */
    public T acquire() throws NoSuchTableException {
        synchronized (this) {
            int n = count;
            if (n != 0) {
                count = --n;
                final T table = tables[n];
                tables[n] = null;
                return table;
            }
        }
        return database.getTable(type);
    }

    /**
     * Returns the given table to the pool. The table is discarded if the pool is full.
     *
     * @param table The table to give back to the pool.
     */
    public synchronized void release(final T table) {
        assert table.getDatabase() == database;
        if (count != tables.length) {
            tables[count++] = table;
        }
    }

    /**
     * Clears the pool. Used for flushing the cache.
     */
    public synchronized void clear() {
        Arrays.fill(tables, null);
        count = 0;
    }

    /**
     * Returns a string representation of this pool for debugging purpose.
     */
    @Override
    public synchronized String toString() {
        return "TablePool<" + Classes.getShortClassName(type) + ">(" + count + ')';
    }
}
