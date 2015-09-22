package org.geotoolkit.metadata.dimap;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.metadata.geotiff.GeoTiffExtension;
import org.geotoolkit.util.DomUtilities;
import org.geotoolkit.util.dom.LazyLoadElement;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.metadata.dimap");

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
        dimapMetadata.mergeTree(DimapMetadataFormat.NATIVE_FORMAT, new LazyLoadElement(metadataFile));

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
            final Date prodDate = DimapAccessor.getImagingDate(dimapNode);
            if (prodDate != null) {
                GeoTiffExtension.setOrCreateSliceDimension(dimapMetadata, CommonCRS.Temporal.JAVA.crs(), prodDate.getTime());
            }

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
    private File searchMetadataFile(final Object input) throws IOException {
        if (input instanceof File) {
            final File file = (File) input;
            final File parent = file.getAbsoluteFile().getParentFile();

            // filename.dim
            final String pattern1 = ((File)IOUtilities.changeExtension(file, "dim")).getName();
            final String pattern2 = "metadata.dim";

            // Quick search for "filename.dim"
            final File candidate1 = new File(parent, pattern1);
            if (candidate1.exists()) return candidate1;

            // Quick search for "metadata.dim"
            final File candidate2 = new File(parent, pattern2);
            if (candidate2.exists()) return candidate2;

            // Full directory scan.
            // Search file with name matching patterns IGNORING case
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(parent.toPath())) {
                for (Path candidate : dirStream) {
                    if (pattern1.equalsIgnoreCase(candidate.getFileName().toString())) {
                        return candidate.toFile();
                    } else if (pattern2.equalsIgnoreCase(candidate.getFileName().toString())) {
                        return candidate.toFile();
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }
}
