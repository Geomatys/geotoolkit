/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.storage;

import java.util.Collections;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.ScaleSortedMap;
import org.geotoolkit.wmts.xml.v100.ContentsType;
import org.geotoolkit.wmts.xml.v100.TileMatrixLimits;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLimits;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLink;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
final class WMTSTileMatrixSet extends AbstractTileMatrixSet {

    private final WMTSTileMatrixSets set;
    private final TileMatrixSetLink link;
    private final org.geotoolkit.wmts.xml.v100.TileMatrixSet matrixset;
    private CoordinateReferenceSystem crs;
    private final SortedMap<GenericName, WMTSTileMatrix> mosaics;

    WMTSTileMatrixSet(final WMTSTileMatrixSets set, final ContentsType contents, final TileMatrixSetLink link) {
        super(null);
        this.set = set;
        this.link = link;
        matrixset = contents.getTileMatrixSetByIdentifier(link.getTileMatrixSet());

        final String crsstr = matrixset.getSupportedCRS();
        try {
            // WMTS is made for display like WMS, so longitude is expected to be on the X axis.
            // Note : this is not written in the spec.
            crs = AbstractCRS.castOrCopy(CRS.forCode(crsstr)).forConvention(AxesConvention.RIGHT_HANDED);
        } catch (NoSuchAuthorityCodeException ex) {
            try {
                crs = CRS.forCode("EPSG:" + crsstr);
            } catch (Exception e) {
                e.addSuppressed(ex);
                Logger.getLogger("org.geotoolkit.ogcwxs.storage").log(Level.WARNING, null, e);
            }
        } catch (FactoryException ex) {
            Logger.getLogger("org.geotoolkit.ogcwxs.storage").log(Level.WARNING, null, ex);
        }

        final TileMatrixSetLimits limits = link.getTileMatrixSetLimits();

        final ScaleSortedMap<WMTSTileMatrix> m = new ScaleSortedMap<>();
        for (final org.geotoolkit.wmts.xml.v100.TileMatrix matrix : matrixset.getTileMatrix()) {
            TileMatrixLimits limit = null;
            if (limits != null) {
                for (TileMatrixLimits li : limits.getTileMatrixLimits()) {
                    if (li.getTileMatrix().equals(matrix.getIdentifier().getValue())) {
                        limit = li;
                        break;
                    }
                }
            }
            m.insertByScale(new WMTSTileMatrix(this, matrix, limit));
        }
        this.mosaics = Collections.unmodifiableSortedMap(m);
    }

    org.geotoolkit.wmts.xml.v100.TileMatrixSet getMatrixset() {
        return matrixset;
    }

    WMTSTileMatrixSets getPyramidSet() {
        return set;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public SortedMap<GenericName, WMTSTileMatrix> getTileMatrices() {
        return mosaics;
    }

}
