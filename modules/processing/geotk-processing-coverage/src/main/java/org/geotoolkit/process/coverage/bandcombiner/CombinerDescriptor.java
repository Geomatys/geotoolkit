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
package org.geotoolkit.process.coverage.bandcombiner;

import java.util.List;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class CombinerDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "Combiner";
    
    /**
     * Mandatory - Bands to merge.
     */
    public static final ParameterDescriptor<Coverage> IN_RED =
            new DefaultParameterDescriptor<Coverage>("red","red band to combine.", Coverage.class,null,true);
    
    public static final ParameterDescriptor<Coverage> IN_GREEN =
            new DefaultParameterDescriptor<Coverage>("green","green band to combine.", Coverage.class,null,true);
    
    public static final ParameterDescriptor<Coverage> IN_BLUE =
            new DefaultParameterDescriptor<Coverage>("blue","blue band to combine.", Coverage.class,null,true);
    
    public static final ParameterDescriptorGroup INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_RED, IN_GREEN, IN_BLUE);
    
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<Coverage> OUT_BAND =
            new DefaultParameterDescriptor<Coverage>("result","Coverage created", Coverage.class,null,true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_BAND);
    
    public static final ProcessDescriptor INSTANCE = new CombinerDescriptor();
    
    private CombinerDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, new SimpleInternationalString("Get multiple raster bands to merge them into one entity"), INPUT_DESC, OUTPUT_DESC);
    }
    
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new CombinerProcess(input);
    }
    
}
