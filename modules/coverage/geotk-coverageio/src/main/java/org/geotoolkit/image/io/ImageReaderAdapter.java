/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;
import java.util.Iterator;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.XArrays;


/**
 * Base class for readers which delegate most of their work to an other {@link ImageReader}.
 * This is used for reusing an existing image reader while adding some extra functionalities,
 * like adding geographic information in {@link SpatialMetadata}.
 * <p>
 * The wrapped image reader is called the {@linkplain #main} reader. The input given to that
 * reader is determined by the {@link #createMainInput()} method - it may or may not be the
 * same input than the one given to this {@code ImageReaderAdapter}. Most methods like
 * {@link #getWidth(int)}, {@link #getHeight(int)} and {@link #read(int)} delegate directly
 * to the main reader.
 * <p>
 * The amount of methods declared in this class is large, but the only new method is
 * {@link #createMainInput()}. All other methods override existing methods declared in
 * parent classes, mostly {@link ImageReader} and {@link SpatialImageReader}.
 *
 * {@section Example}
 * The <cite>World File</cite> format is composed of a classical image file (usually with the
 * {@code ".tiff"}, {@code ".jpg"} or {@code ".png"} extension) together with a small text file
 * containing geolocalization information (often with the {@code ".tfw"} extension) and an other
 * small text file containing projection information ({@code ".prj"} extension). This
 * {@code ImageReaderAdapter} class can be used for wrapping a TIFF image reader, augmented with
 * the parsing of TFW and PRJ files. The information fetched from those files are stored in the
 * {@link SpatialMetadata} object returned by the {@link #getImageMetadata(int)} method in this
 * class. The {@link #createMetadata(int)} protected method provides a convenient hook where to
 * fill the metadata information.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see ImageWriterAdapter
 *
 * @since 3.07
 * @module
 */
@Decorator(ImageReader.class)
public abstract class ImageReaderAdapter extends SpatialImageReader {
    /**
     * The reader to use for reading the image in the classical image format.
     */
    protected final ImageReader main;

    /**
     * The input types accepted by the {@linkplain #main} provider which are also
     * accepted by this provider, or {@code null} if none.
     */
    private final Class<?>[] inputTypes;

    /**
     * {@code true} if the {@link #main} reader accepts inputs of kind {@link ImageInputStream}.
     */
    private final boolean acceptStream;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     * @param main The reader to use for reading the image in the classical image format.
     */
    protected ImageReaderAdapter(final Spi provider, final ImageReader main) {
        super(provider);
        this.main = main;
        Spi.ensureNonNull("main", main);
        if (provider != null) {
            inputTypes   = provider.getMainInputTypes();
            acceptStream = provider.acceptStream;
        } else {
            inputTypes   = null;
            acceptStream = true;
        }
    }

    /**
     * Returns the input to give to the {@linkplain #main} reader. This method is invoked at an
     * arbitrary time after the {@link #setInput(Object, boolean, boolean) setInput} method, when
     * the main reader needs to be used for the first time.
     * <p>
     * The default implementation first checks if the {@linkplain #main} reader can accept directly
     * the {@linkplain #input input} of this reader. If so, then that input is returned with no
     * change. Otherwise the input is wrapped in an {@linkplain ImageInputStream}, which is
     * returned. The input stream assigned to the {@linkplain #main} reader will be closed by
     * the {@link #close()} method.
     * <p>
     * Subclasses can override this method if they want to handle other kind of inputs.
     *
     * @return The input to give to the {@linkplain #main} reader, or {@code null} if this
     *         method can not create such input.
     * @throws IllegalStateException if the {@linkplain #input input} source has not been set.
     * @throws IOException If an error occured while creating the input for the main reader.
     */
    protected Object createMainInput() throws IllegalStateException, IOException {
        final Object input = this.input;
        if (input == null) {
            throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_INPUT));
        }
        if (inputTypes != null) {
            for (final Class<?> type : inputTypes) {
                if (type.isInstance(input)) {
                    return input;
                }
            }
        }
        return acceptStream ? ImageIO.createImageInputStream(input) : null;
    }

    /**
     * Ensures that the input of the {@linkplain #main} reader is initialized.
     */
    private void ensureInputInitialized() throws IOException {
        if (main.getInput() == null) {
            final Object mainInput = createMainInput();
            if (mainInput == null) {
                throw new IIOException(getErrorResources().getString(
                        Errors.Keys.UNKNOW_TYPE_$1, input.getClass()));
            }
            main.setInput(mainInput, seekForwardOnly, ignoreMetadata);
        }
        sync();
    }

    /**
     * Synchronizes the state of this reader with the state of the {@linkplain #main} reader.
     */
    private void sync() {
        minIndex = main.getMinIndex();
    }

    /**
     * Returns the number of images available from the current input source.
     * The default implementation ensures that the {@linkplain #main} reader
     * has its input set, then delegates to that reader.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        ensureInputInitialized();
        final int n = main.getNumImages(allowSearch);
        sync();
        return n;
    }

    /**
     * Returns the number of thumbnail preview images associated with the given image. The default
     * implementation delegates to the {@linkplain #main} reader. Note that the main reader is
     * indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #hasThumbnails(int)
     */
    @Override
    public int getNumThumbnails(final int imageIndex) throws IllegalStateException, IOException {
        checkImageIndex(imageIndex);
        final int n = main.getNumThumbnails(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the number of bands available for the specified image. The default implementation
     * delegates to the {@linkplain #main} reader if it is an instance of {@link SpatialImageReader},
     * or returns the number of bands of the raw image type otherwise.
     * <p>
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n;
        if (main instanceof SpatialImageReader) {
            n = ((SpatialImageReader) main).getNumBands(imageIndex);
        } else {
            n = main.getRawImageType(imageIndex).getNumBands();
        }
        sync();
        return n;
    }

    /**
     * Returns the number of dimension of the image at the given index. The default implementation
     * delegates to the {@linkplain #main} reader if it is an instance of {@link SpatialImageReader},
     * or returns 2 otherwise.
     * <p>
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public int getDimension(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n;
        if (main instanceof SpatialImageReader) {
            n = ((SpatialImageReader) main).getDimension(imageIndex);
        } else {
            n = 2;
        }
        sync();
        return n;
    }

    /**
     * Returns the aspect ratio of the given image. The default implementation delegates to the
     * {@linkplain #main} reader. Note that the main reader is indirectly initialized by an
     * implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #getWidth(int)
     * @see #getHeight(int)
     */
    @Override
    public float getAspectRatio(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final float ar = main.getAspectRatio(imageIndex);
        sync();
        return ar;
    }

    /**
     * Returns the width in pixels of the given image within the input source. The default
     * implementation delegates to the {@linkplain #main} reader. Note that the main reader
     * is indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #getTileWidth(int)
     * @see #getThumbnailWidth(int, int)
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getWidth(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the height in pixels of the given image within the input source. The default
     * implementation delegates to the {@linkplain #main} reader. Note that the main reader
     * is indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #getTileHeight(int)
     * @see #getThumbnailHeight(int, int)
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getHeight(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the width of a tile in the given image. The default implementation delegates to
     * the {@linkplain #main} reader. Note that the main reader is indirectly initialized by an
     * implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #isImageTiled(int)
     * @see #getTileGridXOffset(int)
     */
    @Override
    public int getTileWidth(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getTileWidth(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the height of a tile in the given image. The default implementation delegates to
     * the {@linkplain #main} reader. Note that the main reader is indirectly initialized by an
     * implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #isImageTiled(int)
     * @see #getTileGridYOffset(int)
     */
    @Override
    public int getTileHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getTileHeight(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the X coordinate of the upper-left corner of tile (0, 0) in the given image.
     * The default implementation delegates to the {@linkplain #main} reader. Note that the
     * main reader is indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #isImageTiled(int)
     */
    @Override
    public int getTileGridXOffset(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getTileGridXOffset(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the Y coordinate of the upper-left corner of tile (0, 0) in the given image.
     * The default implementation delegates to the {@linkplain #main} reader. Note that the
     * main reader is indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #isImageTiled(int)
     */
    @Override
    public int getTileGridYOffset(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getTileGridYOffset(imageIndex);
        sync();
        return n;
    }

    /**
     * Returns the width of the thumbnail preview image associated to the given image. The default
     * implementation delegates to the {@linkplain #main} reader. Note that the main reader is
     * indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #hasThumbnails(int)
     * @see #getNumThumbnails(int)
     */
    @Override
    public int getThumbnailWidth(final int imageIndex, int thumbnailIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getThumbnailWidth(imageIndex, thumbnailIndex);
        sync();
        return n;
    }

    /**
     * Returns the height of the thumbnail preview image associated to the given image. The default
     * implementation delegates to the {@linkplain #main} reader. Note that the main reader is
     * indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #hasThumbnails(int)
     * @see #getNumThumbnails(int)
     */
    @Override
    public int getThumbnailHeight(final int imageIndex, int thumbnailIndex) throws IOException {
        checkImageIndex(imageIndex);
        final int n = main.getThumbnailHeight(imageIndex, thumbnailIndex);
        sync();
        return n;
    }

    /**
     * Returns {@code true} if the given image has thumbnail preview images associated with it.
     * The default implementation delegates to the {@linkplain #main} reader. Note that the main
     * reader is indirectly initialized by an implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #getNumThumbnails(int)
     * @see #readerSupportsThumbnails()
     * @see #getThumbnailWidth(int, int)
     * @see #getThumbnailHeight(int, int)
     */
    @Override
    public boolean hasThumbnails(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final boolean b = main.hasThumbnails(imageIndex);
        sync();
        return b;
    }

    /**
     * Returns {@code true} if the image is organized into tiles, that is, equal-sized
     * non-overlapping rectangles. The default implementation delegates to the {@linkplain #main}
     * reader. Note that the main reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     *
     * @see #getTileWidth(int)
     * @see #getTileHeight(int)
     * @see #getTileGridXOffset(int)
     * @see #getTileGridYOffset(int)
     */
    @Override
    public boolean isImageTiled(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final boolean b = main.isImageTiled(imageIndex);
        sync();
        return b;
    }

    /**
     * Returns {@code true} if the storage format of the given image places no inherent impediment
     * on random access to pixels. The default implementation delegates to the {@linkplain #main}
     * reader. Note that the main reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final boolean b = main.isRandomAccessEasy(imageIndex);
        sync();
        return b;
    }

    /**
     * Returns metadata associated with the input source as a whole. The default implementation
     * ensures that the {@linkplain #main} reader is initialized, then delegates to the
     * {@linkplain #createMetadata(int)} method as documented in the
     * {@linkplain SpatialImageReader#getStreamMetadata() super-class method}.
     * <p>
     * Subclasses should consider overriding the {@link #createMetadata(int)} method instead
     * than this one.
     */
    @Override
    public SpatialMetadata getStreamMetadata() throws IOException {
        ensureInputInitialized();
        final SpatialMetadata metadata = super.getStreamMetadata();
        sync();
        return metadata;
    }

    /**
     * Returns metadata associated with the given image. The default implementation ensures
     * (indirectly, though a call to {@link #getNumImages(boolean)}) that the {@linkplain #main}
     * reader is initialized, then delegates to the {@linkplain #createMetadata(int)} method as
     * documented in the {@linkplain SpatialImageReader#getImageMetadata(int) super-class method}.
     * <p>
     * Subclasses should consider overriding the {@link #createMetadata(int)} method instead
     * than this one.
     */
    @Override
    public SpatialMetadata getImageMetadata(final int imageIndex) throws IOException {
        final SpatialMetadata metadata = super.getImageMetadata(imageIndex);
        sync();
        return metadata;
    }

    /**
     * Creates a new stream or image metadata. This method is invoked by the public
     * {@link #getStreamMetadata()} and {@link #getImageMetadata(int)} methods. The
     * default implementation delegates to the corresponding method of the
     * {@linkplain #main} reader, then wraps the result in a {@link SpatialMetadata}
     * object if it is not-null. Otherwise this method returns {@code null}.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if (imageIndex >= 0) {
            final IIOMetadata metadata = main.getImageMetadata(imageIndex);
            if (metadata != null) {
                return new SpatialMetadata(SpatialMetadataFormat.IMAGE, this, metadata);
            }
        } else {
            final IIOMetadata metadata = main.getStreamMetadata();
            if (metadata != null) {
                return new SpatialMetadata(SpatialMetadataFormat.STREAM, this, metadata);
            }
        }
        return null;
    }

    /**
     * Returns a collection of {@link ImageTypeSpecifier} containing possible image types
     * to which the given image may be decoded. The default implementation delegates to the
     * {@linkplain #main} image reader. Note that the {@linkplain #main} reader is indirectly
     * initialized by an implicit call to {@link #getNumImages(boolean)}.
     */
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final Iterator<ImageTypeSpecifier> it = main.getImageTypes(imageIndex);
        sync();
        return it;
    }

    /**
     * Returns an image type specifier indicating the {@link java.awt.image.SampleModel} and
     * {@link java.awt.image.ColorModel} which most closely represents the "raw" internal format
     * of the image. The default implementation delegates to the {@linkplain #main} image reader.
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final ImageTypeSpecifier type = main.getRawImageType(imageIndex);
        sync();
        return type;
    }

    /**
     * Reads the image indexed by {@code imageIndex} using a default {@link ImageReadParam}.
     * The default implementation delegates to the {@linkplain #main} image reader. Note that
     * the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public BufferedImage read(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        final BufferedImage image = main.read(imageIndex);
        sync();
        return image;
    }

    /**
     * Reads the image indexed by {@code imageIndex} using the given parameters.
     * The default implementation delegates to the {@linkplain #main} image reader.
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        checkImageIndex(imageIndex);
        final BufferedImage image = main.read(imageIndex, param);
        sync();
        return image;
    }

    /**
     * Reads the image indexed by {@code imageIndex} as a rendered image.
     * The default implementation delegates to the {@linkplain #main} image reader.
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     */
    @Override
    public RenderedImage readAsRenderedImage(int imageIndex, ImageReadParam param) throws IOException {
        checkImageIndex(imageIndex);
        final RenderedImage image = main.readAsRenderedImage(imageIndex, param);
        sync();
        return image;
    }

    /**
     * Reads the tile indicated by the {@code tileX} and {@code tileY} arguments.
     * The default implementation delegates to the {@linkplain #main} image reader.
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     *
     * @see #isImageTiled(int)
     */
    @Override
    public BufferedImage readTile(final int imageIndex, int tileX, int tileY) throws IOException {
        checkImageIndex(imageIndex);
        final BufferedImage image = main.readTile(imageIndex, tileX, tileY);
        sync();
        return image;
    }

    /**
     * Returns a new raster containing the raw pixel data from the image stream, without any color
     * conversion applied. The default implementation delegates to the {@linkplain #main} image
     * reader. Note that the {@linkplain #main} reader is indirectly initialized by an implicit
     * call to {@link #getNumImages(boolean)}.
     *
     * @see #canReadRaster()
     */
    @Override
    public Raster readRaster(final int imageIndex, final ImageReadParam param) throws IOException {
        checkImageIndex(imageIndex);
        final Raster image = main.readRaster(imageIndex, param);
        sync();
        return image;
    }

    /**
     * Reads the raster indicated by the {@code tileX} and {@code tileY} arguments, without any
     * color conversion applied. The default implementation delegates to the {@linkplain #main}
     * image reader. Note that the {@linkplain #main} reader is indirectly initialized by an
     * implicit call to {@link #getNumImages(boolean)}.
     *
     * @see #isImageTiled(int)
     * @see #canReadRaster()
     */
    @Override
    public Raster readTileRaster(final int imageIndex, int tileX, int tileY) throws IOException {
        checkImageIndex(imageIndex);
        final Raster image = main.readTileRaster(imageIndex, tileX, tileY);
        sync();
        return image;
    }

    /**
     * Returns the thumbnail preview image indexed by {@code thumbnailIndex}, associated with the
     * given image. The default implementation delegates to the {@linkplain #main} image reader.
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     *
     * @see #hasThumbnails(int)
     */
    @Override
    public BufferedImage readThumbnail(int imageIndex, int thumbnailIndex) throws IOException {
        checkImageIndex(imageIndex);
        final BufferedImage image = main.readThumbnail(imageIndex, thumbnailIndex);
        sync();
        return image;
    }

    /**
     * Returns {@code true} if the image format supports thumbnail preview images associated
     * with it. The default implementation delegates to the {@linkplain #main} reader.
     * No input needs to be set for this method.
     *
     * @see #hasThumbnails(int)
     */
    @Override
    public boolean readerSupportsThumbnails() {
        return main.readerSupportsThumbnails();
    }

    /**
     * Returns {@code true} if this plug-in supports reading just a {@link Raster} of pixel data.
     * The default implementation delegates to the {@linkplain #main} reader.
     * No input needs to be set for this method.
     */
    @Override
    public boolean canReadRaster() {
        return main.canReadRaster();
    }

    /**
     * Returns the locales that may be used to localize warning listeners.
     * The default implementation delegates to the {@linkplain #main} reader.
     * No input needs to be set for this method.
     */
    @Override
    public Locale[] getAvailableLocales() {
        return main.getAvailableLocales();
    }

    /**
     * Returns the locale used to localize warning listeners.
     * The default implementation delegates to the {@linkplain #main} reader.
     * No input needs to be set for this method.
     */
    @Override
    public Locale getLocale() {
        return main.getLocale();
    }

    /**
     * Sets the locale used to localize warning listeners.
     * The default implementation delegates to the {@linkplain #main} reader.
     * No input needs to be set for this method.
     */
    @Override
    public void setLocale(final Locale locale) {
        main.setLocale(locale);
        this.locale = locale; // Bypass the check for available locale.
    }

    /**
     * Requests that any current read operation be aborted. The default implementation delegates
     * to both the {@linkplain #main} reader and the super-class method.
     */
    @Override
    public void abort() {
        super.abort();
        main.abort();
    }

    /**
     * Restores the {@code ImageReader} to its initial state. The default implementation
     * delegates to both the {@linkplain #main} reader and the super-class method.
     */
    @Override
    public void reset() {
        super.reset();
        main.reset();
    }

    /**
     * Allows any resources held by this object to be released. The default implementation
     * delegates to both the {@linkplain #main} reader and the super-class method.
     */
    @Override
    public void dispose() {
        super.dispose();
        main.dispose();
    }

    /**
     * Closes the input stream created by {@link #createMainInput()}. This method does nothing
     * if the input used by the {@linkplain #main} reader is the one given by the user to the
     * {@link #setInput(Object, boolean, boolean) setInput} method. Otherwise, if the input of
     * the main reader is an instance of {@link ImageInputStream} or {@link Closeable}, then it
     * is closed.
     * <p>
     * This method is invoked automatically by {@link #setInput(Object, boolean, boolean)
     * setInput(...)}, {@link #reset() reset()}, {@link #dispose() dispose()} or
     * {@link #finalize()} methods and doesn't need to be invoked explicitly.
     * It has protected access only in order to allow overriding by subclasses.
     * Overriding methods shall make sure that {@code super.close()} is invoked
     * even if their own code fail.
     *
     * @throws IOException if an error occured while closing the stream.
     */
    @Override
    protected void close() throws IOException {
        super.close();
        final Object mainInput = main.getInput();
        main.setInput(null);
        if (mainInput != null && mainInput != input) {
            if (mainInput instanceof ImageInputStream) {
                ((ImageInputStream) mainInput).close();
            } else if (mainInput instanceof Closeable) {
                ((Closeable) mainInput).close();
            }
        }
        sync();
    }

    /**
     * Invokes the {@link #close()} method when this reader is garbage-collected.
     * Note that this will actually close the stream only if it has been created
     * by this reader, rather than supplied by the user.
     */
    @Override
    protected void finalize() throws Throwable {
        closeSilently();
        super.finalize();
    }



    /**
     * Service provider interface (SPI) for {@link ImageReaderAdapter}s. The constructor of this
     * class initializes the {@link ImageReaderSpi#inputTypes} field to types that can represent
     * a filename, like {@link File} or {@link URL} (see the table below for the complete list),
     * rather than the usual {@linkplain #STANDARD_INPUT_TYPE standard input type}. The other
     * fields ({@link #names names}, {@link #suffixes suffixes}, {@link #MIMETypes MIMETypes})
     * are set to the same values than the wrapped provider. Because the names are the same by
     * default, an ordering needs to be etablished between this provider and the wrapped one.
     * By default this implementation conservatively gives precedence to the original provider.
     * Subclasses shall override the {@link #onRegistration(ServiceRegistry, Class)} method if
     * they want a different ordering.
     * <p>
     * The table below summarizes the initial values.
     * Those values can be modified by subclass constructors.
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #names}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #suffixes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #MIMETypes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #inputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link File}, {@link URI}, {@link URL}&nbsp;</td>
     * </tr>
     * </table>
     * <p>
     * It is up to subclass constructors to initialize all other instance variables
     * in order to provide working versions of every methods.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @see ImageWriterAdapter.Spi
     *
     * @since 3.07
     * @module
     */
    protected static abstract class Spi extends SpatialImageReader.Spi {
        /**
         * The {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME} value
         * in an array, for assignment to {@code extra[Stream|Image]MetadataFormatNames} fields.
         */
        static final String[] EXTRA_METADATA = {
            SpatialMetadataFormat.FORMAT_NAME
        };

        /**
         * List of legal input types for {@link StreamImageReader}.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            File.class,
            URI.class,
            URL.class,
            String.class // To be interpreted as file path.
        };

        /**
         * The provider of the readers to use for reading the image in the classical image format.
         * This is the provider specified at the construction time.
         */
        protected final ImageReaderSpi main;

        /**
         * {@code true} if the {@link #main} provider accepts inputs of kind {@link ImageInputStream}.
         */
        final boolean acceptStream;

        /**
         * {@code true} if the {@link #main} provider accepts other kind of inputs than
         * {@link ImageInputStream}.
         */
        private final boolean acceptOther;

        /**
         * Creates an {@code ImageReaderAdapter.Spi} wrapping the given provider. The fields are
         * initialized as documented in the <a href="#skip-navbar_top">class javadoc</a>. It is up
         * to the subclass to initialize all other instance variables in order to provide working
         * versions of all methods.
         * <p>
         * For efficienty reasons, the {@code inputTypes} field is initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         *
         * @param main The provider of the readers to use for reading the image in the classical
         *        image format.
         */
        protected Spi(final ImageReaderSpi main) {
            ensureNonNull("main", main);
            this.main  = main;
            names      = main.getFormatNames();
            suffixes   = main.getFileSuffixes();
            MIMETypes  = main.getMIMETypes();
            inputTypes = INPUT_TYPES;
            supportsStandardStreamMetadataFormat = main.isStandardStreamMetadataFormatSupported();
            supportsStandardImageMetadataFormat  = main.isStandardImageMetadataFormatSupported();
            nativeStreamMetadataFormatClassName  = main.getNativeStreamMetadataFormatName();
            nativeImageMetadataFormatClassName   = main.getNativeImageMetadataFormatName();
            extraStreamMetadataFormatClassNames  = XArrays.concatenate(
                    main.getExtraStreamMetadataFormatNames(), EXTRA_METADATA);
            extraImageMetadataFormatClassNames  = XArrays.concatenate(
                    main.getExtraImageMetadataFormatNames(), EXTRA_METADATA);
            boolean acceptStream = false;
            boolean acceptOther  = false;
            for (final Class<?> type : main.getInputTypes()) {
                if (type.isAssignableFrom(ImageInputStream.class)) {
                    acceptStream = true;
                } else {
                    acceptOther = true;
                }
            }
            this.acceptStream = acceptStream;
            this.acceptOther  = acceptOther;
        }

        /**
         * Creates an {@code ImageReaderAdapter.Spi} wrapping the default provider for the
         * given format. This is a convenience constructor for {@link #Spi(ImageReaderSpi)}
         * with a provider fetched from the given format name.
         *
         * @param  format The name of the provider to fetch.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        protected Spi(final String format) throws IllegalArgumentException {
            this(Formats.getReaderByFormatName(format));
        }

        /**
         * Makes sure an argument is non-null.
         *
         * @param  name   Argument name.
         * @param  object User argument.
         * @throws NullArgumentException if {@code object} is null.
         */
        static void ensureNonNull(String name, Object object) throws NullArgumentException {
            if (object == null) {
                throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getStreamMetadataFormat(final String formatName) {
            if (SpatialMetadataFormat.FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.STREAM;
            }
            return main.getStreamMetadataFormat(formatName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getImageMetadataFormat(final String formatName) {
            if (SpatialMetadataFormat.FORMAT_NAME.equals(formatName) && isSpatialMetadataSupported(true)) {
                return SpatialMetadataFormat.IMAGE;
            }
            return main.getStreamMetadataFormat(formatName);
        }

        /**
         * Returns the input types accepted by the {@linkplain #main} provider which are also
         * accepted by this provider, or {@code null} if none.
         */
        final Class<?>[] getMainInputTypes() {
            final Class<?>[] types = main.getInputTypes();
            int count = 0;
            for (int i=0; i<types.length; i++) {
                final Class<?> mainType = types[i];
                for (final Class<?> thisType : inputTypes) {
                    if (mainType.isAssignableFrom(thisType)) {
                        types[count++] = mainType;
                        break;
                    }
                }
            }
            return (count != 0) ? XArrays.resize(types, count) : null;
        }

        /**
         * Returns {@code true} if the supplied source object appears to be of the format supported
         * by this reader. The default implementation checks if the given source is an instance of
         * one of the {@link ImageReaderSpi#inputTypes inputTypes}, then delegates to the provider
         * given at construction time. A temporary {@link ImageInputStream} is created if needed.
         *
         * @param  source The input (typically a {@link File}) to be decoded.
         * @return {@code true} if it is likely that the file can be decoded.
         * @throws IOException If an error occured while reading the file.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            ensureNonNull("source", source);
            for (final Class<?> type : inputTypes) {
                if (type.isInstance(source)) {
                    if (acceptOther && main.canDecodeInput(source)) {
                        return true;
                    }
                    if (acceptStream) {
                        final ImageInputStream in = Formats.createUncachedImageInputStream(source);
                        try {
                            return main.canDecodeInput(in);
                        } finally {
                            in.close();
                        }
                    }
                }
            }
            return false;
        }

        /**
         * A callback that will be called exactly once after the {@code Spi} class has been
         * instantiated and registered in a {@code ServiceRegistry}. The default implementation
         * conservatively gives precedence to the {@linkplain #main} provider, using the code
         * below:
         *
         * {@preformat java
         *     registry.setOrdering(category, main, this);
         * }
         *
         * Subclasses should override this method if they want to specify a different ordering.
         *
         * @see ServiceRegistry#setOrdering(Class, Object, Object)
         */
        @Override
        @SuppressWarnings({"unchecked","rawtypes"})
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            registry.setOrdering((Class) category, main, this);
        }
    }
}
