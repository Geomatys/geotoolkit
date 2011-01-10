/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.xml.transform.TransformerException;

import org.geotoolkit.image.io.ImageWriterAdapter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.metadata.dimap.DimapMetadataFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.DomUtilities;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.logging.Logging;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Writer for the <cite>Coverage Dimap</cite> format. This writer wraps an other image writer
 * for an image format GEOTIFF. This {@code DimapImageWriter}
 * delegates the writing of pixel values to the wrapped writer, and writes an additional metadata.dim
 * file.
 *
 * See {@link DimapImageReader} for more information about the name, content and encoding
 * of metadatas.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimapImageWriter extends ImageWriterAdapter{

    /**
     * Constructs a new image writer. The provider argument is mandatory for this constructor.
     * If the provider is unknown, use the next constructor below instead.
     *
     * @param  provider The {@link ImageWriterSpi} that is constructing this object.
     * @throws IOException If an error occurred while creating the {@linkplain #main main} writer.
     */
    public DimapImageWriter(final Spi provider) throws IOException {
        super(provider);
    }

    /**
     * Constructs a new image writer wrapping the given writer.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     * @param main The writer to use for writing the pixel values.
     */
    public DimapImageWriter(final Spi provider, final ImageWriter main) {
        super(provider, main);
    }

    /**
     * Add support for "dim" writerId, other cases are delegated to the parent class.
     */
    @Override
    protected Object createOutput(final String writerID) throws IOException {
        if ("dim".equalsIgnoreCase(writerID)) {
            return IOUtilities.changeExtension(output, "dim");
        }
        return super.createOutput(writerID);
    }

    /**
     * Invoked by the {@code write} methods when image metadata needs to be written.
     * The default implementation writes the <cite>Dimap</cite> if a dimap metadata model
     * is declared in the spatial metadata.
     */
    @Override
    protected void writeImageMetadata(final IIOMetadata metadata, final int imageIndex,
            final ImageWriteParam param) throws IOException {

        if (imageIndex != 0) {
            throw new IIOException(Errors.getResources(locale).getString(
                    Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, imageIndex));
        }
        if (metadata instanceof SpatialMetadata) {
            final SpatialMetadata md = (SpatialMetadata) metadata;
            final int index = Arrays.binarySearch(md.getMetadataFormatNames(), DimapMetadataFormat.NATIVE_FORMAT);

            if(index >= 0){
                //found some dimap metadatas, write them
                final Node node = md.getAsTree(DimapMetadataFormat.NATIVE_FORMAT);
                final Object output = createOutput("dim");
                try {
                    DomUtilities.write((Document) node, output);
                } catch (TransformerException ex) {
                    Logger.getLogger(DimapImageWriter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DimapImageWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    /**
     * Service provider interface (SPI) for {@code DimapImageWriter}s. This provider wraps
     * an other provider (GEOTIFF), which shall be specified at construction time.
     *
     * {@section Plugins registration}
     * At the difference of other {@code ImageWriter} plugins, the {@code DimapImageWriter}
     * plugin is not automatically registered in the JVM. This is because there is many plugins
     * to register (one instance of this {@code Spi} class for each format to wrap), and because
     * attempts to get an {@code ImageWriter} to wrap while {@link IIORegistry} is scanning the
     * classpath for services cause an infinite loop. To enable the <cite>Dimap</cite> plugins,
     * users must invoke {@link #registerDefaults(ServiceRegistry)} explicitly.
     *
     * @author Johann Sorel (Geomatys)
     * @version 3.16
     * @see GeoTiffImageReader.Spi
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
            names           = new String[] {"dimap"};
            MIMETypes       = new String[] {"image/x-dimap"};
            pluginClassName = "org.geotoolkit.image.io.plugin.DimapImageWriter";
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
            return "Dimap format.";
        }

        /**
         * Creates a new <cite>Coverage Dimap</cite> writer. The {@code extension} argument
         * is forwarded to the {@linkplain #main main} provider with no change.
         *
         * @param  extension A plug-in specific extension object, or {@code null}.
         * @return A new writer.
         * @throws IOException If the writer can not be created.
         */
        @Override
        public ImageWriter createWriterInstance(final Object extension) throws IOException {
            return new DimapImageWriter(this, main.createWriterInstance(extension));
        }

        /**
         * Registers a default set of <cite>Dimap</cite> formats. This method shall be invoked
         * at least once by client application before to use Image I/O library if they wish to encode
         * <cite>Dimap</cite> images. This method can also be invoked more time if the TIFF
         * writer changed, and this change needs to be taken in account by the
         * <cite>Dimap</cite> writers. See the <cite>System initialization</cite> section in
         * the <a href="../package-summary.html#package_description">package description</a>
         * for more information.
         * <p>
         * The current implementation registers plugins for the GEOTIFF.
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

            //dimap requiere geotiff
            GeoTiffImageWriter.Spi.registerDefaults(registry);

            for (int index=0; ;index++) {
                final Spi provider;
                try {
                    switch (index) {
                        case 0: provider = new GEOTIFF(); break;
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
                registry.setOrdering(ImageWriterSpi.class, provider.main, provider);
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
                    case 0: type = GEOTIFF.class; break;
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
    static final class GEOTIFF extends Spi {GEOTIFF() {super("geotiff"      );}}
}
