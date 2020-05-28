/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.multires.DefiningPyramid;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.storage.multires.TileFormat;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningMultiResolutionFeatureSet extends DefiningFeatureSet implements MultiResolutionResource {

    public final Map<String,MultiResolutionModel> models = new HashMap<>();
    private TileFormat tileFormat;

    public DefiningMultiResolutionFeatureSet(FeatureType type) {
        super(type, null);
    }

    public void setTileFormat(TileFormat tileFormat) {
        ArgumentChecks.ensureNonNull(null, tileFormat);
        this.tileFormat = tileFormat;
    }

    @Override
    public TileFormat getTileFormat() {
        return tileFormat;
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
