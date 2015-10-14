/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.processing.coverage.merge;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.ProcessBundle;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 *  
 * 
 * @author Johann Sorel (Geomatys)
 */
public class MergeDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "merge";

    /**
     * Mandatory - Coverages to merge
     */
    public static final ParameterDescriptor<Coverage[]> IN_COVERAGES;
    
    /**
     * Mandatory - output coverage envelope
     */
    public static final ParameterDescriptor<Envelope> IN_ENVELOPE;
    
    /**
     * Mandatory - output coverage resolution
     */
    public static final ParameterDescriptor<Double> IN_RESOLUTION;

    public static final ParameterDescriptorGroup INPUT_DESC;
    
    /**
     * Mandatory - Resulting coverage.
     */
    public static final ParameterDescriptor<Coverage> OUT_COVERAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;
    
    static {
        Map<String, Object> propertiesInCov = new HashMap<String, Object>();
        propertiesInCov.put(IdentifiedObject.NAME_KEY,        "coverages");
        propertiesInCov.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_inCoverages));
        propertiesInCov.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_inCoveragesDesc));
        IN_COVERAGES = new DefaultParameterDescriptor<Coverage[]>(propertiesInCov, Coverage[].class, null, null, null, null, null, true);
        
        Map<String, Object> propertiesInEnv = new HashMap<String, Object>();
        propertiesInEnv.put(IdentifiedObject.NAME_KEY,        "envelope");
        propertiesInEnv.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_inEnvelope));
        propertiesInEnv.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_inEnvelopeDesc));
        IN_ENVELOPE = new DefaultParameterDescriptor<Envelope>(propertiesInEnv, Envelope.class, null, null, null, null, null, true);

        Map<String, Object> propertiesInResolution = new HashMap<String, Object>();
        propertiesInResolution.put(IdentifiedObject.NAME_KEY,        "resolution");
        propertiesInResolution.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_inResolution));
        propertiesInResolution.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_inResolutionDesc));
        IN_RESOLUTION = new DefaultParameterDescriptor<Double>(propertiesInResolution, Double.class, null, null, null, null, null, true);

        
        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_COVERAGES, IN_ENVELOPE, IN_RESOLUTION);

        Map<String, Object> propertiesOutCov = new HashMap<String, Object>();
        propertiesOutCov.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesOutCov.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_outCoverage));
        propertiesOutCov.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_merge_outCoverageDesc));
        OUT_COVERAGE = new DefaultParameterDescriptor<Coverage>(propertiesOutCov, Coverage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_COVERAGE);
    }

    public static final ProcessDescriptor INSTANCE = new MergeDescriptor();

    private MergeDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, 
                new SimpleInternationalString("Merge multiple coverages."), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new MergeProcess(input);
    }
    
    
}
