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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.storage.ResourceProcessor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process calling {@link ResourceProcessor#concatenate(FeatureSet...)}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetConcatenateDescriptor extends AbstractProcessDescriptor {

    /** Process name : concatenate */
    public static final String NAME = "resource:concatenate";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<FeatureSet[]> SOURCES = new ParameterBuilder()
            .addName("sources")
            .setRemarks("Feature sets to concatenate, they must at least inherit a common data type")
            .setRequired(true)
            .create(FeatureSet[].class, null);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCES, IDENTIFIER);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<FeatureSet> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Concatenated feature set")
            .setRequired(true)
            .create(FeatureSet.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new FeatureSetConcatenateDescriptor();

    private FeatureSetConcatenateDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Concatenate feature sets of same or different types."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(FeatureSetConcatenateDescriptor.INSTANCE, input);
        }

        @Override
        protected void execute() throws ProcessException {
            final FeatureSet[] sources = inputParameters.getValue(SOURCES);

            final FeatureSet result;
            try {
                result = ResourceProcessing.createProcessor(inputParameters).concatenate(sources);
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
