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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.geometry.AbstractEnvelope;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.map.MapLayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.referencing.operation.transform.InterpolatedTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
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
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.util.collection.BackingStoreException;
import org.opengis.referencing.operation.MathTransform;
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
            CoordinateReferenceSystem suggestCommonTarget = CRS.suggestCommonTarget(null, bbox.getCoordinateReferenceSystem(), CRS.getHorizontalComponent(refGG.getCoordinateReferenceSystem()));
            if (suggestCommonTarget != null) {
                try {
                    //Use GridGeometry.getEnvelope(crs) instead of Envelopes.transform(refGG.getEnvelope(), bboxcrs)
                    //This method makes additional clipping and use a more accurate transformation in relation to grid extent
                    Envelope refEnv = refGG.getEnvelope(suggestCommonTarget);
                    Envelope bboxTrs = Envelopes.transform(bbox, suggestCommonTarget);
                    if (!AbstractEnvelope.castOrCopy(bboxTrs).intersects(refEnv, true)) {
                        throw new DisjointExtentException("Coverage resource envelope do not intersect canvas");
                    }
                } catch (TransformException e) {
                    // TODO: maybe remove this catch clause if Apache SIS improves this case management. .
                    // Ignore error. In some cases, Apache SIS fails to reproject envelopes to a common target.
                    // In such case, we try to continue rendering, because maybe data is in region of interest,
                    // but we cannot quickly check it now.
                    LOGGER.log(Level.FINE, "Cannot check if dataset intersects rendering context", e);
                }
            }
        }

        final var margin = computeMargin(interpolation);
        final GridGeometry slice = extractSlice(refGG, canvasGrid, margin);

        if (sourceBands != null && sourceBands.length < 1) sourceBands = null;
        GridCoverage coverage = ref.read(slice, sourceBands);

        if (coverage instanceof GridCoverageStack) {
            Logger.getLogger("org.geotoolkit.display2d.primitive").log(Level.WARNING, "Coverage reader return more than one slice.");
        }
        while (coverage instanceof GridCoverageStack) {
            //pick the first slice
            coverage = ((GridCoverageStack) coverage).coverageAtIndex(0);
        }


        //at this point, we want a single slice in 2D
        //we remove all other dimension to simplify any following operation
        CoordinateReferenceSystem coverageCrs = coverage.getCoordinateReferenceSystem();
        if (coverageCrs.getCoordinateSystem().getDimension() > 2) {
            //TODO index is wrong, it ignores grid to crs transform
            //to be fixed when moved to SIS
            final int idx = CRSUtilities.firstHorizontalAxis(coverageCrs);
            try {
                coverage = new ReducedGridCoverage(coverage, idx, idx+1);
            } catch (BackingStoreException ex) {
                coverage = new GridCoverageProcessor().resample(coverage, CRS.getHorizontalComponent(coverageCrs));
            }
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
                    .selectDimensions(0,1);
            } catch (DisjointExtentException ex) {
                //don't log, still continue
            } catch (IllegalGridGeometryException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(), ex);
            }
            resampleGrid = CoverageUtilities.forceLowerToZero(resampleGrid);
            /////// HACK FOR 0/360 /////////////////////////////////////////

            final MathTransform gridToCRS = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER);
            if (isNonLinear(gridToCRS)) {

                final GridGeometry slice2 = extractSlice(refGG, canvasGrid, margin);

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

    private static int computeMargin(InterpolationCase interpolationCase) {
        return Math.abs(interpolationCase.margin);
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
            canvasGrid = canvasGrid.selectDimensions(0,1);
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

    private static GridGeometry extractSlice(GridGeometry fullArea, GridGeometry areaOfInterest, final int margin)
            throws DataStoreException, TransformException, FactoryException {

        // HACK : This method cannot manage incomplete grid geometries, so we have to skip
        if (!fullArea.isDefined(GridGeometry.ENVELOPE | GridGeometry.GRID_TO_CRS | GridGeometry.EXTENT)) {
            return areaOfInterest;
        }

        final int[] sourceDimensionsMatchingTargetDimensions;
        final int[] sourceRenderDimensions;
        final int[] targetRenderDimensions;
        if (areaOfInterest.getDimension() == 2) {
            targetRenderDimensions = new int[] { 0, 1 };
        } else {
            targetRenderDimensions = areaOfInterest.getExtent().getSubspaceDimensions(2);
        }
        final var sourceDimension = fullArea.getDimension();
        if (sourceDimension == 2) {
            sourceDimensionsMatchingTargetDimensions = sourceRenderDimensions = new int[] { 0, 1 };
        } else {
            MathTransform sourceToTarget;
            try {
                sourceToTarget = fullArea.createTransformTo(areaOfInterest, PixelInCell.CELL_CENTER);
            } catch (TransformException e) {
                final var sourceCRS = fullArea.getCoordinateReferenceSystem();
                final var targetCRS = areaOfInterest.getCoordinateReferenceSystem();
                var sourceCrsToTargetCrs = Objects.requireNonNull(
                        CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform(),
                        "MathTransform from data CRS to objective CRS should not be null"
                );
                sourceToTarget = MathTransforms.concatenate(
                        fullArea.getGridToCRS(PixelInCell.CELL_CENTER),
                        sourceCrsToTargetCrs,
                        areaOfInterest.getGridToCRS(PixelInCell.CELL_CENTER).inverse()
                );
            }
            var dimensionMatching = new TransformSeparator(sourceToTarget);
            // first, check what dimensions in source dataset are selected/filtered by area of interest
            dimensionMatching.separate();
            sourceDimensionsMatchingTargetDimensions = dimensionMatching.getSourceDimensions();
            // Then, identify source dimensions that will strictly used for rendering
            dimensionMatching.clear();
            dimensionMatching.addTargetDimensions(targetRenderDimensions);
            dimensionMatching.separate();
            sourceRenderDimensions = dimensionMatching.getSourceDimensions();
        }

        int[] sourceMargin = new int[sourceDimension];
        for (int sourceIdx : sourceRenderDimensions) sourceMargin[sourceIdx] = margin;

        try {
            GridGeometry slice = fullArea;
            // Arbitrarily select highest values for dimensions not filtered by canvas.
            // This is required so we can display a 2D rendering
            if (sourceDimensionsMatchingTargetDimensions.length < sourceDimension) {
                slice = fullArea.derive()
                                .sliceByRatio(1.0, sourceDimensionsMatchingTargetDimensions)
                                .build();
            }

            // HACK: Workaround waiting for a fix in SIS:
            // The code in the try block should be enough.
            // However, in some cases, Apache SIS fails to analyse GridGeometry given as argument to subgrid method.
            // In such cases, replacing it with a subset of information (envelope and approximate resolution) works.
            // Note that this workaround might cause an approximation in the output geometry.
            try {
                return slice.derive()
                            // Ensure that no edge is omitted on rendering
                            .rounding(GridRoundingMode.ENCLOSING)
                            // Apply margin on source grid, to ensure we've got enough extra-context to apply interpolations.
                            // This is required because readers like NetCDF return strictly requested domain on read.
                            .margin(sourceMargin)
                            // Focus on drawing area
                            .subgrid(areaOfInterest)
                            // Workaround for multidimensional datasets: if region of interest does not pinpoint a single slice,
                            // we arbitrary select highest 2D slice
                            .sliceByRatio(1, sourceRenderDimensions)
                            .build();
            } catch (IllegalGridGeometryException|IndexOutOfBoundsException ex) {
                return slice.derive()
                            // Ensure that no edge is omitted on rendering
                            .rounding(GridRoundingMode.ENCLOSING)
                            // Apply margin on source grid, to ensure we've got enough extra-context to apply interpolations.
                            // This is required because readers like NetCDF return strictly requested domain on read.
                            .margin(sourceMargin)
                            // Focus on drawing area
                            .subgrid(areaOfInterest.getEnvelope(), areaOfInterest.getResolution(true))
                            // Workaround for multidimensional datasets: if region of interest does not pinpoint a single slice,
                            // we arbitrary select highest 2D slice
                            .sliceByRatio(1, sourceRenderDimensions)
                            .build();
            }
        } catch (DisjointExtentException ex) {
            throw new DisjointCoverageDomainException(ex.getMessage(), ex);
        } catch (IllegalGridGeometryException ex) {
            throw new DisjointCoverageDomainException(ex.getMessage(), ex);
        }
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
}
