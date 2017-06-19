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

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.storage.DefaultDataSet;
import org.geotoolkit.storage.Resource;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 *
 * @author remi marechal (Geomatys)
 */
public class MPCoverageStore extends AbstractCoverageStore {

    private final DefaultDataSet rootNode = new DefaultDataSet();

    /**
     * Dummy parameter descriptor group.
     */
    private static final ParameterDescriptorGroup DESC = new ParameterBuilder().addName("Unamed").createGroup(AbstractCoverageStoreFactory.NAMESPACE);

    public MPCoverageStore(){
        super(DESC.createValue());
    }

    @Override
    public Resource getRootResource() {
        return rootNode;
    }

    @Override
    public CoverageResource create(GenericName name) throws DataStoreException {
        final MPCoverageResource mpcref = new MPCoverageResource(this, name);
        rootNode.addResource(mpcref);
        return mpcref;
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public CoverageType getType() {
        return CoverageType.PYRAMID;
    }

}
