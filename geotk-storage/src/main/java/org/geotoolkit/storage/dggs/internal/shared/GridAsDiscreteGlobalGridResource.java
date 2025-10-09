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
package org.geotoolkit.storage.dggs.internal.shared;

import java.util.List;
import java.util.Optional;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
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
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverageProcessor;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.opengis.feature.FeatureType;
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

    private final GridCoverageResource source;
    private final DiscreteGlobalGridGeometry gridGeometry;
    private final int maxLevel;

    public GridAsDiscreteGlobalGridResource(DiscreteGlobalGridReferenceSystem dggrs, GridCoverageResource resource)
            throws DataStoreException, IncommensurableException, TransformException {
        super(null);
        if (resource.getGridGeometry().getDimension() != 2) {
            throw new IllegalArgumentException("Only 2D coverage resources are supported. Use GridAsReferencedGridResource for N dimensions.");
        }
        this.source = resource;
        this.gridGeometry = new DiscreteGlobalGridGeometry(dggrs, null, null);

        Quantity<?> res = computeAverageResolution(resource.getGridGeometry());
        DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        coder.setPrecision(res, null);
        maxLevel = coder.getPrecisionLevel();
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return source.getIdentifier();
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
    public FeatureType getSampleType() throws DataStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(source.getIdentifier().get());
        CodedCoverageAsFeatureSet.toFeatureType(ftb, getSampleDimensions());
        return ftb.build();
    }

    @Override
    public DiscreteGlobalGridGeometry getGridGeometry() {
        return gridGeometry;
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
    public DiscreteGlobalGridCoverage read(CodedGeometry grid, int... range) throws DataStoreException {
        final DiscreteGlobalGridGeometry geometry = DiscreteGlobalGridResource.toDiscreteGlobalGridGeometry(grid);
        final DiscreteGlobalGridCoverageProcessor processor = new DiscreteGlobalGridCoverageProcessor();
        try {
            return processor.resample(source, geometry, range);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * compute average resolution of a grid geometry
     */
    public static Quantity<?> computeAverageResolution(GridGeometry domain) throws TransformException {
        final SingleCRS horizontalCrs = CRS.getHorizontalComponent(domain.getCoordinateReferenceSystem());
        //todo we should slice the domain to horizontal crs to be sure we have the resolution at index 0
        //we use 1/100 of the resolution as distance to avoid problems with pole to pole coverages with very low resolution
        final double resScale = 100;
        final double resolution = domain.getResolution(true)[0] / resScale;
        final Envelope envelope = domain.getEnvelope(horizontalCrs);
        final DirectPosition start = GeneralEnvelope.castOrCopy(envelope).getMedian();
        final DirectPosition end = new DirectPosition2D(start.getCoordinateReferenceSystem());
        end.setCoordinate(0, start.getCoordinate(0) + resolution);
        end.setCoordinate(1, start.getCoordinate(1));
        final GeodeticCalculator calculator = GeodeticCalculator.create(horizontalCrs);
        calculator.setStartPoint(start);
        calculator.setEndPoint(end);
        final double distance = calculator.getGeodesicDistance();
        return Quantities.create(distance * resScale, Units.METRE);
    }

}
