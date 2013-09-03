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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author rmarechal
 */
public class MPCoverageStore extends AbstractCoverageStore {

    private final Map<Name, CoverageReference> layers = new HashMap<Name, CoverageReference>();

    /**
     * Dummy parameter descriptor group.
     */
    private static final ParameterDescriptorGroup DESC = new DefaultParameterDescriptorGroup("Unamed", AbstractCoverageStoreFactory.NAMESPACE);

    public MPCoverageStore(){
        super(DESC.createValue());
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        final MPCoverageReference mpcref = new MPCoverageReference(this, name);
        layers.put(name, mpcref);
        return mpcref;
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return null;
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        return layers.keySet();
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        return layers.get(name);
    }

    @Override
    public void dispose() {
    }
    
	@Override
	public CoverageType getType() {
		return CoverageType.PYRAMID;
	}
}
