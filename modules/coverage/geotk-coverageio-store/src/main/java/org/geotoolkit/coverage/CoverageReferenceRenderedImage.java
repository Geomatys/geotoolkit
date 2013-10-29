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
package org.geotoolkit.coverage;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.Vector;
import javax.media.jai.RasterFactory;
import javax.swing.event.EventListenerList;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.filestore.XMLSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.BufferedImageUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * On the fly calculated tiled image for a Coverage reference and grid definition.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CoverageReferenceRenderedImage implements RenderedImage{

    private final CoverageReference ref;
    private final GridMosaic mosaic;

    private final List<GridSampleDimension> sampleDimensions;
    private final ColorModel colorModel;
    private final SampleModel sampleModel;
    private final Envelope dataEnv;

    /** listener support */
    private final EventListenerList listeners = new EventListenerList();

    public CoverageReferenceRenderedImage(CoverageReference ref, GridMosaic mosaic) throws DataStoreException,
            IOException, TransformException {
        this.ref = ref;
        this.mosaic = mosaic;


        final GridCoverageReader reader = ref.acquireReader();
        dataEnv = reader.getGridGeometry(ref.getImageIndex()).getEnvelope();
        ref.recycle(reader);

        final RenderedImage prototype = getTileCoverage(0, 0).getRenderedImage();
        colorModel = prototype.getColorModel();
        sampleModel = prototype.getSampleModel();
        sampleDimensions = ref.acquireReader().getSampleDimensions(ref.getImageIndex());

    }

    /**
     * Tiles are generated on the fly, so we have informations on their generation
     * process but we don't have the tiles themselves.
     *
     * @return empty vector
     */
    @Override
    public Vector<RenderedImage> getSources() {
        return new Vector<RenderedImage>();
    }

    /**
     * A PortrayalRenderedImage does not have any properties
     *
     * @param name
     * @return always Image.UndefinedProperty
     */
    @Override
    public Object getProperty(String name) {
        return Image.UndefinedProperty;
    }

    /**
     * A PortrayalRenderedImage does not have any properties
     *
     * @return always null
     */
    @Override
    public String[] getPropertyNames() {
        return null;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size width * mosaic tile size width.
     */
    @Override
    public int getWidth() {
        return mosaic.getGridSize().width * mosaic.getTileSize().width;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size width * mosaic tile size width.
     */
    @Override
    public int getHeight() {
        return mosaic.getGridSize().height * mosaic.getTileSize().height;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinX() {
        return 0;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinY() {
        return 0;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size width.
     */
    @Override
    public int getNumXTiles() {
        return mosaic.getGridSize().width;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size height.
     */
    @Override
    public int getNumYTiles() {
        return mosaic.getGridSize().height;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinTileX() {
        return 0;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinTileY() {
        return 0;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic tile size width.
     */
    @Override
    public int getTileWidth() {
        return mosaic.getTileSize().width;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic tile size height.
     */
    @Override
    public int getTileHeight() {
        return mosaic.getTileSize().height;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getTileGridXOffset() {
        return 0;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getTileGridYOffset() {
        return 0;
    }

    /**
     * Returns the image's bounds as a <code>Rectangle</code>.
     *
     * <p> The image's bounds are defined by the values returned by
     * <code>getMinX()</code>, <code>getMinY()</code>,
     * <code>getWidth()</code>, and <code>getHeight()</code>.
     * A <code>Rectangle</code> is created based on these four methods.
     *
     * @return Rectangle
     */
    public Rectangle getBounds() {
	return new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
    }

    @Override
    public ColorModel getColorModel() {
        return colorModel;
    }

    @Override
    public SampleModel getSampleModel() {
        return sampleModel;
    }

    public GridCoverage2D getTileCoverage(int idx, int idy) throws CoverageStoreException, TransformException {
        final GridCoverageReadParam rparam = new GridCoverageReadParam();
        Envelope tenv = mosaic.getEnvelope(idx, idy);
        final GeneralEnvelope genv = new GeneralEnvelope(tenv);
        genv.setRange(0, tenv.getMinimum(0) - mosaic.getScale(), tenv.getMaximum(0) + mosaic.getScale());
        genv.setRange(1, tenv.getMinimum(1) - mosaic.getScale(), tenv.getMaximum(1) + mosaic.getScale());
        tenv = CRS.transform(genv, dataEnv.getCoordinateReferenceSystem());
        rparam.setEnvelope(tenv);

        final GridCoverageReader reader = ref.acquireReader();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, rparam);
        ref.recycle(reader);
        return coverage;
    }

    @Override
    public Raster getTile(int idx, int idy) {
        try{
            final GridCoverage2D coverage = getTileCoverage(idx, idy);
            final Envelope coverageEnvelope = coverage.getEnvelope2D();
            final RenderedImage image = coverage.getRenderedImage();

            Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(image), InterpolationCase.NEIGHBOR, 2);

            //create an empty tile
            final Pyramid pyramid = mosaic.getPyramid();
            final int tileWidth = getTileWidth();
            final int tileHeight = getTileHeight();
            final BufferedImage workTile;
            final int nbBand = sampleDimensions.size();
            final double res = mosaic.getScale();
            if(sampleDimensions!=null && !sampleDimensions.isEmpty()){
                workTile = BufferedImageUtilities.createImage(tileWidth, tileHeight, sampleDimensions.size(),
                        XMLSampleDimension.getDataType(sampleDimensions.get(0).getSampleDimensionType()));
            }else{
                workTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
            }

            // define tile translation from bufferedImage min pixel position to mosaic pixel position.
            final int minidx = idx * getTileWidth();
            final int minidy = idy * getTileHeight();
            final double mosULX = mosaic.getUpperLeftCorner().getOrdinate(0);
            final double mosULY = mosaic.getUpperLeftCorner().getOrdinate(1);

            CoordinateReferenceSystem destCrs2D = CRSUtilities.getCRS2D(pyramid.getCoordinateReferenceSystem());
            MathTransform crsDestToCrsCoverage = CRS.findMathTransform(destCrs2D, coverageEnvelope.getCoordinateReferenceSystem());
            MathTransform srcCRSToGrid = ((GridCoverage2D)coverage).getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
            MathTransform crsDestToSrcGrid = MathTransforms.concatenate(crsDestToCrsCoverage, srcCRSToGrid);


            //define destination grid to CRS.
            final AffineTransform2D destImgToCRSDest = new AffineTransform2D(res, 0, 0, -res, mosULX + (minidx + 0.5) * res, mosULY - (minidy + 0.5) * res);
            final MathTransform destImgToCrsCoverage = MathTransforms.concatenate(destImgToCRSDest, crsDestToSrcGrid);

            try {
                final Resample resample = new Resample(destImgToCrsCoverage, workTile, interpolation, new double[nbBand]);
                resample.fillImage();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            fireTileCreated(idx,idy);

            return workTile.getData();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Raster getData() {
        return getData(null);
    }

    @Override
    public WritableRaster getData(Rectangle region) {
        return copyData(region, null);
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        final Rectangle bounds = (raster!=null) ? raster.getBounds() : null;
        return copyData(bounds, raster);
    }

    public WritableRaster copyData(Rectangle region, WritableRaster dstRaster) {
        final Rectangle bounds = getBounds();	// image's bounds

        if (region == null) {
            region = bounds;
        } else if (!region.intersects(bounds)) {
            throw new IllegalArgumentException("Rectangle does not intersect datas.");
        }

        // Get the intersection of the region and the image bounds.
        final Rectangle xsect = (region == bounds) ? region : region.intersection(bounds);

        //create a raster of this size
        if(dstRaster == null){
            SampleModel sampleModel = getSampleModel();
            sampleModel = sampleModel.createCompatibleSampleModel(xsect.width, xsect.height);
            dstRaster = RasterFactory.createWritableRaster(sampleModel, new Point(0, 0));
        }

        //calculate the first and last tiles index we will need
        final int startTileX = xsect.x / getTileWidth();
        final int startTileY = xsect.y / getTileHeight();
        final int endTileX = (xsect.x+xsect.width) / getTileWidth();
        final int endTileY = (xsect.y+xsect.height) / getTileHeight();

        //loop on each tile
        for (int j = startTileY; j <= endTileY; j++) {
            for (int i = startTileX; i <= endTileX; i++) {
                final Raster tile = getTile(i, j);
                dstRaster.setRect(
                        i*getTileWidth(),
                        j*getTileHeight(),
                        tile);
            }
        }

        return dstRaster;
    }

    protected void fireTileCreated(int x, int y){
        for(ProgressListener l : listeners.getListeners(ProgressListener.class)){
            l.tileCreated(x, y);
        }
    }

    public void addProgressListener(ProgressListener listener){
        listeners.add(ProgressListener.class, listener);
    }

    public void removeProgressListener(ProgressListener listener){
        listeners.remove(ProgressListener.class, listener);
    }

    public static interface ProgressListener extends EventListener{

        void tileCreated(int x, int y);

    }

}
