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
package org.geotoolkit.process.coverage.statistics;

import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * @author bgarcia
 */
public class StatisticsDescriptor extends AbstractProcessDescriptor{

    private static final String BUNDLE_PATH = "org/geotoolkit/process/coverage/bundle";
    private static final String ABSTRACT_KEY    = "statistic.abstract";
    private static final String DISPLAY_NAME_KEY    = "statistic.display.name";
    private static final String IN_COVERAGE_KEY = "statistic.inCoverage";
    private static final String OUT_STATISTIC_KEY   = "statistic.outStatistic";


    public static final String NAME = "statistic";
    public static final InternationalString abs = new ResourceInternationalString(BUNDLE_PATH, ABSTRACT_KEY);
    public static final InternationalString displayName = new ResourceInternationalString(BUNDLE_PATH, DISPLAY_NAME_KEY);

    /*
     * Coverage base image
     */
    public static final String IN_COVERAGE_PARAM_NAME = "inCoverage";
    public static final InternationalString IN_COVERAGE_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, IN_COVERAGE_KEY);
    public static final ParameterDescriptor<GridCoverage2D> COVERAGE =
            new DefaultParameterDescriptor(IN_COVERAGE_PARAM_NAME, IN_COVERAGE_PARAM_REMARKS, GridCoverage2D.class, null, true);


    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
                    new GeneralParameterDescriptor[]{COVERAGE});

    /*
     * Coverage result
     */
    public static final String OUT_COVERAGE_PARAM_NAME = "outStatistic";
    public static final InternationalString OUT_COVERAGE_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, OUT_STATISTIC_KEY);
    public static final ParameterDescriptor<ImageStatistics> OUTCOVERAGE =
            new DefaultParameterDescriptor(OUT_COVERAGE_PARAM_NAME, OUT_COVERAGE_PARAM_REMARKS, ImageStatistics.class, null, true);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{OUTCOVERAGE});


    public static final ProcessDescriptor INSTANCE = new StatisticsDescriptor();

    private StatisticsDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, abs, displayName, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Statistics(input);
    }

}
