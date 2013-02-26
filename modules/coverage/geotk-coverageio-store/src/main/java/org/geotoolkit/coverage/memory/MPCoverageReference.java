/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.AbstractCoverageReference;
import org.geotoolkit.coverage.DefaultPyramid;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.coverage.PyramidalModelReader;
import org.geotoolkit.coverage.PyramidalModelWriter;
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
public class MPCoverageReference extends AbstractCoverageReference implements PyramidalModel {

    private final MPCoverageStore store;
    private final MPPyramidSet pyramidSet;
    private final Name name;
    private long mosaicID;

    public MPCoverageReference(final MPCoverageStore store, final Name name) {
        this.store = store;
        this.name = name;
        this.pyramidSet = new MPPyramidSet(this);
        this.mosaicID = 0;
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public Name getName() {
        return name;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getImageIndex() {
        return 0;
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
    public MPCoverageStore getStore() {
        return store;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public GridCoverageReader createReader() throws DataStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public GridCoverageWriter createWriter() throws DataStoreException {
        return new PyramidalModelWriter(this);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Image getLegend() throws DataStoreException {
        return null;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void createSampleDimension(List<GridSampleDimension> dimensions, Map<String, Object> analyse) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
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
        if (pyram == null) throw new IllegalArgumentException("impossible to create mosaic in a non-existant pyramid"); 
        GridMosaic gm = new MPGridMosaic(this, mosaicID++, pyram, upperleft, gridSize, tilePixelSize, pixelscale);
        ((DefaultPyramid)pyram).getMosaicsInternal().add(gm);
        return gm;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        final Pyramid pyram = findPyramidByID(pyramidId);
        if (pyram == null) return;
        
        final List<GridMosaic> listGM = pyram.getMosaics();
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
    public void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing) throws DataStoreException {
        final Pyramid pyramid = findPyramidByID(pyramidId);
        if (pyramid == null) throw new IllegalArgumentException("pyramid not find");
        for (GridMosaic gm : pyramid.getMosaics()) {
            if (gm.getId().equalsIgnoreCase(mosaicId)) {
                final MPTileReference[][] mpTileReference = ((MPGridMosaic)gm).getTiles();
                
                final ColorModel cm      = image.getColorModel();
                final int dataType       = cm.getColorSpace().getType();
                final int nbBand         = cm.getNumComponents();
                final PixelIterator pix  = PixelIteratorFactory.createRowMajorIterator(image);
                
                final int minx           = image.getMinX();
                final int miny           = image.getMinY();
                final int imgWidth       = image.getWidth();
                final int imgHeight      = image.getHeight();
                final int maxx           = minx + imgWidth;
                final int maxy           = miny + imgHeight;
                
                final Dimension gridSize = gm.getGridSize();
                final int gridWidth      = gridSize.width;
                final int gridHeight     = gridSize.height;
                
                final Dimension tileSize = gm.getTileSize();
                final int tileWidth      = tileSize.width;
                final int tileHeight     = tileSize.height;
                
                if (tileWidth * gridWidth < imgWidth || tileHeight * gridHeight < imgHeight) 
                    throw new IllegalArgumentException("image too large to be stored");
                
                final int nbrTX = (imgWidth  + tileWidth  - 1) / tileWidth;
                final int nbrTY = (imgHeight + tileHeight - 1) / tileHeight;
                
                for (int idy = 0; idy < nbrTY; idy++) {
                    final int imy   = miny + idy * tileHeight;
                    final int imaxy = Math.min(imy+tileHeight, maxy);
                    final int ih    = imaxy - imy;
                    
                    for (int idx = 0; idx < nbrTX; idx++) {
                        
                        final int imx   = minx + idx * tileWidth;
                        final int imaxx = Math.min(imx+tileWidth, maxx);
                        final int iw    = imaxx - imx;
                        
                        final BufferedImage tile  = new BufferedImage(iw, ih, dataType);
                        final PixelIterator copix = PixelIteratorFactory.createRowMajorWriteableIterator(tile, tile);
                        
                        for (int py = imy; py < imaxy; py++) {
                            pix.moveTo(imx, py, 0);
                            for(int px = 0, pmx = iw * nbBand; px < pmx; px++){
                                copix.next();
                                copix.setSampleDouble(pix.getSampleDouble());
                                pix.next();
                            }
                        }
                        mpTileReference[idy][idx] = new MPTileReference(null, tile, 0, new Point(idx, idy));
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeTile(String pyramidId, String mosaicId, int tileX, int tileY, RenderedImage image) throws DataStoreException {
        final Pyramid pyram = findPyramidByID(pyramidId);
        if (pyram == null) return;
        
        final List<GridMosaic> listGM = pyram.getMosaics();
        for (int id = 0, len = listGM.size(); id < len; id++) {
            final MPGridMosaic gm = (MPGridMosaic) listGM.get(id);
            if (gm.getId().equalsIgnoreCase(mosaicId)) {
                final Dimension tileSize = gm.getTileSize();
                if (tileSize.width < image.getWidth() || tileSize.height < image.getHeight()) {
                    throw new IllegalArgumentException("image too large from tile dimensions.");
                }
                gm.getTiles()[tileY][tileX] = new MPTileReference(null, image, 0, new Point(tileX, tileY));
                return;
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteTile(String pyramidId, String mosaicId, int tileX, int tileY) throws DataStoreException {
        final Pyramid pyram = findPyramidByID(pyramidId);
        if (pyram == null) return;
        
        final List<GridMosaic> listGM = pyram.getMosaics();
        for (int id = 0, len = listGM.size(); id < len; id++) {
            MPGridMosaic gm = (MPGridMosaic) listGM.get(id);
            if (gm.getId().equalsIgnoreCase(mosaicId)) {
                gm.getTiles()[tileY][tileX] = null;
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
        return null;
    }
}
