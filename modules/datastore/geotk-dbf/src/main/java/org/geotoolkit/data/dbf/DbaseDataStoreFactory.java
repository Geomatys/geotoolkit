/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.data.dbf;

import java.io.IOException;
import java.net.URL;

import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * DBF datastore factory. handle only reading actually.
 * Todo : handle feature writer.
 *
 * @author Johann Sorel
 * @module pending
 */
public class DbaseDataStoreFactory extends AbstractFileDataStoreFactory {

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("DBFParameters",
                URLP,NAMESPACE);

    @Override
    public String getDescription() {
        return "Database III files (*.dbf)";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
        
        if(namespace == null){
            namespace = "http://geotoolkit.org";
        }
        
        final String path = url.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        final String name = path.substring(slash, dot);
        try {
            return new DbaseFileDataStore(IOUtilities.toFile(url, null), namespace, name);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public DataStore createNew(final ParameterValueGroup params) throws DataStoreException {
        return create(params);
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {".dbf"};
    }

}
