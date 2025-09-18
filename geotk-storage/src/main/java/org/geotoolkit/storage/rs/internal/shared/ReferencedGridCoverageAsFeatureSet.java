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
package org.geotoolkit.storage.rs.internal.shared;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.AbstractFeature;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.privy.AbstractIterator;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.rs.ReferencedGridCoverage;
import org.geotoolkit.storage.rs.ReferencedGridGeometry;
import org.geotoolkit.storage.rs.ReferencedGridTransform;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.rs.AddressIterator;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * View a DGGS Coverage as a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReferencedGridCoverageAsFeatureSet extends AbstractFeatureSet {

    /**
     * For vector output formats, retrieve the zone centroid as geometry.
     */
    public static final String GEOMETRY_ZONE_CENTROID = "zone-centroid";
    /**
     * For vector output formats, retrieve the zone region as geometry.
     */
    public static final String GEOMETRY_ZONE_REGION = "zone-region";
    /**
     * For vector output formats, do not return the geometry.
     */
    public static final String GEOMETRY_ZONE_NONE = "none";

    private final String geometryType;
    private final ReferencedGridCoverage coverage;
    private final ReferencedGridGeometry coverageGeometry;
    private final ReferencedGridTransform gridToRs;
    private final ReferenceSystem[] singleRS;
    private DiscreteGlobalGridReferenceSystem horizontal;
    private final FeatureType type;
    private final String[] sampleNames;
    private final boolean idAsLong;
    /**
     * Negative values point to ordinate (with an offset -1), positive to sample dimensions
     */
    private final Map<String,Integer> index = new HashMap();

    /**
     *
     * @param coverage
     * @param idAsLong
     * @param geometryType one of GEOMETRY_X constants
     */
    public ReferencedGridCoverageAsFeatureSet(ReferencedGridCoverage coverage, boolean idAsLong, String geometryType) {
        super(null);
        this.coverage = coverage;
        this.idAsLong = idAsLong;
        coverageGeometry = coverage.getGeometry();
        gridToRs = coverageGeometry.getGridToRS();
        if (geometryType == null) geometryType = GEOMETRY_ZONE_REGION;
        this.geometryType = geometryType;

        singleRS = ReferenceSystems.getSingleComponents(coverageGeometry.getReferenceSystem(), true).toArray(ReferenceSystem[]::new);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("rs");

        //convert location components to properties
        for (int i = 0, n = singleRS.length; i < n ;i++) {
            ReferenceSystem rs  = singleRS[i];
            if (rs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                horizontal = dggrs;
                if (idAsLong) {
                    ftb.addAttribute(Long.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).addRole(AttributeRole.IDENTIFIER_COMPONENT);
                } else {
                    ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).addRole(AttributeRole.IDENTIFIER_COMPONENT);
                }
                index.put(AttributeConvention.IDENTIFIER, -i-1);

                switch (geometryType) {
                    case GEOMETRY_ZONE_REGION :
                        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(coverage.getCoordinateReferenceSystem()).addRole(AttributeRole.DEFAULT_GEOMETRY);
                        index.put(AttributeConvention.GEOMETRY, -i-1);
                        break;
                    case GEOMETRY_ZONE_CENTROID :
                        ftb.addAttribute(Point.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(coverage.getCoordinateReferenceSystem()).addRole(AttributeRole.DEFAULT_GEOMETRY);
                        index.put(AttributeConvention.GEOMETRY, -i-1);
                        break;
                    case GEOMETRY_ZONE_NONE :
                }
            } else {
                String name = rs.getName().toString();
                ftb.addAttribute(Object.class).setName(name);
                index.put(name, -i-1);
            }
        }

        //fill the index
        final FeatureType sampleType = coverage.getSampleType();
        sampleNames = sampleType.getProperties(true).stream().map(PropertyType::getName).map(Objects::toString).toArray(String[]::new);
        for (int i = 0; i < sampleNames.length; i++) {
            index.put(sampleNames[i], i);

            ftb.addAttribute((AttributeType) sampleType.getProperty(sampleNames[i]));
        }

        type = ftb.build();
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final AddressIterator iterator = coverage.createIterator();
        final DiscreteGlobalGridReferenceSystem.Coder coder = horizontal.createCoder();

        final Iterator<AddressIterator> ite = new AbstractIterator() {
            @Override
            public boolean hasNext() {
                if (next != null) return true;
                if (iterator.next()) {
                    next = iterator;
                }
                return next != null;
            }
        };

        final Stream<AddressIterator> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(ite, Spliterator.ORDERED), false);
        return stream.map((AddressIterator t) -> new CellAsFeature(t, coder));
    }

    private class CellAsFeature extends AbstractFeature {

        private final AddressIterator iterator;
        private final DiscreteGlobalGridReferenceSystem.Coder coder;
        private Object[] ordinates;
        private Feature cell;

        public CellAsFeature(AddressIterator iterator, DiscreteGlobalGridReferenceSystem.Coder coder) {
            super(type);
            this.iterator = iterator;
            this.coder = coder;
        }

        private synchronized Object[] getOrdinate() throws TransformException {
            if (ordinates == null) {
                int[] pos = iterator.getPosition();
                ordinates = gridToRs.toAddress(pos).getOrdinates();
            }
            return ordinates;
        }

        private synchronized Feature getCell() {
            if (cell == null) {
                cell = iterator.getSample();
            }
            return cell;
        }

        @Override
        public Object getValueOrFallback(String propName, Object missingPropertyFallback) {
            final Integer idx = index.get(propName);
            if (idx == null) return missingPropertyFallback;
            if (idx < 0) {
                try {
                    Object obj = getOrdinate()[Math.abs(idx+1)];
                    if (AttributeConvention.IDENTIFIER.equals(propName)) {
                        Zone zone = coder.decode(obj);
                        if (idAsLong) {
                            obj = zone.getLongIdentifier();
                        } else {
                            obj = zone.getTextIdentifier().toString();
                        }
                    } else if (AttributeConvention.GEOMETRY.equals(propName)) {
                        Zone zone = coder.decode(obj);
                        switch (geometryType) {
                            case GEOMETRY_ZONE_REGION :
                                obj = DiscreteGlobalGridSystems.toJTSPolygon(zone.getGeographicExtent());
                                break;
                            case GEOMETRY_ZONE_CENTROID :
                                Point pt = DiscreteGlobalGridSystems.toJTSPolygon(zone.getGeographicExtent()).getCentroid();
                                //force 2d, bad habit from JTS to add a NaN Z dimension
                                obj = pt.getFactory().createPoint(new CoordinateXY(pt.getX(), pt.getY()));
                                break;
                            case GEOMETRY_ZONE_NONE :
                                return missingPropertyFallback;
                        }

                    }
                    return obj;
                } catch (TransformException ex) {
                    throw new BackingStoreException(ex);
                }
            } else {
                return getCell().getValueOrFallback(propName, missingPropertyFallback);
            }
        }

        @Override
        public void setPropertyValue(String propName, Object o) throws IllegalArgumentException {
            final Integer idx = index.get(propName);
            if (idx == null) throw new IllegalArgumentException("Property not found or can not be edited");
            if (idx < 0) {
                throw new IllegalArgumentException("Property " + propName + "can not be edited");
            } else {
                getCell().setPropertyValue(propName, o);
            }
        }

        @Override
        public Object getPropertyValue(String propName) throws PropertyNotFoundException {
            if (index.get(propName) == null) throw new PropertyNotFoundException();
            return getValueOrFallback(propName, null);
        }

    }

    /**
     * Convert SampleDimension to feature attributes.
     *
     * @param ftb to append attributes to
     * @param sampleDimensions coverage sample dimensions
     * @return the created attributes
     */
    public static AttributeTypeBuilder<?>[] toFeatureType(FeatureTypeBuilder ftb, List<SampleDimension> sampleDimensions) {
        final AttributeTypeBuilder[] samples = new AttributeTypeBuilder[sampleDimensions.size()];
        for (int i = 0; i < samples.length; i++) {
            final GenericName name = sampleDimensions.get(i).getName();
            samples[i] = ftb.addAttribute(Double.class).setName(name);
        }
        return samples;
    }

}
