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
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.LinearRing;
import org.apache.sis.geometries.PointSequence;
import org.apache.sis.geometries.math.Tuple;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.Zone;
import org.geotoolkit.storage.dggs.ZoneIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;

/**
 * View a DGGS Coverage as a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridCoverageAsFeatureSet extends AbstractFeatureSet {

    private static final GeometryFactory GF = new GeometryFactory();
    private final DiscreteGlobalGridCoverage coverage;
    private final FeatureType type;
    private final String[] sampleNames;

    public DiscreteGlobalGridCoverageAsFeatureSet(DiscreteGlobalGridCoverage coverage) {
        super(null);
        this.coverage = coverage;

        final List<SampleDimension> sampleDimensions = coverage.getSampleDimensions();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("dggrs");
        ftb.addAttribute(String.class).setName("zone").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(Geometry.class).setName("geometry").setCRS(coverage.getCoordinateReferenceSystem()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        sampleNames = new String[sampleDimensions.size()];
        for (int i = 0; i < sampleNames.length; i++) {
            sampleNames[i] = sampleDimensions.get(i).getName().toString();
            ftb.addAttribute(Double.class).setName(sampleNames[i]);
        }
        type = ftb.build();
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final ZoneIterator iterator = coverage.createIterator();

        //TODO make a stream
        final List<Feature> features = new ArrayList<>();
        double[] dest = null;
        try {
            while (iterator.next()) {
                Zone zone = iterator.getZone();
                org.apache.sis.geometries.Polygon geom = (org.apache.sis.geometries.Polygon) zone.getGeometry();
                dest = iterator.getCell(dest);

                final LinearRing ring = (LinearRing) geom.getExteriorRing();
                final PointSequence ps = ring.getPoints();
                final Coordinate[] shell = new Coordinate[ps.size()];
                for (int i = 0; i <shell.length; i++) {
                    Tuple position = ps.getPosition(i);
                    shell[i] = new CoordinateXY(position.get(0), position.get(1));
                }

                Polygon p = GF.createPolygon(shell);

                final Feature feature = type.newInstance();
                feature.setPropertyValue("zone", zone.getGeographicIdentifier().toString());
                feature.setPropertyValue("geometry", p);
                for (int i = 0; i < sampleNames.length; i++) {
                    feature.setPropertyValue(sampleNames[i], dest[i]);
                }

                features.add(feature);
            }
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        return parallel ? features.parallelStream(): features.stream();
    }

}
