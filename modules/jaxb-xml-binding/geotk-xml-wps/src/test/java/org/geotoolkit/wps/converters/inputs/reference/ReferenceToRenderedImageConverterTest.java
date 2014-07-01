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

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import static org.geotoolkit.test.Assert.*;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.converters.inputs.references.AbstractReferenceInputConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ReferenceToRenderedImageConverterTest extends AbstractWPSConverterTest {

    
    @Test
    public void testConversion() throws UnconvertibleObjectException, IOException  {
        final WPSObjectConverter<ReferenceType, RenderedImage> converter = WPSConverterRegistry.getInstance().getConverter(ReferenceType.class, RenderedImage.class);
        
        final URL image = ReferenceToRenderedImageConverterTest.class.getResource("/inputs/image.tiff");
        assertNotNull(image);
        
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AbstractReferenceInputConverter.IOTYPE, WPSIO.IOType.INPUT);
        
        final ReferenceType reference = new InputReferenceType();
        reference.setHref(image.toString());
        reference.setMimeType("image/tiff");
        reference.setEncoding(null);
        
        final RenderedImage convertedImage = converter.convert(reference, parameters);
        assertNotNull(convertedImage);
        
        final RenderedImage expectedImage = ConvertersTestUtils.makeRendredImage();
        assertRasterEquals(expectedImage, convertedImage);
    }
    
    @Test
    public void testConversionBase64() throws UnconvertibleObjectException, IOException  {
        final WPSObjectConverter<ReferenceType, RenderedImage> converter = WPSConverterRegistry.getInstance().getConverter(ReferenceType.class, RenderedImage.class);
        
        final URL imageBase64 = ReferenceToRenderedImageConverterTest.class.getResource("/inputs/image_tiff_base64");
        assertNotNull(imageBase64);
        
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AbstractReferenceInputConverter.IOTYPE, WPSIO.IOType.INPUT);
        parameters.put(AbstractReferenceInputConverter.ENCODING, "base64");
        
        final ReferenceType reference = new InputReferenceType();
        reference.setHref(imageBase64.toString());
        reference.setMimeType("image/tiff");
        reference.setEncoding("base64");
        
        final RenderedImage convertedImage = converter.convert(reference, parameters);
        assertNotNull(convertedImage);
        
        final RenderedImage expectedImage = ConvertersTestUtils.makeRendredImage();
        assertRasterEquals(expectedImage, convertedImage);
    }
    
}
