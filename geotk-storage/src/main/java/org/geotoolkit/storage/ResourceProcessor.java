/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage;

import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.image.DataType;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.system.Loggers;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.internal.shared.Numerics;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.MathTransform;


/**
 * A predefined set of operations on resources as convenience methods.
 *
 * @author  Johann Sorel (Geomatys)
 * @version 1.4
 * @since   1.4
 */
public class ResourceProcessor implements Cloneable {

    private final GridCoverageProcessor processor;

    /**
     * Creates a new processor with default configuration.
     */
    public ResourceProcessor() { this(null); }

    public ResourceProcessor(GridCoverageProcessor processor) {
        this.processor = processor == null ? new GridCoverageProcessor() : processor;
    }

    /**
     * @return The processor used internally to transform produced {@link GridCoverage grid coverages}. Not null.
     */
    public GridCoverageProcessor getProcessor() { return processor; }

    @Override
    public ResourceProcessor clone() {
        return new ResourceProcessor(processor.clone());
    }

    /**
     * Returns a coverage resource with sample values converted by the given functions.
     * The number of sample dimensions in the returned coverage is the length of the {@code converters} array,
     * which must be greater than 0 and not greater than the number of sample dimensions in the source coverage.
     * If the {@code converters} array length is less than the number of source sample dimensions,
     * then all sample dimensions at index ≥ {@code converters.length} will be ignored.
     *
     * <h4>Sample dimensions customization</h4>
     * By default, this method creates new sample dimensions with the same names and categories than in the
     * previous coverage, but with {@linkplain org.apache.sis.coverage.Category#getSampleRange() sample ranges}
     * converted using the given converters and with {@linkplain SampleDimension#getUnits() units of measurement}
     * omitted. This behavior can be modified by specifying a non-null {@code sampleDimensionModifier} function.
     * If non-null, that function will be invoked with, as input, a pre-configured sample dimension builder.
     * The {@code sampleDimensionModifier} function can {@linkplain SampleDimension.Builder#setName(CharSequence)
     * change the sample dimension name} or {@linkplain SampleDimension.Builder#categories() rebuild the categories}.
     *
     * <h4>Result relationship with source</h4>
     * If the source coverage is backed by a {@link java.awt.image.WritableRenderedImage},
     * then changes in the source coverage are reflected in the returned coverage and conversely.
     *
     * @see GridCoverageProcessor#convert(org.apache.sis.coverage.grid.GridCoverage, org.opengis.referencing.operation.MathTransform1D[], java.util.function.Function)
     * @see ImageProcessor#convert(RenderedImage, NumberRange[], MathTransform1D[], DataType, ColorModel)
     */
    public GridCoverageResource convert(final GridCoverageResource source, MathTransform1D[] converters,
            Function<SampleDimension.Builder, SampleDimension> sampleDimensionModifier)
    {
        return new ConvertedCoverageResource(source, converters, sampleDimensionModifier);
    }

    /**
     * Appends the specified grid dimensions after the dimensions of the given source coverage.
     * This method is typically invoked for adding a vertical or temporal axis to a two-dimensional coverage.
     * The grid extent must have a size of one cell in all the specified additional dimensions.
     *
     * @param  source    the source on which to append dimensions.
     * @param  dimToAdd  the dimensions to append. The grid extent size must be 1 cell in all dimensions.
     * @return a coverage with the specified dimensions added.
     * @throws IllegalGridGeometryException if a dimension has more than one grid cell, or concatenation
     *         would result in duplicated {@linkplain GridExtent#getAxisType(int) grid axis types},
     *         or the compound CRS cannot be created.
     */
    public GridCoverageResource appendDimensions(final GridCoverageResource source, final GridGeometry dimToAdd) {
        ArgumentChecks.ensureNonNull("source",   source);
        ArgumentChecks.ensureNonNull("dimToAdd", dimToAdd);
        try {
            return DimensionAddedCoverageResource.create(processor, source, dimToAdd);
        } catch (IllegalGridGeometryException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new IllegalGridGeometryException(e.getMessage(), e);
        }
    }

    /**
     * Appends a single grid dimension after the dimensions of the given source coverage.
     * This method is typically invoked for adding a vertical axis to a two-dimensional coverage.
     * The default implementation delegates to {@link #appendDimensions(GridCoverage, GridGeometry)}.
     *
     * @param  source  the source on which to append a dimension.
     * @param  lower   lower coordinate value of the slice, in units of the CRS.
     * @param  span    size of the slice, in units of the CRS.
     * @param  crs     one-dimensional coordinate reference system of the slice, or {@code null} if unknown.
     * @return a coverage with the specified dimension added.
     * @throws IllegalGridGeometryException if the compound CRS or compound extent cannot be created.
     */
    public GridCoverageResource appendDimension(final GridCoverageResource source, double lower, final double span, final SingleCRS crs) {
        /*
         * Choose a cell index such as the translation term in the matrix will be as close as possible to zero.
         * Reducing the magnitude of additions with IEEE 754 arithmetic can help to reduce rounding errors.
         * It also has the desirable side-effect to increase the chances that slices share the same
         * "grid to CRS" transform.
         */
        final long index = Numerics.roundAndClamp(lower / span);
        final long[] indices = new long[] {index};
        final DimensionNameType dimName = GridExtent.typeFromAxis(crs.getCoordinateSystem().getAxis(0)).orElse(null);
        final GridExtent extent = new GridExtent(dimName == null ? null : new DimensionNameType[]{dimName}, indices, indices, true);
        final MathTransform gridToCRS = MathTransforms.linear(span, Math.fma(index, -span, lower));
        return appendDimensions(source, new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCRS, crs));
    }

    /**
     * Appends a temporal grid dimension after the dimensions of the given source coverage.
     * The default implementation delegates to {@link #appendDimensions(GridCoverage, GridGeometry)}.
     *
     * @param  source  the source on which to append a temporal dimension.
     * @param  lower   start time of the slice.
     * @param  span    duration of the slice.
     * @return a coverage with the specified temporal dimension added.
     * @throws IllegalGridGeometryException if the compound CRS or compound extent cannot be created.
     */
    public GridCoverageResource appendDimension(final GridCoverageResource source, final Instant lower, final Duration span) {
        final DefaultTemporalCRS crs = DefaultTemporalCRS.castOrCopy(CommonCRS.defaultTemporal());
        double scale  = crs.toValue(span);
        double offset = crs.toValue(lower);
        long   index  = Numerics.roundAndClamp(offset / scale);             // See comment in above method.
        offset = crs.toValue(lower.minus(span.multipliedBy(index)));
        final GridExtent extent = new GridExtent(DimensionNameType.TIME, index, index, true);
        final MathTransform gridToCRS = MathTransforms.linear(scale, offset);
        return appendDimensions(source, new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCRS, crs));
    }

    /**
     * Wraps a given resource with a resample operator, to ensure it fits a provided grid geometry.
     * Neither resampling nor read is triggered immediately. Instead, a virtual resource is returned.
     * It will launch read then resample operations upon call to {@link GridCoverageResource#read(GridGeometry, int...)}.
     *
     * @param source Resource to resample. Must not be null.
     * @param target Grid geometry to use for output/resampled resource. Must not be null.
     * @param targetName An optional name for returned resource. If null, output {@link org.apache.sis.storage.Resource#getIdentifier() resource identifier} will not be present.
     * @return A resource decorating provided one. It triggers a {@link GridCoverageProcessor#resample(GridCoverage, GridGeometry) resampling operation} upon reads. Never null.
     */
    public GridCoverageResource resample(final GridCoverageResource source, final GridGeometry target, GenericName targetName) {
        return new ResampledGridCoverageResource(source, target, targetName, processor);
    }

    /**
     * Reprojects provided resource. Note that:
     * <ul>
     *     <li>Provided resource metadata and grid geometry will be immediately fetched</li>
     *     <li>Resampling will be postponed until a call to {@link GridCoverageResource#read(GridGeometry, int...)}</li>
     * </ul>
     * @return Either the input resource if no reprojection is needed for conversion to target CRS. Otherwise, a virtual dataset performing resample on read.
     * @throws DataStoreException If input resource metadata or grid geometry cannot be acquired.
     * @throws FactoryException If referencing database is not reachable, or if it is not possible to find any valid operation from input resource system to provided CRS.
     * @throws TransformException If an error occurs while transforming input resource geometry to the target CRS.
     * @throws IncompleteGridGeometryException If input resource geometry does not provide enough information to build a resampling pipeline (i.e. No CRS or no envelope).
     */
    public GridCoverageResource resample(final GridCoverageResource source, final CoordinateReferenceSystem target, GenericName targetName)
            throws DataStoreException, FactoryException, TransformException
    {
        ensureNonNull("Source", source);
        ensureNonNull("Target CRS", target);
        final GridGeometry sourceGeom = source.getGridGeometry();
        final CoordinateReferenceSystem sourceCrs = sourceGeom.getCoordinateReferenceSystem();

        final GridGeometry reprojected;
        if (sourceGeom.isDefined(GridGeometry.GRID_TO_CRS + GridGeometry.EXTENT)) {
            final CoordinateOperation op = CRS.findOperation(sourceCrs, target, searchGeographicExtent(source).orElse(null));
            if (op.getMathTransform() == null || op.getMathTransform().isIdentity()) return source;
            reprojected = new GridGeometry(sourceGeom.getExtent(), PixelInCell.CELL_CENTER,
                    MathTransforms.concatenate(sourceGeom.getGridToCRS(PixelInCell.CELL_CENTER), op.getMathTransform()), target);
        } else if (sourceGeom.isDefined(GridGeometry.ENVELOPE)) {
            reprojected = new GridGeometry(null, null, sourceGeom.getEnvelope(target), GridRoundingMode.ENCLOSING);
        } else throw new IncompleteGridGeometryException("Cannot reproject a grid coverage resource whose geometry defines neither an envelope nor a conversion for grid to CRS");

        return new ResampledGridCoverageResource(source, reprojected, targetName, processor);
    }

    /**
     * Apply a mask on given resource.
     *
     * @param source the resource on which to apply a mask.
     * @param maskingset FeatureSet with masking features.
     * @param maskInside {@code true} for masking pixels inside the shape, or {@code false} for masking outside.
     * @return a resource with mask applied.
     * @see GridCoverageProcessor#mask(org.apache.sis.coverage.grid.GridCoverage, org.apache.sis.coverage.RegionOfInterest, boolean)
     */
    public GridCoverageResource mask(final GridCoverageResource source, FeatureSet maskingset, boolean maskInside) {
        return new MaskGridCoverageResource(source, maskingset, maskInside);
    }

    /**
     * Aggregates in a single coverage resource the ranges of all specified coverage resources, in order.
     * The {@linkplain GridCoverage#getSampleDimensions() list of sample dimensions} of
     * the aggregated coverage resource will be the concatenation of the lists from all sources.
     *
     * <p>This convenience method delegates to {@link #aggregateRanges(GridCoverageResource[], int[][])}.
     * See that method for more information on restrictions.</p>
     *
     * @param  sources  coverage resources whose ranges shall be aggregated, in order. At least one resource must be provided.
     * @return the aggregated resource, or {@code sources[0]} returned directly if only one resource was supplied.
     *
     * @see GridCoverageProcessor#aggregateRanges(org.apache.sis.coverage.grid.GridCoverage...)
     */
    public GridCoverageResource aggregateRanges(final GridCoverageResource... sources)
            throws DataStoreException, TransformException {
        return aggregateRanges(sources, null, (int[][]) null);
    }

    /**
     * Aggregates in a single coverage resource the specified bands of a sequence of source coverage resources, in order.
     * This method performs the same work as {@link #aggregateRanges(GridCoverageResource...)},
     * but with the possibility to specify the sample dimensions to retain in each source resource.
     * The {@code bandsPerSource} argument specifies the sample dimensions to keep, in order.
     * That array can be {@code null} for selecting all sample dimensions in all source coverage resources,
     * or may contain {@code null} elements for selecting all sample dimensions of the corresponding coverage resource.
     * An empty array element (i.e. zero sample dimension to select) discards the corresponding source coverage resource.
     * <p>
     * This class do not use {GridCoverageProcessor#aggregateRanges} directly because it has very strong constraints which
     * makes it unusable at a resource processing level at this moment.
     *
     * @param  sources  coverages whose bands shall be aggregated, in order. At least one coverage must be provided.
     * @param crs the produced resource CRS, may be {@code null} to find best match.
     * @param  bandsPerSource  bands to use for each source coverage, in order. May contain {@code null} elements.
     * @return the aggregated coverage, or one of the sources if it can be used directly.
     *
     * @see GridCoverageProcessor#aggregateRanges(org.apache.sis.coverage.grid.GridCoverage[], int[][])
     */
    public GridCoverageResource aggregateRanges(GridCoverageResource[] sources, CoordinateReferenceSystem crs, int[][] bandsPerSource)
            throws DataStoreException, TransformException {

        final List<AggregatedCoverageResource.VirtualBand> bands = new ArrayList();

        for (int i = 0; i < sources.length; i++) {
            final List<SampleDimension> sampleDimensions = sources[i].getSampleDimensions();
            final int[] selection = bandsPerSource == null ? null : bandsPerSource[i];

            if (selection == null) {
                //pick all bands
                for (int k = 0, n = sampleDimensions.size(); k < n ;k++) {
                    final AggregatedCoverageResource.VirtualBand vb = new AggregatedCoverageResource.VirtualBand();
                    vb.setSources(new AggregatedCoverageResource.Source(sources[i], k));
                    bands.add(vb);
                }
            } else {
                for (int k : selection) {
                    final AggregatedCoverageResource.VirtualBand vb = new AggregatedCoverageResource.VirtualBand();
                    vb.setSources(new AggregatedCoverageResource.Source(sources[i], k));
                    bands.add(vb);
                }
            }
        }

        return new AggregatedCoverageResource(bands, AggregatedCoverageResource.Mode.ORDER, crs);
    }

    /**
     * Aggregate grid coverage resources as a single coverage resource on the same horizontal CRS.
     * Coverage gaps are progressively filled by the next resource until there are no more resources or no more gaps.
     *
     * @param sources coverages resources to merge.
     * @return the aggregated coverage resource.
     *
     * @see #aggregateDomain2D(org.apache.sis.storage.GridCoverageResource[], org.opengis.referencing.crs.CoordinateReferenceSystem, boolean)
     */
    public GridCoverageResource aggregateDomain2D(final GridCoverageResource... sources)
            throws DataStoreException, TransformException {
        return aggregateDomain2D(sources, null, false);
    }

    /**
     * Aggregate grid coverage resources as a single coverage resource on the same horizontal CRS.
     * Coverage gaps are progressively filled by the next resource until there are no more resources or no more gaps.
     *
     * @param sources coverages resources to merge.
     * @param crs the produced resource CRS, may be {@code null} to find best match.
     * @param byScale true to aggregate resources by most appropriate scale first, otherwise use the order defined in the source array
     * @return the aggregated coverage resource.
     */
    public GridCoverageResource aggregateDomain2D(GridCoverageResource[] sources, CoordinateReferenceSystem crs, boolean byScale)
            throws DataStoreException, TransformException {
        return AggregatedCoverageResource.create(crs,
                byScale ? AggregatedCoverageResource.Mode.SCALE : AggregatedCoverageResource.Mode.ORDER, sources);
    }

    private static Optional<GeographicBoundingBox> searchGeographicExtent(GridCoverageResource source) throws DataStoreException {
        final Optional<GeographicBoundingBox> bbox = source.getMetadata().getIdentificationInfo().stream()
                .flatMap(it -> it.getExtents().stream())
                .flatMap(it -> it.getGeographicElements().stream())
                .filter(GeographicBoundingBox.class::isInstance)
                .map(it -> (GeographicBoundingBox) it)
                .reduce(ResourceProcessor::union);

        if (bbox.isPresent()) return bbox;

        return source.getEnvelope()
                .map(it -> {
                    DefaultGeographicBoundingBox g = new DefaultGeographicBoundingBox();
                    try {
                        g.setBounds(it);
                    } catch (TransformException e) {
                        Logger.getLogger(Loggers.COORDINATE_OPERATION)
                                .log(Level.FINE, "Cannot extract geographic extent from source resource", e);
                        return null;
                    }
                    return g;
                });
    }

    private static GeographicBoundingBox union(GeographicBoundingBox g1, GeographicBoundingBox g2) {
        final DefaultGeographicBoundingBox union = new DefaultGeographicBoundingBox(g1);
        union.add(g2);
        return union;
    }
}
