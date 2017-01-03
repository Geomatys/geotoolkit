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
package org.geotoolkit.processing.io.createtempfile;

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
 * Parameters description of CreateTempFolder process.
 * name of the process : "createTempFile"
 * inputs :
 * <ul>
 *     <li>PREFIX_IN "prefix" file prefix</li>
 *     <li>POSTFIX_IN "postfix" file postfix</li>
 *     <li>DELETE_IN "delete" file deletion on program exit</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FILE_OUT "file" resulting temp file</li>
 * </ul>
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class CreateTempFileDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : createTempFile 
     */
    public static final String NAME = "io:createTempFile";

    /**
     * Optional - file prefix
     */
    public static final ParameterDescriptor<String> PREFIX_IN = new ParameterBuilder()
            .addName("prefix")
            .setRemarks("The file prefix")
            .setRequired(false)
            .create(String.class, "temp");
    
    /**
     * Optional - file postfix
     */
    public static final ParameterDescriptor<String> POSTFIX_IN = new ParameterBuilder()
            .addName("postfix")
            .setRemarks("The file postfix")
            .setRequired(false)
            .create(String.class, "tmp");
    
    /**
     * Optional - delete on exit, default true.
     */
    public static final ParameterDescriptor<Boolean> DELETE_IN = new ParameterBuilder()
            .addName("delete")
            .setRemarks("Delete file on application end")
            .setRequired(false)
            .create(Boolean.class, true);
        
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(PREFIX_IN,POSTFIX_IN,DELETE_IN);

    /**
     * Mandatory - temporary output file
     */
    public static final ParameterDescriptor<URL> FILE_OUT = new ParameterBuilder()
            .addName("file")
            .setRemarks("Created temp file")
            .setRequired(true)
            .create(URL.class, null);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FILE_OUT);
    
    public static final ProcessDescriptor INSTANCE = new CreateTempFileDescriptor();

    private CreateTempFileDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a temporary file."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CreateTempFile(input);
    }
}
