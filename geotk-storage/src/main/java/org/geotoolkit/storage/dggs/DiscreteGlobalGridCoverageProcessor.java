/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.dggs;

import org.apache.sis.geometry.DirectPosition1D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.dggs.Zone;
import java.awt.image.RenderedImage;
import org.geotoolkit.storage.dggs.internal.shared.ArrayDiscreteGlobalGridCoverage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.rs.Code;
import org.geotoolkit.storage.rs.CodeTransform;
import org.geotoolkit.storage.rs.internal.shared.WritableBandedCodeIterator;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

/**
 * Provide operations related to DiscreteGlobalGridReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridCoverageProcessor {

    private static final String[] CELLS_RESOURCES_NAMES = {"cell", "cells", "dggs_id", "cells_id", "cell_id", "dggs_cell", "dggs_cells"};

    public DiscreteGlobalGridCoverageProcessor() {
    }

    /**
     * Resample a GridCoverage to a DiscreteGlobalGridCoverage.
     * The current sampling method use nearest neighbor.
     *
     * @param source to sample from, not null
     * @param gridGeometry geometry of the desired dggs coverage.
     * @param range bands to read
     * @return dggs coverage, not null
     *
     * @throws DataStoreException if extracting datas for the source failed or dggs coverage failed at writing
     * @throws TransformException if a dggrs zone computation failed
     */
    public DiscreteGlobalGridCoverage resample(GridCoverageResource source, DiscreteGlobalGridGeometry gridGeometry, int ... range) throws DataStoreException, TransformException {
        final List<Object> zones = gridGeometry.getZoneIds();
        List<SampleDimension> sampleDimensions = source.getSampleDimensions();
        if (range != null && range.length != 0) {
            final List<SampleDimension> selected = new ArrayList<>();
            for (int i = 0; i < range.length; i++) {
                selected.add(sampleDimensions.get(range[i]));
            }
            sampleDimensions = selected;
        }

        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            final double[] datas = new double[zones.size()];
            Arrays.fill(datas, Double.NaN);
            samples.add(TupleArrays.of(ss, datas));
            nans[i] = Double.NaN;
        }

        final ArrayDiscreteGlobalGridCoverage target = new ArrayDiscreteGlobalGridCoverage(source.getIdentifier().get(), gridGeometry, samples);
        final CodeTransform gridToRS = gridGeometry.getGridToRS();
        final DiscreteGlobalGridReferenceSystem dggrs = gridGeometry.getReferenceSystem();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        try (final WritableBandedCodeIterator iterator = target.createWritableIterator()) {

            final Envelope env = target.getEnvelope().get();
            final double[] resolution = target.getResolution(true);

            final GridExtent extent = new GridExtent(
                    (long) Math.ceil((env.getSpan(0) / resolution[0])*2),
                    (long) Math.ceil((env.getSpan(1) / resolution[0])*2));
            final GridGeometry grid = new GridGeometry(extent, env, GridOrientation.REFLECTION_Y);
            try {
                source.setLoadingStrategy(RasterLoadingStrategy.AT_GET_TILE_TIME);
                GridCoverage coverage = source.read(grid, range).forConvertedValues(true);

                //if (coverage.getClass().getName().endsWith("TileMatrixCoverage")) {
                    //todo : ugly hack to enforce coverage cache
                    final GridGeometry domain = coverage.getGridGeometry();
                    RenderedImage image = coverage.render(domain.getExtent());
                    coverage = new GridCoverageBuilder()
                            .setDomain(domain)
                            .setRanges(coverage.getSampleDimensions())
                            .setValues(image)
                            .build();
                //}

                final GridCoverage.Evaluator evaluator = coverage.evaluator();
                evaluator.setNullIfOutside(true);

                while (iterator.next()) {
                    final int[] position = iterator.getPosition();
                    final Code code = gridToRS.toCode(position);
                    final Object zid = code.getOrdinate(0);
                    final Zone zone = coder.decode(zid);
                    final DirectPosition dp = zone.getPosition();
                    double[] values = evaluator.apply(dp);
                    if (values == null) {
                        values = nans;
                    }
                    iterator.setCell(values);
                }
            } catch (NoSuchDataException ex) {
                // do nothing
            }
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        return target;
    }

    /**
     * Resample a GridCoverage to a DiscreteGlobalGridCoverage.
     * The current sampling method use nearest neighbor.
     * This method assume the source coverage has a dimension representing the dggrs zones (ids).
     *
     * @param source
     * @param gridGeometry
     * @param range
     * @return
     * @throws DataStoreException
     */
    public DiscreteGlobalGridCoverage resampleZonalCoverage(GridCoverageResource source, DiscreteGlobalGridGeometry gridGeometry, int... range) throws DataStoreException, TransformException {
        final List<Object> zones = gridGeometry.getZoneIds();

        List<SampleDimension> sampleDimensions = source.getSampleDimensions();
        if (range != null && range.length != 0) {
            final List<SampleDimension> selected = new ArrayList<>();
            for (int i = 0; i < range.length; i++) {
                selected.add(sampleDimensions.get(range[i]));
            }
            sampleDimensions = selected;
        }

        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            final double[] datas = new double[zones.size()];
            Arrays.fill(datas, Double.NaN);
            samples.add(TupleArrays.of(ss, datas));
            nans[i] = Double.NaN;
        }

        final ArrayDiscreteGlobalGridCoverage target = new ArrayDiscreteGlobalGridCoverage(source.getIdentifier().get(), gridGeometry, samples);
        final CodeTransform gridToRS = gridGeometry.getGridToRS();
        final DiscreteGlobalGridReferenceSystem dggrs = gridGeometry.getReferenceSystem();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();

        try (final WritableBandedCodeIterator iterator = target.createWritableIterator()) {

            CoordinateReferenceSystem crs = source.getGridGeometry().getCoordinateReferenceSystem();
            CoordinateSystem cs = crs.getCoordinateSystem();
            int dimension = cs.getDimension();

            int cellDimensionId = -1;
            for (int dimIdx = 0; dimIdx < dimension; dimIdx++) {
                CoordinateSystemAxis csa = cs.getAxis(dimIdx);
                AxisDirection axisDirection = csa.getDirection();
                String abbreviation = csa.getAbbreviation().toLowerCase();
                DimensionNameType axisType = source.getGridGeometry().getExtent().getAxisType(dimIdx).orElse(null);

                if (cellDimensionId == -1 && (axisDirection == AxisDirection.COLUMN_POSITIVE || Arrays.asList(CELLS_RESOURCES_NAMES).contains(abbreviation))) {
                    cellDimensionId = dimIdx;
                }
            }

            if (cellDimensionId == -1) {
                throw new DataStoreException("No cell dimension found in the source raster resource.");
            }

            final Envelope env = source.getGridGeometry().getEnvelope();
            GeneralEnvelope env2 = new GeneralEnvelope(env);
            env2.setRange(0, env.getMinimum(0), env.getMinimum(0));
            final GridExtent extent = new GridExtent(source.getGridGeometry().getExtent().getHigh(0), 1L);
            final GridGeometry grid = new GridGeometry(extent, env2, GridOrientation.REFLECTION_Y);

            try {
                source.setLoadingStrategy(RasterLoadingStrategy.AT_GET_TILE_TIME);
                final GridCoverage coverage = source.read(grid);
                final GridCoverage.Evaluator evaluator = coverage.evaluator();
                evaluator.setNullIfOutside(true);

                while(iterator.next()) {
                    final int[] position = iterator.getPosition();
                    final Code code = gridToRS.toCode(position);
                    final Object zid = code.getOrdinate(0);
                    final Zone zone = coder.decode(zid);
                    final long cellId = zone.getLongIdentifier();

                    DirectPosition dp = new DirectPosition1D(cellId);

//                    DirectPosition dp;
//                    if (timeDimensionId != -1) {
//                        dp = new DirectPosition2D(coverage.getCoordinateReferenceSystem(), source.getEnvelope().orElseThrow().getMinimum(0), cellId);
//                    } else {
//                        dp = new DirectPosition1D(cellId);
//                    }

                    double[] values = evaluator.apply(dp);
                    if (values == null) {
                        values = nans;
                    }
                    iterator.setCell(values);
                }
            } catch (NoSuchDataException ex) {
                // do nothing
            }
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        return target;
    }
}
