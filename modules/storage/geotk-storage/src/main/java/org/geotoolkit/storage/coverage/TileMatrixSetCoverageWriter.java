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
package org.geotoolkit.storage.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CancellationException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 */
public class TileMatrixSetCoverageWriter <T extends MultiResolutionResource & org.apache.sis.storage.GridCoverageResource> {

    private final T reference;

    /**
     * Build a writer on an existing {@link GridCoverageResource coverage reference}.
     *
     * @param reference A valid {@link GridCoverageResource coverage reference}.
     *                  Should not be {@code null} and an instance of {@link PyramidalModel}.
     * @throws IllegalArgumentException if the given {@link GridCoverageResource coverage reference}
     *                                  is not an instance of {@link PyramidalModel}.
     */
    public TileMatrixSetCoverageWriter(final T reference) {
        this.reference = reference;
    }

    public void write(GridCoverage coverage, Envelope requestedEnvelope, InterpolationCase interpolation) throws DataStoreException, CancellationException {
        if (coverage == null) {
            return;
        }

        if (requestedEnvelope == null) {
            requestedEnvelope = coverage.getGridGeometry().getEnvelope();
        }

        ArgumentChecks.ensureNonNull("requestedEnvelope", requestedEnvelope);
        final CoordinateReferenceSystem crsCoverage2D;
        final CoordinateReferenceSystem envelopeCrs;
        try {
            crsCoverage2D = CRSUtilities.getCRS2D(coverage.getCoordinateReferenceSystem());
            envelopeCrs   = CRSUtilities.getCRS2D(requestedEnvelope.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        if (!Utilities.equalsIgnoreMetadata(crsCoverage2D, envelopeCrs)) {
            try {
                requestedEnvelope = ReferencingUtilities.transform2DCRS(requestedEnvelope, crsCoverage2D);
            } catch (TransformException ex) {
                throw new DataStoreException(ex);
            }
        }

        //source image CRS to grid
        final MathTransform srcCRSToGrid;
        RenderedImage image = null;
        try {
            image        = coverage.render(null);
            srcCRSToGrid = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
        } catch (NoninvertibleTransformException ex) {
            throw new DataStoreException(ex);
        }

        //to fill value table : see resample.
        final int nbBand = image.getSampleModel().getNumBands();

        final Iterator<Runnable> tileQueue;
        try {
            //extract the 2D part of the gridtocrs transform
            final TransformSeparator filter = new TransformSeparator(srcCRSToGrid);
            filter.addSourceDimensionRange(0, 2);
            tileQueue = new Ite(reference, requestedEnvelope, crsCoverage2D, image, nbBand, filter.separate(), interpolation);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }

        final Spliterator<Runnable> spliterator = Spliterators.spliteratorUnknownSize(tileQueue, Spliterator.ORDERED);
        final Stream<Runnable> stream = StreamSupport.stream(spliterator, false);

        stream.parallel().forEach(Runnable::run);
    }

    private static class Ite <T extends MultiResolutionResource & org.apache.sis.storage.GridCoverageResource> implements Iterator<Runnable> {

        private final T model;
        private final Envelope requestedEnvelope;
        private final CoordinateReferenceSystem crsCoverage2D;
        private final RenderedImage sourceImage;
        private final MathTransform srcCRSToGrid;
        private final int nbBand;
        private final InterpolationCase interpolation;
        private volatile boolean finished = false;

        //iteration state informations
        private final Iterator<TileMatrixSet> pyramidsIte;
        private Iterator<TileMatrix> mosaics;
        private TileMatrixSet currentPyramid = null;
        private MathTransform crsDestToSrcGrid;
        private TileMatrix currentMosaic = null;
        private CoordinateReferenceSystem destCrs2D;
        private MathTransform crsDestToCrsCoverage;
        private Envelope pyramidEnvelope;

        //mosaic infos
        private double res;
        private double mosULX;
        private double mosULY;
        private int mosAreaX;
        private int mosAreaY;
        private int mosAreaMaxX;
        private int mosAreaMaxY;
        private int idminx;
        private int idminy;
        private int idmaxx;
        private int idmaxy;
        private int idx = -1;
        private int idy = -1;

        private Runnable next = null;

        private Ite(T model, Envelope requestedEnvelope,
                CoordinateReferenceSystem crsCoverage2D, RenderedImage sourceImage, int nbBand,
                MathTransform srcCRSToGrid, InterpolationCase interpolation) throws DataStoreException{
            this.model = model;
            this.requestedEnvelope = requestedEnvelope;
            this.crsCoverage2D = crsCoverage2D;
            this.sourceImage = sourceImage;
            this.nbBand = nbBand;
            this.srcCRSToGrid = srcCRSToGrid;
            this.interpolation = interpolation;
            pyramidsIte = TileMatrices.getTileMatrixSets(model).iterator();
        }

        private boolean calculateMosaicRange(final TileMatrixSet pyramid, final TileMatrix mosaic, Envelope pyramidEnvelope){

            res = mosaic.getScale();
            final DirectPosition moUpperLeft = mosaic.getUpperLeftCorner();

            // define geographic intersection
            final Envelope mosaicEnv = mosaic.getEnvelope();
            final GeneralEnvelope intersection = new GeneralEnvelope(pyramidEnvelope.getCoordinateReferenceSystem());
            intersection.setRange(0, mosaicEnv.getMinimum(0), mosaicEnv.getMaximum(0));
            intersection.setRange(1, mosaicEnv.getMinimum(1), mosaicEnv.getMaximum(1));
            final int minOrdinate = CoverageUtilities.getMinOrdinate(intersection.getCoordinateReferenceSystem());
            if (!intersection.intersects(pyramidEnvelope, true)) {
                return false;
            }
            intersection.intersect(pyramidEnvelope);

            // mosaic upper left corner coordinates.
            mosULX      = moUpperLeft.getOrdinate(minOrdinate);
            mosULY      = moUpperLeft.getOrdinate(minOrdinate+1);

            //define pixel work area of current mosaic.
            mosAreaX       = (int) Math.round(Math.abs((mosULX - intersection.getMinimum(minOrdinate))) /res);
            mosAreaY       = (int) Math.round(Math.abs((mosULY - intersection.getMaximum(minOrdinate+1))) /res);
            final int mosAreaWidth   = (int)((intersection.getSpan(minOrdinate) / res));

            final int mosAreaHeight  = (int)((intersection.getSpan(minOrdinate+1) / res));
            mosAreaMaxX    = mosAreaX + mosAreaWidth;
            mosAreaMaxY    = mosAreaY + mosAreaHeight;

            // mosaic tiles properties.
            final Dimension tileSize = mosaic.getTileSize();
            final int tileWidth      = tileSize.width;
            final int tileHeight     = tileSize.height;

            // define tiles indexes from current mosaic which will be changed.
            idminx         = mosAreaX / tileWidth;
            idminy         = mosAreaY / tileHeight;
            idmaxx         = (mosAreaMaxX + tileWidth-1) / tileWidth;
            idmaxy         = (mosAreaMaxY + tileHeight - 1) / tileHeight;

            return true;
        }

        @Override
        public Runnable next() {
            findNext();
            Runnable r = next;
            next = null;
            if (r == null) {
                throw new NoSuchElementException();
            }
            return r;
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        private void findNext() {
            if (next != null) {
                return;
            }
            next = poll();
        }

        public synchronized Runnable poll() {
            if (finished) return null;

            //find next tile to build
            loop:
            while (true) {
                if (currentPyramid == null) {
                    if (pyramidsIte.hasNext()) {
                        currentPyramid = pyramidsIte.next();
                        try {
                            final GeneralEnvelope tmpFilter = new GeneralEnvelope(
                                    ReferencingUtilities.transform(requestedEnvelope, currentPyramid.getCoordinateReferenceSystem()));

                            mosaics = CoverageUtilities.findMosaics(currentPyramid, tmpFilter, false).iterator();

                            //define CRS and mathTransform from current pyramid to source coverage.
                            destCrs2D = CRS.getHorizontalComponent(currentPyramid.getCoordinateReferenceSystem());
                            crsDestToCrsCoverage = CRS.findOperation(destCrs2D, crsCoverage2D, null).getMathTransform();
                            //geographic
                            pyramidEnvelope = Envelopes.transform(requestedEnvelope, destCrs2D);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        crsDestToSrcGrid = MathTransforms.concatenate(crsDestToCrsCoverage, srcCRSToGrid);

                    } else {
                        //we have finish
                        finished = true;
                        return null;
                    }
                }

                if (currentMosaic == null) {
                    if (mosaics != null && mosaics.hasNext()) {
                        //next mosaic
                        currentMosaic = mosaics.next();
                        idx=-1;
                    } else {
                        //next pyramid
                        mosaics = null;
                        currentMosaic = null;
                        currentPyramid = null;
                        continue;
                    }
                }

                if (idx == -1) {
                    calculateMosaicRange(currentPyramid, currentMosaic, pyramidEnvelope);
                    idx = idminx-1;
                    idy = idminy;
                }

                while (true) {
                    idx++;
                    if (idx >= idmaxx) {
                        idy++;
                        idx = idminx;
                    }
                    if (idy >= idmaxy) {
                        //finished thie mosaic
                        currentMosaic = null;
                        continue loop;
                    }
                    break;
                }

                return new TileUpdater(model, currentPyramid, currentMosaic,
                        idx, idy,
                        mosAreaX, mosAreaY,
                        mosAreaMaxX, mosAreaMaxY,
                        mosULX, mosULY,
                        crsDestToSrcGrid,
                        sourceImage, nbBand, res,
                        interpolation);
            }
        }

    }

    private static class TileUpdater <T extends MultiResolutionResource & org.apache.sis.storage.GridCoverageResource> implements Runnable{

        private final T pm;
        private final TileMatrixSet pyramid;
        private final TileMatrix mosaic;
        private final int idx;
        private final int idy;
        private final int mosAreaX;
        private final int mosAreaY;
        private final int mosAreaMaxX;
        private final int mosAreaMaxY;
        private final double mosULX;
        private final double mosULY;
        private final Interpolation interpolation;
        private final int nbBand;
        private final double res;
        private final MathTransform crsDestToSrcGrid;
        private final RenderedImage baseImage;

        private final int tileWidth;
        private final int tileHeight;

        public TileUpdater(T pm, TileMatrixSet pyramid, TileMatrix mosaic, int idx, int idy,
                int mosAreaX, int mosAreaY, int mosAreaMaxX, int mosAreaMaxY,
                double mosULX, double mosULY, MathTransform crsDestToSrcGrid,
                RenderedImage image, int nbBand, double res, InterpolationCase interpolation) {
            this.pm = pm;
            this.mosaic = mosaic;
            this.pyramid = pyramid;
            this.idx = idx;
            this.idy = idy;
            this.mosAreaX = mosAreaX;
            this.mosAreaY = mosAreaY;
            this.mosAreaMaxX = mosAreaMaxX;
            this.mosAreaMaxY = mosAreaMaxY;
            this.mosULX = mosULX;
            this.mosULY = mosULY;
            this.interpolation = Interpolation.create(new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(image), interpolation, 2);
            this.nbBand = nbBand;
            this.res = res;
            this.crsDestToSrcGrid = crsDestToSrcGrid;
            this.tileWidth = mosaic.getTileSize().width;
            this.tileHeight = mosaic.getTileSize().height;
            this.baseImage = image;
        }



        @Override
        public void run() {
            final BufferedImage currentlyTile;

            if (!mosaic.isMissing(idx, idy)) {
                try {
                    ImageTile tile = (ImageTile) mosaic.getTile(idx, idy);
                    currentlyTile = (BufferedImage) tile.getImage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                currentlyTile = BufferedImages.createImage(tileWidth, tileHeight, baseImage);
            }

            // define tile translation from bufferedImage min pixel position to mosaic pixel position.
            final int minidx = idx * tileWidth;
            final int minidy = idy * tileHeight;

            //define destination grid to CRS.
            final AffineTransform2D destImgToCRSDest = new AffineTransform2D(res, 0, 0, -res, mosULX + (minidx + 0.5) * res, mosULY - (minidy + 0.5) * res);
            final MathTransform destImgToCrsCoverage = MathTransforms.concatenate(destImgToCRSDest, crsDestToSrcGrid);

            // define currently tile work area.
            final int tminx  = Math.max(mosAreaX, minidx);
            final int tminy  = Math.max(mosAreaY, minidy);
            final int tmaxx  = Math.min(mosAreaMaxX, minidx + tileWidth);
            final int tmaxy  = Math.min(mosAreaMaxY, minidy + tileHeight);
            final Rectangle tileAreaWork = new Rectangle();
            tileAreaWork.setBounds(tminx - minidx, tminy - minidy, tmaxx - tminx, tmaxy - tminy);
            if(tminx==tmaxx || tminy==tmaxy) return;

            try {
                final Resample resample = new Resample(destImgToCrsCoverage, currentlyTile, tileAreaWork, interpolation, new double[nbBand], ResampleBorderComportement.EXTRAPOLATION);
                resample.fillImage();
                mosaic.writeTiles(Stream.of(new DefaultImageTile(currentlyTile, new Point(idx, idy))), null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
