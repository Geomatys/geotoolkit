/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.Unit;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.ModifiableMetadata;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.content.DefaultCoverageDescription;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.identification.DefaultResolution;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.collection.BackingStoreException;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;
import org.apache.sis.util.iso.Names;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.ImageMetadataException;
import static org.geotoolkit.image.io.MultidimensionalImageStore.*;
import org.geotoolkit.image.io.SampleConversionType;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.large.LargeRenderedImage;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.ISO_FORMAT_NAME;
import org.geotoolkit.image.io.mosaic.MosaicImageReadParam;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.internal.image.io.CheckedImageInputStream;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.XCollections;
import static org.geotoolkit.util.collection.XCollections.addIfNonNull;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A {@link GridCoverageReader} implementation which use an {@link ImageReader} for reading
 * sample values. This implementation stores the sample values in a {@link RenderedImage},
 * and consequently is targeted toward two-dimensional slices of data.
 * <p>
 * {@code ImageCoverageReader} basically works as a layer which converts <cite>geodetic
 * coordinates</cite> (for example the region to read) to <cite>pixel coordinates</cite>
 * before to pass them to the wrapped {@code ImageReader}, and conversely: from pixel
 * coordinates to geodetic coordinates. The later conversion is called "<cite>grid to CRS</cite>"
 * and is determined from the {@link SpatialMetadata} provided by the {@code ImageReader}.
 *
 * {@section Closing the input stream}
 * An {@linkplain ImageInputStream Image Input Stream} may be created automatically from various
 * input types like {@linkplain java.io.File} or {@linkplain java.net.URL}. That input stream is
 * <strong>not</strong> closed after a read operation, because many consecutive read operations
 * may be performed for the same input. To ensure that the automatically generated input stream
 * is closed, user shall invoke the {@link #setInput(Object)} method with a {@code null} input,
 * or invoke the {@link #reset()} or {@link #dispose()} methods.
 * <p>
 * Note that input streams explicitly given by the users are never closed. It is caller
 * responsibility to close them.
 *
 * {@section Default metadata value}
 * If no {@linkplain CoordinateReferenceSystem Coordinate Reference System} or no
 * <cite>grid to CRS</cite> {@linkplain MathTransform Math Transform} can be created
 * from the {@link SpatialMetadata}, then the default values listed below are used:
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue"><th>Type</th><th>Default value</th></tr>
 *   <tr><td>&nbsp;{@link CoordinateReferenceSystem}&nbsp;</td>
 *       <td>&nbsp;{@link DefaultImageCRS#GRID_2D}&nbsp;</td></tr>
 *   <tr><td>&nbsp;{@link MathTransform}&nbsp;</td>
 *       <td>&nbsp;{@link MathTransforms#identity(int)} with the CRS dimension&nbsp;</td></tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class ImageCoverageReader extends GridCoverageStore implements GridCoverageReader {
    /**
     * The name of metadata nodes we are interested in. Some implementations of
     * {@link ImageReader} may use this information for reading only the metadata
     * we are interested in.
     *
     * @see SpatialMetadataFormat
     */
    private static final Set<String> METADATA_NODES;
    static {
        final Set<String> s = new HashSet<>(25);
                                                // geotk-coverageio
                                                // ├───ImageDescription
        s.add("Dimensions");                    // │   ├───Dimensions
        s.add("Dimension");                     // │   │   └───Dimension
        s.add("RangeElementDescriptions");      // │   └───RangeElementDescriptions
        s.add("RangeElementDescription");       // │       └───RangeElementDescription
        s.add("SpatialRepresentation");         // ├───SpatialRepresentation
        s.add("RectifiedGridDomain");           // └───RectifiedGridDomain
        s.add("Limits");                        //     ├───Limits
        s.add("OffsetVectors");                 //     ├───OffsetVectors
        s.add("OffsetVector");                  //     │   └───OffsetVector
        s.add("CoordinateReferenceSystem");     //     └───CoordinateReferenceSystem
        s.add("CoordinateSystem");              //         ├───CoordinateSystem
        s.add("Axes");                          //         │   └───Axes
        s.add("CoordinateSystemAxis");          //         │       └───CoordinateSystemAxis
        s.add("Datum");                         //         ├───Datum
        s.add("Ellipsoid");                     //         │   ├───Ellipsoid
        s.add("PrimeMeridian");                 //         │   └───PrimeMeridian
        s.add("Conversion");                    //         └───Conversion
        s.add("Parameters");                    //             └───Parameters
        s.add("ParameterValue");                //                 └───ParameterValue
        METADATA_NODES = Collections.unmodifiableSet(s);
    }

    /**
     * The {@link ImageReader} to use for decoding {@link RenderedImage}s. This reader is
     * initially {@code null} and lazily created the first time {@link #setInput(Object)}
     * is invoked. Once created, it is reused for subsequent inputs if possible.
     */
    protected ImageReader imageReader;

    /**
     * Optional parameter to be given (if non-null) to the image reader
     * {@link ImageReader#setInput(Object, boolean, boolean) setInput} method.
     *
     * If {@code TRUE}, images and metadata may only be read in ascending order from the input
     * source. If {@code FALSE}, they may be read in any order. If {@code null}, then this
     * parameter is not given to the {@linkplain #imageReader image reader} which is free to
     * use a plugin-dependent default (usually {@code false}).
     */
    protected Boolean seekForwardOnly;

    /**
     * Optional parameter to be given (if non-null) to the image reader
     * {@link ImageReader#setInput(Object, boolean, boolean) setInput} method.
     *
     * If {@code TRUE}, metadata may be ignored during reads. If {@code FALSE}, metadata will be
     * parsed. If {@code null}, then this parameter is not given to the {@linkplain #imageReader
     * image reader} which is free to use a plugin-dependent default (usually {@code false}).
     */
    protected Boolean ignoreMetadata;

    /**
     * The names of coverages, or {@code null} if not yet determined.
     * This is created by {@link #getCoverageNames()} when first needed.
     */
    private transient List<? extends GenericName> coverageNames;

    /**
     * The value returned by {@link #getGridGeometry(int)}, computed when first needed.
     */
    private transient Map<Integer,GridGeometry2D> gridGeometries;

    /**
     * The value returned by {@link #getSampleDimensions(int)}, computed when first needed. By
     * convention, an empty list means that we already checked for bands and didn't found any
     * having a {@code SampleDimension} description, in which case {@link #getSampleDimensions(int)}
     * shall returns {@code null}. We use this convention because a coverage having zero bands
     * should not be valid.
     */
    private transient Map<Integer,List<SampleDimension>> sampleDimensions;

    /**
     * The metadata for the image at index {@link #imageMetadataIndex}, cached for avoiding to
     * compute it many time. Note that {@code null} if a valid value - we need to check the image
     * index in order to determine if the value is valid.
     *
     * @see #getImageMetadata(ImageReader, int)
     */
    private transient SpatialMetadata imageMetadata;

    /**
     * The image index of {@link #imageMetadata}, or -1 if not yet computed.
     *
     * @see #getImageMetadata(ImageReader, int)
     */
    private transient int imageMetadataIndex;

    /**
     * Helper utilities for parsing metadata. Created only when needed.
     */
    private transient MetadataHelper helper;

    /**
     * The grid coverage builder to use for building {@link GridCoverage2D} instances.
     *
     * @since 3.21
     */
    private final GridCoverageBuilder coverageBuilder;

    /**
     * The name factory to use for building {@link GenericName} instances.
     * This factory can be specified at construction time in the {@link Hints} map.
     *
     * @since 3.20
     */
    protected final NameFactory nameFactory;

    /**
     * The input (typically a {@link java.io.File}, {@link java.net.URL} or {@link String}),
     * or {@code null} if input is not set.
     */
    Object input;

    /**
     * Creates a new instance using the default
     * {@linkplain GridCoverageFactory grid coverage factory}.
     */
    public ImageCoverageReader() {
        this(null);
    }

    /**
     * Creates a new instance using the {@linkplain GridCoverageFactory grid coverage factory}
     * specified by the given set of hints.
     *
     * @param hints The hints to use for fetching a {@link GridCoverageFactory},
     *        or {@code null} for the default hints.
     */
    public ImageCoverageReader(final Hints hints) {
        ignoreGridTransforms = true;
        coverageBuilder = new GridCoverageBuilder(hints);
        nameFactory = DefaultFactories.forBuildin(NameFactory.class);
        imageMetadataIndex = -1;
    }

    /**
     * If the given metadata is non-null, supports the ISO-19115 format and contains a
     * {@link Metadata} user object in the root node, returns that object. Otherwise
     * creates a new, initially empty, metadata object.
     */
    private static DefaultMetadata createMetadata(final IIOMetadata streamMetadata) throws DataStoreException {
        if (streamMetadata != null) try {
            if (ArraysExt.contains(streamMetadata.getExtraMetadataFormatNames(), ISO_FORMAT_NAME)) {
                final Node root = streamMetadata.getAsTree(ISO_FORMAT_NAME);
                if (root instanceof IIOMetadataNode) {
                    final Object userObject = ((IIOMetadataNode) root).getUserObject();
                    if (userObject instanceof Metadata) {
                        // Unconditionally copy the metadata, even if the original object was
                        // already an instance of DefaultMetadata, because the original object
                        // may be cached in the ImageReader - so we don't want to modify it.
                        return new DefaultMetadata((Metadata) userObject);
                    }
                }
            }
        } catch (BackingStoreException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw new CoverageStoreException(cause);
            }
            throw e.unwrapOrRethrow(CoverageStoreException.class);
        }
        return new DefaultMetadata();
    }

    /**
     * Sets the logging level to use for read operations. If the {@linkplain #imageReader image
     * reader} implements the {@link org.geotoolkit.util.logging.LogProducer} interface, then it
     * is also set to the given level.
     *
     * @since 3.15
     */
    @Override
    public void setLogLevel(final Level level) {
        super.setLogLevel(level);
        copyLevel(imageReader);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The given locale will also be given to the wrapped {@linkplain #imageReader image reader},
     * providing that the image reader supports the locale language. If it doesn't, then the image
     * reader locale is set to {@code null}.
     */
    @Override
    public void setLocale(final Locale locale) {
        super.setLocale(locale);
        setLocale(imageReader, locale);
        helper = null;
    }

    /**
     * Sets the given locale to the given {@link ImageReader}, provided that the image reader
     * supports the language of that locale. Otherwise sets the reader locale to {@code null}.
     *
     * @see ImageReader#setLocale(Locale)
     */
    private static void setLocale(final ImageReader reader, final Locale locale) {
        if (reader != null) {
            reader.setLocale(select(locale, reader.getAvailableLocales()));
        }
    }

    /**
     * Sets the input source to the given object. The input is typically a
     * {@link java.nio.file.Path}, {@link java.io.File}, {@link java.net.URL} or {@link String} object,
     * but other types (especially {@link ImageInputStream}) may be accepted
     * as well depending on the {@linkplain #imageReader image reader} implementation.
     * <p>
     * The given input can also be an {@link ImageReader} instance with its input initialized,
     * in which case it is used directly as the {@linkplain #imageReader image reader} wrapped
     * by this {@code ImageCoverageReader}.
     *
     * {@section Implementation note}
     * This method ensures that the {@link #imageReader} field is set to a suitable
     * {@link ImageReader} instance. This is done by invoking the following methods,
     * which can be overridden by subclasses:
     * <p>
     * <ol>
     *   <li>If the current {@link #imageReader} is non-null, invoke
     *       {@link #canReuseImageReader(ImageReaderSpi, Object)} for determining
     *       if it can be reused for the new input.</li>
     *   <li>If the current {@code imageReader} was null or if the above method call
     *       returned {@code false}, invoke {@link #createImageReader(Object)} for creating
     *       a new {@link ImageReader} instance for the given input.</li>
     * </ol>
     * <p>
     * Then this method {@linkplain ImageReader#setInput(Object, boolean, boolean) sets the input}
     * of the {@link #imageReader} instance, if it was not already done by the above method calls.
     */
    @Override
    public void setInput(final Object input) throws DataStoreException {
        final ImageReader oldReader = imageReader;
        try {
            reset();
            assert (oldReader == null) || (oldReader.getInput() == null) : oldReader;
            if (input != null) {
                ImageReader newReader = null;
                if (input instanceof ImageReader) {
                    newReader = (ImageReader) input;
                    // The old reader will be disposed and the locale will be set below.
                } else {
                    /*
                     * First, check if the current reader can be reused. If the user
                     * didn't overridden the canReuseImageReader(...) method, then the
                     * default implementation is to look at the file extension.
                     */
                    if (oldReader != null) {
                        final ImageReaderSpi provider = oldReader.getOriginatingProvider();
                        if (provider != null && canReuseImageReader(provider, input)) {
                            newReader = oldReader;
                        }
                    }
                    /*
                     * If we can't reuse the old reader, create a new one. If the user didn't
                     * overridden the createImageReader(...) method, then the default behavior
                     * is to get an image reader by the extension.
                     */
                    if (newReader == null) {
                        newReader = createImageReader(input);
                    }
                    /*
                     * Set the input if it was not already done. In the default implementation,
                     * this is done by 'createImageReader' but not by 'canReuseImageReader'.
                     * However the user could have overridden the above-cited methods with a
                     * different behavior.
                     */
                    if (newReader != input && newReader.getInput() == null) {
                        Object imageInput = input;
                        final ImageReaderSpi provider = newReader.getOriginatingProvider();
                        if (provider != null) {
                            boolean needStream = false;
                            for (final Class<?> inputType : provider.getInputTypes()) {
                                if (inputType.isInstance(imageInput)) {
                                    needStream = false;
                                    break;
                                }
                                if (inputType.isAssignableFrom(ImageInputStream.class)) {
                                    needStream = true;
                                    // Do not break - maybe the input type is accepted later.
                                }
                            }
                            if (needStream) {
                                imageInput = ImageIO.createImageInputStream(input);
                                assert CheckedImageInputStream.isValid((ImageInputStream) (imageInput =
                                       CheckedImageInputStream.wrap((ImageInputStream) imageInput)));
                                if (imageInput == null) {
                                    final short messageKey;
                                    final Object argument;
                                    if (IOUtilities.canProcessAsPath(input)) {
                                        messageKey = Errors.Keys.CantReadFile_1;
                                        argument = IOUtilities.filename(input);
                                    } else {
                                        messageKey = Errors.Keys.UnknownType_1;
                                        argument = input.getClass();
                                    }
                                    throw new CoverageStoreException(Errors.getResources(locale).getString(messageKey, argument));
                                }
                            }
                        }
                        if (seekForwardOnly != null) {
                            if (ignoreMetadata != null) {
                                newReader.setInput(imageInput, seekForwardOnly, ignoreMetadata);
                            } else {
                                newReader.setInput(imageInput, seekForwardOnly);
                            }
                        } else {
                            newReader.setInput(imageInput);
                        }
                    }
                }
                if (newReader != oldReader) {
                    if (oldReader != null) {
                        oldReader.dispose();
                    }
                    copyLevel(newReader);
                    setLocale(newReader, locale);
                    if (LOGGER.isLoggable(getFineLevel())) {
                        ImageCoverageStore.logCodecCreation(this, ImageCoverageReader.class,
                                newReader, newReader.getOriginatingProvider());
                    }
                }
                imageReader = newReader;
            }
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(input, e, false), e);
        }
        this.input = input;
        abortRequested = false;
    }

    /**
     * Returns the input which was set by the last call to {@link #setInput(Object)},
     * or {@code null} if none.
     *
     * @return The current input, or {@code null} if none.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageReader#getInput()
     */
    public Object getInput() throws DataStoreException {
        return input;
    }

    /**
     * Returns the name of the {@linkplain #input}, or "<cite>Untitled</cite>" if
     * the input is not a recognized type. This is used for formatting messages only.
     */
    final String getInputName() {
        final Object input = this.input;
        if (IOUtilities.canProcessAsPath(input)) {
            return IOUtilities.filename(input);
        } else {
            return Vocabulary.getResources(locale).getString(Vocabulary.Keys.Untitled);
        }
    }

    /**
     * Returns {@code true} if the image reader created by the given provider can be reused.
     * This method is invoked automatically by {@link #setInput(Object)} for determining if
     * the current {@linkplain #imageReader image reader} can be reused for reading the given
     * input.
     * <p>
     * The default implementation checks if the suffix of the given input is one of the
     * {@linkplain ImageReaderSpi#getFileSuffixes() file suffixes known to the provider}.
     * If the given object has no suffix (for example if it is an instance of
     * {@link ImageInputStream}), then this method fallbacks on
     * {@link ImageReaderSpi#canDecodeInput(Object)}.
     * <p>
     * Subclasses can override this method if they want to determine in another way
     * whatever the {@linkplain #imageReader image reader} can be reused. Subclasses
     * don't need to set the image reader input; this will be done by the caller.
     *
     * @param  provider The provider of the image reader.
     * @param  input The input to set to the image reader.
     * @return {@code true} if the image reader can be reused.
     * @throws IOException If an error occurred while determining if the current
     *         image reader can read the given input.
     */
    protected boolean canReuseImageReader(final ImageReaderSpi provider, final Object input) throws IOException {
        if (IOUtilities.canProcessAsPath(input)) {
            final String[] suffixes = provider.getFileSuffixes();
            return (suffixes != null) && ArraysExt.containsIgnoreCase(suffixes,
                    IOUtilities.extension(input));
        } else {
            return provider.canDecodeInput(input);
        }
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the given input.
     * This method is invoked automatically by {@link #setInput(Object)} for creating
     * a new {@linkplain #imageReader image reader}.
     * <p>
     * This method shall {@linkplain ImageReader#setInput(Object, boolean, boolean) set
     * the input} of the image reader that it create before returning it.
     * <p>
     * The default implementation delegates to {@link XImageIO#getReaderBySuffix(Object,
     * Boolean, Boolean)}. Subclasses can override this method if they want to create a
     * new {@linkplain #imageReader image reader} in another way.
     *
     * @param  input The input source.
     * @return An initialized image reader for reading the given input.
     * @throws IOException If no suitable image reader has been found, or if an error occurred
     *         while creating it.
     */
    protected ImageReader createImageReader(final Object input) throws IOException {
        // No need to check for MosaicImageReader inputs, because XImageIO does this check.
        return XImageIO.getReaderBySuffix(input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Returns the default Java I/O parameters to use for reading an image. This method
     * is invoked by the {@link #read(int, GridCoverageReadParam)} method in order to get
     * the Java parameter object to use for controlling the reading process.
     * <p>
     * The default implementation returns {@link ImageReader#getDefaultReadParam()}.
     * Subclasses can override this method in order to perform additional parameter settings.
     * For example a subclass may want to {@linkplain SpatialImageReadParam#setPaletteName set
     * the color palette} according some information unknown to this base class. Note however
     * that any
     * {@linkplain ImageReadParam#setSourceRegion source region},
     * {@linkplain ImageReadParam#setSourceSubsampling source subsampling} and
     * {@linkplain ImageReadParam#setSourceBands source bands} settings may be overwritten
     * by the {@code read} method, which perform its own computation.
     *
     * @param  index The index of the image to be queried.
     * @return A default Java I/O parameters object to use for controlling the reading process.
     * @throws IOException If an I/O operation was required and failed.
     *
     * @see #read(int, GridCoverageReadParam)
     *
     * @since 3.11
     */
    protected ImageReadParam createImageReadParam(int index) throws IOException {
        return imageReader.getDefaultReadParam();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericName getCoverageName() throws CoverageStoreException {
        if (coverageNames == null) {
            final ImageReader imageReader = this.imageReader; // Protect from changes.
            if (imageReader == null) {
                throw new IllegalStateException(formatErrorMessage(Errors.Keys.NoImageInput));
            }
            try {
                List<String> imageNames = null;
                if (imageNames != null) {
                    coverageNames = new NameList(nameFactory, imageNames);
                } else {
                    coverageNames = new NameList(nameFactory, getInputName(), imageReader.getNumImages(true));
                }
            } catch (IOException e) {
                throw new CoverageStoreException(formatErrorMessage(e), e);
            }
        }
        return coverageNames.get(0);
    }

    /**
     * Returns a helper object for parsing metadata.
     */
    private MetadataHelper getMetadataHelper() {
        if (helper == null) {
            helper = new MetadataHelper(this);
        }
        return helper;
    }

    /**
     * Returns the grid geometry for the {@link GridCoverage2D} to be read at the given index.
     * The default implementation performs the following:
     * <p>
     * <ul>
     *   <li>The {@link GridExtent} is determined from the
     *       {@linkplain SpatialImageReader#getGridEnvelope(int) spatial image reader}
     *       if possible, or from the image {@linkplain ImageReader#getWidth(int) width}
     *       and {@linkplain ImageReader#getHeight(int) height} otherwise.</li>
     *   <li>The {@link CoordinateReferenceSystem} and the "<cite>grid to CRS</cite>" conversion
     *       are determined from the {@link SpatialMetadata} if any.</li>
     * </ul>
     */
    @Override
    public GridGeometry2D getGridGeometry() throws DataStoreException {
        final int index = 0;
        GridGeometry2D gridGeometry = getCached(gridGeometries, index);
        if (gridGeometry == null) {
            final ImageReader imageReader = this.imageReader; // Protect from changes.
            if (imageReader == null) {
                throw new IllegalStateException(formatErrorMessage(Errors.Keys.NoImageInput));
            }
            /*
             * Get the required information from the SpatialMetadata, if any.
             * For now we just collect them - they will be processed later.
             */
            CoordinateReferenceSystem crs = null;
            MathTransform       gridToCRS = null;
            PixelOrientation pointInPixel = null;
            final int width, height;
            try {
                width  = imageReader.getWidth(index);
                height = imageReader.getHeight(index);
                final SpatialMetadata metadata = getImageMetadata(imageReader, index);
                if (metadata != null) {
                    crs = metadata.getInstanceForType(CoordinateReferenceSystem.class);
                    if (crs == null || crs == PredefinedCRS.GRID_2D) {
                        crs = coverageBuilder.getCoordinateReferenceSystem();
                    }
                    if (crs == null) {
                        crs = PredefinedCRS.GRID_2D;
                    }
                    if (crs instanceof GridGeometry) { // Some formats (e.g. NetCDF) do that.
                        gridToCRS = ((GridGeometry) crs).getGridToCRS(PixelInCell.CELL_CENTER);
                    } else {
                        final RectifiedGrid grid = metadata.getInstanceForType(RectifiedGrid.class);
                        if (grid != null) {
                            gridToCRS = getMetadataHelper().getGridToCRS(grid);
                        }
                        final Georectified georect = metadata.getInstanceForType(Georectified.class);
                        if (georect != null) {
                            pointInPixel = georect.getPointInPixel();
                        }
                    }
                }
            } catch (IOException e) {
                throw new CoverageStoreException(formatErrorMessage(e), e);
            }
            /*
             * If any metadata are still null, replace them by their default values. Those default
             * values are selected in order to be as neutral as possible: An ImageCRS which is not
             * convertible to GeodeticCRS, an identity "grid to CRS" conversion, a PixelOrientation
             * equivalent to performing no shift at all in the "grid to CRS" conversion.
             */
            if (crs == null) {
                crs = PredefinedCRS.GRID_2D;
            }
            final int dimension = crs.getCoordinateSystem().getDimension();
            if (gridToCRS == null) {
                gridToCRS = MathTransforms.identity(dimension);
            }
            if (pointInPixel == null) {
                pointInPixel = PixelOrientation.CENTER;
            }
            /*
             * Now build the grid geometry. Note that the grid extent spans shall be set to 1
             * for all dimensions other than X and Y, even if the original file has more data,
             * since this is a GridGeometry2D requirement.
             */
            final long[] lower = new long[dimension];
            final long[] upper = new long[dimension];
            Arrays.fill(upper, 1);
            upper[X_DIMENSION] = width;
            upper[Y_DIMENSION] = height;
            final GridExtent gridExtent = new GridExtent(null, lower, upper, false);
            gridGeometry = new GridGeometry2D(gridExtent, pointInPixel, gridToCRS, crs);
            Map.Entry<Map<Integer,GridGeometry2D>,GridGeometry2D> entry = setCached(gridGeometry, gridGeometries, index);
            gridGeometries = entry.getKey();
            gridGeometry = entry.getValue();
        }
        return gridGeometry;
    }

    /**
     * Gets the element at the given index in the given map if the map is non null and the
     * index is valid, or returns {@code null} otherwise. This method is used for fetching
     * a value that may or may not have been cached in a previous method call.
     */
    private static <T> T getCached(final Map<Integer,T> cache, final int index) {
        return (cache != null) ? cache.get(index) : null;
    }

    /**
     * Sets the cached value to the given element. The cache is returned together with the value.
     * Both of them may be different than the given arguments. We use {@code Map.Entry} only as a
     * lazy way to emulate multi return values.
     */
    private static <T> Map.Entry<Map<Integer,T>,T> setCached(T value, Map<Integer,T> cache, final int index) {
        if (value != null) {
            if (cache == null) {
                cache = new HashMap<>();
            }
            for (final T current : cache.values()) {
                if (current.equals(value)) {
                    value = current;
                    break;
                }
            }
            cache.put(index, value);
        }
        return new HashMap.SimpleEntry<>(cache, value);
    }

    /**
     * Return {@code true} if metadata contains Dimension informations from image descriptions else false.
     *
     * @param metadata current image description.
     * @return {@code true} if metadata contains Dimension informations from image descriptions else false.
     */
    private boolean hasDimensionMetadata(final IIOMetadata metadata) {
        final Node asTree = metadata.getAsTree(GEOTK_FORMAT_NAME);
        if (asTree.hasChildNodes()) {
            final NodeList nl = asTree.getChildNodes();
            final int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                 final Node current = nl.item(i);
                 if (current.getNodeName().equalsIgnoreCase("ImageDescription")) {
                     final NodeList idnl = current.getChildNodes();
                     final int l = idnl.getLength();
                     for (int j = 0; j < l; j++) {
                         if (idnl.item(j).getNodeName().equalsIgnoreCase("Dimensions")) return true;
                     }
                 }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws CoverageStoreException {
        return getSampleDimensions(0);
    }

    public List<SampleDimension> getSampleDimensions(final int index) throws CoverageStoreException {
        List<SampleDimension> sd = getCached(sampleDimensions, index);
        if (sd == null) {
            final ImageReader imageReader = this.imageReader; // Protect from changes.
            if (imageReader == null) {
                throw new IllegalStateException(formatErrorMessage(Errors.Keys.NoImageInput));
            }
            /*
             * Get the required information from the SpatialMetadata, if any.
             * Here we just collect them - they will be processed by MetadataHelper.
             */
            List<org.geotoolkit.image.io.metadata.SampleDimension> bands = null;
            try {
                final SpatialMetadata metadata = getImageMetadata(imageReader, index);
                if (metadata != null && hasDimensionMetadata(metadata)) {
                    DimensionAccessor accessor = new DimensionAccessor(metadata);
                    sd = accessor.getSampleDimensions();
                    if (sd != null) return sd;
                    bands = metadata.getListForType(org.geotoolkit.image.io.metadata.SampleDimension.class);
                }
            } catch (IOException e) {
                throw new CoverageStoreException(formatErrorMessage(e), e);
            }
            if (isNullOrEmpty(bands)) {
                // See the convention documented below.
                sd = Collections.emptyList();
            } else try {
                // MetadataHelper default implementation returns an unmodifiable list.
                sd = getMetadataHelper().getSampleDimensions(bands);
            } catch (ImageMetadataException e) {
                throw new CoverageStoreException(formatErrorMessage(e), e);
            }
            Map.Entry<Map<Integer,List<SampleDimension>>,List<SampleDimension>> entry =
                    setCached(sd, sampleDimensions, 0);
            sampleDimensions = entry.getKey();
            sd = entry.getValue();
        }
        /*
         * By convention, an empty list means that we already checked for sample dimensions
         * and didn't found any. This is not the same than a coverage having no bands, which
         * should not be valid.
         */
        if (sd == null || sd.isEmpty()) {
            return null;
        }
        return sd;
    }

    /**
     * Returns the sample dimensions for each band to be read, as determined from the given
     * optional parameters. If parameters are not null, then this method returns only the
     * sample dimensions for supplied source bands list and returns them in the order
     * inferred from the destination bands list.
     *
     * @return The bands as a non-empty array, or {@code null}. This method is not allowed to
     *         return an empty array, because {@link GridCoverageFactory} interprets that as
     *         "no band" (as opposed to {@code null} which means "unspecified bands").
     */
    private SampleDimension[] getSampleDimensions(final int index, final int[] srcBands, final int[] dstBands)
            throws CoverageStoreException
    {
        final List<SampleDimension> bands = getSampleDimensions(index);
        if (bands != null) {
            int bandCount = bands.size();
            if (bandCount != 0) {
                if (srcBands != null && srcBands.length < bandCount) bandCount = srcBands.length;
                if (dstBands != null && dstBands.length < bandCount) bandCount = dstBands.length;
                final SampleDimension[] selectedBands = new SampleDimension[bandCount];
                /*
                 * Searches for 'SampleDimension' from the given source band index and
                 * stores their reference at the position given by destination band index.
                 */
                for (int j=0; j<bandCount; j++) {
                    final int srcBand = (srcBands != null) ? srcBands[j] : j;
                    final int dstBand = (dstBands != null) ? dstBands[j] : j;
                    selectedBands[dstBand] = bands.get(srcBand % bandCount);
                }
                return selectedBands;
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the given sample dimensions contain at least one signed range.
     */
    private static boolean isRangeSigned(final SampleDimension[] bands) {
        if (bands != null) {
            for (final SampleDimension band : bands) {
                if (band != null && SampleDimensionUtils.isRangeSigned(band)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns the ISO 19115 metadata object associated with the input source as a whole
     * and each coverages. The default implementation constructs the metadata from the
     * {@linkplain #getStreamMetadata() stream metadata} and the
     * {@linkplain #getCoverageMetadata(int) coverage metadata},
     * eventually completed by the {@link #getGridGeometry(int)}.
     * <p>
     * Since the relationship between Image I/O metadata and ISO 19115 is not always a
     * "<cite>one-to-one</cite>" relationship, this method works on a best effort basis.
     *
     * @return The ISO 19115 metadata (never {@code null}).
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     *
     * @see <a href="../../image/io/metadata/SpatialMetadataFormat.html#default-formats">Metadata formats</a>
     *
     * @since 3.18
     */
    public Metadata getMetadata() throws DataStoreException {
        final SpatialMetadata streamMetadata = getStreamMetadata();
        final DefaultMetadata metadata = createMetadata(streamMetadata);
        /*
         * Extract all information available from the stream metadata, provided that metadata
         * elements were not already provided by the above call to createMetadata(...). Since
         * createMetadata(...) typically get its information from the stream metadata as well,
         * we assume that creating here new objects from stream metadata would be redundant.
         */
        DataIdentification identification = null;
        if (streamMetadata != null) {
            final Collection<DataQuality> quality = metadata.getDataQualityInfo();
            if (quality.isEmpty()) {
                addIfNonNull(quality, streamMetadata.getInstanceForType(DataQuality.class));
            }
            final Collection<AcquisitionInformation> acquisition = metadata.getAcquisitionInformation();
            if (acquisition.isEmpty()) {
                addIfNonNull(acquisition, streamMetadata.getInstanceForType(AcquisitionInformation.class));
            }
            /*
             * Get the existing identification info if any, or create a new one otherwise.
             * If an identification info is found, remove it from the metadata (it will be
             * added back at the end of this method, or a copy of it will be added).
             */
            final Iterator<Identification> it = metadata.getIdentificationInfo().iterator();
            while (it.hasNext()) {
                final Identification candidate = it.next();
                if (candidate instanceof DataIdentification) {
                    identification = (DataIdentification) candidate;
                    it.remove();
                    break;
                }
            }
            if (identification == null) {
                identification = streamMetadata.getInstanceForType(DataIdentification.class);
            }
        }
        /*
         * Check if we should complete the extents and resolutions. We will do so only
         * if the vertical/temporal extent, geographic bounding box and resolution are
         * not already provided in the metadata.  If the geographic extent is declared
         * by an other kind of object than GeographicBoundingBox, we will still add the
         * bounding box because the existing extent could be only a textual description.
         */
        boolean failed              = false;  // For logging warning only once.
        boolean computeExtents      = true;   // 'false' if extents are already present.
        boolean computeResolutions  = true;   // 'false' is resolutions are already present.
        DefaultExtent   extent      = null;   // The extent to compute, if needed.
        List<Extent>    extents     = null;   // The extents already provided in the metadata.
        Set<Resolution> resolutions = null;   // The resolutions to compute, if needed.
        if (identification != null) {
            computeResolutions = isNullOrEmpty(identification.getSpatialResolutions());
            final Collection<? extends Extent> existings = identification.getExtents();
            if (!isNullOrEmpty(existings)) {
                extents = new ArrayList<>(existings);
                extent = UniqueExtents.getIncomplete(extents);
                if (extent == null) {
                    // The plugin-provided Metadata instance seems to contain Extents
                    // that are complete enough, so we will not try to complete them.
                    computeExtents = false;
                    extents = null;
                }
            }
        }
        /*
         * Check if we should complete the content info and the spatial representation info.
         * If the plugin-provided metadata declare explicitly such information, we will not
         * compute them in this method (the plugin information will have precedence).
         */
        final Collection<ContentInformation>    contentInfo = metadata.getContentInfo();
        final Collection<SpatialRepresentation> spatialInfo = metadata.getSpatialRepresentationInfo();
        final boolean computeContent = (contentInfo != null) && contentInfo.isEmpty();
        final boolean computeSpatial = (spatialInfo != null) && spatialInfo.isEmpty();
        if (computeContent || computeSpatial || computeResolutions || computeExtents) {
            final GenericName coverageName = getCoverageName();
            if (computeContent || computeSpatial) {

                CoverageDescription ci = null;
                final SpatialMetadata coverageMetadata = getCoverageMetadata();
                if (coverageMetadata != null) {
                    if (computeContent) {
                        ci = coverageMetadata.getInstanceForType(ImageDescription.class);
                        if (ci != null) {
                            contentInfo.add(ci);
                        }
                    }
                    if (computeSpatial) {
                        final Georectified rectified = coverageMetadata.getInstanceForType(Georectified.class);
                        if (rectified != null) {
                            metadata.getSpatialRepresentationInfo().add(rectified);
                        }
                    }
                }

                /*
                 * Get or create the content info to store sample dimensions
                 */
                if (ci==null) {
                    //get or create it
                    if (contentInfo.size()>0) {
                        CoverageDescription cd = contentInfo.stream().limit(1)
                                .filter(CoverageDescription.class::isInstance)
                                .map(CoverageDescription.class::cast)
                                .findFirst().orElse(null);
                        if (cd instanceof ModifiableMetadata && ((ModifiableMetadata)cd).isModifiable()) {
                            ci = cd;
                        }
                    } else {
                        ci = new DefaultCoverageDescription();
                        contentInfo.add(ci);
                    }
                }

                if (ci!=null && ci.getAttributeGroups()!=null && ci.getAttributeGroups().isEmpty() && ci.getDimensions().isEmpty()) {
                    final List<SampleDimension> sampleDimensions = getSampleDimensions();
                    if (sampleDimensions!=null) {
                        final MetadataBuilder mb = new MetadataBuilder();
                        for (int idx=0,n=sampleDimensions.size();idx<n;idx++) {
                            SampleDimension gsd = sampleDimensions.get(idx).forConvertedValues(true);
                            final Unit<? extends Quantity<?>> units = gsd.getUnits().orElse(null);
                            mb.newSampleDimension();
                            mb.setBandIdentifier(Names.createMemberName(null, null, ""+idx, Integer.class));
                            mb.addBandDescription(gsd.getName().toString());
                            if(units!=null) mb.setSampleUnits(units);
                            mb.addMinimumSampleValue(SampleDimensionUtils.getMinimumValue(gsd));
                            mb.addMaximumSampleValue(SampleDimensionUtils.getMaximumValue(gsd));
                            gsd = gsd.forConvertedValues(false);
                            gsd.getTransferFunctionFormula().ifPresent((f) -> {
                                mb.setTransferFunction(f.getScale(), f.getOffset());
                            });
                        }
                        final DefaultMetadata meta = mb.build(false);
                        final CoverageDescription imgDesc = (CoverageDescription) meta.getContentInfo().iterator().next();
                        ci.getAttributeGroups().addAll((Collection)imgDesc.getAttributeGroups());
                    }
                }

            }
            if (computeResolutions || computeExtents) {
                /*
                 * Resolution along the horizontal axes only, ignoring all other axes. For linear units (feet,
                 * kilometres, etc.), we convert the units to metres for compliance with a current limitation
                 * of Apache SIS, which can handle only metres. For angular resolution (typically in degrees),
                 * we perform an APPROXIMATIVE conversion to metres using the nautical mile definition. This
                 * conversion is only valid along the latitudes axis (the number is wrong along the longitude
                 * axis), and more accurate for mid-latitude (the numbers are differents close to equator or
                 * to the poles).
                 */
                final GridGeometry gg = getGridGeometry();
                if (computeResolutions && gg.isDefined(GridGeometry.CRS)) {

                    double[] res = null;
                    try {
                        res = gg.getResolution(false);
                    } catch (IncompleteGridGeometryException ex) {
                    }

                    final Quantity<?> m = CRSUtilities.getHorizontalResolution(
                            gg.getCoordinateReferenceSystem(), res);
                    if (m != null) {
                        double  measureValue = m.getValue().doubleValue();
                        final Unit<?>   unit = m.getUnit();
                        Unit<?> standardUnit = null;
                        double  scaleFactor = 1;
                        if (Units.isAngular(unit)) {
                            standardUnit = Units.DEGREE;
                            scaleFactor  = (1852*60); // From definition of nautical miles.
                        } else if (Units.isLinear(unit)) {
                            standardUnit = Units.METRE;
                        }
                        if (standardUnit != null) try {
                            measureValue = unit.getConverterToAny(standardUnit).convert(measureValue) * scaleFactor;
                            final DefaultResolution resolution = new DefaultResolution();
                            resolution.setDistance(measureValue);
                            if (resolutions == null) {
                                resolutions = new LinkedHashSet<>();
                            }
                            resolutions.add(resolution);
                        } catch (IncommensurableException e) {
                            // In case of failure, do not create a Resolution object.
                            Logging.recoverableException(LOGGER, AbstractGridCoverageReader.class, "getMetadata", e);
                        }
                    }
                }
                /*
                * Horizontal, vertical and temporal extents. The horizontal extents is
                * represented as a geographic bounding box, which may require a reprojection.
                */
                if (computeExtents && gg.isDefined(GridGeometry.ENVELOPE)) {
                    if (extent == null) {
                        extent = new UniqueExtents();
                    }
                    try {
                        extent.addElements(gg.getEnvelope());
                    } catch (TransformException e) {
                        // Not a big deal if we fail. We will just let the identification section unchanged.
                        if (!failed) {
                            failed = true; // Log only once.
                            Logging.recoverableException(LOGGER, AbstractGridCoverageReader.class, "getMetadata", e);
                        }
                    }
                }
            }
        }
        /*
         * At this point, we have computed extents and resolutions from every images
         * in the stream. Now store the result. Note that we unconditionally create
         * a copy of the identification info, even if the original object was already
         * an instance of DefaultDataIdentification, because the original object may
         * be cached in the ImageReader.
         */
        if (extent != null || resolutions != null) {
            final DefaultDataIdentification copy = new DefaultDataIdentification(identification);
            if (extent != null) {
                if (extents != null) {
                    copy.setExtents(extents);
                } else {
                    copy.getExtents().add(extent);
                }
            }
            if (resolutions != null) {
                copy.setSpatialResolutions(resolutions);
            }
            identification = copy;
        }
        if (identification != null) {
            metadata.getIdentificationInfo().add(identification);
        }
        return metadata;
    }

    /**
     * Returns the metadata associated with the input source as a whole, or {@code null} if none.
     * The default implementation delegates to the {@linkplain #imageReader image reader}, wrapping
     * the {@link IIOMetadata} in a {@code SpatialMetadata} if necessary.
     *
     * @return The metadata associated with the input source as a whole, or {@code null}.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     *
     * @since 3.14
     */
    public SpatialMetadata getStreamMetadata() throws CoverageStoreException {
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader == null) {
            throw new IllegalStateException(formatErrorMessage(Errors.Keys.NoImageInput));
        }
        try {
            final IIOMetadata metadata = imageReader.getStreamMetadata();
            if (metadata instanceof SpatialMetadata) {
                return (SpatialMetadata) metadata;
            } else if (metadata != null) {
                return new SpatialMetadata(true, imageReader, metadata);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
    }

    /**
     * Returns the metadata associated with the given coverage, or {@code null} if none.
     * The default implementation delegates to the {@linkplain #imageReader image reader},
     * wrapping the {@link IIOMetadata} in a {@code SpatialMetadata} if necessary.
     *
     * @return The metadata associated with the given coverage, or {@code null}.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     *
     * @since 3.14
     */
    public SpatialMetadata getCoverageMetadata() throws CoverageStoreException {
        final int index = 0;
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader == null) {
            throw new IllegalStateException(formatErrorMessage(Errors.Keys.NoImageInput));
        }
        try {
            final IIOMetadata metadata = imageReader.getImageMetadata(index);
            if (metadata instanceof SpatialMetadata) {
                return (SpatialMetadata) metadata;
            } else if (metadata != null) {
                return new SpatialMetadata(false, imageReader, metadata);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
    }

    /**
     * Gets the spatial metadata from the given image reader, or return {@code null}
     * if none were found. This method asks only for the metadata nodes listed in the
     * {@link #METADATA_NODES} collection. Note however that most {@link ImageReader}
     * implementations will return all metadata anyway.
     *
     * @param  imageReader The image reader from which to get the metadata.
     * @param  index The index of the image to be queried.
     * @return The metadata of the given index, or {@code null} if none.
     * @throws IOException If an error occurred while reading the metadata.
     *
     * @see #getCoverageMetadata(int)
     */
    private SpatialMetadata getImageMetadata(final ImageReader imageReader, final int index) throws IOException {
        if (imageMetadataIndex != index) {
            final IIOMetadata metadata = imageReader.getImageMetadata(index, GEOTK_FORMAT_NAME, METADATA_NODES);
            if (metadata == null || metadata instanceof SpatialMetadata) {
                imageMetadata = (SpatialMetadata) metadata;
            } else {
                imageMetadata = new SpatialMetadata(false, imageReader, metadata);
            }
            imageMetadataIndex = index;
        }
        return imageMetadata;
    }

    /**
     * Converts geodetic parameters to image parameters, reads the image and wraps it in a
     * grid coverage. First, this method creates an initially empty block of image parameters
     * by invoking the {@link #createImageReadParam(int)} method. The image parameter
     * {@linkplain ImageReadParam#setSourceRegion source region},
     * {@linkplain ImageReadParam#setSourceSubsampling source subsampling} and
     * {@linkplain ImageReadParam#setSourceBands source bands} are computed from the
     * parameter given to this {@code read} method. Then, the following image parameters
     * are set (if the image parameter class allows such settings):
     * <p>
     * <ul>
     *   <li><code>{@linkplain SpatialImageReadParam#setSampleConversionAllowed
     *       setSampleConversionAllowed}({@linkplain SampleConversionType#REPLACE_FILL_VALUES
     *       REPLACE_FILL_VALUES}, true)</code> in order to allow the replacement of
     *       fill values by {@link Float#NaN NaN}.</li>
     *
     *   <li><code>{@linkplain SpatialImageReadParam#setSampleConversionAllowed
     *       setSampleConversionAllowed}({@linkplain SampleConversionType#SHIFT_SIGNED_INTEGERS
     *       SHIFT_SIGNED_INTEGERS}, true)</code> if the sample dimensions declare an unsigned
     *       range of sample values.</li>
     *
     *   <li><code>{@linkplain MosaicImageReadParam#setSubsamplingChangeAllowed
     *       setSubsamplingChangeAllowed}(true)</code> in order to allow {@link MosaicImageReader}
     *       to use a different resolution than the requested one. This is crucial from a
     *       performance point of view. Since the {@code GridCoverageReader} contract does not
     *       guarantee that the grid geometry of the returned coverage is the requested geometry,
     *       we are allowed to do that.</li>
     * </ul>
     * <p>
     * Finally, the image is read and wrapped in a {@link GridCoverage2D} using the
     * information provided by {@link #getGridGeometry(int)} and {@link #getSampleDimensions(int)}.
     *
     * /!\ If {@link org.geotoolkit.coverage.io.GridCoverageReadParam#setDeferred(boolean)} parameter is set to true, the
     * returned coverage will rely on the current reader to cache it's data on the fly, so you CANNOT dispose of the current
     * reader while your using the resulting coverage.
     */
    @Override
    public GridCoverage2D read(final GridCoverageReadParam param)
            throws DataStoreException, CancellationException
    {
        final int index = 0;
        final boolean loggingEnabled = isLoggable();
        long fullTime = (loggingEnabled) ? System.nanoTime() : 0;
        ignoreGridTransforms = !loggingEnabled;
        /*
         * Parameters check.
         */
        abortRequested = false;
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader == null) {
            throw new IllegalStateException(formatErrorMessage(Errors.Keys.NoImageInput));
        }
        GridGeometry2D gridGeometry = getGridGeometry();
        checkAbortState();
        final ImageReadParam imageParam;
        try {
            imageParam = createImageReadParam(0);
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        final int[] srcBands;
        final int[] dstBands;
        MathTransform2D destToExtractedGrid = null;
        boolean supportBandSelection = true;
        if (param != null) {
            srcBands = param.getSourceBands();
            dstBands = param.getDestinationBands();
            if (srcBands != null && dstBands != null && srcBands.length != dstBands.length) {
                throw new IllegalArgumentException(Errors.getResources(locale).getString(
                        Errors.Keys.MismatchedArrayLength_2, "sourceBands", "destinationBands"));
            }
            /*
             * Convert geodetic envelope and resolution to pixel coordinates.
             * Store the result of the above conversions in the ImageReadParam object.
             */
            destToExtractedGrid = geodeticToPixelCoordinates(gridGeometry, param, imageParam, false);
            /*
             * Conceptually we could compute right now:
             *
             *     AffineTransform change = new AffineTransform();
             *     change.translate(sourceRegion.x, sourceRegion.y);
             *     change.scale(xSubsampling, ySubsampling);
             *
             * However this implementation will scale only after the image has been read,
             * because the MosaicImageReader may have changed the subsampling to more
             * efficient values if it was authorized to make such change.
             */
            if (srcBands != null) {
                //-- TODO : implement into image tiff reader source and dest band.
                //-- particulaity case for tiff image reader which does not support setbands indexes.
                try {
                    imageParam.setSourceBands(srcBands);
                } catch(UnsupportedOperationException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage()+"Read coverage without set any source bands.");
                    supportBandSelection = false;
                }
            }

            if (dstBands != null) {
                try {
                    imageParam.setDestinationBands(dstBands);
                } catch(UnsupportedOperationException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage()+"Read coverage without set any destination bands.");
                }
            }
        } else {
            srcBands = null;
            dstBands = null;
        }
        /*
         * At this point, the standard parameters (source region, source bands) are set.
         * The following is Geotk-specific. First, allow MosaicImageReader to use a different
         * resolution than the requested one. This is crucial from a performance point of view.
         * Since the GridCoverageReader contract does not guarantee that the grid geometry of the
         * returned coverage is the requested geometry, we are allowed to do that.
         */
        if (imageParam instanceof MosaicImageReadParam) {
            // Note: we don't create a new ImageReadParam if it is null
            // since we would be reading the image at full resolution anyway.
            ((MosaicImageReadParam) imageParam).setSubsamplingChangeAllowed(true);
        }
        /*
         * Next, check if we should allow the image reader to add an offset to signed intergers
         * in order to make them unsigned. We will allow such offset if the SampleDimensions
         * declare unsigned range of sample values.
         */
        boolean usePaletteFactory = false;
        final SampleDimension[] bands = getSampleDimensions(index,
                supportBandSelection ? srcBands : null,
                supportBandSelection ? dstBands : null);
        if (imageParam instanceof SpatialImageReadParam) {
            final SpatialImageReadParam sp = (SpatialImageReadParam) imageParam;
            if (!isRangeSigned(bands)) {
                sp.setSampleConversionAllowed(SampleConversionType.SHIFT_SIGNED_INTEGERS, true);
            }
            sp.setSampleConversionAllowed(SampleConversionType.REPLACE_FILL_VALUES, true);
            /*
             * If the image does not have its own color palette, provides a palette factory
             * which will create the IndexColorModel (if needed) from the SampleDimension.
             */
            if (bands != null && imageReader instanceof SpatialImageReader) try {
                usePaletteFactory = !((SpatialImageReader) imageReader).hasColors(index);
            } catch (IOException e) {
                throw new CoverageStoreException(formatErrorMessage(e), e);
            }
            /*
             * If there is supplemental dimensions (over the usual 2 dimensions) and the subclass
             * implementation did not defined explicitely some dimension slices, then convert the
             * envelope bounds in those supplemental dimensions to slices index.
             *
             * TODO: there is some duplication between this code and the work done in the parent
             *       class. We need to refactor geodeticToPixelCoordinates(…) in a new helper
             *       class for making easier to divide the work in smaller parts.
             */
            if (param != null && !sp.hasDimensionSlices()) {
                final int gridDim = gridGeometry.getDimension();
                if (gridDim > 2) { // max(X_DIMENSION, Y_DIMENSION) + 1
                    final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
                    final int geodeticDim = crs.getCoordinateSystem().getDimension();
                    if (geodeticDim > 2) {
                        Envelope envelope = param.getEnvelope();
                        if (envelope != null && envelope.getDimension() > 2) try {
                            if (crs instanceof CompoundCRS) {
                                envelope = CRSUtilities.appendMissingDimensions(envelope, (CompoundCRS) crs);
                            }
                            envelope = Envelopes.transform(envelope, crs);
                            final double[] median = new double[geodeticDim];
                            for (int i=0; i<geodeticDim; i++) {
                                median[i] = envelope.getMedian(i);
                            }
                            final double[] indices = new double[gridDim];
                            gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER).inverse().transform(median, 0, indices, 0, 1);
                            final GridExtent gridExtent;
                            if (crs instanceof GridGeometry) {
                                gridExtent = ((GridGeometry) crs).getExtent();
                            } else {
                                // We can not fallback on gridGeometry.getExtent(), because
                                // GridGeometry2D contract forces all extra dimensions to have
                                // a span of 1.
                                gridExtent = null;
                            }
                            for (int i=0; i<gridDim; i++) {
                                if (i != gridGeometry.gridDimensionX && i != gridGeometry.gridDimensionY) {
                                    final double sliceIndex = indices[i];
                                    if (!Double.isNaN(sliceIndex)) {
                                        final DimensionSlice slice = sp.newDimensionSlice();
                                        slice.addDimensionId(i);
                                        slice.setSliceIndex((int) Math.round(
                                                Math.max(gridExtent != null ? gridExtent.getLow (i) : Integer.MIN_VALUE,
                                                Math.min(gridExtent != null ? gridExtent.getHigh(i) : Integer.MAX_VALUE,
                                                sliceIndex))));
                                    }
                                }
                            }
                        } catch (TransformException e) {
                            throw new CoverageStoreException(formatErrorMessage(e), e);
                        }
                    }
                }
            }
        }
        checkAbortState();
        /*
         * Read the image using the ImageReader.read(...) method.  We could have used
         * ImageReader.readAsRenderedImage(...) instead in order to give the reader a
         * chance to return a tiled image,  but experience with some formats suggests
         * that it requires to keep the ImageReader with its input stream open.
         */
        final String name;
        RenderedImage image;
        try {
            final GenericName gname = getCoverageName();
            try {
                name = (gname != null) ? gname.toString() : null;
            } catch (BackingStoreException e) {
                throw e.unwrapOrRethrow(IOException.class);
            }
            if (usePaletteFactory) {
                SampleDimensionPalette.BANDS.set(bands);
                ((SpatialImageReadParam) imageParam).setPaletteFactory(SampleDimensionPalette.FACTORY);
            }
            if (param != null && param.isDeferred()) {
                image = new LargeRenderedImage(imageReader.getOriginatingProvider(), imageParam, imageReader.getInput(), index, null, null);
            } else {
                image = imageReader.read(index, imageParam);
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        } finally {
            if (usePaletteFactory) {
                SampleDimensionPalette.BANDS.remove();
            }
        }
        /*
         * If the grid geometry changed as a result of subsampling or reading a smaller region,
         * update the grid geometry. The (xmin, ymin) values are usually (0,0), but we take
         * them in account anyway as a paranoiac safety (a previous version of this code used
         * the 'readAsRenderedImage(...)' method, which could have shifted the image).
         */
        if (param != null) {
            final Rectangle sourceRegion = imageParam.getSourceRegion();
            final AffineTransform change = AffineTransform.getTranslateInstance(sourceRegion.x, sourceRegion.y);
            change.scale(imageParam.getSourceXSubsampling(), imageParam.getSourceYSubsampling());
            final int xmin = image.getMinX();
            final int ymin = image.getMinY();
            final int xi = gridGeometry.gridDimensionX;
            final int yi = gridGeometry.gridDimensionY;
            final MathTransform gridToCRS = gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER);
            MathTransform newGridToCRS = gridToCRS;
            if (!change.isIdentity()) {
                final int gridDimension = gridToCRS.getSourceDimensions();
                final Matrix matrix = Matrices.createIdentity(gridDimension + 1);
                matrix.setElement(xi, xi, change.getScaleX());
                matrix.setElement(yi, yi, change.getScaleY());
                matrix.setElement(xi, gridDimension, change.getTranslateX() - xmin);
                matrix.setElement(yi, gridDimension, change.getTranslateY() - ymin);
                newGridToCRS = MathTransforms.concatenate(MathTransforms.linear(matrix), gridToCRS);
            }
            final GridExtent gridExtent = gridGeometry.getExtent();
            final long[] low  = GridGeometryIterator.getLow(gridExtent);
            final long[] high = GridGeometryIterator.getHigh(gridExtent);
            low[xi] = xmin; high[xi] = xmin + image.getWidth()  - 1;
            low[yi] = ymin; high[yi] = ymin + image.getHeight() - 1;
            if (imageParam instanceof SpatialImageReadParam) {
                for (final DimensionSlice slice : ((SpatialImageReadParam) imageParam).getDimensionSlices()) {
                    for (final Object id : slice.getDimensionIds()) {
                        if (id instanceof Integer) {
                            final int dim = (Integer) id;
                            low[dim] = high[dim] = slice.getSliceIndex();
                        }
                    }
                }
            }
            final GridExtent newGridRange = new GridExtent(null, low, high, true);
            if (newGridToCRS != gridToCRS || !newGridRange.equals(gridExtent)) {
                gridGeometry = new GridGeometry2D(newGridRange, PixelInCell.CELL_CORNER,
                        newGridToCRS, gridGeometry.getCoordinateReferenceSystem());
            }
        }
        final GridCoverage2D coverage;
        final GridCoverageBuilder builder = coverageBuilder;
        try {
            builder.setName(name);
            builder.setRenderedImage(image);
            builder.setSampleDimensions(bands);
            builder.setGridGeometry(gridGeometry);
            coverage = builder.getGridCoverage2D();
        } finally {
            builder.reset();
        }
        if (loggingEnabled) {
            fullTime = System.nanoTime() - fullTime;
            final Level level = getLogLevel(fullTime);
            if (LOGGER.isLoggable(level)) {
                ImageCoverageStore.logOperation(level, locale, ImageCoverageReader.class, false,
                        input, index, coverage, null, null, destToExtractedGrid, fullTime);
            }
        }
        return coverage;
    }

    /**
     * Cancels the read operation. The default implementation forward the call to the
     * {@linkplain #imageReader image reader}, if any. The content of the coverage
     * following the abort will be undefined.
     */
    @Override
    public void abort() {
        super.abort();
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader != null) {
            imageReader.abort();
        }
    }

    /**
     * Returns an error message for the given exception. If the {@linkplain #input input} is
     * known, this method returns "<cite>Can't read 'the name'</cite>" followed by the cause
     * message. Otherwise it returns the localized message of the given exception.
     */
    @Override
    final String formatErrorMessage(final Throwable e) {
        return formatErrorMessage(input, e, false);
    }

    /**
     * Closes the input used by the {@link ImageReader}, provided that the stream was not
     * given explicitly by the user. The {@link ImageReader} is not disposed, so it can be
     * reused for the next image to read.
     *
     * @throws IOException if an error occurs while closing the input.
     */
    private void close() throws IOException {
        final Object oldInput = input;
        input = null; // Clear now in case the code below fails.
        coverageNames = null;
        XCollections.clear(gridGeometries);
        XCollections.clear(sampleDimensions);
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader != null) {
            if (imageReader.getInput() != oldInput) {
                XImageIO.close(imageReader);
            } else {
                imageReader.setInput(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ImageReader#reset()
     */
    @Override
    public void reset() throws DataStoreException {
        input = null;
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (imageReader != null) {
            imageReader.reset();
        }
        helper             = null;
        imageMetadata      = null;
        imageMetadataIndex = -1;
        super.reset();
    }

    /**
     * Allows any resources held by this reader to be released. The result of calling any other
     * method subsequent to a call to this method is undefined.
     * <p>
     * The default implementation closes the {@linkplain #imageReader image reader} input if
     * the later is a stream, then {@linkplain ImageReader#dispose() disposes} that reader.
     *
     * @see ImageReader#dispose()
     */
    @Override
    public void dispose() throws DataStoreException {
        input = null;
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (imageReader != null) {
            imageReader.dispose();
            imageReader = null;
        }
        helper = null;
        super.dispose();
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
}
