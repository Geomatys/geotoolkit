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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.geometries.LinearRing;
import org.apache.sis.geometries.PointSequence;
import org.apache.sis.geometries.Polygon;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.Tuple;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.NoSuchDataException;
import org.geotoolkit.storage.dggs.ArrayDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.WritableZoneIterator;
import org.geotoolkit.storage.dggs.ZonalIdentifier;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureSetAsDiscreteGlobalGridResource extends AbstractResource implements DiscreteGlobalGridResource {

    private static final GeometryFactory GF = new GeometryFactory();
    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final FeatureSet featureSet;

    //caches
    private List<String> attributeNames;
    private List<SampleDimension> sampleDimensions;

    public FeatureSetAsDiscreteGlobalGridResource(DiscreteGlobalGridReferenceSystem dggrs, FeatureSet featureSet) {
        super(null);
        this.dggrs = dggrs;
        this.featureSet = featureSet;
    }

    @Override
    public DiscreteGlobalGridReferenceSystem getGridReferenceSystem() {
        return dggrs;
    }

    @Override
    public NumberRange<Integer> getAvailableDepths() {
        return NumberRange.create(0, true, dggrs.getGridSystem().getHierarchy().getGrids().size(), false);
    }

    @Override
    public int getDefaultDepth() {
        return 0;
    }

    @Override
    public int getMaxRelativeDepth() {
        return 10;
    }

    private synchronized void init() throws DataStoreException {
        if (sampleDimensions != null) return;

        final FeatureType type = featureSet.getType();
        sampleDimensions = new ArrayList<>();
        attributeNames = new ArrayList<>();
        for (PropertyType pt : type.getProperties(true)) {
            if (pt instanceof AttributeType at) {
                final Class valueClass = at.getValueClass();
                if (Number.class.isAssignableFrom(valueClass)) {
                    attributeNames.add(pt.getName().toString());

                    final SampleDimension.Builder sdb = new SampleDimension.Builder();
                    sdb.setName(at.getName());
                    final SampleDimension sd = sdb.build();
                    sampleDimensions.add(sd);
                }
            }
        }
    }

    @Override
    public DiscreteGlobalGridCoverage read(List<ZonalIdentifier> zones, int... range) throws DataStoreException {
        if (zones.isEmpty()) throw new NoSuchDataException();
        init();

        final DiscreteGlobalGridGeometry geometry = new DiscreteGlobalGridGeometry(dggrs, zones);

        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            final double[] datas = new double[zones.size()];
            Arrays.fill(datas, Double.NaN);
            samples.add(TupleArrays.of(ss, datas));
            nans[i] = Double.NaN;
        }

        final DiscreteGlobalGridCoverage coverage = new ArrayDiscreteGlobalGridCoverage(geometry, samples);
        final Envelope env = coverage.getEnvelope().get();

        //read only the features needed
        final List<FeatureQuery.NamedExpression> projections = new ArrayList<>(1 + sampleDimensions.size());
        final FilterFactory<Feature, Object, Object> ff = DefaultFilterFactory.forFeatures();
        projections.add(new FeatureQuery.NamedExpression(ff.function("ST_Transform", ff.property(AttributeConvention.GEOMETRY), ff.literal(dggrs.getGridSystem().getCrs())), AttributeConvention.GEOMETRY_PROPERTY));
        attributeNames.forEach((n) -> projections.add(new FeatureQuery.NamedExpression(ff.property(n), n)));
        final FeatureQuery query = new FeatureQuery();
        query.setSelection(env);
        query.setProjection(projections.toArray(FeatureQuery.NamedExpression[]::new));
        final FeatureSet subset = featureSet.subset(query);

        //todo : this approach is very basic, just as a proof of concept
        final WritableZoneIterator ite = coverage.createWritableIterator();
        zoneLoop:
        while (ite.next()) {
            try {
                //todo handle geometry libraries correctly
                final Polygon zoneGeom = (Polygon) ite.getZone().getGeometry();
                final LinearRing exterior = (LinearRing) zoneGeom.getExteriorRing();
                final PointSequence ps = exterior.getPoints();
                final Coordinate[] shell = new Coordinate[ps.size()];
                for (int i = 0; i < shell.length; i++) {
                    Tuple position = ps.getPosition(i);
                    shell[i] = new CoordinateXY(position.get(0), position.get(1));
                }

                final org.locationtech.jts.geom.Polygon jtspoly = GF.createPolygon(shell);


                try (Stream<Feature> stream = subset.features(false)) {
                    final Iterator<Feature> fite = stream.iterator();
                    while (fite.hasNext()) {
                        final Feature feature = fite.next();
                        org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) feature.getPropertyValue(AttributeConvention.GEOMETRY);
                        if (jtspoly.intersects(geom)) {
                            final double[] cell = new double[attributeNames.size()];
                            for (int i = 0; i < cell.length; i++) {
                                Object cv = feature.getPropertyValue(attributeNames.get(i));
                                cell[i] = cv instanceof Number ? ((Number)cv).doubleValue() : Double.NaN;
                            }
                            ite.setCell(cell);
                            continue zoneLoop;
                        }
                    }
                }
            } catch (TransformException e) {
                throw new DataStoreException(e.getMessage(), e);
            }
        }

        return coverage;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        init();
        return Collections.unmodifiableList(sampleDimensions);
    }

}
