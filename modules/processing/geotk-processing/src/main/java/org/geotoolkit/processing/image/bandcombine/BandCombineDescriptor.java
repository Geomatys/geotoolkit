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
package org.geotoolkit.processing.image.bandcombine;

import java.awt.image.RenderedImage;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "image:bandcombine";

    /**
     * Mandatory - Images to combine
     */
    public static final ParameterDescriptor<RenderedImage[]> IN_IMAGES = new ParameterBuilder()
            .addName("images")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_bandcombine_inImages))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_bandcombine_inImagesDesc))
            .setRequired(true)
            .create(RenderedImage[].class, null);
    
    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").createGroup(IN_IMAGES);
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<RenderedImage> OUT_IMAGE = new ParameterBuilder()
            .addName("result")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_bandcombine_outImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_bandcombine_outImageDesc))
            .setRequired(true)
            .create(RenderedImage.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").createGroup(OUT_IMAGE);
    
    public static final ProcessDescriptor INSTANCE = new BandCombineDescriptor();

    private BandCombineDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Combine multiple images."), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new BandCombineProcess(input);
    }
    
}
