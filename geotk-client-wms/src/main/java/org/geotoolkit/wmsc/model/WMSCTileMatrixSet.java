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

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.ScaleSortedMap;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.wmsc.xml.v111.TileSet;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCTileMatrixSet extends AbstractTileMatrixSet {

    private final TileSet tileset;
    private final GeneralDirectPosition upperleft;
    private final WMSCTileMatrixSets set;
    private final SortedMap<GenericName,WMSCTileMatrix> mosaics;

    public WMSCTileMatrixSet(final WMSCTileMatrixSets set, final TileSet tileset) throws NoSuchAuthorityCodeException, FactoryException{
        super(CRS.forCode(tileset.getSRS()));
        this.set = set;
        this.tileset = tileset;

        this.upperleft = new GeneralDirectPosition(getCoordinateReferenceSystem());
        this.upperleft.setCoordinate(0, tileset.getBoundingBox().getMinx());
        this.upperleft.setCoordinate(1, tileset.getBoundingBox().getMiny());

        List<Double> ress = tileset.getResolutions();
        if (ress == null) ress = Collections.EMPTY_LIST;

        ScaleSortedMap<WMSCTileMatrix>m = new ScaleSortedMap<>();
        for (int i=0; i<ress.size(); i++){
            m.insertByScale(new WMSCTileMatrix(this, tileset.getResolutions().get(i)));
        }
        this.mosaics = Collections.unmodifiableSortedMap(m);
    }

    public WMSCTileMatrixSets getPyramidSet() {
        return set;
    }

    public TileSet getTileset() {
        return tileset;
    }

    public DirectPosition getUpperLeftCorner(){
        return upperleft;
    }

    @Override
    public SortedMap<GenericName, ? extends TileMatrix> getTileMatrices() {
        return mosaics;
    }

}
