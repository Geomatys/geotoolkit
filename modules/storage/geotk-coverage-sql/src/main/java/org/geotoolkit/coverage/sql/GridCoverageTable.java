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
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.nio.file.Path;
import java.time.Instant;
import javax.measure.IncommensurableException;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;


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
     * @throws SQLException if an error occurred while reading the database.
     *
     * @todo returns a stream instead. Requires to be careful about closing the statement and the connection.
     */
    final List<GridCoverageEntry> find(final String product, final Envelope areaOfInterest)
            throws SQLException, DataStoreException, TransformException
    {
        final Database database   = transaction.database;
        final boolean hasTemporal = CRS.getTemporalComponent(areaOfInterest.getCoordinateReferenceSystem()) != null;
        final Envelope normalized = Envelopes.transform(areaOfInterest, hasTemporal ? database.spatioTemporalCRS : database.extentCRS);
        final Envelope horizontal;
        if (hasTemporal) {
            final GeneralEnvelope ge = GeneralEnvelope.castOrCopy(normalized).subEnvelope(0, 2);
            ge.setCoordinateReferenceSystem(database.extentCRS);                        // Required for handling wrap-around axis.
            horizontal = ge;
        } else {
            horizontal = normalized;
        }
        String sql = "SELECT \"series\", \"filename\", \"grid\","
                   + " \"startTime\", \"endTime\" FROM " + SCHEMA + ".\"" + TABLE + "\""
                   + " INNER JOIN " + SCHEMA + ".\"" + SeriesTable.TABLE + "\" ON (\"series\" = \"" + SeriesTable.TABLE + "\".\"identifier\")"
                   + " INNER JOIN " + SCHEMA + ".\"" + GridGeometryTable.TABLE + "\" ON (\"grid\" = \"" + GridGeometryTable.TABLE + "\".\"identifier\")"
                   + " WHERE \"product\"=? AND ST_Intersects(extent, ST_GeomFromText(?,4326)) AND \"endTime\" >= ? AND \"startTime\" <= ?";
        /*
         * Note: we could write    ("startTime", "endTime") OVERLAPS (?,?)    but our tests
         * with EXPLAIN on PostgreSQL 10.5 suggests that >= and <= make better use of index.
         */
        if (!hasTemporal) {
            sql = sql.substring(0, sql.lastIndexOf(" AND \"endTime\""));        // Stop the query after the intersect condition.
        }

        Geometry poly = JTS.toGeometry(horizontal);
        if (poly.getEnvelopeInternal().getMinX() < 0.0) {
            //hack to compensate postgis uncomplete support for 0-360 geometries intersection
            //if the coverage is not a 0-360, this might slow down the search a little
            //bu the gist tree should be able to minimize the loss.
            Geometry part2 = JTS.transform(poly, new AffineTransform2D(1, 0, 0, 1, 360, 0));
            poly = new GeometryFactory().createMultiPolygon(new Polygon[]{(Polygon) poly, (Polygon) part2});
        }

        final PreparedStatement statement = prepareStatement(sql);
        final DefaultTemporalCRS temporalCRS = transaction.database.temporalCRS;
        final long tMin = temporalCRS.toInstant(normalized.getMinimum(2)).toEpochMilli();
        final long tMax = temporalCRS.toInstant(normalized.getMaximum(2)).toEpochMilli();
        final Calendar calendar = newCalendar();
        statement.setString(1, product);
        statement.setString(2, poly.toText());
        if (hasTemporal) {
            statement.setTimestamp(3, new Timestamp(tMin), calendar);
            statement.setTimestamp(4, new Timestamp(tMax), calendar);
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
     * @throws SQLException if an error occurred while reading from or writing to the database.
     */
    final void add(final String product, final NewRaster raster)
            throws SQLException, IncommensurableException, FactoryException, TransformException, DataStoreException
    {
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
     * Closes the statements used by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        gridGeometries.close();
        seriesTable.close();
    }
}
