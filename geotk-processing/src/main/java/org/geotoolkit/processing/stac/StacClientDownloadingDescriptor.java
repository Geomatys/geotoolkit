package org.geotoolkit.processing.stac;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;
import java.nio.file.Path;
import java.util.List;

/**
 * Descriptor for the STAC Client Downloading process.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientDownloadingDescriptor extends AbstractProcessDescriptor {

    /**
     * Name of the descriptor.
     */
    public static final String NAME = "stac.downloading";

    /**
     * Abstract describing the process.
     */
    public static final InternationalString ABSTRACT =
            new SimpleInternationalString("Download STAC tiles from an endpoint.");

    /**
     * Name for the STAC_URL parameter.
     */
    public static final String STAC_URL_NAME = "stac_url";
    private static final String STAC_URL_REMARKS = "The STAC API base URL.";

    /**
     * STAC_URL parameter descriptor.
     */
    public static final ParameterDescriptor<String> STAC_URL = new ParameterBuilder()
            .addName(STAC_URL_NAME)
            .setRemarks(STAC_URL_REMARKS)
            .setRequired(true)
            .create(String.class, null);

    /**
     * Name for the COLLECTION parameter.
     */
    public static final String COLLECTION_NAME = "collection";
    private static final String COLLECTION_REMARKS = "The STAC Collection ID.";

    /**
     * COLLECTION parameter descriptor.
     */
    public static final ParameterDescriptor<String> COLLECTION = new ParameterBuilder()
            .addName(COLLECTION_NAME)
            .setRemarks(COLLECTION_REMARKS)
            .setRequired(true)
            .create(String.class, null);

    /**
     * Name for the SPATIAL_EXTENT parameter.
     */
    public static final String SPATIAL_EXTENT_NAME = "spatial_extent";
    private static final String SPATIAL_EXTENT_REMARKS = "The bounding box / spatial extent.";

    /**
     * SPATIAL_EXTENT parameter descriptor.
     */
    public static final ParameterDescriptor<Envelope> SPATIAL_EXTENT = new ParameterBuilder()
            .addName(SPATIAL_EXTENT_NAME)
            .setRemarks(SPATIAL_EXTENT_REMARKS)
            .setRequired(false)
            .create(Envelope.class, null);

    /**
     * Name for the TEMPORAL_EXTENT parameter.
     */
    public static final String TEMPORAL_EXTENT_NAME = "temporal_extent";
    private static final String TEMPORAL_EXTENT_REMARKS = "The temporal extent to search.";

    /**
     * TEMPORAL_EXTENT parameter descriptor.
     */
    public static final ParameterDescriptor<String[]> TEMPORAL_EXTENT = new ParameterBuilder()
            .addName(TEMPORAL_EXTENT_NAME)
            .setRemarks(TEMPORAL_EXTENT_REMARKS)
            .setRequired(false)
            .create(String[].class, null);

    /**
     * Name for the BANDS parameter.
     */
    public static final String BANDS_NAME = "bands";
    private static final String BANDS_REMARKS = "Bands to filter on or to include.";

    /**
     * BANDS parameter descriptor.
     */
    public static final ParameterDescriptor<String[]> BANDS = new ParameterBuilder()
            .addName(BANDS_NAME)
            .setRemarks(BANDS_REMARKS)
            .setRequired(false)
            .create(String[].class, null);

    /**
     * Name for the OUTPUT_DIRECTORY parameter.
     */
    public static final String OUTPUT_DIRECTORY_NAME = "output_directory";
    private static final String OUTPUT_DIRECTORY_REMARKS = "The output directory where items will be downloaded.";

    /**
     * OUTPUT_DIRECTORY parameter descriptor.
     */
    public static final ParameterDescriptor<Path> OUTPUT_DIRECTORY = new ParameterBuilder()
            .addName(OUTPUT_DIRECTORY_NAME)
            .setRemarks(OUTPUT_DIRECTORY_REMARKS)
            .setRequired(true)
            .create(Path.class, null);

    /**
     * Input parameters group.
     */
    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder().addName("InputParameters").setRequired(true)
            .createGroup(STAC_URL, COLLECTION, SPATIAL_EXTENT, TEMPORAL_EXTENT, BANDS, OUTPUT_DIRECTORY);

    /**
     * Name for the OUTPUT parameter.
     */
    public static final String OUTPUT_NAME = "result";
    private static final String OUTPUT_REMARKS = "List of paths to downloaded items.";

    /**
     * OUTPUT parameter descriptor.
     */
    @SuppressWarnings("unchecked")
    public static final ParameterDescriptor<List<Path>> OUTPUT = new ParameterBuilder()
            .addName(OUTPUT_NAME)
            .setRemarks(OUTPUT_REMARKS)
            .setRequired(true)
            .create((Class<List<Path>>) (Class<?>) List.class, null);

    /**
     * Output parameters group.
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters").setRequired(true)
            .createGroup(OUTPUT);

    /**
     * Public constructor used by the ServiceRegistry to find and instantiate all ProcessDescriptor.
     */
    public StacClientDownloadingDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    /**
     * Process singleton instance.
     */
    public static final ProcessDescriptor INSTANCE = new StacClientDownloadingDescriptor();

    /**
     * {@inheritDoc}
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new StacClientDownloadingProcess(this, input);
    }
}
