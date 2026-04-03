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

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.stac.client.StacClient;
import org.geotoolkit.stac.client.StacResourceType;
import org.geotoolkit.stac.dto.Item;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base abstract class for STAC client processes. Handles extraction of common parameters
 * and coordinates the STAC API search logic. Subclasses implement the specific per-item
 * processing (e.g. downloading the file vs returning the URI) and parameter validation.
 *
 * @param <T> the type of the result accumulated for each item and returned as output
 * @author Quentin BIALOTA (Geomatys)
 */
public abstract class AbstractStacClientProcess<T> extends AbstractProcess {

    private static final Logger LOGGER = Logger.getLogger(AbstractStacClientProcess.class.getName());

    public AbstractStacClientProcess(ProcessDescriptor desc, ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected void execute() throws ProcessException {
        try {
            final String stacUrl = (String) inputParameters.parameter("stac_url").getValue();
            final String collection = (String) inputParameters.parameter("collection").getValue();
            final Envelope spatialExtent = (Envelope) inputParameters.parameter("spatial_extent").getValue();
            final String[] temporalExtentArray = (String[]) inputParameters.parameter("temporal_extent").getValue();
            final String extractorClassName = (String) inputParameters.parameter("extractor_class").getValue();

            if (stacUrl == null) {
                throw new ProcessException("Missing mandatory parameter: stac_url", this);
            }

            // Hook for sub-classes to validate their specific parameters
            validateParameters();

            // Create STAC client
            final StacClient client = StacClientProcessingUtils.getStacClient(extractorClassName);
            final List<T> results = new ArrayList<>();

            StacResourceType type;
            try {
                type = client.detectStacType(stacUrl);
            } catch (Exception e) {
                LOGGER.warning("Could not detect STAC type for URL " + stacUrl + " (fallback to UNKNOWN): " + e.getMessage());
                type = StacResourceType.UNKNOWN;
            }

            if (type == StacResourceType.UNKNOWN || type == StacResourceType.COLLECTION) {
                // UNKNOWN => We assume that the stacUrl is the root of STAC Service
                // COLLECTION => stacUrl is a Collection
                if (type == StacResourceType.UNKNOWN && collection == null) {
                    throw new ProcessException("Missing mandatory parameter for Collection: collection", this);
                }

                // Convert extents
                double[] bbox = StacClientProcessingUtils.getBbox(spatialExtent);
                String temporalExtent = StacClientProcessingUtils.getTemporalExtent(temporalExtentArray);

                final List<Item> items = client.searchItems(stacUrl, collection, bbox, temporalExtent);
                LOGGER.info("Found " + items.size() + " items.");

                for (final Item item : items) {
                    processItem(client, item, results);
                }
            } else if (client.isItem(stacUrl)) {
                // stacUrl is an individual Item
                Item item = client.loadItem(stacUrl);
                if (item != null) {
                    processItem(client, item, results);
                }
            } else {
                throw new ProcessException("Error during parsing Stac URL, " +
                        "check if your url points to a Stac service, collection or item.", this);
            }

            // Push final result to the output
            setOutput(results);

        } catch (Exception ex) {
            throw new ProcessException("Failed to execute STAC process: " + ex.getMessage(), this, ex);
        }
    }

    /**
     * Called before processing starts. Subclasses should override this to check
     * or extract their own mandatory parameters.
     *
     * @throws ProcessException if a parameter is missing or invalid
     */
    protected abstract void validateParameters() throws ProcessException;

    /**
     * Process a single STAC Item (e.g., getting its URI or downloading it) and append
     * the outcome to the results list.
     *
     * @param client  the STAC client
     * @param item    the current STAC item to process
     * @param results the accumulating list of results
     * @throws Exception if an error occurs
     */
    protected abstract void processItem(StacClient client, Item item, List<T> results) throws Exception;

    /**
     * Stores the accumulated results in the process output parameter.
     *
     * @param results the list of generic results
     */
    protected abstract void setOutput(List<T> results);
}
