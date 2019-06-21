/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.time.Instant;
import java.nio.file.Path;
import java.sql.SQLException;
import javax.measure.UnitConverter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.storage.*;


/**
 * Reference to a {@link GridCoverage}. This object holds some metadata about the coverage time range,
 * envelope, <cite>etc.</cite>) without the need to open the image file, since the metadata are extracted
 * from the database.
 *
 * <p>{@code GridCoverageReference} instances are immutable and thread-safe.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Sam Hiatt
 */
final class GridCoverageEntry extends Entry {
    /**
     * The series in which the {@code GridCoverageReference} is defined.
     */
    private final SeriesEntry series;

    /**
     * The grid coverage filename, not including the extension.
     */
    private final String filename;

    /**
     * Grid coverage start time (inclusive), or {@code null} if none.
     */
    private final Instant startTime;

    /**
     * Grid coverage end time (exclusive), or {@code null} if none.
     */
    private final Instant endTime;

    /**
     * The spatial and vertical extents of the grid coverage, together with the <cite>grid to CRS</cite> transform.
     */
    private final GridGeometryEntry grid;

    /**
     * Creates an entry containing coverage information (but not yet the coverage itself).
     *
     * @param  startTime  the coverage start time (inclusive), or {@code null} if none.
     * @param  endTime    the coverage end time (exclusive), or {@code null} if none.
     */
    GridCoverageEntry(final SeriesEntry series, final String filename,
            final Instant startTime, final Instant endTime, final GridGeometryEntry grid) throws SQLException
    {
        this.series    = series;
        this.filename  = filename;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.grid      = grid;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return filename + " @ " + endTime;
    }

    /**
     * Returns the grid geometry of this coverage.
     * The CRS of the returned grid geometry may vary on a coverage-by-coverage basis.
     *
     * @return the grid geometry of this coverage.
     * @throws DataStoreException if the operation failed.
     */
    final GridGeometry getGridGeometry() throws DataStoreException {
        try {
            return grid.getGridGeometry(startTime, endTime);
        } catch (TransformException e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Returns the coverage sample dimensions, or {@code null} if unknown.
     * This method returns always the <cite>geophysics</cite> version of sample dimensions
     * (<code>{@linkplain SampleDimension#geophysics geophysics}(true)</code>), which is
     * consistent with the coverage returned by {@link #getCoverage getCoverage(...)}.
     *
     * @return the sample dimensions, or {@code null} if unknown.
     */
    final List<SampleDimension> getSampleDimensions() {
        return series.format.sampleDimensions;
    }

    final Path getDataPath() {
        return series.path(filename);
    }

    /**
     * Loads the data if needed and returns the coverage.
     * Current implementation reads only the first resource.
     */
    final GridCoverage coverage(GridGeometry targetGeometry, final int... bands) throws Exception {
        try (DataStore store = series.format.open(getDataPath())) {
            final String dataset = series.dataset;
            final GridCoverageResource r;
            if (dataset != null) {
                Resource cdt = store.findResource(dataset);
                if (cdt instanceof GridCoverageResource) {
                    r = (GridCoverageResource) cdt;
                } else {
                    r = null;
                }
            } else {
                // Pick first resource.
                r = resource(store);
            }
            if (r != null) {
                if (targetGeometry != null && targetGeometry.isDefined(GridGeometry.CRS | GridGeometry.ENVELOPE)) {
                    targetGeometry = toAbsoluteRuntime(targetGeometry, r);
                }
                return r.read(targetGeometry, bands);
            }
        }
        throw new CatalogException("No GridCoverageResource found for " + filename);
    }

    /**
     * Returns the first grid coverage resource found in the given resource,
     * scanning recursively in children if that resource is an aggregate.
     */
    private static GridCoverageResource resource(final Resource resource) throws DataStoreException {
        if (resource instanceof Aggregate) {
            for (final Resource child : ((Aggregate) resource).components()) {
                GridCoverageResource r = resource(child);
                if (r != null) return r;
            }
        } else if (resource instanceof GridCoverageResource) {
            return (GridCoverageResource) resource;
        }
        return null;
    }

    /**
     * If the CRS of the request uses a <cite>"Runtime relative to data time"</cite> axis while the
     * CRS of the data uses absolute time in their axis, replaces the relative time by absolute time.
     *
     * @deprecated this code is complicated (probably buggy), costly and may lost information in the process.
     *             This work should be done elsewhere. Maybe the grid resource should understand relative time
     *             by itself. Maybe the conversion between absolute time and relative time should be done by
     *             the referencing module.
     */
    @Deprecated
    private static GridGeometry toAbsoluteRuntime(final GridGeometry request, final GridCoverageResource resource) throws Exception {
        final CoordinateReferenceSystem requestCRS = request.getCoordinateReferenceSystem();
        final List<SingleCRS> componentsOfRequestCRS = CRS.getSingleComponents(requestCRS);
        int dimensionInRequest = 0;
        for (int indexOfCRS=0; indexOfCRS < componentsOfRequestCRS.size(); indexOfCRS++) {
            final SingleCRS requestComponent = componentsOfRequestCRS.get(indexOfCRS);
            if (AdditionalAxisTable.isTemporalAxis(requestComponent, AdditionalAxisTable.RELATIVE_RUNTIME_DATUM)) {
                final GridGeometry gridOfData = resource.getGridGeometry();
                if (gridOfData != null && gridOfData.isDefined(GridGeometry.CRS | GridGeometry.ENVELOPE)) {
                    int dimensionInData = 0;
                    for (final SingleCRS dataComponent : CRS.getSingleComponents(gridOfData.getCoordinateReferenceSystem())) {
                        if (AdditionalAxisTable.isTemporalAxis(dataComponent, AdditionalAxisTable.RUNTIME_DATUM)) {
                            /*
                             * Found a relative runtime axis. Convert the relative time range request
                             * to an absolute time range, using the current image time as the origin.
                             */
                            final UnitConverter requestToData = AdditionalAxisTable.getUnit(requestComponent)
                                             .getConverterToAny(AdditionalAxisTable.getUnit(dataComponent));
                            final double startTime = gridOfData.getEnvelope().getMinimum(dimensionInData);  // In unit of data CRS.
                            final Envelope requestEnvelope = request.getEnvelope();
                            double lower = requestEnvelope.getMinimum(dimensionInRequest);                  // In unit of request CRS
                            double upper = requestEnvelope.getMaximum(dimensionInRequest);
                            lower = requestToData.convert(lower);                                           // In unit of data CRS.
                            upper = requestToData.convert(upper);
                            if (AxisDirections.opposite(AdditionalAxisTable.getDirection(requestComponent)) ==
                                                        AdditionalAxisTable.getDirection(dataComponent))
                            {
                                lower = -upper;
                                upper = -lower;
                            }
                            lower += startTime;             // Now an absolute time with same unit and epoch than data CRS.
                            upper += startTime;
                            /*
                             * Convert the time range to grid indices in the system of data grid. This range may be wider
                             * (contains more cells) than the requested range. We will reduce it to the requested size later.
                             */
                            TransformSeparator sep = new TransformSeparator(gridOfData.getGridToCRS(PixelInCell.CELL_CORNER).inverse());
                            sep.addSourceDimensionRange(dimensionInData, dimensionInData + 1);
                            MathTransform1D runtimeTr = (MathTransform1D) sep.separate();
                            lower = runtimeTr.transform(lower);                             // In grid cell coordinates of data.
                            upper = runtimeTr.transform(upper);
                            runtimeTr = runtimeTr.inverse();                                // Will be needed later.
                            /*
                             * Compute the translation that we need to apply on grid extent for having the center
                             * of requested grid extent located at the center of requested runtime when expressed
                             * in unit of the data CRS. This translation is in the runtime dimension only.
                             */
                            GridExtent extent = request.getExtent();
                            final long[] lowerCoordinates = extent.getLow().getCoordinateValues();
                            final long[] upperCoordinates = extent.getHigh().getCoordinateValues();
                            final long offset = Math.round(0.5 * ((upper - upperCoordinates[dimensionInRequest]) +
                                                                  (lower - lowerCoordinates[dimensionInRequest])));
                            if (offset != 0) {
                                lowerCoordinates[dimensionInRequest] += offset;
                                upperCoordinates[dimensionInRequest] += offset;
                                extent = new GridExtent(null, lowerCoordinates, upperCoordinates, true);
                            }
                            /*
                             * Substitute the data CRS for the temporal dimension.
                             */
                            MathTransform gridToCRS = request.getGridToCRS(PixelInCell.CELL_CORNER);
                            sep = new TransformSeparator(gridToCRS);
                            sep.addSourceDimensionRange(0, dimensionInRequest);
                            MathTransform lowerTr = sep.separate();
                            sep.clear();
                            sep.addSourceDimensionRange(dimensionInRequest + 1, gridToCRS.getSourceDimensions());
                            MathTransform upperTr = sep.separate();
                            gridToCRS = MathTransforms.compound(lowerTr, runtimeTr, upperTr);
                            /*
                             * Rebuild the grid geometry with the new time range.
                             */
                            final SingleCRS[] modifiedComponents = componentsOfRequestCRS.toArray(new SingleCRS[componentsOfRequestCRS.size()]);
                            modifiedComponents[indexOfCRS] = dataComponent;                    // Replace request CRS by data CRS.
                            final CoordinateReferenceSystem modifiedCRS = new DefaultCompoundCRS(
                                    AdditionalAxisTable.properties(requestCRS.getName()), modifiedComponents);
                            return new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCRS, modifiedCRS);
                        }
                        dimensionInData += dataComponent.getCoordinateSystem().getDimension();
                    }
                }
                break;          // Can not convert relative runtime axes. There is no point to continue.
            }
            dimensionInRequest += requestComponent.getCoordinateSystem().getDimension();
        }
        return request;
    }
}
