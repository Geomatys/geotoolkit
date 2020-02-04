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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.multires.DefiningPyramid;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningPyramidResource extends DefiningCoverageResource implements MultiResolutionResource {

    public final Map<String,MultiResolutionModel> models = new HashMap<>();

    public DefiningPyramidResource(GenericName identifier) {
        super(identifier);
    }

    @Override
    public Collection<? extends MultiResolutionModel> getModels() throws DataStoreException {
        return Collections.unmodifiableCollection(models.values());
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        if (template instanceof Pyramid) {
            Pyramid p = (Pyramid) template;
            String id = p.getIdentifier();
            if (id == null) {
                //create a unique id
                id = UUID.randomUUID().toString();
            } else if (models.containsKey(id)) {
                //change id to avoid overriding an existing pyramid
                id = UUID.randomUUID().toString();
            }
            DefiningPyramid cp = new DefiningPyramid(id, p.getFormat(), p.getCoordinateReferenceSystem(), new ArrayList<>());
            Pyramids.copyStructure(p, cp);
            models.put(id, cp);
            return cp;
        } else {
            throw new DataStoreException("Unsupported model "+ template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        models.remove(identifier);
    }

}
