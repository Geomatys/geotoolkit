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
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
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
import org.opengis.referencing.operation.TransformException;

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
    private List<String> attributeNames;
    private List<SampleDimension> sampleDimensions;
    private FeatureType sampleType;

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

    public FeatureSet getOrigin() {
        return featureSet;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        init();
        return Collections.unmodifiableList(sampleDimensions);
    }

    @Override
    public FeatureType getSampleType() throws DataStoreException {
        init();
        return sampleType;
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
        attributeNames = new ArrayList<>();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(type.getName());
        for (PropertyType pt : type.getProperties(true)) {
            if (AttributeConvention.contains(pt.getName())) continue;
            if (!(pt instanceof AttributeType at)) continue;
            final Class valueClass = at.getValueClass();
            if (Geometry.class.isAssignableFrom(valueClass)) continue;
            ftb.addAttribute(at);

            //build matching sample dimensions
            attributeNames.add(pt.getName().toString());

            final SampleDimension.Builder sdb = new SampleDimension.Builder();
            sdb.setName(at.getName());
            final SampleDimension sd = sdb.build();
            sampleDimensions.add(sd);
        }
        sampleType = ftb.build();
    }

    @Override
    public DiscreteGlobalGridCoverage read(CodedGeometry grid, int... range) throws DataStoreException {
        init();
        final DiscreteGlobalGridGeometry geometry = DiscreteGlobalGridResource.toDiscreteGlobalGridGeometry(grid);
        try {
            return processor.resample(featureSet, geometry, sampleDimensions);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

}
