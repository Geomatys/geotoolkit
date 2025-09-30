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
package org.geotoolkit.storage.dggs.privy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.GeodeticCalculator;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.geotoolkit.storage.dggs.ArrayDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.WritableZoneIterator;
import org.geotoolkit.storage.dggs.ZonalIdentifier;
import org.geotoolkit.storage.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * View a grid coverage resource as a dggrs coverage resource.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GridAsDiscreteGlobalGridResource extends AbstractResource implements DiscreteGlobalGridResource {

    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final GridCoverageResource source;
    private final int maxLevel;

    public GridAsDiscreteGlobalGridResource(DiscreteGlobalGridReferenceSystem dggrs, GridCoverageResource resource)
            throws DataStoreException, IncommensurableException, TransformException {
        super(null);
        this.dggrs = dggrs;
        this.source = resource;

        Quantity<?> res = computeAverageResolution(resource.getGridGeometry());
        DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        coder.setPrecision(res, null);
        maxLevel = coder.getPrecisionLevel();
    }

    @Override
    public DiscreteGlobalGridReferenceSystem getGridReferenceSystem() {
        return dggrs;
    }

    @Override
    public NumberRange<Integer> getAvailableDepths() {
        return NumberRange.create(0, true, maxLevel, true);
    }

    @Override
    public int getDefaultDepth() {
        return 0;
    }

    @Override
    public int getMaxRelativeDepth() {
        return 9;
    }

    @Override
    public DiscreteGlobalGridCoverage read(List<ZonalIdentifier> zones, int... range) throws DataStoreException {

        final List<SampleDimension> sampleDimensions = source.getSampleDimensions();
        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            final double[] datas = new double[zones.size()];
            Arrays.fill(datas, Double.NaN);
            samples.add(TupleArrays.of(ss, datas));
            nans[i] = Double.NaN;
        }

        final ArrayDiscreteGlobalGridCoverage target = new ArrayDiscreteGlobalGridCoverage(new DiscreteGlobalGridGeometry(dggrs, zones), samples);
        try (final WritableZoneIterator iterator = target.createWritableIterator()) {

            final Envelope env = target.getEnvelope().get();
            final double[] resolution = target.getResolution(true);

            final GridExtent extent = new GridExtent(
                    (long) Math.ceil((env.getSpan(0) / resolution[0])*2),
                    (long) Math.ceil((env.getSpan(1) / resolution[1])*2));
            final GridGeometry grid = new GridGeometry(extent, env, GridOrientation.REFLECTION_Y);
            try {
                source.setLoadingStrategy(RasterLoadingStrategy.AT_GET_TILE_TIME);
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
            } catch (NoSuchDataException ex) {
                // do nothing
            }
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        return target;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return source.getSampleDimensions();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return source.getEnvelope();
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return source.getIdentifier();
    }

    /**
     * compute average resolution of a grid geometry
     */
    public static Quantity<?> computeAverageResolution(GridGeometry domain) throws TransformException {
        final double[] resolution = domain.getResolution(true);
        final SingleCRS horizontalCrs = CRS.getHorizontalComponent(domain.getCoordinateReferenceSystem());
        final Envelope envelope = domain.getEnvelope(horizontalCrs);
        final DirectPosition start = GeneralEnvelope.castOrCopy(envelope).getMedian();
        final DirectPosition end = new DirectPosition2D(start.getCoordinateReferenceSystem());
        end.setCoordinate(0, start.getCoordinate(0) + resolution[0]);
        end.setCoordinate(1, start.getCoordinate(1));
        final GeodeticCalculator calculator = GeodeticCalculator.create(horizontalCrs);
        calculator.setStartPoint(start);
        calculator.setEndPoint(end);
        final double distance = calculator.getGeodesicDistance();
        return Quantities.create(distance, Units.METRE);
    }

}
