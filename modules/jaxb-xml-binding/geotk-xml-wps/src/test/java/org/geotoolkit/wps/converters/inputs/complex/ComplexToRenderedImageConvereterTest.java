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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static org.geotoolkit.test.Assert.*;

import org.geotoolkit.nio.IOUtilities;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ComplexToRenderedImageConvereterTest extends AbstractWPSConverterTest {

    @Test
    public void testConversion() throws UnconvertibleObjectException, IOException  {
        final WPSObjectConverter<ComplexDataType, RenderedImage> converter = WPSConverterRegistry.getInstance().getConverter(ComplexDataType.class, RenderedImage.class);

        final InputStream expectedStream = ComplexToRenderedImageConvereterTest.class.getResourceAsStream("/expected/image_base64");
        assertNotNull(expectedStream);
        final String encodedImage = IOUtilities.toString(expectedStream);

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(WPSObjectConverter.MIME, "img/tiff");
        param.put(WPSObjectConverter.ENCODING, "base64");

        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding("base64");
        complex.setMimeType("image/tiff");
        complex.setSchema(null);
        complex.getContent().add(encodedImage);

        final RenderedImage convertedImage = converter.convert(complex, param);
        assertNotNull(convertedImage);

        final RenderedImage expectedImage = ConvertersTestUtils.makeRendredImage();

        assertRasterEquals(expectedImage, convertedImage);
    }
}
