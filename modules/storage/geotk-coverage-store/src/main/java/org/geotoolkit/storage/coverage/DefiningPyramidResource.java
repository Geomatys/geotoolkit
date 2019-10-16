/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.MultiResolutionResource;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningPyramidResource extends DefiningCoverageResource implements MultiResolutionResource {

    public final List<MultiResolutionModel> models = new ArrayList<>();

    public DefiningPyramidResource(GenericName identifier) {
        super(identifier);
    }

    @Override
    public Collection<? extends MultiResolutionModel> getModels() throws DataStoreException {
        return Collections.unmodifiableList(models);
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }


}
