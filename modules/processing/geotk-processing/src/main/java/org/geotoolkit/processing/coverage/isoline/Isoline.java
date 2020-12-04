/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2020, Geomatys
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
package org.geotoolkit.processing.coverage.isoline;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.isoline.IsolineDescriptor.*;
import org.geotoolkit.processing.image.MarchingSquares;
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.storage.memory.InMemoryStore;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * Compute isoline contour using Marshing square algorithm.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class Isoline extends AbstractProcess {

    private CoordinateReferenceSystem crs;
    private MathTransform gridToCRS;
    private FeatureType type;
    private WritableFeatureSet col;
    private double[] intervals;

    public Isoline(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final GridCoverageResource resource = inputParameters.getValue(COVERAGE_REF);
        DataStore featureStore = inputParameters.getValue(FEATURE_STORE);
        final String featureTypeName = inputParameters.getValue(FEATURE_NAME);
        intervals = inputParameters.getValue(INTERVALS);

        if (featureStore == null) {
            featureStore = new InMemoryStore();
        }

        try {
            final List<Feature> batches = new ArrayList<Feature>() {
                @Override
                public synchronized boolean addAll(Collection<? extends Feature> c) {
                    boolean b = super.addAll(c);
                    if (super.size() > 200) {
                        try {
                            col.add(super.iterator());
                        } catch (DataStoreException ex) {
                            throw new BackingStoreException(ex);
                        }
                        clear();
                    }
                    return b;
                }
            };

            final GridCoverage coverage = resource.read(null);
            final RenderedImage image = coverage.render(null);
            final GridGeometry gridgeom = coverage.getGridGeometry();
            gridToCRS = getImageToCrs(gridgeom, image);
            crs = gridgeom.getCoordinateReferenceSystem();
            type = getOrCreateIsoType(featureStore, featureTypeName, crs);
            col = (WritableFeatureSet) featureStore.findResource(type.getName().toString());

            final Stream<Rectangle> stream = BufferedImages.tileStream(image, 1, 1, 0, 0);
            stream.map(new Function<Rectangle, List<Feature>>() {
                @Override
                public List<Feature> apply(Rectangle r) {
                    final List<Feature> features = new ArrayList<>();
                    for (double threshold : intervals) {
                        try {
                            for (Feature f : build(image, r, threshold, crs)) {
                                features.add(f);
                            }
                        } catch (MismatchedDimensionException | TransformException ex) {
                            throw new BackingStoreException(ex.getMessage(), ex);
                        }
                    }
                    return features;
            }
            }).parallel().forEach(batches::addAll);

            if (!batches.isEmpty()) {
                col.add(batches.iterator());
            }
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        outputParameters.parameter("outFeatureCollection").setValue(col);
    }

    private List<Feature> build(RenderedImage image, Rectangle rectangle, double threshold, CoordinateReferenceSystem crs) throws MismatchedDimensionException, TransformException {
        final PixelIterator ite = new PixelIterator.Builder().setRegionOfInterest(rectangle).create(image);
        MultiLineString geom = MarchingSquares.build(ite, threshold, 0, true);
        if (geom == null) {
            return Collections.EMPTY_LIST;
        }

        final List<Feature> features = new ArrayList<>(geom.getNumGeometries());
        for (int i = 0; i < geom.getNumGeometries(); i++) {
            final Geometry subgeom = JTS.transform(geom.getGeometryN(i), gridToCRS);
            subgeom.setUserData(crs);
            final Feature feature = type.newInstance();
            feature.setPropertyValue(AttributeConvention.GEOMETRY, subgeom);
            feature.setPropertyValue("value", threshold);
            features.add(feature);
        }

        return features;
    }

    private static FeatureType getOrCreateIsoType(DataStore featureStore, String featureTypeName, CoordinateReferenceSystem crs) throws DataStoreException {
        FeatureType type = buildIsolineFeatureType(featureTypeName,crs);

        //create FeatureType in FeatureStore if not exist
        FeatureSet resource;
        try {
            resource = (FeatureSet) featureStore.findResource(type.getName().toString());
            //will cause an IllegalNameException if not exist
        } catch (DataStoreException ex) {
            resource = (FeatureSet) ((WritableAggregate) featureStore).add(new DefiningFeatureSet(type, null));
        }
        return resource.getType();
    }

    /**
     * Build contour FeatureType.
     *
     * @param featureTypeName
     * @return
     * @throws DataStoreException
     */
    public static FeatureType buildIsolineFeatureType(String featureTypeName,CoordinateReferenceSystem crs) throws DataStoreException {
        //FeatureType with scale
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(featureTypeName != null ? featureTypeName : "contour");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(LineString.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Double.class).setName("value");
        return ftb.build();
    }

    /**
     * TODO, GridCoverage must define a reliable way to obtain the image to crs transform.
     */
    private static MathTransform getImageToCrs(GridGeometry source, RenderedImage sourceImg) {
        final long[] sourceCorner = source.getExtent().getLow().getCoordinateValues();
        final MathTransform sourceOffset = MathTransforms.translation((sourceCorner[0] - sourceImg.getMinX()), (sourceCorner[1] - sourceImg.getMinY()));
        final MathTransform gridSourceToCrs = source.getGridToCRS(PixelInCell.CELL_CENTER);
        return MathTransforms.concatenate(sourceOffset, gridSourceToCrs);
    }
}
