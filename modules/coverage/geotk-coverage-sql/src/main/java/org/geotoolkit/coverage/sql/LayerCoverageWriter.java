/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.CancellationException;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.ImageWriter;

import org.opengis.util.InternationalString;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.operation.MathTransform2D;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.internal.sql.table.ConfigurationKey;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.*;
import static org.geotoolkit.internal.InternalUtilities.firstNonNull;


/**
 * A grid coverage writer for a layer. This class provides a way to write the data using only the
 * {@link GridCoverageWriter} API, with {@linkplain #getOutput() output} of kind {@link Layer}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.21
 *
 * @see CoverageDatabase#createGridCoverageWriter(String)
 * @see Layer#addCoverageReferences(Collection, CoverageDatabaseController)
 *
 * @since 3.20
 * @module
 */
public class LayerCoverageWriter extends GridCoverageWriter {
    /**
     * The default image format to use if it can not be inferred from the existing series.
     */
    private static final String DEFAULT_FORMAT = "PNG";

    /**
     * The default filename prefix to use if no name can be inferred from the coverage.
     */
    private static final String DEFAULT_PREFIX = "IMG";

    /**
     * The coverage database which created this {@code LayerCoverageWriter}.
     */
    protected final CoverageDatabase database;

    /**
     * The {@link ImageCoverageWriter} to use for writing the file.
     * Will be created when first needed.
     */
    private transient GridCoverageWriter writer;

    /**
     * A specialized implementation for the {@link LayerCoverageWriter#writer} field.
     * If the user did not specified explicitely a format, then this encoder will use
     * the format declared in the database rather than trying to guess from the file suffix.
     */
    private static final class Encoder extends ImageCoverageWriter {
        /**
         * The provider for the image writers to be used by {@linkplain #writer}.
         * Will be fetched in same time than {@link #writer}.
         */
        private final ImageWriterSpi writerSpi;

        /**
         * Creates a new encoder which will use the given format for writing images.
         */
        Encoder(final ImageWriterSpi writerSpi) {
            this.writerSpi = writerSpi;
        }

        /**
         * Creates an image writer that claim to be able to encode the given output.
         * The image format will be the one declared in the Series table, unless the
         * user specified explicitely an other format.
         */
        @Override
        protected ImageWriter createImageWriter(final String formatName,
                final Object output, final RenderedImage image) throws IOException
        {
            if (formatName == null && writerSpi.canEncodeImage(image)) {
                return writerSpi.createWriterInstance();
            }
            return super.createImageWriter(formatName, output, image);
        }
    }

    /**
     * The provider for the image readers that can read the images encoded by the writer.
     * Will be fetched in same time than {@link #writer}.
     */
    private transient ImageReaderSpi readerSpi;

    /**
     * The destination directory where to write the images.
     * Will be created in same time than {@link #writer}.
     */
    private transient File directory;

    /**
     * The suffix to append to filename. This string shall contains a leading dot.
     */
    private transient String suffix;

    /**
     * Creates a new writer for the given database. The {@link #setOutput(Object)}
     * method must be invoked before this writer can be used.
     *
     * @param database The database to used with this writer.
     */
    protected LayerCoverageWriter(final CoverageDatabase database) {
        this.database = database;
    }

    /**
     * Creates a new writer for the given database and initializes its layer to the given value.
     *
     * @throws CoverageStoreException Declared for compilation raison, but should never happen.
     */
    LayerCoverageWriter(final CoverageDatabase database, final Future<Layer> layer)
            throws CoverageStoreException
    {
        this(database);
        super.setOutput(layer);
    }

    /**
     * Returns the object to use for formatting error messages.
     */
    private Errors errors() {
        return Errors.getResources(getLocale());
    }

    /**
     * Ensures that the output is set.
     *
     * @throws CoverageStoreException Declared for compilation raison, but should never happen.
     */
    private void ensureOutputSet() throws CoverageStoreException, IllegalStateException {
        if (super.getOutput() == null) { // Use 'super' because we don't want to wait for Future.
            throw new IllegalStateException(errors().getString(Errors.Keys.NO_IMAGE_OUTPUT));
        }
    }

    /**
     * Returns the current layer which is used as output, or {@code null} if none.
     */
    @Override
    public final Layer getOutput() throws CoverageStoreException {
        Object output = super.getOutput();
        if (output instanceof Future<?>) {
            output = ((FutureQuery<?>) output).result();
            super.setOutput(output);
        }
        return (Layer) output;
    }

    /**
     * Sets a new layer as output. The given input can be either a {@link Layer} instance,
     * or the name of a layer as a {@link CharSequence}.
     *
     * @param output The new output as a {@link Layer} instance or a {@link CharSequence},
     *        or {@code null} for removing any output previously set.
     * @throws IllegalArgumentException If the given output is not of a legal type.
     */
    @Override
    public void setOutput(Object output) throws CoverageStoreException {
        if (output != null) {
            if (output instanceof CharSequence) {
                output = database.getLayer(output.toString());
            } else if (!(output instanceof Layer)) {
                throw new IllegalArgumentException(errors().getString(Errors.Keys.ILLEGAL_CLASS_2,
                        output.getClass(), Layer.class));
            }
        }
        clearCache();
        super.setOutput(output);
    }

    /**
     * Returns the name of the given coverage, or {@code null} if none.
     */
    private static String getName(final GridCoverage coverage) throws CoverageStoreException {
        if (coverage instanceof AbstractCoverage) {
            final InternationalString i18n = ((AbstractCoverage) coverage).getName();
            if (i18n != null) {
                String name = i18n.toString();
                if (name != null && !(name = name.trim()).isEmpty()) {
                    return name;
                }
            }
        }
        return null;
    }

    /**
     * Writes a single grid coverage. The default implementation delegates to
     * {@link #write(Iterable, GridCoverageWriteParam)}.
     */
    @Override
    public void write(final GridCoverage coverage, final GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException
    {
        ensureNonNull("coverage", coverage);
        write(Collections.singleton(coverage), param);
    }

    /**
     * Writes one or many grid coverages. This method is a "all of nothing" operation:
     * if an exception occurred while writing an image, then this methods will rollback
     * the database transaction and delete any image files that this method invocation
     * may have created.
     * <p>
     * <b>Notes:</b>
     * <ul>
     *   <li>If the coverage {@linkplain AbstractCoverage#getName() has a name}, the name will
     *       be used as filename. Otherwise a default name will be generated.</li>
     *   <li>If a series already exists or the layer, then the directory and image format of
     *       that series will be used. Otherwise this method will use default values.</li>
     * </ul>
     *
     * @see Layer#addCoverageReferences(Collection, CoverageDatabaseController)
     */
    @Override
    public void write(final Iterable<? extends GridCoverage> coverages, final GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException
    {
        ensureNonNull("coverages", coverages);
        ensureOutputSet();
        /*
         * Infers the image format, file suffix and target directory.
         * The current implementation takes the most frequently used
         * ones by scanning the GridCoverages and Series table.
         */
        final Layer layer = getOutput();
        if (writer == null) {
            directory = firstNonNull(layer.getImageDirectories());
            if (directory == null) {
                directory = new File(database.database.getProperty(ConfigurationKey.ROOT_DIRECTORY), layer.getName());
                if (!directory.isDirectory() && !directory.mkdirs()) {
                    throw new CoverageStoreException(Errors.format(Errors.Keys.CANT_CREATE_DIRECTORY_1, directory));
                }
            }
            String formatName = (param != null) ? param.getFormatName() : null;
            if (formatName == null) {
                formatName = firstNonNull(layer.getImageFormats());
                if (formatName == null) {
                    formatName = DEFAULT_FORMAT;
                }
            }
            final ImageWriterSpi writerSpi;
            writerSpi = XImageIO.getWriterSpiByFormatName(formatName);
            readerSpi = XImageIO.getImageReaderSpi(writerSpi);
            if (readerSpi == null) {
                readerSpi = XImageIO.getReaderSpiByFormatName(formatName);
            }
            suffix = XImageIO.getFileSuffix(writerSpi);
            if (suffix == null) {
                suffix = XImageIO.getFileSuffix(readerSpi);
            }
            writer = new Encoder(writerSpi);
        }
        /*
         * Build a list of image files and write the images immediately. After we have built
         * the full list and written all images, insert the entries in the database.  If any
         * error occurs, we will rollback the database transaction and delete all images that
         * we created.
         */
        final List<Tile> files = new ArrayList<>();
        try {
            for (final GridCoverage coverage : coverages) {
                final File file;
                String filename = getName(coverage);
                if (filename != null) {
                    if (suffix != null) {
                        filename += suffix;
                    }
                    file = new File(directory, filename);
                    if (file.exists()) { // Check must be before to add to the files list.
                        throw new CoverageStoreException(errors().getString(Errors.Keys.FILE_ALREADY_EXISTS_1, file));
                    }
                } else try {
                    // Not really a temporary file, but this method will create a unique filename.
                    file = File.createTempFile(DEFAULT_PREFIX, suffix, directory);
                } catch (IOException e) {
                    throw new CoverageStoreException(errors().getString(Errors.Keys.CANT_WRITE_FILE_1, DEFAULT_PREFIX), e);
                }
                /*
                 * Extract the grid geometry information and store them as a "tile". Even if the
                 * image is not really a tile, this is a convenient way to carry those information.
                 * Write the image, then insert entries in the database only after every images have
                 * been written. In case of failure, the images will be deleted in the catch block.
                 */
                final GridGeometry2D gridGeometry = GridGeometry2D.castOrCopy(coverage.getGridGeometry());
                final MathTransform2D gridToCRS = gridGeometry.getGridToCRS2D();
                if (!(gridToCRS instanceof AffineTransform)) {
                    throw new CoverageStoreException(errors().getString(Errors.Keys.NON_AFFINE_TRANSFORM));
                }
                files.add(new NewGridCoverage(readerSpi, file, gridGeometry, (AffineTransform) gridToCRS));
                writer.setOutput(file);
                writer.write(coverage, param);
            }
            writer.reset();
            layer.addCoverageReferences(files, null);
        } catch (Throwable e) {
            /*
             * Operation failed - delete all image files created by this method call.
             */
            try {
                writer.reset(); // Ensures that the file is closed.
            } catch (Throwable s) {
                e.addSuppressed(s);
            }
            for (final Tile tile : files) {
                final File file = (File) tile.getInput();
                if (!file.delete()) {
                    Logging.getLogger(LayerCoverageWriter.class).warning(errors().getString(Errors.Keys.CANT_DELETE_FILE_1, file));
                }
            }
            throw e;
        }
    }

    /**
     * Clears the cached object. This method needs to be invoked when the output changed,
     * in order to force the calculation of new objects for the new output.
     */
    private void clearCache() {
        writer    = null;
        readerSpi = null;
        directory = null;
        suffix    = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws CoverageStoreException {
        clearCache();
        super.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() throws CoverageStoreException {
        clearCache();
        super.dispose();
    }
}
