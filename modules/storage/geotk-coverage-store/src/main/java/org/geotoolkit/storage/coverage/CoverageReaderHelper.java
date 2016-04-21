/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
import java.awt.Rectangle;
import java.util.Arrays;
import javax.imageio.ImageReader;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.util.ArgumentChecks;

import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;

/**
 * Helper class to help user to define coverage reading parameters adapted to
 * request data from {@link GridCoverageReader}.
 *
 * TODO : do tests.
 *
 * @author Remi Marechal (Geomatys).
 * @version 1.0
 * @since   1.0
 */
public final class CoverageReaderHelper {

    /**
     * GridGeometry of the future reading Coverage at resolution 1, 1.
     */
    private final GridGeometry2D originGridGeom2D;

    /**
     *
     */
    private final GridCoverageReadParam gridCoverageReadParam;

    /**
     * First dimension index of the horizontal part of the multidimensionnal {@link CoordinateReferenceSystem}.
     */
    private final int originFHA;

    /**
     * Destination {@link GridGeometry}.
     * Pointeur to avoid multiple re-computing.
     */
    private GridGeometry2D destinationGridGeometry;

    /**
     * Source read region.
     * Pointeur to avoid multiple re-computing.
     */
    private Rectangle srcImgBoundary;

    /**
     * Destination out put image size.
     * Pointeur to avoid multiple re-computing.
     */
    private Dimension outImgSize;

    /**
     * Build a helper class to request internal {@link ImageReader} from {@link GridCoverageReader}
     * with appropriate read param.
     *
     * @param originGridGeom2D Grid geometry of the coverage which should be read without anysubsampling or offset.
     * It is the Grid geometry of the image coverage at its full resolution.
     * @param gridCoverageReadParam
     */
    public CoverageReaderHelper(final GridGeometry2D originGridGeom2D, final GridCoverageReadParam gridCoverageReadParam) {
        ArgumentChecks.ensureNonNull("originGridGeom2D", originGridGeom2D);
        ArgumentChecks.ensureNonNull("gridCoverageReadParam", gridCoverageReadParam);
        this.originGridGeom2D      = originGridGeom2D;
        this.gridCoverageReadParam = gridCoverageReadParam;
        originFHA                  = CRSUtilities.firstHorizontalAxis(originGridGeom2D.getCoordinateReferenceSystem());
    }

    /**
     * Returns destination {@link Coverage} {@link Envelope} from original {@link GridGeometry} and reader param.
     *
     * @return
     * @throws TransformException
     */
    public GeneralEnvelope getOutIntersectedEnvelope() throws TransformException {

        final Envelope requestEnvelope     = (gridCoverageReadParam.getEnvelope() != null) ? gridCoverageReadParam.getEnvelope() : originGridGeom2D.getEnvelope();
        final int envelopeRequestDimension = requestEnvelope.getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        if (envelopeRequestDimension != originGridGeom2D.getDimension()) {
            throw new IllegalStateException("Dimension mismatch, expected originGrid geometry : "+originGridGeom2D.getDimension()
            +" found envelopeRequestDimension : "+envelopeRequestDimension);
        }

        final Envelope originEnvelope            = originGridGeom2D.getEnvelope();
        final GeneralEnvelope requestTransformed = new GeneralEnvelope(Envelopes.transform(requestEnvelope, originGridGeom2D.getCoordinateReferenceSystem()));

        //-- intersection between request and origin envelope
        requestTransformed.intersect(originEnvelope);
        return requestTransformed;
    }

    /**
     * Returns the source request read region, into source grid image space
     * from origin {@link GridGeometry} and {@link GridCoverageReadParam}.
     *
     * @return
     * @throws org.opengis.referencing.operation.TransformException
     */
    public Rectangle getSrcImgBoundary() throws TransformException {
        if (srcImgBoundary != null)
            return srcImgBoundary;

        Envelope requestTransformed = getOutIntersectedEnvelope();

        //-- convert into 2D
        requestTransformed = Envelopes.transform(requestTransformed, CRSUtilities.getCRS2D(requestTransformed.getCoordinateReferenceSystem()));

        //-- Requested area into origin image resolution
        final GeneralEnvelope destExtent = Envelopes.transform(originGridGeom2D.getGridToCRS2D(PixelOrientation.UPPER_LEFT).inverse(), requestTransformed);

        final int minViewX = (int) Math.floor(destExtent.getMinimum(0));
        final int minViewY = (int) Math.floor(destExtent.getMinimum(1));
        final int maxViewX = (int) Math.ceil(destExtent.getMaximum(0));
        final int maxViewY = (int) Math.ceil(destExtent.getMaximum(1));

        srcImgBoundary = new Rectangle(minViewX, minViewY,
                             maxViewX - minViewX,
                             maxViewY - minViewY);
        return srcImgBoundary;
    }

    /**
     * Returns the destination image size related with requested source read region, into destination grid image space
     * from origin {@link GridGeometry} and {@link GridCoverageReadParam}.
     *
     * @return
     * @throws TransformException
     */
    public Dimension getOutImgSize() throws TransformException {
        if (outImgSize != null)
            return outImgSize;

        final Rectangle srcImgBoundary = getSrcImgBoundary();

        final Envelope requestEnvelope = (gridCoverageReadParam.getEnvelope() != null) ? gridCoverageReadParam.getEnvelope() : originGridGeom2D.getEnvelope();
        final int envelopeRequestDimension = requestEnvelope.getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        if (envelopeRequestDimension != originGridGeom2D.getDimension()) {
            throw new IllegalStateException("Dimension mismatch, expected originGrid geometry : "+originGridGeom2D.getDimension()
            +" found envelopeRequestDimension : "+envelopeRequestDimension);
        }

        //-- get the horizontal part of resolution.
        double[] resolution = gridCoverageReadParam.getResolution();
        if (resolution != null) {
            final int fHA = CRSUtilities.firstHorizontalAxis(requestEnvelope.getCoordinateReferenceSystem());
            resolution = Arrays.copyOfRange(resolution, fHA, fHA+2);
            resolution = ReferencingUtilities.convertResolution(requestEnvelope, resolution, originGridGeom2D.getCoordinateReferenceSystem());
        } else {
            resolution = originGridGeom2D.getResolution();
        }
        assert resolution.length == originGridGeom2D.getDimension() : "Resolution should have same length than origin Grid Geom dimension, "
                + "expected resolution length : "+originGridGeom2D.getDimension()+"  found : "+resolution.length;

        final double[] originResolution = originGridGeom2D.getResolution();

        //-- image boundary into dest gridgeom
        final int outSpanX = (int) (Math.ceil(srcImgBoundary.getWidth()  / (resolution[originFHA]     / originResolution[originFHA])));
        final int outSpanY = (int) (Math.ceil(srcImgBoundary.getHeight() / (resolution[originFHA + 1] / originResolution[originFHA + 1])));
        outImgSize = new Dimension(outSpanX, outSpanY);
        return outImgSize;
    }

    /**
     * Build an appropriate destination {@link GridGeometry} which represente the
     * grid geometry of the result read coverage.
     *
     * @return
     * @throws TransformException
     */
    public GridGeometry2D getDestGridGeometry() throws TransformException {
        if (destinationGridGeometry != null)
            return destinationGridGeometry;

        final GeneralEnvelope destCoverageEnvelope = getOutIntersectedEnvelope();
        final Dimension destGridSize               = getOutImgSize();

        final double scaleX = destCoverageEnvelope.getSpan(originFHA) / destGridSize.width;
        final double scaleY = destCoverageEnvelope.getSpan(originFHA + 1) / destGridSize.height;
        final double transX = destCoverageEnvelope.getMinimum(originFHA);
        final double transY = destCoverageEnvelope.getMaximum(originFHA + 1);

        //-- destination gridToCRS
        final AffineTransform2D gridToCRS2D = new AffineTransform2D(scaleX, 0,
                                                                    0, -scaleY,
                                                                    transX, transY);

        final MathTransform originGridToCRS = originGridGeom2D.getGridToCRS(PixelOrientation.UPPER_LEFT);

        MathTransform destGridToCrs;
        if (originGridGeom2D.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() <= 2) {
            destGridToCrs = gridToCRS2D;
        } else {
            //-- temporary hack in waiting TransformSeparator update / fix
            final boolean hack = true;
           /*
            * Try to re-build appropriate dest gridtocrs from origin grid to CRS.
            */
            try {

                //-- temporary hack in waiting TransformSeparator update / fix
                if (hack)
                    throw new FactoryException("TransformSeparator hack");
                /*
                 * If fha = 0 means 2 compounds parts,  Geographic part for dim 0 and 1, and others dimensions.
                 * If fHA = n means 3 parts, one from dim 0 to n, other which begin n to n+1 (Geo part) and third part the other dimensions.
                 */
                int mtsLength = 1;
                if (Math.abs(0 - originFHA) > 0)
                    mtsLength++;
                if (Math.abs(originFHA + 2 - originGridToCRS.getTargetDimensions()) > 0)
                    mtsLength++;

                final MathTransform[] mts = new MathTransform[mtsLength];
                int mid = 0;

                final TransformSeparator transformSeparator = new TransformSeparator(originGridGeom2D.getGridToCRS());
                if (Math.abs(0 - originFHA) > 0) {
                    transformSeparator.addTargetDimensionRange(0, originFHA);
                    mts[mid++] = transformSeparator.separate();
                }
                transformSeparator.clear();
                mts[mid++] = gridToCRS2D;
                if (Math.abs(originFHA + 2 - originGridToCRS.getTargetDimensions()) > 0) {
                    transformSeparator.addTargetDimensionRange(originFHA + 2, originGridToCRS.getTargetDimensions());
                    mts[mid++] = transformSeparator.separate();
                }

                destGridToCrs = MathTransforms.compound(mts);
            } catch (FactoryException ex) {
                //--log
                /*
                 * Build grid to CRS as we could from envelope coordinates.
                 */
                final int srcDim = originGridToCRS.getSourceDimensions();
                final int dstDim = originGridToCRS.getTargetDimensions();
                final int nbrRow = dstDim + 1;
                final int nbrCol = srcDim + 1;

                final MatrixSIS dgtcrs = Matrices.createDiagonal(nbrRow, nbrCol);
                for (int d = 0; d < originGridToCRS.getTargetDimensions(); d++) {
                    if (d == originFHA) {
                        //-- set the X axis value
                        dgtcrs.setElement(d, d, scaleX);
                        dgtcrs.setElement(d, srcDim, transX);
                    } else if (d == originFHA + 1) {
                        //-- set the Y axis values
                        dgtcrs.setElement(d, d, -scaleY);
                        dgtcrs.setElement(d, srcDim, transY);
                    } else {
                        //-- normaly envelope is a slice on each none geographic dimension part.
                        dgtcrs.setElement(d, d, 0); //-- scale = 0 because slice part of dest envelope
                        dgtcrs.setElement(d, srcDim, destCoverageEnvelope.getMinimum(d));
                    }
                }
                destGridToCrs = MathTransforms.linear(dgtcrs);
            }
        }

        final int[] upper = new int[destGridToCrs.getSourceDimensions()];
        Arrays.fill(upper, 1);
        upper[originFHA] = destGridSize.width;
        upper[originFHA + 1] = destGridSize.height;

        //-- destination grid geometry
        destinationGridGeometry = new GridGeometry2D(new GeneralGridEnvelope(new int[destGridToCrs.getSourceDimensions()], upper, false), PixelInCell.CELL_CORNER,
                destGridToCrs, originGridGeom2D.getCoordinateReferenceSystem(), null);
        return destinationGridGeometry;
    }
}
