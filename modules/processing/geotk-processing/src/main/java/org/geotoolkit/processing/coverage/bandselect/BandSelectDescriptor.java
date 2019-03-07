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
package org.geotoolkit.processing.coverage.bandselect;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandSelectDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:bandselect";

    /**
     * Mandatory - Coverage.
     */
    public static final ParameterDescriptor<GridCoverage> IN_COVERAGE = new ParameterBuilder()
            .addName("coverage")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandselect_inCoverage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandselect_inCoverageDesc))
            .setRequired(true)
            .create(GridCoverage.class,null);
    /**
     * Mandatory - bands to select.
     */
    public static final ParameterDescriptor<int[]> IN_BANDS = new ParameterBuilder()
            .addName("bands")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandselect_inBands))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandselect_inBandsDesc))
            .setRequired(true)
            .create(int[].class,null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").createGroup(IN_COVERAGE, IN_BANDS);

    /**
     * Mandatory - Resulting coverage.
     */
    public static final ParameterDescriptor<GridCoverage> OUT_COVERAGE = new ParameterBuilder()
            .addName("result")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandselect_outCoverage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_bandselect_outCoverageDesc))
            .setRequired(true)
            .create(GridCoverage.class,null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").createGroup(OUT_COVERAGE);

    public static final ProcessDescriptor INSTANCE = new BandSelectDescriptor();

    private BandSelectDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Select bands in a coverage."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new BandSelectProcess(input);
    }

}
