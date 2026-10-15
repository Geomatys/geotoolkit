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
package org.geotoolkit.processing.resource;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.ResourceProcessor;
import org.opengis.parameter.ParameterDescriptor;

/**
 * Parameters and utilities shared by the {@link ResourceProcessor} process descriptors.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ResourceProcessing {

    /**
     * Optional identifier of the created resource.
     */
    static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName("identifier")
            .setRemarks("Identifier of the created resource")
            .setRequired(false)
            .create(String.class, null);

    /**
     * Name of the output parameter holding the created resource.
     */
    static final String RESULT_NAME = "result";

    private ResourceProcessing() {
    }

    /**
     * Creates a resource processor configured with the {@link #IDENTIFIER} input value, if defined.
     *
     * @param input process input parameters
     * @return configured resource processor
     */
    static ResourceProcessor createProcessor(final Parameters input) {
        final ResourceProcessor processor = new ResourceProcessor();
        final String identifier = input.getValue(IDENTIFIER);
        if (identifier != null && !identifier.isBlank()) {
            processor.setIdentifier(Names.createLocalName(null, null, identifier));
        }
        return processor;
    }
}
