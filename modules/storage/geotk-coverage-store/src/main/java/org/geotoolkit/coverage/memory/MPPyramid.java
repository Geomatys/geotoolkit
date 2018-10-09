/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.multires.AbstractPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MPPyramid extends AbstractPyramid {

    private final MPCoverageResource res;
    private final String id;
    private final List<MPMosaic> mosaics = new ArrayList<>();

    public MPPyramid(MPCoverageResource res, String id, CoordinateReferenceSystem crs) {
        super(id, crs);
        this.res = res;
        this.id = id;
    }

    @Override
    public Collection<? extends Mosaic> getMosaics() {
        return Collections.unmodifiableList(mosaics);
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        final MPMosaic gm = new MPMosaic(res, res.mosaicID.incrementAndGet(),
                this, template.getUpperLeftCorner(), template.getGridSize(), template.getTileSize(), template.getScale());
        mosaics.add(gm);
        return gm;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        for (int id = 0, len = mosaics.size(); id < len; id++) {
            if (mosaics.get(id).getIdentifier().equalsIgnoreCase(mosaicId)) {
                mosaics.remove(id);
                break;
            }
        }
    }

}
