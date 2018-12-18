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
import java.time.Instant;
import java.time.Duration;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.coverage.grid.GridGeometry;
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
     * Whether the product grid geometry needs to contain the date of all images.
     * This is a potentially costly operation.
     */
    private static final boolean FETCH_ALL_DATES = true;

    /**
     * The table of series.
     */
    private final SeriesTable seriesTable;

    /**
     * The table of grid geometries.
     */
    private final GridGeometryTable gridGeometries;

    /**
     * Creates a product table.
     */
    ProductTable(final Transaction transaction) {
        super(Target.PRODUCT, transaction);
        seriesTable = new SeriesTable(transaction);
        gridGeometries = new GridGeometryTable(transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"parent\", \"exportedGrid\", \"temporalResolution\", \"metadata\""
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
    ProductEntry createEntry(final ResultSet results, final String name) throws SQLException, CatalogException {
        // TODO: handle parent.
        final int gridID            = results.getInt(2);
        Duration temporalResolution = null;  // TODO results.getString(3);
        final String metadata       = results.getString(4);
        GridGeometryEntry gridEntry = gridGeometries.getEntry(gridID);
        GridGeometry exportedGrid;
        try {
            if (FETCH_ALL_DATES) {
                final double[] timestamps = seriesTable.listAllDates(name);
                MathTransform tr = MathTransforms.interpolate(null, timestamps);
                tr = PixelTranslation.translate(tr, PixelInCell.CELL_CENTER, GridGeometryEntry.CELL_ORIGIN);
                exportedGrid = gridEntry.getGridGeometry(timestamps.length, tr);
            } else {
                // TODO: specify startTime and endTime.
                Instant startTime = null;
                Instant endTime = null;
                exportedGrid = gridEntry.getGridGeometry(startTime, endTime);
            }
        } catch (TransformException e) {
            throw new CatalogException(e);
        }
        final FormatEntry format  = seriesTable.getRepresentativeFormat(name);
        return new ProductEntry(transaction.database, name, exportedGrid, temporalResolution, format, metadata);
    }

    /**
     * Returns all available products.
     */
    final List<ProductEntry> list() throws SQLException, CatalogException {
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
     * @param  name     the name of the product.
     * @param  rasters  the rasters from which to fetch a grid geometry if a new entry needs to be created.
     * @return {@code true} if a new product has been created, or {@code false} if it already exists.
     * @throws SQLException if an error occurred while reading or writing the database.
     */
    private boolean createIfAbsent(final String name, final List<NewRaster> rasters)
            throws SQLException, CatalogException, FactoryException, TransformException
    {
        try {
            if (getEntry(name) != null) {
                return false;
            }
        } catch (NoSuchRecordException e) {
            // TODO: replace this use of exception by null return value.
        }
        GridGeometry exportedGrid = rasters.get(0).geometry;                            // TODO: make a better choice.
        final int gridID = gridGeometries.findOrInsert(exportedGrid, new Instant[2], name);
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                + "\"name\", \"exportedGrid\") VALUES (?,?)");
        statement.setString(1, name);
        statement.setInt(2, gridID);
        return statement.executeUpdate() != 0;
    }

    void addCoverageReferences(final String product, final AddOption option, final List<NewRaster> rasters) throws CatalogException {
        ArgumentChecks.ensureNonNull("product", product);
        try {
            if (option != AddOption.NO_CREATE) {
                if (!createIfAbsent(product, rasters) && option == AddOption.CREATE_NEW_PRODUCT) {
                    throw new CatalogException("Product \"" + product + "\" already exists.");
                }
            }
            try (final GridCoverageTable table = new GridCoverageTable(transaction, seriesTable, gridGeometries)) {
                for (final NewRaster r : rasters) {
                    table.add(product, r);
                }
            }
        } catch (SQLException | FactoryException | TransformException e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Deletes the product of the given name.
     *
     * @param  product  the product to delete.
     * @return whether the product has been deleted.
     */
    void delete(final ProductEntry product) throws SQLException {
        final PreparedStatement statement = prepareStatement("DELETE FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"name\"=?");
        statement.setString(1, product.name);
        if (statement.executeUpdate() != 0) {
            removeCached(product.name);
        }
    }
}
