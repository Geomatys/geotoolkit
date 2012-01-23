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

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.metadata.IIOMetadata;

import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.metadata.geotiff.GeoTiffMetaDataReader;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.logging.Logging;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 * Reader for the <cite>GeoTiff</cite> format. This reader wraps an "ordinary" image format TIFF.
 * This {@code GeoTiffImageReader} delegates the reading of pixel values to the wrapped reader,
 * and additionally looks for Geographic metadatas in the file header :
 *
 * <ul>
 *   <li>
 *      <p>The geotiif header contain additional metadatas to project the current image.
 *      It contain the image projection and transformations.
 *      Check the geotiff description for the complete list of all metadatas available.
 *      </p>
 *   </li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 *
 * @see <a href="http://trac.osgeo.org/geotiff/">GeoTiff Description</a> *
 * @module pending
 */
public class GeoTiffImageReader extends ImageReaderAdapter {

    public GeoTiffImageReader(final Spi provider) throws IOException {
        super(provider);
    }

    public GeoTiffImageReader(final Spi provider, final ImageReader main) {
        super(provider, main);
    }

    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if(imageIndex < 0){
            //stream metadata
            return super.createMetadata(imageIndex);
        }

        final IIOMetadata metadata = main.getImageMetadata(imageIndex);
        try {
            final GeoTiffMetaDataReader metareader = new GeoTiffMetaDataReader(metadata);
            return metareader.readSpatialMetaData();
        } catch (NoSuchAuthorityCodeException ex) {
            throw new IOException(ex);
        } catch (FactoryException ex) {
            throw new IOException(ex);
        }
    }

    public static class Spi extends ImageReaderAdapter.Spi {
        public Spi(final ImageReaderSpi main) {
            super(main);
            names           = new String[] {"geotiff"};
            MIMETypes       = new String[] {"image/x-geotiff"};
            pluginClassName = "org.geotoolkit.image.io.plugin.GeoTiffImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
            writerSpiNames = new String[] {GeoTiffImageWriter.TIFF.class.getName()};
        }

        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getReaderByFormatName(format, Spi.class));
        }

        @Override
        public String getDescription(final Locale locale) {
            return "GeoTiff format.";
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            if(super.canDecodeInput(source)){
                //todo must find a way to check that this tiff has the geo tags.
                try{
                    source = IOUtilities.tryToFile(source);
                    final String str = IOUtilities.extension(source);
                    if(str != null){
                        if(!(str.equalsIgnoreCase("tif") ||
                             str.equalsIgnoreCase("tiff"))){
                            return false;
                        }
                        
                        try{
                            final GeoTiffImageReader reader = new GeoTiffImageReader(this);
                            reader.setInput(source);
                            reader.getImageMetadata(0);
                        }catch(IOException ex){
                            //failed to read metadatas
                            return false;
                        }
                        //ok
                    }else{
                        return false;
                    }
                }catch(IOException ex){
                    //maybe it's a stream
                }
                return true;
            }else{
                return false;
            }
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new GeoTiffImageReader(this, main.createReaderInstance(extension));
        }

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

        @Configuration
        public static void unregisterDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            for (int index=0; ;index++) {
                final Class<? extends Spi> type;
                switch (index) {
                    case 0: type = TIFF.class; break;
                    //todo must add BIL format in the futur
                    default: return;
                }
                final Spi provider = registry.getServiceProviderByClass(type);
                if (provider != null) {
                    registry.deregisterServiceProvider(provider, ImageReaderSpi.class);
                }
            }
        }
    }

    private static final class TIFF extends Spi {TIFF() {super("TIFF"  );}}
}
