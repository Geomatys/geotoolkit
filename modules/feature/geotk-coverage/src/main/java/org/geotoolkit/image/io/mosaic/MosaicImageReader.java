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

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*; // Lot of imports used in this class.
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.concurrent.TimeUnit;
import javax.imageio.IIOParamController;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.nio.IOUtilities;
import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.Disposable;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.logging.LogProducer;
import org.apache.sis.util.logging.PerformanceLevel;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.FrequencySortedSet;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.coverage.grid.ImageGeometry;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;

import static org.geotoolkit.image.io.mosaic.Tile.LOGGER;
import static org.apache.sis.util.ArgumentChecks.ensureValidIndex;


/**
 * An image reader built from a mosaic of other image readers. The mosaic is specified as a
 * collection of {@link Tile} objects, organized in a {@link TileManager}. Images are read
 * using the {@link #read(int,ImageReadParam)} method. The {@code ImageReadParam} argument
 * is optional by strongly recommended, since the whole purpose of {@code MosaicImageReader}
 * is to read efficiently only subsets of big tiled images.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
public class MosaicImageReader extends ImageReader implements LogProducer, Closeable, Disposable {
    /**
     * Type arguments made of a single {@code int} value. Used with reflections in order to check
     * if a method has been overridden (knowing that it is not the case allows some optimizations).
     */
    private static final Class<?>[] INTEGER_ARGUMENTS = {
        int.class
    };

    /**
     * The cached {@link ImageReader} instances.
     */
    final TileReaderPool readers;

    /**
     * The reader currently under process of reading, or {@code null} if none. Used by
     * {@link #abort} only. Changes must be performed inside a {@code synchronized(this)} block.
     */
    private transient ImageReader reading;

    /**
     * The image metadata, created when first needed.
     */
    private transient IIOMetadata[] metadata;

    /**
     * The cached image type, used only for {@link ImageTypePolicy#SUPPORTED_BY_FIRST}.
     *
     * @since 3.15
     */
    private transient ImageTypeSpecifier cachedImageType;

    /**
     * The logging level for tiling information during read operations. If {@code null}, then
     * the level shall be selected by {@link PerformanceLevel#forDuration(long, TimeUnit)}.
     */
    private Level logLevel;

    /**
     * Constructs an image reader with the default provider.
     */
    public MosaicImageReader() {
        this(null);
    }

    /**
     * Constructs an image reader with the specified provider.
     *
     * @param spi The image reader provider, or {@code null} for the default one.
     */
    public MosaicImageReader(final ImageReaderSpi spi) {
        super(spi != null ? spi : Spi.DEFAULT);
        readers = new TileReaderPool();
    }

    /**
     * Returns {@code true} if logging is enabled.
     */
    private boolean isLoggable() {
        Level level = logLevel;
        if (level == null) {
            level = PerformanceLevel.SLOWEST;
        }
        return LOGGER.isLoggable(level);
    }

    /**
     * Returns the logging level for tile information during read operations.
     * The default value is one of the {@link PerformanceLevel} constants,
     * determined according the duration of the read operation.
     *
     * @return The current logging level.
     */
    @Override
    public Level getLogLevel() {
        final Level level = logLevel;
        return (level != null) ? level : PerformanceLevel.PERFORMANCE;
    }

    /**
     * Sets the logging level for tile information during read operations. A {@code null}
     * value restores the default level documented in the {@link #getLogLevel()} method.
     *
     * @param level The new logging level, or {@code null} for the default.
     */
    @Override
    public void setLogLevel(final Level level) {
        logLevel = level;
    }

    /**
     * Returns the tiles manager, making sure that it is set.
     *
     * @param imageIndex The image index, from 0 inclusive to {@link #getNumImages} exclusive.
     * @return The tile manager for image at the given index.
     */
    private TileManager getTileManager(final int imageIndex) throws IOException {
        if (input instanceof TileManager[]) {
            final TileManager[] tiles = (TileManager[]) input;
            ensureValidIndex(tiles.length, imageIndex);
            return tiles[imageIndex];
        }
        throw new IllegalStateException(Errors.format(Errors.Keys.NoImageInput));
    }

    /**
     * Returns the input, which is a an array of {@linkplain TileManager tile managers}.
     * The array length is the {@linkplain #getNumImages number of images}. The element
     * at index <var>i</var> is the tile manager to use when reading at image index <var>i</var>.
     */
    @Override
    public TileManager[] getInput() {
        final TileManager[] managers = (TileManager[]) super.getInput();
        return (managers != null) ? managers.clone() : null;
    }

    /**
     * Sets the input source, which is expected to be an array of
     * {@linkplain TileManager tile managers}. If the given input is a singleton, an array or a
     * {@linkplain Collection collection} of {@link Tile} objects, then it will be wrapped in an
     * array of {@link TileManager}s.
     *
     * @param input The input.
     * @param seekForwardOnly if {@code true}, images and metadata may only be read in ascending
     *        order from this input source.
     * @param ignoreMetadata if {@code true}, metadata may be ignored during reads.
     * @throws IllegalArgumentException if {@code input} is not an instance of one of the
     *         expected classes, or if the input can not be used because of an I/O error
     *         (in which case the exception has a {@link IOException} as its
     *         {@linkplain IllegalArgumentException#getCause cause}).
     */
    @Override
    public void setInput(final Object input, final boolean seekForwardOnly, final boolean ignoreMetadata)
            throws IllegalArgumentException
    {
        cachedImageType = null;
        metadata = null;
        final TileManager[] managers;
        try {
            managers = TileManagerFactory.DEFAULT.createFromObject(input);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
        final int numImages = (managers != null) ? managers.length : 0;
        super.setInput(managers, seekForwardOnly, ignoreMetadata);
        availableLocales = null; // Will be computed by getAvailableLocales() when first needed.
        /*
         * For every tile readers, closes the stream and disposes the ones that are not needed
         * anymore for the new input. The image readers that may still useful will be recycled.
         * We keep their streams open since it is possible that the new input uses the same ones
         * (the old streams will be closed later if appears to not be used).
         */
        Set<ImageReaderSpi> providers = Collections.emptySet();
        try {
            switch (numImages) {
                case 0: {
                    // Keep the empty provider set.
                    break;
                }
                case 1: {
                    providers = managers[0].getImageReaderSpis();
                    break;
                }
                default: {
                    providers = new HashSet<>(managers[0].getImageReaderSpis());
                    for (int i=1; i<numImages; i++) {
                        providers.addAll(managers[i].getImageReaderSpis());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            /*
             * Failed to get the set of providers.  This is not a big issue; the only consequence
             * is that we will dispose more readers than necessary, which means that we will need
             * to recreate them later. Note that the set of providers may be partially filled.
             */
            Logging.unexpectedException(LOGGER, MosaicImageReader.class, "setInput", e);
        }
        readers.setProviders(providers);
    }

    /**
     * Returns the <cite>Service Provider Interfaces</cite> (SPI) of every
     * {@linkplain ImageReader image readers} to be used for reading tiles.
     * This method returns an empty set if no input has been set.
     *
     * @return The service providers for tile readers.
     *
     * @see TileManager#getImageReaderSpis
     */
    public Set<ImageReaderSpi> getTileReaderSpis() {
        return readers.providers;
    }

    /**
     * Returns a reader configured for the given tile. Normally this method just redirect to
     * {@link Tile#getImageReader(MosaicImageReader, boolean, boolean)}. The sole purpose of
     * this method is to allow {@link MosaicImageWriter} to redirect the read operation to a
     * cached file in the RAW format.
     * <p>
     * Because the RAW format contains no metadata, this method should not be used by any
     * method returning a {@link IIOMetadata} object. This method is used only by the
     * following methods:
     * <p>
     * <ul>
     *   <li>{@link #read(int, ImageReadParam)}</li>
     *   <li>Methods that return a {@link ImageTypeSpecifier}</li>
     * </ul>
     *
     * @return An image reader with its {@linkplain ImageReader#getInput input} set.
     * @throws IOException if the image reader can't be initialized.
     */
    ImageReader getTileReader(final Tile tile) throws IOException {
        return tile.getImageReader(this, true, true);
    }

    /**
     * From the given set of tiles, selects one tile to use as a prototype.
     * This method tries to select the tile which use the most specific reader.
     *
     * @return The most specific tile, or {@code null} if none.
     */
    private Tile getSpecificTile(final Collection<Tile> tiles) {
        Tile fallback = null;
        final Set<ImageReader> readers = this.readers.getTileReaders();
        Class<?> type = Classes.findSpecializedClass(readers);
        while (type!=null && ImageReader.class.isAssignableFrom(type)) {
            for (final ImageReader reader : readers) {
                if (type.equals(reader.getClass())) {
                    final ImageReaderSpi provider = reader.getOriginatingProvider(); // May be null
                    for (final Tile tile : tiles) {
                        /*
                         * We give precedence to ImageReaderSpi.equals(ImageReaderSpi) over
                         * ImageReaderSpi.isOwnReader(ImageReader) because we need consistency
                         * with the 'readers' HashMap. However the later will be used as a
                         * fallback if no exact match has been found.
                         */
                        final ImageReaderSpi candidate = tile.getImageReaderSpi(); // Never null
                        if (candidate.equals(provider)) {
                            return tile;
                        }
                        if (fallback == null && candidate.isOwnReader(reader)) {
                            fallback = tile;
                        }
                    }
                }
            }
            type = type.getSuperclass();
        }
        return fallback;
    }

    /**
     * Returns an array of locales that may be used to localize warning listeners. The default
     * implementations returns the union of the locales supported by this reader and every
     * {@linkplain Tile#getImageReader tile readers}.
     *
     * @return An array of supported locales, or {@code null}.
     */
    @Override
    public Locale[] getAvailableLocales() {
        if (availableLocales == null) {
            final Set<Locale> locales = new LinkedHashSet<>();
            for (final ImageReader reader : readers.getTileReaders()) {
                final Locale[] additional = reader.getAvailableLocales();
                if (additional != null) {
                    for (final Locale locale : additional) {
                        locales.add(locale);
                    }
                }
            }
            if (locales.isEmpty()) {
                return null;
            }
            availableLocales = locales.toArray(new Locale[locales.size()]);
        }
        return availableLocales.clone();
    }

    /**
     * Sets the current locale of this image reader and every
     * {@linkplain Tile#getImageReader tile readers}.
     *
     * @param locale The desired locale, or {@code null}.
     * @throws IllegalArgumentException if {@code locale} is non-null but is not
     *         one of the {@linkplain #getAvailableLocales available locales}.
     */
    @Override
    public void setLocale(final Locale locale) throws IllegalArgumentException {
        super.setLocale(locale); // May thrown an exception.
        readers.setLocale(locale);
    }

    /**
     * Returns the number of images, not including thumbnails.
     *
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IOException {
        return (input instanceof TileManager[]) ? ((TileManager[]) input).length : 0;
    }

    /**
     * Returns {@code true} if there is more than one tile for the given image index.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return {@code true} If there is at least two tiles.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public boolean isImageTiled(final int imageIndex) throws IOException {
        return getTileManager(imageIndex).isImageTiled();
    }

    /**
     * Returns the width in pixels of the given image within the input source.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The width of the image.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        return getTileManager(imageIndex).getRegion().width;
    }

    /**
     * Returns the height in pixels of the given image within the input source.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The height of the image.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        return getTileManager(imageIndex).getRegion().height;
    }

    /**
     * Returns the width of a tile in the given image.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The width of a tile.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public int getTileWidth(final int imageIndex) throws IOException {
        return getTileManager(imageIndex).getTileSize().width;
    }

    /**
     * Returns the height of a tile in the given image.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The height of a tile.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public int getTileHeight(final int imageIndex) throws IOException {
        return getTileManager(imageIndex).getTileSize().height;
    }

    /**
     * Returns {@code true} if every image reader uses the default implementation for the given
     * method. Some methods may avoid costly file seeking when this method returns {@code true}.
     * <p>
     * This method always returns {@code true} if there is no tiles.
     */
    private boolean useDefaultImplementation(final String methodName, final Class<?>[] parameterTypes) {
        for (final ImageReader reader : readers.getTileReaders()) {
            Class<?> type = reader.getClass();
            try {
                type = type.getMethod(methodName, parameterTypes).getDeclaringClass();
            } catch (NoSuchMethodException e) {
                Logging.unexpectedException(LOGGER, MosaicImageReader.class, "useDefaultImplementation", e);
                return false; // Conservative value.
            }
            if (!type.equals(ImageReader.class)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if there is only one tile in the given collection and if that singleton
     * tile encloses fully the given source region. In such case, {@code MosaicImageReader} can
     * delegates directly the reading process to the reader used by that tile.
     *
     * @param  tiles The tile collection.
     * @param  sourceRegion The source region to be read, as computed by {@link #getSourceRegion}.
     * @return {@code true} if {@code MosaicImageReader} can delegates the reading process to the
     *         singleton tile contained in the given collection.
     * @throws IOException If an I/O operation was required and failed.
     */
    private static boolean canDelegate(final Collection<Tile> tiles, final Rectangle sourceRegion)
            throws IOException
    {
        final Iterator<Tile> it = tiles.iterator();
        if (it.hasNext()) {
            final Tile tile = it.next();
            if (!it.hasNext()) {
                return tile.getRegion().contains(sourceRegion);
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the storage format of the given image places no inherent impediment
     * on random access to pixels. The default implementation returns {@code true} if the input of
     * every tiles is a {@link File} and {@code isRandomAccessEasy} returned {@code true} for all
     * tile readers.
     *
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        if (useDefaultImplementation("isRandomAccessEasy", INTEGER_ARGUMENTS)) {
            return super.isRandomAccessEasy(imageIndex);
        }
        for (final Tile tile : getTileManager(imageIndex).getTiles()) {
            final Object input = tile.getInput();
            if (!(input instanceof File)) {
                return false;
            }
            final ImageReader reader = getTileReader(tile);
            if (!reader.isRandomAccessEasy(tile.getImageIndex())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the aspect ratio. If all tiles have the same aspect ratio, then that ratio is
     * returned. Otherwise the {@linkplain ImageReader#getAspectRatio default value} is returned.
     *
     * @param  imageIndex The index of the image to be queried.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public float getAspectRatio(final int imageIndex) throws IOException {
        if (!useDefaultImplementation("getAspectRatio", INTEGER_ARGUMENTS)) {
            float ratio = Float.NaN;
            for (final Tile tile : getTileManager(imageIndex).getTiles()) {
                final ImageReader reader = tile.getImageReader(this, true, ignoreMetadata);
                final float candidate = reader.getAspectRatio(tile.getImageIndex());
                if (candidate == ratio || Float.isNaN(candidate)) {
                    // Same ratio or unspecified ratio.
                    continue;
                }
                if (!Float.isNaN(ratio)) {
                    // The ratio is different for different tile. Fall back on default.
                    return super.getAspectRatio(imageIndex);
                }
                ratio = candidate;
            }
            if (!Float.isNaN(ratio)) {
                return ratio;
            }
        }
        return super.getAspectRatio(imageIndex);
    }

    /**
     * Returns the image type policy from the specified parameter.
     * Fallback on the default policy if the parameter to not specify any.
     */
    private ImageTypePolicy getImageTypePolicy(final ImageReadParam param) {
        if (param instanceof MosaicImageReadParam) {
            final ImageTypePolicy policy = ((MosaicImageReadParam) param).getImageTypePolicy();
            if (policy != null) {
                return policy;
            }
        }
        return getDefaultImageTypePolicy();
    }

    /**
     * Returns the policy for {@link #getImageTypes computing image types}. This is also
     * the policy used by {@linkplain #read read} method when none has been explicitly
     * {@linkplain MosaicImageReadParam#setImageTypePolicy set in read parameters}.
     * <p>
     * The default implementation makes the following choice based on the number of
     * {@linkplain #getTileReaderSpis reader providers}:
     * <p>
     * <ul>
     *   <li>{@link ImageTypePolicy#SUPPORTED_BY_ALL SUPPORTED_BY_ALL} if two or more</li>
     *   <li>{@link ImageTypePolicy#SUPPORTED_BY_FIRST SUPPORTED_BY_FIRST} if exactly one</li>
     *   <li>{@link ImageTypePolicy#ALWAYS_ARGB ALWAYS_ARGB} if none.</li>
     * </ul>
     * <p>
     * Note that {@link ImageTypePolicy#SUPPORTED_BY_FIRST SUPPORTED_BY_FIRST} is <strong>not</strong>
     * a really safe choice even if there is only one provider, because the image type can also
     * depends on {@linkplain Tile#getInput tile input}. However the safest choice in all cases
     * ({@link ImageTypePolicy#SUPPORTED_BY_ALL SUPPORTED_BY_ALL}) is costly and often not
     * necessary. The current implementation is a compromise between safety and performance.
     * <p>
     * If Java assertions are enabled, this reader will verify that {@code SUPPORTED_BY_ONE}
     * and  {@code SUPPORTED_BY_FIRST} produce the same result than {@code SUPPORTED_BY_ALL}.
     * <p>
     * Subclasses can override this method if they want a different policy.
     *
     * @return The default image type policy.
     */
    public ImageTypePolicy getDefaultImageTypePolicy() {
        switch (readers.providers.size()) {
            default: return ImageTypePolicy.SUPPORTED_BY_ALL;
            case 1:  return ImageTypePolicy.SUPPORTED_BY_FIRST;
            case 0:  return ImageTypePolicy.ALWAYS_ARGB;
        }
    }

    /**
     * Returns type image type specifier for policy of pre-defined types.
     * More types may be added in future Geotk versions.
     */
    private static ImageTypeSpecifier getPredefinedImageType(final ImageTypePolicy policy) {
        final int type;
        switch (policy) {
            case ALWAYS_ARGB: type = BufferedImage.TYPE_INT_ARGB; break;
            case ALWAYS_RGB:  type = BufferedImage.TYPE_INT_RGB;  break;
            default: throw new IllegalArgumentException(policy.toString());
        }
        return ImageTypeSpecifier.createFromBufferedImageType(type);
    }

    /**
     * Returns an image type which most closely represents the "raw" internal format of the image.
     * The default implementation depends on the {@linkplain #getDefaultImageTypePolicy default
     * image type policy}:
     * <p>
     * <ul>
     *   <li>For {@link ImageTypePolicy#SUPPORTED_BY_ONE SUPPORTED_BY_ONE}, this method delegates
     *       directly to the reader of an arbitrary tile (typically the first one).</li>
     *   <li>For {@link ImageTypePolicy#SUPPORTED_BY_ALL SUPPORTED_BY_ALL}, this method invokes
     *       {@code getRawImageType} for every tile readers, omits the types that are not declared
     *       in <code>{@linkplain ImageReader#getImageTypes getImageTypes}(imageIndex)</code> for
     *       every tile readers, and returns the most common remaining value. If none is found,
     *       then some {@linkplain ImageReader#getRawImageType default specifier} is returned.</li>
     * </ul>
     *
     * @param  imageIndex The image index, from 0 inclusive to {@link #getNumImages} exclusive.
     * @return A raw image type specifier.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        ImageTypeSpecifier type = getRawImageType(null, getDefaultImageTypePolicy(), imageIndex);
        if (type == null) {
            type = super.getRawImageType(imageIndex);
        }
        return type;
    }

    /**
     * Implementation of {@link #getRawImageType(int)}, which can be restricted to a subset
     * of the tile collection.
     *
     * @param tiles      The tiles to use for computing the type, or {@code null} for all of them.
     * @param policy     The image type policy.
     * @param imageIndex The index of the requested image.
     * @return A raw image type specifier, or {@code null} if none were found.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @SuppressWarnings("fallthrough")
    private ImageTypeSpecifier getRawImageType(Collection<Tile> tiles, final ImageTypePolicy policy,
            final int imageIndex) throws IOException
    {
        ImageTypeSpecifier type = null;
        switch (policy) {
            default: {
                type = getPredefinedImageType(policy);
                break;
            }
            case SUPPORTED_BY_FIRST: {
                if (cachedImageType != null) {
                    return cachedImageType;
                }
                // Fall through
            }
            case SUPPORTED_BY_ONE: {
                if (tiles == null) {
                    tiles = getTileManager(imageIndex).getTiles();
                }
                final Tile tile = getSpecificTile(tiles);
                if (tile != null) {
                    type = getTileReader(tile).getRawImageType(tile.getImageIndex());
                    assert type == null || // Should never be null with non-broken ImageReader.
                           type.equals(getRawImageType(tiles)) : incompatibleImageType(tile);
                }
                break;
            }
            case SUPPORTED_BY_ALL: {
                if (tiles == null) {
                    tiles = getTileManager(imageIndex).getTiles();
                }
                type = getRawImageType(tiles);
                break;
            }
        }
        if (policy == ImageTypePolicy.SUPPORTED_BY_FIRST) {
            cachedImageType = type;
        }
        return type;
    }

    /**
     * Returns an image type which most closely represents the "raw" internal format of the
     * given set of tiles. If none is found, returns {@code null}.
     * <p>
     * If there is more than one supported types, this method will give preference to the type
     * having transparency. We do that because we have no guarantee that a tile exists for every
     * area in an image to be read, and the empty area typically need to remain transparent.
     *
     * @param  tiles The tiles to iterate over.
     * @return A raw image type specifier acceptable for all tiles, or {@code null} if none.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    private ImageTypeSpecifier getRawImageType(final Collection<Tile> tiles) throws IOException {
        // Gets the list of every raw image types, with the most frequent type first.
        final Set<ImageTypeSpecifier> rawTypes = new FrequencySortedSet<>(true);
        final Set<ImageTypeSpecifier> allowed = getImageTypes(tiles, rawTypes);
        rawTypes.retainAll(allowed);
        boolean transparent = true;
        do {
            Iterator<ImageTypeSpecifier> it = rawTypes.iterator();
            while (it.hasNext()) {
                final ImageTypeSpecifier type = it.next();
                if (!transparent || isTransparent(type)) {
                    return type;
                }
            }
            // No raw image reader type. Returns the first allowed type even if it is not "raw".
            it = allowed.iterator();
            while (it.hasNext()) {
                final ImageTypeSpecifier type = it.next();
                if (!transparent || isTransparent(type)) {
                    return type;
                }
            }
            // If no type was found and if we were looking for a transparent
            // type, searches again for a type no matter its transparency.
        } while ((transparent = !transparent) == false);
        return null;
    }

    /**
     * Returns the possible image types to which the given image may be decoded. This method
     * invokes <code>{@linkplain ImageReader#getImageTypes getImageTypes}(imageIndex)</code>
     * on every tile readers and returns the intersection of all sets (i.e. only the types
     * that are supported by every readers).
     *
     * @param  tiles       The tiles to iterate over.
     * @param  rawTypes    If non-null, a collection where to store the raw image types.
     *                     No filtering is applied on this collection.
     * @return The image type specifiers that are common to all tiles.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    private Set<ImageTypeSpecifier> getImageTypes(final Collection<Tile> tiles,
                                                  final Collection<ImageTypeSpecifier> rawTypes)
            throws IOException
    {
        int pass = 0;
        final Map<ImageTypeSpecifier,Integer> types = new LinkedHashMap<>();
        for (final Tile tile : tiles) {
            final ImageReader reader = getTileReader(tile);
            final int imageIndex = tile.getImageIndex();
            if (rawTypes != null) {
                rawTypes.add(reader.getRawImageType(imageIndex));
            }
            final Iterator<ImageTypeSpecifier> toAdd = reader.getImageTypes(imageIndex);
            while (toAdd.hasNext()) {
                final ImageTypeSpecifier type = toAdd.next();
                if (type == null) {
                    // The type should never be null for implementations compliant with ImageReader
                    // contract. However experience shows that broken implementations are not uncommon.
                    continue;
                }
                final Integer old = types.put(type, pass);
                if (old == null && pass != 0) {
                    // Just added a type that did not exists in previous tiles, so remove it.
                    types.remove(type);
                }
            }
            // Remove all previous types not found in this pass.
            for (final Iterator<Integer> it=types.values().iterator(); it.hasNext();) {
                if (it.next().intValue() != pass) {
                    it.remove();
                }
            }
            pass++;
        }
        /*
         * Checks for broken ImageReader implementation. Following should never happen,
         * but unfortunately experience show that this error is not uncommon.
         */
        if (rawTypes != null && rawTypes.remove(null)) {
            log("getRawImageType", new LogRecord(Level.WARNING, "Tile.getImageReader().getRawImageType() == null"));
        }
        return types.keySet();
    }

    /**
     * Returns possible image types to which the given image may be decoded. The default
     * implementation depends on the {@linkplain #getDefaultImageTypePolicy default image
     * type policy}:
     * <p>
     * <ul>
     *   <li>For {@link ImageTypePolicy#SUPPORTED_BY_ONE SUPPORTED_BY_ONE}, this method delegates
     *       directly to the reader of an arbitrary tile (typically the first one).</li>
     *   <li>For {@link ImageTypePolicy#SUPPORTED_BY_ALL SUPPORTED_BY_ALL}, this method invokes
     *       <code>{@linkplain ImageReader#getImageTypes getImageTypes}(imageIndex)</code> on
     *       every tile readers and returns the intersection of all sets (i.e. only the types
     *       that are supported by every readers).</li>
     * </ul>
     *
     * @param  imageIndex  The image index, from 0 inclusive to {@link #getNumImages} exclusive.
     * @return The image type specifiers that are common to all tiles.
     * @throws IOException If an error occurs reading the information from the input source.
     */
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        Iterator<ImageTypeSpecifier> types;
        final ImageTypePolicy policy = getDefaultImageTypePolicy();
        switch (policy) {
            default: {
                types = Collections.singleton(getPredefinedImageType(policy)).iterator();
                break;
            }
            case SUPPORTED_BY_FIRST:
            case SUPPORTED_BY_ONE: {
                final Collection<Tile> tiles = getTileManager(imageIndex).getTiles();
                final Tile tile = getSpecificTile(tiles);
                if (tile == null) {
                    final Collection<ImageTypeSpecifier> t = Collections.emptySet();
                    return t.iterator();
                }
                types = getTileReader(tile).getImageTypes(tile.getImageIndex());
                assert (types = containsAll(getImageTypes(tiles, null), types)) != null : incompatibleImageType(tile);
                break;
            }
            case SUPPORTED_BY_ALL: {
                final Collection<Tile> tiles = getTileManager(imageIndex).getTiles();
                types = getImageTypes(tiles, null).iterator();
                break;
            }
        }
        return types;
    }

    /**
     * Helper method for assertions only. This is used by {@link #getImageTypes(int)}.
     * Since the given iterator is consumed, a new iterator over the same elements is
     * returned by this method.
     */
    private static Iterator<ImageTypeSpecifier> containsAll(
            final Collection<ImageTypeSpecifier> expected, final Iterator<ImageTypeSpecifier> types)
    {
        final List<ImageTypeSpecifier> asList = new ArrayList<>(expected.size());
        while (types.hasNext()) {
            final ImageTypeSpecifier type = types.next();
            if (type != null) { // See the comment above about broken ImageReader implementations.
                asList.add(type);
            }
        }
        return expected.containsAll(asList) ? asList.iterator() : null;
    }

    /**
     * Returns {@code true} if the given type has transparency.
     */
    private static boolean isTransparent(final ImageTypeSpecifier type) {
        return type.getColorModel().getTransparency() != ColorModel.OPAQUE;
    }

    /**
     * Helper method for assertions only.
     */
    private static String incompatibleImageType(final Tile tile) {
        return "Image type computed by " + ImageTypePolicy.SUPPORTED_BY_ONE +
                " policy using " +  tile + " is incompatible with type computed by " +
                ImageTypePolicy.SUPPORTED_BY_ALL + " policy.";
    }

    /**
     * Returns default parameters appropriate for this format.
     */
    @Override
    public MosaicImageReadParam getDefaultReadParam() {
        return new MosaicImageReadParam(this);
    }

    /**
     * Returns the metadata associated with the input source as a whole, or {@code null}.
     * The default implementation returns {@code null} in all cases.
     *
     * {@note A previous implementation was iterating over every tiles and attempted to merge
     * the metadata using the <code>IIOMetadata.mergeTree(String, Node)</code> method. However
     * this was extremely slow on large mosaics, and the result was usually not the expected
     * one since the merge operation is hard to implement correctly.}
     *
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    /**
     * Returns the metadata associated with the given image, or {@code null} if none.
     * The default implementation returns an instance of {@link SpatialMetadata}
     * with a {@code "RectifiedGridDomain"} node inferred from the information
     * returned by {@link TileManager#getGridGeometry()}.
     *
     * @param  imageIndex the index of the image whose metadata is to be retrieved.
     * @return The metadata, or {@code null}.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public IIOMetadata getImageMetadata(final int imageIndex) throws IOException {
        IIOMetadata md = null;
        if (metadata != null) {
            md = metadata[imageIndex];
        }
        if (md == null) {
            final TileManager manager = getTileManager(imageIndex);
            final ImageGeometry geom = manager.getGridGeometry();
            if (geom != null) {
                final SpatialMetadata sp = new SpatialMetadata(false, this, null);
                final GridDomainAccessor accessor = new GridDomainAccessor(sp);
                accessor.setAll(geom.getGridToCRS(), geom.getExtent(), null, PixelOrientation.UPPER_LEFT);
                /*
                 * Add the CRS, if the tile manager has been created from a directory or a file
                 * is associated with a PRJ file.
                 */
                Path file = manager.getSourceFile();
                if (file != null) {
                    file = IOUtilities.changeExtension(file, "prj");
                    if (Files.isRegularFile(file)) {
                        final ReferencingBuilder helper = new ReferencingBuilder(sp);
                        helper.setCoordinateReferenceSystem(PrjFiles.read(file));
                    }
                }
                /*
                 * Cache the metadata.
                 */
                if (metadata == null) {
                    metadata = new SpatialMetadata[getNumImages(true)];
                }
                metadata[imageIndex] = sp;
                md = sp;
            }
        }
        return md;
    }

    /**
     * Reads the image indexed by {@code imageIndex} using a supplied parameters. While optional,
     * it is strongly recommended to supply parameters as an instance of {@link MosaicImageReadParam}
     * on which {@code param.setSubsamplingChangeAllowed(true)} has been invoked.
     * <p>
     * If the above recommendation has been followed, then the supplied {@code param} object will
     * be modified by call to this method since the subsampling effectively used will be written
     * back in the given parameters.
     *
     * @param  imageIndex The index of the image to be retrieved.
     * @param  param The parameters used to control the reading process, or {@code null}.
     *         An instance of {@link MosaicImageReadParam} is expected but not required.
     * @return The desired portion of the image.
     * @throws IOException if an error occurs during reading.
     *
     * @see MosaicImageReadParam
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        clearAbortRequest();
        processImageStarted(imageIndex);
        final Dimension subsampling = new Dimension(1,1);
        boolean subsamplingChangeAllowed = false;
        MosaicImageReadParam mosaicParam = null;
        boolean nullForEmptyImage = false;
        if (param != null) {
            subsampling.width  = param.getSourceXSubsampling();
            subsampling.height = param.getSourceYSubsampling();
            if (param instanceof MosaicImageReadParam) {
                mosaicParam = (MosaicImageReadParam) param;
                subsamplingChangeAllowed = mosaicParam.isSubsamplingChangeAllowed();
                nullForEmptyImage = mosaicParam.getNullForEmptyImage();
            }
            // Note: we don't extract subsampling offsets because they will be taken in account
            //       in the 'sourceRegion' to be calculated by ImageReader.computeRegions(...).
        }
        final int srcWidth  = getWidth (imageIndex);
        final int srcHeight = getHeight(imageIndex);
        final Rectangle sourceRegion = getSourceRegion(param, srcWidth, srcHeight);
        final TileManager manager = getTileManager(imageIndex);
        final Collection<Tile> tiles = manager.getTiles(sourceRegion, subsampling, subsamplingChangeAllowed);
        if (nullForEmptyImage && tiles.isEmpty()) {
            processImageComplete();
            return null;
        }
        /*
         * If the subsampling changed as a result of TileManager.getTiles(...) call,
         * stores the new subsampling values in the parameters. Note that the source
         * region will need to be computed again, which we will do later.
         */
        final int xSubsampling = subsampling.width;
        final int ySubsampling = subsampling.height;
        if (subsamplingChangeAllowed) {
            if (param.getSourceXSubsampling() != xSubsampling ||
                param.getSourceYSubsampling() != ySubsampling)
            {
                final int xOffset = param.getSubsamplingXOffset() % xSubsampling;
                final int yOffset = param.getSubsamplingYOffset() % ySubsampling;
                param.setSourceSubsampling(xSubsampling, ySubsampling, xOffset, yOffset);
            } else {
                subsamplingChangeAllowed = false;
            }
        }
        /*
         * If there is exactly one image to read, we will left the image reference to null. It will
         * be understood later as an indication to delegate directly to the sole image reader as an
         * optimization (no search for raw data type). Otherwise, we need to create the destination
         * image here. Note that this is the only image ever to be created during a mosaic read,
         * unless some underlying ImageReader do not honor our ImageReadParam.setDestination(image)
         * setting. In such case, the default behavior is to thrown an exception.
         */
        BufferedImage image = null;
        final Rectangle destRegion;
        final Point destinationOffset;
        ImageTypePolicy policy = null;
        if (canDelegate(tiles, sourceRegion) && (policy = getImageTypePolicy(param)).canDelegate) {
            destRegion = null;
            if (subsamplingChangeAllowed) {
                sourceRegion.setBounds(getSourceRegion(param, srcWidth, srcHeight));
            }
            destinationOffset = (param != null) ? param.getDestinationOffset() : new Point();
        } else {
            if (param != null) {
                image = param.getDestination();
            }
            destRegion = new Rectangle(); // Computed by the following method call.
            computeRegions(param, srcWidth, srcHeight, image, sourceRegion, destRegion);
            if (image == null) {
                /*
                 * If no image was explicitly specified, creates one using a raw image type
                 * acceptable for all tiles. An exception will be thrown if no such raw type
                 * was found. Note that this fallback may be a little bit costly since it may
                 * imply to open, close and reopen later some streams.
                 */
                ImageTypeSpecifier imageType = null;
                if (param != null) {
                    imageType = param.getDestinationType();
                }
                if (imageType == null) {
                    if (policy == null) {
                        policy = getImageTypePolicy(param);
                    }
                    imageType = getRawImageType(tiles, policy, imageIndex);
                    if (imageType == null) {
                        /*
                         * This case occurs if the tiles collection is empty.  We want to produce
                         * a fully transparent (or empty) image in such case. Remember that tiles
                         * are not required to exist everywhere in the mosaic bounds,  so the set
                         * of tiles in a particular sub-area is allowed to be empty.
                         */
                        imageType = getRawImageType(imageIndex);
                    }
                }
                final int width  = destRegion.x + destRegion.width;
                final int height = destRegion.y + destRegion.height;
                image = imageType.createBufferedImage(width, height);
                computeRegions(param, srcWidth, srcHeight, image, sourceRegion, destRegion);
            }
            destinationOffset = destRegion.getLocation();
        }
        /*
         * Gets a MosaicImageReadParam instance to be used for caching Tile parameters. There is
         * no need to invokes 'getDefaultReadParam()' since we are interested only in the cache
         * that MosaicImageReadParam provide.
         */
        MosaicController controller = null;
        if (mosaicParam == null) {
            mosaicParam = new MosaicImageReadParam();
        } else if (mosaicParam.hasController()) {
            final IIOParamController candidate = mosaicParam.getController();
            if (candidate instanceof MosaicController) {
                controller = (MosaicController) candidate;
            }
        }
        /*
         * If logging are enabled, we will format the tiles that we read in a table and logs
         * the table as one log record after the actual reading. If there is nothing to log,
         * then the table will be left to null. If non-null, the table will be completed in
         * the loop below.
         */
        final TableWriter table;
        final long startTime;
        int status; // 0=success, 1=cancelled, 2=failure. Used for logging purpose only.
        if (isLoggable()) {
            table = new TableWriter(null, TableWriter.SINGLE_VERTICAL_LINE);
            table.writeHorizontalSeparator();
            table.write("Reader\tTile\tIndex\tSize\tSource\tDestination\tSubsampling");
            table.writeHorizontalSeparator();
            startTime = System.nanoTime();
            status = 2; // To be set to 0 on success.
        } else {
            table = null;
            startTime = 0;
            status = 0;
        }
        /*
         * Now read every tiles... The log record will be logged in the "finally" block in
         * every case, in order to help debugging in case of failure.
         */
        try {
            for (final Tile tile : tiles) {
                if (abortRequested()) {
                    processReadAborted();
                    status = 1;
                    break;
                }
                final Rectangle tileRegion = tile.getAbsoluteRegion();
                final Rectangle regionToRead = tileRegion.intersection(sourceRegion);
                /*
                 * Computes the location of the region to read relative to the source region
                 * requested by the user, and make sure that this location is a multiple of
                 * subsampling (if any). The region to read may become bigger by one pixel
                 * (in tile units) as a result of this calculation.
                 */
                int xOffset = (regionToRead.x - sourceRegion.x) % xSubsampling;
                int yOffset = (regionToRead.y - sourceRegion.y) % ySubsampling;
                if (xOffset != 0) {
                    regionToRead.x     -= xOffset;
                    regionToRead.width += xOffset;
                    if (regionToRead.x < tileRegion.x) {
                        regionToRead.x = tileRegion.x;
                        if (regionToRead.width > tileRegion.width) {
                            regionToRead.width = tileRegion.width;
                        }
                    }
                }
                if (yOffset != 0) {
                    regionToRead.y      -= yOffset;
                    regionToRead.height += yOffset;
                    if (regionToRead.y < tileRegion.y) {
                        regionToRead.y = tileRegion.y;
                        if (regionToRead.height > tileRegion.height) {
                            regionToRead.height = tileRegion.height;
                        }
                    }
                }
                /*
                 * Now that the offset is a multiple of subsampling, computes the destination offset.
                 * Then translate the region to read from "this image reader" space to "tile" space.
                 */
                if (destRegion != null) {
                    xOffset = (regionToRead.x - sourceRegion.x) / xSubsampling;
                    yOffset = (regionToRead.y - sourceRegion.y) / ySubsampling;
                    destinationOffset.x = destRegion.x + xOffset;
                    destinationOffset.y = destRegion.y + yOffset;
                }
                assert tileRegion.contains(regionToRead) : regionToRead;
                regionToRead.translate(-tileRegion.x, -tileRegion.y);
                /*
                 * Sets the parameters to be given to the tile reader. We don't use any subsampling
                 * offset because it has already been calculated in the region to read. Note that
                 * the tile subsampling should be a divisor of image subsampling; this condition must
                 * have been checked by the tile manager when it selected the tiles to be returned.
                 */
                subsampling.setSize(tile.getSubsampling());
                assert xSubsampling % subsampling.width  == 0 : subsampling;
                assert ySubsampling % subsampling.height == 0 : subsampling;
                /*
                 * Transform the region to read from "absolute" coordinates to "relative to tile"
                 * coordinates. We want to round x and y toward negative infinity, which require
                 * special processing for negative numbers since integer arithmetic round toward
                 * zero. The xOffset and yOffset values are the remainding of the division which
                 * will be added to the width and height in order to get (xmax, ymax) unchanged.
                 */
                xOffset = regionToRead.x % subsampling.width;
                yOffset = regionToRead.y % subsampling.height;
                regionToRead.x /= subsampling.width;
                regionToRead.y /= subsampling.height;
                if (xOffset < 0) {
                    regionToRead.x--;
                    xOffset = subsampling.width - xOffset;
                }
                if (yOffset < 0) {
                    regionToRead.y--;
                    yOffset = subsampling.height - yOffset;
                }
                regionToRead.width  += xOffset;
                regionToRead.height += yOffset;
                regionToRead.width  /= subsampling.width;
                regionToRead.height /= subsampling.height;
                if (regionToRead.isEmpty()) {
                    /*
                     * Should never happen if the TileManager worked perfectly well. But in practice,
                     * different implementations may be of unequal quality. In many cases, one of the
                     * above assertions will fail before we reach this point. This condition avoid an
                     * "empty region" exception in ImageReader when assertions are disabled.
                     */
                    continue;
                }
                subsampling.width  = xSubsampling / subsampling.width;
                subsampling.height = ySubsampling / subsampling.height;
                final int tileIndex = tile.getImageIndex();
                if (table != null) {
                    /*
                     * Add one row in the table if we are logging.
                     */
                    table.write(Formats.getDisplayName(tile.getImageReaderSpi()));
                    table.nextColumn();
                    table.write(tile.getInputName());
                    table.nextColumn();
                    table.write(String.valueOf(tileIndex));
                    format(table, regionToRead.width,  regionToRead.height);
                    format(table, regionToRead.x,      regionToRead.y);
                    format(table, destinationOffset.x, destinationOffset.y);
                    format(table, subsampling.width,   subsampling.height);
                    table.nextLine();
                }
                final ImageReader reader = getTileReader(tile);
                final ImageReadParam tileParam = mosaicParam.getCachedTileParameters(reader);
                final BufferedImage output;
                try {
                    tileParam.setDestinationType(null);
                    if (manager.canWriteInPlace(reader.getOriginatingProvider())) {
                        // Must be after setDestinationType and may be null.
                        tileParam.setDestination(image);
                        tileParam.setDestinationOffset(destinationOffset);
                    }
                    if (tileParam.canSetSourceRenderSize()) {
                        tileParam.setSourceRenderSize(null); // TODO.
                    }
                    tileParam.setSourceRegion(regionToRead);
                    tileParam.setSourceSubsampling(subsampling.width, subsampling.height, 0, 0);
                    if (controller != null) {
                        controller.configure(tile, tileParam);
                    }
                    synchronized (this) {  // Same lock than ImageReader.abort()
                        reading = reader;
                    }
                    output = reader.read(tileIndex, tileParam);
                } finally {
                    synchronized (this) {  // Same lock than ImageReader.abort()
                        reading = null;
                    }
                    // Cleanup because the parameters are cached.
                    tileParam.setDestination(null);
                    tileParam.setSourceRegion(null);
                    tileParam.setDestinationOffset(new Point());
                }
                if (image == null) {
                    image = output;
                } else if (output != image) {
                    /*
                     * The read operation ignored our destination image. Copy the data (slow,
                     * consume memory). Note that the sample and color models should be the
                     * same if we choose correctly the raw image type in the above code.
                     */
                    Raster data = output.getRaster();
                    data = Raster.createRaster(data.getSampleModel(), data.getDataBuffer(), destinationOffset);
                    image.setData(data);
                }
            }
            status = 0; // Success.
        } finally {
            /*
             * Reading is finished, aborted or an exception has been thrown.
             * Logs what we have been able to do up to date.
             */
            if (table != null) {
                final long duration = System.nanoTime() - startTime;
                Level level = logLevel;
                if (level == null) {
                    level = PerformanceLevel.forDuration(duration, TimeUnit.NANOSECONDS);
                }
                table.writeHorizontalSeparator();
                final String message = Loggings.getResources(locale).getString(Loggings.Keys.LoadingRegion_6,
                        new Number[] {
                            sourceRegion.x, sourceRegion.x + sourceRegion.width  - 1,
                            sourceRegion.y, sourceRegion.y + sourceRegion.height - 1,
                            duration / 1E+6, status})
                        + System.lineSeparator() + table;
                log("read", new LogRecord(level, message));
            }
        }
        processImageComplete();
        return image;
    }

    /**
     * Logs the given record to the given logger.
     */
    private static void log(final String method, final LogRecord record) {
        record.setSourceClassName(MosaicImageReader.class.getName());
        record.setSourceMethodName(method);
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Reads the tile indicated by the {@code tileX} and {@code tileY} arguments.
     *
     * @param  imageIndex The index of the image to be retrieved.
     * @param  tileX The column index (starting with 0) of the tile to be retrieved.
     * @param  tileY The row index (starting with 0) of the tile to be retrieved.
     * @return The desired tile.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public BufferedImage readTile(final int imageIndex, final int tileX, final int tileY)
            throws IOException
    {
        final int width  = getTileWidth (imageIndex);
        final int height = getTileHeight(imageIndex);
        final Rectangle sourceRegion = new Rectangle(tileX*width, tileY*height, width, height);
        final ImageReadParam param = getDefaultReadParam();
        param.setSourceRegion(sourceRegion);
        return read(imageIndex, param);
    }

    /**
     * Formats a (x,y) value pair. A call to {@link TableWriter#nextColumn} is performed first.
     */
    private static void format(final TableWriter table, final int x, final int y) {
        table.nextColumn();
        table.write('(');
        table.write(String.valueOf(x));
        table.write(',');
        table.write(String.valueOf(y));
        table.write(')');
    }

    /**
     * Requests that any current read operation be aborted.
     */
    @Override
    public synchronized void abort() {
        super.abort();
        if (reading != null) {
            reading.abort();
        }
    }

    /**
     * Closes any image input streams that may be held by tiles.
     * The streams will be opened again when they will be first needed.
     *
     * @throws IOException if error occurred while closing a stream.
     */
    @Override
    public void close() throws IOException {
        readers.close();
    }

    /**
     * Allows any resources held by this reader to be released. The default implementation
     * closes any image input streams that may be held by tiles, then disposes every
     * {@linkplain Tile#getImageReader tile image readers}.
     */
    @Override
    public void dispose() {
        cachedImageType = null;
        metadata = null;
        input = null;
        try {
            close();
        } catch (IOException e) {
            Logging.unexpectedException(LOGGER, MosaicImageReader.class, "dispose", e);
        }
        readers.dispose();
        super.dispose();
    }

    /**
     * Service provider for {@link MosaicImageReader}. This service provider is not strictly
     * compliant with the Image I/O specification since it cant not work with
     * {@link javax.imageio.stream.ImageInputStream}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.18
     *
     * @since 2.5
     * @module
     */
    public static class Spi extends ImageReaderSpi {
        /**
         * The format names. This array is shared with {@link MosaicImageWriter.Spi}.
         */
        static final String[] NAMES = new String[] {
            "mosaic"
        };

        /**
         * The default instance. There is no instance of this provider registered in the
         * standard {@link javax.imageio.spi.IIORegistry}, because this provider is not
         * strictly compliant with the Image I/O requirement (in particular, the Mosaic
         * Image Reader does not accept {@link javax.imageio.stream.ImageInputStream}).
         * This constant can be used as a replacement.
         */
        public static final Spi DEFAULT = new Spi();

        /**
         * Creates a default provider. This constructor does not set the {@link #inputTypes}
         * field in order to delay loading of {@link Tile} and {@link TileManager} classes
         * as much as possible.
         */
        public Spi() {
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            names           = NAMES;
            pluginClassName = "org.geotoolkit.image.io.mosaic.MosaicImageReader";
        }

        /**
         * Returns the types of objects that may be used as arguments to the
         * {@link MosaicImageReader#setInput(Object)} method. This method
         * initializes the {@link #inputTypes} field when first needed.
         * <p>
         * The types that {@link MosaicImageReader} can accept are the types that
         * {@link TileManagerFactory#createFromObject(Object)} can process.
         *
         * @since 3.18
         */
        @Override
        public synchronized Class<?>[] getInputTypes() {
            if (inputTypes == null) {
                // Initializes the field only when first needed in order to
                // delay the class loading of TileManager and Tile classes.
                inputTypes = new Class<?>[] {
                    TileManager[].class, // Preferred type.
                    TileManager.class,
                    Tile[].class,
                    Collection.class,
                    File.class, // Not present in MosaicImageWriter.Spi.getOutputTypes()
                    Path.class // Not present in MosaicImageWriter.Spi.getOutputTypes()
                };
            }
            return super.getInputTypes();
        }

        /**
         * Returns {@code true} if the image reader can decode the given input. The default
         * implementation returns {@code true} if the given object is an instance assignable
         * to one of the types returned by the {@linkplain #getInputTypes()} implementation
         * of this {@code Spi} class, and other type-specific restrictions are meet (e.g.
         * {@link Collection} contains only instances of {@link Tile}, <i>etc.</i>).
         *
         * @throws IOException If an I/O operation was required and failed.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            if (source instanceof TileManager || source instanceof TileManager[] || source instanceof Tile[]) {
                return true;
            }
            if (source instanceof Collection<?>) {
                for (final Object element : (Collection<?>) source) {
                    if (!(element instanceof Tile)) {
                        return false;
                    }
                }
                return true;
            }
            if (source instanceof File || source instanceof Path) {
                Path path = (source instanceof File) ? ((File) source).toPath() : (Path) source;
                if (Files.isReadable(path)) {
                    if (Files.isRegularFile(path)) {
                        // Maybe a future version could perform a deeper check.
                        return TileManager.SERIALIZED_FILENAME.equals(path.getFileName().toString());
                    } else if (Files.isDirectory(path)) {
                        path = path.resolve(TileManager.SERIALIZED_FILENAME);
                        return Files.isRegularFile(path) &&Files.isReadable(path);
                        /*
                         * While MosaicImageReader can work with a directory containing only
                         * the tiles with their TFW files (without "TileManager.serialized"),
                         * conservatively return 'false' since scanning the directory may be
                         * costly.
                         */
                    }
                }
            }
            return false;
        }

        /**
         * Returns a new {@link MosaicImageReader}.
         *
         * @throws IOException If an I/O operation was required and failed.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new MosaicImageReader(this);
        }

        /**
         * Returns a brief, human-readable description of this service provider.
         *
         * @todo Localize.
         */
        @Override
        public String getDescription(final Locale locale) {
            return "Mosaic Image Reader";
        }
    }
}
