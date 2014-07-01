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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.FileUtilities;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.junit.Test;
import static org.geotoolkit.test.Assert.*;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ComplexToCoverageConverterTest extends AbstractWPSConverterTest {


    @Test
    @org.junit.Ignore("Fails randomly because of GeoTIFF reader not found.")
    public void testConversion() throws UnconvertibleObjectException, IOException, InterruptedException  {

        final WPSObjectConverter<ComplexDataType, GridCoverage2D> converter = WPSConverterRegistry.getInstance().getConverter(ComplexDataType.class, GridCoverage2D.class);

        final InputStream expectedStream = ComplexToRenderedImageConvereterTest.class.getResourceAsStream("/expected/coverage_base64");
        assertNotNull(expectedStream);
        final String encodedCoverage = FileUtilities.getStringFromStream(expectedStream);

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(WPSObjectConverter.MIME, "image/x-geotiff");
        param.put(WPSObjectConverter.ENCODING, "base64");

        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding("base64");
        complex.setMimeType("image/x-geotiff");
        complex.setSchema(null);
        complex.getContent().add(encodedCoverage);

        final GridCoverage2D convertedCoverage = converter.convert(complex, param);
        assertNotNull(convertedCoverage);

        final GridCoverage2D expectedCoverage = ConvertersTestUtils.makeCoverage();

        final Envelope convertedEnvelope = convertedCoverage.getEnvelope();
        final Envelope expectedEnvelope = expectedCoverage.getEnvelope();

        assertTrue(CRS.equalsIgnoreMetadata(expectedEnvelope.getCoordinateReferenceSystem(), convertedEnvelope.getCoordinateReferenceSystem()));
        assertTrue(expectedEnvelope.getMinimum(0) == convertedEnvelope.getMinimum(0));
        assertTrue(expectedEnvelope.getMinimum(1) == convertedEnvelope.getMinimum(1));
        assertTrue(expectedEnvelope.getMaximum(0) == convertedEnvelope.getMaximum(0));
        assertTrue(expectedEnvelope.getMaximum(1) == convertedEnvelope.getMaximum(1));
        assertRasterEquals(expectedCoverage, convertedCoverage);
    }
}
