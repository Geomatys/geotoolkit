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
package org.geotoolkit.image.io.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.Collections;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;

import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.InformationType;
import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.util.logging.Logging;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Reader for the <cite>World File</cite> format. This reader wraps an other image reader
 * for an "ordinary" image format, like TIFF, PNG or JPEG. This {@code WorldFileImageReader}
 * delegates the reading of pixel values to the wrapped reader, and additionally looks for
 * two small text files in the same directory than the image file, with the same filename
 * but a different extension:
 *
 * <ul>
 *   <li><p>A text file containing the coefficients of the affine transform mapping pixel
 *       coordinates to geodesic coordinates. The reader expects one coefficient per line,
 *       in the same order than the one expected by the
 *       {@link AffineTransform#AffineTransform(double[]) AffineTransform(double[])}
 *       constructor, which is <var>scaleX</var>, <var>shearY</var>, <var>shearX</var>,
 *       <var>scaleY</var>, <var>translateX</var>, <var>translateY</var>.
 *       This reader looks for a file having the following extensions, in preference order:</p>
 *       <ol>
 *         <li>The first letter of the image file extension, followed by the last letter of
 *             the image file extension, followed by {@code 'w'}. Example: {@code "tfw"} for
 *             {@code "tiff"} images, and {@code "jgw"} for {@code "jpeg"} images.</li>
 *         <li>The extension of the image file with a {@code 'w'} appended.</li>
 *         <li>The {@code "tfw} extension.</li>
 *       </ol>
 *   </li>
 *   <li><p>A text file containing the <cite>Coordinate Reference System</cite> (CRS)
 *       definition in <cite>Well Known Text</cite> (WKT) syntax. This reader looks
 *       for a file having the {@code ".prj"} extension.</p></li>
 * </ul>
 *
 * Every text file are expected to be encoded in ISO-8859-1 (a.k.a. ISO-LATIN-1) and every
 * numbers are expected to be formatted in US locale.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see <a href="http://en.wikipedia.org/wiki/World_file">World File Format Description</a>
 * @see WorldFileImageWriter
 *
 * @since 3.08 (derived from 3.07)
 * @module
 */
public class WorldFileImageReader extends ImageReaderAdapter {
    /**
     * {@code true} if the attempt to replace the {@linkplain #input} by a {@link File}
     * has been done. We try to use a {@link File} because it allows us to check if the
     * file exists. Only one attempt will be performed for a new input.
     */
    private boolean inputReplaced;

    /**
     * Constructs a new image reader. The provider argument is mandatory for this constructor.
     * If the provider is unknown, use the next constructor below instead.
     *
     * @param  provider The {@link ImageReaderSpi} that is constructing this object.
     * @throws IOException If an error occurred while creating the {@linkplain #main main} reader.
     */
    public WorldFileImageReader(final Spi provider) throws IOException {
        super(provider);
    }

    /**
     * Constructs a new image reader wrapping the given reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     * @param main The reader to use for reading the pixel values.
     */
    public WorldFileImageReader(final Spi provider, final ImageReader main) {
        super(provider, main);
    }

    /**
     * Creates the input to be given to the reader identified by the given argument. If the
     * {@code readerID} argument is {@code "main"} (ignoring case), then this method delegates
     * to the {@linkplain ImageReaderAdapter#createInput(String) super-class method}. Otherwise
     * this method returns an input which is typically a {@link File} or {@link java.net.URL}
     * having the same name than the {@linkplain #input input} of this reader, but a different
     * extension. The new extension is determined from the {@code readerID} argument, which can
     * be:
     *
     * <ul>
     *   <li><p>{@code "tfw"} for the <cite>World File</cite>. The extension of the returned
     *       input may be {@code "tfw"} (most common), {@code "jgw"}, {@code "pgw"} or other
     *       suffix depending on the extension of this reader {@linkplain #input input}, and
     *       depending which file has been determined to exist. See the
     *       <a href="#skip-navbar_top">class javadoc</a> for more details.</p></li>
     *
     *   <li><p>{@code "prj"} for <cite>Map Projection</cite> file. The extension
     *       of the returned input is {@code "prj"}.</p></li>
     * </ul>
     *
     * Subclasses can override this method for specifying a different main ({@code "main"}),
     * <cite>World File</cite> ({@code "tfw"}) or <cite>Map Projection</cite> ({@code "prj"})
     * input. They can also invoke this method with other identifiers than the three above-cited
     * ones, in which case this method uses the given identifier as the extension of the returned
     * input. However the default {@code WorldFileImageReader} implementation uses only
     * {@code "main"}, {@code "tfw"} and {@code "prj"}.
     *
     * @param  readerID {@code "main"} for the {@linkplain #main main} input,
     *         {@code "tfw"} for the <cite>World File</cite> input, or
     *         {@code "prj"} for the <cite>Map Projection</cite> input. Other
     *         identifiers are allowed but subclass-specific.
     * @return The given kind of input typically as a {@link File} or {@link java.net.URL}
     *         object, or {@code null} if there is no input for the given identifier.
     * @throws IOException If an error occurred while creating the input.
     *
     * @see WorldFileImageWriter#createOutput(String)
     */
    @Override
    protected Object createInput(final String readerID) throws IOException {
        if ("main".equalsIgnoreCase(readerID)) {
            return super.createInput(readerID);
        }
        final ImageReaderSpi spi = originatingProvider;
        if ((spi instanceof Spi) && ((Spi) spi).exclude(readerID)) {
            return null;
        }
        return SupportFiles.changeExtension(input, readerID);
    }

    /**
     * Invokes {@link #createInput(String)} and verifies if the returned file exists.
     * If it does not exist, then returns {@code null}.
     *
     * @todo Current implementation checks only {@link File} object.
     *       We should check URL as well.
     */
    private Object getVerifiedInput(final String part) throws IOException {
        /*
         * Replaces the input by a File object if possible,
         * for allowing us to check if the file exists.
         */
        if (!inputReplaced) {
            input = IOUtilities.tryToFile(input);
            inputReplaced = true;
        }
        Object in = createInput(part);
        if (in instanceof File) {
            if (!((File) in).isFile()) {
                in = null;
            }
        }
        return in;
    }

    /**
     * Creates a new stream or image metadata. This method first delegates to the main reader as
     * documented in the {@linkplain ImageReaderAdapter#createMetadata(int) super-class method},
     * then completes the metadata with information read from the <cite>World File</cite> and
     * <cite>Map Projection</cite> files.
     * <p>
     * The <cite>World File</cite> and <cite>Map Projection</cite> files are determined by calls
     * to the {@link #createInput(String)} method with {@code "tfw"} and {@code "prj"} argument
     * values. Subclasses can override the later method if they want to specify different files
     * to be read.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        SpatialMetadata metadata = super.createMetadata(imageIndex);
        if (imageIndex >= 0) {
            AffineTransform gridToCRS = null;
            CoordinateReferenceSystem crs = null;
            Object in = getVerifiedInput("tfw");
            if (in != null) {
                gridToCRS = SupportFiles.parseTFW(IOUtilities.open(in), in);
            }
            in = getVerifiedInput("prj");
            if (in != null) {
                crs = PrjFiles.read(IOUtilities.open(in), true);
            }
            /*
             * If we have found information in TFW or PRJ files, complete metadata.
             */
            if (gridToCRS != null || crs != null) {
                if (metadata == null) {
                    metadata = new SpatialMetadata(false, this, null);
                }
                if (gridToCRS != null) {
                    final int width  = getWidth (imageIndex);
                    final int height = getHeight(imageIndex);
                    new GridDomainAccessor(metadata).setAll(gridToCRS, new Rectangle(width, height),
                            null, PixelOrientation.UPPER_LEFT);
                }
                if (crs != null) {
                    new ReferencingBuilder(metadata).setCoordinateReferenceSystem(crs);
                }
            }
        }
        return metadata;
    }

    /**
     * Closes the input streams created by this reader. This method is automatically
     * invoked when a new input is set, or when the reader is reset or disposed.
     */
    @Override
    protected void close() throws IOException {
        inputReplaced = false;
        super.close();
    }



    /**
     * Service provider interface (SPI) for {@code WorldFileImageReader}s. This provider wraps
     * an other provider (typically for the TIFF, JPEG or PNG formats), which shall be specified
     * at construction time. The legal {@linkplain #inputTypes input types} are {@link String},
     * {@link File}, {@link java.net.URI} and {@link java.net.URL} in order to allow the image
     * reader to infer the <cite>World File</cite> ({@code ".tfw"}) and <cite>Map Projection</cite>
     * ({@code ".prj"}) files from the image input file.
     *
     * {@section Plugins registration}
     * At the difference of other {@code ImageReader} plugins, the {@code WorldFileImageReader}
     * plugin is not automatically registered in the JVM. This is because there is many plugins
     * to register (one instance of this {@code Spi} class for each format to wrap), and because
     * attempts to get an {@code ImageReader} to wrap while {@link IIORegistry} is scanning the
     * classpath for services cause an infinite loop. To enable the <cite>World File</cite> plugins,
     * users must invoke {@link #registerDefaults(ServiceRegistry)} explicitly.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @see WorldFileImageWriter.Spi
     *
     * @since 3.08 (derived from 3.07)
     * @module
     */
    public static class Spi extends ImageReaderAdapter.Spi {
        /**
         * The suffix added to format names and MIME types.
         *
         * @since 3.20
         */
        static final String NAME_SUFFIX = "-wf";

        /**
         * The value to be returned by {@link #getModifiedInformation(Object)}.
         */
        static final Set<InformationType> INFO = Collections.singleton(InformationType.IMAGE_METADATA);

        /**
         * Creates a provider which will use the given format for reading pixel values.
         *
         * @param main The provider of the readers to use for reading the pixel values.
         */
        public Spi(final ImageReaderSpi main) {
            super(main);
            pluginClassName = "org.geotoolkit.image.io.plugin.WorldFileImageReader";
            addFormatNameSuffix(NAME_SUFFIX);
            addExtraMetadataFormat(GEOTK_FORMAT_NAME, false, true);
        }

        /**
         * Creates a provider which will use the given format for reading pixel values.
         * This is a convenience constructor for the above constructor with a provider
         * fetched from the given format name.
         *
         * @param  format The name of the provider to use for reading the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getReaderByFormatName(format, Spi.class));
        }

        /**
         * Creates a provider which will use the given format for reading pixel values.
         *
         * @param  format The name of the provider to use for reading the pixel values.
         * @param  writerSpiName The fully qualified class name of a provider for a writer that
         *         can write the files expected by this reader format, or {@code null} if none.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        Spi(final String format, final String writerSpiName) throws IllegalArgumentException {
            this(format);
            writerSpiNames = new String[] {writerSpiName};
        }

        /**
         * Returns a brief, human-readable description of this service provider.
         *
         * @param  locale The locale for which the return value should be localized.
         * @return A description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return Vocabulary.getResources(locale).getString(
                    Vocabulary.Keys.IMAGE_CODEC_WITH_WORLD_FILE_$2, 0, Formats.getDisplayName(main));
        }

        /**
         * Returns {@code true} if the given type of file should be excluded. The {@code readerID}
         * argument is either {@code "tfw"} or {@code "prj"}. If this method returns {@code true}
         * for the given ID, no attempt to read the corresponding file will be made.
         * <p>
         * This is useful for excluding attempts to read the TFW file when the information
         * is already provided in the main reader, as for example in the ASCII-Grid format.
         *
         * @param  readerID Identifier of the reader for which an input is needed.
         * @return {@code true} if no attempt to read the corresponding file should be made.
         */
        boolean exclude(final String readerID) {
            return false;
        }

        /**
         * Checks if the TFW or PRJ file exists for the given input. If we can not determine
         * the file existence, conservatively returns {@code false}. This happen typically if
         * the input is a URL. By returning {@code false} in the later case, we prevent the
         * world file reader to be selected, but it also avoid the risk of getting an
         * {@link IOException} when the reader will attempt to open a connection for the
         * PRJ or TFW URLs.
         */
        private boolean exists(final Object input, final String readerID) throws IOException {
            if (!exclude(readerID)) {
                final Object derived = SupportFiles.changeExtension(input, readerID);
                if (derived instanceof File) {
                    return ((File) derived).isFile();
                }
            }
            return false;
        }

        /**
         * Returns {@code true} if the supplied source object appears to be of the format supported
         * by this reader. The default implementation checks if at least one of the {@code ".tfw}
         * (actual extension may vary) or {@code ".prj"} file is presents, then delegates to the
         * super-class method.
         *
         * @param  source The input (typically a {@link File}) to be decoded.
         * @return {@code true} if it is likely that the file can be decoded.
         * @throws IOException If an error occurred while reading the file.
         */
        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            if (IOUtilities.canProcessAsPath(source)) {
                source = IOUtilities.tryToFile(source);
                if (exists(source, "tfw") || exists(source, "prj")) {
                    return super.canDecodeInput(source);
                }
            }
            return false;
        }

        /**
         * Returns the kind of information that this wrapper will add or modify compared to the
         * {@linkplain #main} reader.
         *
         * @param  source The input (typically a {@link File}) to be decoded.
         * @return The set of information to be read or modified by this adapter.
         * @throws IOException If an error occurred while reading the file.
         *
         * @since 3.20
         */
        @Override
        public Set<InformationType> getModifiedInformation(final Object source) throws IOException {
            return INFO;
        }

        /**
         * Creates a new <cite>World File</cite> reader. The {@code extension} argument
         * is forwarded to the {@linkplain #main main} provider with no change.
         *
         * @param  extension A plug-in specific extension object, or {@code null}.
         * @return A new reader.
         * @throws IOException If the reader can not be created.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new WorldFileImageReader(this, main.createReaderInstance(extension));
        }

        /**
         * Registers a default set of <cite>World File</cite> formats. This method shall be invoked
         * at least once by client application before to use Image I/O library if they wish to decode
         * <cite>World File</cite> images. This method can also be invoked more time if the PNG, TIFF
         * or other standard readers changed, and this change needs to be taken in account by the
         * <cite>World File</cite> readers. See the <cite>System initialization</cite> section in
         * the <a href="../package-summary.html#package_description">package description</a>
         * for more information.
         * <p>
         * The current implementation registers plugins for the TIFF, JPEG, PNG, GIF, BMP,
         * matrix and ASCII-Grid ({@code ".prj"} file only) formats, but this list can be
         * augmented in any future Geotk version.
         *
         * @param registry The registry where to register the formats, or {@code null} for
         *        the {@linkplain IIORegistry#getDefaultInstance() default registry}.
         *
         * @see org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()
         * @see org.geotoolkit.lang.Setup
         */
        @Configuration
        public static void registerDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            for (int index=0; ;index++) {
                final Spi provider;
                try {
                    switch (index) {
                        case 0: provider = new TIFF   (); break;
                        case 1: provider = new JPEG   (); break;
                        case 2: provider = new PNG    (); break;
                        case 3: provider = new GIF    (); break;
                        case 4: provider = new BMP    (); break;
                        case 5: provider = new TXT    (); break;
                        case 6: provider = new ASC    (); break;
                        case 7: provider = new Records(); break;
                        default: return;
                    }
                } catch (RuntimeException e) {
                    /*
                     * If we failed to register a plugin, this is not really a big deal.
                     * This format will not be available, but it will not prevent the
                     * rest of the application to work.
                     */
                    Logging.recoverableException(Logging.getLogger("org.geotoolkit.image.io"),
                            Spi.class, "registerDefaults", e);
                    continue;
                }
                registry.registerServiceProvider(provider, ImageReaderSpi.class);
                registry.setOrdering(ImageReaderSpi.class, provider, provider.main);
            }
        }

        /**
         * Unregisters the providers registered by {@link #registerDefaults(ServiceRegistry)}.
         *
         * @param registry The registry from which to unregister the formats, or {@code null}
         *        for the {@linkplain IIORegistry#getDefaultInstance() default registry}.
         *
         * @see org.geotoolkit.lang.Setup
         */
        @Configuration
        public static void unregisterDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            for (int index=0; ;index++) {
                final Class<? extends Spi> type;
                switch (index) {
                    case 0: type = TIFF   .class; break;
                    case 1: type = JPEG   .class; break;
                    case 2: type = PNG    .class; break;
                    case 3: type = GIF    .class; break;
                    case 4: type = BMP    .class; break;
                    case 5: type = TXT    .class; break;
                    case 6: type = ASC    .class; break;
                    case 7: type = Records.class; break;
                    default: return;
                }
                final Spi provider = registry.getServiceProviderByClass(type);
                if (provider != null) {
                    registry.deregisterServiceProvider(provider, ImageReaderSpi.class);
                }
            }
        }
    }

    /**
     * Providers for common formats. Each provider needs to be a different class because
     * {@link ServiceRegistry} allows the registration of only one instance of each class.
     */
    private static final class TIFF extends Spi {TIFF() {super("TIFF",       "org.geotoolkit.image.io.plugin.WorldFileImageWriter$TIFF");}}
    private static final class JPEG extends Spi {JPEG() {super("JPEG",       "org.geotoolkit.image.io.plugin.WorldFileImageWriter$JPEG");}}
    private static final class PNG  extends Spi { PNG() {super("PNG",        "org.geotoolkit.image.io.plugin.WorldFileImageWriter$PNG");}}
    private static final class GIF  extends Spi { GIF() {super("GIF",        "org.geotoolkit.image.io.plugin.WorldFileImageWriter$GIF");}}
    private static final class BMP  extends Spi { BMP() {super("BMP",        "org.geotoolkit.image.io.plugin.WorldFileImageWriter$BMP");}}
    private static final class TXT  extends Spi { TXT() {super("matrix",     "org.geotoolkit.image.io.plugin.WorldFileImageWriter$TXT");}}
    private static final class ASC  extends Spi { ASC() {super("ASCII-Grid", "org.geotoolkit.image.io.plugin.WorldFileImageWriter$ASC");}
        @Override boolean exclude(final String readerID) {
            return "tfw".equalsIgnoreCase(readerID);
        }
    }
    private static final class Records extends Spi {
        Records() {
            super("records");
        }
        @Override boolean exclude(final String readerID) {
            return "tfw".equalsIgnoreCase(readerID);
        }
    }
}
