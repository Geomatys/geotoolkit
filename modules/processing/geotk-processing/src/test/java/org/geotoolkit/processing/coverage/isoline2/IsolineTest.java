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
package org.geotoolkit.processing.coverage.isoline2;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.opengis.parameter.ParameterValueGroup;

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
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
            {100,100,100},
            {100,200,100},
            {100,100,100}
        });
        final GridCoverage coverage = gcb.getGridCoverage2D();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor2.NAME);
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
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
            {10,10,20},
            {10,10,20},
            {10,15,10},
        });
        final GridCoverage coverage = gcb.getGridCoverage2D();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);


        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor2.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{15});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(2, FeatureStoreUtilities.getCount(col, true).longValue());

    }

    @Test
    public void test3() throws Exception{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
            {10,10,20},
            {10,10,20},
            {10,15,10},
            {10,15,10},
        });
        final GridCoverage coverage = gcb.getGridCoverage2D();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);


        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor2.NAME);
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverageRef").setValue(ref);
        procparams.parameter("inIntervals").setValue(new double[]{15});
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        FeatureSet col = (FeatureSet) result.parameter("outFeatureCollection").getValue();
        assertEquals(4, FeatureStoreUtilities.getCount(col, true).longValue());

    }


    @Test
    public void test4() throws Exception{
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 7);
        env.setRange(1, 0, 7);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
                { Float.NaN,  Float.NaN, Float.NaN,  Float.NaN, Float.NaN, Float.NaN, Float.NaN},
                { Float.NaN,  3.164f,  2.91f,  2.78f,  1.03f, -2.086f,Float.NaN},
                { Float.NaN, 3.41f,  5.41f,  4.66f,  4.28f, 0.163f, Float.NaN},
                { Float.NaN, 3.78f,  0.41f, -0.83f,  -0.83f, 0.663f, Float.NaN},
                { Float.NaN, -0.58f, -2.83f, -1.21f, -0.83f, 0.038f, Float.NaN},
                { Float.NaN,  Float.NaN, Float.NaN,  Float.NaN, Float.NaN, Float.NaN, Float.NaN}
        });

        final GridCoverage coverage = gcb.getGridCoverage2D();
        final GridCoverageResource ref = new InMemoryGridCoverageResource(coverage);

        double[] intervales = {3.163};

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IsolineDescriptor2.NAME);
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
        CoordinateSequence sequence = line.getCoordinateSequence();
        assertEquals(16, sequence.size());

        Coordinate[] expected = new Coordinate[] {
                new Coordinate(1.5, 2.7515672842277548),
                new Coordinate(1.6830860462828303, 2.916666666666667),
                new Coordinate(2.5, 3.5590333533118175),
                new Coordinate(3.1477647294354694, 3.672392184341381),
                new Coordinate(3.5, 3.765209494383989),
                new Coordinate(4.281408969159637, 3.8283104640195758),
                new Coordinate(4.5, 3.8283104640195758),
                new Coordinate(4.771314100938449, 4.083333333333334),
                new Coordinate(4.675463427429896, 4.288040665334878),
                new Coordinate(4.5, 4.484307738208203),
                new Coordinate(3.912396666264486, 4.564462777308567),
                new Coordinate(3.5, 5.01232266160712),
                new Coordinate(3.3543726058308847, 5.080101373469366),
                new Coordinate(2.5, 5.131933362127943),
                new Coordinate(1.9940001716613773, 4.659666866938274),
                new Coordinate(1.5039371438387095, 5.25)
        };

        for (int i = 0; i < sequence.size(); i++) {
            for (int j = 0; j < 2; j++) {
                assertEquals(expected[i].getOrdinate(j),sequence.getCoordinate(i).getOrdinate(j), 0.0000001);
            }
        }
    }

}
