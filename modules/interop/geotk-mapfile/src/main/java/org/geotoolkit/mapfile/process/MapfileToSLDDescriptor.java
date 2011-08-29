/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.mapfile.process;

import java.io.File;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapfileToSLDDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : mapfileToSLD */
    public static final String NAME = "mapfileToSLD";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<File> IN_FILE =
            new DefaultParameterDescriptor("source", "mapfile", File.class, null, true);
    public static final ParameterDescriptor<File> IN_OUTPUT =
            new DefaultParameterDescriptor("target", "output sld", File.class, null, true);
  
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{IN_FILE,IN_OUTPUT});
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new MapfileToSLDDescriptor();

    private MapfileToSLDDescriptor() {
        super(NAME, MapfileProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Transform a mapfile in sld"),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new MapfileToSLDProcess(input);
    }
    
}
