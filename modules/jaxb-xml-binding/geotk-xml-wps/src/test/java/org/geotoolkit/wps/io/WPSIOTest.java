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
package org.geotoolkit.wps.io;
import java.awt.image.RenderedImage;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class WPSIOTest {
    
    @Test 
    public void isSupportedClass() throws NonconvertibleObjectException {
        
        //RenderedImage
        assertFalse(WPSIO.isSupportedLiteralInputClass(RenderedImage.class));       //IN - Literal
        assertFalse(WPSIO.isSupportedLiteralOutputClass(RenderedImage.class));          //OUT - Literal
        assertFalse(WPSIO.isSupportedBBoxInputClass(RenderedImage.class));          //IN - BBOX
        assertFalse(WPSIO.isSupportedBBoxOutputClass(RenderedImage.class));             //OUT - BBOX
        assertTrue(WPSIO.isSupportedComplexInputClass(RenderedImage.class));        //IN - Complex
        assertTrue(WPSIO.isSupportedComplexOutputClass(RenderedImage.class));           //OUT - Complex
        assertTrue(WPSIO.isSupportedReferenceInputClass(RenderedImage.class));      //IN - Reference
        assertTrue(WPSIO.isSupportedReferenceOutputClass(RenderedImage.class));         //OUT - Reference
        
    }
    
    @Test 
    public void checkSupportedFormat() throws NonconvertibleObjectException {
        
        //RenderedImage INPUT
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), null, null);
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null);
        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.UTF8.getValue(), null);
             fail();
        } catch (NonconvertibleObjectException ex) { /*do nothing*/ }
        
        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), "something");
             fail();
        } catch (NonconvertibleObjectException ex) { /*do nothing*/ }
        
        //RenderedImage OUTPUT
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), null, null);
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null);
        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.UTF8.getValue(), null);
             fail();
        } catch (NonconvertibleObjectException ex) { /*do nothing*/ }
        
        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), "something");
             fail();
        } catch (NonconvertibleObjectException ex) { /*do nothing*/ }
    }
    
    
    @Test 
    public void findConverter() throws NonconvertibleObjectException {
        WPSObjectConverter converter = null ; 
        
        // RenderedImage -> ComplexDataType 
        converter = WPSIO.getConverter(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX);
        assertNotNull(converter);
        assertEquals(RenderedImage.class, converter.getSourceClass());
        assertEquals(ComplexDataType.class, converter.getTargetClass());
        
        //ComplexDataType -> RenderedImage 
        converter = WPSIO.getConverter(RenderedImage.class, WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX);
        assertNotNull(converter);
        assertEquals(RenderedImage.class, converter.getTargetClass());
        assertEquals(ComplexDataType.class, converter.getSourceClass());
        
        // RenderedImage -> ReferenceType 
        converter = WPSIO.getConverter(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSIO.FormChoice.REFERENCE);
        assertNotNull(converter);
        assertEquals(RenderedImage.class, converter.getSourceClass());
        assertEquals(ReferenceType.class, converter.getTargetClass());
        
        //ReferenceType -> RenderedImage 
        converter = WPSIO.getConverter(RenderedImage.class, WPSIO.IOType.INPUT, WPSIO.FormChoice.REFERENCE);
        assertNotNull(converter);
        assertEquals(ReferenceType.class, converter.getSourceClass());
        assertEquals(RenderedImage.class, converter.getTargetClass());
        
    }
    
    @Test
    public void findClass() {
        Class classFound = null;
        
        
        //RendredImage
        classFound = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), null, null, null);
        assertEquals(RenderedImage.class, classFound);
        
        classFound = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null, null);
        assertEquals(RenderedImage.class, classFound);
        
        classFound = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), null, null, null);
        assertEquals(RenderedImage.class, classFound);
        
        classFound = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null, null);
        assertEquals(RenderedImage.class, classFound);
        
    }
}
