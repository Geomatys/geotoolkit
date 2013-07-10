/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.sql;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.LinkedHashSet;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests the {@link ScriptRunner} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class ScriptRunnerTest {
    /**
     * The runner that doesn't read any file, but keep trace of what was intended to be
     * read and their order.
     */
    private static final strictfp class Runner extends ScriptRunner {
        /**
         * The list of files that we pretended to read.
         * We use a set to ensure that no file are given twice.
         */
        private final Set<String> files = new LinkedHashSet<>();

        /**
         * Creates a dummy runner.
         */
        Runner() throws SQLException {
            super(null);
            suffixes.add("Tables");
            suffixes.add("Data");
            suffixes.add("FKeys");
        }

        /**
         * Pretends to read a file, but don't really read it.
         */
        @Override
        int runFile(final File file) {
            assertTrue(files.add(file.getName()));
            return 1;
        }

        /**
         * Returns the files that we pretended to read.
         */
        String[] getFiles() {
            return files.toArray(new String[files.size()]);
        }
    }

    /**
     * Tests the simple (and most common) case where there is only one version available.
     *
     * @throws SQLException Should never happen.
     * @throws IOException Should never happen.
     */
    @Test
    public void testSingleVersion() throws SQLException, IOException {
        final Runner runner = new Runner();
        assertEquals(3, runner.run(null, new String[] {
            "EPSG_v6_14.mdb_Data_PostgreSQL.sql",
            "EPSG_v6_14.mdb_FKeys_PostgreSQL.sql",
            "EPSG_v6_14.mdb_Tables_PostgreSQL.sql"
        }));
        assertArrayEquals(new String[] {
            "EPSG_v6_14.mdb_Tables_PostgreSQL.sql",
            "EPSG_v6_14.mdb_Data_PostgreSQL.sql",
            "EPSG_v6_14.mdb_FKeys_PostgreSQL.sql"
        }, runner.getFiles());
    }

    /**
     * Tests the capability of {@link ScriptRunner} to select the most recent
     * version of a set of files.
     *
     * @throws SQLException Should never happen.
     * @throws IOException Should never happen.
     */
    @Test
    public void testMultipleVersion() throws SQLException, IOException {
        final Runner runner = new Runner();
        assertEquals(3, runner.run(null, new String[] {
            "EPSG_v6_14.mdb_Data_PostgreSQL.sql",
            "EPSG_v6_14.mdb_FKeys_PostgreSQL.sql",
            "EPSG_v6_14.mdb_Tables_PostgreSQL.sql",
            "EPSG_v6_18.mdb_Data_PostgreSQL.sql",
            "EPSG_v6_18.mdb_FKeys_PostgreSQL.sql",
            "EPSG_v6_18.mdb_Tables_PostgreSQL.sql"
        }));
        assertArrayEquals(new String[] {
            "EPSG_v6_18.mdb_Tables_PostgreSQL.sql",
            "EPSG_v6_18.mdb_Data_PostgreSQL.sql",
            "EPSG_v6_18.mdb_FKeys_PostgreSQL.sql"
        }, runner.getFiles());
    }
}
