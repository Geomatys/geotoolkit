/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/**
 * Descriptor for the STAC Client Items process, which retrieves download URIs for items matching specified criteria.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientItemsGetURIsDescriptor extends AbstractProcessDescriptor {

    /**
     * Name of the descriptor.
     */
    public static final String NAME = "stac.items.getURIs";

    /**
     * Abstract describing the process.
     */
    public static final InternationalString ABSTRACT =
            new SimpleInternationalString("Get STAC tiles from an endpoint.");

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
     * Name for the EXTRACTOR_CLASS parameter.
     */
    public static final String EXTRACTOR_CLASS_NAME = "extractor_class";
    private static final String EXTRACTOR_CLASS_REMARKS = "Fully qualified class name of a custom DownloadURIExtractor.";

    /**
     * EXTRACTOR_CLASS parameter descriptor.
     */
    public static final ParameterDescriptor<String> EXTRACTOR_CLASS = new ParameterBuilder()
            .addName(EXTRACTOR_CLASS_NAME)
            .setRemarks(EXTRACTOR_CLASS_REMARKS)
            .setRequired(false)
            .create(String.class, null);

    /**
     * Input parameters group.
     */
    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder().addName("InputParameters").setRequired(true)
            .createGroup(STAC_URL, COLLECTION, SPATIAL_EXTENT, TEMPORAL_EXTENT, BANDS, EXTRACTOR_CLASS);

    /**
     * Name for the OUTPUT parameter.
     */
    public static final String OUTPUT_NAME = "result";
    private static final String OUTPUT_REMARKS = "List of URIs.";

    /**
     * OUTPUT parameter descriptor.
     */
    @SuppressWarnings("unchecked")
    public static final ParameterDescriptor<List<URI>> OUTPUT = new ParameterBuilder()
            .addName(OUTPUT_NAME)
            .setRemarks(OUTPUT_REMARKS)
            .setRequired(true)
            .create((Class<List<URI>>) (Class<?>) List.class, null);

    /**
     * Output parameters group.
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters").setRequired(true)
            .createGroup(OUTPUT);

    /**
     * Public constructor used by the ServiceRegistry to find and instantiate all ProcessDescriptor.
     */
    public StacClientItemsGetURIsDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    /**
     * Process singleton instance.
     */
    public static final ProcessDescriptor INSTANCE = new StacClientItemsGetURIsDescriptor();

    /**
     * {@inheritDoc}
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new StacClientItemsGetURIsProcess(this, input);
    }
}
