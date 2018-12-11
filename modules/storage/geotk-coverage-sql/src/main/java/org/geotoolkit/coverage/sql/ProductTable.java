/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.sis.util.ArgumentChecks;


/**
 * Connection to a table of products.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class ProductTable extends CachedTable<String,ProductEntry> {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "Products";

    /**
     * Creates a product table.
     */
    ProductTable(final Transaction transaction) {
        super(Target.PRODUCT, transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"parent\", \"spatialResolution\", \"temporalResolution\", \"metadata\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"name\" = ?";
    }

    /**
     * Creates a product from the current row in the specified result set.
     *
     * @param  results  the result set to read.
     * @param  name     the identifier of the product to create.
     * @return the entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    ProductEntry createEntry(final ResultSet results, final String name) throws SQLException {
        // TODO: handle parent.
        double spatialResolution  = results.getDouble(2); if (results.wasNull()) spatialResolution  = Double.NaN;
        double temporalResolution = results.getDouble(3); if (results.wasNull()) temporalResolution = Double.NaN;
        final String metadata     = results.getString(4);
        return new ProductEntry(transaction.database, name, spatialResolution, temporalResolution, metadata);
    }

    /**
     * Returns all available products.
     */
    public List<ProductEntry> list() throws SQLException {
        final List<ProductEntry> products = new ArrayList<>();
        final StringBuilder sql = new StringBuilder(select());
        sql.setLength(sql.lastIndexOf(" WHERE"));
        sql.insert(sql.lastIndexOf(" FROM"), ", \"name\"");
        try (Statement statement = getConnection().createStatement();
             ResultSet results   = statement.executeQuery(sql.toString()))
        {
            while (results.next()) {
                final String name = results.getString(5);
                products.add(createEntry(results, name));
            }
        }
        return products;
    }

    /**
     * Creates a new product if none exist for the given name.
     *
     * @param  name  the name of the product.
     * @return {@code true} if a new product has been created, or {@code false} if it already exists.
     * @throws SQLException if an error occurred while reading or writing the database.
     */
    public boolean createIfAbsent(final String name) throws SQLException, CatalogException {
        ArgumentChecks.ensureNonNull("name", name);
        try {
            if (getEntry(name) != null) {
                return false;
            }
        } catch (NoSuchRecordException e) {
            // TODO: replace this use of exception by null return value.
        }
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                + "\"name\") VALUES (?)");
        statement.setString(1, name);
        return statement.executeUpdate() != 0;
    }
}
