/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.raster.coveragetofeatures;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.internal.image.io.GridDomainAccessor;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.referencing.datum.PixelInCell;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for CoverageToFeature process
 * @author Quentin Boileau
 * @module pending
 */
public class CoverageToFeatureTest {

    private static final int max = 5; /* Define the number of row and columns of the generated coverage */


    /**
     * Test coverageToFeature process with a PixelInCell.CELL_CENTER coverage 
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void coverageToFeatureTestPixelCenter() throws NoSuchAuthorityCodeException, FactoryException, ProcessException {

        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

        PixelInCell pixPos = PixelInCell.CELL_CENTER;
        GridCoverageReader reader = buildReader(pixPos);
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "coveragetofeatures");
        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("reader_in").setValue(reader);
        org.geotoolkit.process.Process proc = desc.createProcess(in);
        
        //Features out
        final Collection<Feature> featureListOut = (Collection<Feature>) proc.call().parameter("feature_out").getValue();

        final List<Feature> featureListResult = (List<Feature>) buildFCResultPixelCenter();


        assertEquals(featureListResult.get(0).getType(), featureListOut.iterator().next().getType());
        assertEquals(featureListOut.size(), featureListResult.size());

        Iterator<Feature> iteratorOut = featureListOut.iterator();
        Iterator<Feature> iteratorResult = featureListResult.iterator();

        ArrayList<Geometry> geomsOut = new ArrayList<Geometry>();
        int itOut = 0;
        while (iteratorOut.hasNext()) {
            Feature featureOut = iteratorOut.next();

            for (Property propertyOut : featureOut.getProperties()) {
                if (propertyOut.getDescriptor() instanceof GeometryDescriptor) {
                    geomsOut.add(itOut++, (Geometry) propertyOut.getValue());
                }
            }
        }
        ArrayList<Geometry> geomsResult = new ArrayList<Geometry>();
        int itResult = 0;
        while (iteratorResult.hasNext()) {
            Feature featureResult = iteratorResult.next();

            for (Property propertyResult : featureResult.getProperties()) {
                if (propertyResult.getDescriptor() instanceof GeometryDescriptor) {
                    geomsResult.add(itResult++, (Geometry) propertyResult.getValue());
                }
            }
        }
        assertEquals(geomsResult.size(), geomsOut.size());
        for (int i = 0; i < geomsResult.size(); i++) {
            Geometry gOut = geomsOut.get(i);
            Geometry gResult = geomsResult.get(i);
            assertArrayEquals(gResult.getCoordinates(), gOut.getCoordinates());
        }
    }

    /**
     * Test coverageToFeature process with a PixelInCell.CELL_CORNER coverage
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void coverageToFeatureTestPixelCorner() throws NoSuchAuthorityCodeException, FactoryException, ProcessException {

        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

        PixelInCell pixPos = PixelInCell.CELL_CORNER;
        GridCoverageReader reader = buildReader(pixPos);
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "coveragetofeatures");
        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("reader_in").setValue(reader);
        org.geotoolkit.process.Process proc = desc.createProcess(in);
        
        //Features out
        final Collection<Feature> featureListOut = (Collection<Feature>) proc.call().parameter("feature_out").getValue();

        final List<Feature> featureListResult = (List<Feature>) buildFCResultPixelCorner();


        assertEquals(featureListResult.get(0).getType(), featureListOut.iterator().next().getType());
        assertEquals(featureListOut.size(), featureListResult.size());

        Iterator<Feature> iteratorOut = featureListOut.iterator();
        Iterator<Feature> iteratorResult = featureListResult.iterator();

        ArrayList<Geometry> geomsOut = new ArrayList<Geometry>();
        int itOut = 0;
        while (iteratorOut.hasNext()) {
            Feature featureOut = iteratorOut.next();

            for (Property propertyOut : featureOut.getProperties()) {
                if (propertyOut.getDescriptor() instanceof GeometryDescriptor) {
                    geomsOut.add(itOut++, (Geometry) propertyOut.getValue());
                }
            }
        }
        ArrayList<Geometry> geomsResult = new ArrayList<Geometry>();
        int itResult = 0;
        while (iteratorResult.hasNext()) {
            Feature featureResult = iteratorResult.next();

            for (Property propertyResult : featureResult.getProperties()) {
                if (propertyResult.getDescriptor() instanceof GeometryDescriptor) {
                    geomsResult.add(itResult++, (Geometry) propertyResult.getValue());
                }
            }
        }
        assertEquals(geomsResult.size(), geomsOut.size());
        for (int i = 0; i < geomsResult.size(); i++) {
            Geometry gOut = geomsOut.get(i);
            Geometry gResult = geomsResult.get(i);
            assertArrayEquals(gResult.getCoordinates(), gOut.getCoordinates());
        }
    }

    private GridCoverageReader buildReader(PixelInCell pixPos) throws NoSuchAuthorityCodeException, FactoryException {

        final BufferedImage image = new BufferedImage(max, max, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < max; y++) {
            for (int x = 0; x < max; x++) {
                int val = x << 8;
                image.setRGB(x, y, val + y);
            }
        }

        final CoordinateReferenceSystem crs2d = CRS.decode("EPSG:3395");
        AffineTransform2D gridToCRS;
        if (pixPos == PixelInCell.CELL_CENTER) {
            gridToCRS = new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5);
        } else {
            gridToCRS = new AffineTransform2D(1, 0, 0, 1, 0.5, 1.5);
        }
        final GridCoverageFactory gc = new GridCoverageFactory();
        final GridCoverage2D coverage = gc.create("cover", image,
                crs2d, gridToCRS, null, null, null);

        SimpleCoverageReader reader = new SimpleCoverageReader(coverage, pixPos);
        return reader;
    }

    private FeatureType buildFeatureType() throws NoSuchAuthorityCodeException, FactoryException {

        final FeatureTypeBuilder typeBuilder = new FeatureTypeBuilder();
        typeBuilder.setName("FeatureCoverage");
        typeBuilder.add("position", Point.class, CRS.decode("EPSG:3395"));
        typeBuilder.add("cellgeom", Polygon.class, CRS.decode("EPSG:3395"));
        typeBuilder.add("orientation", String.class);

        for (int i = 0; i < 3; i++) {
            typeBuilder.add("band-" + i, Integer.class);
        }
        typeBuilder.setDefaultGeometry("position");
        return typeBuilder.buildFeatureType();
    }

    private List<Feature> buildFCResultPixelCenter() throws NoSuchAuthorityCodeException, FactoryException {

        FeatureType type = buildFeatureType();
        final List<Feature> featureList = new ArrayList<Feature>();
        GeometryFactory geometryFactory = new GeometryFactory();

        double buffX = 0;
        double buffY = 0;
        for (int y = 0; y < max; y++) {
            for (int x = 0; x < max; x++) {
                buffX = x + 0.5;
                buffY = y + 0.5;
                Point pos = geometryFactory.createPoint(new Coordinate(buffX, buffY));
                LinearRing line = geometryFactory.createLinearRing(new Coordinate[]{
                            new Coordinate(buffX - 0.5, buffY + 0.5),
                            new Coordinate(buffX + 0.5, buffY + 0.5),
                            new Coordinate(buffX + 0.5, buffY - 0.5),
                            new Coordinate(buffX - 0.5, buffY - 0.5),
                            new Coordinate(buffX - 0.5, buffY + 0.5)
                        });

                Feature myfeature = FeatureUtilities.defaultFeature(type, "id-" + x + "-" + y);
                myfeature.getProperty("cellgeom").setValue(geometryFactory.createPolygon(line, null));
                myfeature.getProperty("position").setValue(pos);
                myfeature.getProperty("orientation").setValue(PixelInCell.CELL_CENTER.name());
                myfeature.getProperty("band-0").setValue(0.0);
                myfeature.getProperty("band-1").setValue((Integer) x);
                myfeature.getProperty("band-2").setValue((Integer) y);

                featureList.add((x + (y * max)), myfeature);
            }
        }
        return featureList;
    }

    private List<Feature> buildFCResultPixelCorner() throws NoSuchAuthorityCodeException, FactoryException {

        FeatureType type = buildFeatureType();
        final List<Feature> featureList = new ArrayList<Feature>();
        GeometryFactory geometryFactory = new GeometryFactory();

        double buffX = 0;
        double buffY = 0;
        for (int y = 0; y < max; y++) {
            for (int x = 0; x < max; x++) {
                buffX = x;
                buffY = y + 1.0;
                Point pos = geometryFactory.createPoint(new Coordinate(buffX, buffY));
                LinearRing line = geometryFactory.createLinearRing(new Coordinate[]{
                            new Coordinate(buffX, buffY),
                            new Coordinate(buffX + 1.0, buffY),
                            new Coordinate(buffX + 1.0, buffY - 1.0),
                            new Coordinate(buffX, buffY - 1.0),
                            new Coordinate(buffX, buffY)
                        });

                Feature myfeature = FeatureUtilities.defaultFeature(type, "id-" + x + "-" + y);
                myfeature.getProperty("cellgeom").setValue(geometryFactory.createPolygon(line, null));
                myfeature.getProperty("position").setValue(pos);
                myfeature.getProperty("orientation").setValue(PixelInCell.CELL_CENTER.name());
                myfeature.getProperty("band-0").setValue(0.0);
                myfeature.getProperty("band-1").setValue((Integer) x);
                myfeature.getProperty("band-2").setValue((Integer) y);

                featureList.add((x + (y * max)), myfeature);
            }
        }
        return featureList;
    }

    private static class SimpleCoverageReader extends GridCoverageReader {

        private final GridCoverage2D coverage;
        private final PixelInCell pixPos;

        public SimpleCoverageReader(final GridCoverage2D coverage, PixelInCell pixPos) {
            this.coverage = coverage;
            this.pixPos = pixPos;
        }

        @Override
        public List<String> getCoverageNames() throws CoverageStoreException, CancellationException {
            return Collections.emptyList();
        }

        @Override
        public GeneralGridGeometry getGridGeometry(final int i) throws CoverageStoreException, CancellationException {
            return (GeneralGridGeometry) coverage.getGridGeometry();
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(final int i) throws CoverageStoreException, CancellationException {
            return Collections.singletonList(coverage.getSampleDimension(i));
        }

        @Override
        public GridCoverage read(final int i, final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
            return coverage;
        }

        @Override
        public SpatialMetadata getCoverageMetadata(int i) throws CoverageStoreException {
            SpatialMetadata meta = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
            GridDomainAccessor grid = new GridDomainAccessor(meta);
            grid.setGridGeometry(coverage.getGridGeometry(), pixPos, CellGeometry.POINT, -1);
            ReferencingBuilder ref = new ReferencingBuilder(meta);
            ref.setCoordinateReferenceSystem(coverage.getCoordinateReferenceSystem());
            return meta;
        }
    }
}
