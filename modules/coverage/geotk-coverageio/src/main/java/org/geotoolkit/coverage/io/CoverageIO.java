/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.net.URL;
import java.io.File;
import java.io.IOException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.ArgumentChecks.*;


/**
 * Convenience methods for creating {@link GridCoverageReader} or {@link GridCoverageWriter}
 * instances from given inputs. This class is equivalent to the {@link javax.imageio.ImageIO}
 * and {@link org.geotoolkit.image.io.XImageIO} classes, but applied to coverages.
 *
 * {@section Readers}
 * In the simplest case, this method just creates an {@link ImageCoverageReader} instance with
 * the input set to the given object (typically a {@link File} or {@link URL}). However if the
 * image is very large and is not encoded in a format that support natively tiling, it may be
 * more efficient to create a mosaic of tiles first. The {@link #createMosaicReader(File)}
 * method is provided for this purpose.
 *
 * {@section Writers}
 * To be done in a future Geotk release.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
@Static
public final class CoverageIO {
    /**
     * Do not allow instantiation of this class.
     */
    private CoverageIO() {
    }

    /**
     * Creates a simple reader which does not use any pyramid or mosaic tiling.
     * This reader is appropriate if the image is known to be small.
     *
     * @param  input The input to read (typically a {@link File}).
     * @return A coverage reader for the given input.
     * @throws CoverageStoreException If the reader can not be created for the given file.
     */
    public static GridCoverageReader createSimpleReader(final Object input) throws CoverageStoreException {
        ensureNonNull("input", input);
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(input);
        return reader;
    }

    /**
     * Creates a mosaic reader using a cache of tiles at different resolutions. Tiles will be
     * created the first time this method is invoked for a given input. The tiles creation time
     * depends on the available memory, the image size and its format. The creation time can
     * range from a few seconds to several minutes or even hours if the given image is very large.
     * <p>
     * The tiles will be created in a sub-directory having the same name than the given input,
     * with an additional {@code ".tiles"} extension.
     *
     * @param  input The input to read.
     * @return A coverage reader for the given file.
     * @throws CoverageStoreException If the reader can not be created for the given file.
     */
    public static GridCoverageReader createMosaicReader(final File input) throws CoverageStoreException {
        ensureNonNull("input", input);
        File directory = input.getParentFile(); // May be null.
        if (directory != null && !directory.isDirectory()) {
            throw new CoverageStoreException(Errors.format(Errors.Keys.NOT_A_DIRECTORY_$1, directory));
        }
        final TileWritingPolicy policy;
        directory = new File(directory, input.getName() + ".tiles");
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

        final MosaicImageWriteParam params = new MosaicImageWriteParam();
        params.setTileWritingPolicy(policy);
        final TileManager manager;
        try {
            manager = builder.writeFromInput(input, params);
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(manager);
        return createSimpleReader(reader);
    }
}
