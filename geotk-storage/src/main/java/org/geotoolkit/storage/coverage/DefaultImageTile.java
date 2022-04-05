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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.AbstractResource;
import org.apache.sis.storage.tiling.TileStatus;

/**
 * Default implementation of a TileReference
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultImageTile extends AbstractResource implements ImageTile{

    protected final ImageReaderSpi spi;
    protected final Object input;
    protected final int imageIndex;
    protected final long[] position;


    public DefaultImageTile(RenderedImage image, long... position) {
        this.spi = null;
        this.input = image;
        this.imageIndex = 0;
        this.position = position;
    }

    public DefaultImageTile(ImageReaderSpi spi, Object input, int imageIndex, long... position) {
        this.spi = spi;
        this.input = input;
        this.imageIndex = imageIndex;
        this.position = position;
    }

    @Override
    public Resource getResource() throws DataStoreException {
        return this;
    }

    @Override
    public TileStatus getStatus() {
        return TileStatus.EXISTS;
    }

    @Override
    public ImageReader getImageReader() throws IOException {
        ImageReaderSpi spi = this.spi;
        ImageReader reader = null;

        if (spi == null && input != null) {
            reader = XImageIO.getReader(input, Boolean.FALSE, Boolean.FALSE);
            spi = reader.getOriginatingProvider();
        }

        if (spi == null) {
            //could not find a proper reader for input
            throw new IOException("Could not find image reader spi for input : "+input);
        }

        if (reader == null) {
            Object in = null;
            try {
                in = XImageIO.toSupportedInput(spi, input);
                reader = spi.createReaderInstance();
                reader.setInput(in, true, true);
            } catch (IOException | RuntimeException e) {
                try {
                    IOUtilities.close(in);
                } catch (IOException ex) {
                    e.addSuppressed(ex);
                }
                if (reader != null) {
                    try {
                        XImageIO.dispose(reader);
                    } catch (Exception ex) {
                        e.addSuppressed(ex);
                    }
                }
                throw e;
            }
        }
        return reader;
    }

    @Override
    public ImageReaderSpi getImageReaderSpi() {
        return spi;
    }

    @Override
    public Object getInput() {
        return input;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public long[] getIndices() {
        return position;
    }

}
