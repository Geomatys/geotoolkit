package org.geotoolkit.processing.stac;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.stac.client.StacClient;
import org.geotoolkit.stac.dto.Item;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.BANDS;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.COLLECTION;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.EXTRACTOR_CLASS;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.OUTPUT;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.OUTPUT_DIRECTORY;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.SPATIAL_EXTENT;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.STAC_URL;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.TEMPORAL_EXTENT;

/**
 * Execution class for the STAC Client Downloading Process.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientItemsDownloadingProcess extends AbstractProcess {

    /**
     * Private logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            StacClientItemsDownloadingProcess.class.getName());

    /**
     * Default constructor.
     *
     * @param desc process descriptor
     * @param parameter input parameters
     */
    public StacClientItemsDownloadingProcess(final ProcessDescriptor desc,
                                             final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        try {
            final String stacUrl = inputParameters.getValue(STAC_URL);
            final String collection = inputParameters.getValue(COLLECTION);
            final Envelope spatialExtent = inputParameters.getValue(SPATIAL_EXTENT);
            final String[] temporalExtentArray =
                    inputParameters.getValue(TEMPORAL_EXTENT);
            final String[] bands = inputParameters.getValue(BANDS);
            final Path outputDirectory = inputParameters.getValue(OUTPUT_DIRECTORY);
            final String extractorClassName = inputParameters.getValue(EXTRACTOR_CLASS);

            if (stacUrl == null || collection == null || outputDirectory == null) {
                throw new ProcessException(
                        "Missing mandatory obj: stacUrl, collection, or outputDirectory",
                        this);
            }

            if (!Files.exists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
            }

            // Convert extent
            double[] bbox = StacClientProcessingUtils.getBbox(spatialExtent);

            // Convert temporal extent
            String temporalExtent = StacClientProcessingUtils.getTemporalExtent(temporalExtentArray);

            // Create STAC client
            final StacClient client = StacClientProcessingUtils.getStacClient(extractorClassName);

            final List<Item> items = client.searchItems(stacUrl, collection,
                    bbox, temporalExtent);

            final List<Path> downloadedPaths = new ArrayList<>();
            LOGGER.info("Found " + items.size() + " items to download.");

            for (final Item item : items) {
                final URI downloadURI = client.getDownloadURI(item);
                if (downloadURI != null) {
                    LOGGER.info("Downloading " + downloadURI);
                    final Path downloadedFile = client.downloadFile(downloadURI,
                            outputDirectory);
                    downloadedPaths.add(downloadedFile);
                } else {
                    LOGGER.warning("No download URI found for item: "
                            + item.getId());
                }
            }

            outputParameters.getOrCreate(OUTPUT).setValue(downloadedPaths);

        } catch (Exception ex) {
            throw new ProcessException(
                    "Failed to execute STAC download process: " + ex.getMessage(),
                    this, ex);
        }
    }
}
