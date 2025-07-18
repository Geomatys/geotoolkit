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

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * Provide operations related to DiscreteGlobalGridReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridCoverageProcessor {

    public DiscreteGlobalGridCoverageProcessor() {
    }

    /**
     * Resample a GridCoverage to a DiscreteGlobalGridCoverage.
     * The current sampling method use nearest neighbor.
     *
     * @param source to sample from, not null
     * @param gridGeometry geometry of the desired dggs coverage.
     * @return dggs coverage, not null
     *
     * @throws DataStoreException if extracting datas for the source failed or dggs coverage failed at writing
     * @throws TransformException if a dggrs zone computation failed
     */
    public DiscreteGlobalGridCoverage resample(GridCoverageResource source, DiscreteGlobalGridGeometry gridGeometry) throws DataStoreException, TransformException {

        final List<ZonalIdentifier> zones = gridGeometry.getZones();
        final List<SampleDimension> sampleDimensions = source.getSampleDimensions();
        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            samples.add(TupleArrays.of(ss, new double[zones.size()]));
            nans[i] = Double.NaN;
        }

        final ArrayDiscreteGlobalGridCoverage target = new ArrayDiscreteGlobalGridCoverage(gridGeometry, samples);
        try (final WritableZoneIterator iterator = target.createWritableIterator()) {

            final Envelope env = target.getEnvelope().get();
            final double[] resolution = target.getResolution(true);

            final GridExtent extent = new GridExtent(
                    (long) Math.ceil((env.getSpan(0) / resolution[0])*2),
                    (long) Math.ceil((env.getSpan(1) / resolution[1])*2));
            final GridGeometry grid = new GridGeometry(extent, env, GridOrientation.REFLECTION_Y);
            final GridCoverage coverage = source.read(grid);
            final GridCoverage.Evaluator evaluator = coverage.evaluator();
            evaluator.setNullIfOutside(true);

            while (iterator.next()) {
                final Zone zone = iterator.getZone();
                final DirectPosition dp = zone.getPosition();
                double[] values = evaluator.apply(dp);
                if (values == null) {
                    values = nans;
                }
                iterator.setCell(values);
            }
        }

        return target;
    }

}
