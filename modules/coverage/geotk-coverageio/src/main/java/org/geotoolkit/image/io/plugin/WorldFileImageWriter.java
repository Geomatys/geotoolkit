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
package org.geotoolkit.image.io.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOException;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.image.io.ImageWriterAdapter;
import org.geotoolkit.image.io.metadata.ReferencingMetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.logging.Logging;


/**
 * Writer for the <cite>World File</cite> format. This writer wraps an other image writer
 * for an "ordinary" image format, like TIFF, PNG or JPEG. This {@code WorldFileImageWriter}
 * delegates the writing of pixel values to the wrapped writer, and additionally creates two
 * small text files in the same directory than the image file, with the same filename
 * but a different extension:
 * <p>
 * <ul>
 *   <li>A text file containing the coefficients of the affine transform mapping pixel
 *       coordinates to geodesic coordinates.</li>
 *   <li>A text file containing the <cite>Coordinate Reference System</cite> (CRS)
 *       definition in <cite>Well Known Text</cite> (WKT) syntax.</li>
 * </ul>
 * <p>
 * See {@link WorldFileImageReader} for more information about the name, content and encoding
 * of those files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see <a href="http://en.wikipedia.org/wiki/World_file">World File Format Description</a>
 * @see WorldFileImageReader
 *
 * @since 3.07
 * @module
 */
public class WorldFileImageWriter extends ImageWriterAdapter {
    /**
     * Constructs a new image writer. The provider argument is mandatory for this constructor.
     * If the provider is unknown, use the next constructor below instead.
     *
     * @param  provider The {@link ImageWriterSpi} that is constructing this object.
     * @throws IOException If an error occured while creating the {@linkplain #main main} writer.
     */
    public WorldFileImageWriter(final Spi provider) throws IOException {
        super(provider);
    }

    /**
     * Constructs a new image writer wrapping the given writer.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     * @param main The writer to use for writing the pixel values.
     */
    public WorldFileImageWriter(final Spi provider, final ImageWriter main) {
        super(provider, main);
    }

    /**
     * Creates the output to be given to the writer identified by the given argument. If the
     * {@code writerID} argument is {@code "main"} (ignoring case), then this method delegates
     * to the {@linkplain ImageWriterAdapter#createOutput(String) super-class method}. Otherwise
     * this method returns an output which is typically a {@link File} or {@link java.net.URL}
     * having the same name than the {@linkplain #output output} of this writer, but a different
     * extension. The new extension is determined from the {@code writerID} argument, which can
     * be:
     * <p>
     * <ul>
     *   <li>{@code "tfw"} for the <cite>World File</cite>.</li>
     *   <li>{@code "prj"} for <cite>Map Projection</cite> file.</li>
     * </ul>
     * <p>
     * Subclasses can override this method for specifying a different main ({@code "main"}),
     * <cite>World File</cite> ({@code "tfw"}) or <cite>Map Projection</cite> ({@code "prj"})
     * output. They can also invoke this method with other identifiers than the three above-cited
     * ones, in which case this method uses the given identifier as the extension of the returned
     * output. However the default {@code WorldFileImageWriter} implementation uses only
     * {@code "main"}, {@code "tfw"} and {@code "prj"}.
     *
     * @param  writerID {@code "main"} for the {@linkplain #main main} output,
     *         {@code "tfw"} for the <cite>World File</cite> output, or
     *         {@code "prj"} for the <cite>Map Projection</cite> output. Other
     *         identifiers are allowed but subclass-specific.
     * @return The given kind of output typically as a {@link File} or {@link java.net.URL}
     *         object, or {@code null} if there is no output for the given identifier.
     * @throws IOException If an error occured while creating the output.
     *
     * @see WorldFileImageReader#createInput(String)
     */
    @Override
    protected Object createOutput(String writerID) throws IOException {
        if ("main".equalsIgnoreCase(writerID)) {
            return super.createOutput(writerID);
        }
        final Object output = this.output;
        if ("tfw".equalsIgnoreCase(writerID)) {
            writerID = SupportFiles.toSuffixTFW(output);
        }
        return IOUtilities.changeExtension(output, writerID);
    }

    /**
     * Invoked by the {@code write} methods when image metadata needs to be written.
     * The default implementation writes the <cite>World File</cite> if an affine
     * transform can be build from the {@linkplain RectifiedGrid rectified grid domain}.
     *
     * @todo Needs to write the PRJ file too.
     */
    @Override
    protected void writeImageMetadata(final IIOMetadata metadata, final int imageIndex,
            final ImageWriteParam param) throws IOException
    {
        if (imageIndex != 0) {
            throw new IIOException(Errors.getResources(locale).getString(
                    Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, imageIndex));
        }
        if (metadata instanceof SpatialMetadata) {
            final SpatialMetadata md = (SpatialMetadata) metadata;
            final ReferencingMetadataHelper mh = new ReferencingMetadataHelper(md);
            final RectifiedGrid rf = md.getInstanceForType(RectifiedGrid.class);
            if (rf != null) {
                final AffineTransform tr = mh.getAffineTransform(rf, param);
                final Object path = createOutput("tfw");
                if (path != null) {
                    final OutputStream out = IOUtilities.openWrite(path);
                    SupportFiles.writeTFW(out, tr);
                    out.close();
                }
            }
            final CoordinateReferenceSystem crs = mh.getOptionalCRS();
            if (crs != null) {
                final Object path = createOutput("prj");
                if (path != null) {
                    final OutputStream out = IOUtilities.openWrite(path);
                    PrjFiles.write(crs, out);
                    out.close();
                }
            }
        }
    }



    /**
     * Service provider interface (SPI) for {@link WorldFileImageWriter}s. This provider wraps
     * an other provider (typically for the TIFF, JPEG or PNG formats), which shall be specified
     * at construction time. The legal {@linkplain #outputTypes output types} are {@link String},
     * {@link File}, {@link java.net.URI} and {@link java.net.URL} in order to allow the image
     * writer to infer the <cite>World File</cite> ({@code ".tfw"}) and <cite>Map Projection</cite>
     * ({@code ".prj"}) files from the image output file.
     *
     * {@section Plugins registration}
     * At the difference of other {@code ImageWriter} plugins, the {@code WorldFileImageWriter}
     * plugin is not automatically registered in the JVM. This is because there is many plugins
     * to register (one instance of this {@code Spi} class for each format to wrap), and because
     * attempts to get an {@code ImageWriter} to wrap while {@link IIORegistry} is scanning the
     * classpath for services cause an infinite loop. To enable the <cite>World File</cite> plugins,
     * users must invoke {@link #registerDefaults(ServiceRegistry)} explicitly.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @see WorldFileImageReader.Spi
     *
     * @since 3.07
     * @module
     */
    public static class Spi extends ImageWriterAdapter.Spi {
        /**
         * Creates a provider which will use the given format for writing pixel values.
         *
         * @param main The provider of the writers to use for writing the pixel values.
         */
        public Spi(final ImageWriterSpi main) {
            super(main);
            pluginClassName = "org.geotoolkit.image.io.plugin.WorldFileImageWriter";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
        }

        /**
         * Creates a provider which will use the given format for writing pixel values.
         * This is a convenience constructor for the above constructor with a provider
         * fetched from the given format name.
         *
         * @param  format The name of the provider to use for writing the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getWriterByFormatName(format, Spi.class));
        }

        /**
         * Returns a brief, human-writable description of this service provider.
         *
         * @param  locale The locale for which the return value should be localized.
         * @return A description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return Vocabulary.getResources(locale).getString(
                    Vocabulary.Keys.IMAGE_CODEC_WITH_WORLD_FILE_$2, 1, Formats.getFormatName(main));
        }

        /**
         * Creates a new <cite>World File</cite> writer. The {@code extension} argument
         * is forwarded to the {@linkplain #main main} provider with no change.
         *
         * @param  extension A plug-in specific extension object, or {@code null}.
         * @return A new writer.
         * @throws IOException If the writer can not be created.
         */
        @Override
        public ImageWriter createWriterInstance(final Object extension) throws IOException {
            return new WorldFileImageWriter(this, main.createWriterInstance(extension));
        }

        /**
         * Registers a default set of <cite>World File</cite> formats. This method shall be
         * invoked exactly once by client application before to use Image I/O library if they
         * wish to decode <cite>World File</cite> images. See the
         * <a href="../package-summary.html#package_description">package description</a>
         * for more information.
         * <p>
         * The current implementation registers plugins for the TIFF, JPEG, PNG, GIF, BMP
         * and matrix formats, but this list can be augmented in any future Geotk version.
         *
         * @param registry The registry where to register the formats, or {@code null} for
         *        the {@linkplain IIORegistry#getDefaultInstance() default registry}.
         *
         * @see org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()
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
                        case 0: provider = new TIFF(); break;
                        case 1: provider = new JPEG(); break;
                        case 2: provider = new PNG (); break;
                        case 3: provider = new GIF (); break;
                        case 4: provider = new BMP (); break;
                        case 5: provider = new TXT (); break;
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
                registry.registerServiceProvider(provider, ImageWriterSpi.class);
            }
        }

        /**
         * Unregisters the providers registered by {@link #registerDefaults(ServiceRegistry)}.
         *
         * @param registry The registry from which to unregister the formats, or {@code null}
         *        for the {@linkplain IIORegistry#getDefaultInstance() default registry}.
         */
        @Configuration
        public static void unregisterDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            for (int index=0; ;index++) {
                final Class<? extends Spi> type;
                switch (index) {
                    case 0: type = TIFF.class; break;
                    case 1: type = JPEG.class; break;
                    case 2: type = PNG .class; break;
                    case 3: type = GIF .class; break;
                    case 4: type = BMP .class; break;
                    case 5: type = TXT .class; break;
                    default: return;
                }
                final Spi provider = registry.getServiceProviderByClass(type);
                if (provider != null) {
                    registry.deregisterServiceProvider(provider, ImageWriterSpi.class);
                }
            }
        }
    }

    /**
     * Providers for common formats. Each provider needs to be a different class because
     * {@link ServiceRegistry} allows the registration of only one instance of each class.
     */
    private static final class TIFF extends Spi {TIFF() {super("TIFF"  );}}
    private static final class JPEG extends Spi {JPEG() {super("JPEG"  );}}
    private static final class PNG  extends Spi { PNG() {super("PNG"   );}}
    private static final class GIF  extends Spi { GIF() {super("GIF"   );}}
    private static final class BMP  extends Spi { BMP() {super("BMP"   );}}
    private static final class TXT  extends Spi { TXT() {super("matrix");}}
}
