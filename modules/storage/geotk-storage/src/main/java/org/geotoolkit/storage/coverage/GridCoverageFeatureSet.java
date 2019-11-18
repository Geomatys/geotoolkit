/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.AbstractAssociation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.geotoolkit.internal.feature.TypeConventions;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.InvalidPropertyValueException;
import org.opengis.feature.MultiValuedPropertyException;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Decorate a GridCoverageResource as a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridCoverageFeatureSet extends AbstractResource implements FeatureSet {

    private final org.apache.sis.storage.GridCoverageResource gcr;

    public GridCoverageFeatureSet(org.apache.sis.storage.GridCoverageResource gcr) throws DataStoreException {
        identifier = NamedIdentifier.castOrCopy(gcr.getIdentifier().orElse(null));
        this.gcr = gcr;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.ofNullable(gcr.getGridGeometry().getEnvelope());
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return createCoverageType(gcr);
    }

    @Override
    public Stream<Feature> features(boolean parallal) throws DataStoreException {
        final FeatureType type = getType();
        final FeatureAssociationRole role = (FeatureAssociationRole) type.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final Feature feature = type.newInstance();

        try {
            final GridGeometry gridGeom = gcr.getGridGeometry();
            Envelope envelope = gridGeom.getEnvelope();
            if (envelope != null) {
                Geometry geom = GeometricUtilities.toJTSGeometry(envelope, GeometricUtilities.WrapResolution.SPLIT);
                if (geom != null) {
                    JTS.setCRS(geom, gridGeom.getCoordinateReferenceSystem());
                    feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), geom);
                }
            }
        } catch (CoverageStoreException ex) {
            throw ex;
        }
        feature.setProperty(coverageRecords(gcr, role));
        return Stream.of(feature);
    }

    public static FeatureType createCoverageType(GridCoverage coverage) throws DataStoreException {
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(coverage.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_TYPE);
        ftb.setName(coverage instanceof org.geotoolkit.coverage.grid.GridCoverage ? ((org.geotoolkit.coverage.grid.GridCoverage) coverage).getName() : "Coverage");
        ftb.addAttribute(Polygon.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(createRecordType(coverage)).setName(TypeConventions.RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);

        return ftb.build();
    }

    public static FeatureType createCoverageType(org.apache.sis.storage.GridCoverageResource resource) throws DataStoreException {

        final GridGeometry gridGeometry = resource.getGridGeometry();
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(gridGeometry.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_TYPE);
        ftb.setName(resource.getIdentifier().orElse(null));
        ftb.addAttribute(Polygon.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(createRecordType(resource)).setName(TypeConventions.RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);

        return ftb.build();
    }

    public static FeatureType createRecordType(GridCoverage coverage) throws DataStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_RECORD_TYPE);
        ftb.setName((coverage instanceof org.geotoolkit.coverage.grid.GridCoverage ? ((org.geotoolkit.coverage.grid.GridCoverage) coverage).getName() : "") + "Record" );
        final CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(crs);
        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs2d).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);

        //if the CRS has more the 2 dimensions, we convert the envelope operation
        //to an attribute, the envelope will be N dimesion, and the geometry 2D
        if (crs.getCoordinateSystem().getDimension() > 2) {
            ftb.addAttribute(Envelope.class).setName(AttributeConvention.ENVELOPE_PROPERTY).setCRS(crs);
        }

        //use existing sample dimensions
        final List<SampleDimension> dims = coverage.getSampleDimensions();
        if (dims != null && !dims.isEmpty()) {
            for (int i=0,n=dims.size();i<n;i++) {
                final SampleDimension gsd = dims.get(i);
                final String name = gsd.getName() == null ? ""+i : gsd.getName().toString();
                ftb.addAttribute(Double.class).setName(name).setMinimumOccurs(1).setMaximumOccurs(1);
            }
            return ftb.build();
        }

        //in case of Nd Coverage unstack them.
        while (coverage instanceof GridCoverageStack) {
            coverage = ((GridCoverageStack) coverage).coverageAtIndex(0);
        }

        final int nbBand = coverage.render(null).getSampleModel().getNumBands();
        for (int i=0;i<nbBand;i++) {
            ftb.addAttribute(Double.class).setName(""+i).setMinimumOccurs(1).setMaximumOccurs(1);
        }
        return ftb.build();

    }

    public static FeatureType createRecordType(org.apache.sis.storage.GridCoverageResource resource) throws DataStoreException {

        final GridGeometry gridGeometry = resource.getGridGeometry();
        final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(crs);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_RECORD_TYPE);
        ftb.setName(resource.getIdentifier().get().tip().toString()+"Record");
        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs2d).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);

        //if the CRS has more the 2 dimensions, we convert the envelope operation
        //to an attribute, the envelope will be N dimesion, and the geometry 2D
        if (crs.getCoordinateSystem().getDimension() > 2) {
            ftb.addAttribute(Envelope.class).setName(AttributeConvention.ENVELOPE_PROPERTY).setCRS(crs);
        }

        //use existing sample dimensions
        final List<SampleDimension> samples = resource.getSampleDimensions();
        if (samples!=null) {
            for (int i=0,n=samples.size();i<n;i++) {
                final SampleDimension gsd = samples.get(i);
                final String name = gsd.getName() == null ? ""+i : gsd.getName().toString();
                ftb.addAttribute(Double.class).setName(name).setMinimumOccurs(1).setMaximumOccurs(1);
            }
            return ftb.build();
        }

        //extract dimensions from metadatas
        final Metadata metadata = resource.getMetadata();
        if (metadata != null && !metadata.getContentInfo().isEmpty()) {
            final ContentInformation contentInfo = metadata.getContentInfo().iterator().next();
            if (contentInfo instanceof CoverageDescription) {
                final Collection<? extends AttributeGroup> groups = ((CoverageDescription)contentInfo).getAttributeGroups();
                if (!groups.isEmpty()) {
                    final AttributeGroup attGroup = groups.iterator().next();
                    final Collection<? extends RangeDimension> sampleDims = attGroup.getAttributes();
                    if (!sampleDims.isEmpty()) {
                        final Iterator<? extends RangeDimension> ite = sampleDims.iterator();
                        int i=0;
                        while (ite.hasNext()) {
                            final RangeDimension rd = ite.next();
                            String name;
                            if (rd.getSequenceIdentifier()!=null) {
                                name = rd.getSequenceIdentifier().toString();
                            } else if (!rd.getNames().isEmpty()) {
                                name = rd.getNames().iterator().next().getCode();
                            } else {
                                name = ""+i;
                            }
                            ftb.addAttribute(Double.class).setName(name).setMinimumOccurs(1).setMaximumOccurs(1);
                            i++;
                        }
                        return ftb.build();
                    }
                }
            }
        }

        //read a single pixel coverage
        final GridGeometry gridGeom = resource.getGridGeometry();
        final GridExtent extent = gridGeom.getExtent();
        final int[] subsampling = new int[extent.getDimension()];
        for (int i=0; i<subsampling.length; i++) {
            subsampling[i] = (int) extent.getSize(i);
        }

        GridCoverage coverage = resource.read(gridGeom.derive().subsample(subsampling).build());

        //in case of Nd Coverage unstack them.
        while (coverage instanceof GridCoverageStack) {
            coverage = ((GridCoverageStack) coverage).coverageAtIndex(0);
        }

        final int nbBand = coverage.render(null).getSampleModel().getNumBands();
        for (int i=0;i<nbBand;i++) {
            ftb.addAttribute(Double.class).setName(""+i).setMinimumOccurs(1).setMaximumOccurs(1);
        }
        return ftb.build();

    }

    public static FeatureAssociation coverageRecords(final org.apache.sis.storage.GridCoverageResource res, final FeatureAssociationRole role) {

        final Collection<Feature> pixels = new AbstractCollection<Feature>() {

            int count = -1;

            @Override
            public Iterator<Feature> iterator() {
                final GridCoverage cov;
                try {
                    cov = res.read(null);
                } catch (DataStoreException ex) {
                    throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
                }
                return create(role.getValueType(), cov);
            }

            @Override
            public synchronized int size() {
                if (count==-1) {
                    try {
                        final GridGeometry gg = res.getGridGeometry();
                        final GridExtent extent = gg.getExtent();
                        final int dimension = extent.getDimension();
                        long size = extent.getSize(0);
                        for (int i=1;i<dimension;i++) {
                            size *= extent.getSize(i);
                        }
                        count = (int) size;
                    } catch (DataStoreException ex) {
                        throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
                    }
                }
                return count;
            }
        };

        return new AbstractAssociation(role) {
            @Override
            public Collection<Feature> getValues() {
                return pixels;
            }

            @Override
            public void setValues(Collection<? extends Feature> values) throws InvalidPropertyValueException {
                throw new InvalidPropertyValueException("Property is unmodifiable.");
            }

            @Override
            public Feature getValue() throws MultiValuedPropertyException {
                throw new MultiValuedPropertyException();
            }

            @Override
            public void setValue(Feature value) throws InvalidPropertyValueException {
                throw new InvalidPropertyValueException("Property is unmodifiable.");
            }
        };

    }

    public static FeatureAssociation coverageRecords(final GridCoverage coverage, final FeatureAssociationRole role) {

        final FeatureType recordType = role.getValueType();

        final GridGeometry gg = coverage.getGridGeometry();
        final GridExtent extent = gg.getExtent();
        final int dimension = extent.getDimension();
        long size = extent.getSize(0);
        for (int i=1;i<dimension;i++) {
            size *= extent.getSize(i);
        }
        final int count = (int) size;

        final Collection<Feature> pixels = new AbstractCollection<Feature>() {
            @Override
            public Iterator<Feature> iterator() {
                return create(recordType, coverage);
            }

            @Override
            public int size() {
                return count;
            }
        };

        return new AbstractAssociation(role) {
            @Override
            public Collection<Feature> getValues() {
                return pixels;
            }

            @Override
            public void setValues(Collection<? extends Feature> values) throws InvalidPropertyValueException {
                throw new InvalidPropertyValueException("Property is unmodifiable.");
            }

            @Override
            public Feature getValue() throws MultiValuedPropertyException {
                throw new MultiValuedPropertyException();
            }

            @Override
            public void setValue(Feature value) throws InvalidPropertyValueException {
                throw new InvalidPropertyValueException("Property is unmodifiable.");
            }
        };

    }

    private static Iterator<Feature> create(FeatureType recordType, GridCoverage coverage) {
        if (coverage instanceof GridCoverage2D) {
            return new GridCoverage2DRecordIterator(recordType, (GridCoverage2D) coverage);
        } else if (coverage instanceof GridCoverageStack) {
            return new GridCoverageRecordIterator(recordType, (GridCoverageStack) coverage);
        } else {
            throw new UnsupportedOperationException("Unsupported coverage type "+coverage.getClass().getName());
        }
    }

    private static class GridCoverageRecordIterator implements Iterator<Feature> {

        private final FeatureType recordType;
        private final Iterator<GridCoverage> coverageIte;
        private Iterator<Feature> subIte;
        private Feature next = null;


        private GridCoverageRecordIterator(FeatureType recordType, GridCoverageStack coverage) {
            this.recordType = recordType;
            final int nb = coverage.getStackSize();
            final List<GridCoverage> stack = new ArrayList<>(nb);
            for (int i=0;i<nb;i++) {
                stack.add((GridCoverage) coverage.coverageAtIndex(i));
            }
            this.coverageIte = stack.iterator();
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public Feature next() {
            findNext();
            if(next == null){
                throw new NoSuchElementException("No more features.");
            }
            final Feature candidate = next;
            next = null;
            return candidate;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void findNext(){
            if (next != null) return;

            while (next==null) {
                if (subIte==null) {
                    if (coverageIte.hasNext()) {
                        subIte = create(recordType, coverageIte.next());
                    } else {
                        break;
                    }
                } else if(subIte.hasNext()) {
                    next = subIte.next();
                } else {
                    subIte = null;
                }
            }
        }

    }

    private static class GridCoverage2DRecordIterator implements Iterator<Feature> {

        private final GeometryFactory GF = new GeometryFactory();
        private final FeatureType recordType;
        private final GridCoverage2D coverage;
        private final String[] properties;
        private final PixelIterator pixelIterator;
        private final MathTransform gridToCrs2D;
        private final CoordinateReferenceSystem crs;
        private final CoordinateReferenceSystem crs2D;
        private final Envelope envelope;
        private boolean iteNext;
        private Feature next = null;

        private GridCoverage2DRecordIterator(FeatureType recordType, GridCoverage2D coverage) {
            this.recordType = recordType;
            this.coverage = coverage.forConvertedValues(true);
            this.pixelIterator = new PixelIterator.Builder().create(this.coverage.getRenderedImage());
            final GridGeometry2D gridGeometry = this.coverage.getGridGeometry();
            this.gridToCrs2D = gridGeometry.getGridToCRS2D(PixelOrientation.LOWER_LEFT);
            this.envelope = gridGeometry.getEnvelope();
            this.crs = gridGeometry.getCoordinateReferenceSystem();
            this.crs2D = gridGeometry.getCoordinateReferenceSystem2D();

            //list properties
            final List<String> properties = new ArrayList<>();
            for (PropertyType pt : recordType.getProperties(true)) {
                if (pt instanceof AttributeType && !AttributeConvention.contains(pt.getName())) {
                    properties.add(pt.getName().toString());
                }
            }
            this.properties = properties.toArray(new String[properties.size()]);

        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public Feature next() {
            findNext();
            if(next == null){
                throw new NoSuchElementException("No more features.");
            }
            final Feature candidate = next;
            next = null;
            return candidate;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void findNext(){
            if(next != null) return;

            while (next == null && pixelIterator.next()) {
                next = recordType.newInstance();
                //build geometry
                final int x = pixelIterator.getPosition().x;
                final int y = pixelIterator.getPosition().y;

                final double[] poly = new double[]{
                    x  ,y,
                    x+1,y,
                    x+1,y+1,
                    x  ,y+1,
                    x  ,y
                };
                try {
                    gridToCrs2D.transform(poly, 0, poly, 0, 5);
                } catch (TransformException ex) {
                    ex.printStackTrace();
                }
                final Polygon geom = GF.createPolygon(new LiteCoordinateSequence(poly));
                JTS.setCRS(geom, crs2D);

                next.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), geom);
                //read sample values
                for (int i=0;i<properties.length;i++) {
                    next.setPropertyValue(properties[i], pixelIterator.getSampleDouble(i));
                }

                //if the CRS has more the 2 dimensions, we convert the envelope operation
                //has been converted to an attribute, the envelope will be N dimesion, and the geometry 2D
                if (envelope.getDimension() > 2) {
                    final GeneralEnvelope env = new GeneralEnvelope(envelope);
                    final org.locationtech.jts.geom.Envelope jtsEnv = geom.getEnvelopeInternal();
                    env.setRange(0, jtsEnv.getMinX(), jtsEnv.getMaxX());
                    env.setRange(1, jtsEnv.getMinY(), jtsEnv.getMaxY());
                    next.setPropertyValue(AttributeConvention.ENVELOPE_PROPERTY.toString(), env);
                }
            }
        }

    }

}
