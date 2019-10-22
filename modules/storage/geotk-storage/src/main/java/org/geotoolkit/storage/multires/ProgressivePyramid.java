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
package org.geotoolkit.storage.multires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class ProgressivePyramid implements Pyramid {

    final GeneralProgressiveResource res;
    final Pyramid parent;

    ProgressivePyramid(GeneralProgressiveResource res, Pyramid pyramid) {
        this.res = res;
        this.parent = pyramid;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return parent.getCoordinateReferenceSystem();
    }

    @Override
    public Collection<? extends Mosaic> getMosaics() {
        final Collection<? extends Mosaic> mosaics = parent.getMosaics();
        final List<Mosaic> pmosaics = new ArrayList<>(mosaics.size());
        for (Mosaic m : mosaics) {
            pmosaics.add(new ProgressiveMosaic(this, m));
        }
        return pmosaics;
    }

    @Override
    public double[] getScales() {
        return parent.getScales();
    }

    @Override
    public Collection<? extends Mosaic> getMosaics(int index) {
        final Collection<? extends Mosaic> mosaics = parent.getMosaics(index);
        final List<Mosaic> pmosaics = new ArrayList<>(mosaics.size());
        for (Mosaic m : mosaics) {
            pmosaics.add(new ProgressiveMosaic(this, m));
        }
        return pmosaics;
    }

    @Override
    public Envelope getEnvelope() {
        return parent.getEnvelope();
    }

    @Override
    public String getIdentifier() {
        return parent.getIdentifier();
    }

    @Override
    public String getFormat() {
        return parent.getFormat();
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        return new ProgressiveMosaic(this,  parent.createMosaic(template));
    }

    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        parent.deleteMosaic(mosaicId);
    }

}
