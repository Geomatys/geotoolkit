/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.storage.coverage.mosaic;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.io.TableAppender;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.datum.PixelInCell;


/**
 * A tile to be read by {@link MosaicImageReader}. Each tile must contains the following:
 * <p>
 * <ul>
 *   <li><p><b>An {@link ImageReaderSpi} instance</b>. The same provider is typically used for every
 *   tiles, but this is not mandatory. An {@linkplain ImageReader image reader} will be instantiated
 *   and the {@linkplain #getInput input} will be assigned to it before a tile is read.</p></li>
 *
 *   <li><p><b>An input</b>, typically a {@linkplain Path path}, {@linkplain File file}, {@linkplain URL},
 *   {@linkplain URI} or {@linkplain String}. The input is typically different for every tile to be read, but this
 *   is not mandatory. For example different tiles could be stored at different
 *   {@linkplain #getImageIndex image index} in the same file.</p></li>
 *
 *   <li><p><b>An image index</b> to be given to {@link ImageReader#read(int)} for reading the
 *   tile. This index is often 0.</p></li>
 *
 *   <li><p><b>The upper-left corner</b> in the destination image as a {@linkplain Point point},
 *   or the upper-left corner together with the image size as a {@linkplain Rectangle rectangle}.
 *   If the upper-left corner has been given as a {@linkplain Point point}, then the
 *   {@linkplain ImageReader#getWidth width} and {@linkplain ImageReader#getHeight height} will
 *   be obtained from the image reader when first needed, which may have a slight performance cost.
 *   If the upper-left corner has been given as a {@linkplain Rectangle rectangle} instead, then
 *   this performance cost is avoided but the user is responsible for the accuracy of the
 *   information provided.
 *
 *     <blockquote><font size=2>
 *     <b>NOTE:</b> The upper-left corner is the {@linkplain #getLocation location} of this tile
 *     in the {@linkplain javax.imageio.ImageReadParam#setDestination destination image} when no
 *     {@linkplain javax.imageio.ImageReadParam#setDestinationOffset destination offset} are
 *     specified. If the user specified a destination offset, then the tile location will be
 *     translated accordingly for the image being read.
 *     </font></blockquote></p></li>
 *
 *   <li><p><b>The subsampling relative to the tile having the best resolution.</b> This is not
 *   the subsampling to apply when reading this tile, but rather the subsampling that we would
 *   need to apply on the tile having the finest resolution in order to produce an image equivalent
 *   to this tile. The subsampling is (1,1) for the tile having the finest resolution, (2,3) for an
 *   overview having half the width and third of the height for the same geographic extent,
 *   <i>etc.</i> (note that overviews are not required to have the same geographic extent -
 *   the above is just an example).</p>
 *
 *     <blockquote><font size=2>
 *     <b>NOTE 1:</b> The semantic assumes that overviews are produced by subsampling, not by
 *     interpolation or pixel averaging. The later are not prohibited, but doing so introduce
 *     some subsampling-dependent variations in images produced by {@link MosaicImageReader},
 *     which would not be what we would expect from a strictly compliant {@link ImageReader}.
 *     <br><br>
 *     <b>NOTE 2:</b> Tile {@linkplain #getLocation location} and {@linkplain #getRegion region}
 *     coordinates should be specified in the overview pixel units - they should <em>not</em> be
 *     pre-multiplied by subsampling. This multiplication will be performed automatically by
 *     {@link TileManager} when comparing regions from tiles at different subsampling levels.
 *     </font></blockquote></p></li>
 * </ul>
 * <p>
 * The tiles are not required to be arranged on a regular grid, but performances may be
 * better if they are. {@link TileManagerFactory} is responsible for analyzing the layout
 * of a collection of tiles and instantiate {@link TileManager} subclasses optimized for
 * the layout geometry.
 * <p>
 * {@code Tile}s can be considered as immutable after construction. However some properties
 * may be available only after this tile has been given to a {@link TileManagerFactory}.
 * <p>
 * {@code Tile}s are {@linkplain Serializable serializable} if their {@linkplain #getInput input}
 * given at construction time are serializable too. The {@link ImageReaderSpi} doesn't need to be
 * serializable, but its class must be known to {@link IIORegistry} at deserialization time.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 2.5
 * @module
 */
class Tile {

    /**
     * The logger to use for all logging messages in the
     * {@code org.geotoolkit.image.io.mosaic} package.
     *
     * @since 3.16
     */
    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.mosaic");

    /*
     * IMPLEMENTATION NOTE: Try to keep Tile as compact as possible memory-wise (i.e. put as few
     * non-static fields as possible).  Big mosaics may contain thousands of Tile instances, and
     * OutOfMemoryError tends to occur. The GridTileManager subclass can keep the number of Tile
     * instances low (generating them on the fly as needed), but sometime we have to fallback on
     * the more generic TreeTileManager, which stores a reference to every Tiles.
     */

    /**
     * Mask to apply on every unsigned short values (16 bits) in order to get the 32 bits
     * integer to work with. This is also the maximal value allowed.
     */
    static final int MASK = 0xFFFF;

    private final GridCoverageResource resource;

    /**
     * The subsampling relative to the tile having the finest resolution. If this tile is the
     * one with finest resolution, then the value shall be 1. Should never be 0 or negative,
     * except if its value has not yet been computed.
     * <p>
     * Values are stored as unsigned shorts (i.e. must be used with {@code & MASK}).
     * <p>
     * This field should be considered as final. It is not final only because
     * {@link RegionCalculator} may computes its value automatically.
     */
    private short xSubsampling, ySubsampling;

    /**
     * The upper-left corner in the destination image. Should be considered as final, since
     * this class is supposed to be mostly immutable. However the value can be changed by
     * {@link #translate} before an instance is made public.
     */
    private int x, y;

    /**
     * The size of the image to be read, or 0 if not yet computed. Values are stored
     * as <strong>unsigned</strong> integers to aggregate some different SPOT Image tiles,
     * where tile width and tile height may exceed short max value.
     */
    private int width, height;

    /**
     * The "grid to real world" transform, used by {@link RegionCalculator} in order to compute
     * the {@linkplain #getRegion region} for this tile. This field is set to {@code null} when
     * {@link RegionCalculator}'s work is in progress, and set to a new value on completion.
     * <p>
     * <b>Note:</b> {@link RegionCalculator} really needs a new instance for each tile.
     * No caching allowed before {@code RegionCalculator} processing. Caching allowed
     * <em>after</em> {@code RegionCalculator} processing.
     */
    private AffineTransform gridToCRS;

    /**
     * Creates a tile for the given provider, input and location. This constructor can be used when
     * the size of the image to be read by the supplied reader is unknown. This size will be
     * fetched automatically the first time {@link #getRegion()} is invoked.
     *
     * @param resource
     * @param location
     *          The upper-left corner in the destination image.
     * @param subsampling
     *          The subsampling relative to the tile having the finest resolution, or {@code null}
     *          if none. If non-null, width and height should be strictly positive. This argument
     *          is of {@linkplain Dimension dimension} kind because it can also be understood as
     *          relative "pixel size".
     */
    public Tile(GridCoverageResource resource,
                final Point location, final Dimension subsampling)
    {
        ensureNonNull("resource", resource);
        this.resource   = resource;
        this.x          = location.x;
        this.y          = location.y;
        if (subsampling != null) {
            checkSubsampling(subsampling);
            xSubsampling = toShort(subsampling.width);
            ySubsampling = toShort(subsampling.height);
        } else {
            xSubsampling = ySubsampling = 1;
        }
    }

    /**
     * Creates a tile for the given provider, input and region. This constructor can be used when
     * the size of the image to be read by the supplied reader is known. It avoid the cost of
     * fetching the size from the reader when {@link #getRegion()} will be invoked.
     *
     * @param resource
     * @param region
     *          The region in the destination image. The {@linkplain Rectangle#width width} and
     *          {@linkplain Rectangle#height height} should match the image size.
     * @param subsampling
     *          The subsampling relative to the tile having the finest resolution, or {@code null}
     *          if none. If non-null, width and height should be strictly positive. This argument
     *          is of {@linkplain Dimension dimension} kind because it can also be understood as
     *          relative "pixel size".
     */
    public Tile(GridCoverageResource resource,
                final Rectangle region, final Dimension subsampling)
    {
        ensureNonNull("resource", resource);
        ensureNonNull("region",   region);
        if (region.isEmpty()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EmptyRectangle_1, region));
        }
        this.resource   = resource;
        this.x          = region.x;
        this.y          = region.y;
        setSize(region.width, region.height);
        if (subsampling != null) {
            checkSubsampling(subsampling);
            xSubsampling = toShort(subsampling.width);
            ySubsampling = toShort(subsampling.height);
        } else {
            xSubsampling = ySubsampling = 1;
        }
    }

    /**
     * Creates a tile for the given provider, input and "<cite>grid to real world</cite>" transform.
     * This constructor can be used when the {@linkplain #getLocation() location} of the image to be
     * read by the supplied reader is unknown. The definitive location and the subsampling will be
     * computed automatically when this tile will be given to a {@link TileManagerFactory}.
     * <p>
     * When using this constructor, the {@link #getLocation()}, {@link #getRegion()} and
     * {@link #getSubsampling()} methods will throw an {@link IllegalStateException} until this tile
     * has been given to a {@link TileManager}, which will compute those values automatically.
     *
     * @param resource
     * @param region
     *          The tile region, or {@code null} if unknown. The (<var>x</var>,<var>y</var>)
     *          location of this region is typically (0,0). The definitive location will be
     *          computed when this tile will be given to a {@link TileManagerFactory}.
     * @param gridToCRS
     *          The "<cite>grid to real world</cite>" transform mapping pixel
     *          {@linkplain PixelOrientation#UPPER_LEFT upper left} corner.
     */
    public Tile(GridCoverageResource resource,
                final Rectangle region, final AffineTransform gridToCRS)
    {
        ensureNonNull("resource",  resource);
        ensureNonNull("gridToCRS", gridToCRS);
        this.resource   = resource;
        if (region != null) {
            this.x = region.x;
            this.y = region.y;
            if (!region.isEmpty()) {
                setSize(region.width, region.height);
            }
        }
        this.gridToCRS = new AffineTransform(gridToCRS); // Really needs a new instance - no cache
    }

    /**
     * Creates a tile for the given region with default subsampling. This constructor is
     * provided for avoiding compile-tile ambiguity between null <cite>subsampling</cite>
     * and null <cite>affine transform</cite> (the former is legal, the later is not).
     *
     * @param resource
     * @param region
     *          The region in the destination image. The {@linkplain Rectangle#width width} and
     *          {@linkplain Rectangle#height height} should match the image size.
     */
    public Tile(GridCoverageResource resource, final Rectangle region) {
        this(resource, region, (Dimension) null);
    }

    /**
     * Creates a tile for the given file and its <cite>World File</cite>. The world file typically
     * has a {@code "tfw"} extension (if the input is a TIFF image) or a {@code "jgw"} extension
     * (if the input is a JPEG image), and must exists.
     *
     * @param resource
     * @throws DataStoreException
     *          If no {@code ".tfw} or {@code ".jgw"} file (depending on the extension of the
     *          input file) is found, or if an error occurred while reading that file.
     *
     * @see TileManagerFactory#listTiles(ImageReaderSpi, File[])
     * @deprecated use {@link #Tile(ImageReaderSpi, Path, int)} instead
     */
    public Tile(GridCoverageResource resource) throws DataStoreException {
        this(resource, null, (AffineTransform) resource.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER));
    }

    public GridCoverageResource getResource() {
        return resource;
    }

    /**
     * Ensures that the given value is positive and in the range of 16 bits number.
     * Returns the value casted to an unsigned {@code short} type.
     */
    static short toShort(final int n) throws IllegalArgumentException {
        if (n < 0 || n > MASK) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ValueOutOfBounds_3, n, 0, MASK));
        }
        return (short) n;
    }

    /**
     * Ensures that the subsampling is strictly positive. This method is invoked for checking
     * user-supplied arguments, as opposed to {@link #checkGeometryValidity} which checks if
     * the subsampling has been computed. Both methods differ in exception type for that reason.
     */
    static void checkSubsampling(final Dimension subsampling) throws IllegalArgumentException {
        ensureStrictlyPositive("width",  subsampling.width);
        ensureStrictlyPositive("height", subsampling.height);
    }

    /**
     * Checks if the location, region, and subsampling can be returned. Throw an exception if this
     * tile has been {@linkplain #Tile(ImageReaderSpi, Object, int, Dimension, AffineTransform)
     * created without location} and not yet processed by {@link TileManagerFactory}.
     * <p>
     * <b>Note:</b> It is not strictly necessary to synchronize this method since update to a
     * {@code int} field is atomic according Java language specification, the {@link #xSubsampling} and
     * {@link #ySubsampling} fields do not change anymore as soon as they have a non-zero value (this is
     * checked by setSubsampling(Dimension) implementation) and this method succeed only if both
     * fields are set. Most callers are already synchronized anyway, except {@link TileManager}
     * constructor which invoke this method only has a sanity check. It is okay to conservatively
     * get the exception in situations where a synchronized block would not have thrown it.
     *
     * @todo Localize the exception message.
     */
    final void checkGeometryValidity() throws IllegalStateException {
        if (xSubsampling == 0 || ySubsampling == 0) {
            throw new IllegalStateException("Tile must be processed by TileManagerFactory.");
        }
    }

    /**
     * Disposes the given reader after closing its {@linkplain ImageReader#getInput input stream}.
     * This method can be used for disposing the reader created by {@link #getImageReader()}.
     *
     * @param  reader The reader to dispose.
     * @throws IOException if an error occurred while closing the input stream.
     */
    static void dispose(final ImageReader reader) throws IOException {
        final Object input = reader.getInput();
        reader.dispose();
        IOUtilities.close(input);
    }

    /**
     * If the user-supplied transform is waiting for a processing by {@link RegionCalculator},
     * returns it. Otherwise returns {@code null}. This method is for internal usage by
     * {@link RegionCalculator} only.
     * <p>
     * See {@link #checkGeometryValidity} for a note about synchronization. When {@code clear}
     * is {@code false} (i.e. this method is invoked just in order to get a hint), it is okay
     * to conservatively return a non-null value in situations where a synchronized block would
     * have returned {@code null}.
     *
     * @param clear If {@code true}, clears the {@link #gridToCRS} field before to return. This
     *              is a way to tell that processing is in progress, and also a safety against
     *              transform usage while it may become invalid.
     * @return The transform, or {@code null} if none. This method does not clone the returned
     *         value - {@link RegionCalculator} will reference and modify directly that transform.
     */
    final AffineTransform getPendingGridToCRS(final boolean clear) {
        assert !clear || Thread.holdsLock(this); // Lock required only if 'clear' is true.
        if (xSubsampling != 0 || ySubsampling != 0) {
            // No transform waiting to be processed.
            return null;
        }
        final AffineTransform gridToCRS = this.gridToCRS;
        if (clear) {
            this.gridToCRS = null;
        }
        return gridToCRS;
    }

    /**
     * Returns the "<cite>grid to real world</cite>" transform, or {@code null} if unknown.
     * This transform is derived from the value given to the constructor, but may not be
     * identical since it may have been {@linkplain AffineTransform#translate translated}
     * in order to get a uniform grid geometry for every tiles in a {@link TileManager}.
     * <p>
     * <b>Tip:</b> The <a href="http://en.wikipedia.org/wiki/World_file">World File</a> coefficients
     * of this tile (i.e. the <cite>grid to CRS</cite> transform that we would have if the pixel in
     * the upper-left corner always had indices (0,0)) can be computed as below:
     *
     * {@preformat java
     *     Point location = tile.getLocation();
     *     AffineTransform gridToCRS = new AffineTransform(tile.getGridToCRS());
     *     gridToCRS.translate(location.x, location.y);
     * }
     *
     * @return The "<cite>grid to real world</cite>" transform mapping pixel
     *         {@linkplain PixelOrientation#UPPER_LEFT upper left} corner,
     *         or {@code null} if undefined.
     * @throws IllegalStateException If this tile has been {@linkplain #Tile(ImageReaderSpi,
     *         Object, int, Rectangle, AffineTransform) created without location} and not yet
     *         processed by {@link TileManagerFactory}.
     *
     * @see TileManager#getGridGeometry()
     */
    public synchronized AffineTransform getGridToCRS() throws IllegalStateException {
        checkGeometryValidity();
        return gridToCRS; // No need to clone since TileManagerFactory assigned an immutable instance.
    }

    /**
     * Sets the new "<cite>grid to real world</cite>" transform to use after the translation
     * performed by {@link #translate}, if any. Should be an immutable instance because it will
     * not be cloned.
     *
     * @param at The "<cite>grid to real world</cite>" transform mapping pixel
     *        {@linkplain PixelOrientation#UPPER_LEFT upper left} corner.
     * @throws IllegalStateException if an other transform was already assigned to this tile.
     */
    final synchronized void setGridToCRS(final AffineTransform at) throws IllegalStateException {
        if (gridToCRS != null) {
            if (!gridToCRS.equals(at)) {
                throw new IllegalStateException();
            }
        } else {
            gridToCRS = at;
        }
    }

    /**
     * Returns the subsampling relative to the tile having the finest resolution. This method never
     * returns {@code null}, and the width & height shall never be smaller than 1. The return type
     * is of {@linkplain Dimension dimension} kind because the value can also be interpreted as
     * relative "pixel size".
     *
     * @return The subsampling along <var>x</var> and <var>y</var> axis.
     * @throws IllegalStateException If this tile has been {@linkplain #Tile(ImageReaderSpi,
     *         Object, int, Rectangle, AffineTransform) created without location} and not yet
     *         processed by {@link TileManagerFactory}.
     *
     * @see javax.imageio.ImageReadParam#setSourceSubsampling
     */
    public synchronized Dimension getSubsampling() throws IllegalStateException {
        checkGeometryValidity();
        return new Dimension(xSubsampling & MASK, ySubsampling & MASK);
    }

    /**
     * Invoked by {@link RegionCalculator} only. No other caller allowed.
     */
    final void setSubsampling(final Dimension subsampling) throws IllegalStateException {
        assert Thread.holdsLock(this);
        if (xSubsampling != 0 || ySubsampling != 0) {
            throw new IllegalStateException(); // Should never happen.
        }
        checkSubsampling(subsampling);
        xSubsampling = toShort(subsampling.width);
        ySubsampling = toShort(subsampling.height);
    }

    /**
     * Returns the highest subsampling that this tile can handle, not greater than the given
     * subsampling. Special cases:
     * <p>
     * <ul>
     *   <li>If the given subsampling is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given subsampling is {@code (0,0)}, then this method returns the
     *       same {@code subsampling} reference unchanged. Callers can test using the identity
     *       ({@code ==}) operator.</li>
     *   <li>Otherwise if this tile can handle exactly the given subsampling, then this method
     *       returns the same {@code subsampling} reference unchanged. Callers can test using
     *       the identity ({@code ==}) operator.</li>
     *   <li>Otherwise if there is no subsampling that this tile could handle,
     *       then this method returns {@code null}.</li>
     *   <li>Otherwise this method returns a new {@link Dimension} set to the greatest subsampling
     *       that this tile can handle, not greater than the given subsampling.</li>
     * </ul>
     *
     * @param  subsampling The subsampling along <var>x</var> and <var>y</var> axis.
     * @return A subsampling equals or finer than the given one.
     * @throws IllegalStateException If this tile has been {@linkplain #Tile(ImageReaderSpi,
     *         Object, int, Rectangle, AffineTransform) created without location} and not yet
     *         processed by {@link TileManagerFactory}.
     */
    public Dimension getSubsamplingFloor(final Dimension subsampling) throws IllegalStateException {
        if (subsampling != null) {
            final int dx, dy;
            try {
                dx = subsampling.width  % (xSubsampling & MASK);
                dy = subsampling.height % (ySubsampling & MASK);
            } catch (ArithmeticException e) {
                throw new IllegalStateException("Tile must be processed by TileManagerFactory.", e);
            }
            if (dx != 0 || dy != 0) {
                final int sourceXSubsampling = subsampling.width  - dx;
                final int sourceYSubsampling = subsampling.height - dy;
                if (sourceXSubsampling != 0 && sourceYSubsampling != 0) {
                    return new Dimension(sourceXSubsampling, sourceYSubsampling);
                } else {
                    return null;
                }
            }
        }
        return subsampling;
    }

    /**
     * Returns {@code true} if this tile subsampling is finer than the specified value
     * for at least one dimension. For internal usage by {@link RTree#searchTiles} only.
     */
    final boolean isFinerThan(final Dimension subsampling) {
        return (xSubsampling & MASK) < subsampling.width ||
               (ySubsampling & MASK) < subsampling.height;
    }

    /**
     * Returns {@code true} if the subsampling of this tile is equal to the subsampling of
     * the given tile, but this tile cover a greater area than the given tile.
     *
     * @param other The other tile to compare with.
     * @return {@code true} if both tiles have the same subsampling and this tile is larger.
     */
    final boolean isLargerThan(final Tile other) {
        return xSubsampling == other.xSubsampling && ySubsampling == other.ySubsampling &&
               width * height > other.width * other.height;
    }

    /**
     * Returns the upper-left corner in the
     * {@linkplain javax.imageio.ImageReadParam#setDestination destination image}. This is the
     * location when no {@linkplain javax.imageio.ImageReadParam#setDestinationOffset destination
     * offset} are specified. If the user specified a destination offset, then the tile location
     * will be translated accordingly for the image being read.
     *
     * @return The tile upper-left corner.
     * @throws IllegalStateException If this tile has been {@linkplain #Tile(ImageReaderSpi,
     *         Object, int, Rectangle, AffineTransform) created without location} and not yet
     *         processed by {@link TileManagerFactory}.
     *
     * @see javax.imageio.ImageReadParam#setDestinationOffset
     */
    public synchronized Point getLocation() throws IllegalStateException {
        checkGeometryValidity();
        return new Point(x,y);
    }

    /**
     * Returns {@code true} if the tile size is equal to the given dimension.
     * This method should be invoked when we know that this instance is not a
     * subclass of {@link Tile}, otherwise we should use {@link #getRegion} in
     * case the user overridden the method.
     */
    final boolean isSizeEquals(final int dx, final int dy) {
        assert (getClass() == Tile.class) && (width != 0) && (height != 0) : this;
        return width == dx && height == dy;
    }

    /**
     * Returns the image size. If this tile has been created with the
     * {@linkplain #Tile(ImageReaderSpi,Object,int,Rectangle,Dimension) constructor expecting a
     * rectangle}, the dimension of that rectangle is returned. Otherwise the image {@linkplain
     * ImageReader#getWidth width} and {@linkplain ImageReader#getHeight height} are read from
     * the image reader and cached for future usage.
     * <p>
     * At the difference of {@link #getLocation()} and {@link #getRegion()}, this method never
     * throw {@link IllegalStateException} because the tile size does not depend on the processing
     * performed by {@link TileManagerFactory}.
     *
     * @return The tile size.
     * @throws DataStoreException if it was necessary to fetch the image dimension from the
     *         {@linkplain #getImageReader reader} and this operation failed.
     */
    public synchronized Dimension getSize() throws DataStoreException {
        if (width == 0 && height == 0) {
            GridGeometry gridGeometry = resource.getGridGeometry();
            GridExtent extent = gridGeometry.getExtent();
            setSize((int) extent.getSize(0), (int) extent.getSize(1));
        }
        return new Dimension(width, height);
    }

    /**
     * Returns the upper-left corner in the destination image, with the image size. If this tile
     * has been created with the {@linkplain #Tile(ImageReaderSpi,Object,int,Rectangle,Dimension)
     * constructor expecting a rectangle}, a copy of the specified rectangle is returned.
     * Otherwise the image {@linkplain ImageReader#getWidth width} and
     * {@linkplain ImageReader#getHeight height} are read from the image reader and cached for
     * future usage.
     *
     * @return The region in the destination image.
     * @throws IllegalStateException If this tile has been {@linkplain #Tile(ImageReaderSpi,
     *         Object, int, Rectangle, AffineTransform) created without location} and not yet
     *         processed by {@link TileManagerFactory}.
     * @throws DataStoreException if it was necessary to fetch the image dimension from the
     *         {@linkplain #getImageReader reader} and this operation failed.
     *
     * @see javax.imageio.ImageReadParam#setSourceRegion
     */
    public synchronized Rectangle getRegion() throws IllegalStateException, DataStoreException {
        checkGeometryValidity();
        if (width == 0 && height == 0) {
            final Dimension size = getSize();
            setSize(size.width, size.height); // Useless unless the user overloaded getSize().
        }
        return new Rectangle(x, y, width, height);
    }

    /**
     * Returns the {@linkplain #getRegion region} multiplied by the subsampling.
     * This is this tile coordinates in the units of the tile having the finest
     * resolution, as opposed to the default public methods which are always in
     * units relative to this tile.
     */
    final Rectangle getAbsoluteRegion() throws DataStoreException {
        final Rectangle region = getRegion();
        final int sx = xSubsampling & MASK;
        final int sy = ySubsampling & MASK;
        region.x      *= sx;
        region.y      *= sy;
        region.width  *= sx;
        region.height *= sy;
        return region;
    }

    /**
     * Invoked by {@link RegionCalculator} only. No other caller allowed.
     * {@link #setSubsampling} must be invoked prior this method.
     * <p>
     * Note that invoking this method usually invalidate {@link #gridToCRS}. Calls to this method
     * should be closely followed by calls to {@link #translate} for fixing the "gridToCRS" value.
     *
     * @param region The region to assign to this tile.
     * @throws ArithmeticException if {@link #setSubsampling} has not be invoked.
     */
    final void setAbsoluteRegion(final Rectangle region) throws ArithmeticException {
        assert Thread.holdsLock(this);
        final int sx = xSubsampling & MASK;
        final int sy = ySubsampling & MASK;
        assert (region.width % sx) == 0 && (region.height % sy) == 0 : region;
        x = region.x / sx;
        y = region.y / sy;
        setSize(region.width / sx, region.height / sy);
    }

    /**
     * Sets the tile size to the given values, making sure that they can be stored as unsigned
     * short. This method is overridden by {@link LargeTile} but should never been invoked by
     * anyone else than {@link Tile}.
     *
     * @param dx The tile width.
     * @param dy The tile height.
     * @throws IllegalArgumentException if the given size can't be stored as unsigned short.
     */
    void setSize(final int dx, final int dy) throws IllegalArgumentException {
        ArgumentChecks.ensurePositive("width", dx);
        ArgumentChecks.ensurePositive("height", dy);
        width  = dx;
        height = dy;
    }

    /**
     * Converts to given rectangle from absolute to relative coordinates.
     * Coordinates are rounded to the smallest box enclosing fully the given region.
     *
     * @param region The rectangle to converts. Values are replaced in-place.
     * @throws ArithmeticException if {@link #setSubsampling} has not be invoked.
     */
    final void absoluteToRelative(final Rectangle region) throws ArithmeticException {
        final int sx = xSubsampling & MASK;
        final int sy = ySubsampling & MASK;
        int xmin = region.x;
        int xmax = region.width  + xmin;
        int ymin = region.y;
        int ymax = region.height + ymin;
        if (xmin < 0) xmin -= (sx - 1);
        if (xmax > 0) xmax += (sx - 1);
        if (ymin < 0) ymin -= (sy - 1);
        if (ymax > 0) ymax += (sy - 1);
        xmin /= sx;
        xmax /= sx;
        ymin /= sy;
        ymax /= sy;
        region.x = xmin;
        region.y = ymin;
        region.width  = xmax - xmin;
        region.height = ymax - ymin;
    }

    /**
     * Translates this tile. For internal usage by {@link RegionCalculator} only.
     * This method is invoked slightly after {@link #setRegion} for final adjustment.
     * <p>
     * Reminder: {@link #setGridToCRS(AffineTransform)} should be invoked after this method.
     *
     * @param xSubsampling The translation to apply on <var>x</var> values (often 0).
     * @param ySubsampling The translation to apply on <var>y</var> values (often 0).
     */
    final synchronized void translate(final int dx, final int dy) {
        x += dx;
        y += dy;
        gridToCRS = null;
    }

    /**
     * Returns the amount of pixels in this tile that would be useless if reading the given region
     * at the given subsampling. This method is invoked by {@link TileManager} when two or more
     * tile overlaps, in order to choose the tiles that would minimize the amount of pixels to
     * read. The default implementation computes the sum of:
     * <p>
     * <ul>
     *   <li>the amount of tile pixels skipped because of the given subsampling</li>
     *   <li>the amount of pixels in this {@linkplain #getRegion tile region} that are outside
     *       the given region, including the pixels below the bottom.</li>
     * </ul>
     * <p>
     * The later is conservative since many file formats will stop reading as soon as they reach
     * the region bottom. We may consider allowing overriding in order to alter this calculation
     * if a subclass is sure that pixels below the region have no disk seed cost.
     *
     * @param  toRead The region to read, in the same units than {@link #getAbsoluteRegion}.
     * @param  subsampling The number of columns and rows to advance between pixels
     *         in the given region. Must be strictly positive (not zero).
     * @return The amount of pixels which would be unused if the reading was performed on this
     *         tile. Smaller number is better.
     * @throws DataStoreException if it was necessary to fetch the image dimension from the
     *         {@linkplain #getImageReader reader} and this operation failed.
     */
    final int countUnwantedPixelsFromAbsolute(final Rectangle toRead, final Dimension subsampling)
            throws DataStoreException
    {
        final int sx = xSubsampling & MASK;
        final int sy = ySubsampling & MASK;
        assert subsampling.width >= sx && subsampling.height >= sy : subsampling;
        final Rectangle region = getRegion();
        /*
         * Converts the tile region to absolute coordinates and clips it to the region to read.
         */
        final long xmin, ymin, xmax, ymax;
        xmin = max((long) toRead.x,                 sx * ((long) region.x));
        ymin = max((long) toRead.y,                 sy * ((long) region.y));
        xmax = min((long) toRead.x + toRead.width,  sx * ((long) region.x + region.width));
        ymax = min((long) toRead.y + toRead.height, sy * ((long) region.y + region.height));
        /*
         * Computes the amount of pixels to keep for the given region and subsampling.
         */
        long count = max(xmax - xmin, 0) * max(ymax - ymin, 0);
        count /= (subsampling.width * subsampling.height);
        /*
         * Computes the amount of pixels from the current tile that would be unused. Note that
         * we are subtracting a quantity derived from the absolute space from a quantity in the
         * relative space. The result should be positive anyway because we divided the former by
         * (s.width * s.height), which should be greater than (xSubsampling * ySubsampling).
         */
        count = (region.width * region.height) - count;
        assert count >= 0 && count <= Integer.MAX_VALUE : count;
        return (int) count;
    }

    /**
     * Compares this tile with the specified one for equality. Two tiles are considered equal
     * if they have the same {@linkplain #getImageReaderSpi provider}, {@linkplain #getInput
     * input}, {@linkplain #getImageIndex image index}, {@linkplain #getRegion region} and
     * {@linkplain #getSubsampling subsampling}.
     *
     * @param object The object to compare with.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final Tile that = (Tile) object;
            if (this.x == that.x  &&  this.y == that.y    &&
                this.xSubsampling == that.xSubsampling    &&
                this.ySubsampling == that.ySubsampling    &&
                this.resource     == that.resource)
            {
                /*
                 * Compares width and height only if they are defined in both tiles.  We do not
                 * invoke 'getRegion()' because it may be expensive and useless anyway: If both
                 * tiles have the same image reader, image index and input, then logically they
                 * must have the same size - invoking 'getRegion()' would read exactly the same
                 * image twice.
                 */
                return (width  == 0 || that.width  == 0 || width  == that.width) &&
                       (height == 0 || that.height == 0 || height == that.height);
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this tile. The default implementation uses the
     * {@linkplain #getImageReader reader}, {@linkplain #getInput input} and {@linkplain
     * #getImageIndex image index}, which should be sufficient for uniquely distinguish
     * every tiles.
     */
    @Override
    public int hashCode() {
        return resource.hashCode();
    }

    /**
     * Returns a string representation of this tile. The default implementation uses only the
     * public getter methods, so if a subclass override them the effect should be visible in
     * the returned string.
     *
     * @return A string representation of this tile.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append('[');
        if (xSubsampling != 0 || ySubsampling != 0) {
            buffer.append(", location=(");
            if (width == 0 && height == 0) {
                final Point location = getLocation();
                buffer.append(location.x).append(',').append(location.y);
            } else try {
                final Rectangle region = getRegion();
                buffer.append(region.x).append(',').append(region.y)
                      .append("), size=(").append(region.width).append(',').append(region.height);
            } catch (DataStoreException e) {
                // Should not happen since we checked that 'getRegion' should be easy.
                // If it happen anyway, put the exception message at the place where
                // coordinates were supposed to appear, so we can debug.
                buffer.append(e);
            }
            final Dimension subsampling = getSubsampling();
            buffer.append("), subsampling=(").append(subsampling.width)
                  .append(',').append(subsampling.height).append(')');
        } else {
            /*
             * Location and subsampling not yet computed, so don't display it. We can not
             * invoke 'getRegion()' neither since it would throw an IllegalStateException.
             * Since we have to read the fields directly, make sure that this instance is
             * not a subclass like LargeTile, otherwise those values may be wrong.
             */
            if ((width != 0 || height != 0) && getClass() == Tile.class) {
                buffer.append(", size=(").append(width)
                      .append(',').append(height).append(')');
            }
        }
        return buffer.append(']').toString();
    }

    /**
     * Returns a string representation of a collection of tiles. The tiles are formatted in a
     * table in iteration order. Tip: consider sorting the tiles before to invoke this method;
     * tiles are {@linkplain Comparable comparable} for this purpose.
     * <p>
     * This method is not public because it can consume a large amount of memory (the underlying
     * {@link StringBuffer} can be quite large). Users are encouraged to use the method expecting
     * a {@link Writer}, which may be expensive too but less than this method.
     *
     * @param tiles
     *          The tiles to format in a table.
     * @param maximum
     *          The maximum number of tiles to format. If there is more tiles, a message will be
     *          formatted below the table. A reasonable value like 5000 is recommended since
     *          attempt to format millions of tiles leads to {@link OutOfMemoryError}.
     * @return A string representation of the given tiles as a table.
     *
     * @see java.util.Collections#sort(List)
     */
    static String toString(final Collection<Tile> tiles, final int maximum) {
        final StringWriter writer = new StringWriter();
        try {
            writeTable(tiles, writer, maximum);
        } catch (IOException e) {
            // Should never happen since we are writing to a StringWriter.
            throw new AssertionError(e);
        }
        return writer.toString();
    }

    /**
     * Formats a collection of tiles in a table. The tiles are appended in iteration
     * order. Tip: consider sorting the tiles before to invoke this method; tiles are
     * {@linkplain Comparable comparable} for this purpose.
     *
     * @param tiles
     *          The tiles to format in a table.
     * @param out
     *          Where to write the table.
     * @param maximum
     *          The maximum number of tiles to format. If there is more tiles, a message will be
     *          formatted below the table. A reasonable value like 5000 is recommended since
     *          attempt to format millions of tiles leads to {@link OutOfMemoryError}.
     * @throws IOException
     *          If an error occurred while writing to the given writer.
     *
     * @see java.util.Collections#sort(List)
     */
    public static void writeTable(final Collection<Tile> tiles, final Writer out, final int maximum)
            throws IOException
    {
        int remaining = maximum;
        final TableAppender table = new TableAppender(out);
        table.setMultiLinesCells(false);
        table.nextLine('\u2550');
        table.append("\ty\twidth\theight\tdx\tdy\n");
        table.nextLine('\u2500');
        table.setMultiLinesCells(true);
        for (final Tile tile : tiles) {
            if (--remaining < 0) {
                break;
            }
            table.nextColumn();
            /*
             * Extracts now the tile information that we are going to format, but those
             * informations may be overridden later if the current tile is some subclass
             * of Tile. We format Tile instances in a special way since it allows us to
             * left a blank for subsampling and tile size if they are not yet computed,
             * rather than throwing an exception.
             */
            int x            = tile.x;
            int y            = tile.y;
            int width        = tile.width;
            int height       = tile.height;
            int xSubsampling = tile.xSubsampling & MASK;
            int ySubsampling = tile.ySubsampling & MASK;
            if (tile.getClass() != Tile.class) {
                final Dimension subsampling = tile.getSubsampling();
                xSubsampling = subsampling.width;
                ySubsampling = subsampling.height;
                try {
                    final Rectangle region = tile.getRegion();
                    x      = region.x;
                    y      = region.y;
                    width  = region.width;
                    height = region.height;
                } catch (DataStoreException e) {
                    // The (x,y) are likely to be correct since only (width,height) are read
                    // from the image file. So set only (width,height) to "unknown" and keep
                    // the remaining, with (x,y) obtained from direct access to Tile fields.
                    width  = 0;
                    height = 0;
                }
            }
            table.append(String.valueOf(x));
            table.nextColumn();
            table.append(String.valueOf(y));
            if (width != 0 || height != 0) {
                table.nextColumn();
                table.append(String.valueOf(width));
                table.nextColumn();
                table.append(String.valueOf(height));
            } else {
                table.nextColumn();
                table.nextColumn();
            }
            if (xSubsampling != 0 || ySubsampling != 0) {
                table.nextColumn();
                table.append(String.valueOf(xSubsampling));
                table.nextColumn();
                table.append(String.valueOf(ySubsampling));
            }
            table.nextLine();
        }
        table.nextLine('\u2550');
        /*
         * Table completed. Flushs to the writer and appends additional text if we have
         * not formatted every tiles. IOException may be trown starting from this point
         * (the above code is not expected to thrown any IOException).
         */
        table.flush();
        if (remaining < 0) {
            out.write(Vocabulary.format(Vocabulary.Keys.More_1, tiles.size() - maximum));
            out.write(System.lineSeparator());
        }
    }

}
