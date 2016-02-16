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
package org.geotoolkit.processing.referencing.createdb;

import java.util.Collections;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.apache.sis.referencing.factory.sql.EPSGFactory;
import org.apache.sis.util.ArgumentChecks;
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
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);

        final String dbURL      = (String) inputParameters.parameter(CreateDBDescriptor.DBURL.getName().getCode()).getValue();
        final String user       = (String) inputParameters.parameter(CreateDBDescriptor.USER.getName().getCode()).getValue();
        final String password   = (String) inputParameters.parameter(CreateDBDescriptor.PASSWORD.getName().getCode()).getValue();

        final DefaultDataSource ds = new DefaultDataSource(dbURL);
        try {
            final EPSGFactory installer = new EPSGFactory(Collections.singletonMap("dataSource", ds));
            try (Connection c = ds.getConnection(user, password)) {
                installer.install(c);
            }
        } catch (FactoryException | SQLException | IOException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
