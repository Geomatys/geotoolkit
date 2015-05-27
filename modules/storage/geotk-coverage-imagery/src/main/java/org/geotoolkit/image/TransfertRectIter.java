/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.awt.image.RasterFormatException;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;


/**
 * A {@linkplain WritableRectIter writable iterator} that read pixel values from an image, and
 * write pixel values to a different image. Every {@code get} methods read values from the
 * <cite>source</cite> image specified at {@linkplain #create creation time}. Every {@code set}
 * methods write values to the <cite>destination</cite> image specified at {@linkplain #create
 * creation time}, which may or may not be the same than the <cite>source</cite> image. This is
 * different than the usual {@link WritableRectIter} contract, which read and write values in
 * the same image.
 * <p>
 * The {@code create(...)} methods return an instance of {@code TransfertRectIter} only if the
 * source and target rasters are different. Callers can use this contract for optimizing their
 * code. For example implementations of {@link javax.media.jai.PointOpImage} that copy some
 * pixel values from source to target rasters can skip the copy phase if the iterator is not
 * an instance of {@code TransfertRectIter}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.11
 *
 * @since 2.3
 * @module
 */
public class TransfertRectIter implements WritableRectIter {
    /**
     * The string for error message.
     *
     * @todo Localize.
     */
    private static final String ERROR = "Size mismatch";

    /**
     * The iterator to use for reading from the source.
     *
     * @since 3.11
     */
    protected final RectIter src;

    /**
     * The iterator to use for writing to the destination destination.
     *
     * @since 3.11
     */
    protected final WritableRectIter dst;

    /**
     * Constructs a {@code TransfertRectIter} object.
     *
     * @param src The iterator to use for reading from the source.
     * @param dst The iterator to use for writing to the destination destination.
     *
     * @since 3.11
     */
    protected TransfertRectIter(final RectIter src, final WritableRectIter dst) {
        this.src = src;
        this.dst = dst;
    }

    /**
     * Creates a {@code WritableRectIter} for the specified source and destination iterator.
     * The two iterators must iterate over a rectangle of the same size, otherwise a
     * {@link RasterFormatException} will be thrown during the iteration.
     *
     * @param  src The source iterator.
     * @param  dst The destination iterator.
     * @return An iterator that read samples from {@code src} and write samples to {@code dst}.
     *         If {@code src == dst}, then the destination iterator itself is returned.
     */
    public static WritableRectIter create(final RectIter src, final WritableRectIter dst) {
        if (src == dst) {
            return dst;
        }
        return new TransfertRectIter(src, dst);
    }

    /**
     * Creates a {@code WritableRectIter} for the specified source and destination images.
     * The given rectangle must be in the bounds of both images (this will not be verified).
     *
     * @param  src The source image.
     * @param  dst The destination image.
     * @param  bounds The region of the images to iterate over.
     * @return An iterator that read samples from {@code src} and write samples to {@code dst}.
     *         It will be an instance of {@code TransfertRectIter} if and only if the source
     *         and destination images are not the same.
     *
     * @since 3.00
     */
    public static WritableRectIter create(RenderedImage src, WritableRenderedImage dst, Rectangle bounds) {
        if (dst instanceof BufferedImage) {
            /*
             * BufferedImage are always backed by a single raster. Consequently we are better to
             * delegate the work to create(RenderedImage, WritableRaster, Rectangle), which will
             * detects if the source uses the same raster than the destination.
             */
            return create(src, ((BufferedImage) dst).getRaster(), bounds);
        }
        WritableRectIter iter = RectIterFactory.createWritable(dst, bounds);
        if (src != dst) {
            iter = new TransfertRectIter(RectIterFactory.create(src, bounds), iter);
        }
        return iter;
    }

    /**
     * Creates a {@code WritableRectIter} for the specified source image and destination raster.
     * The given rectangle must be in the bounds of both arguments (this will not be verified).
     *
     * @param  src The source image.
     * @param  dst The destination raster.
     * @param  bounds The region of the image or raster to iterate over.
     * @return An iterator that read samples from {@code src} and write samples to {@code dst}.
     *         It will be an instance of {@code TransfertRectIter} if and only if the source
     *         and destination rasters are not the same.
     *
     * @since 3.00
     */
    public static WritableRectIter create(RenderedImage src, WritableRaster dst, Rectangle bounds) {
        final Raster tile = uniqueTile(src, bounds);
        if (tile == dst) {
            return create(tile, dst, bounds);
        }
        return new TransfertRectIter(RectIterFactory.create(src, bounds),
                RectIterFactory.createWritable(dst, bounds));
    }

    /**
     * Creates a {@code WritableRectIter} for the specified source and destination rasters.
     * The given rectangle must be in the bounds of both rasters (this will not be verified).
     *
     * @param  src The source raster.
     * @param  dst The destination raster.
     * @param  bounds The region of the rasters to iterate over.
     * @return An iterator that read sample from {@code src} and write sample to {@code dst}.
     *         It will be an instance of {@code TransfertRectIter} if and only if the source
     *         and destination rasters are not the same.
     *
     * @since 3.00
     */
    public static WritableRectIter create(Raster src, WritableRaster dst, Rectangle bounds) {
        WritableRectIter iter = RectIterFactory.createWritable(dst, bounds);
        if (src != dst) {
            iter = new TransfertRectIter(RectIterFactory.create(src, bounds), iter);
        }
        return iter;
    }

    /**
     * If the given bounds encompass only one tile of the given image, returns that tile.
     * Otherwise returns {@code null}. The given bounds would be inside the image bounds;
     * this will not be verified.
     *
     * @param  image  The image for which to get a singleton tile.
     * @param  bounds The bounds of the region of interest, or {@code null} for the whole image.
     * @return The raster inside the given bounds, or {@code null} if there is more than one raster
     *         in those bounds.
     */
    private static Raster uniqueTile(final RenderedImage image, final Rectangle bounds) {
        if (bounds == null) {
            if (image.getNumXTiles() == 1 && image.getNumYTiles() == 1) {
                return image.getTile(image.getMinTileX(), image.getMinTileY());
            }
        } else {
            int x = bounds.x;
            final int width   = image.getTileWidth();
            final int xOffset = image.getTileGridXOffset();
            final int tx = PlanarImage.XToTileX(x, xOffset, width);
            x -= PlanarImage.tileXToX(tx, xOffset, width);
            if (x + bounds.width <= width) {
                int y = bounds.y;
                final int height  = image.getTileHeight();
                final int yOffset = image.getTileGridYOffset();
                final int ty = PlanarImage.YToTileY(y, yOffset, height);
                y -= PlanarImage.tileYToY(ty, yOffset, height);
                if (y + bounds.height <= height) {
                    return image.getTile(tx, ty);
                }
            }
        }
        return null;
    }

    /**
     * Sets the iterator to the first line of its bounding rectangle.
     */
    @Override
    public void startLines() {
        src.startLines();
        dst.startLines();
    }

    /**
     * Sets the iterator to the leftmost pixel of its bounding rectangle.
     */
    @Override
    public void startPixels() {
        src.startPixels();
        dst.startPixels();
    }

    /**
     * Sets the iterator to the first band of the image.
     */
    @Override
    public void startBands() {
        src.startBands();
        dst.startBands();
    }

    /**
     * Jumps downward num lines from the current position.
     *
     * @param num The number of lines to jump.
     */
    @Override
    public void jumpLines(int num) {
        src.jumpLines(num);
        dst.jumpLines(num);
    }

    /**
     * Jumps rightward num pixels from the current position.
     *
     * @param num The number of pixels to jump.
     */
    @Override
    public void jumpPixels(int num) {
        src.jumpPixels(num);
        dst.jumpPixels(num);
    }

    /**
     * Sets the iterator to the next line of the image.
     */
    @Override
    public void nextLine() {
        src.nextLine();
        dst.nextLine();
    }

    /**
     * Sets the iterator to the next pixel in image (that is, move rightward).
     */
    @Override
    public void nextPixel() {
        src.nextPixel();
        dst.nextPixel();
    }

    /**
     * Sets the iterator to the next band in the image.
     */
    @Override
    public void nextBand() {
        src.nextBand();
        dst.nextBand();
    }

    /**
     * Sets the iterator to the next line in the image,
     * and returns {@code true} if the bottom row of the bounding rectangle has been passed.
     *
     * @return {@code true} if the iteration over lines is finished.
     */
    @Override
    public boolean nextLineDone() {
        boolean check = src.nextLineDone();
        if (check == dst.nextLineDone()) {
            return check;
        }
        throw new RasterFormatException(ERROR);
    }

    /**
     * Sets the iterator to the next pixel in the image (that is, move rightward).
     *
     * @return {@code true} if the iteration over pixels is finished.
     */
    @Override
    public boolean nextPixelDone() {
        boolean check = src.nextPixelDone();
        if (check == dst.nextPixelDone()) {
            return check;
        }
        throw new RasterFormatException(ERROR);
    }

    /**
     * Sets the iterator to the next band in the image,
     * and returns {@code true} if the max band has been exceeded.
     *
     * @return {@code true} if the iteration over bands is finished.
     */
    @Override
    public boolean nextBandDone() {
        boolean check = src.nextBandDone();
        if (check == dst.nextBandDone()) {
            return check;
        }
        throw new RasterFormatException(ERROR);
    }


    /**
     * Returns {@code true} if the bottom row of the bounding rectangle has been passed.
     *
     * @return {@code true} if the iteration over lines is finished.
     */
    @Override
    public boolean finishedLines() {
        boolean check = src.finishedLines();
        if (check == dst.finishedLines()) {
            return check;
        }
        throw new RasterFormatException(ERROR);
    }

    /**
     * Returns {@code true} if the right edge of the bounding rectangle has been passed.
     *
     * @return {@code true} if the iteration over pixels is finished.
     */
    @Override
    public boolean finishedPixels() {
        boolean check = src.finishedPixels();
        if (check == dst.finishedPixels()) {
            return check;
        }
        throw new RasterFormatException(ERROR);
    }

    /**
     * Returns {@code true} if the max band in the image has been exceeded.
     *
     * @return {@code true} if the iteration over bands is finished.
     */
    @Override
    public boolean finishedBands() {
        boolean check = src.finishedBands();
        if (check == dst.finishedBands()) {
            return check;
        }
        throw new RasterFormatException(ERROR);
    }

    /**
     * Returns the samples of the current pixel from the image in an array of int.
     *
     * @param  array The array where to store the sample values.
     * @return The array of sample values.
     */
    @Override
    public int[] getPixel(int[] array) {
        return src.getPixel(array);
    }

    /**
     * Returns the samples of the current pixel from the image in an array of float.
     *
     * @param  array The array where to store the sample values.
     * @return The array of sample values.
     */
    @Override
    public float[] getPixel(float[] array) {
        return src.getPixel(array);
    }

   /**
    * Returns the samples of the current pixel from the image in an array of double.
     *
     * @param  array The array where to store the sample values.
     * @return The array of sample values.
    */
    @Override
    public double[] getPixel(double[] array) {
        return src.getPixel(array);
    }

    /**
     * Returns the current sample as an integer.
     *
     * @return The current sample value.
     */
    @Override
    public int getSample() {
        return src.getSample();
    }

    /**
     * Returns the specified sample of the current pixel as an integer.
     *
     * @param b The band for which to get the sample value.
     * @return The sample value at the given band.
     */
    @Override
    public int getSample(int b) {
        return src.getSample(b);
    }

    /**
     * Returns the current sample as a float.
     *
     * @return The current sample value.
     */
    @Override
    public float getSampleFloat() {
        return src.getSampleFloat();
    }

    /**
     * Returns the specified sample of the current pixel as a float.
     *
     * @param b The band for which to get the sample value.
     * @return The sample value at the given band.
     */
    @Override
    public float getSampleFloat(int b) {
        return src.getSampleFloat(b);
    }

    /**
     * Returns the current sample as a double.
     *
     * @return The current sample value.
     */
    @Override
    public double getSampleDouble() {
        return src.getSampleDouble();
    }

    /**
     * Returns the specified sample of the current pixel as a double.
     *
     * @param b The band for which to get the sample value.
     * @return The sample value at the given band.
     */
    @Override
    public double getSampleDouble(int b) {
        return src.getSampleDouble(b);
    }

    /**
     * Sets all samples of the current pixel to a set of int values.
     *
     * @param array The new pixel values.
     */
    @Override
    public void setPixel(int[] array) {
        dst.setPixel(array);
    }

    /**
     * Sets all samples of the current pixel to a set of float values.
     *
     * @param array The new pixel values.
     */
    @Override
    public void setPixel(float[] array) {
        dst.setPixel(array);
    }

    /**
     * Sets all samples of the current pixel to a set of double values.
     *
     * @param array The new pixel values.
     */
    @Override
    public void setPixel(double[] array) {
        dst.setPixel(array);
    }

    /**
     * Sets the current sample to an integral value.
     *
     * @param s The new sample value.
     */
    @Override
    public void setSample(int s) {
        dst.setSample(s);
    }

    /**
     * Sets the current sample to a float value.
     *
     * @param s The new sample value.
     */
    @Override
    public void setSample(float s) {
        dst.setSample(s);
    }

    /**
     * Sets the current sample to a double value.
     *
     * @param s The new sample value.
     */
    @Override
    public void setSample(double s) {
        dst.setSample(s);
    }

    /**
     * Sets the specified sample of the current pixel to an integral value.
     *
     * @param b The band for which to set the sample value.
     * @param s The new sample value.
     */
    @Override
    public void setSample(int b, int s) {
        dst.setSample(b, s);
    }

    /**
     * Sets the specified sample of the current pixel to a float value.
     *
     * @param b The band for which to set the sample value.
     * @param s The new sample value.
     */
    @Override
    public void setSample(int b, float s) {
        dst.setSample(b, s);
    }

    /**
     * Sets the specified sample of the current pixel to a double value.
     *
     * @param b The band for which to set the sample value.
     * @param s The new sample value.
     */
    @Override
    public void setSample(int b, double s) {
        dst.setSample(b, s);
    }
}
