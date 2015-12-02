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
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.io.IOProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
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
 * @module pending
 */
public final class CreateTempFileDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : createTempFile 
     */
    public static final String NAME = "createTempFile";

    /**
     * Optional - file prefix
     */
    public static final ParameterDescriptor<String> PREFIX_IN =
            new DefaultParameterDescriptor("prefix", "The file prefix", 
            String.class, "temp", false);
    
    /**
     * Optional - file postfix
     */
    public static final ParameterDescriptor<String> POSTFIX_IN =
            new DefaultParameterDescriptor("postfix", "The file postfix", 
            String.class, "tmp", false);
    
    /**
     * Optional - delete on exit, default true.
     */
    public static final ParameterDescriptor<Boolean> DELETE_IN =
            new DefaultParameterDescriptor("delete", "Delete file on application end", 
            Boolean.class, true, false);
        
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(PREFIX_IN,POSTFIX_IN,DELETE_IN);

    /**
     * Mandatory - temporary output file
     */
    public static final ParameterDescriptor<URL> FILE_OUT =
            new DefaultParameterDescriptor("file", "Created temp file", 
            URL.class, null, true);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FILE_OUT);
    
    public static final ProcessDescriptor INSTANCE = new CreateTempFileDescriptor();

    private CreateTempFileDescriptor() {
        super(NAME, IOProcessingRegistry.IDENTIFICATION,
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
