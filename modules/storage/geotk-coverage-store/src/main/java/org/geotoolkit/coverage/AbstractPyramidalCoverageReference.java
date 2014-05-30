/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.swing.ProgressMonitor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.ImageIOUtilities;
import org.geotoolkit.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

/**
 * Abstract pyramidal coverage reference.
 * All methods return null values if authorized and writing operations raise exceptions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractPyramidalCoverageReference extends AbstractCoverageReference implements PyramidalCoverageReference {

    protected final int imageIndex;
    
    public AbstractPyramidalCoverageReference(CoverageStore store, Name name,int imageIndex) {
        super(store, name);
        this.imageIndex = imageIndex;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }
    
    @Override
    public boolean isWritable() throws CoverageStoreException {
        return false;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        final PyramidalModelReader reader = new PyramidalModelReader();
        reader.setInput(this);
        return reader;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        if(isWritable()){
            return new PyramidalModelWriter(this);
        }else{
            throw new CoverageStoreException("Pyramid is not writable");
        }
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

    @Override
    public ViewType getPackMode() throws DataStoreException {
        return ViewType.RENDERED;
    }

    @Override
    public void setPackMode(ViewType packMode) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions() throws DataStoreException {
        return null;
    }

    @Override
    public void setSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public ColorModel getColorModel() throws DataStoreException {
        return null;
    }

    @Override
    public void setColorModel(ColorModel colorModel) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public SampleModel getSampleModel() throws DataStoreException {
        return null;
    }

    @Override
    public void setSampleModel(SampleModel sampleModel) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize, 
            DirectPosition upperleft, double pixelscale) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeTiles(final String pyramidId, final String mosaicId, final RenderedImage image, final boolean onlyMissing,
            final ProgressMonitor monitor) throws DataStoreException {
        if(!isWritable()){
            throw new DataStoreException("Pyramid writing not supported.");
        }
        
        final int offsetX = image.getMinTileX();
        final int offsetY = image.getMinTileY();

        final RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        final BlockingQueue queue = new ArrayBlockingQueue(Runtime.getRuntime().availableProcessors());
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0, Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES, queue, rejectHandler);

        for(int y=0; y<image.getNumYTiles();y++){
            for(int x=0;x<image.getNumXTiles();x++){
                final Raster raster = image.getTile(offsetX+x, offsetY+y);
                final RenderedImage img = new BufferedImage(image.getColorModel(),
                        (WritableRaster)raster, image.getColorModel().isAlphaPremultiplied(), null);

                final int tx = offsetX+x;
                final int ty = offsetY+y;

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        if (monitor != null && monitor.isCanceled()) {
                            return;
                        }

                        try {
                            writeTile(pyramidId, mosaicId, tx, ty, img);
                        } catch (DataStoreException ex) {
                            Logger.getLogger(AbstractPyramidalCoverageReference.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void writeTile(String pyramidId, String mosaicId, int tileX, int tileY, 
            RenderedImage image) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public void deleteTile(String pyramidId, String mosaicId, 
            int tileX, int tileY) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }
    
    
    /**
     * Get a tile as coverage.
     * @param pyramidId
     * @param mosaicId
     * @param tileX
     * @param tileY
     * @return GridCoverage2D
     */
    public static GridCoverage2D getTileAsCoverage(PyramidalCoverageReference covRef, 
            String pyramidId, String mosaicId, int tileX, int tileY) throws DataStoreException {
        
        TileReference tile = null;
        final Pyramid pyramid = covRef.getPyramidSet().getPyramid(pyramidId);
        if(pyramid==null){
            throw new DataStoreException("Invalid pyramid reference : "+pyramidId);
        }
        
        GridMosaic mosaic = null;
        for(GridMosaic gm : pyramid.getMosaics()){
            if(gm.getId().equals(mosaicId)){
                mosaic = gm;
                tile = gm.getTile(tileX, tileY, null);
            }
        }
        
        if(tile==null){
            throw new DataStoreException("Invalid tile reference : "+pyramidId+" "+mosaicId+" "+tileX+" "+tileY);
        }
        
        return getTileAsCoverage(covRef, pyramidId, mosaicId, tile);
    }
    
    /**
     * Get a tile as coverage.
     * @param covRef
     * @param pyramidId
     * @param mosaicId
     * @param tile
     * @return GridCoverage2D
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static GridCoverage2D getTileAsCoverage(PyramidalCoverageReference covRef, 
            String pyramidId, String mosaicId, TileReference tile) throws DataStoreException {
        
        final Pyramid pyramid = covRef.getPyramidSet().getPyramid(pyramidId);
        if(pyramid==null){
            throw new DataStoreException("Invalid pyramid reference : "+pyramidId);
        }
        
        GridMosaic mosaic = null;
        for(GridMosaic gm : pyramid.getMosaics()){
            if(gm.getId().equals(mosaicId)){
                mosaic = gm;
            }
        }
                
        Object input = tile.getInput();
        RenderedImage image;
        if(input instanceof RenderedImage){
            image = (RenderedImage) input;
        }else{
            ImageReader reader = null;
            try {
                reader = tile.getImageReader();
                image = reader.read(tile.getImageIndex());
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(),ex);
            } finally {
                //dispose reader and substream
                ImageIOUtilities.releaseReader(reader);
            }
        }

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("tile");

        final CoordinateReferenceSystem tileCRS = pyramid.getCoordinateReferenceSystem();
        final MathTransform gridToCrs = AbstractGridMosaic.getTileGridToCRS(mosaic,tile.getPosition());
        
        final GridEnvelope2D ge = new GridEnvelope2D(0, 0, image.getWidth(), image.getHeight());
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelInCell.CELL_CORNER, gridToCrs, tileCRS, null);
        gcb.setGridGeometry(gridgeo);
        gcb.setRenderedImage(image);
        
        final List<GridSampleDimension> dimensions = covRef.getSampleDimensions();
        if(dimensions!=null){
            gcb.setSampleDimensions(dimensions.toArray(new GridSampleDimension[0]));
        }
        
        return (GridCoverage2D) gcb.build();
    }

    
}
