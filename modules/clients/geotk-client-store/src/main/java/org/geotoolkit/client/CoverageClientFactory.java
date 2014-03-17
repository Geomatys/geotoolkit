/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.client;

import java.io.Serializable;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CoverageClientFactory extends CoverageStoreFactory,ClientFactory{

    @Override
    public AbstractCoverageClient open(ParameterValueGroup params) throws DataStoreException;

    @Override
    public AbstractCoverageClient open(Map<String, ? extends Serializable> params) throws DataStoreException;
    
}
