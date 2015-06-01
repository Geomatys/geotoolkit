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
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.image.ImageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

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

    public static final String NAME = "dynamicrangestretch";

    /**
     * Mandatory - image.
     */
    public static final ParameterDescriptor<RenderedImage> IN_IMAGE;
    /**
     * Mandatory - bands to select.
     */
    public static final ParameterDescriptor<int[]> IN_BANDS;
    /**
     * Mandatory - bands range for colors.
     */
    public static final ParameterDescriptor<double[][]> IN_RANGES;
    
    public static final ParameterDescriptorGroup INPUT_DESC;
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<RenderedImage> OUT_IMAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;
    
    static {
        Map<String, Object> propertiesInCov = new HashMap<String, Object>();
        propertiesInCov.put(IdentifiedObject.NAME_KEY,        "image");
        propertiesInCov.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.inImage"));
        propertiesInCov.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.inImageDesc"));
        IN_IMAGE = new DefaultParameterDescriptor<RenderedImage>(propertiesInCov, RenderedImage.class, null, null, null, null, null, true);
        
        Map<String, Object> propertiesInBands = new HashMap<String, Object>();
        propertiesInBands.put(IdentifiedObject.NAME_KEY,        "bands");
        propertiesInBands.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.inBands"));
        propertiesInBands.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.inBandsDesc"));
        IN_BANDS = new DefaultParameterDescriptor<int[]>(propertiesInBands, int[].class, null, null, null, null, null, true);
        
        Map<String, Object> propertiesInRanges = new HashMap<String, Object>();
        propertiesInRanges.put(IdentifiedObject.NAME_KEY,        "ranges");
        propertiesInRanges.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.inRanges"));
        propertiesInRanges.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.inRangesDesc"));
        IN_RANGES = new DefaultParameterDescriptor<double[][]>(propertiesInRanges, double[][].class, null, null, null, null, null, true);
        
        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_IMAGE, IN_BANDS, IN_RANGES);

        Map<String, Object> propertiesOutCov = new HashMap<String, Object>();
        propertiesOutCov.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesOutCov.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.outImage"));
        propertiesOutCov.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString("org/geotoolkit/process/image/bundle", "colorstretch.outImageDesc"));
        OUT_IMAGE = new DefaultParameterDescriptor<RenderedImage>(propertiesOutCov, RenderedImage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_IMAGE);
    }

    public static final ProcessDescriptor INSTANCE = new DynamicRangeStretchDescriptor();

    private DynamicRangeStretchDescriptor() {
        super(NAME, ImageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Stretch colors in an image."), 
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public org.geotoolkit.process.Process createProcess(ParameterValueGroup input) {
        return new DynamicRangeStretchProcess(input);
    }
    
}
