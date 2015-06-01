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


import java.io.File;
import java.io.IOException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.io.createtempfile.CreateTempFileDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Create a temporary file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CreateTempFile extends AbstractProcess {

    public CreateTempFile(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        fireProcessStarted("Starting create temporary file");

        final String prefix = getOrCreate(PREFIX_IN, inputParameters).stringValue();
        final String postfix = getOrCreate(POSTFIX_IN, inputParameters).stringValue();
        final boolean eraseParam = getOrCreate(DELETE_IN, inputParameters).booleanValue();
        
        final File file;
        try {
            file = File.createTempFile(prefix, postfix);            
            
            if(eraseParam){
                file.deleteOnExit();
            }

            getOrCreate(FILE_OUT, outputParameters).setValue(file.toURI().toURL());
            
        } catch (IOException ex) {
            fireProcessFailed("Failed creating temp file", ex);
            return;
        }
        
        fireProcessCompleted("Temporay file created.");
    }

}
