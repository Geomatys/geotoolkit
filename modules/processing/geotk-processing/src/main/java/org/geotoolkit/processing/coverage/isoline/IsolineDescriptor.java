/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.coverage.isoline;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.ProcessBundle;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Create a FeatureCollection of isoline from a GridCoverage2D and an array of intervals.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class IsolineDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "isoline";
    public static final InternationalString abs = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_abstract);

    /*
     * Coverage
     */
    public static final String IN_COVERAGE_PARAM_NAME = "inCoverage";
    public static final InternationalString IN_COVERAGE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_inCoverage);
    public static final ParameterDescriptor<GridCoverage2D> COVERAGE = new ParameterBuilder()
            .addName(IN_COVERAGE_PARAM_NAME)
            .setRemarks(IN_COVERAGE_PARAM_REMARKS)
            .setRequired(true)
            .create(GridCoverage2D.class, null);

    /*
     * Intervals
     */
    public static final String IN_INTERVAL_PARAM_NAME = "inIntervals";
    public static final InternationalString IN_INTERVAL_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_inIntervals);
    public static final ParameterDescriptor<double[]> INTERVALS = new ParameterBuilder()
            .addName(IN_INTERVAL_PARAM_NAME)
            .setRemarks(IN_INTERVAL_PARAM_REMARKS)
            .setRequired(true)
            .create(double[].class, null);

     /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(COVERAGE, INTERVALS);

    /*
     * FeatureCollection of isoline
     */
    public static final String OUT_FCOLL_PARAM_NAME = "outFeatureCollection";
    public static final InternationalString OUT_FCOLL_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_outFeatureCollection);
    public static final ParameterDescriptor<FeatureCollection> FCOLL = new ParameterBuilder()
            .addName(OUT_FCOLL_PARAM_NAME)
            .setRemarks(OUT_FCOLL_PARAM_REMARKS)
            .setRequired(true)
            .create(FeatureCollection.class, null);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName(
            "OutputParameters").createGroup(FCOLL);


    public static final ProcessDescriptor INSTANCE = new IsolineDescriptor();

    private IsolineDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, abs, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
       return new Isoline(INSTANCE, input);
    }

}
