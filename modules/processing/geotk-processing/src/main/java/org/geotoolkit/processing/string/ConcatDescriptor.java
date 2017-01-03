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

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
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
    public static final String NAME = "string:concat";

    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<String> PREFIX = new ParameterBuilder()
            .addName("prefix")
            .setRemarks("Prefix to add to the string value")
            .setRequired(false)
            .create(String.class, null);
    
    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<String> SUFFIX = new ParameterBuilder()
            .addName("suffix")
            .setRemarks("Suffix to add to the string value")
            .setRequired(false)
            .create(String.class, null);
    
    public static final ParameterDescriptor<String> VALUE = new ParameterBuilder()
            .addName("value")
            .setRemarks("The string value")
            .setRequired(true)
            .create(String.class, null);
            
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(PREFIX, SUFFIX, VALUE);

    /**
     * Mandatory - result files
     */
    public static final ParameterDescriptor<String> RESULT_OUT = new ParameterBuilder()
            .addName("result")
            .setRemarks("The concatened string")
            .setRequired(true)
            .create(String.class, null);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new ConcatDescriptor();

    public ConcatDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
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
