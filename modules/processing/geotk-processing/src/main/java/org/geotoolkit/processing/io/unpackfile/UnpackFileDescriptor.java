/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.io.unpackfile;

import java.net.URL;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of UnpackFile process.
 * name of the process : "unpack"
 * inputs :
 * <ul>
 *     <li>SOURCE_IN "source" url,uri,file to read from</li>
 *     <li>TARGET_IN "target" url,uri,file destination</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>RESULT_OUT "files" result files</li>
 * </ul>
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class UnpackFileDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : unpack
     */
    public static final String NAME = "io:unpackFile";

    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<Object> SOURCE_IN =new ParameterBuilder()
            .addName("source")
            .setRemarks("url,uri,file to read from")
            .setRequired(true)
            .create(Object.class, null);
    
    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<Object> TARGET_IN =new ParameterBuilder()
            .addName("target")
            .setRemarks("url,uri,file destination")
            .setRequired(true)
            .create(Object.class, null);
            
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCE_IN,TARGET_IN);

    /**
     * Mandatory - result files
     */
    public static final ParameterDescriptor<URL[]> RESULT_OUT =new ParameterBuilder()
            .addName("files")
            .setRemarks("unpacked files")
            .setRequired(true)
            .create(URL[].class, null);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new UnpackFileDescriptor();

    private UnpackFileDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Unpack a compressed archive in a given directory, supports : zip,jar,tar,tar.gz ."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new UnpackFile(input);
    }
}
