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
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.multires.TileMatrices;

/**
 * Default implementation of a TileReference
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultImageTile extends AbstractGridCoverageResource implements Tile {

    protected final org.geotoolkit.storage.multires.TileMatrix matrix;
    protected final ImageReaderSpi spi;
    protected final Object input;
    protected final int imageIndex;
    protected final long[] position;


    public DefaultImageTile(TileMatrix matrix, RenderedImage image, long... position) {
        super(null,false);
        ArgumentChecks.ensureNonNull("matrix", matrix);
        ArgumentChecks.ensureCanCast("matrix", org.geotoolkit.storage.multires.TileMatrix.class, matrix);
        if (position.length != matrix.getTilingScheme().getDimension()) {
            throw new IllegalArgumentException("Position size (" + position.length + ")do not match matrix dimension (" + matrix.getTilingScheme().getDimension()+ ")");
        }
        if (!matrix.getTilingScheme().getExtent().contains(position)) {
            throw new IllegalArgumentException("Position " + Arrays.toString(position) + " is not within matrix extent " + matrix.getTilingScheme().getExtent() + ")");
        }
        this.matrix = (org.geotoolkit.storage.multires.TileMatrix) matrix;
        this.spi = null;
        this.input = image;
        this.imageIndex = 0;
        this.position = position;
    }

    public DefaultImageTile(TileMatrix matrix, ImageReaderSpi spi, Object input, int imageIndex, long... position) {
        super(null,false);
        ArgumentChecks.ensureNonNull("matrix", matrix);
        ArgumentChecks.ensureCanCast("matrix", org.geotoolkit.storage.multires.TileMatrix.class, matrix);
        if (position.length != matrix.getTilingScheme().getDimension()) {
            throw new IllegalArgumentException("Position size (" + position.length + ")do not match matrix dimension (" + matrix.getTilingScheme().getDimension()+ ")");
        }
        if (!matrix.getTilingScheme().getExtent().contains(position)) {
            throw new IllegalArgumentException("Position " + Arrays.toString(position) + " is not within matrix extent " + matrix.getTilingScheme().getExtent() + ")");
        }
        this.matrix = (org.geotoolkit.storage.multires.TileMatrix) matrix;
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

    /**
     * Default implementation check if input is an image.
     * If not a reader is used to read the image.
     *
     * @return RenderedImage
     * @throws java.io.IOException
     */
    public RenderedImage getImage() throws IOException {
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
    protected ImageReader getImageReader() throws IOException {
        ImageReaderSpi spi = this.getImageReaderSpi();
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

    /**
     * Returns the image reader provider (never {@code null}). This is the provider used for
     * creating the {@linkplain ImageReader image reader} to be used for reading this tile.
     *
     * @return The image reader provider.
     *
     * @see ImageReaderSpi#createReaderInstance()
     */
    public ImageReaderSpi getImageReaderSpi() {
        return spi;
    }

    /**
     * Returns the input to be given to the image reader for reading this tile.
     *
     * @return The image input.
     *
     * @see ImageReader#setInput
     */
    public Object getInput() {
        return input;
    }

    /**
     * Returns the image index to be given to the image reader for reading this tile.
     *
     * @return The image index, numbered from 0.
     *
     * @see ImageReader#read(int)
     */
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public long[] getIndices() {
        return position.clone();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return TileMatrices.getTileGridGeometry2D(matrix, position, matrix.getTileSize());
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return read(null).getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final RenderedImage image;
        try {
            image = getImage();
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        return new GridCoverage2D(getGridGeometry(), null, image);
    }

}
