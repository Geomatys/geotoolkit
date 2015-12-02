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
package org.geotoolkit.processing.coverage.reformat;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 *  
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ReformatDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "reformat";

    
    /**
     * Mandatory - Coverage to reformat
     */
    public static final ParameterDescriptor<Coverage> IN_COVERAGE;
    
    /**
     * Mandatory - new data type
     */
    public static final ParameterDescriptor<Integer> IN_DATATYPE;

    public static final ParameterDescriptorGroup INPUT_DESC;
    
    /**
     * Mandatory - Resulting coverage.
     */
    public static final ParameterDescriptor<Coverage> OUT_COVERAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;
    
    static {
        Map<String, Object> propertiesInCov = new HashMap<String, Object>();
        propertiesInCov.put(IdentifiedObject.NAME_KEY,        "coverage");
        propertiesInCov.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_reformat_inCoverage));
        propertiesInCov.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_reformat_inCoverageDesc));
        IN_COVERAGE = new DefaultParameterDescriptor<Coverage>(propertiesInCov, Coverage.class, null, null, null, null, null, true);
        
        Map<String, Object> propertiesInType = new HashMap<String, Object>();
        propertiesInType.put(IdentifiedObject.NAME_KEY,        "datatype");
        propertiesInType.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_reformat_inType));
        propertiesInType.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_reformat_inTypeDesc));
        IN_DATATYPE = new DefaultParameterDescriptor<Integer>(propertiesInType, Integer.class, null, null, null, null, null, true);

        INPUT_DESC = new ParameterBuilder().addName(NAME + "InputParameters").createGroup(IN_COVERAGE, IN_DATATYPE);

        Map<String, Object> propertiesOutCov = new HashMap<String, Object>();
        propertiesOutCov.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesOutCov.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_reformat_outCoverage));
        propertiesOutCov.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_reformat_outCoverageDesc));
        OUT_COVERAGE = new DefaultParameterDescriptor<Coverage>(propertiesOutCov, Coverage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new ParameterBuilder().addName(NAME + "OutputParameters").createGroup(OUT_COVERAGE);
    }

    public static final ProcessDescriptor INSTANCE = new ReformatDescriptor();

    private ReformatDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Change the sample type of a coverage."), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ReformatProcess(input);
    }
    
    
}
