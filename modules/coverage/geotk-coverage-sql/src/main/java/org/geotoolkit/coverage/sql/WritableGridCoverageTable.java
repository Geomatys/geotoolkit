/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageReader;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.internal.sql.table.NoSuchRecordException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.io.IOUtilities;


/**
 * A grid coverage table with write capabilities. This class can be used in order to insert new
 * image in the database. Note that adding new records in the {@code "GridCoverages"} table may
 * imply adding new records in dependent tables like {@code "GridGeometries"}. This class may
 * add new records, but will never modify existing records.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 * @version 3.12
 *
 * @since 3.12 (derived from Seagis)
 * @module
 */
final class WritableGridCoverageTable extends GridCoverageTable {
    /**
     * {@code true} if {@code WritableGridCoverageTable} is allowed to insert new entries in
     * existing series which seems a good match, or {@code false} to be strict. The default
     * value is {@code false}.
     * <p>
     * This flag can be set to {@code true} if the information declared in the database have
     * been edited manually in such a way that the strict mode would never use that series.
     * For example the series may have been given a more specialized image format than the
     * one automatically detected, or may locate the files in a different directory because
     * the directory structure is different on the server than on the desktop running the
     * {@code WritableGridCoverageTable} code.
     */
    boolean lenientSeries;

    /**
     * The object to use for writting in the {@code "Tiles"} table. Created only if needed.
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
     * Sets the series in which to insert the entries. It should be an existing series in the
     * currently selected layer.
     * <p>
     * This method shall be invoked only when used with {@link WritableGridCoverageTable}.
     *
     * @param  identifier The series identifier.
     * @throws SQLException If the database access failed for an other reason.
     */
    final void setSeries(final int identifier) throws SQLException {
        final LayerEntry layer = getLayerEntry(false);
        if (layer == null) {
            final SeriesTable table = getDatabase().getTable(SeriesTable.class);
            specificSeries = table.getEntry(identifier);
            table.release();
            // Do not set the layer since it still null for SeriesEntry created that way.
        } else {
            specificSeries = layer.getSeries(identifier);
            if (specificSeries == null) {
                throw new NoSuchRecordException(errors().getString(
                        Errors.Keys.ILLEGAL_ARGUMENT_$2, "identifier", identifier));
            }
        }
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
     * @param  imageIndex The index of the image to insert in the database. This argument is
     *                    ignored for {@link Tile} inputs, because they provide their own
     *                    {@link Tile#getImageIndex()} method.
     * @param  listeners  The object which hold the {@link CoverageDatabaseListener}s. While this
     *                    argument is of kind {@link CoverageDatabase}, only the listeners are of
     *                    interest to this method.
     * @param  controller An optional controller to invoke before the listeners, or {@code null}.
     * @return The number of images inserted.
     * @throws SQLException If an error occured while querying the database.
     * @throws IOException If an I/O operation was required and failed.
     */
    public int addEntries(final Collection<?>              inputs,
                          final int                        imageIndex,
                          final CoverageDatabase           listeners,
                          final CoverageDatabaseController controller)
            throws SQLException, IOException, FactoryException, DatabaseVetoException
    {
        int count = 0;
        final Iterator<?> it = inputs.iterator();
        if (it.hasNext()) {
            final Object next = it.next();
            boolean success = false;
            final NewGridCoverageIterator iterator = new NewGridCoverageIterator(listeners,
                    controller, (SpatialDatabase) getDatabase(), null, imageIndex, it, next);
            final SeriesEntry oldSeries = specificSeries;
            synchronized (getLock()) {
                transactionBegin();
                try {
                    count = addEntries(iterator);
                    success = true; // Must be the very last line in the try block.
                } finally {
                    specificSeries = oldSeries;
                    transactionEnd(success);
                }
            }
        }
        return count;
    }

    /**
     * Adds entries without the protection provided by the database rollback mechanism.
     * The synchronisation, commit or rollback must be performed by the caller.
     *
     * @return The number of images inserted.
     */
    private int addEntries(final NewGridCoverageIterator entries)
            throws SQLException, IOException, FactoryException, DatabaseVetoException
    {
        int count = 0;
        final GridCoverageQuery query     = (GridCoverageQuery) this.query;
        final Calendar          calendar  = getCalendar();
        final LocalCache.Stmt   ce        = getStatement(QueryType.INSERT);
        final PreparedStatement statement = ce.statement;
        final GridGeometryTable gridTable = getDatabase().getTable(GridGeometryTable.class);
        final int bySeries    = indexOf(query.series);
        final int byFilename  = indexOf(query.filename);
        final int byIndex     = indexOf(query.index);
        final int byStartTime = indexOf(query.startTime);
        final int byEndTime   = indexOf(query.endTime);
        final int byExtent    = indexOf(query.spatialExtent);
        final int byDx = (query.dx != null) ? query.dx.indexOf(QueryType.INSERT) : 0;
        final int byDy = (query.dy != null) ? query.dy.indexOf(QueryType.INSERT) : 0;
        final boolean explicitTranslate = (byDx != 0 && byDy != 0);
        while (entries.hasNext()) {
            final NewGridCoverageReference entry;
            try {
                entry = entries.next();
            } catch (BackingStoreException exception) {
                final Throwable cause = exception.getCause();
                if (cause instanceof FactoryException) {
                    throw (FactoryException) cause;
                }
                throw exception.unwrapOrRethrow(IOException.class);
            }
            /*
             * If no destination series were explicitly specified, and if we are allowed to
             * "guess" the series, perform the guess now. We do that before to notify the
             * controller because the guess performed here depends only on information that
             * the controller can not modify.
             */
            if (entry.series == null && lenientSeries) {
                final LayerEntry layer = getLayerEntry(true);
                final Collection<SeriesEntry> candidates = layer.getSeries();
                if (!candidates.isEmpty()) {
                    entry.selectSeries(candidates);
                }
            }
            /*
             * Notifies the controller (if any), then the listeners (if any) after the
             * NewGridCoverageReference entry has been fully initialized. The controller
             * may change the values. Then create the series if it does not exists.
             */
            entries.fireCoverageAdding(true, entry);
            if (entry.series == null) {
                final SeriesTable table = getDatabase().getTable(SeriesTable.class);
                table.setLayer(getLayer());
                final String path = (entry.path != null) ? entry.path.getPath() : "";
                final int id = table.findOrCreate(path, entry.extension, entry.format);
                entry.series = table.getEntry(id);
                table.release();
            }
            specificSeries = entry.series;
            /*
             * Gets the metadata of interest. The metadata should contains at least the image
             * envelope and CRS. If it doesn't, then we will use the table envelope as a fall
             * back. It defaults to the whole Earth in WGS 84 geographic coordinates, but the
             * user can set an other value using {@link #setEnvelope}.
             */
            final Rectangle imageBounds = entry.imageBounds;
            final AffineTransform gridToCRS = entry.gridToCRS;
            if (!explicitTranslate && (imageBounds.x != 0 || imageBounds.y != 0)) {
                // If the translation can not be recorded explicitly in the database, then we
                // need to apply it on the affine transform. Note that we really want to update
                // the NewGridCoverageReference field, in order to notify the listeners with an
                // accurate AffineTransform after the change.
                gridToCRS.translate(imageBounds.x, imageBounds.y);
            }
            final int extent = gridTable.findOrCreate(imageBounds.getSize(), gridToCRS,
                    entry.horizontalSRID, entry.verticalValues, entry.verticalSRID);
            /*
             * Adds the entries for each image found in the file.
             * There is often only one image per file, but not always.
             */
            statement.setInt   (bySeries,   specificSeries.getIdentifier());
            statement.setString(byFilename, entry.filename);
            statement.setInt   (byExtent,   extent);
            if (explicitTranslate) {
                final Rectangle translate = entry.imageBounds;
                statement.setInt(byDx, translate.x);
                statement.setInt(byDy, translate.y);
            }
            final DateRange[] dateRanges = entry.dateRanges;
            if (dateRanges == null) {
                statement.setInt (byIndex,     1);
                statement.setNull(byStartTime, Types.TIMESTAMP);
                statement.setNull(byEndTime,   Types.TIMESTAMP);
                if (updateSingleton(statement)) count++;
            } else for (int i=0; i<dateRanges.length; i++) {
                final Date startTime = dateRanges[i].getMinValue();
                final Date   endTime = dateRanges[i].getMaxValue();
                statement.setInt      (byIndex,     i + 1);
                statement.setTimestamp(byStartTime, new Timestamp(startTime.getTime()), calendar);
                statement.setTimestamp(byEndTime,   new Timestamp(endTime  .getTime()), calendar);
                if (updateSingleton(statement)) count++;
            }
            /*
             * Notifies the listeners that the entries have been added.
             */
            entries.fireCoverageAdding(false, entry);
        }
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
     * @throws SQLException If an error occured while querying the database.
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
        tilesTable.addEntries(tiles, 0, listeners, controller);
    }

    /**
     * Searchs for new files in the {@linkplain #getLayer current layer} and {@linkplain #addEntries
     * adds} them to the database. The {@link #setLayer(Layer) setLayer} method must be invoked prior
     * this method. This method will process every {@linkplain Series series} for the current layer.
     *
     * @param  includeSubdirectories If {@code true}, then sub-directories will be included
     *         in the scan. New series may be created if subdirectories are found.
     * @param  policy The action to take for existing entries.
     * @param  listeners The object which hold the {@link CoverageDatabaseListener}s. While this
     *         argument is of kind {@link CoverageDatabase}, only the listeners are of interest
     *         to this method.
     * @param  controller An optional controller to invoke before the listeners, or {@code null}.
     * @return The number of images inserted.
     * @throws SQLException If an error occured while querying the database.
     * @throws IOException If an I/O operation was required and failed.
     */
    public int updateLayer(final boolean                    includeSubdirectories,
                           final UpdatePolicy               policy,
                           final CoverageDatabase           listeners,
                           final CoverageDatabaseController controller)
            throws SQLException, IOException, FactoryException, CoverageStoreException
    {
        final boolean replaceExisting = !UpdatePolicy.SKIP_EXISTING.equals(policy);
        final LayerEntry layer = getLayerEntry(true);
        Set<GridCoverageReference> coverages = null;
        final Map<Object,SeriesEntry> inputs = new LinkedHashMap<Object,SeriesEntry>();
        for (final SeriesEntry series : layer.getSeries()) {
            /*
             * The inputs map will contains File or URI objects. If the protocol is "file",
             * we will scan the directory and put File objects in the map. Otherwise and if
             * the user asked for the replacement of existing file, we just copy the set of
             * existing URI. Otherwise we do nothing since we can't get the list of new items.
             */
            if (series.protocol.equalsIgnoreCase(SeriesEntry.FILE_PROTOCOL)) {
                File directory = series.file("*");
                final String filename = directory.getName();
                final int split = filename.lastIndexOf('*');
                final String extension = (split >= 0) ? filename.substring(split + 1) : "";
                final FileFilter filter = new FileFilter() {
                    @Override public boolean accept(final File file) {
                        if (file.isDirectory()) {
                            return includeSubdirectories;
                        }
                        return file.getName().endsWith(extension);
                    }
                };
                directory = directory.getParentFile();
                if (directory != null) {
                    final File[] list = directory.listFiles(filter);
                    if (list != null) {
                        addFiles(inputs, list, filter, series);
                        continue;
                    }
                }
                throw new FileNotFoundException(errors().getString(
                        Errors.Keys.NOT_A_DIRECTORY_$1, directory.getPath()));
            } else if (replaceExisting) {
                if (coverages == null) {
                    coverages = layer.getCoverageReferences(null);
                }
                for (final GridCoverageReference coverage : coverages) {
                    if (series.equals(((GridCoverageEntry) coverage).getIdentifier().series)) {
                        inputs.put(coverage.getFile(URI.class), series);
                    }
                }
            }
        }
        /*
         * We now have a list of every files found in the directories. Now remove the files that
         * are already present in the database. We perform this removal here instead than during
         * the directories scan in order to make sure that we don't query the database twice for
         * the same files (our usage of hash map ensures this condition).
         */
        int count = 0;
        boolean success = false;
        final SeriesEntry oldSeries = specificSeries;
        transactionBegin();
        try {
            if (UpdatePolicy.CLEAR_BEFORE_UPDATE.equals(policy)) {
                deleteAll();
            } else for (final Iterator<Map.Entry<Object,SeriesEntry>> it=inputs.entrySet().iterator(); it.hasNext();) {
                final Map.Entry<Object,SeriesEntry> entry = it.next();
                final Object input = IOUtilities.tryToFile(entry.getKey());
                if (input instanceof File) {
                    String filename = ((File) input).getName();
                    final int split = filename.lastIndexOf('.');
                    if (split >= 0) {
                        filename = filename.substring(0, split);
                    }
                    specificSeries = entry.getValue();
                    if (replaceExisting) {
                        delete(filename);
                    } else if (exists(filename)) {
                        it.remove();
                    }
                }
            }
            /*
             * Now process to the insertions in the database.
             *
             * TODO: We need to decide what to do with new series (i.e. when specificSeries == null).
             *       In current state of affairs, we will get a NullPointerException.
             */
            Iterator<Map.Entry<Object,SeriesEntry>> it;
            while ((it = inputs.entrySet().iterator()).hasNext()) {
                final Map.Entry<Object,SeriesEntry> entry = it.next();
                specificSeries = entry.getValue();
                final Object next = entry.getKey();
                it.remove();
                final int imageIndex = 0; // TODO: Do we have a better value to provide?
                final NewGridCoverageIterator iterator = new NewGridCoverageIterator(
                        listeners, controller, (SpatialDatabase) getDatabase(),
                        specificSeries, imageIndex, it, next);
                count += addEntries(iterator);
            }
            success = true; // Must be the very last line in the try block.
        } finally {
            specificSeries = oldSeries;
            transactionEnd(success);
        }
        return count;
    }

    /**
     * Adds the {@code toAdd} files or directories to the specified map. This
     * method invokes itself recursively in order to scan for subdirectories.
     *
     * @param  files  The map in which to add the files.
     * @param  toAdd  The files or directories to add. This list will be sorted in place.
     * @param  filter The filename filter, or {@code null} for including all files.
     * @param  series The series to use as value in the map.
     */
    private static void addFiles(final Map<Object,SeriesEntry> files, final File[] toAdd,
                                 final FileFilter filter, final SeriesEntry series)
    {
        Arrays.sort(toAdd);
        for (final File file : toAdd) {
            final File[] list = file.listFiles(filter);
            if (list != null) {
                // If scanning sub-directories, invokes this method recursively but without
                // assigning series, since we will need to create a new series entry.
                addFiles(files, list, filter, null);
            } else {
                final SeriesEntry old = files.put(file, series);
                if (old != null) {
                    // If this filename was already assigned to a series, keep the old entry.
                    // It is more likely to occurs if we are scanning sub-directories while
                    // one of those sub-directories is already used by an existing series.
                    files.put(file, old);
                }
            }
        }
    }
}
