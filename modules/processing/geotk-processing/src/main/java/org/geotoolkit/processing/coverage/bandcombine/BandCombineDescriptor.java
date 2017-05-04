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
package org.geotoolkit.processing.coverage.bandcombine;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:bandcombine";

    /**
     * Mandatory - Coverages to combine
     */
    public static final ParameterDescriptor<Coverage[]> IN_COVERAGES = new ParameterBuilder()
                .addName("coverages")
                .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandcombine_inCoverages))
                .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandcombine_inCoveragesDesc))
                .setRequired(true)
                .create(Coverage[].class, null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").createGroup(IN_COVERAGES);

    /**
     * Mandatory - Resulting coverage.
     */
    public static final ParameterDescriptor<Coverage> OUT_COVERAGE = new ParameterBuilder()
            .addName("result")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandcombine_outCoverage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandcombine_outCoverageDesc))
            .setRequired(true)
            .create(Coverage.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").createGroup(OUT_COVERAGE);


    public static final ProcessDescriptor INSTANCE = new BandCombineDescriptor();

    private BandCombineDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Combine multiple coverage."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new BandCombineProcess(input);
    }

}
