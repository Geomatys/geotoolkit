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
package org.geotoolkit.processing.coverage.math.multiply;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.arrays.CreateArrayGridCoverageValuesDescriptor;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CoverageMultiplyWithValueTest {

    @Test
    public void gridCoverageArraysTest() throws Exception{

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, CoverageMultiplyWithValueDescriptor.NAME);
        assertNotNull(desc);

        final GridCoverage cov = create(BufferedImage.TYPE_BYTE_GRAY);

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();

        params.parameter(CoverageMultiplyWithValueDescriptor.IN_COVERAGE_NAME).setValue(cov);
        params.parameter(CoverageMultiplyWithValueDescriptor.IN_VALUE_NAME).setValue(Double.valueOf(2.0d));

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        GridCoverage resultGridCoverage = (GridCoverage) result.parameter(CoverageMultiplyWithValueDescriptor.OUT_COVERAGE_NAME).getValue();
        assertNotNull(resultGridCoverage);

        RenderedImage image = resultGridCoverage.render(null);
        Raster raster = image.getData();
        int[] resultPixel1 = raster.getPixel(0,0, (int[]) null);
        int[] resultPixel2 = raster.getPixel(10,61, (int[]) null);

        assertArrayEquals(resultPixel1, new int[]{20});
        assertArrayEquals(resultPixel2, new int[]{40});
    }

    private static GridCoverage create(int type){
        final BufferedImage inputImage = new BufferedImage(100, 100, type);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(new Color(10,10,10));
        g.fillRect(0, 0, 100, 50);
        g.setColor(new Color(20, 20, 20));
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
