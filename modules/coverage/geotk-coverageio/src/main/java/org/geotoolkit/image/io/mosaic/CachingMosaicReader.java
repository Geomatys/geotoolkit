/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.util.Map;
import java.util.Iterator;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.internal.image.io.RawFile;
import org.geotoolkit.internal.io.IOUtilities;

import static org.geotoolkit.image.io.mosaic.Tile.LOGGER;


/**
 * A mosaic image reader using the temporary files created by
 * {@link MosaicImageWriter#getImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.01
 * @module
 */
final class CachingMosaicReader extends MosaicImageReader {
    /**
     * The temporary files created for each input tile.
     * This map will not be modified by this class.
     */
    private final Map<Tile,RawFile> temporaryFiles;

    /**
     * The raw image reader.
     */
    private ImageReader reader;

    /**
     * Constructs an image reader using the given cached files. The given map is retained
     * by direct reference - it must not be cloned, because its content will actually be
     * determined a little bit later (but before the read operation begin).
     */
    public CachingMosaicReader(final Map<Tile,RawFile> temporaryFiles, final Object input) {
        this.temporaryFiles = temporaryFiles;
        setInput(input);
    }

    /**
     * Returns the reader to use for reading the given tile,
     * which will use the cached file if possible.
     */
    @Override
    ImageReader getTileReader(final Tile tile) throws IOException {
        final RawFile raw = temporaryFiles.get(tile);
        if (raw == null) {
            return super.getTileReader(tile);
        }
        if (reader == null) {
            final Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("raw");
            while (it.hasNext()) {
                reader = it.next();
                if (!reader.getClass().getName().startsWith("org.geotoolkit.")) {
                    break;
                }
                // The Geotk implementation is oriented toward one-banded values.
                // Prefer an other implementation (typically JAI) if one is found.
            }
            if (reader == null) {
                throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.NO_IMAGE_READER));
            }
        }
        IOUtilities.close(reader.getInput());
        reader.setInput(raw.getImageInputStream());
        return reader;
    }

    /**
     * Disposes this reader.
     */
    @Override
    public void dispose() {
        if (reader != null) {
            try {
                IOUtilities.close(reader.getInput());
            } catch (IOException e) {
                Logging.unexpectedException(LOGGER, Tile.class, "close", e);
            }
            reader.dispose();
            reader = null;
        }
        super.dispose();
    }
}
