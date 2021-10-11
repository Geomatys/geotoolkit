/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.manager.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.sql.DataSource;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LuceneHSQLTreeEltMapper extends LuceneSGBDTreeEltMapper implements TreeElementMapper<NamedEnvelope> {

    public LuceneHSQLTreeEltMapper(final CoordinateReferenceSystem crs, final DataSource source, Path directory) throws SQLException {
        super(crs, source, directory);

    }

    public static TreeElementMapper createTreeEltMapperWithDB(Path directory) throws SQLException, IOException {
        final DataSource dataSource = PGDataSource.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            if (!schemaExist(connection, directory.getFileName().toString())) {
                createSchema(connection, directory.getFileName().toString());
            }
        }
        return new LuceneHSQLTreeEltMapper(SQLRtreeManager.DEFAULT_CRS, dataSource, directory);
    }

    private static void createSchema(Connection connection, String absolutePath) throws SQLException, IOException {
        try {
            ensureNonNull("absolutePath", absolutePath);
            final String schemaName = getSchemaName(absolutePath);
            final InputStream stream = getResourceAsStream("org/geotoolkit/index/tree/create-hsql-treemap-db.sql");
            final ScriptRunner scriptRunner = new ScriptRunner(connection);
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, "UTF-8");
            String sqlQuery = writer.toString();
            sqlQuery = sqlQuery.replaceAll("µSCHEMANAMEµ", schemaName);
            sqlQuery = sqlQuery.replaceAll("µPATHµ", absolutePath);
            scriptRunner.run(sqlQuery);
            scriptRunner.close(false);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unexpected error occurred while trying to create treemap database schema.", ex);
        }
    }

}
