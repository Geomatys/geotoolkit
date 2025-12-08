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
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
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
                createSchema(connection, directory.getFileName().toString(), "org/geotoolkit/index/tree/create-hsql-treemap-db.sql");
            }
        }
        return new LuceneHSQLTreeEltMapper(SQLRtreeManager.DEFAULT_CRS, dataSource, directory);
    }
}
