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
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.junit.Test;
import static org.geotoolkit.test.Assert.*;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.outputs.complex.CoverageToComplexConverterTest;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ComplexToCoverageConverterTest {
    
    @Test
    public void testConversion() throws NonconvertibleObjectException, IOException  {
        Setup.initialize(null);
        
        final WPSObjectConverter<ComplexDataType, GridCoverage2D> converter = WPSConverterRegistry.getInstance().getConverter(ComplexDataType.class, GridCoverage2D.class);
        
        final InputStream expectedStream = ComplexToRenderedImageConvereterTest.class.getResourceAsStream("/expected/coverage_base64");
        String encodedCoverage = FileUtilities.getStringFromStream(expectedStream);
        
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(WPSObjectConverter.MIME, "img/tiff");
        param.put(WPSObjectConverter.ENCODING, "base64");
        
        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding("base64");
        complex.setMimeType("image/tiff");
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
