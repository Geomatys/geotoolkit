/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.net.URL;
import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Collection;
import javax.imageio.ImageReader;

import org.opengis.util.FactoryException;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.SpatialDatabase;


/**
 * A grid coverage table with write capabilities. This class can be used in order to insert new
 * image in the database. Note that adding new records in the {@code "GridCoverages"} table may
 * imply adding new records in dependent tables like {@code "GridGeometries"}. This class may
 * add new records, but will never modify existing records.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 * @version 3.16
 *
 * @since 3.12 (derived from Seagis)
 * @module
 */
final class WritableGridCoverageTable extends GridCoverageTable {
    /**
     * The object to use for writing in the {@code "Tiles"} table. Created only if needed.
     * This is used for adding tiles (as opposed to ordinary images), and is encapsulated
     * in this {@code WritableGridCoverageTable} because we will also need to write an entry
     * in the {@code "GridCoverages"} table for the mosaic as a whole.
     */
    private transient WritableGridCoverageTable tilesTable;

    /**
     * Constructs a new {@code WritableGridCoverageTable}.
     *
     * @param database The connection to the database.
     */
    public WritableGridCoverageTable(final Database database) {
        super(database);
    }

    /**
     * Constructs a new {@code WritableGridCoverageTable} from the specified query.
     */
    private WritableGridCoverageTable(final GridCoverageQuery query) {
        super(query);
    }

    /**
     * Constructs a new {@code WritableGridCoverageTable} with the same initial configuration
     * than the specified table.
     *
     * @param table The table to use as a template.
     */
    private WritableGridCoverageTable(final WritableGridCoverageTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected WritableGridCoverageTable clone() {
        return new WritableGridCoverageTable(this);
    }

    /**
     * Adds entries inferred from the specified image inputs. The inputs shall be any of the
     * following instances:
     * <p>
     * <ul>
     *   <li>{@link File}, {@link URL}, {@link URI} or {@link String} instances.</li>
     *
     *   <li>{@link Tile} instances, which will be added in the {@code "GridCoverages"} table
     *       like any other kind of input processed by this method. For adding tiles in the
     *       {@code "Tiles"} table, use the {@link #addTiles(Collection)} method instead.</li>
     *
     *   <li>{@link ImageReader} instances with the {@linkplain ImageReader#getInput() input}
     *       set and {@linkplain ImageReader#getImageMetadata metadata} conform to the Geotk
     *       {@linkplain SpatialMetadata spatial metadata}. The reader input shall be one of
     *       the above-cited instances. If this is not possible (for example because a
     *       {@link javax.imageio.stream.ImageInputStream} is required), consider wrapping
     *       the {@link javax.imageio.spi.ImageReaderSpi} and the input in a {@link Tile}
     *       instance.</li>
     * </ul>
     * <p>
     * This method will typically not read the full image, but only the required metadata.
     *
     * @param  inputs     The image inputs.
     * @param  listeners  The object which hold the {@link CoverageDatabaseListener}s. While this
     *                    argument is of kind {@link CoverageDatabase}, only the listeners are of
     *                    interest to this method.
     * @param  controller An optional controller to invoke before the listeners, or {@code null}.
     * @return The number of images inserted.
     * @throws SQLException If an error occurred while querying the database.
     * @throws IOException If an I/O operation was required and failed.
     */
    public int addEntries(final Collection<?>              inputs,
                          final CoverageDatabase           listeners,
                          final CoverageDatabaseController controller)
            throws SQLException, IOException, FactoryException, DatabaseVetoException
    {
        int count = 0;
        if (!inputs.isEmpty()) {
            boolean success = false;
            final NewGridCoverageIterator iterator = new NewGridCoverageIterator(listeners,
                    controller, (SpatialDatabase) getDatabase(), inputs);
            final SeriesEntry oldSeries = specificSeries;
            final LocalCache lc = getLocalCache();
            synchronized (lc) {
                transactionBegin(lc);
                try {
                    count = addEntries(lc, iterator);
                    success = true; // Must be the very last line in the try block.
                } finally {
                    specificSeries = oldSeries;
                    transactionEnd(lc, success);
                }
            }
        }
        return count;
    }

    /**
     * Adds entries without the protection provided by the database rollback mechanism.
     * The synchronization, commit or rollback must be performed by the caller.
     *
     * @return The number of images inserted.
     */
    private int addEntries(final LocalCache lc, final NewGridCoverageIterator entries)
            throws SQLException, IOException, FactoryException, DatabaseVetoException
    {
        int count = 0;
        final GridCoverageQuery query       = (GridCoverageQuery) this.query;
        final Database          database    = getDatabase();
        final Calendar          calendar    = getCalendar(lc);
        final LocalCache.Stmt   ce          = getStatement(lc, QueryType.INSERT);
        final PreparedStatement statement   = ce.statement;
        final GridGeometryTable gridTable   = database.getTable(GridGeometryTable.class);
        final FormatTable       formatTable = database.getTable(FormatTable.class);
        final SeriesTable       seriesTable = database.getTable(SeriesTable.class);
        seriesTable.setLayer(getLayer());
        final int bySeries    = indexOf(query.series);
        final int byFilename  = indexOf(query.filename);
        final int byIndex     = indexOf(query.index);
        final int byStartTime = indexOf(query.startTime);
        final int byEndTime   = indexOf(query.endTime);
        final int byExtent    = indexOf(query.spatialExtent);
        final int byDx = (query.dx != null) ? query.dx.indexOf(QueryType.INSERT) : 0;
        final int byDy = (query.dy != null) ? query.dy.indexOf(QueryType.INSERT) : 0;
        final boolean explicitTranslate = (byDx != 0 && byDy != 0);
        NewGridCoverageReference mainEntry;
        while ((mainEntry = entries.next()) != null) {
            /*
             * Notifies the controller (if any), then the listeners (if any) after the
             * NewGridCoverageReference entry has been fully initialized. The controller
             * may change the values. Then creates the format, assuming that every entry
             * will use the same format.
             */
            entries.fireCoverageAdding(true, mainEntry);
            mainEntry.format = formatTable.findOrCreate(mainEntry.format,
                    mainEntry.bestFormat.imageFormat, mainEntry.sampleDimensions);
            /*
             * Gets the metadata of interest. The metadata should contains at least the image
             * envelope and CRS. If it doesn't, then we will use the table envelope as a fall
             * back. It defaults to the whole Earth in WGS 84 geographic coordinates, but the
             * user can set an other value using the setEnvelope(...) method.
             */
            final Rectangle imageBounds = mainEntry.imageBounds;
            final AffineTransform gridToCRS = mainEntry.gridToCRS;
            if (!explicitTranslate && (imageBounds.x != 0 || imageBounds.y != 0)) {
                // If the translation can not be recorded explicitly in the database, then we
                // need to apply it on the affine transform. Note that we really want to update
                // the NewGridCoverageReference field, in order to notify the listeners with an
                // accurate AffineTransform after the change.
                gridToCRS.translate(imageBounds.x, imageBounds.y);
            }
            final int extent = gridTable.findOrCreate(imageBounds.getSize(), gridToCRS,
                    mainEntry.horizontalSRID, mainEntry.verticalValues, mainEntry.verticalSRID);
            /*
             * If the entry is an aggregation, actually inserts the aggregated elements instead
             * than the aggregation. This loop assumes that all aggregated element use the same
             * format and the same extent than the aggregation. This assumption is burned in the
             * NewGridCoverageReference(NewGridCoverageReference, ...) constructor. If this
             * assumption doesn't hold anymore in a future version, then the calculation of
             * 'entry.format' and 'extent' above need to move inside the loop.
             */
            for (final NewGridCoverageReference entry : entries.aggregation(mainEntry)) {
                /*
                 * Create the series if it does not exist. Note that new series
                 * may be created if the entries are in different directories.
                 */
                final String directory = (entry.path != null) ? entry.path.toString() : "";
                final int seriesID = seriesTable.findOrCreate(directory, entry.extension, entry.format);
                specificSeries = seriesTable.getEntry(seriesID);
                /*
                 * Adds the entries for each image found in the file.
                 * There is often only one image per file, but not always.
                 */
                statement.setInt   (bySeries,   specificSeries.getIdentifier());
                statement.setString(byFilename, entry.filename);
                statement.setInt   (byExtent,   extent);
                if (explicitTranslate) {
                    final Rectangle translate = entry.imageBounds;
                    statement.setInt(byDx, translate.x); // NOSONAR: byDx can not be zero.
                    statement.setInt(byDy, translate.y); // NOSONAR: byDy can not be zero.
                }
                final DateRange[] dateRanges = entry.dateRanges;
                int imageIndex = entry.imageIndex;
                if (dateRanges == null) {
                    statement.setInt (byIndex,     ++imageIndex);
                    statement.setNull(byStartTime, Types.TIMESTAMP);
                    statement.setNull(byEndTime,   Types.TIMESTAMP);
                    if (updateSingleton(statement)) count++;
                } else for (final DateRange dateRange : dateRanges) {
                    final Date startTime = dateRange.getMinValue();
                    final Date   endTime = dateRange.getMaxValue();
                    statement.setInt      (byIndex,     ++imageIndex);
                    statement.setTimestamp(byStartTime, new Timestamp(startTime.getTime()), calendar);
                    statement.setTimestamp(byEndTime,   new Timestamp(endTime  .getTime()), calendar);
                    if (updateSingleton(statement)) count++;
                }
            }
            /*
             * Notifies the listeners that the entries have been added.
             */
            entries.fireCoverageAdding(false, mainEntry);
        }
        seriesTable.release();
        formatTable.release();
        gridTable.release();
        return count;
    }

    /**
     * Adds the specified tiles in the {@code "Tiles"} table.
     *
     * @param  tiles The tiles to insert.
     * @param  listeners The object which hold the {@link CoverageDatabaseListener}s. While this
     *         argument is of kind {@link CoverageDatabase}, only the listeners are of interest
     *         to this method.
     * @param  controller An optional controller to invoke before the listeners, or {@code null}.
     * @throws SQLException If an error occurred while querying the database.
     * @throws IOException If an I/O operation was required and failed.
     */
    public void addTiles(final Collection<Tile>           tiles,
                         final CoverageDatabase           listeners,
                         final CoverageDatabaseController controller)
            throws SQLException, IOException, FactoryException, DatabaseVetoException
    {
        if (tilesTable == null) {
            // Uses the special GridCoverageQuery constructor for insertions in "Tiles" table.
            tilesTable = new WritableGridCoverageTable(new GridCoverageQuery((SpatialDatabase) getDatabase(), true));
        }
        tilesTable.setLayer(getLayer());
        tilesTable.specificSeries = specificSeries;
        tilesTable.addEntries(tiles, listeners, controller);
    }
}
