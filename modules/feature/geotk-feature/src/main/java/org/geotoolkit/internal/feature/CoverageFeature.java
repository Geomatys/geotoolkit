/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.internal.feature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.sis.feature.AbstractAssociation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
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
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CoverageFeature {

    private CoverageFeature(){}

    public static FeatureType createCoverageType(GridCoverage coverage) throws CoverageStoreException {
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(coverage.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_TYPE);
        ftb.setName(coverage instanceof AbstractCoverage ? ((AbstractCoverage)coverage).getName() : "Coverage");
        ftb.addAttribute(Polygon.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(createRecordType(coverage)).setName(TypeConventions.RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);

        return ftb.build();
    }

    public static FeatureType createCoverageType(GridCoverageReader reader) throws CoverageStoreException {
        final int imageIndex = 0;

        final GeneralGridGeometry gridGeometry = reader.getGridGeometry(imageIndex);
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(gridGeometry.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_TYPE);
        ftb.setName(reader.getCoverageNames().get(imageIndex));
        ftb.addAttribute(Polygon.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(createRecordType(reader)).setName(TypeConventions.RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);



        return ftb.build();
    }

    public static FeatureType createRecordType(Coverage coverage) throws CoverageStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_RECORD_TYPE);
        ftb.setName((coverage instanceof AbstractCoverage ? ((AbstractCoverage)coverage).getName() : "") + "Record" );
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(coverage.getCoordinateReferenceSystem());
        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);

        //use existing sample dimensions
        final int nbDim = coverage.getNumSampleDimensions();
        if (nbDim>0) {
            for (int i=0;i<nbDim;i++) {
                final SampleDimension gsd = coverage.getSampleDimension(i);
                final String name = gsd.getDescription() == null ? ""+i : gsd.getDescription().toString();
                ftb.addAttribute(Double.class).setName(name).setMinimumOccurs(1).setMaximumOccurs(1);
            }
            return ftb.build();
        }

        //in case of Nd Coverage unstack them.
        while (coverage instanceof CoverageStack) {
            coverage = ((CoverageStack)coverage).coverageAtIndex(0);
        }

        if (coverage instanceof GridCoverage2D) {
            final int nbBand = ((GridCoverage2D)coverage).getRenderedImage().getSampleModel().getNumBands();
            for (int i=0;i<nbBand;i++) {
                ftb.addAttribute(Double.class).setName(""+i).setMinimumOccurs(1).setMaximumOccurs(1);
            }
            return ftb.build();
        } else {
            throw new CoverageStoreException("Unsupported coverage type "+coverage);
        }

    }

    public static FeatureType createRecordType(GridCoverageReader reader) throws CoverageStoreException {
        final int imageIndex = 0;

        final GeneralGridGeometry gridGeometry = reader.getGridGeometry(imageIndex);
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(gridGeometry.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_RECORD_TYPE);
        ftb.setName(reader.getCoverageNames().get(imageIndex).tip().toString()+"Record");
        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);

        //use existing sample dimensions
        final List<GridSampleDimension> samples = reader.getSampleDimensions(imageIndex);
        if (samples!=null) {
            for (int i=0,n=samples.size();i<n;i++) {
                final GridSampleDimension gsd = samples.get(i);
                final String name = gsd.getDescription() == null ? ""+i : gsd.getDescription().toString();
                ftb.addAttribute(Double.class).setName(name).setMinimumOccurs(1).setMaximumOccurs(1);
            }
            return ftb.build();
        }

        //extract dimensions from metadatas
        final Metadata metadata = reader.getMetadata();
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
        final GeneralGridGeometry gridGeom = reader.getGridGeometry(imageIndex);
        final Envelope env = gridGeom.getEnvelope();
        final GridEnvelope ext = gridGeom.getExtent();

        final double[] res = new double[ext.getDimension()];
        for(int i=0;i<res.length;i++){
            res[i] = (env.getSpan(i) / ext.getSpan(i));
        }

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(env);
        param.setResolution(res);
        Coverage coverage = reader.read(imageIndex, param);

        //in case of Nd Coverage unstack them.
        while (coverage instanceof CoverageStack) {
            coverage = ((CoverageStack)coverage).coverageAtIndex(0);
        }

        if (coverage instanceof GridCoverage2D) {
            final int nbBand = ((GridCoverage2D)coverage).getRenderedImage().getSampleModel().getNumBands();
            for (int i=0;i<nbBand;i++) {
                ftb.addAttribute(Double.class).setName(""+i).setMinimumOccurs(1).setMaximumOccurs(1);
            }
            return ftb.build();
        } else {
            throw new CoverageStoreException("Unsupported coverage type "+coverage);
        }

    }

    public static FeatureAssociation coverageRecords(final GridCoverage coverage, final FeatureAssociationRole role) {

        final FeatureType recordType = role.getValueType();

        final GridGeometry gg = coverage.getGridGeometry();
        final GridEnvelope extent = gg.getExtent();
        final int dimension = extent.getDimension();
        int size = extent.getSpan(0);
        for (int i=1;i<dimension;i++) {
            size *= extent.getSpan(i);
        }
        final int count = size;

        final Collection<Feature> pixels = new AbstractCollection<Feature>() {
            @Override
            public Iterator<Feature> iterator() {
                final GridCoverage2D cov2d = (GridCoverage2D) coverage;
                return new CoverageRecordIterator(recordType, cov2d);
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

    private static class CoverageRecordIterator implements Iterator<Feature> {

        private final GeometryFactory GF = new GeometryFactory();
        private final FeatureType recordType;
        private final GridCoverage2D coverage;
        private final String[] properties;
        private final PixelIterator pixelIterator;
        private final MathTransform gridToCrs2D;
        private final CoordinateReferenceSystem crs2D;
        private boolean iteNext;
        private Feature next = null;

        private CoverageRecordIterator(FeatureType recordType, GridCoverage2D coverage) {
            this.recordType = recordType;
            this.coverage = coverage.view(ViewType.GEOPHYSICS);
            this.pixelIterator = PixelIteratorFactory.createDefaultIterator(this.coverage.getRenderedImage());
            final GridGeometry2D gridGeometry = this.coverage.getGridGeometry();
            this.gridToCrs2D = gridGeometry.getGridToCRS2D(PixelOrientation.LOWER_LEFT);
            this.crs2D = gridGeometry.getCoordinateReferenceSystem2D();

            //list properties
            final List<String> properties = new ArrayList<>();
            for (PropertyType pt : recordType.getProperties(true)) {
                if (pt instanceof AttributeType && !pt.getName().equals(AttributeConvention.GEOMETRY_PROPERTY)) {
                    properties.add(pt.getName().toString());
                }
            }
            this.properties = properties.toArray(new String[properties.size()]);

            //move to first pixel
            iteNext = pixelIterator.next();
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

            while(next == null && iteNext){
                next = recordType.newInstance();
                //build geometry
                final int x = pixelIterator.getX();
                final int y = pixelIterator.getY();

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
                    next.setPropertyValue(properties[i], pixelIterator.getSampleDouble());
                    iteNext = pixelIterator.next();
                }
            }

        }

    }

}
