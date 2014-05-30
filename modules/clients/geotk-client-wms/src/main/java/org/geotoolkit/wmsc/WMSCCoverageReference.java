/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.wmsc;

import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.coverage.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.wmsc.model.WMSCPyramidSet;
import org.geotoolkit.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCCoverageReference extends AbstractPyramidalCoverageReference {

    private final PyramidSet set;

    public WMSCCoverageReference(final WebMapClientCached server,
            final Name name) throws CapabilitiesException{
        super(server, name, 0);
        set = new WMSCPyramidSet(server, name.getLocalPart());
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

}
