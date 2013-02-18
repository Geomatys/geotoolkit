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
package org.geotoolkit.wps.converters.inputs.reference;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.referencing.CRS;
import static org.geotoolkit.test.Assert.*;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.converters.inputs.references.AbstractReferenceInputConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ReferenceToGridCoverage2DConverterTest extends AbstractWPSConverterTest {

    
    @Test
    public void testConversion() throws NonconvertibleObjectException, IOException  {
        final WPSObjectConverter<ReferenceType, GridCoverage2D> converter = WPSConverterRegistry.getInstance().getConverter(ReferenceType.class, GridCoverage2D.class);
        
        final URL coverage = ReferenceToRenderedImageConverterTest.class.getResource("/inputs/coverage.tiff");
        assertNotNull(coverage);
        
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AbstractReferenceInputConverter.IOTYPE, WPSIO.IOType.INPUT);
        parameters.put(AbstractReferenceInputConverter.MIME, "image/geotiff");
        
        final ReferenceType reference = new InputReferenceType();
        reference.setHref(coverage.toString());
        reference.setMimeType("image/x-geotiff");
        reference.setEncoding(null);
        
        final GridCoverage2D convertedCvg = converter.convert(reference, parameters);
        assertNotNull(convertedCvg);
        
        final GridCoverage2D expectedCvg = ConvertersTestUtils.makeCoverage();
        final Envelope convertedEnvelope = convertedCvg.getEnvelope();
        final Envelope expectedEnvelope = expectedCvg.getEnvelope();
        
        assertTrue(CRS.equalsIgnoreMetadata(expectedEnvelope.getCoordinateReferenceSystem(), convertedEnvelope.getCoordinateReferenceSystem()));
        assertTrue(expectedEnvelope.getMinimum(0) == convertedEnvelope.getMinimum(0));
        assertTrue(expectedEnvelope.getMinimum(1) == convertedEnvelope.getMinimum(1));
        assertTrue(expectedEnvelope.getMaximum(0) == convertedEnvelope.getMaximum(0));
        assertTrue(expectedEnvelope.getMaximum(1) == convertedEnvelope.getMaximum(1));
        assertRasterEquals(expectedCvg, convertedCvg);
    }
    
    @Test
    public void testConversionBase64() throws NonconvertibleObjectException, IOException  {
        
        final WPSObjectConverter<ReferenceType, GridCoverage2D> converter = WPSConverterRegistry.getInstance().getConverter(ReferenceType.class, GridCoverage2D.class);
        
        final URL coverageBase64 = ReferenceToRenderedImageConverterTest.class.getResource("/inputs/coverage_geotiff_base64");
        assertNotNull(coverageBase64);
        
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AbstractReferenceInputConverter.IOTYPE, WPSIO.IOType.INPUT);
        parameters.put(AbstractReferenceInputConverter.ENCODING, "base64");
        
        final ReferenceType reference = new InputReferenceType();
        reference.setHref(coverageBase64.toString());
        reference.setMimeType("image/x-geotiff");
        reference.setEncoding("base64");
        
        final GridCoverage2D convertedCvg = converter.convert(reference, parameters);
        assertNotNull(convertedCvg);
        
        final GridCoverage2D expectedCvg = ConvertersTestUtils.makeCoverage();
        final Envelope convertedEnvelope = convertedCvg.getEnvelope();
        final Envelope expectedEnvelope = expectedCvg.getEnvelope();

        assertTrue(CRS.equalsIgnoreMetadata(expectedEnvelope.getCoordinateReferenceSystem(), convertedEnvelope.getCoordinateReferenceSystem()));
        assertTrue(expectedEnvelope.getMinimum(0) == convertedEnvelope.getMinimum(0));
        assertTrue(expectedEnvelope.getMinimum(1) == convertedEnvelope.getMinimum(1));
        assertTrue(expectedEnvelope.getMaximum(0) == convertedEnvelope.getMaximum(0));
        assertTrue(expectedEnvelope.getMaximum(1) == convertedEnvelope.getMaximum(1));
        assertRasterEquals(expectedCvg, convertedCvg);
    }
    
}
