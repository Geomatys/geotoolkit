/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.privy.WritableTiledImage;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.geometry.wrapper.jts.Factory;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Note: We do not use {@link Geometry#equalsExact(Geometry, double) equalsExact+tolerance } to test isoline equality:
 * <ul>
 *     <li>There's no guarantee the geometries use the same winding order</li>
 *     <li>In case of rings, we cannot ensure they use the same starting point.</li>
 * </ul>
 * Therefore, we use the {@link Geometry#equalsTopo(Geometry)}.
 * Alternatively, we could create a small buffer from one of the lines, and check that it contains the second one.
 * @author Johann Sorel (Geomatys)
 */
public class IsolineTest {

    private static final GeometryFactory GEOM_FACTORY = Factory.INSTANCE.factory(false);

    /**
     * Test an isoline created arount a central point.
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 3), env, GridOrientation.HOMOTHETY));
        gcb.setValues(BufferedImages.toDataBuffer1D(new float[][]{
            {100,100,100},
            {100,200,100},
            {100,100,100}
        }), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());

        final GridCoverage coverage = gcb.build();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{150});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        final FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(1l, FeatureStoreUtilities.getCount(col, true).longValue());
        final Feature feature = col.features(true).findFirst().get();
        final LineString geom = (LineString) feature.getPropertyValue(AttributeConvention.GEOMETRY);
        assertIsolineEquals("LINESTRING (1 1.5, 1.5 2, 2 1.5, 1.5 1, 1 1.5)", geom);
    }

    @Test
    public void test2() throws Exception{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 3), env, GridOrientation.HOMOTHETY));
        gcb.setValues(BufferedImages.toDataBuffer1D(new float[][]{
            {10,10,20},
            {10,10,20},
            {10,15,10},
        }), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final GridCoverage coverage = gcb.build();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{15});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        final FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(1, FeatureStoreUtilities.getCount(col, true).longValue());
        final Feature feature = col.features(false).iterator().next();
        final LineString geom = (LineString) feature.getPropertyValue(AttributeConvention.GEOMETRY);
        assertIsolineEquals("LINESTRING (2 0.5, 2 1.5, 2.5 2)", geom);
    }

    @Test
    public void test3() throws Exception{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 4), PixelInCell.CELL_CORNER, new AffineTransform2D(1, 0, 0, -0.75, 0, 3.0), env.getCoordinateReferenceSystem()));
        gcb.setValues(BufferedImages.toDataBuffer1D(new float[][]{
            {10,10,20},
            {10,10,20},
            {10,15,10},
            {10,15,10},
        }), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final GridCoverage coverage = gcb.build();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);


        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{15});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(1, FeatureStoreUtilities.getCount(col, true).longValue());
        final Feature feature = col.features(false).iterator().next();
        final LineString geom = (LineString) feature.getPropertyValue(AttributeConvention.GEOMETRY);
        assertIsolineEquals("LINESTRING (2 2.625, 2 1.875, 2.5 1.5)", geom);
    }

    /**
     * A single threshold is detected near a no-data border. The algorithm should consider that not enough data is
     * available to draw a segment.
     */
    @Test
    public void test4() throws Exception{
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 7);
        env.setRange(1, 0, 7);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(7, 6), PixelInCell.CELL_CORNER, new AffineTransform2D(1, 0, 0, -1.1666666666666667, 0, 7.0), env.getCoordinateReferenceSystem()));
        gcb.setValues(BufferedImages.toDataBuffer1D(new float[][]{
                { Float.NaN,  Float.NaN, Float.NaN,  Float.NaN, Float.NaN, Float.NaN, Float.NaN},
                { Float.NaN,  3.164f,  2.91f,  2.78f,  1.03f, -2.086f,Float.NaN},
                { Float.NaN, 3.41f,  5.41f,  4.66f,  4.28f, 0.163f, Float.NaN},
                { Float.NaN, 3.78f,  0.41f, -0.83f,  -0.83f, 0.663f, Float.NaN},
                { Float.NaN, -0.58f, -2.83f, -1.21f, -0.83f, 0.038f, Float.NaN},
                { Float.NaN,  Float.NaN, Float.NaN,  Float.NaN, Float.NaN, Float.NaN, Float.NaN}
        }), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final GridCoverage coverage = gcb.build();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);

        double[] intervales = {3.163};

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(intervales);
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(1, FeatureStoreUtilities.getCount(col, true).longValue());
    }

    /**
     * Test isoline creation on a tiled image.
     */
    @Test
    public void testTiledImage() throws NoSuchIdentifierException, ProcessException, DataStoreException, ParseException {

        /* create a tiled image with 4 tiles
              0 1   2 3
            +-----+-----+
          0 | 0 0 | 0 0 |
          1 | 0 1 | 1 0 |
            +-----+-----+
          2 | 0 1 | 1 0 |
          3 | 0 0 | 0 0 |
            +-----+-----+
            Result geometry is an octogone, a square with cutted edges
        */
        final BufferedImage tile00 = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_DOUBLE);
        final BufferedImage tile10 = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_DOUBLE);
        final BufferedImage tile01 = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_DOUBLE);
        final BufferedImage tile11 = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_DOUBLE);
        final WritableRaster raster00 = tile00.getRaster();
        final WritableRaster raster10 = tile10.getRaster().createWritableTranslatedChild(2, 0);
        final WritableRaster raster01 = tile01.getRaster().createWritableTranslatedChild(0, 2);
        final WritableRaster raster11 = tile11.getRaster().createWritableTranslatedChild(2, 2);
        raster00.setSample(1+0, 1+0, 0, 1);
        raster10.setSample(0+2, 1+0, 0, 1);
        raster01.setSample(1+0, 0+2, 0, 1);
        raster11.setSample(0+2, 0+2, 0, 1);

        final WritableTiledImage img = new WritableTiledImage(null, tile00.getColorModel(), 4, 4, 0, 0, new WritableRaster[]{raster00, raster10, raster01, raster11});

        final MathTransform gridtoCrs = new AffineTransform2D(1, 0, 0, 1, 0, 0);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(4, 4), PixelInCell.CELL_CENTER, gridtoCrs, CommonCRS.WGS84.normalizedGeographic()));
        gcb.setValues(img);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());

        final GridCoverage coverage = gcb.build();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{0.5});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        final FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        final List<Feature> features = col.features(false).collect(Collectors.toList());
        assertEquals(1L, features.size());
        final LineString geom = (LineString) features.get(0).getPropertyValue(AttributeConvention.GEOMETRY);
        assertIsolineEquals("LINESTRING (1 0.5, 2 0.5, 2.5 1, 2.5 2, 2 2.5, 1 2.5, 0.5 2, 0.5 1, 1 0.5)", geom);
    }

    private static void assertIsolineEquals(String expectedWkt, final Geometry isoline) throws ParseException {
        final Geometry expectedGeometry = new WKTReader(GEOM_FACTORY).read(expectedWkt);
        assertTrue(String.format("Geometry should be equal.%nExpected: %s%nBut was: %s", expectedWkt, isoline), isoline.equalsTopo(expectedGeometry));
    }
}
