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
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.geometry.wrapper.jts.JTS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.CodedResource;
import org.geotoolkit.storage.rs.internal.shared.FeatureCodedCoverage;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureSetAsDiscreteGlobalGridResource extends AbstractResource implements CodedResource {

    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final DiscreteGlobalGridGeometry gridGeometry;
    private final FeatureSet featureSet;

    //caches
    private List<String> attributeNames;
    private List<SampleDimension> sampleDimensions;
    private FeatureType sampleType;

    public FeatureSetAsDiscreteGlobalGridResource(DiscreteGlobalGridReferenceSystem dggrs, FeatureSet featureSet) {
        super(null);
        this.dggrs = dggrs;
        this.gridGeometry = new DiscreteGlobalGridGeometry(dggrs, null, null);
        this.featureSet = featureSet;
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
    public FeatureCodedCoverage read(CodedGeometry grid, int... range) throws DataStoreException {
        init();

        final DiscreteGlobalGridGeometry geometry = DiscreteGlobalGridResource.toDiscreteGlobalGridGeometry(grid);
        final List<Object> zoneIds = geometry.getZoneIds();
        final FeatureType sampleType = getSampleType();

        final List<Feature> samples = new ArrayList<>();
        for (int i = 0, n = zoneIds.size(); i < n; i++) {
            samples.add(sampleType.newInstance());
        }

        final FeatureCodedCoverage coverage;
        try {
            coverage = new FeatureCodedCoverage(featureSet.getIdentifier().get(), geometry, samples);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        final Envelope env = coverage.getEnvelope().get();

        //read only the features needed
        final List<FeatureQuery.NamedExpression> projections = new ArrayList<>(1 + sampleDimensions.size());
        final FilterFactory<Feature, Object, Object> ff = DefaultFilterFactory.forFeatures();
        // todo FIX SIS !!!!!!
        projections.add(new FeatureQuery.NamedExpression(ff.property(AttributeConvention.GEOMETRY), AttributeConvention.GEOMETRY));
        //projections.add(new FeatureQuery.NamedExpression(ff.function("ST_Transform", ff.property(AttributeConvention.GEOMETRY), ff.literal(dggrs.getGridSystem().getCrs())), AttributeConvention.GEOMETRY_PROPERTY));
        attributeNames.forEach((n) -> projections.add(new FeatureQuery.NamedExpression(ff.property(n), n)));
        final FeatureQuery query = new FeatureQuery();
        //query.setSelection(env); fix sis !!!!!!!!!!!, broken
        query.setProjection(projections.toArray(FeatureQuery.NamedExpression[]::new));
        final FeatureSet subset = featureSet.subset(query);

        //todo : this approach is very basic, just as a proof of concept
        final DiscreteGlobalGridGeometry gridGeometry = (DiscreteGlobalGridGeometry) coverage.getGeometry();
        final DiscreteGlobalGridReferenceSystem.Coder coder = gridGeometry.getReferenceSystem().createCoder();

        //prepare all zones as geometries
        //todo create a quadtree
        final Quadtree tree = new Quadtree();
        final int nbZones = zoneIds.size();
        IntStream.range(0, nbZones).parallel().forEach(new IntConsumer() {
            @Override
            public void accept(int i) {
                final Zone zone;
                synchronized (coder) {
                    try {
                        zone = coder.decode(zoneIds.get(i));
                    } catch (TransformException ex) {
                        throw new BackingStoreException(ex);
                    }
                }
                final Polygon zoneGeom = DiscreteGlobalGridSystems.toJTSPolygon(zone.getGeographicExtent());
                final org.locationtech.jts.geom.Envelope zenv = zoneGeom.getEnvelopeInternal();
                synchronized(tree) {
                    tree.insert(zenv, new Object[]{zenv, zoneGeom, i});
                }
            }
        });

        MathTransform trs;
        try { //todo remove when SIS bug is fixed
            final CoordinateReferenceSystem srcCrs = FeatureExt.getCRS(featureSet.getType());
            final CoordinateReferenceSystem targetCrs = dggrs.getGridSystem().getCrs();
            if (!Utilities.equalsIgnoreMetadata(srcCrs, targetCrs)) {
                trs = CRS.findOperation(srcCrs, targetCrs, null).getMathTransform();
            } else {
                trs = null;
            }

        } catch (OperationNotFoundException ex){
            //todo FIX SIS !
            trs = null;
        } catch (FactoryException ex) {
            //SIS bug, use identity for now
            throw new DataStoreException(ex.getMessage(), ex);
        }

        try (Stream<Feature> stream = subset.features(false)) {
            final Iterator<Feature> fite = stream.iterator();
            zoneLoop:
            while (fite.hasNext()) {
                final Feature feature = fite.next();
                org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) feature.getPropertyValue(AttributeConvention.GEOMETRY);
                org.locationtech.jts.geom.Envelope geomEnv = geom.getEnvelopeInternal();

                if (true && trs != null) { //waiting for fix in SIS
                    geom = JTS.transform(geom, trs);
                    geomEnv = geom.getEnvelopeInternal();
                }

                final List<Object[]> candidates = tree.query(geomEnv);
                for (Object[] entry : candidates) {
                    if (((Geometry)entry[1]).intersects(geom)) {
                        final Feature sample = samples.get((Integer) entry[2]);
                        for (String attName : attributeNames) {
                            sample.setPropertyValue(attName, feature.getPropertyValue(attName));
                        }

                        //remove this zone from futur searchs
                        tree.remove((org.locationtech.jts.geom.Envelope) entry[0], entry[1]);
                        //continue zoneLoop; // a feature may intersect multiple zones
                    }
                }
            }
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        return coverage;
    }

}
