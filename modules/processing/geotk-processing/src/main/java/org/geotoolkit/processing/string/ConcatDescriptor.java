/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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

package org.geotoolkit.processing.string;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ConcatDescriptor  extends AbstractProcessDescriptor {

    /**
     * Process name : unpack
     */
    public static final String NAME = "concat";

    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<String> PREFIX =
            new DefaultParameterDescriptor("prefix", "Prefix to add to the string value", 
            String.class, null, false);
    
    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<String> SUFFIX =
            new DefaultParameterDescriptor("suffix", "Suffix to add to the string value", 
            String.class, null, false);
    
    public static final ParameterDescriptor<String> VALUE =
            new DefaultParameterDescriptor("value", "The string value", 
            String.class, null, true);
            
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{PREFIX, SUFFIX, VALUE});

    /**
     * Mandatory - result files
     */
    public static final ParameterDescriptor<String> RESULT_OUT =
            new DefaultParameterDescriptor("result", "The concatened string", 
            String.class, null, true);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new ConcatDescriptor();

    public ConcatDescriptor() {
        super(NAME, StringProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Concatenate a string with a sufix and/or a prefix."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
        return new Concat(input);
    }
}
