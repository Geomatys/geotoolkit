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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.NoSuchDataException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverageProcessor;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureSetAsDiscreteGlobalGridResource extends AbstractResource implements DiscreteGlobalGridResource {

    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final DiscreteGlobalGridGeometry gridGeometry;
    private final DiscreteGlobalGridCoverageProcessor processor;
    private final FeatureSet featureSet;
    private final int defaultDepth;
    private final int maxRelativeDepth;
    private final NumberRange<Integer> availableDepths;

    //caches
    private List<SampleDimension> sampleDimensions;

    public FeatureSetAsDiscreteGlobalGridResource(DiscreteGlobalGridReferenceSystem dggrs, FeatureSet featureSet, DiscreteGlobalGridCoverageProcessor processor) {
        super(null);
        this.dggrs = dggrs;
        this.processor = processor;
        this.gridGeometry = DiscreteGlobalGridGeometry.unstructured(dggrs, null, null);
        this.featureSet = featureSet;
        this.defaultDepth = 3;
        this.maxRelativeDepth = 10;
        this.availableDepths = NumberRange.create(0, true, dggrs.getGridSystem().getHierarchy().getGrids().size()-1, true);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return featureSet.getIdentifier();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return featureSet.getEnvelope();
    }

    @Override
    protected Metadata createMetadata() throws DataStoreException {
        return featureSet.getMetadata();
    }

    public FeatureSet getOrigin() {
        return featureSet;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        init();
        return Collections.unmodifiableList(sampleDimensions);
    }

    @Override
    public DiscreteGlobalGridGeometry getGridGeometry() {
        return gridGeometry;
    }

    @Override
    public int getDefaultDepth() {
        return defaultDepth;
    }

    @Override
    public int getMaxRelativeDepth() {
        return maxRelativeDepth;
    }

    @Override
    public NumberRange<Integer> getAvailableDepths() {
        return availableDepths;
    }

    private synchronized void init() throws DataStoreException {
        if (sampleDimensions != null) return;

        final FeatureType type = featureSet.getType();

        //create a sample type with only attribute types
        sampleDimensions = new ArrayList<>();
        for (PropertyType pt : type.getProperties(true)) {
            if (AttributeConvention.contains(pt.getName())) continue;
            if (!(pt instanceof AttributeType at)) continue;
            final Class valueClass = at.getValueClass();
            if (Geometry.class.isAssignableFrom(valueClass)) continue;

            //build matching sample dimensions
            final SampleDimension.Builder sdb = new SampleDimension.Builder();
            sdb.setName(at.getName());
            final SampleDimension sd = sdb.build();
            sampleDimensions.add(sd);
        }
    }

    @Override
    public DiscreteGlobalGridCoverage read(CodedGeometry grid, int... range) throws DataStoreException {
        init();
        final DiscreteGlobalGridGeometry geometry = DiscreteGlobalGridResource.toDiscreteGlobalGridGeometry(grid);

        final Optional<Envelope> envelope = featureSet.getEnvelope();
        if (!envelope.isEmpty()) {
            Envelope e = envelope.get();
            final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(e.getCoordinateReferenceSystem());
            try {
                e = Envelopes.transform(e, crs2d);
                Envelope gridEnv = geometry.getEnvelope(crs2d);

                if (!GeneralEnvelope.castOrCopy(gridEnv).intersects(e)) {
                    //no data
                    throw new NoSuchDataException();
                }
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        try {
            return processor.resample(featureSet, geometry, sampleDimensions);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

}
