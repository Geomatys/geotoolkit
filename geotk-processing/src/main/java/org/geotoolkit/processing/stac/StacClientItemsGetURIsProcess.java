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
import org.geotoolkit.stac.client.StacClient;
import org.geotoolkit.stac.dto.Item;
import org.opengis.parameter.ParameterValueGroup;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import static org.geotoolkit.processing.stac.StacClientItemsGetURIsDescriptor.OUTPUT;

/**
 * Execution class for the STAC Client Process, which retrieves download URIs for items matching specified criteria.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientItemsGetURIsProcess extends AbstractStacClientProcess<URI> {

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

    @Override
    protected void validateParameters() throws ProcessException {
        // No specific additional parameters beyond the common STAC ones.
    }

    @Override
    protected void processItem(StacClient client, Item item, List<URI> results) {
        final URI downloadURI = client.getDownloadURI(item);
        if (downloadURI != null) {
            LOGGER.fine("Adding " + downloadURI);
            results.add(downloadURI);
        } else {
            LOGGER.warning("No download URI found for item: " + item.getId());
        }
    }

    @Override
    protected void setOutput(List<URI> results) {
        outputParameters.getOrCreate(OUTPUT).setValue(results);
    }
}
