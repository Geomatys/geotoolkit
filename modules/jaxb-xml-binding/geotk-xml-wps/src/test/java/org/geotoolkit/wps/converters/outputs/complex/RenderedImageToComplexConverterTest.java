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
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Quentin Boileau (Geomatys)
 */
public class RenderedImageToComplexConverterTest {
    
    
    @Test
    public void testConversion() throws NonconvertibleObjectException, IOException  {
        final WPSObjectConverter<RenderedImage, ComplexDataType> converter = WPSConverterRegistry.getInstance().getConverter(RenderedImage.class, ComplexDataType.class);
        
        final InputStream inStream = RenderedImageToComplexConverterTest.class.getResourceAsStream("/data/coverage/clouds.jpg");
        final RenderedImage img = ImageIO.read(inStream);
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(WPSObjectConverter.MIME, WPSMimeType.IMG_TIFF.val());
        param.put(WPSObjectConverter.ENCODING, "base64");
        
        final ComplexDataType complex = converter.convert(img, param);
        final List<Object> content = complex.getContent();
        final String encodedImage = (String) content.get(0);
        
        final InputStream expectedStream = RenderedImageToComplexConverterTest.class.getResourceAsStream("/expected/clouds_base64");
        String expectedString = FileUtilities.getStringFromStream(expectedStream);
        expectedString = expectedString.trim();
        
        assertEquals(expectedString, encodedImage);
        
    }
    
}
