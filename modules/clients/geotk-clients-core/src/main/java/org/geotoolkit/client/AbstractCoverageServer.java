/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageServer extends AbstractServer {

    public AbstractCoverageServer(ParameterValueGroup params) {
        super(params);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // versioning methods : handle nothing by default                         //
    ////////////////////////////////////////////////////////////////////////////
    
    public boolean handleVersioning() {
        return false;
    }

    public VersionControl getVersioning(Name typeName) throws VersioningException {
        throw new VersioningException("Versioning not supported");
    }

    public CoverageReference getCoverageReference(Name name, Version version) throws DataStoreException {
        throw new DataStoreException("Versioning not supported");
    }
    
}
