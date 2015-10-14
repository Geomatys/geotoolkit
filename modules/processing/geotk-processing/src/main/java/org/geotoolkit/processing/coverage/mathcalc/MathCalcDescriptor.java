/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.coverage.mathcalc;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.ProcessBundle;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcDescriptor extends AbstractProcessDescriptor {
    
    public static final String NAME = "mathcalc";

    /**
     * Coverage images
     */
    public static final ParameterDescriptor<Coverage[]> IN_COVERAGES =
            new DefaultParameterDescriptor("inCoverages", 
                    ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_mathcalc_inCoverages),
                    Coverage[].class, null, true);
    
    /**
     * Mathematic expression
     */
    public static final ParameterDescriptor<String> IN_FORMULA =
            new DefaultParameterDescriptor("inFormula", 
                    ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_mathcalc_inFormula),
                    String.class, null, true);
    
    /**
     * Mapping of input coverages to formula names.
     */
    public static final ParameterDescriptor<String[]> IN_MAPPING =
            new DefaultParameterDescriptor("inMapping", 
                    ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_mathcalc_inMapping),
                    String[].class, null, true);
    
    /**
     * Writable coverage where the expression result will be written.
     * 
     * TODO this must be writable
     */
    public static final ParameterDescriptor<CoverageReference> IN_RESULT_COVERAGE =
            new DefaultParameterDescriptor("inResultCoverage", 
                    ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_mathcalc_inResultCoverage),
                    CoverageReference.class, null, true);
    
     /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{IN_COVERAGES, IN_FORMULA, IN_MAPPING, IN_RESULT_COVERAGE});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{});
    
    public MathCalcDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, 
                new SimpleInternationalString("Perform a mathematic equation on input datas."),
                INPUT_DESC, OUTPUT_DESC);
    }

    public static final MathCalcDescriptor INSTANCE = new MathCalcDescriptor();
    
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new MathCalcProcess(input);
    }
    
}
