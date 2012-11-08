/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.osmtms;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSCoverageReference implements CoverageReference, PyramidalModel{

    private final OSMTileMapServer server;
    private final Name name;

    OSMTMSCoverageReference(OSMTileMapServer server, Name name){
        this.server = server;
        this.name = name;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public int getImageIndex() {
        return 0;
    }
    
    @Override
    public CoverageStore getStore() {
        return server;
    }

    @Override
    public GridCoverageReader createReader() throws CoverageStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    @Override
    public GridCoverageWriter createWriter() throws DataStoreException {
        throw new DataStoreException("OSM TMS coverages are not writable.");
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return server.getPyramidSet();
    }

    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize, Point2D upperleft, double pixelscale) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing) throws DataStoreException {
        throw new DataStoreException("Model is not writeable.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }


}
