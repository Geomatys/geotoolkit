/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.processing.image.dynamicrange;

import java.awt.image.RenderedImage;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Color Stretch is a process which calculates color components for each band.
 * A mapping for each band to color component is given.
 * A range for each band indicate how values are stretched.
 * Values inferior or superior to the range will be clamped.
 * NaN values are mapped to a transparent color.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DynamicRangeStretchDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "image:dynamicrangestretch";

    /**
     * Mandatory - image.
     */
    public static final ParameterDescriptor<RenderedImage> IN_IMAGE = new ParameterBuilder()
            .addName("image")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_inImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_inImageDesc))
            .setRequired(true)
            .create(RenderedImage.class, null);
    /**
     * Mandatory - bands to select.
     */
    public static final ParameterDescriptor<int[]> IN_BANDS = new ParameterBuilder()
            .addName("bands")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_inBands))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_inBandsDesc))
            .setRequired(true)
            .create(int[].class, null);
    /**
     * Mandatory - bands range for colors.
     */
    public static final ParameterDescriptor<double[][]> IN_RANGES = new ParameterBuilder()
            .addName("ranges")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_inRanges))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_inRangesDesc))
            .setRequired(true)
            .create(double[][].class, null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").createGroup(IN_IMAGE, IN_BANDS, IN_RANGES);

    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<RenderedImage> OUT_IMAGE = new ParameterBuilder()
            .addName("result")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_outImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_colorstretch_outImageDesc))
            .setRequired(true)
            .create(RenderedImage.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").createGroup(OUT_IMAGE);

    public static final ProcessDescriptor INSTANCE = new DynamicRangeStretchDescriptor();

    private DynamicRangeStretchDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Stretch colors in an image."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public org.geotoolkit.process.Process createProcess(ParameterValueGroup input) {
        return new DynamicRangeStretchProcess(input);
    }

}
