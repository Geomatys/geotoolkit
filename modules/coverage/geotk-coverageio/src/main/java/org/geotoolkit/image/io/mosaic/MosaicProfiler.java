/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Random;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import net.jcip.annotations.ThreadSafe;

import org.apache.sis.math.Statistics;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive;


/**
 * Profiles the loading of random rectangular regions using a given {@link TileManager}.
 * The images to load while have random locations and random sizes, constrained between
 * a {@linkplain #getMinSize() minimal} and {@linkplain #getMaxSize() maximal size}. A
 * random subsampling will be requested, constrained between a {@linkplain #getMinSubsampling()
 * minimum} and {@linkplain #getMaxSubsampling() maximum subsampling}.
 * <p>
 * More details on the algorithm used by this class are defined in the following methods:
 * <p>
 * <ul>
 *   <li>{@link #estimateEfficiency(int)}</li>
 * </ul>
 *
 * The example below profiles a mosaic for different subsampling values.
 * See {@link MosaicImageReadParam} for an explanation about why invoking
 * <code>{@linkplain #setSubsamplingChangeAllowed setSubsamplingChangeAllowed}(true)</code>
 * is strongly recommended.
 *
 * {@preformat java
 *     MosaicProfiler profiler = new MosaicProfiler(mosaic);
 *     profiler.setSubsamplingChangeAllowed(true); // STRONGLY RECOMMANDED
 *     profiler.setMaxSubsampling(1);
 *     for (int i=1; i<20; i++) {
 *         profiler.setMinSubsampling(i);
 *         double efficiency = profiler.estimateEfficiency(100);
 *         System.out.println("Subsampling=" + i + ", estimated efficiency=" + efficiency);
 *     }
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
public class MosaicProfiler {
    /**
     * The default minimal tile size.
     */
    private static final int DEFAULT_MIN = 512;

    /**
     * The default maximal tile size.
     */
    private static final int DEFAULT_MAX = 1024;

    /**
     * The mosaic to profile.
     */
    public final TileManager mosaic;

    /**
     * The area where to create random rectangle. <strong>Do not modify</strong>.
     * On construction, this is set to a rectangle which may be a shared instance.
     */
    private Rectangle region;

    /**
     * The minimal tile size, inclusive. Shall not be greater than {@link #maxSize}.
     */
    private final Dimension minSize;

    /**
     * The maximal tile size, inclusive. Shall not be greater than the mosaic size.
     */
    private final Dimension maxSize;

    /**
     * The minimal subsampling, inclusive. Shall not be greater than {@link #maxSubsampling}.
     */
    private final Dimension minSubsampling;

    /**
     * The maximal subsampling, inclusive.
     */
    private final Dimension maxSubsampling;

    /**
     * If {@code true}, the mosaic will be allowed to change the specified
     * subsampling to some lower but more efficient subsampling.
     *
     * @see #isSubsamplingChangeAllowed
     */
    private boolean subsamplingChangeAllowed;

    /**
     * The random number generator.
     */
    private final Random random = new Random();

    /**
     * Creates a profiler for the given mosaic.
     *
     * @param mosaic The mosaic to profile.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    public MosaicProfiler(final TileManager mosaic) throws IOException {
        this.mosaic = mosaic;
        region = mosaic.getRegion(); // Shared instance - do not modify!
        maxSize = new Dimension(Math.min(DEFAULT_MAX, region .width), Math.min(DEFAULT_MAX, region .height));
        minSize = new Dimension(Math.min(DEFAULT_MIN, maxSize.width), Math.min(DEFAULT_MIN, maxSize.height));
        maxSubsampling = new Dimension(region.width / minSize.width, region.height / minSize.height);
        minSubsampling = new Dimension(1, 1);
    }

    /**
     * Sets the seed of the random number generator. If this profiler is set to the same seed
     * than a previous profiling session and if the properties (minimum and maximal tile size
     * and subsampling) have not been changed, then the generated random rectangular regions
     * while be the same than the previous profiling session.
     *
     * @param seed The seed to be given to the random number generator.
     */
    public synchronized void setSeed(final long seed) {
        random.setSeed(seed);
    }

    /**
     * Copies the given source dimension to the given target dimension. This method
     * ensures that the source dimension is not empty before to perform the copy.
     */
    private static void setSize(final Dimension source, final Dimension target)
            throws IllegalArgumentException
    {
        final int width, height;
        ensureStrictlyPositive("width",  width  = source.width);
        ensureStrictlyPositive("height", height = source.height);
        target.setSize(width, height);
    }

    /**
     * If the minimum size is greater than the maximum size, ajust one of the size in order
     * to keep them ordered.
     *
     * @param min The minimum size.
     * @param max The maximum size.
     * @param a   If {@code true}, the maximum size may be augmented. If {@code false}, the
     *            minimum size may be reduced.
     */
    private static void adjust(final Dimension min, final Dimension max, final boolean a) {
        if (min.width > max.width) {
            if (a) max.width = min.width;
            else   min.width = max.width;
        }
        if (min.height > max.height) {
            if (a) max.height = min.height;
            else   min.height = max.height;
        }
    }

    /**
     * Returns the region in which random rectangles will be calculated. On
     * {@code MosaicProfiler} construction, this is set to the region covered
     * by the whole mosaic.
     *
     * @return The region in which random rectangles will be calculated.
     */
    public synchronized Rectangle getQueryRegion() {
        return (Rectangle) region.clone();
    }

    /**
     * Sets the region in which random rectangles will be calculated. This method computes
     * the intersection of the given rectangle with the mosaic bounds, and the result must
     * be non-empty.
     *
     * @param  bounds The region in which random rectangles will be calculated.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    public synchronized void setQueryRegion(final Rectangle bounds) throws IOException {
        final Rectangle old = region;
        if ((region = mosaic.getRegion().intersection(bounds)).isEmpty()) {
            region = old;
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_RECTANGLE_1, bounds));
        }
    }

    /**
     * Returns the size of the mosaic given to the constructor.
     *
     * @return The mosaic size.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    public synchronized Dimension getMosaicSize() throws IOException {
        return mosaic.getRegion().getSize();
    }

    /**
     * Returns the minimal size of the images to load.
     *
     * @return The minimal size of the images to load.
     */
    public synchronized Dimension getMinSize() {
        return (Dimension) minSize.clone();
    }

    /**
     * Sets the minimal size of the images to load. If the given size is greater
     * than the maximal size, then the maximal size will be increased accordingly.
     *
     * @param size The minimal size of the images to load.
     */
    public synchronized void setMinSize(final Dimension size) {
        setSize(size, minSize);
        adjust(minSize, maxSize, true);
    }

    /**
     * Convenience method setting the minimal size to the same value along <var>x</var>
     * and <var>y</var> axis.
     *
     * @param size The minimal width and height of the images to load.
     */
    public synchronized void setMinSize(final int size) {
        ensureStrictlyPositive("size", size);
        minSize.setSize(size, size);
        adjust(minSize, maxSize, true);
    }

    /**
     * Returns the maximal size of the images to load.
     *
     * @return The maximal size of the images to load.
     */
    public synchronized Dimension getMaxSize() {
        return (Dimension) maxSize.clone();
    }

    /**
     * Sets the maximal size of the images to load. If the given size is smaller
     * than the minimal size, then the minimal size will be reduced accordingly.
     *
     * @param size The maximal size of the images to load.
     */
    public synchronized void setMaxSize(final Dimension size) {
        setSize(size, maxSize);
        adjust(minSize, maxSize, false);
    }

    /**
     * Convenience method setting the maximal size to the same value along <var>x</var>
     * and <var>y</var> axis.
     *
     * @param size The maximal width and height of the images to load.
     */
    public synchronized void setMaxSize(final int size) {
        ensureStrictlyPositive("size", size);
        maxSize.setSize(size, size);
        adjust(minSize, maxSize, false);
    }

    /**
     * Returns the minimal subsampling of the random rectangular regions to load.
     *
     * @return The minimal subsampling of the regions to load.
     */
    public synchronized Dimension getMinSubsampling() {
        return (Dimension) minSubsampling.clone();
    }

    /**
     * Sets the minimal subsampling of the random rectangular regions to load. If the given
     * subsampling is greater than the maximal subsampling, then the maximal subsampling will
     * be increased accordingly.
     *
     * @param subsampling The minimal subsampling of the regions to load.
     */
    public synchronized void setMinSubsampling(final Dimension subsampling) {
        setSize(subsampling, minSubsampling);
        adjust(minSubsampling, maxSubsampling, true);
    }

    /**
     * Convenience method setting the minimal subsampling to the same value along <var>x</var>
     * and <var>y</var> axis.
     *
     * @param subsampling The minimal subsampling along <var>x</var> and <var>y</var> axis.
     */
    public synchronized void setMinSubsampling(final int subsampling) {
        ensureStrictlyPositive("subsampling", subsampling);
        minSubsampling.setSize(subsampling, subsampling);
        adjust(minSubsampling, maxSubsampling, true);
    }

    /**
     * Returns the maximal subsampling of the random rectangular regions to load.
     *
     * @return The maximal subsampling of the regions to load.
     */
    public synchronized Dimension getMaxSubsampling() {
        return (Dimension) maxSubsampling.clone();
    }

    /**
     * Sets the maximal subsampling of the random rectangular regions to load. If the given
     * subsampling is smaller than the minimal subsampling, then the minimal subsampling will
     * be reduced accordingly.
     *
     * @param subsampling The maximal subsampling of the regions to load.
     */
    public synchronized void setMaxSubsampling(final Dimension subsampling) {
        setSize(subsampling, maxSubsampling);
        adjust(minSubsampling, maxSubsampling, false);
    }

    /**
     * Convenience method setting the maximal subsampling to the same value along <var>x</var>
     * and <var>y</var> axis.
     *
     * @param subsampling The maximal subsampling along <var>x</var> and <var>y</var> axis.
     */
    public synchronized void setMaxSubsampling(final int subsampling) {
        ensureStrictlyPositive("subsampling", subsampling);
        maxSubsampling.setSize(subsampling, subsampling);
        adjust(minSubsampling, maxSubsampling, false);
    }

    /**
     * Returns {@code true} if the mosaic is allowed to change the subsampling to some more
     * efficient value. The default value is {@code false}, which means that the mosaic will
     * use exactly the given subsampling and may leads to very slow reading.
     *
     * @return {@code true} if the mosaic is allowed to change the subsampling.
     *
     * @see MosaicImageReadParam#isSubsamplingChangeAllowed
     */
    public synchronized boolean isSubsamplingChangeAllowed() {
        return subsamplingChangeAllowed;
    }

    /**
     * Sets whatever the mosaic will be allowed to change the subsampling to some more efficient
     * value. <strong>Users are strongly encouraged to set this value to {@code true}</strong>,
     * which is not the default because doing so would violate the {@link javax.imageio.ImageReader}
     * contract.
     *
     * @param allowed {@code true} if the mosaic is allowed to change the subsampling.
     *
     * @see MosaicImageReadParam#setSubsamplingChangeAllowed
     */
    public synchronized void setSubsamplingChangeAllowed(final boolean allowed) {
        subsamplingChangeAllowed = allowed;
    }

    /**
     * Returns an empirical estimation of the efficiency of loading images using the mosaic. This
     * method creates the given amount of random rectangles in the area of the mosaic, then
     * estimates the theorical cost that loading those images would have. This is a only a
     * guess - the images are not really loaded.
     * <p>
     * The highest value that this method can return is 1, which means that <cite>optimal
     * loading</cite> (defined below) would occur. Values lower than 1 are the average time
     * of image loadings compared to the optimal case. For example a value of 0.5 means that the
     * <cite>theorical image loading time</cite> (defined below) is on average two time greater
     * than it would be if it was possible to read all images optimally.
     *
     * {@section Definition of terms}
     * The "<cite>theorical image loading time</cite>" is defined as proportional to the amount of
     * pixels to read, assuming that each tile is read in a "<cite>all or nothing</cite>" fashion.
     * This is only a gross approximation of the reality since image compression induces non-linear
     * relationship between the amount of pixels and the loading time, and some image formats like
     * TIFF and JPEG2000 can be tiled - thus breaking the "all or nothing" assumption.
     * <p>
     * The "<cite>optimal loading</cite>" case is defined as the case where every pixels to be
     * traversed while reading tiles are useful to the requested image, with no pixel to discart
     * because of croping or subsampling.
     *
     * @param  numSamples The number of rectangular region to simulate loading.
     * @return The estimated efficiency of loading images, as a value between 0 and 1 inclusve.
     *         Higher values are better.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    public synchronized Statistics estimateEfficiency(int numSamples) throws IOException {
        final int dsx = maxSubsampling.width  - minSubsampling.width  + 1;
        final int dsy = maxSubsampling.height - minSubsampling.height + 1;
        final int dw  = maxSize.width  - minSize.width  + 1;
        final int dh  = maxSize.height - minSize.height + 1;
        final Rectangle region = this.region; // Shared instance - do not modify!
        final Rectangle sample = new Rectangle();
        final Dimension subsampling = new Dimension();
        final Statistics stats = new Statistics(null);
        while (--numSamples >= 0) {
            final int sx = minSubsampling.width  + random.nextInt(dsx);
            final int sy = minSubsampling.height + random.nextInt(dsy);
            final int width  = Math.min((minSize.width  + random.nextInt(dw)) * sx, region.width);
            final int height = Math.min((minSize.height + random.nextInt(dh)) * sy, region.height);
            final int x = region.x + random.nextInt(region.width  - width  + 1);
            final int y = region.y + random.nextInt(region.height - height + 1);
            sample.setBounds(x, y, width, height);
            assert region.contains(sample) : sample;
            subsampling.setSize(sx, sy);
            long cost = 0;
            for (final Tile tile : mosaic.getTiles(sample, subsampling, subsamplingChangeAllowed)) {
                cost += tile.countUnwantedPixelsFromAbsolute(sample, subsampling);
            }
            final long area = (long) width * (long) height / (subsampling.width * subsampling.height);
            stats.accept(1 / ((double) cost / (double) area + 1));
        }
        return stats;
    }
}
