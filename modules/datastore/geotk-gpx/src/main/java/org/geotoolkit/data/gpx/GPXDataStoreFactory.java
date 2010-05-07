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

package org.geotoolkit.data.gpx;

import java.io.IOException;
import java.net.URL;

import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GPX datastore factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXDataStoreFactory extends AbstractFileDataStoreFactory {


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("GPXParameters",
                new GeneralParameterDescriptor[]{URLP});

    @Override
    public String getDescription() {
        return "GPX files (*.gpx)";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore createDataStore(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
                
        final String path = url.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        final String name = path.substring(slash, dot);
        try {
            return new GPXDataStore(IOUtilities.toFile(url, null));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public DataStore createNewDataStore(ParameterValueGroup params) throws DataStoreException {
        return createDataStore(params);
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {".gpx"};
    }

}
