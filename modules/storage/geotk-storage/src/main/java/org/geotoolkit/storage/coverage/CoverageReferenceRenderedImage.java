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
package org.geotoolkit.storage.coverage;

import java.awt.Rectangle;
import java.awt.image.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.PlanarImage;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.storage.multires.TileMatrix;

/**
 * On the fly calculated tiled image for a Coverage reference and grid definition.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CoverageReferenceRenderedImage extends PlanarImage {

    private final GridCoverageResource ref;
    private final TileMatrix mosaic;

    private final ColorModel colorModel;
    private final SampleModel sampleModel;
    private final Envelope dataEnv;

    /** listener support */
    private final EventListenerList listeners = new EventListenerList();

    public CoverageReferenceRenderedImage(GridCoverageResource ref, TileMatrix mosaic) throws DataStoreException,
            IOException, TransformException {
        this.ref = ref;
        this.mosaic = mosaic;

        dataEnv = ref.getGridGeometry().getEnvelope();

        final RenderedImage prototype = getTileCoverage(0, 0).render(null);
        colorModel = prototype.getColorModel();
        sampleModel = prototype.getSampleModel();

        //TODO we should do this here, but the GRIB reader do not return the same data
        //types on the reader and on the readed coverage.
        //TODO wait for the new NETCDF reader
        //sampleDimensions = ref.acquireReader().getSampleDimensions(ref.getImageIndex());

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

    public GridCoverage getTileCoverage(int idx, int idy) throws DataStoreException, TransformException {
        Envelope tenv = TileMatrices.computeTileEnvelope(mosaic, idx, idy);
        final GeneralEnvelope genv = new GeneralEnvelope(tenv);
        genv.setRange(0, tenv.getMinimum(0) - mosaic.getScale(), tenv.getMaximum(0) + mosaic.getScale());
        genv.setRange(1, tenv.getMinimum(1) - mosaic.getScale(), tenv.getMaximum(1) + mosaic.getScale());
        tenv = ReferencingUtilities.transform(genv, dataEnv.getCoordinateReferenceSystem());
        return ref.read(ref.getGridGeometry().derive().subgrid(tenv).build());
    }

    @Override
    public Raster getTile(int idx, int idy) {
        try{
            final GridCoverage coverage = getTileCoverage(idx, idy);
            final CoordinateReferenceSystem coverageCrs2D = CRS.getHorizontalComponent(coverage.getCoordinateReferenceSystem());
            final RenderedImage image = coverage.render(null);
            final SampleDimension[] sampleDimensions = coverage.getSampleDimensions().toArray(new SampleDimension[0]);
            Interpolation interpolation = Interpolation.create(new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(image), InterpolationCase.NEIGHBOR, 2);

            //create an empty tile
            final int tileWidth = getTileWidth();
            final int tileHeight = getTileHeight();
            final BufferedImage workTile;
            final int nbBand = sampleDimensions.length;
            final double[] fillValue = new double[nbBand];
            Arrays.fill(fillValue,Double.NaN);
            final double res = mosaic.getScale();
            if (sampleDimensions.length > 0) {
                workTile = BufferedImages.createImage(tileWidth, tileHeight, sampleDimensions.length, CoverageUtilities.getDataType(coverage));
                for (int i=0; i<nbBand; i++) {
                    final double[] nodata = SampleDimensionUtils.getNoDataValues(sampleDimensions[i].forConvertedValues(true));
                    if (nodata != null && nodata.length > 0) {
                        fillValue[i] = nodata[0];
                    }
                }
            } else {
                workTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
            }

            ImageUtilities.fill(workTile, fillValue[0]);

            // define tile translation from bufferedImage min pixel position to mosaic pixel position.
            final int minidx = idx * getTileWidth();
            final int minidy = idy * getTileHeight();
            final double mosULX = mosaic.getUpperLeftCorner().getOrdinate(0);
            final double mosULY = mosaic.getUpperLeftCorner().getOrdinate(1);

            CoordinateReferenceSystem destCrs2D = CRSUtilities.getCRS2D(mosaic.getUpperLeftCorner().getCoordinateReferenceSystem());
            MathTransform crsDestToCrsCoverage = CRS.findOperation(destCrs2D, coverageCrs2D, null).getMathTransform();
            MathTransform srcCRSToGrid = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
            MathTransform crsDestToSrcGrid = MathTransforms.concatenate(crsDestToCrsCoverage, srcCRSToGrid);


            //define destination grid to CRS.
            final AffineTransform2D destImgToCRSDest = new AffineTransform2D(res, 0, 0, -res, mosULX + (minidx + 0.5) * res, mosULY - (minidy + 0.5) * res);
            final MathTransform destImgToCrsCoverage = MathTransforms.concatenate(destImgToCRSDest, crsDestToSrcGrid);

            try {
                final Resample resample = new Resample(destImgToCrsCoverage, workTile, null, interpolation, fillValue, ResampleBorderComportement.EXTRAPOLATION);
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
