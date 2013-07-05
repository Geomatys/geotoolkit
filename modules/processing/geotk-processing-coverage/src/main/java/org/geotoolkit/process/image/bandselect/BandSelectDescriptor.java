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
package org.geotoolkit.process.image.bandselect;

import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.image.ImageProcessingRegistry;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandSelectDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "bandselect";

    /**
     * Mandatory - image.
     */
    public static final ParameterDescriptor<RenderedImage> IN_IMAGE;
    /**
     * Mandatory - bands to select.
     */
    public static final ParameterDescriptor<int[]> IN_BANDS;
    
    public static final ParameterDescriptorGroup INPUT_DESC;
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<RenderedImage> OUT_IMAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;
    
    static {
        Map<String, Object> propertiesInCov = new HashMap<String, Object>();
        propertiesInCov.put(IdentifiedObject.NAME_KEY,        "image");
        propertiesInCov.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "bandselect.inImage"));
        propertiesInCov.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "bandselect.inImageDesc"));
        IN_IMAGE = new DefaultParameterDescriptor<RenderedImage>(propertiesInCov, RenderedImage.class, null, null, null, null, null, true);
        
        Map<String, Object> propertiesInBands = new HashMap<String, Object>();
        propertiesInBands.put(IdentifiedObject.NAME_KEY,        "bands");
        propertiesInBands.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "bandselect.inBands"));
        propertiesInBands.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "bandselect.inBandsDesc"));
        IN_BANDS = new DefaultParameterDescriptor<int[]>(propertiesInBands, int[].class, null, null, null, null, null, true);
        
        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_IMAGE, IN_BANDS);

        Map<String, Object> propertiesOutCov = new HashMap<String, Object>();
        propertiesOutCov.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesOutCov.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "bandselect.outImage"));
        propertiesOutCov.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "bandselect.outImageDesc"));
        OUT_IMAGE = new DefaultParameterDescriptor<RenderedImage>(propertiesOutCov, RenderedImage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_IMAGE);
    }

    public static final ProcessDescriptor INSTANCE = new BandSelectDescriptor();

    private BandSelectDescriptor() {
        super(NAME, ImageProcessingRegistry.IDENTIFICATION, 
                new SimpleInternationalString("Select bands in a image."), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new BandSelectProcess(input);
    }
    
}
