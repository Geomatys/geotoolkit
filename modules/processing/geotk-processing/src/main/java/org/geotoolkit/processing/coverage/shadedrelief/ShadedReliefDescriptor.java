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

package org.geotoolkit.processing.coverage.shadedrelief;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.ProcessBundle;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ShadedReliefDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "shadedrelief";
    public static final InternationalString abs = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_shadedrelief_abstract);

    /*
     * Coverage base image
     */
    public static final String IN_COVERAGE_PARAM_NAME = "inCoverage";
    public static final InternationalString IN_COVERAGE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_shadedrelief_inCoverage);
    public static final ParameterDescriptor<GridCoverage2D> COVERAGE = new ParameterBuilder()
            .addName(IN_COVERAGE_PARAM_NAME)
            .setRemarks(IN_COVERAGE_PARAM_REMARKS)
            .setRequired(true)
            .create(GridCoverage2D.class, null);
    
    /*
     * Coverage elevation
     */
    public static final String IN_ELEVATION_PARAM_NAME = "inElevation";
    public static final InternationalString IN_ELEVATION_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_shadedrelief_inElevation);
    public static final ParameterDescriptor<GridCoverage2D> ELEVATION = new ParameterBuilder()
            .addName(IN_ELEVATION_PARAM_NAME)
            .setRemarks(IN_ELEVATION_PARAM_REMARKS)
            .setRequired(true)
            .create(GridCoverage2D.class, null);
    
    /*
     * Coverage elevation value to meters
     */
    public static final String IN_ELECONV_PARAM_NAME = "inEleEnv";
    public static final InternationalString IN_ELECONV_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_shadedrelief_inElevation);
    public static final ParameterDescriptor<MathTransform1D> ELECONV = new ParameterBuilder()
            .addName(IN_ELECONV_PARAM_NAME)
            .setRemarks(IN_ELECONV_PARAM_REMARKS)
            .setRequired(true)
            .create(MathTransform1D.class, null);

     /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(COVERAGE, ELEVATION, ELECONV);

    /*
     * Coverage result
     */
    public static final String OUT_COVERAGE_PARAM_NAME = "outCoverage";
    public static final InternationalString OUT_COVERAGE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_shadedrelief_outCoverage);
    public static final ParameterDescriptor<GridCoverage2D> OUTCOVERAGE = new ParameterBuilder()
            .addName(OUT_COVERAGE_PARAM_NAME)
            .setRemarks(OUT_COVERAGE_PARAM_REMARKS)
            .setRequired(true)
            .create(GridCoverage2D.class, null);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters").createGroup(OUTCOVERAGE);


    public static final ProcessDescriptor INSTANCE = new ShadedReliefDescriptor();

    private ShadedReliefDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, abs, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
       return new ShadedRelief(INSTANCE, input);
    }

}
