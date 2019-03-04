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

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.internal.referencing.j2d.IntervalRectangle;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.MathTransform2D;


/**
 * Connection to a table of grid coverages. This table builds references in the form of
 * {@link GridCoverageEntry} objects, which will defer the image loading until first
 * needed. A {@code GridCoverageTable} can produce a list of available image intercepting
 * a given {@linkplain #setEnvelope2D horizontal area} and {@linkplain #setTimeRange time range}.
 *
 * {@section Implementation note}
 * For proper working of this class, the SQL query must sort entries by end time. If this
 * condition is changed, then {@link GridCoverageEntry#equalsAsSQL} must be updated accordingly.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Sam Hiatt
 */
final class GridCoverageTable extends Table {
    /**
     * Name of this table in the database.
     */
    static final String TABLE = "GridCoverages";

    /**
     * The table of series.
     */
    private final SeriesTable seriesTable;

    /**
     * The table of grid geometries.
     */
    private final GridGeometryTable gridGeometries;

    /**
     * Constructs a new {@code GridCoverageTable}.
     */
    GridCoverageTable(final Transaction transaction) {
        super(transaction);
        seriesTable = new SeriesTable(transaction);
        gridGeometries = new GridGeometryTable(transaction);
    }

    GridCoverageTable(final Transaction transaction, final SeriesTable seriesTable, final GridGeometryTable gridGeometries) {
        super(transaction);
        this.seriesTable    = seriesTable;
        this.gridGeometries = gridGeometries;
    }

    /**
     * Returns all grid geometries used by the given product.
     */
    final List<GridGeometryEntry> getGridGeometries(final String product) throws SQLException, DataStoreException {
        final List<GridGeometryEntry> geometries = new ArrayList<>();
        final PreparedStatement statement = prepareStatement("SELECT DISTINCT \"grid\" FROM " + SCHEMA + ".\"" + TABLE + "\""
                + " INNER JOIN " + SCHEMA + ".\"" + SeriesTable.TABLE + "\" ON (\"series\" = \"" + SeriesTable.TABLE + "\".\"identifier\")"
                + " WHERE \"product\" = ?");
        statement.setString(1, product);
        try (final ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                final int grid = results.getInt(1);
                if (!results.wasNull()) {
                    geometries.add(gridGeometries.getEntry(grid));
                }
            }
        }
        return geometries;
    }

    /**
     * Returns the two-dimensional coverages that intercept the given envelope.
     *
     * @return areaOfInterest in the current envelope of interest.
     * @throws Exception if the operation failed (many checked exceptions possible).
     *
     * @todo returns a stream instead. Requires to be careful about closing the statement and the connection.
     */
    final List<GridCoverageEntry> find(final String product, final Envelope areaOfInterest) throws Exception {
        String sql = "SELECT \"series\", \"filename\", \"grid\","
                   + " \"startTime\", \"endTime\" FROM " + SCHEMA + ".\"" + TABLE + "\""
                   + " INNER JOIN " + SCHEMA + ".\"" + SeriesTable.TABLE + "\" ON (\"series\" = \"" + SeriesTable.TABLE + "\".\"identifier\")"
                   + " INNER JOIN " + SCHEMA + ".\"" + GridGeometryTable.TABLE + "\" ON (\"grid\" = \"" + GridGeometryTable.TABLE + "\".\"identifier\")"
                   + " WHERE \"product\"=? AND ST_Intersects(extent, ST_GeomFromText(?,4326)) AND \"endTime\" >= ? AND \"startTime\" <= ?";
        /*
         * Note: we could write    ("startTime", "endTime") OVERLAPS (?,?)    but our tests
         * with EXPLAIN on PostgreSQL 10.5 suggests that >= and <= make better use of index.
         */
        final Instant tmin, tmax;
        final CoordinateReferenceSystem crs = areaOfInterest.getCoordinateReferenceSystem();

        // note : we do not use CRS.getTemporalCRS with AxisDirections.indexOfColinear
        // because of forecast axes who are colinear but not temporal
        DefaultTemporalCRS temporalCRS = null;
        int index = 0;
        for (SingleCRS cdt : CRS.getSingleComponents(crs)) {
            if (cdt instanceof TemporalCRS) {
                temporalCRS = (DefaultTemporalCRS) DefaultTemporalCRS.castOrCopy(cdt);
                break;
            }
            index += cdt.getCoordinateSystem().getDimension();
        }

        if (temporalCRS != null) {
            tmin = temporalCRS.toInstant(areaOfInterest.getMinimum(index));     // May be null if the value is NaN.
            tmax = temporalCRS.toInstant(areaOfInterest.getMaximum(index));
        } else {
            tmin = null;
            tmax = null;
        }
        if (tmin == null || tmax == null) {
            sql = sql.substring(0, sql.lastIndexOf(" AND \"endTime\""));        // Stop the query after the intersect condition.
        }
        /*
         * Compute the WKT of the geographic area in the [-180 … +180]° range. It may be a multipolygon
         * if we had to split in two parts an area that cross the anti-meridian.
         */
        final String extentWKT;
        final SingleCRS horizontalCRS = CRS.getHorizontalComponent(crs);
        if (horizontalCRS != null) {
            final int d = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), horizontalCRS.getCoordinateSystem());
            IntervalRectangle area = new IntervalRectangle(areaOfInterest.getMinimum(d), areaOfInterest.getMinimum(d + 1),
                                                           areaOfInterest.getMaximum(d), areaOfInterest.getMaximum(d + 1));
            extentWKT = gridGeometries.geographicAreaWKT(area, (MathTransform2D) MathTransforms.identity(2), horizontalCRS);
        } else {
            throw new CatalogException("Horizontal CRS not identified in " + crs.getName().getCode());
        }
        /*
         * SQL query part.
         */
        final PreparedStatement statement = prepareStatement(sql);
        final Calendar calendar = newCalendar();
        statement.setString(1, product);
        statement.setString(2, extentWKT);
        if (tmin != null && tmax != null) {
            statement.setTimestamp(3, new Timestamp(tmin.toEpochMilli()), calendar);
            statement.setTimestamp(4, new Timestamp(tmax.toEpochMilli()), calendar);
        }
        final List<GridCoverageEntry> entries = new ArrayList<>();
        try (final ResultSet results = statement.executeQuery()) {
            SeriesEntry series = null;
            GridGeometryEntry grid = null;
            int lastGridID = 0;
            while (results.next()) {
                final int       seriesID  = results.getInt      (1);
                final String    filename  = results.getString   (2);
                final int       gridID    = results.getInt      (3);
                final Timestamp startTime = results.getTimestamp(4, calendar);
                final Timestamp endTime   = results.getTimestamp(5, calendar);
                if (series == null || series.identifier != seriesID) {
                    series = seriesTable.getEntry(seriesID);
                }
                if (grid == null || lastGridID != gridID) {
                    grid = gridGeometries.getEntry(gridID);
                    lastGridID = gridID;
                }
                entries.add(new GridCoverageEntry(series, filename, toInstant(startTime), toInstant(endTime), grid));
            }
        }
        return entries;
    }

    /**
     * Adds a coverage having the specified grid geometry.
     *
     * @param  product  name of the product for which to add a raster.
     * @param  raster   information about the raster to add.
     * @throws Exception if the operation failed (many checked exceptions possible).
     */
    final void add(final String product, final NewRaster raster) throws Exception {
        /*
         * Decompose the given path into the directory, filename and extension components.
         */
        Path path = raster.path;
        if (path.isAbsolute()) {
            path = transaction.database.root.relativize(path);
        }
        String filename  = path.getFileName().toString();
        String extension = null;
        final int s = filename.lastIndexOf('.');
        if (s > 0) {
            extension = filename.substring(s + 1);
            filename  = filename.substring(0, s);
        }
        path = path.getParent();
        final String directory = (path != null) ? path.toString() : ".";
        /*
         * Insert dependencies in other tables.
         */
        final Instant[] period = new Instant[2];        // Values to be provided by 'gridGeometries'.
        final int gridID = gridGeometries.findOrInsert(raster.geometry, period, raster.suggestedID(product));
        final int series = seriesTable.findOrInsert(product, directory, extension, raster);
        raster.completeTimeRange(period);
        /*
         * If the "gridToCRS" has NaN scale factor and is mapping pixel corner, then only the lower
         * bounds is set since we can not compute the upper bounds. But for insertion in GridCoverages
         * table, we need both bounds. Set the upper bounds to the same value for now. Future version
         * should use the temporal resolution instead (TODO).
         */
        if (period[1] == null) {
            period[1] = period[0];
        }
        /*
         * Insert the grid coverage entry.
         */
        String sql = "INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                    + "\"series\", \"filename\", \"grid\", \"startTime\", \"endTime\") VALUES (?,?,?,?,?)";
        final PreparedStatement statement = prepareStatement(sql);
        statement.setInt   (1, series);
        statement.setString(2, filename);
        statement.setInt   (3, gridID);
        Calendar calendar = null;
        for (int i=0; i<2; i++) {
            final int column = 4 + i;
            final Instant instant = period[i];
            if (instant != null) {
                if (calendar == null) calendar = newCalendar();
                statement.setTimestamp(column, new Timestamp(instant.toEpochMilli()), calendar);
            } else {
                statement.setNull(column, Types.TIMESTAMP);
            }
        }
        if (statement.executeUpdate() != 1) {
            throw new IllegalUpdateException("Can not add the coverage.");      // Should never happen (paranoiac check).
        }
    }

    /**
     * Removes a specific file from any product.
     * This method removes only the entry in the database; it does not delete the file.
     *
     * @param  raster  path to the raster file to remove from the database.
     */
    final void remove(final Path raster) throws Exception {
        /*
         * The SQL statement to use for deleting entries.  That statement must enumerate the series where the
         * filename appears since the series defines the path. This is necessary for avoiding to delete files
         * of the same name but in another directory.
         */
        StringBuilder sqlDelete = null;
        /*
         * Following separation of suffix from filename must be identical to the one performed in 'addEntries(…).'.
         */
        String filename = raster.getFileName().toString();
        final int s = filename.lastIndexOf('.');
        if (s > 0) filename = filename.substring(0, s);
        /*
         * Collect the list of all series where the file appears.
         */
        final PreparedStatement statement = prepareStatement("SELECT DISTINCT \"series\" FROM "
                + SCHEMA + ".\"" + TABLE + "\" WHERE \"filename\"=?");
        statement.setString(1, filename);
        try (final ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                final int seriesID = results.getInt(1);
                final SeriesEntry series = seriesTable.getEntry(seriesID);
                if (Files.isSameFile(raster, series.path(filename))) {
                    if (sqlDelete == null) {
                        sqlDelete = new StringBuilder("DELETE FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"filename\"='")
                                .append(filename.replace("'", "''")).append("' AND \"series\" IN (");
                    } else {
                        sqlDelete.append(", ");
                    }
                    sqlDelete.append(seriesID);
                }
            }
        }
        /*
         * Actual deletion of coverage entries happen here.
         */
        if (sqlDelete != null) {
            try (Statement stmt = transaction.connection.createStatement()) {
                stmt.executeUpdate(sqlDelete.append(')').toString());
            }
        }
    }

    /**
     * Closes the statements used by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        gridGeometries.close();
        seriesTable.close();
    }
}
