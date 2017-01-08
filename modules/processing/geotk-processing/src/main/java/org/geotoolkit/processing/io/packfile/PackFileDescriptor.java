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

package org.geotoolkit.processing.io.packfile;

import java.io.File;
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
 * @author Guilhem Legal Geomatys
 */
public class PackFileDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : unpack
     */
    public static final String NAME = "io:packFile";

    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<File[]> SOURCE_IN =new ParameterBuilder()
            .addName("source")
            .setRemarks("files to pack")
            .setRequired(true)
            .create(File[].class, null);
    
    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<File> TARGET_IN =new ParameterBuilder()
            .addName("target")
            .setRemarks("zip file destination")
            .setRequired(true)
            .create(File.class, null);
            
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCE_IN,TARGET_IN);

    /**
     * Mandatory - result files
     */
    public static final ParameterDescriptor<File> RESULT_OUT = new ParameterBuilder()
            .addName("zip-file")
            .setRemarks("packed file")
            .setRequired(true)
            .create(File.class, null);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new PackFileDescriptor();

    public PackFileDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Pack a file list into a compressed archive."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
        return new PackFile(input);
    }
}
