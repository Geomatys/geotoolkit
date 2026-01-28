/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.complex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.Format;
import org.junit.Test;
import org.opengis.geometry.Envelope;

import static org.junit.Assert.*;


/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ComplexToCoverageConverterTest extends AbstractWPSConverterTest {

    @Test
    public void testConversion() throws UnconvertibleObjectException, IOException, InterruptedException, URISyntaxException  {

        final WPSObjectConverter<Data, GridCoverage> converter = WPSConverterRegistry.getInstance().getConverter(Data.class, GridCoverage.class);

        final Path expectedStream = Paths.get(ComplexToRenderedImageConvereterTest.class.getResource("/expected/coverage_base64").toURI());
        assertNotNull(expectedStream);
        final String encodedCoverage = new String(Files.readAllBytes(expectedStream), StandardCharsets.US_ASCII);

        final Map<String, Object> param = new HashMap<>();
        param.put(WPSObjectConverter.MIME, "image/x-geotiff");
        param.put(WPSObjectConverter.ENCODING, "base64");

        final Format format = new Format("base64", "image/x-geotiff", null, null);
        final Data complex = new Data(format, encodedCoverage);

        final GridCoverage convertedCoverage = converter.convert(complex, param);
        assertNotNull(convertedCoverage);

        final GridCoverage expectedCoverage = ConvertersTestUtils.makeCoverage();

        final Envelope convertedEnvelope = convertedCoverage.getGridGeometry().getEnvelope();
        final Envelope expectedEnvelope = expectedCoverage.getGridGeometry().getEnvelope();

        assertTrue(CRS.equivalent(expectedEnvelope.getCoordinateReferenceSystem(), convertedEnvelope.getCoordinateReferenceSystem()));
        assertTrue(expectedEnvelope.getMinimum(0) == convertedEnvelope.getMinimum(0));
        assertTrue(expectedEnvelope.getMinimum(1) == convertedEnvelope.getMinimum(1));
        assertTrue(expectedEnvelope.getMaximum(0) == convertedEnvelope.getMaximum(0));
        assertTrue(expectedEnvelope.getMaximum(1) == convertedEnvelope.getMaximum(1));
        assertRasterEquals(expectedCoverage, convertedCoverage);
    }
}
