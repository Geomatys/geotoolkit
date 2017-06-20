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
package org.geotoolkit.internal.feature;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.matrix.Matrix4;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.coverage.GridCoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.SampleDimensionBuilder;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.geotoolkit.image.BufferedImages;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import static org.junit.Assert.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageFeatureTest {

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * Test coverage 2D mapped as a feature.
     *
     * @throws CoverageStoreException
     */
    @Test
    public void coverageRecord2DTest() throws CoverageStoreException {

        //create coverage
        final BufferedImage image = BufferedImages.createImage(2, 2, 2, DataBuffer.TYPE_INT);
        final WritableRaster raster = image.getRaster();
        raster.setPixel(0, 0, new int[]{10,2});
        raster.setPixel(1, 0, new int[]{30,4});
        raster.setPixel(0, 1, new int[]{50,6});
        raster.setPixel(1, 1, new int[]{70,8});

        SampleDimensionBuilder sdb = new SampleDimensionBuilder(DataBuffer.TYPE_INT);
        sdb.setDescription("values");
        sdb.add("valuesCat", null, NumberRange.create(0, true, 1000, true), 10, -5);
        final GridSampleDimension sdim1 = sdb.build();
        sdb = new SampleDimensionBuilder(DataBuffer.TYPE_INT);
        sdb.setDescription("quality");
        sdb.add("qualityCat", null, NumberRange.create(0, true, 100, true), 1, 0);
        final GridSampleDimension sdim2 = sdb.build();


        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("MyCoverage");
        gcb.setRenderedImage(image);
        gcb.setGridToCRS(new AffineTransform(2, 0, 0, 2, 31, 11));
        gcb.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        gcb.setSampleDimensions(sdim1,sdim2);
        final GridCoverage coverage = gcb.build();

        //test mapped feature type
        final FeatureType coverageType = CoverageFeature.createCoverageType(coverage);
        final FeatureAssociationRole role = (FeatureAssociationRole) coverageType.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType recordType = role.getValueType();

        assertEquals("MyCoverage",coverageType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_TYPE.isAssignableFrom(coverageType));

        assertEquals("MyCoverageRecord",recordType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_RECORD_TYPE.isAssignableFrom(recordType));

        //convert coverage to feature
        final Feature feature = coverageType.newInstance();
        feature.setProperty(CoverageFeature.coverageRecords(coverage,role));

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

    }

    /**
     * Test coverage 3D mapped as a feature.
     *
     * @throws CoverageStoreException
     */
    @Test
    public void coverageRecord3DTest() throws CoverageStoreException, IOException, TransformException, FactoryException {

        //create CRS
        final CoordinateReferenceSystem crs3d = new DefaultCompoundCRS(Collections.singletonMap("name", "crs3d"),
                CommonCRS.WGS84.normalizedGeographic(),
                CommonCRS.Vertical.DEPTH.crs());

        //create sample dimensions
        SampleDimensionBuilder sdb = new SampleDimensionBuilder(DataBuffer.TYPE_INT);
        sdb.setDescription("values");
        sdb.add("valuesCat", null, NumberRange.create(0, true, 1000, true), 10, -5);
        final GridSampleDimension sdim1 = sdb.build();
        sdb = new SampleDimensionBuilder(DataBuffer.TYPE_INT);
        sdb.setDescription("quality");
        sdb.add("qualityCat", null, NumberRange.create(0, true, 100, true), 1, 0);
        final GridSampleDimension sdim2 = sdb.build();

        final GridCoverage slice1;
        {//create first slice
            final BufferedImage image1 = BufferedImages.createImage(2, 2, 2, DataBuffer.TYPE_INT);
            final WritableRaster raster1 = image1.getRaster();
            raster1.setPixel(0, 0, new int[]{10,2});
            raster1.setPixel(1, 0, new int[]{30,4});
            raster1.setPixel(0, 1, new int[]{50,6});
            raster1.setPixel(1, 1, new int[]{70,8});

            final Matrix matrix = new Matrix4(
                    2, 0, 0, 31,
                    0, 2, 0, 11,
                    0, 0, 1, 100,
                    0, 0, 0, 1);
            final MathTransform gridToCrs = MathTransforms.linear(matrix);
            final GridGeometry2D gg = new GridGeometry2D(new GeneralGridEnvelope(new int[]{0,0,0}, new int[]{2,2,1}, false), gridToCrs, crs3d);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName("Slice1");
            gcb.setRenderedImage(image1);
            gcb.setGridGeometry(gg);
            gcb.setSampleDimensions(sdim1,sdim2);
            slice1 = gcb.build();
        }

        final GridCoverage slice2;
        {//create first slice
            final BufferedImage image1 = BufferedImages.createImage(2, 2, 2, DataBuffer.TYPE_INT);
            final WritableRaster raster1 = image1.getRaster();
            raster1.setPixel(0, 0, new int[]{20,3});
            raster1.setPixel(1, 0, new int[]{40,5});
            raster1.setPixel(0, 1, new int[]{60,7});
            raster1.setPixel(1, 1, new int[]{80,9});

            final Matrix matrix = new Matrix4(
                    2, 0, 0, 31,
                    0, 2, 0, 11,
                    0, 0, 1, 101,
                    0, 0, 0, 1);
            final MathTransform gridToCrs = MathTransforms.linear(matrix);
            final GridGeometry2D gg = new GridGeometry2D(new GeneralGridEnvelope(new int[]{0,0,0}, new int[]{2,2,1}, false), gridToCrs, crs3d);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName("Slice2");
            gcb.setRenderedImage(image1);
            gcb.setGridGeometry(gg);
            gcb.setSampleDimensions(sdim1,sdim2);
            slice2 = gcb.build();
        }

        final GridCoverage coverage3D;
        {//create coverage 3d
            coverage3D = new GridCoverageStack("Coverage3D", Arrays.asList(slice1,slice2));
        }

        //test mapped feature type
        final FeatureType coverageType = CoverageFeature.createCoverageType(coverage3D);
        final FeatureAssociationRole role = (FeatureAssociationRole) coverageType.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType recordType = role.getValueType();

        assertEquals("Coverage3D",coverageType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_TYPE.isAssignableFrom(coverageType));

        assertEquals("Coverage3DRecord",recordType.getName().toString());
        assertTrue(TypeConventions.COVERAGE_RECORD_TYPE.isAssignableFrom(recordType));

        //convert coverage to feature
        final Feature feature = coverageType.newInstance();
        feature.setProperty(CoverageFeature.coverageRecords(coverage3D,role));

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
