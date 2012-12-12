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
import javax.imageio.ImageIO;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Assert.*;

/**
 * 
 * @author Quentin Boileau (Geomatys)
 */
public class ComplexToRenderedImageConvereterTest {
    
    
    @Test
    public void testConversion() throws NonconvertibleObjectException, IOException  {
        final WPSObjectConverter<ComplexDataType, RenderedImage> converter = WPSConverterRegistry.getInstance().getConverter(ComplexDataType.class, RenderedImage.class);
        
        final InputStream expectedStream = ComplexToRenderedImageConvereterTest.class.getResourceAsStream("/expected/clouds_base64");
        String encodedImage = FileUtilities.getStringFromStream(expectedStream);
        
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(WPSObjectConverter.MIME, "img/jpg");
        param.put(WPSObjectConverter.ENCODING, "base64");
        
        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding("base64");
        complex.setMimeType("image/jpg");
        complex.setSchema(null);
        complex.getContent().add(encodedImage);
        
        final RenderedImage convertedImage = converter.convert(complex, param);
        assertNotNull(convertedImage);
        
        final InputStream inStream = ComplexToRenderedImageConvereterTest.class.getResourceAsStream("/data/coverage/clouds.jpg");
        final RenderedImage expectedImage = ImageIO.read(inStream);
        
        assertRasterEquals(expectedImage, convertedImage);
        
    }
    
}
