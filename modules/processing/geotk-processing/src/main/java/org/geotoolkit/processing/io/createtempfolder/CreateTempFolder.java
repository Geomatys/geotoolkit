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
package org.geotoolkit.processing.io.createtempfolder;


import java.io.File;
import java.io.IOException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.io.createtempfolder.CreateTempFolderDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Create a temporary folder.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CreateTempFolder extends AbstractProcess {

    public CreateTempFolder(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        fireProcessStarted("Starting create temporary folder");

        final String prefix = getOrCreate(PREFIX_IN, inputParameters).stringValue();
        
        final File file;
        try {
            file = File.createTempFile(prefix, "");            
            file.delete();
            file.mkdirs();

            getOrCreate(FILE_OUT, outputParameters).setValue(file.toURI().toURL());
            
        } catch (IOException ex) {
            fireProcessFailed("Failed creating temp folder", ex);
            return;
        }
        
        fireProcessCompleted("Temporay folder created.");
    }

}
