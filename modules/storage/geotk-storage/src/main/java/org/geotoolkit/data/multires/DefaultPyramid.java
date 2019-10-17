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
package org.geotoolkit.data.multires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default pyramid
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultPyramid extends AbstractPyramid {

    private final List<Mosaic> mosaics = new ArrayList<>();

    public DefaultPyramid(CoordinateReferenceSystem crs) {
        this(null,crs);
    }

    public DefaultPyramid(String id, CoordinateReferenceSystem crs) {
        super(id,crs);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Internal list of pyramids, modify with causion.
     */
    public List<Mosaic> getMosaicsInternal() {
        return mosaics;
    }

    @Override
    public List<Mosaic> getMosaics() {
        return Collections.unmodifiableList(mosaics);
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
