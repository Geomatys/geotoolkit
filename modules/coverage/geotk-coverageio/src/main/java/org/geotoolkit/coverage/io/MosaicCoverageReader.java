/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.coverage.io;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileManagerFactory;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.ArgumentChecks.*;


/**
 * An image reader specialized for image mosaics. The {@linkplain #getInput() input} shall be
 * instance of {@link TileManager}. In addition, this coverage reader requires a CRS determined
 * at construction time.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
final class MosaicCoverageReader extends ImageCoverageReader {
    /**
     * The extension to give to the directory which will contain the cached tiles.
     */
    static final String CACHE_EXTENSION = ".tiles";

    /**
     * {@code true} if this reader has been built from a pre-existing cache.
     * This is used only for debugging and testing purpose.
     */
    final boolean cached;

    /**
     * The coordinate reference system of the mosaic.
     */
    private final CoordinateReferenceSystem crs;

    /**
     * The grid geometry, computed when first needed.
     */
    private transient GridGeometry2D gridGeometry;

    /**
     * Creates a mosaic reader using a cache of tiles at different resolutions. Tiles will be
     * created the first time this constructor is invoked for a given input. The tiles will be
     * created in a sub-directory having the same name than the given input, with an additional
     * {@value #CACHE_EXTENSION} extension.
     * <p>
     * This method will fetch the {@linkplain CoordinateReferenceSystem Coordinate Reference System}
     * from a file having the same name than the given {@code input} file but with the {@code ".prj"}
     * suffix.
     *
     * @param  input The input to read.
     * @throws CoverageStoreException If the reader can not be created for the given file.
     */
    public MosaicCoverageReader(final File input) throws CoverageStoreException {
        File directory = input.getParentFile(); // May be null.
        if (directory != null && !directory.isDirectory()) {
            throw new CoverageStoreException(Errors.format(Errors.Keys.NOT_A_DIRECTORY_$1, directory));
        }
        /*
         * Before to perform the more costly operations, try to load the ".prj" file.
         */
        try {
            crs = PrjFiles.read((File) SupportFiles.changeExtension(input, "prj"));
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        /*
         * If a serialized TileManager exists, reuse it.
         */
        directory = new File(directory, input.getName() + CACHE_EXTENSION);
        final File serialized = new File(directory, TileManager.SERIALIZED_FILENAME);
        if (serialized.exists()) {
            TileManager[] managers = null;
            try {
                managers = TileManagerFactory.DEFAULT.create(serialized);
            } catch (Exception e) { // Catch IOException and various RuntimeExceptions.
                // Ignore, we will try to rebuild the manager using the code below.
                // Declare the public CoverageIO.createMosaicReader(...) method in the log record.
                Logging.recoverableException(GridCoverageStore.LOGGER, CoverageIO.class, "createMosaicReader", e);
            }
            if (managers != null && managers.length == 1) {
                setInput(managers[0]);
                cached = true;
                return;
            }
        }
        /*
         * Creates (if it does not already exist) the directory which will contain the tiles.
         */
        final TileWritingPolicy policy;
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new CoverageStoreException(Errors.format(Errors.Keys.NOT_A_DIRECTORY_$1, directory));
            }
            policy = TileWritingPolicy.NO_WRITE;
        } else {
            if (!directory.mkdir()) {
                throw new CoverageStoreException(Errors.format(Errors.Keys.CANT_CREATE_DIRECTORY_$1, directory));
            }
            policy = TileWritingPolicy.OVERWRITE;
        }
        final MosaicBuilder builder = new MosaicBuilder();
        builder.setTileDirectory(directory);
        /*
         * Process to the creation of tiles, and serialize the tile manager for
         * faster access next time.
         */
        final MosaicImageWriteParam params = new MosaicImageWriteParam();
        params.setTileWritingPolicy(policy);
        final TileManager manager;
        try {
            manager = builder.writeFromInput(input, params);
            final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialized));
            out.writeObject(manager);
            out.writeObject(crs);
            out.close();
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        setInput(manager);
        cached = false;
    }

    /**
     * Sets the input, which must be an instance of {@link TileManager}.
     *
     * @param  input The {@code TileManager} to use as input, or {@code null}.
     * @throws IllegalArgumentException if input is not a {@code TileManager} instance.
     * @throws CoverageStoreException if the input can not be set.
     */
    @Override
    public void setInput(final Object input) throws CoverageStoreException {
        ensureCanCast("input", TileManager.class, input);
        super.setInput(input);
    }

    /**
     * Returns the input, or {@code null} if none.
     */
    @Override
    public TileManager getInput() throws CoverageStoreException {
        return (TileManager) super.getInput();
    }

    /**
     * Returns the grid geometry computed from the tile manager.
     */
    @Override
    public GridGeometry2D getGridGeometry(final int index) throws CoverageStoreException {
        /*
         * There is typically only one coverage. If the user asks for an other coverage,
         * delegates to the super-class (which will typically thrown an exception).
         */
        if (index != 0) {
            return super.getGridGeometry(index);
        }
        if (gridGeometry == null) {
            final TileManager input = getInput();
            if (input == null) {
                throw new IllegalStateException(formatErrorMessage(Errors.Keys.NO_IMAGE_INPUT));
            }
            final GridGeometry gg;
            try {
                gg = input.getGridGeometry();
            } catch (IOException e) {
                throw new CoverageStoreException(formatErrorMessage(e), e);
            }
            gridGeometry = (gg == null) ? super.getGridGeometry(index) :
                    new GridGeometry2D(gg.getGridRange(), gg.getGridToCRS(), crs);
        }
        return gridGeometry;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return "MosaicCoverageReader[cached=" + cached + ']';
    }
}
