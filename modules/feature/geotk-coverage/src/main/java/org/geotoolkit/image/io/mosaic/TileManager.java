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
package org.geotoolkit.image.io.mosaic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

import org.apache.sis.util.logging.Logging;
import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.coverage.grid.ImageGeometry;
import org.geotoolkit.util.collection.FrequencySortedSet;
import org.apache.sis.internal.referencing.j2d.ImmutableAffineTransform;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.collection.XCollections.unmodifiableOrCopy;


/**
 * A collection of {@link Tile} objects to be given to {@link MosaicImageReader}. This base
 * class does not assume that the tiles are arranged in any particular order (especially grids).
 * But subclasses can make such assumption for better performances.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 2.5
 * @module
 */
public abstract class TileManager implements Serializable {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.mosaic");

    /**
     * For cross-version compatibility during serialization.
     */
    private static final long serialVersionUID = -7645850962821189968L;

    /**
     * The default name of the filename containing a serialized form of a {@code TileManager}
     * instance. Files of this name can be created and loaded by various Geotk module, for
     * example {@code geotk-coverage-sql} and {@code geotk-wizards-swing}.
     *
     * @since 3.18
     */
    public static final String SERIALIZED_FILENAME = "TileManager.serialized";

    /**
     * The grid geometry, including the "<cite>grid to real world</cite>" transform
     * mapping pixel {@linkplain PixelOrientation#UPPER_LEFT upper left} corner.
     * This is provided by {@link TileManagerFactory} when this information is available.
     */
    private ImageGeometry geometry;

    /**
     * All image providers used as an unmodifiable set. Computed when first needed.
     */
    transient Set<ImageReaderSpi> providers;

    /**
     * If this tile manager has been created by reading a {@value #SERIALIZED_FILENAME}
     * file or by scanning a directory, that file or directory. Otherwise {@code null}.
     * <p>
     * This field is not serialized because it is set by {@link TileManagerFactory} on
     * deserialization, and the user may have moved the {@value #SERIALIZED_FILENAME} file.
     *
     * @since 3.15
     */
    private transient Path sourceFile;

    /**
     * Creates a tile manager.
     */
    protected TileManager() {
    }

    /**
     * Sets the file or directory from which this tile manager has been created.
     * At most one non-null file can be specified.
     *
     * @param file The file or directory from which this tile manager has been created.
     *
     * @since 3.15
     */
    final synchronized void setSourceFile(final Path file) {
        if (sourceFile != null) {
            throw new IllegalStateException();
        }
        sourceFile = file;
    }

    /**
     * Returns the file or directory from which this tile manager has been created.
     *
     * @return The file or directory from which this tile manager has been created,
     *         or {@code null} if none or unknown.
     *
     * @since 3.15
     */
    final synchronized Path getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the {@linkplain Tile#getGridToCRS grid to CRS} transform for every tiles. A copy of
     * the supplied affine transform is {@linkplain AffineTransform#scale scaled} according the
     * {@linkplain Tile#getSubsampling subsampling} of each tile. Tiles having the same
     * subsampling will share the same immutable instance of affine transform.
     * <p>
     * The <cite>grid to CRS</cite> transform is not necessary for proper working of {@linkplain
     * MosaicImageReader mosaic image reader}, but is provided as a convenience for users.
     * <p>
     * This method can be invoked at most once. It can not be invoked at all if the transform
     * has been automatically invoked by {@link TileManagerFactory}.
     *
     * @param  gridToCRS The "<cite>grid to real world</cite>" transform mapping
     *         pixel {@linkplain PixelOrientation#UPPER_LEFT upper left} corner.
     * @throws IllegalStateException if a transform was already assigned to at least one tile.
     * @throws IOException If an I/O operation was required and failed.
     */
    public synchronized void setGridToCRS(final AffineTransform gridToCRS)
            throws IllegalStateException, IOException
    {
        if (geometry != null) {
            throw new IllegalStateException();
        }
        final Map<Dimension,AffineTransform> shared = new HashMap<>();
        AffineTransform at = new ImmutableAffineTransform(gridToCRS);
        shared.put(new Dimension(1,1), at);
        geometry = new ImageGeometry(getRegion(), at);
        for (final Tile tile : getInternalTiles()) {
            final Dimension subsampling = tile.getSubsampling();
            at = shared.get(subsampling);
            if (at == null) {
                at = new AffineTransform(gridToCRS);
                at.scale(subsampling.width, subsampling.height);
                at = new ImmutableAffineTransform(at);
                shared.put(subsampling, at);
            }
            tile.setGridToCRS(at);
        }
    }

    /**
     * Sets the grid geometry to the given value. This method is for {@link TileManagerFactory}
     * usage only, which will set the geometry computed by {@link RegionCalculator}. The given
     * geometry shall maps pixel upper left corner.
     */
    synchronized void setGridGeometry(final ImageGeometry geometry) {
        if (this.geometry != null) {
            throw new IllegalStateException();
        }
        this.geometry = geometry;
    }

    /**
     * Returns the grid geometry, including the "<cite>grid to real world</cite>" transform.
     * This information is typically available only when {@linkplain AffineTransform affine
     * transform} were explicitly given to {@linkplain Tile#Tile(ImageReaderSpi,Object,int,
     * Rectangle,AffineTransform) tile constructor}.
     * <p>
     * Note that the {@linkplain ImageGeometry#getGridToCRS() grid to CRS} transform of the
     * returned geometry maps pixel {@linkplain PixelOrientation#UPPER_LEFT upper left} corner.
     * This is different than OGC practice which maps pixel center, but is consistent with
     * Java2D practice for which this class is designed.
     *
     * @return The grid geometry, or {@code null} if this information is not available.
     * @throws IOException If an I/O operation was required and failed.
     *
     * @see Tile#getGridToCRS()
     */
    public synchronized ImageGeometry getGridGeometry() throws IOException {
        return geometry;
    }

    /**
     * Returns the region enclosing all tiles. Subclasses will override this method with
     * a better implementation.
     *
     * @return The region. <strong>Do not modify</strong> since it may be a direct reference to
     *         internal structures.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    Rectangle getRegion() throws IOException {
        return getGridGeometry().getExtent();
    }

    /**
     * Returns the tiles dimension. Subclasses will override this method with a better
     * implementation.
     * <p>
     * {@link MosaicImageReader#getTileWidth(int)} (and the equivalent method for height)
     * delegate to this method.
     *
     * @return The tiles dimension. <strong>Do not modify</strong> since it may be a direct
     *         reference to internal structures.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    Dimension getTileSize() throws IOException {
        return getRegion().getSize();
    }

    /**
     * Returns {@code true} if there is more than one tile. The default implementation returns
     * {@code true} in all cases.
     * <p>
     * {@link MosaicImageReader#isImageTiled(int)} delegates to this method.
     *
     * @return {@code true} if the image is tiled.
     * @throws IOException If an I/O operation was required and failed.
     */
    boolean isImageTiled() throws IOException {
        return true;
    }

    /**
     * Returns all image reader providers used by the tiles. The set will typically contains
     * only one element, but more are allowed. In the later case, the entries in the set are
     * sorted from the most frequently used provider to the less frequently used.
     *
     * @return The image reader providers (never {@code null}).
     * @throws IOException If an I/O operation was required and failed.
     *
     * @see MosaicImageReader#getTileReaderSpis
     */
    public synchronized Set<ImageReaderSpi> getImageReaderSpis() throws IOException {
        if (providers == null) {
            final FrequencySortedSet<ImageReaderSpi> providers = new FrequencySortedSet<>(4, true);
            final Collection<Tile> tiles = getInternalTiles();
            int[] frequencies = null;
            if (tiles instanceof FrequencySortedSet<?>) {
                frequencies = ((FrequencySortedSet<Tile>) tiles).frequencies();
            }
            int i = 0;
            for (final Tile tile : tiles) {
                final int n = (frequencies != null) ? frequencies[i++] : 1;
                providers.add(tile.getImageReaderSpi(), n);
            }
            this.providers = unmodifiableOrCopy(providers);
        }
        return providers;
    }

    /**
     * Creates a tile with a {@linkplain Tile#getRegion region} big enough for containing
     * {@linkplain #getTiles every tiles}. The created tile has a {@linkplain Tile#getSubsampling
     * subsampling} of (1,1). This is sometime useful for creating a "virtual" image representing
     * the assembled mosaic as a whole.
     *
     * @param  provider
     *              The image reader provider to be given to the created tile, or {@code null} for
     *              inferring it automatically. In the later case the provider is inferred from the
     *              input suffix if any (e.g. the {@code ".png"} extension in a filename), or
     *              failing that the most frequently used provider is selected.
     * @param  input
     *              The input to be given to the created tile. It doesn't need to be an existing
     *              {@linkplain java.io.File file} or URI since this method will not attempt to
     *              read it.
     * @param  imageIndex
     *              The image index to be given to the created tile (usually 0).
     * @return A global tile big enough for containing every tiles in this manager.
     * @throws NoSuchElementException
     *              If this manager do not contains at least one tile.
     * @throws IOException
     *              If an I/O operation was required and failed.
     */
    public Tile createGlobalTile(ImageReaderSpi provider, final Object input, final int imageIndex)
            throws NoSuchElementException, IOException
    {
        if (provider == null) {
            // Following line may throw the NoSuchElementException documented in javadoc.
            provider = getImageReaderSpis().iterator().next();
            ImageReaderSpi inferred = Tile.getImageReaderSpi(input);
            if (inferred != null && inferred != provider) {
                final Collection<String> f1 = Arrays.asList(provider.getFormatNames());
                final Collection<String> f2 = Arrays.asList(inferred.getFormatNames());
                if (!f1.containsAll(f2)) {
                    provider = inferred;
                }
            }
        }
        final Tile tile;
        final ImageGeometry geometry = getGridGeometry();
        if (geometry == null) {
            tile = new LargeTile(provider, input, imageIndex, getRegion());
        } else {
            tile = new LargeTile(provider, input, imageIndex, geometry.getExtent());
            tile.setGridToCRS(geometry.getGridToCRS());
        }
        return tile;
    }

    /**
     * Returns a reference to the tiles used internally by the tile manager. The returned collection
     * must contains only direct references to the tiles hold internally, not instances created on
     * the fly (as {@link GridTileManager} can do). This is because we want to update the state of
     * those tiles in a persistent way if this method is invoked by {@link #setGridToCRS}.
     * <p>
     * Callers of this method should not rely on the {@linkplain Tile#getInput tile input} and
     * should not attempt to read the tiles, since the inputs can be non-existent files or patterns
     * (again the case of {@link GridTileManager}). This method is not public for that reason.
     * <p>
     * The default implementation returns {@link #getTiles}.
     *
     * @return The internal tiles. If the returned collection is an instance of
     *         {@link FrequencySortedSet}, then the frequencies will be honored
     *         in methods where it matter like {@link #getImageReaderSpis}.
     * @throws IOException If an I/O operation was required and failed.
     */
    Collection<Tile> getInternalTiles() throws IOException {
        return getTiles();
    }

    /**
     * Returns all tiles.
     *
     * @return The tiles.
     * @throws IOException If an I/O operation was required and failed.
     */
    public abstract Collection<Tile> getTiles() throws IOException;

    /**
     * Returns every tiles that intersect the given region.
     *
     * @param region
     *          The region of interest (shall not be {@code null}).
     * @param subsampling
     *          On input, the number of source columns and rows to advance for each pixel. On
     *          output, the effective values to use. Those values may be different only if
     *          {@code subsamplingChangeAllowed} is {@code true}.
     * @param subsamplingChangeAllowed
     *          If {@code true}, this method is allowed to replace {@code subsampling} by the
     *          highest subsampling that overviews can handle, not greater than the given
     *          subsampling.
     * @return The tiles that intercept the given region. May be empty but never {@code null}.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    public abstract Collection<Tile> getTiles(Rectangle region, Dimension subsampling,
            boolean subsamplingChangeAllowed) throws IOException;

    /**
     * Returns {@code true} if at least one tile having the given subsampling or a finer
     * one intersects the given region. The default implementation returns {@code true} if
     * <code>{@linkplain #getTiles(Rectangle,Dimension,boolean) getTiles}(region, subsampling, false)</code>
     * returns a non-empty set. Subclasses are encouraged to provide a more efficient implementation.
     *
     * @param  region
     *          The region of interest (shall not be {@code null}).
     * @param  subsampling
     *          The maximal subsampling to look for.
     * @return {@code true} if at least one tile having the given subsampling or a finer one
     *          intersects the given region.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    public boolean intersects(Rectangle region, Dimension subsampling) throws IOException {
        return !getTiles(region, subsampling, false).isEmpty();
    }

    /**
     * Checks for file existence and image size of every tiles and reports any error found.
     *
     * @param out Where to report errors ({@code null} for default, which is the
     *            {@linkplain System#out standard output stream}).
     */
    public void printErrors(PrintWriter out) {
        if (out == null) {
            out = new PrintWriter(System.out, true);
        }
        final Collection<Tile> tiles;
        try {
            tiles = getTiles();
        } catch (IOException e) {
            e.printStackTrace(out);
            return;
        }
        for (final Tile tile : tiles) {
            final int imageIndex = tile.getImageIndex();
            ImageReader reader = null;
            String message = null;
            try {
                final Dimension size = tile.getSize();
                reader = tile.getImageReader();
                final int width  = reader.getWidth (imageIndex);
                final int height = reader.getHeight(imageIndex);
                if (width != size.width || height != size.height) {
                    message = Errors.format(Errors.Keys.UnexpectedImageSize);
                }
                Tile.dispose(reader);
                reader = null;
            } catch (IOException | RuntimeException exception) {
                message = exception.toString();
            }
            if (message != null) {
                out.println(tile);
                out.print("    ");
                out.println(message);
            }
            // In case an exception was thrown before Tile.dispose(reader).
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    /**
     * Returns the greatest number of pixels found in all tiles.
     *
     * @return The greatest number of pixels found in all tiles.
     * @throws IOException If an I/O error occurred.
     *
     * @since 3.00
     */
    final long largestTileArea() throws IOException {
        long max = 0;
        for (final Tile tile : getInternalTiles()) {
            final Dimension size = tile.getSize();
            final long area = ((long) size.width) * ((long) size.height);
            if (area > max) {
                max = area;
            }
        }
        return max;
    }

    /**
     * Returns the root directory of all tiles, or {@code null} if unknown. This method searches
     * for a common {@linkplain Path#getParent() parent directory} of every tiles.
     *
     * @return The root directory, or {@code null} if unknown.
     * @throws IOException If an I/O error occurred.
     *
     * @since 3.00
     */
    final Path rootDirectory() throws IOException {
        Path root = null;
        for (final Tile tile : getTiles()) {
            Object input = tile.getInput();

            if (IOUtilities.canProcessAsPath(input)) {
                Path inputPath = IOUtilities.toPath(input);
                Path parent = inputPath.getParent();
                if (parent == null) {
                    return Paths.get(".");
                }

                if (root == null) {
                    root = parent;
                } else {
                    root = IOUtilities.commonParent(root, parent);
                }

            } else {
                // Unknown type - log and look at the next tile.
                LOGGER.log(Level.WARNING, "Unsupported tile input instance of "+input.getClass().getName());
            }
        }
        return root;
    }

    /**
     * Returns the disk space which would be required if every tiles were saved on disk in
     * the uncompressed RAW format. Current implementation assumes that the image have only
     * one band - consequently the returned value should be multiplied by the amount of bands.
     * <p>
     * This method is not public because of the above-cited "single banded" assumption.
     *
     * @return The required disk space for uncompressed tiles.
     * @throws IOException If an I/O error occurred.
     *
     * @since 3.00
     */
    final long diskUsage() throws IOException {
        long space = 0;
        final Collection<Tile> tiles = getInternalTiles();
        final FrequencySortedSet<Tile> ft;
        if (tiles instanceof FrequencySortedSet<?>) {
            ft = (FrequencySortedSet<Tile>) tiles;
        } else {
            ft = null;
        }
        for (final Tile tile : tiles) {
            final Dimension size = tile.getSize();
            long length = ((long) size.width) * ((long) size.height);
            if (ft != null) {
                length *= ft.frequency(tile);
            }
            space += length;
        }
        return space;
    }

    /**
     * Returns {@code true} if the image reader from the given provider can write
     * the image directly in a destination image. It should be the case of every
     * formats, but experience suggests that the JAI TIFF reader has bugs.
     *
     * @since 3.15
     */
    boolean canWriteInPlace(final ImageReaderSpi spi) {
        if (spi == null) {
            return true;
        }
        final String cn = spi.getClass().getName();
        return !cn.equals("com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi");
    }

    /**
     * Returns a string representation of this tile manager. The default implementation
     * formats the first tiles in a table. Subclasses may format the tiles in a tree
     * instead. Note that in both cases the result may be a quite long string.
     *
     * @return A string representation.
     */
    @Override
    public String toString() {
        final Collection<Tile> tiles;
        try {
            tiles = getTiles();
        } catch (IOException e) {
            return e.toString();
        }
        /*
         * If each lines are 100 characters long, then limiting the formatting to 10000 tiles
         * will limit memory consumption to approximatively 1 Mb.
         */
        return Tile.toString(tiles, 10000);
    }
}
