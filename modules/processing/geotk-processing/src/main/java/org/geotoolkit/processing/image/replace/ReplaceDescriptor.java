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

package org.geotoolkit.processing.image.replace;

import java.awt.image.BufferedImage;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReplaceDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "image:replace";

    
    /**
     * Mandatory - Image where to replace samples.
     */
    public static final ParameterDescriptor<BufferedImage> IN_IMAGE = new ParameterBuilder()
            .addName("image")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inImageDesc))
            .setRequired(true)
            .create(BufferedImage.class, null);
    
    /**
     * Mandatory - samples to replace.
     * double[0] : original sample values
     * double[1] : new sample values
     */
    public static final ParameterDescriptor<double[][][]> IN_REPLACEMENTS = new ParameterBuilder()
            .addName("replacements")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inReplacements))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inReplacementsDesc))
            .setRequired(true)
            .create(double[][][].class, null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").createGroup(IN_IMAGE, IN_REPLACEMENTS);
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<BufferedImage> OUT_IMAGE = new ParameterBuilder()
            .addName("result")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_outImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_outImageDesc))
            .setRequired(true)
            .create(BufferedImage.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").createGroup(OUT_IMAGE);
    
    public static final ProcessDescriptor INSTANCE = new ReplaceDescriptor();
    
    public ReplaceDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Replace sample values in image"), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ReplaceProcess(input);
    }
    
}
