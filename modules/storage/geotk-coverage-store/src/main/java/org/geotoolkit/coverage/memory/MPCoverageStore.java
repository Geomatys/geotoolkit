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
package org.geotoolkit.coverage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableAggregate;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author remi marechal (Geomatys)
 */
public class MPCoverageStore extends AbstractCoverageStore implements WritableAggregate {

    private final List<Resource> resources = Collections.synchronizedList(new ArrayList<>());

    /**
     * Dummy parameter descriptor group.
     */
    private static final ParameterDescriptorGroup DESC = new ParameterBuilder().addName("Unamed").createGroup();

    public MPCoverageStore(){
        super(DESC.createValue());
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public GridCoverageResource add(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof DefiningCoverageResource)) {
            throw new DataStoreException("Unsupported resource "+resource);
        }
        final DefiningCoverageResource cr = (DefiningCoverageResource) resource;
        final GenericName name = cr.getName();

        final MPCoverageResource mpcref = new MPCoverageResource(this, name);
        resources.add(mpcref);
        fireCoverageAdded(name);
        return mpcref;
    }

    @Override
    public void remove(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof GridCoverageResource)) {
            throw new DataStoreException("Unknown resource "+resource);
        }
        final GridCoverageResource cr = (GridCoverageResource) resource;
        final NamedIdentifier name = cr.getIdentifier();

        //TODO
        throw new DataStoreException("Remove operation not supported.");
    }

    @Override
    public DataStoreFactory getProvider() {
        return null;
    }

    @Override
    public void close() {
    }

}
