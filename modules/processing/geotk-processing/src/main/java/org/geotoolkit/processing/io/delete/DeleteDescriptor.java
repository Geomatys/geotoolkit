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
package org.geotoolkit.processing.io.delete;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.io.IOProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of Delete process.
 * name of the process : "delete"
 * inputs :
 * <ul>
 *     <li>PATH_IN "path" url,uri,file to delete</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>RESULT_OUT "result" result of the deletion</li>
 * </ul>
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DeleteDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : delete
     */
    public static final String NAME = "delete";

    /**
     * Mandatory - path
     */
    public static final ParameterDescriptor<Object> PATH_IN =
            new DefaultParameterDescriptor("path", "The path(URI,URL,File,String) to object to delete", 
            Object.class, null, true);
            
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{PATH_IN});

    /**
     * Mandatory - deletion result
     */
    public static final ParameterDescriptor<Boolean> RESULT_OUT =
            new DefaultParameterDescriptor("result", "Success of the deletion", 
            Boolean.class, null, true);
    
    /** 
     * Output Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",RESULT_OUT);
    
    public static final ProcessDescriptor INSTANCE = new DeleteDescriptor();

    private DeleteDescriptor() {
        super(NAME, IOProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Delete the inputed file or folder."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Delete(input);
    }
}
