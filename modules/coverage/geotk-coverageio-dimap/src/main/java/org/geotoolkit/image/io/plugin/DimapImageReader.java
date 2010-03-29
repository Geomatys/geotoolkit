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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.metadata.dimap.TDatasetFrame;
import org.geotoolkit.metadata.dimap.TDimapDocument;
import org.geotoolkit.metadata.dimap.TVertex;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;
import org.geotoolkit.util.Version;
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
        if ("main".equalsIgnoreCase(readerID)) {
            return super.createInput(readerID);
        }else if("dim".equalsIgnoreCase(readerID)){
            File metadata = DimapImageReader.Spi.searchMetadataFile(input);
            return metadata;
        }
        throw new IOException("Unexpected reader id : " + readerID +" allowed ids are 'main' and 'dim'.");
    }

    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        SpatialMetadata metadata = super.createMetadata(imageIndex);
        if (imageIndex >= 0) {
            AffineTransform gridToCRS = null;
            CoordinateReferenceSystem crs = null;
            try {
                final JAXBContext context = JAXBContext.newInstance(org.geotoolkit.metadata.dimap.ObjectFactory.class);
                final File metaFile = (File) createInput("dim");
                final Unmarshaller marshal = context.createUnmarshaller();
                final Object obj = marshal.unmarshal(metaFile);
                final JAXBElement ele = (JAXBElement) obj;
                final TDimapDocument doc = (TDimapDocument) ele.getValue();

                crs = CRS.decode(doc.getCoordinateReferenceSystem().getHorizontalCS().getHORIZONTALCSCODE());
                gridToCRS = new AffineTransform();

//                final TDatasetFrame frame = doc.getDatasetFrame();
//                final List<Point2D> sources = new ArrayList<Point2D>();
//                final List<Point2D> dests = new ArrayList<Point2D>();
//
//                for(TVertex vertex : frame.getVertex()){
//                    sources.add(new Point2D.Double(
//                            vertex.getFRAMEX().getValue(),
//                            vertex.getFRAMEY().getValue()));
//                    dests.add(new Point2D.Double(
//                            vertex.getFRAMELAT().getValue(),
//                            vertex.getFRAMELON().getValue()));
//                }
//
//                final WarpTransform2D trs = new WarpTransform2D(
//                        sources.toArray(new Point2D[sources.size()]),
//                        dests.toArray(new Point2D[dests.size()]), 1);
//
//                final Warp warp = trs.getWarp();
//                if(warp instanceof WarpAffine){
//                    WarpAffine wa = (WarpAffine) warp;
//                    gridToCRS = wa.getTransform();
//                }else{
//                    throw new UnsupportedOperationException("not yet.");
//                }

            } catch (JAXBException ex) {
                throw new IOException(ex);
            } catch (FactoryException ex) {
                throw new IOException(ex);
            }

            /*
             * If we have found metadata information in dimap file, complete metadata.
             */
            if (gridToCRS != null || crs != null) {
                if (metadata == null) {
                    metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE, this, null);
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

    public static class Spi extends ImageReaderAdapter.Spi {
        public Spi(final ImageReaderSpi main) {
            super(main);
            pluginClassName = "org.geotoolkit.image.io.plugin.DimapImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
        }

        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getReaderByFormatName(format, Spi.class));
        }

        @Override
        public String getDescription(final Locale locale) {
            return "Dimap format.";
        }

        private static File searchMetadataFile(Object input) throws IOException{
            if(input instanceof File){
                final File file = (File) input;
                final File parent = file.getParentFile();
                final File candidate = new File(parent, "metadata.dim");
                if (candidate.isFile()) {
                    return candidate;
                }else{
                    throw new IOException("Could not find metadata file");
                }
            }else{
                throw new IOException("Input must be of type file, found : " + input.getClass());
            }
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            if (IOUtilities.canProcessAsPath(source)) {
                source = IOUtilities.tryToFile(source);
                searchMetadataFile(source);
            }
            return super.canDecodeInput(source);
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
                    case 1: type = JPEG.class; break;
                    case 2: type = PNG .class; break;
                    case 3: type = GIF .class; break;
                    case 4: type = BMP .class; break;
                    case 5: type = TXT .class; break;
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
    private static final class JPEG extends Spi {JPEG() {super("JPEG"  );}}
    private static final class PNG  extends Spi { PNG() {super("PNG"   );}}
    private static final class GIF  extends Spi { GIF() {super("GIF"   );}}
    private static final class BMP  extends Spi { BMP() {super("BMP"   );}}
    private static final class TXT  extends Spi { TXT() {super("matrix");}}
}
