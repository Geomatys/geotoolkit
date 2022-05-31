/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.image.Interpolation;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.geotoolkit.storage.memory.InMemoryTiledGridCoverageResource;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.DefaultTileMatrixSet;
import org.geotoolkit.storage.multires.EmptyTile;
import org.geotoolkit.storage.multires.TileInError;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.util.StringUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;

/**
 * Tile generator splitting a coverage in tiles.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageTileGenerator extends AbstractTileGenerator {

    private final GridCoverageResource resource;
    private final double[] empty;
    private final Future<Object> baseRendering;

    private InterpolationCase interpolation = InterpolationCase.NEIGHBOR;
    private double[] fillValues;
    private boolean coverageIsHomogeneous = true;
    private boolean skipExistingTiles = false;
    private boolean generateFromSource = false;
    private boolean alwaysGenerateUsingLowerLevel = false;

    public CoverageTileGenerator(GridCoverageResource resource) throws DataStoreException {
        ArgumentChecks.ensureNonNull("resource", resource);
        this.resource = resource;
        final List<SampleDimension> sampleDimensions = resource.getSampleDimensions();
        if (sampleDimensions == null || sampleDimensions.isEmpty()) {
            throw new DataStoreException("Base resource sample dimensions are undefined");
        }
        empty = new double[sampleDimensions.size()];
        for (int i = 0; i < empty.length; i++) {
            empty[i] = getEmptyValue(sampleDimensions.get(i));
        }

        baseRendering = ForkJoinPool.commonPool().submit(() -> createBaseRendering(resource));
    }

    /**
     * Retrns the resource used to generate tiles.
     *
     * @return resource, not null
     */
    public GridCoverageResource getOrigin() {
        return resource;
    }

    public void setInterpolation(InterpolationCase interpolation) {
        ArgumentChecks.ensureNonNull("interpolation", interpolation);
        this.interpolation = interpolation;
    }

    public InterpolationCase getInterpolation() {
        return interpolation;
    }

    /**
     * Indicate if coverage is homogeneous, which is most of the time true.
     * This allows the generator to start by generation the lowest level tiles
     * and generate upper one based on the previous.
     *
     * If the source coverage is something starter composed of various coverages
     * at different scales it is necessary to set this value to false.
     *
     * @return true if coverage is homogeneous.
     */
    public boolean isCoverageIsHomogeneous() {
        return coverageIsHomogeneous;
    }

    /**
     *
     * @param coverageIsHomogeneous
     */
    public void setCoverageIsHomogeneous(boolean coverageIsHomogeneous) {
        this.coverageIsHomogeneous = coverageIsHomogeneous;
    }

    public boolean isSkipExistingTiles() {
        return skipExistingTiles;
    }

    /**
     * Indicate if existing tiles should be regenerated.
     *
     * @param skipExistingTiles
     */
    public void setSkipExistingTiles(boolean skipExistingTiles) {
        this.skipExistingTiles = skipExistingTiles;
    }

    public void setFillValues(double[] fillValues) {
        this.fillValues = fillValues;
    }

    public double[] getFillValues() {
        return fillValues;
    }

    /**
     * Set to true to always generate tiles from original resource.
     * This will disable generation optimisation.
     *
     * @param generateFromSource
     */
    public void setGenerateFromSource(boolean generateFromSource) {
        this.generateFromSource = generateFromSource;
    }

    /**
     * @return true if tiles are always generated from original resource.
     */
    public boolean isGenerateFromSource() {
        return generateFromSource;
    }

    /**
     * Set to true to always generate tiles using previously generated level
     * when possible otherwise original resource may still be used on border tiles
     * to generate complete tiles.
     * This flag has not effect when generateFromSource flag is active or if resource
     * is not homogeneous.
     * This flag may produce partially filled tiles at upper levels.
     *
     * @param alwaysGnerateUsingLowerLevel
     */
    public void setAlwaysGenerateUsingLowerLevel(boolean alwaysGnerateUsingLowerLevel) {
        this.alwaysGenerateUsingLowerLevel = alwaysGnerateUsingLowerLevel;
    }

    public boolean isAlwaysGenerateUsingLowerLevel() {
        return alwaysGenerateUsingLowerLevel;
    }

    private static double getEmptyValue(SampleDimension dim){
        //dim = dim.forConvertedValues(true);
        double fillValue = Double.NaN;
        final double[] nodata = SampleDimensionUtils.getNoDataValues(dim);
        if (nodata!=null && nodata.length>0) {
            fillValue = nodata[0];
        }
        return fillValue;
    }

    @Override
    protected boolean isEmpty(Tile tileData) throws DataStoreException {
        ImageTile it = (ImageTile) tileData;
        try {
            final RenderedImage image = it.getImage();
            return BufferedImages.isAll(image, empty);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public void generate(WritableTileMatrixSet pyramid, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {
        if (!coverageIsHomogeneous) {
            super.generate(pyramid, env, resolutions, listener);
            return;
        }

        /*
        We can generate the pyramid starting from the lowest level then going up
        using the previously generated level.
        */
        if (env != null) {
            try {
                CoordinateReferenceSystem targetCrs = pyramid.getCoordinateReferenceSystem();
                Envelope baseEnv = env;
                env = Envelopes.transform(env, targetCrs);
                if (resolutions != null) {
                    double[] minres = new double[]{resolutions.getMinDouble(), resolutions.getMinDouble()};
                    double[] maxres = new double[]{resolutions.getMaxDouble(), resolutions.getMaxDouble()};
                    minres = ReferencingUtilities.convertResolution(baseEnv, minres, targetCrs, null);
                    maxres = ReferencingUtilities.convertResolution(baseEnv, maxres, targetCrs, null);
                    resolutions = NumberRange.create(minres[0], true, maxres[0], true);
                }
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        //generate lower level from data
        final WritableTileMatrix[] tileMatrices = pyramid.getTileMatrices().values().toArray(new WritableTileMatrix[0]);
        Arrays.sort(tileMatrices, TileMatrices.SCALE_COMPARATOR);

        final long total = TileMatrices.countTiles(pyramid, env, resolutions);
        final double totalAsDouble = total;
        final AtomicLong al = new AtomicLong();
        final Supplier<Float> progress = () -> (float) (al.get() / totalAsDouble *100.0);

        GridCoverageResource resourceCenter = this.resource;
        GridCoverageResource resourceBorder = this.resource;

        for (final WritableTileMatrix tileMatrix : tileMatrices) {
            if (resolutions == null || resolutions.contains(TileMatrices.getScale(tileMatrix))) {
                final GridExtent rect;
                try {
                    rect = TileMatrices.getTilesInEnvelope(tileMatrix, env);
                } catch (NoSuchDataException ex) {
                    continue;
                }
                final GridCoverageResource sourceCenter = resourceCenter;
                final GridCoverageResource sourceBorder = resourceBorder;

                final long nbTile = TileMatrices.countCells(rect);
                final long eventstep = Math.min(1000, Math.max(1, nbTile/100l));

                Stream<Tile> stream = TileMatrices.pointStream(rect).parallel()
                        .map(new Function<long[],Tile>() {
                            @Override
                            public Tile apply(long[] indices) {
                                final boolean isBorderTile = TileMatrices.onBorder(rect, indices);

                                Tile data = null;
                                try {
                                    if (skipExistingTiles) {
                                        try {
                                            if (TileStatus.EXISTS.equals(tileMatrix.getTileStatus(indices))) {
                                                //tile already exist
                                                return null;
                                            }
                                        } catch (DataStoreException ex) {
                                            //just log it, consider the tile do not exist
                                            LOGGER.warning(ex.getMessage());
                                            if (listener != null) {
                                                long v = al.incrementAndGet();
                                                listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+tileMatrix.getIdentifier(), progress.get(), ex));
                                            }
                                        }
                                    }

                                    try {
                                        data = generateTile(pyramid, tileMatrix, indices, isBorderTile ? sourceBorder : sourceCenter);
                                    } catch (Exception ex) {
                                        data = TileInError.create(indices, null, ex);
                                    }
                                } finally {
                                    long v = al.incrementAndGet();
                                    if (listener != null & (v % eventstep == 0))  {
                                        listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+tileMatrix.getIdentifier(), progress.get()  ));
                                    }
                                }
                                return data;
                            }
                        })
                        .filter(this::emptyFilter);

                batchWrite(stream, tileMatrix, listener == null ? null : err -> listener.progressing(new ProcessEvent(DUMMY, "Error while writing tile batch", progress.get(), err)), 200);

                long v = al.get();
                if (listener != null) {
                    listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+tileMatrix.getIdentifier(), progress.get()  ));
                }

                if (!generateFromSource) {
                    //modify context
                    final DefaultTileMatrixSet.Writable pm = new DefaultTileMatrixSet.Writable(pyramid.getCoordinateReferenceSystem()){};
                    pm.getMosaicsInternal().insertByScale(tileMatrix);
                    final InMemoryTiledGridCoverageResource r = new InMemoryTiledGridCoverageResource(NamesExt.create("test"));
                    r.setSampleDimensions(resourceCenter.getSampleDimensions());
                    r.getTileMatrixSets().add(pm);

                    if (alwaysGenerateUsingLowerLevel) {
                        resourceCenter = r;
                        resourceBorder = r;
                    } else {
                        //we may still have to use the original resource for generation because
                        //lower level tiles may not be sufficient to generate border tiles
                        final AggregatedCoverageResource aggregated = new AggregatedCoverageResource();
                        aggregated.setMode(AggregatedCoverageResource.Mode.ORDER);
                        aggregated.add(r);
                        aggregated.add(this.resource);
                        aggregated.setInterpolation(interpolation.toSis());

                        resourceCenter = r;
                        resourceBorder = aggregated;

                        // common case when tiling a data in the same crs and
                        // matrix envelope equals requested envelope.
                        // then we can avoid aggregation
                        Optional<Envelope> cdt = this.resource.getEnvelope();
                        if (cdt.isPresent() && Utilities.equalsIgnoreMetadata(cdt.get().getCoordinateReferenceSystem(), r.getEnvelope().get().getCoordinateReferenceSystem())) {
                            Envelope dataEnv = cdt.get();
                            Envelope genEnv = (env == null) ? r.getEnvelope().get() : env;
                            if (dataEnv.equals(genEnv)){
                                resourceCenter = r;
                                resourceBorder = r;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Tile generateTile(WritableTileMatrixSet pyramid, WritableTileMatrix mosaic, long[] tileCoord) throws DataStoreException {
        return generateTile(pyramid, mosaic, tileCoord, resource);
    }

    private Tile generateTile(TileMatrixSet pyramid, TileMatrix matrix, long[] tileCoord, GridCoverageResource resource) throws DataStoreException {
        final Dimension tileSize = matrix.getTileSize();
        final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
        final LinearTransform gridToCrsNd = TileMatrices.getTileGridToCRS(matrix, tileCoord, PixelInCell.CELL_CENTER);
        final long[] high = new long[crs.getCoordinateSystem().getDimension()];
        high[0] = tileSize.width-1; //inclusive
        high[1] = tileSize.height-1; //inclusive
        final GridExtent extent = new GridExtent(null, null, high, true);
        final GridGeometry gridGeomNd = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrsNd, crs);

        GridCoverage coverage;
        try {
            //add a margin for resample operations
            final int[] margins = new int[extent.getDimension()];
            Arrays.fill(margins, 2);
            coverage = resource.read(gridGeomNd.derive().margin(margins).build());
        } catch (NoSuchDataException ex) {
            return replaceIfEmpty(TileInError.create(tileCoord, ex), tileSize);
        } catch (DataStoreException ex) {
            throw ex;
        }

        //at this point we should have a coverage 2D
        //if not, this means the source coverage has more dimensions then the pyramid
        //resample coverage to exact tile grid geometry
        try {
            GridCoverageProcessor processor = new GridCoverageProcessor();
            switch (interpolation) {
                case NEIGHBOR : processor.setInterpolation(Interpolation.NEAREST); break;
                case BILINEAR : processor.setInterpolation(Interpolation.BILINEAR); break;
                case LANCZOS : processor.setInterpolation(Interpolation.LANCZOS); break;
                default: processor.setInterpolation(Interpolation.BILINEAR); break;
            }
            coverage = processor.resample(coverage, gridGeomNd);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } catch (IllegalGridGeometryException ex) {
            return replaceIfEmpty(TileInError.create(tileCoord, ex), tileSize);
        }

        RenderedImage image = coverage.render(null);
        ImageProcessor ip = new ImageProcessor();
        image = ip.prefetch(image, null);
        return new DefaultImageTile(image, tileCoord);
    }

    @Override
    public String toString() {
        final List<String> elements = new ArrayList<>();
        elements.add("interpolation : " + interpolation.name());
        elements.add("coverage is homogeneous : " + coverageIsHomogeneous);
        elements.add("skip existing tiles : " + skipExistingTiles);
        elements.add("empty : " + Arrays.toString(empty));
        elements.add("fillValues : " + Arrays.toString(fillValues));
        elements.add("origin : " + resource.toString());
        return StringUtilities.toStringTree(this.getClass().getSimpleName(), elements);
    }

    private Tile replaceIfEmpty(final Tile source, Dimension tileSize) {
        if (source instanceof EmptyTile) {
            try {
                Object result = baseRendering.get(5, TimeUnit.MINUTES);
                if (result instanceof RenderedImage) {
                    final RenderedImage base = (RenderedImage) result;
                    final BufferedImage image = BufferedImages.createImage(base, tileSize.width, tileSize.height, null, null);
                    BufferedImages.setAll(image, fillValues == null ? empty : fillValues);
                    return new DefaultImageTile(image, source.getIndices());
                } else {
                    if (result instanceof NoSuchDataException) {
                        LOGGER.log(Level.FINE, "Resource is empty, create empty tile from samples informations");
                    } else {
                        Exception ex = (Exception) result;
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }

                double[] arr = fillValues == null ? empty : fillValues;
                final BufferedImage image = BufferedImages.createImage(tileSize.width, tileSize.height, arr.length, DataBuffer.TYPE_DOUBLE);
                BufferedImages.setAll(image, fillValues == null ? empty : fillValues);
                return new DefaultImageTile(image, source.getIndices());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Cannot emulate empty tile !", e);
            }
        }

        return source;
    }

    /**
     * HACK: we try to replace notion of empty tile / tile in error by filling a canvas with fill values and returning
     * it in a tile.
     *
     * This method is <em>NOT</em> safe at all. In the mid-term, we should get rid of it and properly manage emptiness
     * and result (by tile) error.
     *
     * @param resource The resource to read from
     * @return Result of the rendering. Never null, RenderedImage or Exception
     */
    private static Object createBaseRendering(final GridCoverageResource resource) throws DataStoreException {
        final GridGeometry gg = resource.getGridGeometry();

        final long[] lower;
        final long[] upper;
        if (gg.isDefined(GridGeometry.EXTENT)) {
            final GridExtent extent = gg.getExtent();
            lower = extent.getLow().getCoordinateValues();
            upper = extent.getHigh().getCoordinateValues();

            for (int i = 0 ; i < 2 ; i++) {
                upper[i] = Math.min(upper[i], lower[i] + 7);
            }

            for (int i = 2 ; i < extent.getDimension() ; i++) {
                upper[i] = lower[i];
            }
        } else {
            int dim = 2;
            if (gg.isDefined(GridGeometry.CRS)) {
                dim = gg.getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
            }
            lower = new long[dim];
            upper = new long[dim];
        }

        final GridExtent newExtent = new GridExtent(null, lower, upper, true);
        final GridGeometry newGg;
        if (gg.isDefined(GridGeometry.CRS) && gg.isDefined(GridGeometry.GRID_TO_CRS)) {
            newGg = gg.derive().subgrid(new GridGeometry(newExtent, PixelInCell.CELL_CENTER, gg.getGridToCRS(PixelInCell.CELL_CENTER), gg.getCoordinateReferenceSystem())).build();
        } else {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            newGg = new GridGeometry(newExtent, CRS.getDomainOfValidity(crs), GridOrientation.REFLECTION_Y);
        }
        try {
            return resource.read(newGg).render(null);
        } catch (Exception ex) {
            return ex;
        }
    }
}
