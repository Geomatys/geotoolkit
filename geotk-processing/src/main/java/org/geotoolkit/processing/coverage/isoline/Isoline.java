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
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.geometry.wrapper.jts.Factory;
import org.apache.sis.image.processing.isoline.Isolines;
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
import org.geotoolkit.util.Streams;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * Compute isoline contour using Marshing square algorithm.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class Isoline extends AbstractProcess {

    public Isoline(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final GridCoverageResource resource = inputParameters.getValue(COVERAGE_REF);
        DataStore featureStore = inputParameters.getValue(FEATURE_STORE);
        final String featureTypeName = inputParameters.getValue(FEATURE_NAME);
        final double[] intervals = inputParameters.getValue(INTERVALS);

        if (featureStore == null) {
            featureStore = new InMemoryStore();
        }

        try {
            final List<SampleDimension> bands = resource.getSampleDimensions();
            // TODO: support multiple bands; Both SIS and Geotk support it, but process interval input is designed for mono-band.
            // Changing it would cause a breaking-change.
            if (bands.size() != 1) throw new ProcessException("Only single banded coverages are supported for now, but input dataset has "+bands.size(), this);

            final GridCoverage coverage = resource.read(null);
            final GridGeometry gridgeom = coverage.getGridGeometry();
            final CoordinateReferenceSystem crs = gridgeom.isDefined(GridGeometry.CRS) ? gridgeom.getCoordinateReferenceSystem() : null;
            final FeatureType type = getOrCreateIsoType(featureStore, featureTypeName, crs);
            final WritableFeatureSet outputDataset = (WritableFeatureSet) featureStore.findResource(type.getName().toString());

            final RenderedImage image = coverage.render(null);
            final MathTransform gridToCRS = getImageToCrs(gridgeom, image);
            final IsolineInput context = new IsolineInput(image, intervals, type, gridToCRS, crs);

            final Stream<IsolineRecord> isolines;
            final String method = inputParameters.getValue(METHOD);
            if (Method.GEOTK_MARCHING_SQUARE.name().equals(method)) isolines = computeMarchingSquareGeotk(context);
            else isolines = computeMarchingSquareSIS(context);

            try {
                Streams.batchExecute(
                        isolines
                                .map(record -> toFeature(record, context)),
                        values -> {
                            try {
                                outputDataset.add(values.iterator());
                            } catch (DataStoreException e) {
                                throw new BackingStoreException(e);
                            }
                        },
                        200);
            } catch (BackingStoreException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof TransformException) throw (TransformException) cause;
                else if (cause instanceof DataStoreException) throw (DataStoreException) cause;
                else throw e;
            }

            outputParameters.getOrCreate(FCOLL).setValue(outputDataset);
        } catch (TransformException ex) {
            throw new ProcessException("Cannot transforms isolines from image space to real(geographic or projected) space", this, ex);
        } catch (DataStoreException ex) {
            throw new ProcessException("Cannot read input data, or cannot interact with output datastore", this, ex);
        }
    }

    private Stream<IsolineRecord> computeMarchingSquareSIS(IsolineInput context) throws TransformException {
        final Isolines[] isolines = Isolines.generate(context.image, new double[][]{context.intervals}, context.imageToIsolineCoordinateTransform);
        final GeometryFactory factory = Factory.INSTANCE.factory(false);
        return Arrays.stream(isolines)
                /* Notes:
                 * 1. Geometry conversion from J2D to JTS use a flatness argument which is not adapted to the dataset.
                 *    However, as isoline proces does not generate any curve (only segments), it should not have any
                 *    impact on conversions.
                 * 2. We expand geometry collections into unitary line-strings / rings, because output feature type
                 *    specifies LineString geometry type.
                 */
                .mapMulti((isoline, sink) -> isoline.polylines().forEach((threshold, shape) -> {
                    final Geometry jtsGeom = JTS.fromAwt(factory, shape, 1);
                    if (jtsGeom instanceof GeometryCollection jtsCol) {
                        for (int i = 0 ; i < jtsCol.getNumGeometries() ; i++) {
                            sink.accept(new IsolineRecord(threshold, jtsCol.getGeometryN(i)));
                        }
                    } else {
                        sink.accept(new IsolineRecord(threshold, jtsGeom));
                    }
                }));
    }

    private Stream<IsolineRecord> computeMarchingSquareGeotk(final IsolineInput input) {
        return BufferedImages.tileStream(input.image, 1, 1, 0, 0)
                .flatMap(tile -> Arrays.stream(input.intervals)
                            .boxed()
                            .flatMap(threshold -> geotkInternalComputationForTile(input, tile, threshold)));
    }

    private Stream<IsolineRecord> geotkInternalComputationForTile(IsolineInput input, final Rectangle roi, final double threshold) {
        final PixelIterator ite = new PixelIterator.Builder().setRegionOfInterest(roi).create(input.image);
        MultiLineString isolines = MarchingSquares.build(ite, threshold, 0, true);
        if (isolines == null || isolines.isEmpty()) return Stream.empty();
        else return IntStream.range(0, isolines.getNumGeometries())
                .mapToObj(idx -> {
                    try {
                        final Geometry geometryN = isolines.getGeometryN(idx);
                        return org.apache.sis.geometry.wrapper.jts.JTS.transform(geometryN, input.imageToIsolineCoordinateTransform);
                    } catch (TransformException e) {
                        throw new BackingStoreException(e);
                    }
                })
                .map(geometry -> new IsolineRecord(threshold, geometry));
    }

    private static Feature toFeature(final IsolineRecord record, IsolineInput context) {
        Geometry geom = record.shape;
        // For retro-compatibility purpose, we force output to be only line strings.
        if (geom instanceof Polygon) geom = ((Polygon) geom).getExteriorRing();
        if (context.outputCrs != null) geom.setUserData(context.outputCrs);

        final Feature feature = context.outputType.newInstance();
        feature.setPropertyValue(AttributeConvention.GEOMETRY, geom);
        feature.setPropertyValue("value", record.threshold);
        return feature;
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
     */
    public static FeatureType buildIsolineFeatureType(String featureTypeName, CoordinateReferenceSystem crs) {
        //FeatureType with scale
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(featureTypeName != null ? featureTypeName : "contour");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        final AttributeTypeBuilder<LineString> lineAttribute = ftb.addAttribute(LineString.class).setName(AttributeConvention.GEOMETRY_PROPERTY);
        if (crs != null) lineAttribute.setCRS(crs);
        lineAttribute.addRole(AttributeRole.DEFAULT_GEOMETRY);
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

    private record IsolineInput(RenderedImage image, double[] intervals, FeatureType outputType, MathTransform imageToIsolineCoordinateTransform, CoordinateReferenceSystem outputCrs) {}
    private record IsolineRecord(double threshold, Geometry shape) {}
}
