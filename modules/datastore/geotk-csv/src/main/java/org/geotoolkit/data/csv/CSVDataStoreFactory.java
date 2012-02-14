/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.csv;

import java.io.IOException;
import java.net.URL;

import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * CSV datastore factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVDataStoreFactory extends AbstractFileDataStoreFactory {

    /**
     * Optional - the separator character
     */
    public static final ParameterDescriptor<Character> SEPARATOR =
            new DefaultParameterDescriptor<Character>("separator","spécify the separator",Character.class,';',false);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("CSVParameters",
                URLP,NAMESPACE,SEPARATOR);

    @Override
    public String getDescription() {
        return "CSV files (*.csv)";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
        final char separator = (Character) params.parameter(SEPARATOR.getName().toString()).getValue();
        
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
            return new CSVDataStore(IOUtilities.toFile(url, null), namespace, name, separator);
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
        return new String[] {".csv"};
    }

}
