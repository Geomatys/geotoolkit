/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.image.RenderedImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.io.LineWriter;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.referencing.crs.DefaultImageCRS;
import org.geotoolkit.referencing.operation.transform.IdentityTransform;


/**
 * A {@link GridCoverageReader} implementation which use an {@link ImageReader} for reading
 * sample values. This implementation stores the sample values in a {@link RenderedImage},
 * and consequently is targeted toward two-dimensional slices of data.
 * <p>
 * {@code ImageCoverageReader} basically works as a layer which converts <cite>geodetic
 * coordinates</cite> (for example the region to read) to <cite>pixel coordinates</cite>
 * before to pass them to the wrapped {@code ImageReader}, and conversely: from pixel
 * coordinates to geodetic coordinates. The later conersion is called "<cite>grid to CRS</cite>"
 * and is determined from the {@link SpatialMetadata} provided by the {@code ImageReader}.
 *
 * {@section Default metadata value}
 * If no <cite>grid to CRS</cite> conversion or no {@linkplain CoordinateReferenceSystem
 * Coordinate Reference System} object can be created from the {@link SpatialMetadata},
 * then {@code ImageCoverageReader} invokes the {@link #getDefaultMetadata(int, Class)}
 * method in order to get a default value. Subclasses can override that method in order
 * to provide different default values.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from 2.2)
 * @module
 */
public class ImageCoverageReader extends GridCoverageReader {
    /**
     * The name of metadata nodes we are interrested in. Some implementations of
     * {@link ImageReader} may use this information for reading only the metadata
     * we are interrested in.
     *
     * @see SpatialMetadataFormat
     */
    private static final Set<String> METADATA_NODES;
    static {
        final Set<String> s = new HashSet<String>(25);
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
     * Optional parameter to be given (if non-null) to the
     * <code>{@linkplain #imageReader}.{@linkplain ImageReader#setInput(Object, boolean, boolean)
     * setInput}(&hellip;, seekForwardOnly, &hellip;)</code> method.
     *
     * If {@code TRUE}, images and metadata may only be read in ascending order from the input
     * source. If {@code FALSE}, they may be read in any order. If {@code null}, then this
     * parameter is not given to the {@linkplain #imageReader image reader} which is free to
     * use a plugin-dependent default (usually {@code false}).
     */
    protected Boolean seekForwardOnly;

    /**
     * Optional parameter to be given (if non-null) to the
     * <code>{@linkplain #imageReader}.{@linkplain ImageReader#setInput(Object, boolean, boolean)
     * setInput}(&hellip;, &hellip;, ignoreMetadata)</code> method.
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
    private List<String> coverageNames;

    /**
     * The grid coverage factory to use.
     */
    private final GridCoverageFactory factory;

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
        this.factory = CoverageFactoryFinder.getGridCoverageFactory(hints);
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
    }

    /**
     * Sets the given locale to the given {@link ImageReader}, provided that the image reader
     * supports the language of that locale. Otherwise set the reader locale to {@code null}.
     *
     * @see ImageReader#setLocale(Locale)
     */
    private static void setLocale(final ImageReader reader, final Locale locale) {
        if (reader != null && locale != null) {
            final Locale[] list = reader.getAvailableLocales();
            for (int i=list.length; --i>=0;) {
                if (locale.equals(list[i])) {
                    reader.setLocale(locale);
                    return;
                }
            }
            final String language = getISO3Language(locale);
            if (language != null) {
                for (int i=list.length; --i>=0;) {
                    final Locale candidate = list[i];
                    if (language.equals(getISO3Language(candidate))) {
                        reader.setLocale(candidate);
                        return;
                    }
                }
            }
            reader.setLocale(null);
        }
    }

    /**
     * Returns the ISO language code for the specified locale, or {@code null} if not available.
     * This is used for finding a match when the locale given to the {@link #setLocale(Locale)}
     * method does not match exactly the locale supported by the image reader. In such case, we
     * will pickup a locale for the same language even if it is not the same country.
     */
    private static String getISO3Language(final Locale locale) {
        try {
            return locale.getISO3Language();
        } catch (MissingResourceException exception) {
            return null;
        }
    }

    /**
     * Sets the input source to the given object. The input is typically a
     * {@link java.io.File}, {@link java.net.URL} or {@link String} object,
     * but other types (especially {@link ImageInputStream}) may be accepted
     * as well depending on the {@linkplain #imageReader image reader} implementation.
     * <p>
     * The given input can also be an {@link ImageReader} instance with its input initialized,
     * in which case it is used directly as the {@linkplain #imageReader image reader} wrapped
     * by this {@code ImageCoverageReader}.
     *
     * {@section Overriding in subclasses}
     * Subclasses wanting a different behavior should consider overriding the following
     * methods before to override this {@code setInput(Object)} method:
     * <p>
     * <ul>
     *   <li>{@link #canReuseImageReader(ImageReaderSpi, Object)} - for determining if the
     *       current {@linkplain #imageReader image reader} can be reused.</li>
     *   <li>{@link #createImageReader(Object)} - for creating a new
     *       {@linkplain #imageReader image reader} for the given input.</li>
     * </ul>
     */
    @Override
    public void setInput(final Object input) throws CoverageStoreException {
        final ImageReader oldReader = imageReader;
        final boolean disposeReader = (oldReader != null && oldReader != this.input);
        // The above must be determined before the call to close(),
        // because the above-cited method set this.input to null.
        try {
            close();
            assert (oldReader == null) || (oldReader.getInput() == null) : oldReader;
            if (input != null) {
                ImageReader newReader = null;
                if (input instanceof ImageReader) {
                    newReader = (ImageReader) input;
                    // The old reader will be disposed and the locale will be set below.
                } else {
                    /*
                     * First, check if the current reader can be reused. If the user
                     * didn't overriden the canReuseImageReader(...) method, then the
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
                     * overriden the createImageReader(...) method, then the default behavior
                     * is to get an image reader by the extension.
                     */
                    if (newReader == null) {
                        newReader = createImageReader(input);
                    }
                    /*
                     * Set the input if it was not already done. In the default implementation,
                     * this is done by 'createImageReader' but not by 'canReuseImageReader'.
                     * However the user could have overriden the above-cited methods with a
                     * different behavior.
                     */
                    if (newReader != input && newReader.getInput() == null) {
                        if (seekForwardOnly != null) {
                            if (ignoreMetadata != null) {
                                newReader.setInput(input, seekForwardOnly, ignoreMetadata);
                            } else {
                                newReader.setInput(input, seekForwardOnly);
                            }
                        } else {
                            newReader.setInput(input);
                        }
                    }
                }
                if (newReader != oldReader) {
                    if (disposeReader) {
                        oldReader.dispose();
                    }
                    setLocale(newReader, locale);
                }
                imageReader = newReader;
            }
        } catch (IOException e) {
            throw new CoverageStoreException(error(input, e), e);
        }
        super.setInput(input);
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
     * {@link javax.imageio.stream.ImageInputStream}), then this method fallbacks on
     * {@link ImageReaderSpi#canDecodeInput(Object)}.
     * <p>
     * Subclasses can override this method if they want to determine in another way
     * whatever the {@linkplain #imageReader image reader} can be reused. Subclasses
     * don't need to set the image reader input; this will be done by the caller.
     *
     * @param  provider The provider of the image reader.
     * @param  input The input to set to the image reader.
     * @return {@code true} if the image reader can be reused.
     * @throws IOException If an error occured while determining if the current
     *         image reader can use the given input.
     */
    protected boolean canReuseImageReader(final ImageReaderSpi provider, final Object input) throws IOException {
        if (IOUtilities.canProcessAsPath(input)) {
            final String[] suffixes = provider.getFileSuffixes();
            return suffixes != null && XArrays.containsIgnoreCase(suffixes, IOUtilities.extension(input));
        } else {
            return provider.canDecodeInput(input);
        }
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the given input.
     * This method is invoked automatically by {@link #setInput(Object)} for creating
     * a new {@linkplain #imageReader image reader}. The image reader input must be set.
     * <p>
     * The default implementation delegates to {@link XImageIO#getReaderBySuffix(Object,
     * Boolean, Boolean)}. Subclasses can override this method if they want to create a
     * new {@linkplain #imageReader image reader} in another way.
     *
     * @param  input The input source.
     * @return An initialized image reader for reading the given input.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     */
    protected ImageReader createImageReader(final Object input) throws IOException {
        return XImageIO.getReaderBySuffix(input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Returns the default metadata for an object of the given type. This method is invoked
     * by {@link #getGridGeometry(int)} when a required metadata object can not be created.
     * The {@code type} argument can be any of the types listed in left column of the table
     * below. The values listed in the right columns are the default values provided by the
     * default implementation of this method.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Type</th><th>Default value</th></tr>
     *   <tr><td>&nbsp;{@link CoordinateReferenceSystem}&nbsp;</td>
     *       <td>&nbsp;{@link DefaultImageCRS#GRID_2D}&nbsp;</td></tr>
     * </table>
     *
     * @param  <T>   The type of the metadata object to return.
     * @param  index The index of the image for which a metadata value is required.
     * @param  type  The type of the metadata object to return.
     * @return The default metadata value for an object of the given kind.
     * @throws CoverageStoreException If an error occured while determining the default value.
     */
    protected <T> T getDefaultMetadata(final int index, final Class<T> type) throws CoverageStoreException {
        Object value = null;
        if (type.equals(CoordinateReferenceSystem.class)) {
            value = DefaultImageCRS.GRID_2D;
        }
        return type.cast(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCoverageNames() throws CoverageStoreException {
        if (coverageNames == null) {
            final ImageReader imageReader = this.imageReader; // Protect from changes.
            if (imageReader == null) {
                throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
            }
            try {
                if (imageReader instanceof NamedImageStore) {
                    coverageNames = ((NamedImageStore) imageReader).getImageNames();
                } else {
                    coverageNames = new NameList(getInputName(), imageReader.getNumImages(true));
                }
            } catch (IOException e) {
                throw new CoverageStoreException(error(e), e);
            }
        }
        return coverageNames;
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
     * @throws IOException If an error occured while reading the metadata.
     */
    private static SpatialMetadata getSpatialMetadata(final ImageReader imageReader, final int index)
            throws IOException
    {
        final IIOMetadata metadata = imageReader.getImageMetadata(index,
                SpatialMetadataFormat.FORMAT_NAME, METADATA_NODES);
        if (metadata instanceof SpatialMetadata) {
            return (SpatialMetadata) metadata;
        } else if (metadata != null) {
            return new SpatialMetadata(SpatialMetadataFormat.IMAGE, imageReader, metadata);
        } else {
            return null;
        }
    }

    /**
     * Returns the grid geometry for the {@link GridCoverage2D} to be read at the given index.
     * The default implementation performs the following:
     * <p>
     * <ul>
     *   <li>The {@link org.opengis.coverage.grid.GridEnvelope} is determined from the image
     *       {@linkplain ImageReader#getWidth(int) width} and
     *       {@linkplain ImageReader#getHeight(int) height}.</li>
     *   <li>The {@link CoordinateReferenceSystem} and the "<cite>grid to CRS</cite>" conversion
     *       are determined from the {@link SpatialMetadata} if any, or from the values returned
     *       by {@link #getDefaultMetadata(int, Class)} otherwise.</li>
     * </ul>
     */
    @Override
    public GridGeometry2D getGridGeometry(final int index) throws CoverageStoreException {
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader == null) {
            throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
        }
        /*
         * Get the metadata.
         */
        CoordinateReferenceSystem crs = null;
        MathTransform gridToCRS = null;
        final int width, height;
        try {
            width  = imageReader.getWidth(index);
            height = imageReader.getHeight(index);
            final SpatialMetadata metadata = getSpatialMetadata(imageReader, index);
            if (metadata != null) {
                crs = metadata.getInstanceForType(CoordinateReferenceSystem.class);
                // TODO get gridToCRS.
            }
        } catch (IOException e) {
            throw new CoverageStoreException(error(e), e);
        }
        /*
         * Replace missing metadata by their default values
         * and create the grid geometry.
         */
        if (crs == null) {
            crs = getDefaultMetadata(index, CoordinateReferenceSystem.class);
        }
        final int dimension = crs.getCoordinateSystem().getDimension();
        if (gridToCRS == null) {
            gridToCRS = IdentityTransform.create(dimension);
        }
        final int[] lower = new int[dimension];
        final int[] upper = new int[dimension];
        Arrays.fill(upper, 1);
        upper[0] = width;
        upper[1] = height;
        final GeneralGridEnvelope gridRange = new GeneralGridEnvelope(lower, upper, false);
        return new GridGeometry2D(gridRange, gridToCRS, crs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(final int index) throws CoverageStoreException {
        return null;
    }

    /**
     * Reads the grid coverage. The default implementation creates a grid coverage using
     * the information provided by {@link #getGridGeometry(int)}.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public GridCoverage2D read(final int index, final GridCoverageReadParam param)
            throws CoverageStoreException
    {
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        if (imageReader == null) {
            throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
        }
        final ImageReadParam ip = imageReader.getDefaultReadParam();
        final GridGeometry2D gridGeometry = getGridGeometry(index);
        // TODO: configure ip according gridGeometry and the values in 'param'.
        final List<GridSampleDimension> bands = getSampleDimensions(index);
        final Map<?,?> properties = getProperties(index);
        final String name;
        final RenderedImage image;
        try {
            try {
                name = getCoverageNames().get(index);
            } catch (BackingStoreException e) {
                throw e.unwrapOrRethrow(IOException.class);
            }
            image = imageReader.readAsRenderedImage(index, ip);
        } catch (IOException e) {
            throw new CoverageStoreException(error(e), e);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            /*
             * Log the arguments used for creating the GridCoverage. This is a costly logging:
             * the string representations for some argument are very long   (RenderedImage and
             * CoordinateReferenceSystem), and string representation for sample dimensions may
             * use many lines.
             */
            final StringWriter buffer = new StringWriter(         );
            final LineWriter   trimer = new LineWriter  (buffer   );
            final TableWriter   table = new TableWriter (trimer, 1);
            final PrintWriter     out = new PrintWriter (table    );
            buffer.write("Creating GridCoverage[\"");
            buffer.write(name);
            buffer.write("\"] with:");
            buffer.write(trimer.getLineSeparator());
            table.setMultiLinesCells(true);
            final int sdCount = (bands != null) ? bands.size() : 0;
            for (int i=-3; i<sdCount; i++) {
                String key = "";
                Object value;
                switch (i) {
                    case -3: {
                        key = "RenderedImage";
                        value = image;
                        break;
                    }
                    case -2: {
                        key = "CoordinateReferenceSystem";
                        value = gridGeometry.getCoordinateReferenceSystem();
                        break;
                    }
                    case -1: {
                        key = "Envelope";
                        value = gridGeometry.getEnvelope();
                        break;
                    }
                    case 0: {
                        key = "SampleDimensions";
                        // fall through
                    }
                    default: {
                        value = bands.get(i);
                        break;
                    }
                }
                out.print("    ");
                out.print(key   ); table.nextColumn();
                out.print('='   ); table.nextColumn();
                out.print(value ); table.nextLine();
            }
            out.flush();
            LOGGER.fine(buffer.toString());
        }
        return factory.create(name, image, gridGeometry,
                (bands != null) ? bands.toArray(new GridSampleDimension[bands.size()]) : null,
                null, properties);
    }

    /**
     * Returns an error message for the given exception. If the {@linkplain #input input} is
     * known, this method returns "<cite>Can't read 'the name'</cite>". Otherwise it returns
     * the localized message of the given exception.
     */
    private String error(final IOException e) {
        return error(input, e);
    }

    /**
     * Same than {@link #error(IOException)}, but with an explicitly specified input.
     */
    private String error(final Object input, final IOException e) {
        if (IOUtilities.canProcessAsPath(input)) {
            return Errors.getResources(locale).getString(Errors.Keys.CANT_READ_$1, IOUtilities.name(input));
        } else {
            return e.getLocalizedMessage();
        }
    }

    /**
     * Closes the input used by the {@link ImageReader}, provided that this is not the input
     * object explicitly given by the user. The {@link ImageReader} is not disposed, so it
     * can be reused for the next image to read.
     *
     * @throws IOException if an error occurs while closing the input.
     */
    private void close() throws IOException {
        coverageNames = null;
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        final Object oldInput = input;
        input = null; // Clear now in case the code below fails.
        if (imageReader != null) {
            final Object readerInput = imageReader.getInput();
            imageReader.setInput(null);
            if (readerInput != oldInput) {
                if (readerInput instanceof Closeable) {
                    ((Closeable) readerInput).close();
                } else if (readerInput instanceof ImageInputStream) {
                    ((ImageInputStream) readerInput).close();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method {@linkplain ImageReader#dispose() disposes} the {@linkplain #imageReader
     * image reader} if and only if it was not an instance provided explicitly by the user.
     *
     * @see ImageReader#dispose()
     */
    @Override
    public void reset() throws CoverageStoreException {
        final ImageReader reader = imageReader;
        final boolean disposeReader = (reader != null && reader != input);
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        if (disposeReader) {
            reader.dispose();
        }
        imageReader = null;
        super.reset();
    }
}
