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
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.ImageCombiner;
import org.apache.sis.image.Interpolation;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.TileMatrices;
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

    public void write(GridCoverage coverage, Envelope requestedEnvelope, Interpolation interpolation) throws DataStoreException, CancellationException {
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

        final Iterator<Runnable> tileQueue;
        try {
            //extract the 2D part of the gridtocrs transform
            final TransformSeparator filter = new TransformSeparator(srcCRSToGrid);
            filter.addSourceDimensionRange(0, 2);
            tileQueue = new Ite(reference, requestedEnvelope, crsCoverage2D, image, filter.separate(), interpolation, coverage);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }

        final Spliterator<Runnable> spliterator = Spliterators.spliteratorUnknownSize(tileQueue, Spliterator.ORDERED);
        final Stream<Runnable> stream = StreamSupport.stream(spliterator, false);

        stream.parallel().forEach(Runnable::run);
    }

    private static class Ite<T extends MultiResolutionResource & org.apache.sis.storage.GridCoverageResource> implements Iterator<Runnable> {

        private final GridCoverage sourceCoverage;
        private final Envelope requestedEnvelope;
        private final CoordinateReferenceSystem crsCoverage2D;
        private final RenderedImage sourceImage;
        private final MathTransform srcCRSToGrid;
        private final Interpolation interpolation;
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
                CoordinateReferenceSystem crsCoverage2D, RenderedImage sourceImage,
                MathTransform srcCRSToGrid, Interpolation interpolation, GridCoverage sourceCoverage) throws DataStoreException{
            this.requestedEnvelope = requestedEnvelope;
            this.crsCoverage2D = crsCoverage2D;
            this.sourceImage = sourceImage;
            this.srcCRSToGrid = srcCRSToGrid;
            this.interpolation = interpolation;
            pyramidsIte = TileMatrices.getTileMatrixSets(model).iterator();
            this.sourceCoverage = sourceCoverage;
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

                return new TileUpdater(currentMosaic,
                        idx, idy, sourceImage, interpolation, sourceCoverage);
            }
        }

    }

    private static class TileUpdater <T extends MultiResolutionResource & org.apache.sis.storage.GridCoverageResource> implements Runnable{

        private final TileMatrix mosaic;
        private final int idx;
        private final int idy;
        private final Interpolation interpolation;
        private final RenderedImage baseImage;
        private final GridCoverage coverage;

        private final int tileWidth;
        private final int tileHeight;

        public TileUpdater(TileMatrix mosaic, int idx, int idy,
                RenderedImage image, Interpolation interpolation, GridCoverage coverage) {
            this.mosaic = mosaic;
            this.idx = idx;
            this.idy = idy;
            this.interpolation = interpolation;
            this.tileWidth = mosaic.getTileSize().width;
            this.tileHeight = mosaic.getTileSize().height;
            this.baseImage = image;
            this.coverage = coverage;
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

            final GridGeometry tileGridGeom = TileMatrices.getTileGridGeometry2D(mosaic, new Point(idx, idy));

            try {
                final MathTransform toSource = AggregatedCoverageResource.createTransform(tileGridGeom, coverage.getGridGeometry());

                final ImageCombiner ic = new ImageCombiner(currentlyTile);
                ic.setInterpolation(interpolation);
                ic.resample(coverage.render(null), new Rectangle(currentlyTile.getWidth(), currentlyTile.getHeight()), toSource);
                final RenderedImage tileImage = ic.result();
                mosaic.writeTiles(Stream.of(new DefaultImageTile(tileImage, new Point(idx, idy))), null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
