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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.OUTPUT;
import static org.geotoolkit.processing.stac.StacClientItemsDownloadingDescriptor.OUTPUT_DIRECTORY;

/**
 * Execution class for the STAC Client Downloading Process.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientItemsDownloadingProcess extends AbstractStacClientProcess<Path> {

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

    @Override
    protected void validateParameters() throws ProcessException {
        final Path outputDirectory = inputParameters.getValue(OUTPUT_DIRECTORY);
        if (outputDirectory == null) {
            throw new ProcessException("Missing mandatory parameter: outputDirectory", this);
        }

        try {
            if (!Files.exists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
            }
        } catch (Exception ex) {
            throw new ProcessException("Could not create output directory: " + outputDirectory, this, ex);
        }
    }

    @Override
    protected void processItem(StacClient client, Item item, List<Path> results) throws Exception {
        final Path outputDirectory = inputParameters.getValue(OUTPUT_DIRECTORY);
        final URI downloadURI = client.getDownloadURI(item);

        if (downloadURI != null) {
            LOGGER.fine("Downloading " + downloadURI);
            final Path downloadedFile = client.downloadFile(downloadURI, outputDirectory);
            results.add(downloadedFile);
        } else {
            LOGGER.warning("No download URI found for item: " + item.getId());
        }
    }

    @Override
    protected void setOutput(List<Path> results) {
        outputParameters.getOrCreate(OUTPUT).setValue(results);
    }
}
