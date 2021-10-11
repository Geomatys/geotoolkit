/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.processing.image.statistics;

import java.awt.image.RenderedImage;
import org.apache.sis.math.Statistics;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Describe attribut which {@link ImageStatisticsProcess} need.
 *
 * @author Remi Marechal (Geomatys).
 */
public class ImageStatisticsDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "image:statistics";

    public static final String INPUT_IMAGE_PARAM_NAME = "image_in";

    /**
     * Input image.
     */
    public static final ParameterDescriptor<RenderedImage> INPUT_IMAGE = new ParameterBuilder()
            .addName(INPUT_IMAGE_PARAM_NAME)
            .setRemarks("Input image")
            .setRequired(true)
            .create(RenderedImage.class, null);

    public static final String OUTPUT_STATS_PARAM_NAME = "statistic_out";

    /**
     * Output statistic object.
     */
    public static final ParameterDescriptor<Statistics[]> OUTPUT_STATS = new ParameterBuilder()
            .addName(OUTPUT_STATS_PARAM_NAME)
            .setRemarks("Output statistic")
            .setRequired(true)
            .create(Statistics[].class, null);


    /**
     * Input parameters.
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(INPUT_IMAGE);

    /**
     * Output parameters.
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(OUTPUT_STATS);

    /**
     * Instance.
     */
    public static final ProcessDescriptor INSTANCE = new ImageStatisticsDescriptor();

    /**
     * Default constructor.
     */
    private ImageStatisticsDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Parameter description of Image to get its Statistics."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ImageStatisticsProcess(input);
    }
}
