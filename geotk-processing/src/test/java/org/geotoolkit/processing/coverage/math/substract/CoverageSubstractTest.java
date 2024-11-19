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
package org.geotoolkit.processing.coverage.math.substract;

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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CoverageSubstractTest {

    @Test
    public void gridCoverageArraysTest() throws Exception{

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, CoverageSubstractDescriptor.NAME);
        assertNotNull(desc);

        final GridCoverage cov1 = create(BufferedImage.TYPE_BYTE_GRAY, new Color(10,10,10), new Color(20,20,20));
        final GridCoverage cov2 = create(BufferedImage.TYPE_BYTE_GRAY, new Color(2,2,2), new Color(2,2,2));

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();

        params.parameter(CoverageSubstractDescriptor.IN_FIRST_COVERAGE_NAME).setValue(cov1);
        params.parameter(CoverageSubstractDescriptor.IN_SECOND_COVERAGE_NAME).setValue(cov2);

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        GridCoverage resultGridCoverage = (GridCoverage) result.parameter(CoverageSubstractDescriptor.OUT_COVERAGE_NAME).getValue();
        assertNotNull(resultGridCoverage);

        RenderedImage image = resultGridCoverage.render(null);
        Raster raster = image.getData();
        int[] resultPixel1 = raster.getPixel(0,0, (int[]) null);
        int[] resultPixel2 = raster.getPixel(10,61, (int[]) null);

        assertArrayEquals(resultPixel1, new int[]{8});
        assertArrayEquals(resultPixel2, new int[]{18});
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
