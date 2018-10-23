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
import java.util.Date;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.measure.Range;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.coverage.GridSampleDimension;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.Identification;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;


/**
 * Connection to a table of grid coverages. This table builds references in the form of
 * {@link GridCoverageReference} objects, which will defer the image loading until first
 * needed. A {@code GridCoverageTable} can produce a list of available image intercepting
 * a given {@linkplain #setEnvelope2D horizontal area} and {@linkplain #setTimeRange time range}.
 *
 * {@section Implementation note}
 * For proper working of this class, the SQL query must sort entries by end time. If this
 * condition is changed, then {@link GridCoverageReference#equalsAsSQL} must be updated accordingly.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 */
final class GridCoverageTable extends Table {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "GridCoverages";

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

    /**
     * Returns all grid geometries used by the given product.
     */
    public final List<GridGeometryEntry> getGridGeometries(final String product) throws SQLException, CatalogException {
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
    public final List<GridCoverageReference> find(final String product, final Envelope areaOfInterest)
            throws SQLException, CatalogException, TransformException
    {
        final PreparedStatement statement = prepareStatement("SELECT \"series\", \"filename\", \"index\","
                + " \"startTime\", \"endTime\", \"grid\" FROM " + SCHEMA + ".\"" + TABLE + "\""
                + " INNER JOIN " + SCHEMA + ".\"" + SeriesTable.TABLE + "\" ON (\"series\" = \"" + SeriesTable.TABLE + "\".\"identifier\")"
                + " INNER JOIN " + SCHEMA + ".\"" + GridGeometryTable.TABLE + "\" ON (\"grid\" = \"" + GridGeometryTable.TABLE + "\".\"identifier\")"
                + " WHERE \"product\" = ? AND \"startTime\" <= ? AND \"endTime\" > ? AND ST_Intersects(extent, ST_GeomFromText(?,4326))");

        final Envelope normalized = Envelopes.transform(areaOfInterest, transaction.database.spatioTemporalCRS);
        final DefaultTemporalCRS temporalCRS = transaction.database.temporalCRS;
        final long tMin = temporalCRS.toDate(normalized.getMinimum(2)).getTime();
        final long tMax = temporalCRS.toDate(normalized.getMaximum(2)).getTime();
        final Calendar calendar = newCalendar();
        statement.setString   (1, product);
        statement.setTimestamp(2, new Timestamp(tMax), calendar);
        statement.setTimestamp(3, new Timestamp(tMin), calendar);
        statement.setString   (4, Envelopes.toPolygonWKT(GeneralEnvelope.castOrCopy(normalized).subEnvelope(0, 2)));
        final List<GridCoverageReference> entries = new ArrayList<>();
        try (final ResultSet results = statement.executeQuery()) {
            SeriesTable.Entry series = null;
            GridGeometryEntry grid   = null;
            int lastGridID           = 0;
            while (results.next()) {
                final int       seriesID  = results.getInt      (1);
                final String    filename  = results.getString   (2);
                final short     index     = results.getShort    (3);                    // We expect 0 if null.
                final Timestamp startTime = results.getTimestamp(4, calendar);
                final Timestamp endTime   = results.getTimestamp(5, calendar);
                final int       gridID    = results.getInt      (6);
                if (series == null || series.identifier != seriesID) {
                    series = seriesTable.getEntry(seriesID);
                }
                if (grid == null || lastGridID != gridID) {
                    grid = gridGeometries.getEntry(gridID);
                    lastGridID = gridID;
                }
                entries.add(new GridCoverageReference(series, filename, index, startTime, endTime, grid));
            }
        }
        return entries;
    }

    /**
     * Adds a coverage having the specified properties.
     *
     * @param  directory  the path relative to the root directory, or the base URL.
     * @param  filename   the raster filename without its extension.
     * @param  extension  the file extension, or {@code null} or empty if none.
     * @param  imageIndex 1-based index of the image.
     * @return the identifier of a matching entry (never {@code null}).
     * @throws SQLException if an error occurred while reading from or writing to the database.
     *
     * @todo missing {@code "additionalAxes"}.
     */
    private void add(final String product, final String format, String directory, final String filename, final String extension,
            final int imageIndex, final long width, final long height, final Matrix gridToCRS, final int srid,
            final Timestamp startTime, final Timestamp endTime) throws SQLException, IllegalUpdateException
    {
        if (directory == null) {
            directory = ".";
        }
        final int series = seriesTable.findOrCreate(product, directory, extension, format);
        final int grid = gridGeometries.findOrCreate(width, height, gridToCRS, srid, null);
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                    + "\"series\", \"filename\", \"index\", \"startTime\", \"endTime\", \"grid\") VALUES (?,?,?,?,?,?)");

        final Calendar calendar = newCalendar();
        statement.setInt   (1, series);
        statement.setString(2, filename);
        statement.setInt   (3, imageIndex);
        if (startTime != null) statement.setTimestamp(4, startTime, calendar); else statement.setNull(4, Types.TIMESTAMP);
        if (endTime   != null) statement.setTimestamp(5, endTime,   calendar); else statement.setNull(5, Types.TIMESTAMP);
        statement.setInt(6, grid);
        if (statement.executeUpdate() != 1) {
            throw new IllegalUpdateException("Can not add the coverage.");      // Should never happen (paranoiac check).
        }
    }

    /**
     * Adds a coverage having the specified grid geometry.
     *
     * @param  imageIndex 1-based index of the image.
     */
    final void add(final String product, Path path, final GridGeometry geometry,
            final List<GridSampleDimension> bands, final Metadata metadata, final int imageIndex)
            throws SQLException, FactoryException, TransformException, IllegalUpdateException, CatalogException
    {
        if (path.isAbsolute()) {
            path = transaction.database.root.relativize(path);
        }
        String filename  = path.getFileName().toString();
        String extension = null;
        path = path.getParent();
        final int s = filename.lastIndexOf('.');
        if (s > 0) {
            extension = filename.substring(s + 1);
            filename  = filename.substring(0, s);
        }
        final GridExtent extent = geometry.getExtent();
        final DefaultTemporalCRS temporalCRS = transaction.database.temporalCRS;
        long tMin = Long.MIN_VALUE, tMax = Long.MIN_VALUE;
        CoordinateReferenceSystem crs = geometry.getCoordinateReferenceSystem();
        if (CRS.getTemporalComponent(crs) != null) {
            final Envelope time = Envelopes.transform(geometry.getEnvelope(), temporalCRS);
            if (time != null) {
                Date t;
                if ((t = temporalCRS.toDate(time.getMinimum(0))) != null) tMin = t.getTime();
                if ((t = temporalCRS.toDate(time.getMaximum(0))) != null) tMax = t.getTime();
            }
        }
        if (tMax == Long.MIN_VALUE && metadata != null) {
search:     for (Identification info : metadata.getIdentificationInfo()) {
                for (Extent ex : info.getExtents()) {
                    Range<Date> tr = Extents.getTimeRange(ex);
                    if (tr != null) {
                        Date start = tr.getMinValue();
                        Date end   = tr.getMaxValue();
                        if (start != null && end != null) {
                            tMin = start.getTime();
                            tMax = end  .getTime();
                            break search;
                        }
                    }
                }
            }
        }
        if (tMax == Long.MIN_VALUE) {
            throw new IllegalUpdateException("Date not found.");
        }
        final TransformSeparator sep = new TransformSeparator(geometry.getGridToCRS(PixelInCell.CELL_CORNER));
        sep.addSourceDimensionRange(0, 2);
        final Matrix gridToCRS = MathTransforms.getMatrix(sep.separate());
        /*
         * TODO: replace following code by something looking in spatialref table.
         */
        crs = CRS.getHorizontalComponent(crs);
        final IdentifiedObjectFinder finder = IdentifiedObjects.newFinder("EPSG");
        finder.setIgnoringAxes(true);
        final AbstractCRS c = AbstractCRS.castOrCopy((CoordinateReferenceSystem) finder.findSingleton(crs));
        Integer srid = null;
        if (c != null && c.forConvention(AxesConvention.NORMALIZED).equals(crs, ComparisonMode.APPROXIMATIVE)) {
            Identifier id = IdentifiedObjects.getIdentifier(c, Citations.EPSG);
            if (id != null) try {
                srid = Integer.valueOf(id.getCode());
            } catch (NumberFormatException e) {
                throw new IllegalUpdateException("Illegal SRID.");
            }
        }
        if (srid == null) {
            /*
             * Temporary hack for Coriolis data (to be removed in a future version).
             */
            if (IdentifiedObjects.isHeuristicMatchForName(crs, "Mercator_1SP (Unspecified datum based upon the GRS 1980 Authalic Sphere)")) {
                srid = 3395;
            } else {
                throw new IllegalUpdateException("SRID not found.");
            }
        }
        final String format = seriesTable.formats.findOrCreate(product, "NetCDF", bands);
        add(product, format, (path != null) ? path.toString() : ".", filename, extension, imageIndex,
            extent.getSize(0), extent.getSize(1), gridToCRS, srid, new Timestamp(tMin), new Timestamp(tMax));
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
