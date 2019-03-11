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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreReferencingException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.iso.DefaultNameSpace;


/**
 * Connection to a table of products.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 */
final class ProductTable extends CachedTable<String,ProductEntry> {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "Products";

    /**
     * The column name (including quotes) for product name.
     */
    private static final String NAME = "\"name\"";

    /**
     * The column name (including quotes) for product parent.
     */
    private static final String PARENT = "\"parent\"";

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
        return "SELECT " + NAME + ", " + PARENT + ", \"exportedGrid\", \"temporalResolution\", \"metadata\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE " + NAME + " = ?";
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
    ProductEntry createEntry(final ResultSet results, final String name) throws SQLException, DataStoreException {
        final String   parent       = results.getString(2);
        final int      gridID       = results.getInt(3);
        final boolean  hasNoGrid    = results.wasNull();
        final Duration timeRes      = null;                           // TODO results.getString(4);
        final String   metadata     = results.getString(5);
        GridGeometry   exportedGrid = null;
        if (!hasNoGrid) {
            final GridGeometryEntry gridEntry = gridGeometries.getEntry(gridID);
            final double[] timestamps;
            try {
                if (FETCH_ALL_DATES) {
                    timestamps = seriesTable.listAllDates(name, gridGeometries);
                } else {
                    timestamps = ArraysExt.EMPTY_DOUBLE;
                }
                exportedGrid = gridEntry.getGridGeometry(timestamps);
            } catch (TransformException e) {
                throw new DataStoreReferencingException(e);
            }
        }
        final FormatEntry format  = seriesTable.getRepresentativeFormat(name);
        return new ProductEntry(transaction.database, parent, name, exportedGrid, timeRes, format, metadata);
    }

    /**
     * Returns all available products having the given parent as an unmodifiable list.
     */
    final List<ProductEntry> list(final String parent) throws SQLException, DataStoreException {
        final List<ProductEntry> products = new ArrayList<>();
        final StringBuilder sql = new StringBuilder(select());
        final int p = sql.lastIndexOf(NAME);
        sql.replace(p, p + NAME.length(), PARENT);
        try (PreparedStatement statement = getConnection().prepareStatement(sql.toString())) {
            statement.setString(1, parent);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final String name = results.getString(1);
                    products.add(createEntry(results, name));
                }
            }
        }
        return UnmodifiableArrayList.wrap(products.toArray(new ProductEntry[products.size()]));
    }

    /**
     * Returns all available products as an unmodifiable list.
     */
    final List<ProductEntry> list() throws SQLException, DataStoreException {
        final Map<String,ProductEntry> products = new HashMap<>();
        final List<ProductEntry> deferred = new ArrayList<>();
        final StringBuilder sql = new StringBuilder(select());
        sql.setLength(sql.lastIndexOf(" WHERE "));
        try (PreparedStatement statement = getConnection().prepareStatement(sql.toString())) {
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final String name = results.getString(1);
                    final ProductEntry product = createEntry(results, name);
                    if (!product.dispatch(products)) deferred.add(product);
                }
            }
        }
        while (!deferred.isEmpty()) {
            final Iterator<ProductEntry> it = deferred.iterator();
            while (it.hasNext()) {
                final ProductEntry product = it.next();
                if (product.dispatch(products)) it.remove();
            }
        }
        final ProductEntry[] array = products.values().toArray(new ProductEntry[products.size()]);
        for (int i=0; i<array.length; i++) {
            array[i].finish();
        }
        return UnmodifiableArrayList.wrap(array);
    }

    /**
     * Creates a new product if none exist for the given name.
     *
     * @param  name     the name of the product.
     * @param  parent   the parent product, or {@code null} if none.
     * @param  rasters  the rasters from which to fetch a grid geometry if a new entry needs to be created, or {@code null}.
     * @throws Exception if the operation failed (many checked exceptions possible).
     */
    private void createIfAbsent(final AddOption option, final String name, final String parent, final List<NewRaster> rasters) throws Exception {
        if (option != AddOption.NO_CREATE) {
            boolean exists;
            try {
                exists = (getEntry(name) != null);
            } catch (NoSuchRecordException e) {                 // TODO: replace this use of exception by null return value.
                exists = false;
            }
            if (!exists) {
                final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                        + "\"name\", \"parent\", \"exportedGrid\") VALUES (?,?,?) ON CONFLICT (\"name\") DO NOTHING");
                statement.setString(1, name);
                if (parent != null) {
                    statement.setString(2, parent);
                } else {
                    statement.setNull(2, Types.VARCHAR);
                }
                if (rasters != null && !rasters.isEmpty()) {
                    final GridGeometry exportedGrid = rasters.get(0).geometry;                        // TODO: make a better choice.
                    final int gridID = gridGeometries.findOrInsert(exportedGrid, new Instant[2], name);
                    statement.setInt(3, gridID);
                } else {
                    statement.setNull(3, Types.INTEGER);
                }
                if (statement.executeUpdate() != 0) {
                    return;
                }
            }
            if (option == AddOption.CREATE_NEW_PRODUCT) {
                throw new CatalogException("Product \"" + name + "\" already exists.");
            }
        }
    }

    /**
     * Adds entries in the {@code "GridCoverages"} table for this product.
     * This method adds sub-products if the rasters to add have more than one component.
     */
    void addCoverageReferences(String product, final AddOption option, final Map<String,List<NewRaster>> rasters) throws DataStoreException {
        ArgumentChecks.ensureNonNull("product", product);
        try (final GridCoverageTable table = new GridCoverageTable(transaction, seriesTable, gridGeometries)) {
            final String parent;
            if (option != AddOption.CREATE_AS_CHILD_PRODUCT && rasters.size() <= 1) {
                parent = null;
            } else {
                parent = product;
                createIfAbsent(option, parent, null, null);
            }
            for (final Map.Entry<String,List<NewRaster>> entry : rasters.entrySet()) {
                final List<NewRaster> list = entry.getValue();
                if (parent != null) {
                    product = parent + DefaultNameSpace.DEFAULT_SEPARATOR + entry.getKey();
                }
                createIfAbsent(option, product, parent, list);
                for (final NewRaster r : list) {
                    table.add(product, r);
                }
            }
        } catch (DataStoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Deletes the product of the given name.
     *
     * @param  product  name of the product to delete.
     * @return whether the product has been deleted.
     */
    void delete(final String product) throws SQLException {
        final PreparedStatement statement = prepareStatement("DELETE FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"name\"=?");
        statement.setString(1, product);
        statement.executeUpdate();
    }
}
