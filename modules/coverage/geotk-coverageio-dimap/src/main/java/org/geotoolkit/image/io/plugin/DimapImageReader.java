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

import java.awt.Color;
import java.awt.Point;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.media.jai.JAI;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.jai.FloodFill;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.metadata.dimap.DimapAccessor;
import org.geotoolkit.metadata.dimap.DimapMetadataFormat;
import org.geotoolkit.util.DomUtilities;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.logging.Logging;

/**
 * Reader for the <cite>Dimap</cite> format. This reader wraps an other image reader
 * for an "ordinary" image format, like TIFF, PNG or JPEG. This {@code DimapImageReader}
 * delegates the reading of pixel values to the wrapped reader, and additionally looks for
 * a xml file in the same directory than the image file, with the same filename or constant name
 * metadata and extension .dim :
 *
 * <ul>
 *   <li><p>The dim file contain a complete metadata description of the image.
 *      This file may contain source, aquisition, referencing and color informations.
 *      So other informations may be found on different dimap profiles. Check the dimap
 *      description for the complete list of all metadatas available.
 *      </p>
 *   </li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 *
 * @see <a href="http://www.spotimage.com/web/154-le-format-dimap.php">DIMAP Description</a> *
 * @module pending
 */
public class DimapImageReader extends ImageReaderAdapter {

    public DimapImageReader(final Spi provider) throws IOException {
        super(provider);
    }

    public DimapImageReader(final Spi provider, final ImageReader main) {
        super(provider, main);
    }

    @Override
    protected Object createInput(final String readerID) throws IOException {
        if("dim".equalsIgnoreCase(readerID)){
            return DimapImageReader.Spi.searchMetadataFile(input);
        }
        return super.createInput(readerID);
    }

    /**
     * Apply the band selection indexes provided in the dimap file.
     */
    private RenderedImage changeColorModel(RenderedImage image, final boolean bufferedImage) throws IOException{
        if(image == null) return image;

        final boolean oldState = ignoreMetadata;
        ignoreMetadata = false;
        final SpatialMetadata metadata;
        try {
            metadata = getImageMetadata(0);
        } finally {
            ignoreMetadata = oldState;
        }
        if(metadata == null){
            //TODO geotiff did not returned the metadata
            //find solution to pass the ignoreMetadata flag down in the reader stack.
            return image;
        }

        //apply the band <-> color mapping -------------------------------------
        final int[] colorMapping = DimapAccessor.readColorBandMapping((Element)metadata.getAsTree(DimapMetadataFormat.NATIVE_FORMAT));
        if(colorMapping == null){
            //we have no default styling
            return image;
        }

        // select the visible bands
        final ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(colorMapping);
        image = JAI.create("bandSelect",pb);

        //ensure we have a bufferedImage for floodfill operation
        final BufferedImage buffer;
        if(image instanceof BufferedImage){
            buffer = (BufferedImage) image;
        }else{
            buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            buffer.createGraphics().drawRenderedImage(image, new AffineTransform());
        }

        //remove black borders+
        FloodFill.fill(buffer, new Color[]{Color.BLACK}, new Color(0f,0f,0f,0f),
                new Point(0,0),
                new Point(buffer.getWidth()-1,0),
                new Point(buffer.getWidth()-1,buffer.getHeight()-1),
                new Point(0,buffer.getHeight()-1)
                );

        return buffer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage read(final int imageIndex) throws IOException {
        return (BufferedImage) changeColorModel(super.read(imageIndex),true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        return (BufferedImage) changeColorModel(super.read(imageIndex,param),true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RenderedImage readAsRenderedImage(final int imageIndex, final ImageReadParam param) throws IOException {
        return changeColorModel(super.readAsRenderedImage(imageIndex, param),false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage readTile(final int imageIndex, final int tileX, final int tileY) throws IOException {
        return (BufferedImage) changeColorModel(super.readTile(imageIndex, tileX, tileY),true);
    }

    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {

        //grab spatial metadata from underlying geotiff
        final SpatialMetadata metadata = super.createMetadata(imageIndex);

        if(metadata == null){
            //it can happen if reading metadata has not been asked.
            return metadata;
        }

        //parse the dimap metadata file
        final Object metaFile = createInput("dim");
        final Document doc;
        try {
            doc = DomUtilities.read(metaFile);
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        } catch (SAXException ex) {
            throw new IOException(ex);
        }
        final Element dimapNode = doc.getDocumentElement();

        final SpatialMetadata dimapMeta = new SpatialMetadata(DimapMetadataFormat.INSTANCE, this, metadata);
        dimapMeta.mergeTree(DimapMetadataFormat.NATIVE_FORMAT, dimapNode);
        return dimapMeta;
    }

    public static class Spi extends ImageReaderAdapter.Spi {
        public Spi(final ImageReaderSpi main) {
            super(main);
            names           = new String[] {"dimap"};
            MIMETypes       = new String[] {"image/x-dimap"};
            pluginClassName = "org.geotoolkit.image.io.plugin.DimapImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
            extraImageMetadataFormatNames = XArrays.concatenate(extraImageMetadataFormatNames, new String[] {
                nativeImageMetadataFormatName
            });
            nativeImageMetadataFormatName = DimapMetadataFormat.NATIVE_FORMAT;
            writerSpiNames = new String[] {DimapImageWriter.GEOTIFF.class.getName()};
        }

        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getReaderByFormatName(format, Spi.class));
        }

        @Override
        public String getDescription(final Locale locale) {
            return "Dimap format.";
        }

        private static File searchMetadataFile(final Object input) throws IOException{
            if(input instanceof File){
                final File file = (File) input;
                final File parent = file.getParentFile();

                //search for metadata.dim
                File candidate = new File(parent, "metadata.dim");
                if(candidate.isFile()) return candidate;

                //search for filename.dim
                Object obj = IOUtilities.changeExtension(file, "dim");
                if(obj instanceof File){
                    candidate = (File)obj;
                    if(candidate.isFile()) return candidate;
                }else if(obj instanceof String){
                    candidate = new File((String)obj);
                    if(candidate.isFile()) return candidate;
                }

                return null;
            }else{
                return null;
            }
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            boolean can = super.canDecodeInput(source);

            if (can && IOUtilities.canProcessAsPath(source)) {
                source = IOUtilities.tryToFile(source);
                final File f = searchMetadataFile(source);
                return (f != null);
            }
            return false;
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new DimapImageReader(this, main.createReaderInstance(extension));
        }

        @Configuration
        public static void registerDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }

            //dimap requiere geotiff
            GeoTiffImageReader.Spi.registerDefaults(registry);

            for (int index=0; ;index++) {
                final Spi provider;
                try {
                    switch (index) {
                        case 0: provider = new GEOTIFF(); break;
                        //todo must add BIL format in the futur
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
                    case 0: type = GEOTIFF.class; break;
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

    private static final class GEOTIFF extends Spi {GEOTIFF() {super("geotiff"  );}}
}
