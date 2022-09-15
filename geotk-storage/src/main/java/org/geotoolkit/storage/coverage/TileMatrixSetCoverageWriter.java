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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CancellationException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.ImageCombiner;
import org.apache.sis.image.Interpolation;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TiledResource;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 */
public class TileMatrixSetCoverageWriter <T extends TiledResource & org.apache.sis.storage.GridCoverageResource> {

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
            image        = coverage.render(coverage.getGridGeometry().getExtent());
            srcCRSToGrid = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
        } catch (NoninvertibleTransformException ex) {
            throw new DataStoreException(ex);
        }

        final Iterator<Runnable> tileQueue;
        try {
            //extract the 2D part of the gridtocrs transform
            final TransformSeparator filter = new TransformSeparator(srcCRSToGrid);
            filter.addSourceDimensionRange(0, 2);
            filter.separate();
            tileQueue = new Ite(reference, requestedEnvelope, image, interpolation, coverage);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }

        final Spliterator<Runnable> spliterator = Spliterators.spliteratorUnknownSize(tileQueue, Spliterator.ORDERED);
        final Stream<Runnable> stream = StreamSupport.stream(spliterator, false);

        stream.parallel().forEach(Runnable::run);
    }

    private static class Ite<T extends TiledResource & org.apache.sis.storage.GridCoverageResource> implements Iterator<Runnable> {

        private final GridCoverage sourceCoverage;
        private final Envelope requestedEnvelope;
        private final RenderedImage sourceImage;
        private final Interpolation interpolation;
        private volatile boolean finished = false;

        //iteration state informations
        private final Iterator<? extends TileMatrixSet> pyramidsIte;
        private Iterator<WritableTileMatrix> tileMatrices;
        private TileMatrixSet currentTileMatrixSet = null;
        private WritableTileMatrix currentTileMatrix = null;
        private CoordinateReferenceSystem destCrs2D;
        private Envelope tileMatrixSetEnvelope;

        //mosaic infos
        private long idminx;
        private long idminy;
        private long idmaxx;
        private long idmaxy;
        private long idx = -1;
        private long idy = -1;

        private Runnable next = null;

        private Ite(T model, Envelope requestedEnvelope, RenderedImage sourceImage, Interpolation interpolation, GridCoverage sourceCoverage) throws DataStoreException{
            this.requestedEnvelope = requestedEnvelope;
            this.sourceImage = sourceImage;
            this.interpolation = interpolation;
            pyramidsIte = model.getTileMatrixSets().iterator();
            this.sourceCoverage = sourceCoverage;
        }

        private boolean calculateMosaicRange(final TileMatrix tileMatrix, Envelope pyramidEnvelope){

            // define tiles indexes from current mosaic which will be changed.
            final GridExtent intersection;
            try {
                intersection = tileMatrix.getTilingScheme().derive()
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(pyramidEnvelope)
                    .getIntersection();
            } catch (DisjointExtentException ex) {
                return false;
            }
            idminx = intersection.getLow(0);
            idminy = intersection.getLow(1);
            idmaxx = intersection.getHigh(0) + 1;
            idmaxy = intersection.getHigh(1) + 1;
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
                if (currentTileMatrixSet == null) {
                    if (pyramidsIte.hasNext()) {
                        currentTileMatrixSet = pyramidsIte.next();
                        try {
                            final GeneralEnvelope tmpFilter = new GeneralEnvelope(
                                    ReferencingUtilities.transform(requestedEnvelope, currentTileMatrixSet.getCoordinateReferenceSystem()));

                            tileMatrices = ((List) TileMatrices.findTileMatrix(currentTileMatrixSet, tmpFilter, false)).iterator();

                            //define CRS and mathTransform from current pyramid to source coverage.
                            destCrs2D = CRS.getHorizontalComponent(currentTileMatrixSet.getCoordinateReferenceSystem());
                            //geographic
                            tileMatrixSetEnvelope = Envelopes.transform(requestedEnvelope, destCrs2D);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                    } else {
                        //we have finish
                        finished = true;
                        return null;
                    }
                }

                if (currentTileMatrix == null) {
                    if (tileMatrices != null && tileMatrices.hasNext()) {
                        //next mosaic
                        currentTileMatrix = tileMatrices.next();
                        idx=-1;
                    } else {
                        //next pyramid
                        tileMatrices = null;
                        currentTileMatrix = null;
                        currentTileMatrixSet = null;
                        continue;
                    }
                }

                if (idx == -1) {
                    calculateMosaicRange(currentTileMatrix, tileMatrixSetEnvelope);
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
                        currentTileMatrix = null;
                        continue loop;
                    }
                    break;
                }

                return new TileUpdater(currentTileMatrix,
                        idx, idy, sourceImage, interpolation, sourceCoverage);
            }
        }

    }

    private static class TileUpdater <T extends TiledResource & org.apache.sis.storage.GridCoverageResource> implements Runnable{

        private final WritableTileMatrix matrix;
        private final long idx;
        private final long idy;
        private final Interpolation interpolation;
        private final RenderedImage baseImage;
        private final GridCoverage coverage;

        private final int[] tileSize;
        private final int tileWidth;
        private final int tileHeight;

        public TileUpdater(WritableTileMatrix matrix, long idx, long idy,
                RenderedImage image, Interpolation interpolation, GridCoverage coverage) {
            this.matrix = matrix;
            this.idx = idx;
            this.idy = idy;
            this.interpolation = interpolation;
            this.tileSize = matrix.getTileSize();
            this.tileWidth = (int) tileSize[0];
            this.tileHeight = (int) tileSize[1];
            this.baseImage = image;
            this.coverage = coverage;
        }

        @Override
        public void run() {
            final BufferedImage currentlyTile;

            try {
                if (matrix.getTileStatus(idx, idy) != TileStatus.MISSING) {
                    try {
                        Tile tile = (Tile) matrix.getTile(idx, idy).orElse(null);
                        //TODO possible null pointer here, what should we do ?
                        GridCoverageResource resource = (GridCoverageResource) tile.getResource();
                        //TODO this will have to be reviewed, it will work with only a few datastores
                        GridCoverage coverage = resource.read(null);
                        currentlyTile = (BufferedImage) coverage.render(coverage.getGridGeometry().getExtent());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    currentlyTile = BufferedImages.createImage(tileWidth, tileHeight, baseImage);
                }

                final GridGeometry tileGridGeom = TileMatrices.getTileGridGeometry2D(matrix, new long[]{idx, idy}, tileSize);

                final RenderedImage coverageImage = coverage.render(coverage.getGridGeometry().getExtent());
                final MathTransform toSource = AggregatedCoverageResource.createTransform(tileGridGeom, currentlyTile, coverage.getGridGeometry(), coverageImage);

                final ImageCombiner ic = new ImageCombiner(currentlyTile);
                ic.setInterpolation(interpolation);
                ic.resample(coverageImage, new Rectangle(currentlyTile.getWidth(), currentlyTile.getHeight()), toSource);
                final RenderedImage tileImage = ic.result();
                matrix.writeTiles(Stream.of(new DefaultImageTile(matrix, tileImage, new long[]{idx, idy})));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
