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

import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.PixelInCell;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class IsolineTest extends org.geotoolkit.test.TestBase {

    @Test
    public void test() throws Exception{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 3), env));
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
        FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(1l, FeatureStoreUtilities.getCount(col, true).longValue());

    }

    @Test
    public void test2() throws Exception{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 3), env));
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
        assertEquals("LINESTRING (2 0.5, 2 1.5, 2.5 2)", geom.toText());

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
        assertEquals("LINESTRING (2 2.625, 2 1.875, 2.5 1.5)", geom.toText());

    }


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

        org.opengis.feature.Feature candidate = col.features(false).iterator().next();
        Geometry geom = (Geometry) candidate.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
        assertTrue(geom instanceof LineString);
        LineString line = (LineString) geom;
        assertEquals("LINESTRING (1.5 2.7515672842277548, 1.6830860462828303 2.916666666666667, 2.5 3.5590333533118175, 3.5 3.765209494383988, 4.5 3.8283104640195758, 4.771314100938448 4.083333333333334, 4.5 4.484307738208203, 3.5 5.01232266160712, 2.5 5.131933362127943, 1.5039371438387097 5.25)", line.toText());
    }

}
