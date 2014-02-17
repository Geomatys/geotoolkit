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
package org.geotoolkit.wps.converters.outputs.complex;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CoverageToComplexConverterTest extends AbstractWPSConverterTest {


    @Test
    @org.junit.Ignore("Fails randomly because of GeoTIFF reader not found.")
    public void testConversion() throws NonconvertibleObjectException, IOException  {

        final WPSObjectConverter<GridCoverage2D, ComplexDataType> converter = WPSConverterRegistry.getInstance().getConverter(GridCoverage2D.class, ComplexDataType.class);

        final GridCoverage2D coverage = ConvertersTestUtils.makeCoverage();
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(WPSObjectConverter.MIME, WPSMimeType.IMG_GEOTIFF.val());
        param.put(WPSObjectConverter.ENCODING, "base64");

        final ComplexDataType complex = converter.convert(coverage, param);
        final List<Object> content = complex.getContent();
        final String encodedCvg = (String) content.get(0);

        final InputStream expectedStream = RenderedImageToComplexConverterTest.class.getResourceAsStream("/expected/coverage_base64");
        assertNotNull(expectedStream);
        String expectedString = FileUtilities.getStringFromStream(expectedStream);
        expectedString = expectedString.trim();

        assertEquals(expectedString, encodedCvg);
    }
}
