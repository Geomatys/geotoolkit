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
package org.geotoolkit.processing.image.reformat;

import java.awt.image.RenderedImage;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReformatDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "image:reformat";


    /**
     * Mandatory - Image to reformat.
     */
    public static final ParameterDescriptor<RenderedImage> IN_IMAGE = new ParameterBuilder()
            .addName("image")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_reformat_inImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_reformat_inImageDesc))
            .setRequired(true)
            .create(RenderedImage.class, null);

    /**
     * Mandatory - new data type
     */
    public static final ParameterDescriptor<Integer> IN_DATATYPE = new ParameterBuilder()
            .addName("datatype")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_reformat_inType))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_reformat_inTypeDesc))
            .setRequired(true)
            .create(Integer.class, null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").createGroup(IN_IMAGE, IN_DATATYPE);

    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<RenderedImage> OUT_IMAGE = new ParameterBuilder()
            .addName("result")
            .addName(ProcessBundle.formatInternational(ProcessBundle.Keys.image_reformat_outImage))
            .setRemarks(ProcessBundle.formatInternational(ProcessBundle.Keys.image_reformat_outImageDesc))
            .setRequired(true)
            .create(RenderedImage.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").createGroup(OUT_IMAGE);

    public static final ProcessDescriptor INSTANCE = new ReformatDescriptor();

    private ReformatDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Change the sample type of a image."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ReformatProcess(input);
    }


}
