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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 */
public class PyramidalModelWriter extends GridCoverageWriter {
    private final CoverageReference reference;
    private static final double EPSILON_ONE_HOUR = 3.6E6;//hack for tests in attempt to correcting
    /**
     * Build a writer on an existing {@linkplain CoverageReference coverage reference}.
     *
     * @param reference A valid {@linkplain CoverageReference coverage reference}.
     *                  Should not be {@code null} and an instance of {@link PyramidalModel}.
     * @throws IllegalArgumentException if the given {@linkplain CoverageReference coverage reference}
     *                                  is not an instance of {@link PyramidalModel}.
     */
    public PyramidalModelWriter(final CoverageReference reference) {
        if (!(reference instanceof PyramidalModel)) {
            throw new IllegalArgumentException("Given coverage reference should be an instance of PyramidalModel!");
        }
        this.reference = reference;
    }

    /**
     * Write a {@linkplain GridCoverage grid coverage} into the coverage store pointed by the
     * {@link CoverageReference coverage reference}.
     *
     * @param coverage {@link GridCoverage} to write.
     * @param param Writing parameters for the given coverage. Should not be {@code null}, and
     *              should contain a valid envelope.
     * @throws CoverageStoreException if something goes wrong during writing.
     * @throws CancellationException never thrown in this implementation.
     */
    @Override
    public void write(GridCoverage coverage, final GridCoverageWriteParam param) throws CoverageStoreException, CancellationException {
        ArgumentChecks.ensureNonNull("param", param);
        if (coverage == null) {
            return;
        }
        //geographic area where pixel values changes.
        Envelope requestedEnvelope = param.getEnvelope();
        ArgumentChecks.ensureNonNull("requestedEnvelope", requestedEnvelope);
        
        final CoordinateReferenceSystem crsCoverage2D;
        final CoordinateReferenceSystem envelopeCrs;
        try {
            crsCoverage2D = CRSUtilities.getCRS2D(coverage.getCoordinateReferenceSystem());
            envelopeCrs = CRSUtilities.getCRS2D(requestedEnvelope.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex);
        }

        if (!CRS.equalsIgnoreMetadata(crsCoverage2D, envelopeCrs)) {
            try {
                requestedEnvelope = ReferencingUtilities.transform2DCRS(requestedEnvelope, crsCoverage2D);
            } catch (TransformException ex) {
                throw new CoverageStoreException(ex);
            }
        }

        //source image CRS to grid
        final MathTransform srcCRSToGrid;
        RenderedImage image = null;
        try {
            if (coverage instanceof GridCoverage2D) {
                image = ((GridCoverage2D)coverage).getRenderedImage();
                srcCRSToGrid = ((GridCoverage2D)coverage).getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
            } else {
                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setGridCoverage(coverage);
                gcb.setPixelAnchor(PixelInCell.CELL_CENTER);
                image = gcb.getRenderedImage();
                srcCRSToGrid = gcb.getGridToCRS().inverse();
            }
        } catch (NoninvertibleTransformException ex) {
            throw new CoverageStoreException(ex);
        }

        // interpolation neighbourg to find pixel values changed.
        final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(image), InterpolationCase.NEIGHBOR, 2);
        
        //to fill value table : see resample.
        final int nbBand = image.getSampleModel().getNumBands();
        
        final PyramidalModel pm = (PyramidalModel)reference;
        final PyramidSet pyramidSet;
        try {
            pyramidSet = pm.getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }
        
        for (Pyramid pyramid : pyramidSet.getPyramids()) {
            //define CRS and mathTransform from current pyramid to source coverage.
            final CoordinateReferenceSystem destCrs2D;
            final MathTransform crsDestToCrsCoverage;
            final Envelope pyramidEnvelope;
            try {
                destCrs2D = CRSUtilities.getCRS2D(pyramid.getCoordinateReferenceSystem());
                crsDestToCrsCoverage = CRS.findMathTransform(destCrs2D, crsCoverage2D);
                //geographic
                pyramidEnvelope = ReferencingUtilities.transform2DCRS(requestedEnvelope, destCrs2D);
            } catch (Exception ex) {
                throw new CoverageStoreException(ex);
            }

            final MathTransform crsDestToSrcGrid = MathTransforms.concatenate(crsDestToCrsCoverage, srcCRSToGrid);
            noIntersection :
            for (GridMosaic mosaic : pyramid.getMosaics()) {

                final double res = mosaic.getScale();
                final DirectPosition moUpperLeft = mosaic.getUpperLeftCorner();
                
                // define geographic intersection
                final GeneralEnvelope intersection = new GeneralEnvelope(mosaic.getEnvelope());
                final int minOrdinate = CoverageUtilities.getMinOrdinate(intersection.getCoordinateReferenceSystem());
                if (!intersection.intersects(pyramidEnvelope, true)) {
                    // supplementary test caused by cast long value in double within renderer.
                    for (int d = 0; d < moUpperLeft.getDimension(); d++) {
                        if (d != minOrdinate && d != (minOrdinate+1)) {
                            final double val = moUpperLeft.getOrdinate(d);
                            final double pyMin = pyramidEnvelope.getMinimum(d);
                            final double pyMax = pyramidEnvelope.getMaximum(d);
                            if (Math.abs(val-pyMin) > EPSILON_ONE_HOUR && Math.abs(val-pyMax) > EPSILON_ONE_HOUR) {
                                continue noIntersection;
                            }
                        }
                    }
                }
                
                intersection.intersect(pyramidEnvelope);
                
                // mosaic upper left corner coordinates.
                final double mosULX = moUpperLeft.getOrdinate(minOrdinate);
                final double mosULY = moUpperLeft.getOrdinate(minOrdinate+1);
                
                //define pixel work area of current mosaic.
                final int mosAreaX      = (int) Math.round(Math.abs((mosULX - intersection.getMinimum(minOrdinate))) /res);
                final int mosAreaY      = (int) Math.round(Math.abs((mosULY - intersection.getMaximum(minOrdinate+1))) /res);
                final int mosAreaWidth  = (int)(Math.round(intersection.getSpan(minOrdinate) / res));
                final int mosAreaHeight = (int)(Math.round(intersection.getSpan(minOrdinate+1) / res));
                final int mosAreaMaxX   = mosAreaX + mosAreaWidth;
                final int mosAreaMaxY   = mosAreaY + mosAreaHeight;

                // mosaic tiles properties.
                final Dimension tileSize = mosaic.getTileSize();
                final int tileWidth      = tileSize.width;
                final int tileHeight     = tileSize.height;

                // define tiles indexes from current mosaic which will be changed.
                final int idminx = mosAreaX / tileWidth;
                final int idminy = mosAreaY / tileHeight;
                final int idmaxx = (mosAreaMaxX + tileWidth-1) / tileWidth;
                final int idmaxy = (mosAreaMaxY + tileHeight - 1) / tileHeight;

                //define destination grid to CRS.
                final MathTransform gridToCrsDest     = new AffineTransform2D(res, 0, 0, -res, mosULX + 0.5 * res, mosULY - 0.5 * res);
                final MathTransform gridToCrsCoverage = MathTransforms.concatenate(gridToCrsDest, crsDestToSrcGrid);

                // browse selected tile.
                for (int idy = idminy; idy < idmaxy; idy++) {
                    for (int idx = idminx; idx < idmaxx; idx++) {
                        final BufferedImage currentlyTile;
                        try {
                            currentlyTile = mosaic.getTile(idx, idy, null).getImageReader().read(0);
                        } catch (Exception ex) {
                            throw new CoverageStoreException(ex);
                        }

                        // define tile translation from bufferedImage min pixel position to mosaic pixel position.
                        final int minidx = idx * tileWidth;
                        final int minidy = idy * tileHeight;
                        final MathTransform destImgToGrid        = new AffineTransform2D(1, 0, 0, 1, minidx, minidy);
                        final MathTransform destImgToCrsCoverage = MathTransforms.concatenate(destImgToGrid, gridToCrsCoverage);

                        // define currently tile work area.
                        final int tminx = Math.max(mosAreaX, minidx);
                        final int tminy = Math.max(mosAreaY, minidy);
                        final int tmaxx = Math.min(mosAreaMaxX, minidx + tileWidth);
                        final int tmaxy = Math.min(mosAreaMaxY, minidy + tileHeight);
                        final Rectangle tileAreaWork = new Rectangle(tminx-minidx, tminy-minidy, tmaxx-tminx, tmaxy-tminy);

                        try {
                            final Resample resample = new Resample(destImgToCrsCoverage.inverse(), currentlyTile, tileAreaWork, interpolation, new double[nbBand]);
                            resample.fillImage();
                            pm.writeTile(pyramid.getId(), mosaic.getId(), idx, idy, currentlyTile);
                        } catch (Exception ex) {
                            throw new CoverageStoreException(ex);
                        }
                    }
                }
            }
        }
    }
}
