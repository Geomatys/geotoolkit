/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.storage.coverage;

import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.storage.tiling.Tile;
import org.geotoolkit.image.io.XImageIO;

/**
 * Expose informations on how to access a tile.
 * <p>
 * TODO : should be a parent of {@link org.geotoolkit.image.io.mosaic.Tile}.
 *
 * @author Johann Sorel (Geomatys)
 * @deprecated use Tile.getResource to obtain a GridCoverageResource instead
 */
@Deprecated
public interface ImageTile extends Tile {

    /**
     * Default implementation check if input is an image.
     * If not a reader is used to read the image.
     *
     * @return RenderedImage
     * @throws java.io.IOException
     */
    default RenderedImage getImage() throws IOException {
        final Object input = getInput();
        RenderedImage tileImage = null;
        if (input instanceof RenderedImage) {
            tileImage = (RenderedImage) input;
        } else {
            ImageReader reader = null;
            try {
                reader    = getImageReader();
                tileImage = reader.read(getImageIndex());
            } catch (IOException ex) {
                throw new IOException("Failed to read tile : "+ input +"\n"+ex.getMessage(), ex);
            } finally {
                XImageIO.disposeSilently(reader);
            }
        }
        return tileImage;
    }

    /**
     * Returns a new reader created by the {@linkplain #getImageReaderSpi provider} and setup for
     * reading the image from the {@linkplain #getInput input}. This method returns a new reader
     * on each invocation.
     * <p>
     * It is the user's responsibility to close the {@linkplain ImageReader#getInput reader input}
     * after usage and {@linkplain ImageReader#dispose() dispose} the reader.
     *
     * @return An image reader with its {@linkplain ImageReader#getInput input} set.
     * @throws IOException if the image reader can't be initialized.
     */
    ImageReader getImageReader() throws IOException;

    /**
     * Returns the image reader provider (never {@code null}). This is the provider used for
     * creating the {@linkplain ImageReader image reader} to be used for reading this tile.
     *
     * @return The image reader provider.
     *
     * @see ImageReaderSpi#createReaderInstance()
     */
    ImageReaderSpi getImageReaderSpi();

    /**
     * Returns the input to be given to the image reader for reading this tile.
     *
     * @return The image input.
     *
     * @see ImageReader#setInput
     */
    Object getInput();

    /**
     * Returns the image index to be given to the image reader for reading this tile.
     *
     * @return The image index, numbered from 0.
     *
     * @see ImageReader#read(int)
     */
    int getImageIndex();

}
