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
package org.geotoolkit.processing.coverage.statistics;

import org.geotoolkit.metadata.ImageStatistics;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

import java.awt.image.RenderedImage;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 * Statistic process descriptor.
 *
 * @author bgarcia
 * @author Quentin Boileau (Geomatys)
 */
public class StatisticsDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:statistic";
    public static final InternationalString abs = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_abstract);
    public static final InternationalString displayName = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_display_name);

    /*
     * Image to analyse
     */
    public static final String IN_IMAGE_PARAM_NAME = "inImage";
    public static final InternationalString IN_IMAGE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_inImage);
    public static final ParameterDescriptor<RenderedImage> IMAGE = new ParameterBuilder()
            .addName(IN_IMAGE_PARAM_NAME)
            .setRemarks(IN_IMAGE_PARAM_REMARKS)
            .setRequired(false)
            .create(RenderedImage.class, null);

    /*
     * Coverage to analyse
     */
    public static final String IN_COVERAGE_PARAM_NAME = "inCoverage";
    public static final InternationalString IN_COVERAGE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_inCoverage);
    public static final ParameterDescriptor<GridCoverage2D> COVERAGE = new ParameterBuilder()
            .addName(IN_COVERAGE_PARAM_NAME)
            .setRemarks(IN_COVERAGE_PARAM_REMARKS)
            .setRequired(false)
            .create(GridCoverage2D.class, null);

    /*
     * CoverageResource to analyse
     */
    public static final String IN_REF_PARAM_NAME = "inReference";
    public static final InternationalString IN_REF_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_inReference);
    public static final ParameterDescriptor<CoverageResource> REF = new ParameterBuilder()
            .addName(IN_REF_PARAM_NAME)
            .setRemarks(IN_REF_PARAM_REMARKS)
            .setRequired(false)
            .create(CoverageResource.class, null);

    /*
    * GridCoverageReader to analyse
    */
    public static final String IN_READER_PARAM_NAME = "inReader";
    public static final InternationalString IN_READER_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_inReader);
    public static final ParameterDescriptor<GridCoverageReader> READER = new ParameterBuilder()
            .addName(IN_READER_PARAM_NAME)
            .setRemarks(IN_READER_PARAM_REMARKS)
            .setRequired(false)
            .create(GridCoverageReader.class, null);

    public static final String IN_IMAGE_IDX_PARAM_NAME = "inImageIdx";
    public static final InternationalString IN_IMAGE_IDX_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_inImageIdx);
    public static final ParameterDescriptor<Integer> IMAGE_IDX = new ParameterBuilder()
            .addName(IN_IMAGE_IDX_PARAM_NAME)
            .setRemarks(IN_IMAGE_IDX_PARAM_REMARKS)
            .setRequired(false)
            .create(Integer.class, 0);

    /*
     * Flag to exclude no-data from distribution
     */
    public static final String IN_EXCLUDE_NO_DATA_PARAM_NAME = "inExcludeNoData";
    public static final InternationalString IN_EXCLUDE_NO_DATA_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_inExcludeNoData);
    public static final ParameterDescriptor<Boolean> EXCLUDE_NO_DATA = new ParameterBuilder()
            .addName(IN_EXCLUDE_NO_DATA_PARAM_NAME)
            .setRemarks(IN_EXCLUDE_NO_DATA_PARAM_REMARKS)
            .setRequired(true)
            .create(Boolean.class, true);


    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(
                    IMAGE, COVERAGE, REF, READER, IMAGE_IDX, EXCLUDE_NO_DATA);

    /*
     * Coverage result
     */
    public static final String OUT_COVERAGE_PARAM_NAME = "outStatistic";
    public static final InternationalString OUT_COVERAGE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_statistic_outCoverage);
    public static final ParameterDescriptor<ImageStatistics> OUTCOVERAGE = new ParameterBuilder()
            .addName(OUT_COVERAGE_PARAM_NAME)
            .setRemarks(OUT_COVERAGE_PARAM_REMARKS)
            .setRequired(true)
            .create(ImageStatistics.class, null);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters")
            .createGroup(OUTCOVERAGE);


    public static final ProcessDescriptor INSTANCE = new StatisticsDescriptor();

    private StatisticsDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, abs, displayName, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Statistics(input);
    }

}
