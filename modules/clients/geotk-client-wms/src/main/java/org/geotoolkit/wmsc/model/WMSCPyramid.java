/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2018, Geomatys
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
package org.geotoolkit.wmsc.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.multires.AbstractPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.wmsc.xml.v111.TileSet;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCPyramid extends AbstractPyramid {

    private final TileSet tileset;
    private final GeneralDirectPosition upperleft;
    private final WMSCPyramidSet set;
    private final List<Mosaic> mosaics;

    public WMSCPyramid(final WMSCPyramidSet set, final TileSet tileset) throws NoSuchAuthorityCodeException, FactoryException{
        super(CRS.forCode(tileset.getSRS()));
        this.set = set;
        this.tileset = tileset;

        this.upperleft = new GeneralDirectPosition(getCoordinateReferenceSystem());
        this.upperleft.setOrdinate(0, tileset.getBoundingBox().getMinx());
        this.upperleft.setOrdinate(1, tileset.getBoundingBox().getMiny());

        List<Double> ress = tileset.getResolutions();
        if (ress == null) ress = Collections.EMPTY_LIST;

        final Mosaic[] mosaics = new Mosaic[ress.size()];
        for (int i=0; i<mosaics.length; i++){
            mosaics[i] = new WMSCMosaic(this, tileset.getResolutions().get(i));
        }
        this.mosaics = UnmodifiableArrayList.wrap(mosaics);
    }

    public WMSCPyramidSet getPyramidSet() {
        return set;
    }

    public TileSet getTileset() {
        return tileset;
    }

    public DirectPosition getUpperLeftCorner(){
        return upperleft;
    }

    @Override
    public Collection<? extends Mosaic> getMosaics() {
        return mosaics;
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
