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

import java.util.logging.Level;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
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

        if (input instanceof Resource) {
            try {
                Metadata metadata = ((Resource) input).getMetadata();
                outputParameters.getOrCreate(ExtractionDescriptor.OUT_METADATA).setValue(metadata);
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.processing.coverage.metadataextractor").log(Level.SEVERE, null, ex);
            }
        } else {
            try (DataStore store = DataStores.open(input)) {
                Metadata metadata = store.getMetadata();
                outputParameters.getOrCreate(ExtractionDescriptor.OUT_METADATA).setValue(metadata);
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.processing.coverage.metadataextractor").log(Level.SEVERE, null, ex);
            }
        }
    }

}
