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


import java.io.IOException;
import java.nio.file.Path;

import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.io.delete.DeleteDescriptor.*;

/**
 * Delete a file or folder recursivly.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class Delete extends AbstractProcess {

    public Delete(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        fireProcessStarted("Start deletion");

        Object path = inputParameters.getValue(PATH_IN);

        outputParameters.getOrCreate(RESULT_OUT).setValue(Boolean.FALSE);

        try {
            if(!(path instanceof Path)){
                path = IOUtilities.tryToPath(path);
            }

            if(path instanceof Path){
                IOUtilities.deleteRecursively((Path) path);
                outputParameters.getOrCreate(RESULT_OUT).setValue(Boolean.TRUE);
            }else{
                throw new IOException("Path is not a file.");
            }
        } catch (IOException ex) {
            fireProcessFailed("Failed to delete file : "+path, ex);
            return;
        }

        fireProcessCompleted("Deletion done.");
    }

}
