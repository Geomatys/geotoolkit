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
package org.geotoolkit.process.referencing.createdb;

import java.io.File;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;

/**
 * Create an EPSG database.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CreateDBProcess extends AbstractProcess {


    CreateDBProcess(final ParameterValueGroup input) {
        super(CreateDBDescriptor.INSTANCE,input);
    }

    @Override
    public ParameterValueGroup call() {
        if (inputParameters == null) {
            fireFailEvent(new ProcessEvent(this,
                    "Input parameters not set.",0,
                    new NullPointerException("Input parameters not set.")));
        }

        final String dbURL      = (String) inputParameters.parameter(CreateDBDescriptor.DBURL.getName().getCode()).getValue();
        final String user       = (String) inputParameters.parameter(CreateDBDescriptor.USER.getName().getCode()).getValue();
        final String password   = (String) inputParameters.parameter(CreateDBDescriptor.PASSWORD.getName().getCode()).getValue();
                
        final EpsgInstaller installer = new EpsgInstaller();
        installer.setDatabase(dbURL, user, password);
        try {
            installer.call();
        } catch (FactoryException ex) {
            fireFailEvent(new ProcessEvent(this,"create epsg db",0.5f,ex));
        }
        
        fireEndEvent(new ProcessEvent(this));

        return outputParameters;
    }

}
