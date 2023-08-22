/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.image.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import org.apache.sis.image.ComputedImage;
import org.apache.sis.coverage.grid.j2d.ImageLayout;
import org.apache.sis.util.ArgumentChecks;


/**
 * An image where tiles are read from an {@link ImageReader} when first needed.
 *
 * <p>Some {@link ImageReader} implementations may provide their own tiled image or deferred image.
 * There is no API for determining if this is the case. As a heuristic check, we could presume that
 * this is the case if the {@code ImageReader.readAsRenderedImage(…)} method is overridden. This is
 * not done yet since it would complexify the question of when to dispose the {@link ImageReader}.
 * But if the caller know that the image reader may have its own tiled or deferred image support,
 * then {@link ImageReader#readAsRenderedImage(int, ImageReadParam)} should be invoked instead.</p>
 *
 * @todo Investigate when we should use {@link ImageReader#readAsRenderedImage(int, ImageReadParam)}.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class DeferredImageRead extends ComputedImage {
    /**
     * Preferred tile size to use if the image to read is not tiled. We use much larger tiles
     * than the usual 256×256 size on the assumption that reading tiles in a non-tiled image
     * is costly, so we are better to limit our I/O operations to a few big ones.
     *
     * <p>Note that this preferred tile size is not necessarily the size that will be effectively used.
     * {@link ImageLayout} will search for a size close to the preferred size which is a divisor of the
     * image size.</p>
     */
    private static final ImageLayout COSTLY_ACCESS = new ImageLayout(new Dimension(4000, 4000), false);

    /**
     * Preferred tile size for image that are untiled but still capable to provide random accesses easily.
     * Note that this preferred tile size is not necessarily the size that will be effectively used.
     * {@link ImageLayout} will search for a size close to the preferred size which is a divisor of
     * the image size.
     */
    private static final ImageLayout EASY_ACCESS = new ImageLayout(new Dimension(1000, 1000), false);

    /**
     * The reader to use for reading tiles.
     * All read operations will be synchronized on this reader.
     */
    private final ImageReader reader;

    /**
     * Index of the image to read.
     */
    private final int imageIndex;

    /**
     * The color model (may be {@code null}).
     */
    private final ColorModel colorModel;

    /**
     * The image size in pixels.
     */
    private final int width, height;

    /**
     * The (<var>x</var>,<var>y</var>) coordinates of the upper-left pixel of tile (0,0).
     */
    private final int tileGridXOffset, tileGridYOffset;

    /**
     * Whether the reader supports tiling.
     */
    private final boolean isTiled;

    /**
     * Creates a new deferred image read operation.
     * Caller should hold a synchronization lock on {@code reader}.
     */
    private DeferredImageRead(final ImageReader reader, final int imageIndex, final boolean isTiled,
                              final SampleModel sampleModel, final ColorModel colorModel) throws IOException
    {
        super(sampleModel);
        this.reader     = reader;
        this.isTiled    = isTiled;
        this.imageIndex = imageIndex;
        this.colorModel = colorModel;
        this.width      = reader.getWidth (imageIndex);
        this.height     = reader.getHeight(imageIndex);
        tileGridXOffset = reader.getTileGridXOffset(imageIndex);
        tileGridYOffset = reader.getTileGridYOffset(imageIndex);
    }

    /**
     * Creates a new deferred image read operation.
     * All read operations will be synchronized on the given reader.
     *
     * @param  reader      the reader to use for reading tiles.
     * @param  imageIndex  index of the image to read.
     * @return an image which will read data from the specified reader.
     * @throws IOException if an error occurred while fetching image description.
     */
    public static RenderedImage create(final ImageReader reader, final int imageIndex) throws IOException {
        ArgumentChecks.ensureNonNull("reader", reader);
        synchronized (reader) {
            final ImageTypeSpecifier type = reader.getRawImageType(imageIndex);
            final ColorModel colorModel = type.getColorModel();
            int tileWidth  = reader.getTileWidth (imageIndex);
            int tileHeight = reader.getTileHeight(imageIndex);
            final boolean isTiled = reader.isImageTiled(imageIndex);
            if (!isTiled) {
                boolean supportTransparency = false;
                if (colorModel != null) {
                    if (colorModel instanceof IndexColorModel) {
                        supportTransparency = ((IndexColorModel) colorModel).getTransparentPixel() == 0;
                    } else {
                        supportTransparency = colorModel.hasAlpha();
                    }
                }
                final ImageLayout layout = reader.isRandomAccessEasy(imageIndex) ? EASY_ACCESS : COSTLY_ACCESS;
                final Dimension size = layout.suggestTileSize(tileWidth, tileHeight, supportTransparency);
                tileWidth  = size.width;
                tileHeight = size.height;
            }
            final SampleModel sampleModel = type.getSampleModel(tileWidth, tileHeight);
            return new DeferredImageRead(reader, imageIndex, isTiled, sampleModel, colorModel);
        }
    }

    /**
     * Returns the color model of the image to be read.
     */
    @Override
    public final ColorModel getColorModel() {
        return colorModel;
    }

    /**
     * Returns the number of pixels in the <var>x</var> axis of the image to be read.
     */
    @Override
    public final int getWidth() {
        return width;
    }

    /**
     * Returns the number of pixels in the <var>y</var> axis of the image to be read.
     */
    @Override
    public final int getHeight() {
        return height;
    }

    /**
     * Returns the minimum <var>x</var> coordinate (inclusive) of this image.
     *
     * @return the minimum <var>x</var> coordinate (column) of this image.
     */
    @Override
    public final int getMinX() {
        // We may have temporary `int` overflow after multiplication but exact result after addition.
        return Math.toIntExact(tileGridXOffset + Math.multiplyFull(getMinTileX(), getTileWidth()));
    }

    /**
     * Returns the minimum <var>y</var> coordinate (inclusive) of this image.
     *
     * @return the minimum <var>y</var> coordinate (column) of this image.
     */
    @Override
    public final int getMinY() {
        // We may have temporary `int` overflow after multiplication but exact result after addition.
        return Math.toIntExact(tileGridYOffset + Math.multiplyFull(getMinTileY(), getTileHeight()));
    }

    /**
     * Returns the <var>x</var> coordinate of the upper-left pixel of tile (0,0).
     *
     * @return the <var>x</var> offset of the tile grid relative to the origin.
     */
    @Override
    public final int getTileGridXOffset() {
        return tileGridXOffset;
    }

    /**
     * Returns the <var>y</var> coordinate of the upper-left pixel of tile (0,0).
     *
     * @return the <var>y</var> offset of the tile grid relative to the origin.
     */
    @Override
    public final int getTileGridYOffset() {
        return tileGridYOffset;
    }

    /**
     * Invoked when a tile needs to be read.
     *
     * @param  tileX     the column index of the tile to compute.
     * @param  tileY     the row index of the tile to compute.
     * @param  previous  if the tile already exists but needs to be updated, the tile to update. Otherwise {@code null}.
     * @return computed tile for the given indices. May be the {@code previous} tile after update but can not be null.
     * @throws IOException if an error occurred while computing the tile.
     */
    @Override
    protected Raster computeTile(final int tileX, final int tileY, final WritableRaster previous) throws IOException {
        final Rectangle source = new Rectangle(getTileWidth(), getTileHeight());
        source.x = tileX * source.width;
        source.y = tileY * source.height;
        final BufferedImage tile;
        synchronized (reader) {
            if (isTiled) {
                tile = reader.readTile(imageIndex, tileX, tileY);
            } else {
                final ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(source);
                tile = reader.read(imageIndex, param);
            }
        }
        Raster raster = tile.getRaster();
        if ((source.x | source.y) != 0) {
            raster = raster.createTranslatedChild(source.x, source.y);
        }
        return raster;
    }

    /**
     * Disposes all resources held by the image. This {@link DeferredImageRead}
     * instance shall not be used any longer after this method has been invoked.
     */
    @Override
    public void dispose() {
        IOException ioe = null;
        synchronized (reader) {
            final Object input = reader.getInput();
            reader.reset();
            reader.dispose();
            if (input instanceof Closeable) try {
                ((Closeable) input).close();
            } catch (IOException e) {
                ioe = e;
            }
        }
        super.dispose();
        if (ioe != null) {
            throw new UncheckedIOException(ioe);
        }
    }
}
