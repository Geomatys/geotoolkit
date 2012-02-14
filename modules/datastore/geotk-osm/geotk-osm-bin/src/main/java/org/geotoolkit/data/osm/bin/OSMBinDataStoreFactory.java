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

package org.geotoolkit.data.osm.bin;

import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Datastore factory for Open Street Map Binary files (*.obm).
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMBinDataStoreFactory extends AbstractFileDataStoreFactory{

    private static final String[] EXTENSIONS = new String[]{".obm"};

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("OSMBinParameters",URLP,NAMESPACE);

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return "Open Street Map binary files (*.obm)";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getFileExtensions() {
        return EXTENSIONS;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore createNew(final ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Creation of OSMbin datastore not supported yet.");
    }

}
