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

package org.geotoolkit.storage.coverage;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.imageio.ImageReader;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.process.Monitor;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Abstract pyramidal coverage reference.
 * All methods return null values if authorized and writing operations raise exceptions.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public abstract class AbstractPyramidalCoverageResource extends AbstractCoverageResource implements PyramidalCoverageResource {

    protected final int imageIndex;

    public AbstractPyramidalCoverageResource(DataStore store, GenericName name,int imageIndex) {
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
    public DefaultMetadata createMetadata() throws DataStoreException {
        final DefaultMetadata meta = super.createMetadata();

        final DefaultDataIdentification ident = (DefaultDataIdentification) meta.getIdentificationInfo().iterator().next();

        //-- geographic extent
        try {
            final Envelope envelope = getPyramidSet().getEnvelope();
            if(envelope != null) {
                final Envelope geoenv = Envelopes.transform(envelope, CommonCRS.WGS84.normalizedGeographic());
                final DefaultGeographicBoundingBox geo = new DefaultGeographicBoundingBox(
                        geoenv.getMinimum(0), geoenv.getMaximum(0),
                        geoenv.getMinimum(1), geoenv.getMaximum(1));
                final DefaultExtent ex = new DefaultExtent();
                ex.setGeographicElements(Arrays.asList(geo));
                ident.setExtents(Arrays.asList(ex));
            }
        } catch (TransformException ex) {
            //do not break metadata creation for a possible not geographic CRS
        }

        return meta;
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return getPyramidSet().getEnvelope();
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
    public List<GridSampleDimension> getGridSampleDimensions() throws DataStoreException {
        return null;
    }

    @Override
    public void setGridSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
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
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize,
            Dimension dataPixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
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
            final Monitor monitor) throws DataStoreException {

        final Rectangle fullArea = new Rectangle(image.getNumXTiles(), image.getNumYTiles());
        writeTiles(pyramidId, mosaicId, image, fullArea, onlyMissing, monitor);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeTiles(final String pyramidId, final String mosaicId, final RenderedImage image, final Rectangle area,
                           final boolean onlyMissing, final Monitor monitor) throws DataStoreException {
        if(!isWritable()){
            throw new DataStoreException("Pyramid writing not supported.");
        }

        final int offsetX = image.getMinTileX();
        final int offsetY = image.getMinTileY();

        final int startX = (int)area.getMinX();
        final int startY = (int)area.getMinY();
        final int endX = (int)area.getMaxX();
        final int endY = (int)area.getMaxY();

        assert startX >= 0;
        assert startY >= 0;
        assert endX > startX && endX <= image.getNumXTiles();
        assert endY > startY && endY <= image.getNumYTiles();

        final int base = Runtime.getRuntime().availableProcessors();
        final RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        final BlockingQueue queue = new ArrayBlockingQueue(base);

        final ThreadPoolExecutor executor = new ThreadPoolExecutor(Math.max(base, 4), Math.max(base, 4),
                1, TimeUnit.MINUTES, queue,
                new ThreadFactory() {
                    int i=0;
                    @Override
                    public synchronized Thread newThread(Runnable r) {
                        final Thread t = new Thread(r);
                        t.setName("Pyramid writer Pool -"+(i++));
                        return t;
                    }
                }, rejectHandler);

        for(int y=startY; y<endY;y++){
            for(int x=startX;x<endX;x++){
                final int X = x;
                final int Y = y;
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        if (monitor != null && monitor.isCanceled()) {
                            return;
                        }
                        final Raster raster = image.getTile(offsetX+X, offsetY+Y);
                        final RenderedImage img = new BufferedImage(image.getColorModel(),
                                (WritableRaster)raster, image.getColorModel().isAlphaPremultiplied(), null);

                        final int tx = offsetX+X;
                        final int ty = offsetY+Y;

                        try {
                            writeTile(pyramidId, mosaicId, tx, ty, img);
                        } catch (DataStoreException ex) {
                            Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                });
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            throw new DataStoreException("Pyramid writing took to much time : "+ex.getMessage(), ex);
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
    public static GridCoverage2D getTileAsCoverage(PyramidalCoverageResource covRef,
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
    public static GridCoverage2D getTileAsCoverage(PyramidalCoverageResource covRef,
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
                XImageIO.disposeSilently(reader);
            }
        }

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("tile");

        final CoordinateReferenceSystem tileCRS = pyramid.getCoordinateReferenceSystem();
        final MathTransform gridToCrs = AbstractGridMosaic.getTileGridToCRS(mosaic,tile.getPosition());

        final GeneralGridEnvelope ge = new GeneralGridEnvelope(
                new Rectangle(image.getWidth(), image.getHeight()),tileCRS.getCoordinateSystem().getDimension());
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelInCell.CELL_CORNER, gridToCrs, tileCRS, null);
        gcb.setGridGeometry(gridgeo);
        gcb.setRenderedImage(image);

        final List<GridSampleDimension> dimensions = covRef.getGridSampleDimensions();
        if(dimensions!=null){
            gcb.setSampleDimensions(dimensions.toArray(new GridSampleDimension[0]));
        }

        return (GridCoverage2D) gcb.build();
    }


}
