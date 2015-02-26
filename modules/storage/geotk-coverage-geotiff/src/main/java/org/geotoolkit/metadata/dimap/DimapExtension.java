package org.geotoolkit.metadata.dimap;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.metadata.GeoTiffExtension;
import org.geotoolkit.util.DomUtilities;
import org.opengis.util.FactoryException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Geotiff extension with partial support of DIMAP_v1.1 metadata format.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class DimapExtension extends GeoTiffExtension {

    private static final Logger LOGGER = Logging.getLogger(DimapExtension.class);

    @Override
    public boolean isPresent(Object input) {
        try {
            return searchMetadataFile(input) != null;
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Error searching dimap metadata file : "+e.getMessage(), e);
        }
        return false;
    }

    @Override
    public SpatialMetadata fillSpatialMetaData(TiffImageReader reader, SpatialMetadata metadata) throws IOException {
        final File metadataFile = searchMetadataFile(reader.getInput());

        if (metadataFile == null)
            throw new IOException("Dimap metadata file not found.");

        final Document doc;
        try {
            doc = DomUtilities.read(metadataFile);
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException(ex);
        }
        final Element dimapNode = doc.getDocumentElement();

        //add new format to SpatialMetadata
        final SpatialMetadata dimapMetadata = new SpatialMetadata(DimapMetadataFormat.INSTANCE, reader, metadata);
        dimapMetadata.mergeTree(DimapMetadataFormat.NATIVE_FORMAT, dimapNode);

        boolean geotkFormat = false;
        final String[] formatNames = dimapMetadata.getMetadataFormatNames();
        for (int i = 0; i < formatNames.length; i++) {
            String formatName = formatNames[i];
            if (formatName.equals(SpatialMetadataFormat.GEOTK_FORMAT_NAME)) {
                geotkFormat = true;
            }
        }

        //ensure GEOTK format before override parts of metadata
        if (!geotkFormat) {
            return dimapMetadata;
        }

        try {
            //temporal
            final Date prodDate = DimapAccessor.getProductionDate(dimapNode);
            GeoTiffExtension.setOrCreateSliceDimension(dimapMetadata, CommonCRS.Temporal.JAVA.crs(), prodDate.getTime());

        } catch (FactoryException e) {
            throw new IOException(e.getMessage(), e);
        }

        // add GridSampleDimensions definition to SpatialMetadata
        addSampleDimensions(dimapMetadata, dimapNode);

        return dimapMetadata;
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

    private void writeDimapMetadata(SpatialMetadata metadata) {
        if (metadata instanceof SpatialMetadata) {
            final SpatialMetadata md = (SpatialMetadata) metadata;
            final int index = Arrays.binarySearch(md.getMetadataFormatNames(), DimapMetadataFormat.NATIVE_FORMAT);

            if(index >= 0){
                //found some dimap metadatas, write them
                final Node node = md.getAsTree(DimapMetadataFormat.NATIVE_FORMAT);
                //TODO
                final Object output = null;
                try {
                    DomUtilities.write((Document) node, output);
                } catch (TransformerException| IOException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Search DIMAP metadata file.
     * E.g. : For an input geotiff named "myimage.tif"
     * DIMAP metadata file should be in the same directory and
     * can be named "metadata.dim" or "myimage.dim" or "myimage.DIM".
     *
     * @param input reader image input
     * @return File to metadata or null if not found.
     * @throws MalformedURLException
     */
    private File searchMetadataFile(final Object input) throws MalformedURLException {
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
}
