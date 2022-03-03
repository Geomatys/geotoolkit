/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridDerivation;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.geometry.AbstractEnvelope;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.referencing.operation.transform.InterpolatedTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.ReducedGridCoverage;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;


/**
 * Abstract renderer for symbolizer which only apply on coverages data.
 * This class will take care to implement the coverage hit method.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module
 */
public abstract class AbstractCoverageSymbolizerRenderer<C extends CachedSymbolizer<? extends Symbolizer>> extends AbstractSymbolizerRenderer<C>{


    public AbstractCoverageSymbolizerRenderer(final SymbolizerRendererService service, final C symbol, final RenderingContext2D context){
        super(service, symbol,context);
    }

    /**
     * Returns expected {@link GridCoverage} from given {@link ProjectedCoverage},
     * adapted to asked {@linkplain #renderingContext internally rendering context} situation.
     *
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @return an expected slice 2D of given {@link ProjectedCoverage}.
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ResampleDescriptor
     * @see ResampleProcess
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected final GridCoverage getObjectiveCoverage(final ProjectedCoverage projectedCoverage)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, renderingContext.getGridGeometry(), false);
    }

    /**
     * Returns expected {@linkplain GridCoverage elevation coverage} from given {@link ProjectedCoverage},
     * adapted to asked {@linkplain #renderingContext internally rendering context} situation.
     *
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @return an expected slice 2D of given {@link ProjectedCoverage}.
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ResampleDescriptor
     * @see ResampleProcess
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected final GridCoverage getObjectiveElevationCoverage(final ProjectedCoverage projectedCoverage)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, renderingContext.getGridGeometry(), true);
    }

    /**
     * Returns expected {@linkplain GridCoverage elevation coverage} or {@linkplain GridCoverage coverage}
     * from given {@link ProjectedCoverage}.
     *
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @param canvasGrid Rendering canvas grid geometry
     * @param isElevation {@code true} if we want elevation coverage, else ({@code false}) for features coverage.
     * @return expected {@linkplain GridCoverage elevation coverage} or {@linkplain GridCoverage coverage}
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected GridCoverage getObjectiveCoverage(final ProjectedCoverage projectedCoverage,
            GridGeometry canvasGrid, final boolean isElevation)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, canvasGrid, isElevation, null);
    }

    /**
     * Returns expected {@linkplain GridCoverage elevation coverage} or {@linkplain GridCoverage coverage}
     * from given {@link ProjectedCoverage}.
     *
     * TODO: add a margin or interpolation parameter. To properly interpolate border on output canvas, we need extra
     *  lines/columns on source image. Their number depends on applied interpolation (bilinear, bicubic, etc.).
     *
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @param canvasGrid Rendering canvas grid geometry
     * @param isElevation {@code true} if we want elevation coverage, else ({@code false}) for features coverage.
     * @param sourceBands coverage source bands to features
     * @return expected {@linkplain GridCoverage elevation coverage} or {@linkplain GridCoverage coverage}
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected GridCoverage getObjectiveCoverage(final ProjectedCoverage projectedCoverage,
            GridGeometry canvasGrid, final boolean isElevation, int[] sourceBands)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        ArgumentChecks.ensureNonNull("projectedCoverage", projectedCoverage);

        final MapLayer coverageLayer = projectedCoverage.getLayer();
        final GridCoverageResource ref = (GridCoverageResource) coverageLayer.getData();

        return getObjectiveCoverage(ref, canvasGrid, isElevation, sourceBands);
    }

    /**
     * Returns expected {@linkplain GridCoverage elevation coverage} or {@linkplain GridCoverage coverage}
     * from given {@link ProjectedCoverage}.
     *
     * TODO: add a margin or interpolation parameter. To properly interpolate border on output canvas, we need extra
     *  lines/columns on source image. Their number depends on applied interpolation (bilinear, bicubic, etc.).
     *
     * @param ref coverage resource
     * @param canvasGrid Rendering canvas grid geometry
     * @param isElevation {@code true} if we want elevation coverage, else ({@code false}) for features coverage.
     * @param sourceBands coverage source bands to features
     * @return expected {@linkplain GridCoverage elevation coverage} or {@linkplain GridCoverage coverage}
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected GridCoverage getObjectiveCoverage(final GridCoverageResource ref,
            GridGeometry canvasGrid, final boolean isElevation, int[] sourceBands)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        ArgumentChecks.ensureNonNull("projectedCoverage", ref);

        final InterpolationCase interpolation = InterpolationCase.BILINEAR;

        final GridGeometry refGG = ref.getGridGeometry();

        //fast envelope intersection in 2D
        if (refGG.isDefined(GridGeometry.ENVELOPE)) {
            Envelope bbox = renderingContext.getCanvasObjectiveBounds2D();
            Envelope refEnv = Envelopes.transform(refGG.getEnvelope(), bbox.getCoordinateReferenceSystem());
            if (!AbstractEnvelope.castOrCopy(bbox).intersects(refEnv, true)) {
                throw new DisjointExtentException("Coverage resource envelope do not intersect canvas");
            }
        }

        final GridGeometry baseGG = trySubGrid(refGG, canvasGrid);
        final GridGeometry slice = extractSlice(baseGG, canvasGrid, computeMargin2D(interpolation), true);

        if (sourceBands != null && sourceBands.length < 1) sourceBands = null;
        GridCoverage coverage = ref.read(slice, sourceBands);

        if (coverage instanceof GridCoverageStack) {
            Logging.getLogger("org.geotoolkit.display2d.primitive").log(Level.WARNING, "Coverage reader return more than one slice.");
        }
        while (coverage instanceof GridCoverageStack) {
            //pick the first slice
            coverage = ((GridCoverageStack) coverage).coverageAtIndex(0);
        }


        //at this point, we want a single slice in 2D
        //we remove all other dimension to simplify any following operation
        if (coverage.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() > 2) {
            coverage = new ReducedGridCoverage(coverage, 0, 1);
        }

        final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(canvasGrid.getCoordinateReferenceSystem());
        if (Utilities.equalsIgnoreMetadata(crs2d, coverage.getCoordinateReferenceSystem())) {
            return coverage;
        } else {
            coverage = prepareCoverageToResampling(coverage, symbol);
            //resample
            final double[] fill = new double[coverage.getSampleDimensions().size()];
            Arrays.fill(fill, Double.NaN);

            /////// HACK FOR 0/360 /////////////////////////////////////////
            GeneralEnvelope ge = new GeneralEnvelope(coverage.getGridGeometry().getEnvelope());
            try {
                GeneralEnvelope cdt = GeneralEnvelope.castOrCopy(Envelopes.transform(coverage.getGridGeometry().getEnvelope(), CommonCRS.WGS84.normalizedGeographic()));
                cdt.normalize();
                if (!cdt.isEmpty()) {
                    ge = cdt;
                }
            } catch (ProjectionException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(), ex);
            }
            GridGeometry resampleGrid = canvasGrid;
            try {
                resampleGrid = resampleGrid.derive()
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(ge)
                    .build()
                    .reduce(0,1);
            } catch (DisjointExtentException ex) {
                //don't log, still continue
            } catch (IllegalGridGeometryException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(), ex);
            }
            resampleGrid = CoverageUtilities.forceLowerToZero(resampleGrid);
            /////// HACK FOR 0/360 /////////////////////////////////////////

            final MathTransform gridToCRS = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER);
            if (isNonLinear(gridToCRS)) {

                final GridGeometry slice2 = extractSlice(refGG, canvasGrid, computeMargin2D(interpolation), false);

                coverage = ref.read(slice2, sourceBands);

                //at this point, we want a single slice in 2D
                //we remove all other dimension to simplify any following operation
                if (coverage.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() > 2) {
                    coverage = new ReducedGridCoverage(coverage, 0, 1);
                }

                return forwardResample(coverage, resampleGrid);
            } else {
                ResampleProcess process = new ResampleProcess(coverage, crs2d, resampleGrid, interpolation, fill);
                //do not extrapolate values, can cause large areas of incorrect values
                process.getInput().parameter(ResampleDescriptor.IN_BORDER_COMPORTEMENT_TYPE.getName().getCode()).setValue(ResampleBorderComportement.FILL_VALUE);
                return process.executeNow();
            }
        }
    }

    private static int[] computeMargin2D(InterpolationCase interpolationCase) {
        if (interpolationCase == null || InterpolationCase.NEIGHBOR.equals(interpolationCase))
            return new int[2];
        int margin;
        switch (interpolationCase) {
            case LANCZOS: margin = 4; break;
            case BILINEAR: margin = 1; break;
            case NEIGHBOR: margin = 0; break;
            default: margin = 2;
        }

        return new int[]{margin, margin};
    }

    private boolean isNonLinear(MathTransform trs) {
        if (trs instanceof InterpolatedTransform) {
            return true;
        } else if (trs instanceof PassThroughTransform) {
            final PassThroughTransform pt = (PassThroughTransform) trs;
            return isNonLinear(pt.getSubTransform());
        } else {
            for (MathTransform t : MathTransforms.getSteps(trs)) {
                if (t != trs && isNonLinear(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static GridCoverage forwardResample(GridCoverage coverage, GridGeometry canvasGrid) throws FactoryException, NoninvertibleTransformException, TransformException {

        //interpolate in geophysic
        coverage = coverage.forConvertedValues(true);

        //reduce the canvas grid to 2D
        if (canvasGrid.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() > 2) {
            canvasGrid = canvasGrid.reduce(0,1);
        }

        //compute forward transform
        final GridGeometry coverageGridGeom = coverage.getGridGeometry();
        final MathTransform transform = coverageGridGeom.createTransformTo(canvasGrid, PixelInCell.CELL_CENTER);

        //create result image
        final RenderedImage img = coverage.render(null);
        final GridExtent canvasExtent = canvasGrid.getExtent();
        final int width = (int) canvasExtent.getSize(0);
        final int height = (int) canvasExtent.getSize(1);
        final BufferedImage result = BufferedImages.createImage(width, height, img);
        final WritablePixelIterator output = WritablePixelIterator.create(result);

        //fill out image with NaN
        final double[] pixel = new double[img.getSampleModel().getNumBands()];
        Arrays.fill(pixel, Double.NaN);
        BufferedImages.setAll(result, pixel);

        //resample
        final Rasterizer canvas = new Rasterizer();
        canvas.width = width;
        canvas.height = height;
        canvas.img = output;
        canvas.pixel = pixel;
        final double[] ptd = new double[8];
        final PixelIterator input = PixelIterator.create(img);
        final Rasterizer.Vertice v1 = new Rasterizer.Vertice();
        final Rasterizer.Vertice v2 = new Rasterizer.Vertice();
        final Rasterizer.Vertice v3 = new Rasterizer.Vertice();
        final Rasterizer.Vertice v4 = new Rasterizer.Vertice();
        while (input.next()) {
            final Point pt = input.getPosition();
            ptd[0] = pt.x-0.5;
            ptd[1] = pt.y-0.5;
            ptd[2] = pt.x+0.5;
            ptd[3] = pt.y-0.5;
            ptd[4] = pt.x+0.5;
            ptd[5] = pt.y+0.5;
            ptd[6] = pt.x-0.5;
            ptd[7] = pt.y+0.5;
            transform.transform(ptd, 0, ptd, 0, 4);
            v1.x = ptd[0]; v1.y = ptd[1];
            v2.x = ptd[2]; v2.y = ptd[3];
            v3.x = ptd[4]; v3.y = ptd[5];
            v4.x = ptd[6]; v4.y = ptd[7];
            input.getPixel(pixel);
            canvas.drawTriangle(v1, v2, v3);
            canvas.drawTriangle(v1, v3, v4);
        }

        return new GridCoverage2D(canvasGrid, coverage.getSampleDimensions(), result);
    }

    private static GridGeometry extractSlice(GridGeometry fullArea, GridGeometry areaOfInterest, final int[] margin, boolean applyResolution)
            throws DataStoreException, TransformException, FactoryException {

        double[] resolution = areaOfInterest.getResolution(true);
        if (fullArea.isDefined(GridGeometry.RESOLUTION)) {
            CoordinateReferenceSystem crsarea = areaOfInterest.getCoordinateReferenceSystem();
            CoordinateReferenceSystem crsdata = fullArea.getCoordinateReferenceSystem();
            if (CRS.isHorizontalCRS(crsarea) && CRS.isHorizontalCRS(crsdata)) {
                //we are dealing with simple 2D rendering, preserve the canvas grid geometry.
                if (margin != null && Arrays.stream(margin).anyMatch(value -> value != 0)) {
                    try {
                        //try to adjust margin
                        //TODO : we should use a GridCoverageResource.subset with a margin value but this isn't implemented yet
                        Envelope env = fullArea.getEnvelope();
                        double[] est = CoverageUtilities.estimateResolution(env, fullArea.getResolution(true), areaOfInterest.getCoordinateReferenceSystem());
                        margin[0] = (int) Math.ceil(margin[0] * (est[0] / resolution[0]));
                        margin[1] = (int) Math.ceil(margin[1] * (est[1] / resolution[1]));
                        areaOfInterest = areaOfInterest.derive().margin(margin).build();
                        // Force rebuilding envelope. Not sure it is really needed however.
                        areaOfInterest = new GridGeometry(
                                areaOfInterest.getExtent(),
                                PixelInCell.CELL_CENTER,
                                areaOfInterest.getGridToCRS(PixelInCell.CELL_CENTER),
                                areaOfInterest.getCoordinateReferenceSystem());
                        return areaOfInterest;
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Cannot compute adapted margin. Artifacts may appear on tile borders");
                        LOGGER.log(Level.FINE, "Details about margin computation failure", e);
                    }
                }
            }
        } else {
            //we have no way to apply margin
            //must wait for GridCoverageResource.subset with a margin
        }

        // HACK : This method cannot manage incomplete grid geometries, so we have to skip
        if (!fullArea.isDefined(GridGeometry.ENVELOPE | GridGeometry.GRID_TO_CRS | GridGeometry.EXTENT)) {
            return areaOfInterest;
        }

        // on displayed area
        Envelope canvasEnv = areaOfInterest.getEnvelope();
        if (!applyResolution) resolution = null;
        /////// HACK FOR 0/360 /////////////////////////////////////////////
        try {
            Map.Entry<Envelope, double[]> entry = solveWrapAround(fullArea, canvasEnv, resolution);
            if (entry != null) {
                canvasEnv = entry.getKey();
                resolution = applyResolution ? entry.getValue() : null;
            }
        } catch (ProjectionException ex) {
            //mays happen when displaying an area partialy outside
            //computation area of coverage crs
            LOGGER.log(Level.INFO, ex.getMessage(), ex);
        }
        /////// HACK FOR 0/360 /////////////////////////////////////////////
        GridGeometry slice = fullArea;
        try {
            GridDerivation derivation = fullArea.derive()
                    .rounding(GridRoundingMode.ENCLOSING);
            if (margin != null && margin.length > 0)
                derivation = derivation.margin(margin);
            slice = derivation
                    .subgrid(canvasEnv, resolution)
                    .sliceByRatio(1, 0, 1)
                    .build();
        } catch (DisjointExtentException ex) {
            throw new DisjointCoverageDomainException(ex.getMessage(), ex);
        } catch (IllegalGridGeometryException ex) {
            throw new DisjointCoverageDomainException(ex.getMessage(), ex);
        }

//        // latest data slice
//        final GridExtent extent = slice.getExtent();
//        final MathTransform gridToCrs = slice.getGridToCRS(PixelInCell.CELL_CENTER);
//        final long[] low = new long[extent.getDimension()];
//        final long[] high = new long[extent.getDimension()];
//        low[0] = extent.getLow(0);
//        low[1] = extent.getLow(1);
//        high[0] = extent.getHigh(0);
//        high[1] = extent.getHigh(1);
//        for (int i=2,n=low.length;i<n;i++) {
//            low[i] = extent.getHigh(i);
//            high[i] = extent.getHigh(i);
//        }
//        //add 3 cell padding for interpolations
//        for (int i=0;i<2;i++) {
//            low[i] = extent.getLow(i) - 3;
//            high[i] = extent.getHigh(i) + 3;
//        }
//        final GridExtent sliceExt = new GridExtent(null, low, high, true);
//        slice = new GridGeometry(sliceExt, PixelInCell.CELL_CENTER, gridToCrs, slice.getCoordinateReferenceSystem());

        return slice;
    }

    /**
     * Pragmatic approach trying to solve intersection of areas with
     * different meridian origin such as -180/+180 to +0/+360.
     *
     * @param resolution, may be changed by this method.
     * @return update area of interest envelope, CRS may have changed and new resolution
     *         or null if unchanged.
     */
    private static Map.Entry<Envelope, double[]> solveWrapAround(final GridGeometry grid, Envelope areaOfInterest, double[] resolution) throws TransformException, FactoryException {

        // unchanged
        if (areaOfInterest == null) return null;

        final CoordinateReferenceSystem gridCrs = grid.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem areaCrs = areaOfInterest.getCoordinateReferenceSystem();

        // unchanged
        if (Utilities.equalsIgnoreMetadata(gridCrs, areaCrs)) return null;

        // find area horizontal crs and it's index.
        List<SingleCRS> areaCrsComponents = CRS.getSingleComponents(areaCrs);
        int areaHorizontalIndex = 0;
        int areaHorizontalOffset = 0;
        SingleCRS areaHorizontalCrs = null;
        for (int n=areaCrsComponents.size(); areaHorizontalIndex < n; areaHorizontalIndex++) {
            SingleCRS areaCmpCrs = areaCrsComponents.get(areaHorizontalIndex);
            if (CRS.isHorizontalCRS(areaCmpCrs)) {
                areaHorizontalCrs = areaCmpCrs;
                break;
            }
            areaHorizontalOffset += areaCmpCrs.getCoordinateSystem().getDimension();
        }

        // if no horizontal part found, return area unchanged
        if (areaHorizontalCrs == null) return null;

        // find counterpart in grid crs
        final List<SingleCRS> gridCrsComponents = CRS.getSingleComponents(gridCrs);
        int offsetGrid = 0;
        SingleCRS gridHorizontalCrs = null;
        for (SingleCRS gridCmpCrs : gridCrsComponents) {
            if (CRS.isHorizontalCRS(gridCmpCrs)) {
                gridHorizontalCrs = gridCmpCrs;
                break;
            }
            offsetGrid += gridCmpCrs.getCoordinateSystem().getDimension();
        }

        // no horizontal counter part found, return area unchanged
        if (gridHorizontalCrs == null) return null;
        // unchanged
        if (Utilities.equalsIgnoreMetadata(areaHorizontalCrs, gridHorizontalCrs)) return null;


        // Extract Horizontal envelopes
        final Envelope gridEnvelope = grid.getEnvelope();
        GeneralEnvelope areaEnv = new GeneralEnvelope(areaHorizontalCrs);
        areaEnv.setRange(0, areaOfInterest.getMinimum(areaHorizontalOffset), areaOfInterest.getMaximum(areaHorizontalOffset));
        areaEnv.setRange(1, areaOfInterest.getMinimum(areaHorizontalOffset+1), areaOfInterest.getMaximum(areaHorizontalOffset+1));
        GeneralEnvelope gridEnv = new GeneralEnvelope(gridHorizontalCrs);
        gridEnv.setRange(0, gridEnvelope.getMinimum(offsetGrid), gridEnvelope.getMaximum(offsetGrid));
        gridEnv.setRange(1, gridEnvelope.getMinimum(offsetGrid+1), gridEnvelope.getMaximum(offsetGrid+1));

        // Convert envelopes to geographic
        GeneralEnvelope areaHorizontalEnv = areaEnv;
        SingleCRS areaGeographicCrs = areaHorizontalCrs;
        if (areaHorizontalCrs instanceof ProjectedCRS) {
            areaGeographicCrs = ((ProjectedCRS) areaHorizontalCrs).getBaseCRS();
            areaEnv = GeneralEnvelope.castOrCopy(Envelopes.transform(areaEnv, areaGeographicCrs));
        }
        SingleCRS gridGeographicCrs = gridHorizontalCrs;
        if (gridHorizontalCrs instanceof ProjectedCRS) {
            gridGeographicCrs = ((ProjectedCRS) gridHorizontalCrs).getBaseCRS();
            gridEnv = GeneralEnvelope.castOrCopy(Envelopes.transform(gridEnv, gridGeographicCrs));
        }

        // intersections are correctly handle in geographic CRS where WrapAround axis are defined.
        CoordinateOperation operation = CRS.findOperation(areaGeographicCrs, gridGeographicCrs, null);
        gridEnv.intersect(Envelopes.transform(operation, areaEnv));

        // Create new compound CRS for area of interest
        areaCrsComponents = new ArrayList(areaCrsComponents); //make list modifiable
        areaCrsComponents.set(areaHorizontalIndex, gridGeographicCrs);
        final CoordinateReferenceSystem newAreaCrs = CRS.compound(areaCrsComponents.toArray(new CoordinateReferenceSystem[0]));

        // Rebuild area of interest in new CRS
        final GeneralEnvelope env = new GeneralEnvelope(newAreaCrs);
        for (int k=0,kn=env.getDimension(); k<kn; k++) {
            env.setRange(k, areaOfInterest.getMinimum(k), areaOfInterest.getMaximum(k));
        }
        env.setRange(areaHorizontalOffset, gridEnv.getMinimum(0), gridEnv.getMaximum(0));
        env.setRange(areaHorizontalOffset+1, gridEnv.getMinimum(1), gridEnv.getMaximum(1));

        if (env.isEmpty()) {
            //the solvewrap arround method is not 100% reliable with special projection
            //in some cases envelopes becomes empty
            return null;
        }

        //compute new resolution
        if (resolution != null && resolution.length != 0) {
            operation = CRS.findOperation(areaHorizontalCrs, gridGeographicCrs, null);

            double[] horizontalResolution = new double[]{
                resolution[areaHorizontalOffset],
                resolution[areaHorizontalOffset+1]};
            final Matrix m = operation.getMathTransform().derivative(areaHorizontalEnv.getMedian());
            horizontalResolution = MatrixSIS.castOrCopy(m).multiply(horizontalResolution);

            resolution = resolution.clone(); //do not modify user parameter
            resolution[areaHorizontalOffset] = Math.abs(horizontalResolution[0]);
            resolution[areaHorizontalOffset+1] = Math.abs(horizontalResolution[1]);
        }

        return new AbstractMap.SimpleImmutableEntry<>(env, resolution);
    }

    /**
     * {@inheritDoc }
     *
     * Prepare coverage for Raster rendering.
     */
    protected GridCoverage prepareCoverageToResampling(final GridCoverage coverageSource, final C symbolizer) {
        return coverageSource;
    }

    /**
     * Useful in IntelliJ: allow to display input image in debugger: Add a new watch calling this method on wanted image.
     * <em>WARNINGS:</em>
     * <ul>
     *     <li>if given image color-model is null, we assume 3 byte/RGB image.</li>
     *     <li>Works only with single-tile images.</li>
     * </ul>
     *
     * @param source The image to display.
     * @param queriedRendering If non-null, we restrict rendering to the rectangle defined to given extent, assuming
     *                         extent low coordinate matches source image (0, 0) coordinate.
     * @return The image directly displayable through debugger.
     */
    public static BufferedImage debug(final RenderedImage source, GridExtent queriedRendering) {
        Raster tile = source.getTile(source.getMinTileX(), source.getMinTileY());
        final int width, height;
        if (queriedRendering == null) {
            tile = tile.createTranslatedChild(0, 0);
            width = tile.getWidth();
            height = tile.getHeight();
        } else {
            width = Math.toIntExact(queriedRendering.getSize(0));
            height = Math.toIntExact(queriedRendering.getSize(1));
            tile = tile.createChild(0, 0, width, height, 0, 0, null);
        }

        final BufferedImage view;
        if (source.getColorModel() == null) {
            view = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            view.getRaster().setRect(tile);
        } else {
            final WritableRaster wr = tile.createCompatibleWritableRaster(0, 0, width, height);
            wr.setRect(tile);
            view = new BufferedImage(source.getColorModel(), wr, false, new Hashtable<>());
        }

        return view;
    }

    /**
     * Try to subset dataset geometry to the area of interest. It allow to short any unnecessary operation
     * as soon as possible in case there's no intersection between the two.
     * For now, the subgrid method have some limitations, so this "optimisation" is not applied on following cases:
     * <ul>
     *     <li>When the grid geometry to derive is incomplete.</li>
     *     <li>When region of interest dimension does not match base one.</li>
     *     <li>When both geometry CRS are incompatible.</li>
     * </ul>
     *
     * The above limitations are expected to be solved in future versions of Apache SIS. For now, we simply catch and
     * log any exception that is potentially mitigated in rendering code. The only fail-first case is when we detect
     * that both geometries do not intersect.
     *
     * @param toDerive The original geometry (of the dataset) to subgrid upon rendering area.
     * @param roi Region Of Interest: The area we want to focus on for the rendering.
     * @return If no error has been raised, The result of {@link GridDerivation#subgrid(GridGeometry) geometry subgrid}.
     * Otherwise, the input {@param toDerive} is returned.
     * @throws DisjointExtentException If given geometries do not intersect.
     */
    private static GridGeometry trySubGrid(final GridGeometry toDerive, final GridGeometry roi) throws DisjointExtentException {
        try {
            return toDerive.derive()
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(roi)
                    .build();
        } catch (DisjointExtentException e) {
            throw e; // Expected: If queried area does not intersect dataset, we let this propagate to short rendering
        } catch (Exception e) {
            LOGGER.log(Level.FINE, e, () -> String.format(
                    "Subgrid has failed and will be ignored.%nBase:%n%s%nRegion of interest:%n%s",
                    toDerive, roi));
        }

        return toDerive;
    }
}
