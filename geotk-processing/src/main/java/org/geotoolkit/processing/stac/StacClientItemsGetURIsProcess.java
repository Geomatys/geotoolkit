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
import org.geotoolkit.stac.dto.Item;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.BANDS;
import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.COLLECTION;
import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.EXTRACTOR_CLASS;
import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.OUTPUT;
import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.SPATIAL_EXTENT;
import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.STAC_URL;
import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.TEMPORAL_EXTENT;

/**
 * Execution class for the STAC Client Process, which retrieves download URIs for items matching specified criteria.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientItemsGetURIsProcess extends AbstractProcess {

    /**
     * Private logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            StacClientItemsGetURIsProcess.class.getName());

    /**
     * Default constructor.
     *
     * @param desc process descriptor
     * @param parameter input parameters
     */
    public StacClientItemsGetURIsProcess(final ProcessDescriptor desc,
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
            final String extractorClassName = inputParameters.getValue(EXTRACTOR_CLASS);

            if (stacUrl == null || collection == null) {
                throw new ProcessException(
                        "Missing mandatory obj: stacUrl or collection",
                        this);
            }

            // Convert extent
            double[] bbox = StacClientProcessingUtils.getBbox(spatialExtent);

            // Convert temporal extent
            String temporalExtent = StacClientProcessingUtils.getTemporalExtent(temporalExtentArray);

            // Create STAC client
            final StacClient client = StacClientProcessingUtils.getStacClient(extractorClassName);

            final List<Item> items = client.searchItems(stacUrl, collection,
                    bbox, temporalExtent);

            final List<URI> assetsURIs = new ArrayList<>();
            LOGGER.info("Found " + items.size() + " items.");

            for (final Item item : items) {
                final URI downloadURI = client.getDownloadURI(item);
                if (downloadURI != null) {
                    LOGGER.info("Adding " + downloadURI);
                    assetsURIs.add(downloadURI);
                } else {
                    LOGGER.warning("No download URI found for item: "
                            + item.getId());
                }
            }

            outputParameters.getOrCreate(OUTPUT).setValue(assetsURIs);

        } catch (Exception ex) {
            throw new ProcessException(
                    "Failed to execute STAC download process: " + ex.getMessage(),
                    this, ex);
        }
    }
}
