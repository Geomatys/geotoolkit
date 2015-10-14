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
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.ProcessBundle;
import org.geotoolkit.processing.image.ImageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReplaceDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "replace";

    
    /**
     * Mandatory - Image where to replace samples.
     */
    public static final ParameterDescriptor<BufferedImage> IN_IMAGE;
    
    /**
     * Mandatory - samples to replace.
     * double[0] : original sample values
     * double[1] : new sample values
     */
    public static final ParameterDescriptor<double[][][]> IN_REPLACEMENTS;

    public static final ParameterDescriptorGroup INPUT_DESC;
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<BufferedImage> OUT_IMAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;
    
    static {
        Map<String, Object> propertiesInCov = new HashMap<>();
        propertiesInCov.put(IdentifiedObject.NAME_KEY,        "image");
        propertiesInCov.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inImage));
        propertiesInCov.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inImageDesc));
        IN_IMAGE = new DefaultParameterDescriptor<>(propertiesInCov, BufferedImage.class, null, null, null, null, null, true);
        
        Map<String, Object> propertiesInType = new HashMap<>();
        propertiesInType.put(IdentifiedObject.NAME_KEY,        "replacements");
        propertiesInType.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inReplacements));
        propertiesInType.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_inReplacementsDesc));
        IN_REPLACEMENTS = new DefaultParameterDescriptor<>(propertiesInType, double[][][].class, null, null, null, null, null, true);

        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_IMAGE, IN_REPLACEMENTS);

        Map<String, Object> propertiesOutCov = new HashMap<>();
        propertiesOutCov.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesOutCov.put(IdentifiedObject.ALIAS_KEY,       ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_outImage));
        propertiesOutCov.put(IdentifiedObject.REMARKS_KEY,     ProcessBundle.formatInternational(ProcessBundle.Keys.image_replace_outImageDesc));
        OUT_IMAGE = new DefaultParameterDescriptor<>(propertiesOutCov, BufferedImage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_IMAGE);
    }
    
    public static final ProcessDescriptor INSTANCE = new ReplaceDescriptor();
    
    public ReplaceDescriptor() {
        super(NAME, ImageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Replace sample values in image"), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ReplaceProcess(input);
    }
    
}
