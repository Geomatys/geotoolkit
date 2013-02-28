/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 
 *
 * @author Marechal Remi (Geomatys).
 */
public class MPCoverageReference extends AbstractPyramidalModel {

    private final DefaultPyramidSet pyramidSet;
    private final AtomicLong mosaicID = new AtomicLong(0);
    private List<GridSampleDimension> dimensions;

    public MPCoverageReference(final MPCoverageStore store, final Name name) {
        super(store,name,0);
        this.pyramidSet = new DefaultPyramidSet();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean isWritable() throws DataStoreException {
        return true;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return pyramidSet;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws DataStoreException {
        return dimensions;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void createSampleDimension(List<GridSampleDimension> dimensions, Map<String, Object> analyse) throws DataStoreException {
        this.dimensions = dimensions;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        Pyramid py = new DefaultPyramid(pyramidSet, crs);
        pyramidSet.getPyramids().add(py);
        return py;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        final Collection<Pyramid> coll = pyramidSet.getPyramids();
        final Iterator<Pyramid> it     = coll.iterator();
        while (it.hasNext()) {
            final Pyramid py = it.next();
            if (pyramidId.equalsIgnoreCase(py.getId())) {
                coll.remove(py);
                break;
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
        final Pyramid pyram = findPyramidByID(pyramidId);
        final GridMosaic gm = new MPGridMosaic(mosaicID.incrementAndGet(), pyram, upperleft, gridSize, tilePixelSize, pixelscale);
        ((DefaultPyramid)pyram).getMosaicsInternal().add(gm);
        return gm;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        final Pyramid pyramid = findPyramidByID(pyramidId);
        
        final List<GridMosaic> listGM = pyramid.getMosaics();
        for (int id = 0, len = listGM.size(); id < len; id++) {
            if (listGM.get(id).getId().equalsIgnoreCase(mosaicId)) {
                listGM.remove(id);
                break;
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeTile(String pyramidId, String mosaicId, int tileX, int tileY, RenderedImage image) throws DataStoreException {
        final Pyramid pyram = findPyramidByID(pyramidId);
        
        final List<GridMosaic> listGM = pyram.getMosaics();
        for (int id = 0, len = listGM.size(); id < len; id++) {
            final MPGridMosaic gm = (MPGridMosaic) listGM.get(id);
            if (gm.getId().equalsIgnoreCase(mosaicId)) {
                final Dimension tileSize = gm.getTileSize();
                if (tileSize.width < image.getWidth() || tileSize.height < image.getHeight()) {
                    throw new IllegalArgumentException("image too large from tile dimensions.");
                }
                gm.setTile(tileX, tileY, new MPTileReference(null, image, 0, new Point(tileX, tileY)));
                return;
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteTile(String pyramidId, String mosaicId, int tileX, int tileY) throws DataStoreException {
        final Pyramid pyramid = findPyramidByID(pyramidId);

        for (GridMosaic m : pyramid.getMosaics()) {
            final MPGridMosaic gm = (MPGridMosaic)m;
            if (gm.getId().equalsIgnoreCase(mosaicId)) {
                gm.setTile(tileX,tileY,null);
                return;
            }
        }
    }
    
    /**
     * Find and return Pyramid from pyramid set by its ID.
     * 
     * @param pyramidId Pyramid to find.
     * @return Pyramid from pyramid set by its ID or {@code null} if no {@link Pyramid} is find.
     */
    private Pyramid findPyramidByID(String pyramidId) {
        final Iterator<Pyramid> it = pyramidSet.getPyramids().iterator();
        while (it.hasNext()) {
            final Pyramid py = it.next();
            if (pyramidId.equalsIgnoreCase(py.getId())) {
                return py;
            }
        }
        throw new IllegalArgumentException("Pyramid with id "+pyramidId+" do not exist.");
    }
}
