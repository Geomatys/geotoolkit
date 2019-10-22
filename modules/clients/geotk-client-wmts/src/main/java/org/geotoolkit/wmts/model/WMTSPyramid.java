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
package org.geotoolkit.wmts.model;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.multires.AbstractPyramid;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.wmts.xml.v100.*;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSPyramid extends AbstractPyramid {

    private final WMTSPyramidSet set;
    private final TileMatrixSetLink link;
    private final TileMatrixSet matrixset;
    private CoordinateReferenceSystem crs;
    private final List<Mosaic> mosaics;

    public WMTSPyramid(final WMTSPyramidSet set, final TileMatrixSetLink link){
        super(null);
        this.set = set;
        this.link = link;
        matrixset = set.getCapabilities().getContents().getTileMatrixSetByIdentifier(link.getTileMatrixSet());

        final String crsstr = matrixset.getSupportedCRS();
        try {
            // WMTS is made for display like WMS, so longitude is expected to be on the X axis.
            // Note : this is not written in the spec.
            crs = AbstractCRS.castOrCopy(CRS.forCode(crsstr)).forConvention(AxesConvention.RIGHT_HANDED);
        } catch (NoSuchAuthorityCodeException ex) {
            try {
                crs = CRS.forCode("EPSG:"+crsstr);
            } catch (Exception e) {
                e.addSuppressed(ex);
                Logging.getLogger("org.geotoolkit.wmts.model").log(Level.WARNING, null, e);
            }
        } catch (FactoryException ex) {
            Logging.getLogger("org.geotoolkit.wmts.model").log(Level.WARNING, null, ex);
        }

        final TileMatrixSetLimits limits = link.getTileMatrixSetLimits();

        final Mosaic[] mosaics = new Mosaic[matrixset.getTileMatrix().size()];
        for (int i=0;i<mosaics.length;i++) {
            final TileMatrix matrix = matrixset.getTileMatrix().get(i);
            TileMatrixLimits limit = null;
            if(limits != null){
                for(TileMatrixLimits li : limits.getTileMatrixLimits()){
                    if(li.getTileMatrix().equals(matrix.getIdentifier().getValue())){
                        limit = li;
                        break;
                    }
                }
            }
            mosaics[i] = new WMTSMosaic(this, matrix, limit);
        }
        this.mosaics = UnmodifiableArrayList.wrap(mosaics);
    }

    public TileMatrixSet getMatrixset() {
        return matrixset;
    }

    public WMTSPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
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
