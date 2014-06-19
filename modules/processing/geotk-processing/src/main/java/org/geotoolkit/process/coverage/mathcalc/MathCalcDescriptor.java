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

package org.geotoolkit.process.coverage.mathcalc;

import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
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

    private static final String BUNDLE_PATH = "org/geotoolkit/process/coverage/bundle";
    
    public static final String NAME = "mathcalc";

    /**
     * Coverage images
     */
    public static final ParameterDescriptor<Coverage[]> IN_COVERAGES =
            new DefaultParameterDescriptor("inCoverages", 
                    new ResourceInternationalString(BUNDLE_PATH, "mathcalc.inCoverages"),
                    Coverage[].class, null, true);
    
    /**
     * Mathematic expression
     */
    public static final ParameterDescriptor<String> IN_FORMULA =
            new DefaultParameterDescriptor("inFormula", 
                    new ResourceInternationalString(BUNDLE_PATH, "mathcalc.inFormula"),
                    String.class, null, true);
    
    /**
     * Mapping of input coverages to formula names.
     */
    public static final ParameterDescriptor<String[]> IN_MAPPING =
            new DefaultParameterDescriptor("inMapping", 
                    new ResourceInternationalString(BUNDLE_PATH, "mathcalc.inMapping"),
                    String[].class, null, true);
    
    /**
     * Writable coverage where the expression result will be written.
     * 
     * TODO this must be writable
     */
    public static final ParameterDescriptor<CoverageReference> IN_RESULT_COVERAGE =
            new DefaultParameterDescriptor("inResultCoverage", 
                    new ResourceInternationalString(BUNDLE_PATH, "mathcalc.inResultCoverage"),
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
