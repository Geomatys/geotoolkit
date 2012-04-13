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
package org.geotoolkit.coverage.sql;

import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Wrap coverage-sql as a coverage-store.
 * TODO : toporary binding waiting for CoverageStore interface to be revisited
 * and integrated in geotk.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CoverageSQLStoreFactory extends AbstractCoverageStoreFactory{

    @Override
    public String getDescription() {
        return "Coverage-SQL store";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return CoverageDatabase.PARAMETERS;
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        return new CoverageSQLStore(params);
    }

    @Override
    public CoverageStore createNew(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }
    
}
