/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.process.coverage.metadataextractor;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

    
/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class ExtractionDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "extractor";
    
    /**
     * Mandatory - Coverage to process
     */
    public static final ParameterDescriptor<Object> IN_SOURCE =
            new DefaultParameterDescriptor<Object>("source","Coverage to extract metadata.",Object.class,null,true);
    
    public static final ParameterDescriptorGroup INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_SOURCE);
    
        /**
     * Mandatory - Found metadata
     */
    public static final ParameterDescriptor<Metadata> OUT_METADATA =
            new DefaultParameterDescriptor<Metadata>("result","Extracted metadata.",Metadata.class,null,true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_METADATA);
    
    public static final ProcessDescriptor INSTANCE = new ExtractionDescriptor();

    private ExtractionDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, new SimpleInternationalString("Read the given coverage to retrieve metadata"), INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ExtractionProcess(input);
    }
}
