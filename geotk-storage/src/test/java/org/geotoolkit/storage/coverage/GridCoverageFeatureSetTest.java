/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.storage.coverage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.feature.TypeConventions;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridCoverageFeatureSetTest {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    /**
     * Test coverage 2D mapped as a feature.
     *
     * @throws DataStoreException
     */
    @Test
    public void coverageRecord2DTest() throws DataStoreException {

        //create coverage
        final BufferedImage image = BufferedImages.createImage(2, 2, 2, DataBuffer.TYPE_INT);
        final WritableRaster raster = image.getRaster();
        raster.setPixel(0, 0, new int[]{10,2});
        raster.setPixel(1, 0, new int[]{30,4});
        raster.setPixel(0, 1, new int[]{50,6});
        raster.setPixel(1, 1, new int[]{70,8});

        SampleDimension.Builder sdb = new SampleDimension.Builder();
        sdb.setName("values");
        sdb.addQuantitative("valuesCat", NumberRange.create(0, true, 1000, true), (MathTransform1D) MathTransforms.linear(10, -5), null);
        final SampleDimension sdim1 = sdb.build();
        sdb.clear();
        sdb.setName("quality");
        sdb.addQuantitative("qualityCat", NumberRange.create(0, true, 100, true), (MathTransform1D) MathTransforms.linear(1, 0), null);
        final SampleDimension sdim2 = sdb.build();

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(image);
        gcb.setDomain(new GridGeometry(null, PixelInCell.CELL_CENTER, new AffineTransform2D(2, 0, 0, 2, 31, 11), CommonCRS.WGS84.normalizedGeographic()));
        gcb.setRanges(sdim1,sdim2);
        final GridCoverage coverage = gcb.build();

        //test mapped feature type
        final FeatureType coverageType = GridCoverageFeatureSet.createCoverageType(coverage);
        final FeatureAssociationRole role = (FeatureAssociationRole) coverageType.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType recordType = role.getValueType();

        assertEquals("Coverage",coverageType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_TYPE.isAssignableFrom(coverageType));

        assertEquals("Record",recordType.getName().tip().toString());
        assertTrue(TypeConventions.COVERAGE_RECORD_TYPE.isAssignableFrom(recordType));

        //convert coverage to feature
        final Feature feature = coverageType.newInstance();
        feature.setProperty(GridCoverageFeatureSet.coverageRecords(coverage,role));

        //check records
        final Collection col = (Collection) feature.getPropertyValue(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        assertEquals(4, col.size());
        final Iterator<Feature> ite = col.iterator();
        final Feature r1 = ite.next();
        final Feature r2 = ite.next();
        final Feature r3 = ite.next();
        final Feature r4 = ite.next();
        assertFalse(ite.hasNext());

        assertEquals(10.0*10-5, r1.getPropertyValue("values"));
        assertEquals(2.0,       r1.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,10, 32,10, 32,12, 30,12, 30,10})), r1.getProperty("geometry").getValue());
        assertEquals(30.0*10-5, r2.getPropertyValue("values"));
        assertEquals(4.0,       r2.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,10, 34,10, 34,12, 32,12, 32,10})), r2.getProperty("geometry").getValue());
        assertEquals(50.0*10-5, r3.getPropertyValue("values"));
        assertEquals(6.0,       r3.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,12, 32,12, 32,14, 30,14, 30,12})), r3.getProperty("geometry").getValue());
        assertEquals(70.0*10-5, r4.getPropertyValue("values"));
        assertEquals(8.0,       r4.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,12, 34,12, 34,14, 32,14, 32,12})), r4.getProperty("geometry").getValue());

    }

    /**
     * Test coverage rgb mapped as a feature.
     *
     * @throws DataStoreException
     */
    @Test
    public void coverageRecordRGBTest() throws DataStoreException {

        //create coverage
        final BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, Color.BLUE.getRGB());
        image.setRGB(1, 0, Color.RED.getRGB());
        image.setRGB(0, 1, Color.GREEN.getRGB());
        image.setRGB(1, 1, Color.PINK.getRGB());

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(image);
        gcb.setDomain(new GridGeometry(null, PixelInCell.CELL_CENTER, new AffineTransform2D(2, 0, 0, 2, 31, 11), CommonCRS.WGS84.normalizedGeographic()));
        final GridCoverage coverage = gcb.build();

        //test mapped feature type
        final FeatureType coverageType = GridCoverageFeatureSet.createCoverageType(coverage);
        final FeatureAssociationRole role = (FeatureAssociationRole) coverageType.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType recordType = role.getValueType();

        assertEquals("Coverage",coverageType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_TYPE.isAssignableFrom(coverageType));

        assertEquals("Record",recordType.getName().tip().toString());
        assertTrue(TypeConventions.COVERAGE_RECORD_TYPE.isAssignableFrom(recordType));

        //convert coverage to feature
        final Feature feature = coverageType.newInstance();
        feature.setProperty(GridCoverageFeatureSet.coverageRecords(coverage,role));

        //check records
        final Collection col = (Collection) feature.getPropertyValue(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        assertEquals(4, col.size());
        final Iterator<Feature> ite = col.iterator();
        final Feature r1 = ite.next();
        final Feature r2 = ite.next();
        final Feature r3 = ite.next();
        final Feature r4 = ite.next();
        assertFalse(ite.hasNext());

        assertEquals(0.0, r1.getPropertyValue("Red"));
        assertEquals(0.0, r1.getPropertyValue("Green"));
        assertEquals(255.0, r1.getPropertyValue("Blue"));
        assertEquals(255.0, r1.getPropertyValue("Transparency"));
        assertEquals(Color.BLUE, r1.getPropertyValue("color"));

        assertEquals(255.0, r2.getPropertyValue("Red"));
        assertEquals(0.0, r2.getPropertyValue("Green"));
        assertEquals(0.0, r2.getPropertyValue("Blue"));
        assertEquals(255.0, r2.getPropertyValue("Transparency"));
        assertEquals(Color.RED, r2.getPropertyValue("color"));

        assertEquals(0.0, r3.getPropertyValue("Red"));
        assertEquals(255.0, r3.getPropertyValue("Green"));
        assertEquals(0.0, r3.getPropertyValue("Blue"));
        assertEquals(255.0, r3.getPropertyValue("Transparency"));
        assertEquals(Color.GREEN, r3.getPropertyValue("color"));

        assertEquals(255.0, r4.getPropertyValue("Red"));
        assertEquals(175.0, r4.getPropertyValue("Green"));
        assertEquals(175.0, r4.getPropertyValue("Blue"));
        assertEquals(255.0, r4.getPropertyValue("Transparency"));
        assertEquals(Color.PINK, r4.getPropertyValue("color"));
    }

    /**
     * Test coverage 3D mapped as a feature.
     *
     * @throws DataStoreException
     */
    @Ignore
    @Test
    public void coverageRecord3DTest() throws DataStoreException, IOException, TransformException, FactoryException {

        //create CRS
        final CoordinateReferenceSystem crs3d = new DefaultCompoundCRS(Collections.singletonMap("name", "crs3d"),
                CommonCRS.WGS84.normalizedGeographic(),
                CommonCRS.Vertical.DEPTH.crs());

        //create sample dimensions
        SampleDimension.Builder sdb = new SampleDimension.Builder();
        sdb.setName("values");
        sdb.addQuantitative("valuesCat", NumberRange.create(0, true, 1000, true), (MathTransform1D) MathTransforms.linear(10, -5), null);
        final SampleDimension sdim1 = sdb.build();
        sdb.clear();
        sdb.setName("quality");
        sdb.addQuantitative("qualityCat", NumberRange.create(0, true, 100, true), (MathTransform1D) MathTransforms.linear(1, 0), null);
        final SampleDimension sdim2 = sdb.build();

        final int width = 2, height = 2, depth = 2, nbSamples = 2;

        // Cube geometry
        final GridExtent cubeGrid = new GridExtent(
                new DimensionNameType[]{DimensionNameType.COLUMN, DimensionNameType.ROW, DimensionNameType.VERTICAL},
                new long[3],
                new long[]{width, height, depth}, false);
        final MathTransform cubeGrid2Crs = MathTransforms.linear(Matrices.create(4, 4, new double[] {
                2, 0, 0, 31,
                0, 2, 0, 11,
                0, 0, 1, 100,
                0, 0, 0, 1
        }));
        final GridGeometry domain = new GridGeometry(cubeGrid, PixelInCell.CELL_CENTER, cubeGrid2Crs, crs3d);
        final int[] values = {
                // z = 0
                10, 2,  30, 4,
                50, 6,  70, 8,

                // z = 1
                20, 3,  40, 5,
                60, 7,  80,9
        };

        final GridCoverage coverage3D = new BufferedGridCoverage(domain, Arrays.asList(sdim1, sdim2), new DataBufferInt(values, values.length));

        //test mapped feature type
        final FeatureType coverageType = GridCoverageFeatureSet.createCoverageType(coverage3D);
        final FeatureAssociationRole role = (FeatureAssociationRole) coverageType.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType recordType = role.getValueType();

        assertEquals("Coverage3D",coverageType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_TYPE.isAssignableFrom(coverageType));

        assertEquals("Coverage3DRecord",recordType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_RECORD_TYPE.isAssignableFrom(recordType));

        //convert coverage to feature
        final Feature feature = coverageType.newInstance();
        feature.setProperty(GridCoverageFeatureSet.coverageRecords(coverage3D,role));

        //check records
        final Collection col = (Collection) feature.getPropertyValue(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        assertEquals(4*2, col.size());
        final Iterator<Feature> ite = col.iterator();
        final Feature r1 = ite.next();
        final Feature r2 = ite.next();
        final Feature r3 = ite.next();
        final Feature r4 = ite.next();
        final Feature r5 = ite.next();
        final Feature r6 = ite.next();
        final Feature r7 = ite.next();
        final Feature r8 = ite.next();
        assertFalse(ite.hasNext());

        assertEquals(10.0*10-5, r1.getPropertyValue("values"));
        assertEquals(2.0,       r1.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,12, 32,12, 32,14, 30,14, 30,12})), r1.getProperty("geometry").getValue());
        assertEquals(30.0*10-5, r2.getPropertyValue("values"));
        assertEquals(4.0,       r2.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,12, 34,12, 34,14, 32,14, 32,12})), r2.getProperty("geometry").getValue());
        assertEquals(50.0*10-5, r3.getPropertyValue("values"));
        assertEquals(6.0,       r3.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,14, 32,14, 32,16, 30,16, 30,14})), r3.getProperty("geometry").getValue());
        assertEquals(70.0*10-5, r4.getPropertyValue("values"));
        assertEquals(8.0,       r4.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,14, 34,14, 34,16, 32,16, 32,14})), r4.getProperty("geometry").getValue());

        assertEquals(20.0*10-5, r5.getPropertyValue("values"));
        assertEquals(3.0,       r5.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,12, 32,12, 32,14, 30,14, 30,12})), r5.getProperty("geometry").getValue());
        assertEquals(40.0*10-5, r6.getPropertyValue("values"));
        assertEquals(5.0,       r6.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,12, 34,12, 34,14, 32,14, 32,12})), r6.getProperty("geometry").getValue());
        assertEquals(60.0*10-5, r7.getPropertyValue("values"));
        assertEquals(7.0,       r7.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{30,14, 32,14, 32,16, 30,16, 30,14})), r7.getProperty("geometry").getValue());
        assertEquals(80.0*10-5, r8.getPropertyValue("values"));
        assertEquals(9.0,       r8.getPropertyValue("quality"));
        assertEquals(GF.createPolygon(new LiteCoordinateSequence(new double[]{32,14, 34,14, 34,16, 32,16, 32,14})), r8.getProperty("geometry").getValue());

    }

}
