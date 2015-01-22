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

import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.metadata.dimap.DimapAccessor;
import org.geotoolkit.metadata.dimap.DimapMetadataFormat;
import org.geotoolkit.util.DomUtilities;
import org.geotoolkit.util.Utilities;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.geotoolkit.image.io.MultidimensionalImageStore.X_DIMENSION;
import static org.geotoolkit.image.io.MultidimensionalImageStore.Y_DIMENSION;

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
     * {@inheritDoc }
     */
    @Override
    public BufferedImage read(final int imageIndex) throws IOException {
        return super.read(imageIndex);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        return super.read(imageIndex,param);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RenderedImage readAsRenderedImage(final int imageIndex, final ImageReadParam param) throws IOException {
        return super.readAsRenderedImage(imageIndex, param);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage readTile(final int imageIndex, final int tileX, final int tileY) throws IOException {
        return super.readTile(imageIndex, tileX, tileY);
    }

    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if(imageIndex < 0){
            //stream metadata
            return super.createMetadata(imageIndex);
        }

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
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException(ex);
        }
        final Element dimapNode = doc.getDocumentElement();

        final SpatialMetadata dimapMeta = new SpatialMetadata(DimapMetadataFormat.INSTANCE, this, metadata);
        dimapMeta.mergeTree(DimapMetadataFormat.NATIVE_FORMAT, dimapNode);

        boolean geotkFormat = false;
        final String[] formatNames = dimapMeta.getMetadataFormatNames();
        for (int i = 0; i < formatNames.length; i++) {
            String formatName = formatNames[i];
            if (formatName.equals(SpatialMetadataFormat.GEOTK_FORMAT_NAME)) {
                geotkFormat = true;
            }
        }

        //ensure GEOTK format before override parts of metadata
        if (!geotkFormat) {
            return dimapMeta;
        }

        try {
            // add temporal to CRS
            final CoordinateReferenceSystem crs3D = fixCRS(dimapMeta);

            //override GridGeometry
            fixGridGeometry(imageIndex, dimapMeta, dimapNode, crs3D);

            //TODO fix sample dimension override
            //addSampleDimensions(dimapMeta, dimapNode);

            dimapMeta.clearInstancesCache();
        } catch (FactoryException | TransformException e) {
            throw new IOException(e.getMessage(), e);
        }

        return dimapMeta;
    }

    /**
     * Add temporal CRS to image geographic CRS extracted from sub-reader metadata.
     * @param dimapMeta SpacialMetadata generated from sub-reader.
     * @return CoordinateReferenceSystem 3D
     * @throws FactoryException
     */
    private CoordinateReferenceSystem fixCRS(final SpatialMetadata dimapMeta) throws FactoryException {
        final ReferencingBuilder refBuilder = new ReferencingBuilder(dimapMeta);
        final CoordinateReferenceSystem originalCRS = refBuilder.getCoordinateReferenceSystem(CoordinateReferenceSystem.class);

        final TemporalCRS temporalCRS = CommonCRS.Temporal.JAVA.crs();

        final String crs3DName = originalCRS.getName().getCode() + "+time" ;
        final Map<String,?> name = Collections.singletonMap(IdentifiedObject.NAME_KEY, crs3DName);
        final CoordinateReferenceSystem crs3D = new DefaultCompoundCRS(name, originalCRS, temporalCRS);
        refBuilder.setCoordinateReferenceSystem(crs3D);

        return crs3D;
    }

    /**
     *
     * @param imageIndex
     * @param dimapMeta
     * @param dimapNode
     * @param crs3D
     * @throws FactoryException
     * @throws TransformException
     * @throws IOException
     */
    private void fixGridGeometry(int imageIndex, final SpatialMetadata dimapMeta, final Element dimapNode, final CoordinateReferenceSystem crs3D)
            throws FactoryException, TransformException, IOException {

        MathTransform gridToCRS2D;
        MetadataHelper helper = new MetadataHelper(null);
        final RectifiedGrid grid = dimapMeta.getInstanceForType(RectifiedGrid.class);
        if (grid != null) {
            gridToCRS2D = helper.getGridToCRS(grid);
            dimapMeta.clearInstancesCache();
        } else {
            final AffineTransform at2D = DimapAccessor.readGridToCRS2D(dimapNode);
            gridToCRS2D = new AffineTransform2D(at2D);
        }

        //temporal
        final Date prodDate = DimapAccessor.getProductionDate(dimapNode);
        final LinearTransform gridToCRSTime = MathTransforms.linear(1, prodDate.getTime());

        // concatenate transforms
        final MathTransform gridToCRS2DPassThrough = PassThroughTransform.create(0, gridToCRS2D, 1);
        final MathTransform gridToCRSTimePassThrough = PassThroughTransform.create(2, gridToCRSTime, 0);
        final MathTransform gridToCRS3D = MathTransforms.concatenate(gridToCRS2DPassThrough, gridToCRSTimePassThrough);

        // GridEnvelope
        int[] low = new int[3];
        int[] high = new int[3];
        Arrays.fill(high, 1);
        high[X_DIMENSION] = super.getWidth(imageIndex);
        high[Y_DIMENSION] = super.getHeight(imageIndex);

        //only one slice for time dimension and high excluded -> {0,1}
        final GeneralGridEnvelope extend3D = new GeneralGridEnvelope(low, high, false);

        //finally rebuild gridGeometry
        final GridGeometry gridGeometry = new GeneralGridGeometry(extend3D, gridToCRS3D, crs3D);

        //override GridGeometry
        final GridDomainAccessor gridAccessor = new GridDomainAccessor(dimapMeta);
        gridAccessor.setGridGeometry(gridGeometry, null, null);
    }

    /**
     * Add sample dimensions extracted from Dimap native metadata in SpatialMetadata.
     * @param dimapMeta geotk spacial metadata
     * @param dimapNode native Dimap metadata
     */
    private void addSampleDimensions(SpatialMetadata dimapMeta, Element dimapNode) {
        final DimensionAccessor dimAccessor = new DimensionAccessor(dimapMeta);
        dimAccessor.selectParent();
        dimAccessor.removeChildren();
        final GridSampleDimension[] gridSampleDimensions = DimapAccessor.readSampleDimensions(dimapNode);

        for (final GridSampleDimension sampleDimension : gridSampleDimensions) {
            dimAccessor.selectChild(dimAccessor.appendChild()); //new child
            dimAccessor.setDimension(sampleDimension, Locale.ENGLISH);
            dimAccessor.selectParent();
        }
    }

    public static class Spi extends ImageReaderAdapter.Spi {
        public Spi(final ImageReaderSpi main) {
            super(main);
            names           = new String[] {"dimap"};
            MIMETypes       = new String[] {"image/x-dimap"};
            pluginClassName = "org.geotoolkit.image.io.plugin.DimapImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            extraImageMetadataFormatNames = ArraysExt.concatenate(extraImageMetadataFormatNames, new String[] {
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
                File candidate = null;
                for(final File f : parent.listFiles()){
                    if("metadata.dim".equalsIgnoreCase(f.getName())){
                        candidate = f;
                    }
                }

                if(candidate != null && candidate.isFile()) return candidate;

                //search for filename.dim
                Object obj = IOUtilities.changeExtension(file, "dim");
                if(obj instanceof File){
                    candidate = (File)obj;
                    if(candidate.isFile()) return candidate;
                }else if(obj instanceof String){
                    candidate = new File((String)obj);
                    if(candidate.isFile()) return candidate;
                }

                //search for filename.DIM
                obj = IOUtilities.changeExtension(file, "DIM");
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
