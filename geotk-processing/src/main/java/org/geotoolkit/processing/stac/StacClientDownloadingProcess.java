package org.geotoolkit.processing.stac;

import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.stac.client.DownloadURIExtractor;
import org.geotoolkit.stac.client.StacClient;
import org.geotoolkit.stac.dto.Item;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.BANDS;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.COLLECTION;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.EXTRACTOR_CLASS;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.OUTPUT;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.OUTPUT_DIRECTORY;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.SPATIAL_EXTENT;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.STAC_URL;
import static org.geotoolkit.processing.stac.StacClientDownloadingDescriptor.TEMPORAL_EXTENT;

/**
 * Execution class for the STAC Client Downloading Process.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientDownloadingProcess extends AbstractProcess {

    /**
     * Private logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            StacClientDownloadingProcess.class.getName());

    /**
     * Default constructor.
     *
     * @param desc process descriptor
     * @param parameter input parameters
     */
    public StacClientDownloadingProcess(final ProcessDescriptor desc,
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
            double[] bbox = null;
            if (spatialExtent != null) {
                GeneralEnvelope env = new GeneralEnvelope(spatialExtent);
                bbox = new double[]{
                        env.getMinimum(0), env.getMinimum(1),
                        env.getMaximum(0), env.getMaximum(1)
                };
            }

            String temporalExtent = null;
            if (temporalExtentArray != null && temporalExtentArray.length > 0) {
                if (temporalExtentArray.length == 1) {
                    temporalExtent = temporalExtentArray[0];
                } else if (temporalExtentArray.length >= 2) {
                    temporalExtent = temporalExtentArray[0] + "/"
                            + temporalExtentArray[1];
                }
            }

            final StacClient client;
            if (extractorClassName != null && !extractorClassName.trim().isEmpty()) {
                final Class<?> extractorClass = Class.forName(extractorClassName);
                final DownloadURIExtractor extractor = (DownloadURIExtractor)
                        extractorClass.getDeclaredConstructor().newInstance();
                client = new StacClient(HttpClient.newHttpClient(), extractor);
            } else {
                client = new StacClient();
            }

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
