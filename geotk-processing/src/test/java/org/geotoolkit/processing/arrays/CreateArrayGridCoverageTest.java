/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.processing.arrays;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CreateArrayGridCoverageTest {

    @Test
    public void gridCoverageArraysTest() throws Exception{

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, CreateArrayGridCoverageValuesDescriptor.NAME);
        assertNotNull(desc);

        final GridCoverage cov1 = create(BufferedImage.TYPE_3BYTE_BGR, Color.RED, Color.BLACK);
        final GridCoverage cov2 = create(BufferedImage.TYPE_4BYTE_ABGR, Color.BLUE, Color.GREEN);
        final GridCoverage cov3 = create(BufferedImage.TYPE_3BYTE_BGR, Color.GREEN, Color.RED);
        GridCoverage[] arrayExpected = {cov1, cov2, cov3};

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();

        params.parameter(CreateArrayGridCoverageValuesDescriptor.INPUT_FIRST_NAME).setValue(arrayExpected[0]);
        params.parameter(CreateArrayGridCoverageValuesDescriptor.INPUT_SECOND_NAME).setValue(arrayExpected[1]);
        params.parameter(CreateArrayGridCoverageValuesDescriptor.INPUT_THIRD_NAME).setValue(arrayExpected[2]);

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        GridCoverage[] array = (GridCoverage[]) result.parameter(CreateArrayGridCoverageValuesDescriptor.RESULT_NAME).getValue();
        assertNotNull(array);
        assertArrayEquals(arrayExpected, array);
    }

    private static GridCoverage create(int type, Color color1, Color color2){
        final BufferedImage inputImage = new BufferedImage(100, 100, type);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(color1);
        g.fillRect(0, 0, 100, 50);
        g.setColor(color2);
        g.fillRect(0, 50, 100, 50);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 500);
        env.setRange(1, 0, 30);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(inputImage);
        gcb.setDomain(env);
        return gcb.build();
    }
}
