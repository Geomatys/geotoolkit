/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.processing.coverage.metadataextractor;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Alexis Manin(Geomatys)
 */
public class ExtractionProcess extends AbstractProcess {

    ExtractionProcess(final ParameterValueGroup input) {
       super(ExtractionDescriptor.INSTANCE, input);
    }

    /**
     *
     * @param source source to extract metadata from.
     *
     */
    public ExtractionProcess(Object source){
        super(ExtractionDescriptor.INSTANCE, asParameters(source));
    }

    private static ParameterValueGroup asParameters(Object source){
        final Parameters params = Parameters.castOrWrap(ExtractionDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(ExtractionDescriptor.IN_SOURCE).setValue(source);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return metadata
     * @throws ProcessException
     */
    public Metadata executeNow() throws ProcessException {
        execute();
        return (Metadata) outputParameters.parameter(ExtractionDescriptor.OUT_METADATA.getName().getCode()).getValue();
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        Object input = inputParameters.getOrCreate(ExtractionDescriptor.IN_SOURCE).getValue();
        Object reader = null;
        Metadata output = null;
        //Check if we get a file, or a reference to file.
        if (input instanceof String || input instanceof URL || input instanceof File) {
            reader = new ImageCoverageReader();
            try {
                ((ImageCoverageReader)reader).setInput(input);
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.processing.coverage.metadataextractor").log(Level.SEVERE, null, ex);
            }
        }
        //Coverage case is not supported yet
        if (input instanceof GridCoverage) {
            //TODO : add a convenience method into coverage interface to get metadata
        } else if (input instanceof GridCoverageResource) {
            try {
                reader = ((GridCoverageResource)input).acquireReader();
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.processing.coverage.metadataextractor").log(Level.SEVERE, null, ex);
            }
            //Case if we directly get a reader
        } else if (input instanceof GridCoverageReader || input instanceof ImageCoverageReader) {
            reader = input;
        }
        if (reader == null){
            throw new ProcessException("Input object is not supported for this operation", this, null);
        }
        //Try to find metadata
        if (reader instanceof GridCoverageReader){
            try {
                output = ((GridCoverageReader)reader).getMetadata();
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.processing.coverage.metadataextractor").log(Level.SEVERE, null, ex);
            }
        }
        if (reader instanceof ImageCoverageReader){
            try {
                output = ((ImageCoverageReader)reader).getMetadata();
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.processing.coverage.metadataextractor").log(Level.SEVERE, null, ex);
            }
        }
        outputParameters.getOrCreate(ExtractionDescriptor.OUT_METADATA).setValue(output);
    }

}
