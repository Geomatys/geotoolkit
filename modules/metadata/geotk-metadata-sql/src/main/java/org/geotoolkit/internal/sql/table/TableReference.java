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
package org.geotoolkit.internal.sql.table;

import java.lang.reflect.Constructor;
import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.util.converter.Classes;


/**
 * A reference to a {@link Table}. Instances of this class are synchronized on {@code this}.
 * The current implementation remembers only the last table instance used. For now we don't
 * remember more tables in order to avoid retaining references to too much cached elements.
 * However if we want to do a more sophesticated work in the future, this is the place.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
@ThreadSafe
final class TableReference {
    /**
     * The last table used. May or may not be available depending on the value of the
     * {@link #inUse} flag.
     */
    private Table table;

    /**
     * Whatever {@link #table} is currently in use.
     */
    private boolean inUse;

    /**
     * Creates a new instance.
     */
    TableReference(final Database database, final Class<? extends Table> type) throws NoSuchTableException {
        try {
            final Constructor<? extends Table> c = type.getConstructor(Database.class);
            c.setAccessible(true);
            table = c.newInstance(database);
        } catch (Exception exception) { // Too many exeptions for enumerating them.
            throw new NoSuchTableException(Classes.getShortName(type), exception);
        }
    }

    /**
     * Gets a new table instance.
     */
    public synchronized Table get() {
        Table tb = table;
        if (inUse) {
            tb = tb.clone();
            assert tb.getClass().equals(table.getClass());
            table = tb;
        }
        inUse = true;
        return tb;
    }

    /**
     * Releases the given table instance.
     */
    public synchronized void release(final Table tb) {
        assert tb.getClass().equals(table.getClass());
        table = tb;
        inUse = false;
    }
}
