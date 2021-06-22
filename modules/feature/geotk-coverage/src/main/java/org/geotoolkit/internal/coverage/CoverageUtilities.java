/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.OutOfDomainOfValidityException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;


/**
 * A set of utilities methods for the Grid Coverage package. Those methods are not really
 * rigorous; must of them should be seen as temporary implementations.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Simone Giannecchini (Geosolutions)
 * @module
 */
public final class CoverageUtilities extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private CoverageUtilities() {
    }

    /**
     * Retrieves a best guess for the sample value to use for background,
     * inspecting the categories of the provided {@link GridCoverage2D}.
     *
     * @param coverage to use for guessing background values.
     * @return an array of double values to use as a background.
     *
     * @deprecated Replaced by {@link SampleDimension#getNoDataValues()}.
     */
    @Deprecated
    public static double[] getBackgroundValues(final GridCoverage coverage) {
        final List<SampleDimension> dims = coverage.getSampleDimensions();
        final int numBands = dims.size();
        final double[] background = new double[numBands];
        for (int i=0; i<numBands; i++) {
            final SampleDimension band = dims.get(i);
            if (band != null) {
                final int j = i;
                band.getBackground().ifPresent((n) -> background[j] = n.doubleValue());
            }
        }
        return background;
    }


    /**
     * Returns {@code true} if at least one of the specified sample dimensions has a
     * {@linkplain SampleDimension#getSampleToGeophysics sample to geophysics} transform
     * which is not the identity transform.
     *
     * @param sampleDimensions The sample dimensions to check.
     * @return {@code true} if at least one sample dimension has defined a transform.
     */
    public static boolean hasTransform(final SampleDimension[] sampleDimensions) {
        for (int i=sampleDimensions.length; --i>=0;) {
            SampleDimension sd = sampleDimensions[i];
            if (sd != null) {
                sd = sd.forConvertedValues(false);
            }
            MathTransform1D tr = sd.getTransferFunction().orElse(null);
            if (tr!=null && !tr.isIdentity()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the visible band in the specified {@link RenderedImage} or {@link PropertySource}.
     * This method fetch the {@code "GC_VisibleBand"} property. If this property is undefined,
     * then the visible band default to the first one.
     *
     * @param  image The image for which to fetch the visible band, or {@code null}.
     * @return The visible band.
     */
    public static int getVisibleBand(final Object image) {
        Object candidate = null;
        if (image instanceof RenderedImage) {
            candidate = ((RenderedImage) image).getProperty("GC_VisibleBand");
        }
        if (candidate instanceof Integer) {
            return ((Integer) candidate).intValue();
        }
        return 0;
    }

    /**
     * Adapt input envelope to fit urn:ogc:def:wkss:OGC:1.0:GoogleCRS84Quad. Also give well known scales into the interval
     * given in parameter.
     *
     * As specified by WMTS standard v1.0.0 :
     * <p>
     *     [GoogleCRS84Quad] well-known scale set has been defined to allow quadtree pyramids in CRS84. Level
     * 0 allows representing the whole world in a single 256x256 pixels (where the first 64 and
     * last 64 lines of the tile are left blank). The next level represents the whole world in 2x2
     * tiles of 256x256 pixels and so on in powers of 2. Scale denominator is only accurate near
     * the equator.
     * </p>
     *
     * /!\ The well-known scales computed here have been designed for CRS:84 and Mercator projected CRS. Using it for
     * other coordinate reference systems can result in strange results.
     *
     * Note : only horizontal part of input envelope is analysed, so returned envelope will have same values as input one
     * for all additional dimension.
     *
     * @param envelope An envelope to adapt to well known scale quad-tree.
     * @param scaleLimit Minimum and maximum authorized scales. Edge inclusive. Unit must be input envelope horizontal
     *                    axis unit.
     * @return An entry with adapted envelope and its well known scales.
     */
    public static Map.Entry<Envelope, double[]> toWellKnownScale(final Envelope envelope, final NumberRange<Double> scaleLimit)
            throws TransformException, OutOfDomainOfValidityException
    {
        final CoordinateReferenceSystem targetCRS = CRS.getHorizontalComponent(envelope.getCoordinateReferenceSystem());
        if (targetCRS == null) {
            throw new IllegalArgumentException("Input envelope CRS has no defined horizontal component.");
        }
        /*
         * First, we retrieve total envelope of our Quad-tree. We try to use domain of validity of our input envelope
         * CRS. If we cannot, we'll take the world. After that, we'll perform consecutive divisions in order to find
         * minimal Quad-tree cell in which our envelope can be set. It will give us the result envelope. From this
         * envelope, we'll be able to build the final scale list.
         *
         * Note : final envelope can be the fusion of two neighbour Quad-Tree cells.
         */
        final Envelope tmpDomain = CRS.getDomainOfValidity(targetCRS);
        final GeneralEnvelope quadTreeCell;
        if (tmpDomain == null) {
            final GeographicCRS crs84 = CommonCRS.defaultGeographic();
            final Envelope tmpWorld = CRS.getDomainOfValidity(crs84);
            quadTreeCell = new GeneralEnvelope(Envelopes.transform(tmpWorld, targetCRS));
        } else {
            quadTreeCell = new GeneralEnvelope(tmpDomain);
        }

        // We check we can perform divisions on computed domain.
        double min, max;
        for (int i = 0; i < quadTreeCell.getDimension(); i++) {
            min = quadTreeCell.getMinimum(i);
            max = quadTreeCell.getMaximum(i);
            if (Double.isNaN(min) || Double.isInfinite(min) ||
                    Double.isNaN(max) || Double.isInfinite(max)) {
                throw new OutOfDomainOfValidityException("Invalid world bounds " + quadTreeCell);
            }
        }

        GeneralEnvelope targetEnv = new GeneralEnvelope(envelope);

        GeneralEnvelope cellX = quadTreeCell.subEnvelope(0, 1);
        GeneralEnvelope cellY = quadTreeCell.subEnvelope(1, 2);

        final int xAxis = CRSUtilities.firstHorizontalAxis(envelope.getCoordinateReferenceSystem());
        final int yAxis = xAxis + 1;
        final GeneralEnvelope tmpInput = new GeneralEnvelope(envelope);
        GeneralEnvelope inputRangeX = tmpInput.subEnvelope(xAxis, xAxis+1);
        GeneralEnvelope inputRangeY = tmpInput.subEnvelope(yAxis, yAxis+1);

        double midQuadX, midQuadY;
        boolean containX = cellX.contains(inputRangeX);
        boolean containY = cellY.contains(inputRangeY);
        while (containX || containY) {
            // Resize on longitude
            if (containX) {
                targetEnv.setRange(xAxis, cellX.getMinimum(0), cellX.getMaximum(0));
                midQuadX = cellX.getLower(0) + (cellX.getSpan(0) / 2);
                if (inputRangeX.getMinimum(0) < midQuadX) {
                    // west side
                    cellX.setRange(0, cellX.getMinimum(0), midQuadX);
                } else {
                    // east side
                    cellX.setRange(0, midQuadX, cellX.getMaximum(0));
                }

                // Update envelope test
                containX = cellX.contains(inputRangeX);
            }

            // Resize on latitude
            if (containY) {
                targetEnv.setRange(yAxis, cellY.getMinimum(0), cellY.getMaximum(0));
                midQuadY = cellY.getLower(0) + (cellY.getSpan(0) / 2);
                if (inputRangeY.getMinimum(0) < midQuadY) {
                    // south side
                    cellY.setRange(0, cellY.getMinimum(0), midQuadY);
                } else {
                    // north side
                    cellY.setRange(0, midQuadY, cellY.getMaximum(0));
                }

                // Update envelope test
                containY = cellY.contains(inputRangeY);
            }
        }

        final double lowestResolution = Math.max(scaleLimit.getMinDouble(), scaleLimit.getMaxDouble());
        final double highestResolution = Math.min(scaleLimit.getMinDouble(), scaleLimit.getMaxDouble());
        // Go to lowest authorized resolution boundary : A single 256px side tile for output envelope.
        final List<Double> scalesList = new LinkedList<>();
        double minScale = targetEnv.getSpan(xAxis) / 256;
        scalesList.add(minScale);
        while (minScale > highestResolution) {
            minScale /= 2;
            //-- add after to get the last scale to, at least, reach data 1:1 resolution
            scalesList.add(minScale);
        }

        //-- cast
        final double[] scales = new double[scalesList.size()];
        for (int i = 0; i < scalesList.size(); i++) {
            scales[i] = scalesList.get(i);
        }
        return new AbstractMap.SimpleEntry<Envelope, double[]>(targetEnv, scales);
    }

    /**
     * Shift lower coordinates to zero; this is what BufferedImage wants.
     */
    public static GridGeometry forceLowerToZero(final GridGeometry gg) {
        if (gg != null && gg.isDefined(GridGeometry.EXTENT)) {
            final GridExtent extent = gg.getExtent();
            if (!extent.startsAtZero()) {
                CoordinateReferenceSystem crs = null;
                if (gg.isDefined(GridGeometry.CRS)) crs = gg.getCoordinateReferenceSystem();
                final int dimension = extent.getDimension();
                final double[] vector = new double[dimension];
                final long[] high = new long[dimension];
                for (int i=0; i<dimension; i++) {
                    final long low = extent.getLow(i);
                    high[i] = extent.getHigh(i) - low;
                    vector[i] = low;
                }
                MathTransform gridToCRS = gg.getGridToCRS(PixelInCell.CELL_CENTER);
                gridToCRS = MathTransforms.concatenate(MathTransforms.translation(vector), gridToCRS);
                return new GridGeometry(new GridExtent(null, null, high, true), PixelInCell.CELL_CENTER, gridToCRS, crs);
            }
        }
        return gg;
    }

    public static InternationalString getName(GridCoverage coverage) {
        if (coverage instanceof org.geotoolkit.coverage.grid.GridCoverage) {
            return ((org.geotoolkit.coverage.grid.GridCoverage) coverage).getName();
        }
        return new SimpleInternationalString("");
    }

    /**
     * return a part of given image corresponding to input extent.
     * @param baseImage The image to extract a piece of.
     * @param subgrid Rectangle corresponding to the piece of image to extract.
     * @return Part of image corresponding to given extent.
     * @throws java.awt.image.RasterFormatException if given extent does not represent a piece of input image.
     */
    public static BufferedImage subgrid(final BufferedImage baseImage, final GridExtent subgrid) {
        final int[] imgAxes = subgrid.getSubspaceDimensions(2);
        final int subX = Math.toIntExact(subgrid.getLow(imgAxes[0]));
        final int subY = Math.toIntExact(subgrid.getLow(imgAxes[1]));
        final int subWidth = Math.toIntExact(subgrid.getSize(imgAxes[0]));
        final int subHeight = Math.toIntExact(subgrid.getSize(imgAxes[1]));
        return baseImage.getSubimage(subX, subY, subWidth, subHeight);
    }

    /**
     * Compute an estimation of the resolution in another crs.
     *
     * @param env resource where resolution applies
     * @param res resolution in given envelope
     * @param crs wanted resolution crs
     * @return resolution in target crs.
     */
    public static double[] estimateResolution(Envelope env, double[] res, CoordinateReferenceSystem crs)
            throws FactoryException, MismatchedDimensionException, TransformException
    {
        final int dim = env.getDimension();
        final GeneralDirectPosition center = new GeneralDirectPosition(env.getCoordinateReferenceSystem());
        final GeneralDirectPosition vec = new GeneralDirectPosition(env.getCoordinateReferenceSystem());
        for (int i = 0; i < dim; i++) {
            center.setOrdinate(i, env.getMedian(i));
            vec.setOrdinate(i, env.getMedian(i) + res[i]);
        }
        final MathTransform trs = CRS.findOperation(env.getCoordinateReferenceSystem(), crs, null).getMathTransform();
        DirectPosition center2 = trs.transform(center, null);
        DirectPosition vec2 = trs.transform(vec, null);
        double[] res2 = new double[center2.getDimension()];
        for (int i = 0; i < res2.length; i++) {
            res2[i] = Math.abs(vec2.getOrdinate(i) - center2.getOrdinate(i));
        }
        return res2;
    }

    /**
     * Render coverage and verify values are in the range of sample Dimensions.
     */
    public static void validateCoverage(GridCoverage coverage) {
        final SampleDimension[] sampleDimensions = coverage.getSampleDimensions().toArray(new SampleDimension[0]);
        final RenderedImage image = coverage.render(null);
        final PixelIterator ite = PixelIterator.create(image);
        while (ite.next()) {
            for (int i=0;i<sampleDimensions.length;i++) {
                checkSample(ite.getSampleDouble(i), sampleDimensions[i]);
            }
        }
    }

    private static void checkSample(double value, SampleDimension sd) {
        if (sd.getCategories().isEmpty()) return;

        for (Category cat : sd.getCategories()) {
            NumberRange range = cat.getSampleRange();
            if (range.containsAny(value)) {
                return;
            }
        }
        throw new RuntimeException("Sample "+value+" not found.");
    }

    /**
     * Get or create transform for 2D image to coverage ND CRS.
     *
     * @param gridGeometry original coverage grid geometry
     * @param readExtent request extent at reading time
     * @param image read image
     * @return Image to CRS transform, from image pixel coordinate 2D to Coverage ND CRS.
     */
    public static MathTransform getImageToCRS(GridGeometry gridGeometry, GridExtent readExtent, RenderedImage image, PixelInCell pixelInCell) {
        if (readExtent == null) {
            readExtent = gridGeometry.getExtent();
        }
        // grid geometry to CRS transform
        final MathTransform gridToCRS = gridGeometry.getGridToCRS(pixelInCell);

        // grid extent corner transform
        final int srcDim = 2;
        final int tgtDim = readExtent.getDimension();

        // numcol = 3, we want a 2D image pixel coordinates as input
        final Matrix m = Matrices.createZero(tgtDim + 1, srcDim + 1);

        // set translation to grid extent corner
        for (int j = 0; j < tgtDim; j++) {
           m.setElement(j, srcDim, readExtent.getLow(j));
        }

        // last line scale, 1 in the last column to have an affine-like transform
        m.setElement(tgtDim, srcDim, 1);

        // set scale to 1 for horizontal part which correspond to the image X,Y
        // other lines will result in constant values
        final int[] s = readExtent.getSubspaceDimensions(srcDim);
        for (int i = 0; i < s.length; i++) {
            m.setElement(s[i], i, 1);
        }

        return MathTransforms.concatenate(MathTransforms.linear(m), gridToCRS);
    }

}
