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
package org.geotoolkit.image.io;

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Collections;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.event.IIOReadProgressListener;

import org.opengis.coverage.grid.GridEnvelope;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.image.io.CheckedImageInputStream;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.XArrays;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Base class for readers which delegate most of their work to an other {@link ImageReader}.
 * This is used for reusing an existing image reader while adding some extra functionalities,
 * like adding geographic information in {@link SpatialMetadata}.
 * <p>
 * The wrapped image reader is called the {@linkplain #main} reader. The input given to that
 * reader is determined by the {@link #createInput(String)} method - it may or may not be the
 * same input than the one given to this {@code ImageReaderAdapter}. Most methods like
 * {@link #getWidth(int)}, {@link #getHeight(int)} and {@link #read(int)} delegate directly
 * to the main reader.
 * <p>
 * The amount of methods declared in this class is large, but the only new methods are
 * {@link #createInput(String)} and {@link #initialize()}. All other methods override
 * existing methods declared in parent classes, mostly {@link ImageReader} and
 * {@link SpatialImageReader}.
 * <p>
 * Subclasses typically need to implement of override the following methods only:
 * <p>
 * <ul>
 *   <li>{@link #initialize()} as a hook available for custom initialization.</li>
 *   <li>{@link #createInput(String)} for defining how, given a "main" file,
 *       to find the additional files (TFW, PRJ, DIM, <i>etc.</i>).</li>
 *   <li>{@link #createMetadata(int)} for creating the {@link IIOMetadata} objects.</li>
 * </ul>
 *
 * {@section Example}
 * The <cite>World File</cite> format is composed of a classical image file (usually with the
 * {@code ".tiff"}, {@code ".jpg"} or {@code ".png"} extension) together with a small text file
 * containing geolocalization information (often with the {@code ".tfw"} extension) and an other
 * small text file containing projection information ({@code ".prj"} extension).
 * This {@code ImageReaderAdapter} class can be used for wrapping a TIFF image reader,
 * augmented with the parsing of TFW and PRJ files, as below:
 *
 * {@preformat java
 *    class MyReader extends ImageReaderAdapter {
 *        MyReader(Spi provider) throws IOException {
 *            super(provider);
 *       }
 *
 *       // Gets the input field with a different file suffix.
 *       private File getInputWithNewSuffix(String newSuffix) {
 *           File file = (File) input;
 *           String name = file.getName();
 *           name = name.substring(0, name.lastIndexOf('.');
 *           return new File(file.getParent(), name + suffix);
 *       }
 *
 *       &#64;Override
 *       protected Object createInput(String readerID) throws IOException {
 *           switch (readerID) {
 *               case "tfw": return getInputWithNewSuffix(".tfw");
 *               case "prj": return getInputWithNewSuffix(".prj");
 *           }
 *           return super.createInput(readerID);
 *       }
 *
 *       &#64;Override
 *       protected SpatialMetadata createMetadata(int imageIndex) throws IOException {
 *           SpatialMetadata metadata = super.createMetadata(imageIndex);
 *           Object inputTFW = createInput("tfw");
 *           if ((inputTFW instanceof File) && ((File) inputTFW).isFile()) {
 *               // Read the "TFW" file and complete the metadata here.
 *           }
 *           return metadata;
 *       }
 *    }
 * }
 *
 * The information fetched from the TFW and PRJ files are stored in the {@link SpatialMetadata}
 * object returned by the {@link #createMetadata(int)} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see ImageWriterAdapter
 *
 * @since 3.07
 * @module
 */
@Decorator(ImageReader.class)
public abstract class ImageReaderAdapter extends SpatialImageReader {
    /**
     * The reader to use for reading the pixel values.
     */
    protected final ImageReader main;

    /**
     * The input types accepted by both the {@linkplain #main} reader and this reader.
     */
    private final Class<?>[] inputTypes;

    /**
     * {@code true} if the {@link #main} reader accepts inputs of kind {@link ImageInputStream}.
     * If the input types are unspecified, then this field is {@code null}.
     */
    private final boolean acceptStream;

    /**
     * Constructs a new image reader. The provider argument is mandatory for this constructor.
     * If the provider is unknown, use the next constructor below instead.
     *
     * @param  provider The {@link ImageReaderSpi} that is constructing this object.
     * @throws IOException If an error occurred while creating the {@linkplain #main} reader.
     */
    protected ImageReaderAdapter(final Spi provider) throws IOException {
        this(provider, provider.main.createReaderInstance());
    }

    /**
     * Constructs a new image reader wrapping the given reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     * @param main The reader to use for reading the pixel values.
     */
    protected ImageReaderAdapter(final Spi provider, final ImageReader main) {
        super(provider);
        this.main = main;
        ensureNonNull("main", main);
        if (provider != null) {
            inputTypes   = provider.getMainTypes();
            acceptStream = provider.acceptStream;
        } else {
            inputTypes   = null;
            acceptStream = true;
        }
    }

    /**
     * Creates the input to give to the {@linkplain #main} reader, or to an other reader identified
     * by the {@code readerID} argument. The default {@code ImageReaderAdapter} implementation
     * invokes this method with a {@code readerID} argument value equals to {@code "main"} when
     * the {@linkplain #main} reader needs to be used for the first time. This may happen at any
     * time after the {@link #setInput(Object, boolean, boolean) setInput} method has been invoked.
     * <p>
     * The only {@code readerID} argument value accepted by the default implementation is
     * {@code "main"}; any other argument value causes a {@code null} value to be returned.
     * However subclasses can override this method for supporting more reader types. The
     * table below summarizes a few types supported by this reader and different subclasses:
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Reader type</th>
     *     <th>Defined by</th>
     *     <th>Usage</th>
     *   </tr><tr>
     *     <td>&nbsp;{@code "main"}&nbsp;</td>
     *     <td>&nbsp;{@code ImageReaderAdapter}&nbsp;</td>
     *     <td>&nbsp;The input to be given to the {@linkplain #main} reader.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@code "tfw"}&nbsp;</td>
     *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.WorldFileImageReader}&nbsp;</td>
     *     <td>&nbsp;The input for the <cite>World File</cite>.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@code "prj"}&nbsp;</td>
     *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.WorldFileImageReader}&nbsp;</td>
     *     <td>&nbsp;The input for the <cite>Map Projection</cite> file.&nbsp;</td>
     *   </tr>
     * </table>
     * <p>
     * The default implementation first checks if the {@linkplain #main} reader accepts directly
     * the {@linkplain #input input} of this reader. If so, then that input is returned with no
     * change. Otherwise the input is wrapped in an {@linkplain ImageInputStream}, which is
     * returned. The input stream assigned to the {@linkplain #main} reader will be closed by
     * the {@link #close()} method.
     *
     * @param  readerID Identifier of the reader for which an input is needed.
     * @return The input to give to the identified reader, or {@code null} if this
     *         method can not create such input.
     * @throws IllegalStateException if the {@linkplain #input input} source has not been set.
     * @throws IOException If an error occurred while creating the input for the reader.
     *
     * @see ImageWriterAdapter#createOutput(String)
     */
    protected Object createInput(final String readerID) throws IllegalStateException, IOException {
        final Object input = this.input;
        if (input == null) {
            throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_INPUT));
        }
        if (!"main".equalsIgnoreCase(readerID)) {
            return null;
        }
        if (inputTypes != null) {
            for (final Class<?> type : inputTypes) {
                if (type.isInstance(input)) {
                    return input;
                }
            }
        }
        ImageInputStream in = null;
        if (acceptStream) {
            in = ImageIO.createImageInputStream(input);
            if (in == null) {
                final Object alternate = IOUtilities.tryToFile(input);
                if (alternate != input) {
                    in = ImageIO.createImageInputStream(alternate);
                }
            }
        }
        assert CheckedImageInputStream.isValid(in = // Intentional side effect.
               CheckedImageInputStream.wrap(in));
        return in;
    }

    /**
     * Invoked automatically when the {@linkplain #main} reader has been given a new input.
     * When this method is invoked, the main reader input has already been set to the value
     * returned by <code>{@linkplain #createInput(String) createInput}("main")</code>.
     * <p>
     * The default implementation does nothing. Subclasses can override this method
     * for performing additional initialization.
     *
     * @throws IOException If an error occurred while initializing the main reader.
     */
    protected void initialize() throws IOException {
    }

    /**
     * Ensures that the input of the {@linkplain #main} reader is initialized.
     * If not, this method tries to create an informative error message.
     */
    private void ensureInitialized() throws IOException {
        if (main.getInput() == null) {
            final Object mainInput = createInput("main");
            if (mainInput == null) {
                throw new InvalidImageStoreException(getErrorResources(), input, inputTypes, false);
            }
            main.setInput(mainInput, seekForwardOnly, ignoreMetadata);
            initialize();
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
     * has been {@linkplain #initialize() initialized}, then delegates to that reader.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        ensureInitialized();
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
     * or returns the number of bands of the {@linkplain ImageReader#getRawImageType(int) raw image
     * type} otherwise.
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
        final int n;
        if (main instanceof SpatialImageReader) {
            checkImageIndex(imageIndex);
            n = ((SpatialImageReader) main).getDimension(imageIndex);
        } else {
            n = super.getDimension(imageIndex);
        }
        sync();
        return n;
    }

    /**
     * Returns the grid range of the image at the given index. The default implementation
     * delegates to the {@linkplain #main} reader if it is an instance of {@link SpatialImageReader},
     * or to the super-class otherwise.
     * <p>
     * Note that the {@linkplain #main} reader is indirectly initialized by an implicit call to
     * {@link #getNumImages(boolean)}.
     *
     * @since 3.19
     */
    @Override
    public GridEnvelope getGridEnvelope(final int imageIndex) throws IOException {
        final GridEnvelope range;
        if (main instanceof SpatialImageReader) {
            checkImageIndex(imageIndex);
            range = ((SpatialImageReader) main).getGridEnvelope(imageIndex);
        } else {
            range = super.getGridEnvelope(imageIndex);
        }
        sync();
        return range;
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
        ensureInitialized();
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
     * {@linkplain #main} reader. Then there is a choice:
     * <p>
     * <ul>
     *   <li>If the metadata is null or already an instance of {@link SpatialMetadata},
     *       returns it unchanged.</li>
     *   <li>Otherwise wraps the result in a new {@link SpatialMetadata}, which will
     *       delegate the request for any metadata format other than
     *       {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}
     *       to the wrapped format.</li>
     * </ul>
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if (imageIndex >= 0) {
            final IIOMetadata metadata = main.getImageMetadata(imageIndex);
            if (metadata != null) {
                if (metadata instanceof SpatialMetadata) {
                    final SpatialMetadata sm = (SpatialMetadata) metadata;
                    sm.setReadOnly(false);
                    return sm;
                }
                return new SpatialMetadata(false, this, metadata);
            }
        } else {
            final IIOMetadata metadata = main.getStreamMetadata();
            if (metadata != null) {
                if (metadata instanceof SpatialMetadata) {
                    final SpatialMetadata sm = (SpatialMetadata) metadata;
                    sm.setReadOnly(false);
                    return sm;
                }
                return new SpatialMetadata(true, this, metadata);
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the image at the given index has a color palette. The default
     * implementation delegates to the {@linkplain #main} reader if it is an instance of
     * {@link SpatialImageReader}, or returns {@code true} otherwise (on the assumption that
     * the wrapped reader is for some standard format like PNG).
     *
     * @since 3.11
     */
    @Override
    public boolean hasColors(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (main instanceof SpatialImageReader) {
            return ((SpatialImageReader) main).hasColors(imageIndex);
        }
        return true;
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
     * Returns a default parameter object appropriate for this format.
     */
    @Override
    public SpatialImageReadParam getDefaultReadParam() {
        final ImageReadParam param = main.getDefaultReadParam();
        if (param instanceof SpatialImageReadParam) {
            return (SpatialImageReadParam) param;
        }
        return new ImageReadParamAdapter(this, param);
    }

    /**
     * If the given parameter object is an instance of {@link ImageReadParamAdapter},
     * returns the wrapped parameters.
     *
     * @since 3.18
     */
    private static ImageReadParam unwrap(ImageReadParam param) {
        if (param instanceof ImageReadParamAdapter) {
            param = ((ImageReadParamAdapter) param).param;
        }
        return param;
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
        final BufferedImage image = main.read(imageIndex, unwrap(param));
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
        final RenderedImage image = main.readAsRenderedImage(imageIndex, unwrap(param));
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
        final Raster image = main.readRaster(imageIndex, unwrap(param));
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
     * Adds the given listener to the list of registered warning listeners.
     * Thie listener is added both to this reader and to the {@linkplain #main} reader.
     */
    @Override
    public void addIIOReadWarningListener(final IIOReadWarningListener listener) {
        super.addIIOReadWarningListener(listener);
        main .addIIOReadWarningListener(listener);
    }

    /**
     * Removes the given listener from the list of registered warning listeners.
     */
    @Override
    public void removeIIOReadWarningListener(final IIOReadWarningListener listener) {
        super.removeIIOReadWarningListener(listener);
        main .removeIIOReadWarningListener(listener);
    }

    /**
     * Removes all currently registered warning listeners.
     */
    @Override
    public void removeAllIIOReadWarningListeners() {
        super.removeAllIIOReadWarningListeners();
        main .removeAllIIOReadWarningListeners();
    }

    /**
     * Adds the given listener to the list of registered progress listeners. This method
     * adds the listener only to the {@linkplain #main} reader, not to this reader, in
     * order to ensure that progress methods are invoked only once.
     */
    @Override
    public void addIIOReadProgressListener(final IIOReadProgressListener listener) {
        main.addIIOReadProgressListener(listener);
    }

    /**
     * Removes the given listener from the list of registered progress listeners.
     */
    @Override
    public void removeIIOReadProgressListener(final IIOReadProgressListener listener) {
        super.removeIIOReadProgressListener(listener); // As a safety.
        main .removeIIOReadProgressListener(listener);
    }

    /**
     * Removes all currently registered progress listeners.
     */
    @Override
    public void removeAllIIOReadProgressListeners() {
        super.removeAllIIOReadProgressListeners(); // As a safety.
        main .removeAllIIOReadProgressListeners();
    }

    /**
     * Adds the given listener to the list of registered update listeners. This method
     * adds the listener only to the {@linkplain #main} reader, not to this reader, in
     * order to ensure that update methods are invoked only once.
     */
    @Override
    public void addIIOReadUpdateListener(final IIOReadUpdateListener listener) {
        main.addIIOReadUpdateListener(listener);
    }

    /**
     * Removes the given listener from the list of registered update listeners.
     */
    @Override
    public void removeIIOReadUpdateListener(final IIOReadUpdateListener listener) {
        super.removeIIOReadUpdateListener(listener); // As a safety.
        main .removeIIOReadUpdateListener(listener);
    }

    /**
     * Removes all currently registered update listeners.
     */
    @Override
    public void removeAllIIOReadUpdateListeners() {
        super.removeAllIIOReadUpdateListeners(); // As a safety.
        main .removeAllIIOReadUpdateListeners();
    }

    /**
     * Requests that any current read operation be aborted. The default implementation delegates
     * to both the {@linkplain #main} reader and to the super-class method.
     */
    @Override
    public void abort() {
        super.abort();
        main.abort();
    }

    /**
     * Restores the {@code ImageReader} to its initial state. The default implementation
     * delegates to both the {@linkplain #main} reader and to the super-class method.
     */
    @Override
    public void reset() {
        super.reset();
        main.reset();
    }

    /**
     * Allows any resources held by this object to be released. The default implementation
     * delegates to both the {@linkplain #main} reader and to the super-class method.
     */
    @Override
    public void dispose() {
        super.dispose();
        main.dispose();
    }

    /**
     * Closes the input stream created by {@link #createInput(String)}. This method does nothing
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
     * even in case of failure.
     *
     * @throws IOException if an error occurred while closing the stream.
     */
    @Override
    protected void close() throws IOException {
        super.close();
        final Object mainInput = main.getInput();
        main.setInput(null);
        if (mainInput != null && mainInput != input) {
            IOUtilities.close(mainInput);
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
     * a filename, like {@link File} or {@link URL}, rather than the usual
     * {@linkplain #STANDARD_INPUT_TYPE standard input type}. The {@link #names names} and
     * {@link #MIMETypes MIMETypes} fields are set to the values of the wrapped provider,
     * suffixed with the string given to the {@link #addFormatNameSuffix(String)} method.
     * <p>
     * <b>Example:</b> An {@code ImageReaderAdapter} wrapping the {@code "tiff"} image reader
     * with the {@code "-wf"} suffix will have the {@code "tiff-wf"} format name and the
     * {@code "image/x-tiff-wf"} MIME type.
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
     *     <td>&nbsp;Same values than the {@linkplain #main} provider, suffixed by the given string.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #suffixes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider.&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #MIMETypes}&nbsp;</td>
     *     <td>&nbsp;Same values than the {@linkplain #main} provider, suffixed by the given string.&nbsp;</td>
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
     * @version 3.20
     *
     * @see ImageWriterAdapter.Spi
     *
     * @since 3.07
     * @module
     */
    public abstract static class Spi extends SpatialImageReader.Spi {
        /**
         * List of legal input and output types for {@link ImageReaderAdapter} and
         * {@link ImageWriterAdapter} - except the two last elements which are
         * different for writers.
         */
        static final Class<?>[] TYPES = new Class<?>[] {
            File.class,
            URI.class,
            URL.class,
            String.class, // To be interpreted as file path.
// TODO     InputStream.class,
// GEOTK-231 ImageInputStream.class
        };

        /**
         * The value to be returned by {@link #getModifiedInformation(Object)}.
         */
        static final Set<InformationType> INFO = Collections.unmodifiableSet(EnumSet.allOf(InformationType.class));

        /**
         * The provider of the readers to use for reading the pixel values.
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
         * For efficiency reasons, the {@code inputTypes} field is initialized to a shared array.
         * Subclasses can assign new arrays, but should not modify the default array content.
         *
         * @param main The provider of the readers to use for reading the pixel values.
         */
        protected Spi(final ImageReaderSpi main) {
            ensureNonNull("main", main);
            this.main  = main;
            names      = main.getFormatNames();
            suffixes   = main.getFileSuffixes();
            MIMETypes  = main.getMIMETypes();
            inputTypes = TYPES;
            supportsStandardStreamMetadataFormat = main.isStandardStreamMetadataFormatSupported();
            supportsStandardImageMetadataFormat  = main.isStandardImageMetadataFormatSupported();
            nativeStreamMetadataFormatName       = main.getNativeStreamMetadataFormatName();
            nativeImageMetadataFormatName        = main.getNativeImageMetadataFormatName();
            extraStreamMetadataFormatNames       = main.getExtraStreamMetadataFormatNames();
            extraImageMetadataFormatNames        = main.getExtraImageMetadataFormatNames();
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
         * Creates a provider which will use the given format for reading pixel values.
         * This is a convenience constructor for the above constructor with a provider
         * fetched from the given format name.
         *
         * @param  format The name of the provider to use for reading the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        protected Spi(final String format) {
            this(Formats.getReaderByFormatName(format, Spi.class));
        }

        /**
         * Adds the given suffix to all {@linkplain #names format names} and
         * {@linkplain #MIMETypes MIME types}. Subclasses should invoke this
         * method in their constructor.
         *
         * @param suffix The suffix to append to format names and MIME types.
         *
         * @since 3.20
         */
        protected void addFormatNameSuffix(final String suffix) {
            addFormatNameSuffix(names, MIMETypes, suffix);
        }

        /**
         * Appends the given suffix in the given names array if the array is non-null,
         * then updates the MIME type arrays. The replacement is performed in-place.
         */
        static void addFormatNameSuffix(final String[] names, final String[] MIMETypes, final String suffix) {
            if (names != null && !suffix.isEmpty()) {
                final String upper = suffix.toUpperCase(Locale.ENGLISH);
                final boolean[] replaced = new boolean[(MIMETypes != null) ? MIMETypes.length : 0];
                for (int i=0; i<names.length; i++) {
                    final String oldName = names[i];
                    final String newName = oldName.concat(Strings.isUpperCase(oldName) ? upper : suffix);
                    for (int j=0; j<replaced.length; j++) {
                        if (!replaced[j]) {
                            MIMETypes[j] = MIMETypes[j].replace(oldName, newName);
                            replaced[j] = true;
                        }
                    }
                    names[i] = newName;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getStreamMetadataFormat(final String formatName) {
            switch (getMetadataFormatCode(formatName,
                    nativeStreamMetadataFormatName,
                    nativeStreamMetadataFormatClassName,
                    extraStreamMetadataFormatNames,
                    extraStreamMetadataFormatClassNames))
            {
                case 1:  return SpatialMetadataFormat.getStreamInstance(formatName);
                case 2:  return super.getStreamMetadataFormat(formatName);
                default: return main.getStreamMetadataFormat(formatName);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IIOMetadataFormat getImageMetadataFormat(final String formatName) {
            switch (getMetadataFormatCode(formatName,
                    nativeImageMetadataFormatName,
                    nativeImageMetadataFormatClassName,
                    extraImageMetadataFormatNames,
                    extraImageMetadataFormatClassNames))
            {
                case 1:  return SpatialMetadataFormat.getImageInstance(formatName);
                case 2:  return super.getImageMetadataFormat(formatName);
                default: return main.getImageMetadataFormat(formatName);
            }
        }

        /**
         * Returns the input types accepted by the {@linkplain #main} provider which are also
         * accepted by this provider, or {@code null} if none.
         */
        final Class<?>[] getMainTypes() {
            return getMainTypes(inputTypes, main.getInputTypes());
        }

        /**
         * Implementation of {@link #getMainTypes()}. The {@code types} argument must be a copy
         * of the main types array, because it will be modified in-place.
         */
        static Class<?>[] getMainTypes(final Class<?>[] adapterTypes, final Class<?>[] types) {
            int count = 0;
            for (int i=0; i<types.length; i++) {
                final Class<?> mainType = types[i];
                for (final Class<?> thisType : adapterTypes) {
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
         * @throws IOException If an error occurred while reading the file.
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
                        if (in != null) try {
                            return main.canDecodeInput(in);
                        } finally {
                            in.close();
                        }
                    }
                    break;
                }
            }
            return false;
        }

        /**
         * Returns the kind of information that this wrapper will add or modify compared to the
         * {@linkplain #main} reader. If this method returns an empty set, then there is no
         * raison to use this adapter instead than the main reader.
         * <p>
         * The default implementation conservatively returns all of the {@link InformationType}
         * enum values. Subclasses should return more accurate information when possible.
         *
         * @param  source The input (typically a {@link File}) to be decoded.
         * @return The set of information to be read or modified by this adapter.
         * @throws IOException If an error occurred while reading the file.
         *
         * @since 3.20
         */
        public Set<InformationType> getModifiedInformation(final Object source) throws IOException {
            return INFO;
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
         * The plugin order matter when an {@linkplain javax.imageio.ImageIO#getImageReadersBySuffix(String)
         * image reader is selected by file suffix}, because the {@linkplain #getFileSuffixes() file suffixes}
         * of this adapter are the same than the file suffixes of the {@linkplain #main} provider by default.
         *
         * @see ServiceRegistry#setOrdering(Class, Object, Object)
         */
        @Override
        @SuppressWarnings({"unchecked","rawtypes"})
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            registry.setOrdering((Class) category, main, this);
        }

        /**
         * If the given provider is an instance of {@code ImageReaderAdapter.Spi}, returns the
         * underlying {@linkplain #main} provider. Otherwise returns the given provider unchanged.
         * <p>
         * This method is convenient when the caller is not interested in spatial metadata,
         * in order to ensure that the cost of parsing TFW, PRJ or similar files is avoided.
         *
         * @param  spi An image reader provider, or {@code null}.
         * @return The wrapped image reader provider, or {@code null}.
         *
         * @since 3.14
         */
        public static ImageReaderSpi unwrap(ImageReaderSpi spi) {
            while (spi instanceof Spi) {
                spi = ((Spi) spi).main;
            }
            return spi;
        }
    }
}
