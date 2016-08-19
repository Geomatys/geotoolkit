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
import java.net.URLConnection;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.coverage.Coverage;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class WPSIOTest extends org.geotoolkit.test.TestBase {

    @Test
    public void isSupportedClass() throws UnconvertibleObjectException {

        //RenderedImage
        assertFalse(WPSIO.isSupportedLiteralInputClass(RenderedImage.class));       //IN - Literal
        assertFalse(WPSIO.isSupportedLiteralOutputClass(RenderedImage.class));          //OUT - Literal
        assertFalse(WPSIO.isSupportedBBoxInputClass(RenderedImage.class));          //IN - BBOX
        assertFalse(WPSIO.isSupportedBBoxOutputClass(RenderedImage.class));             //OUT - BBOX
        assertTrue(WPSIO.isSupportedComplexInputClass(RenderedImage.class));        //IN - Complex
        assertTrue(WPSIO.isSupportedComplexOutputClass(RenderedImage.class));           //OUT - Complex
        assertTrue(WPSIO.isSupportedReferenceInputClass(RenderedImage.class));      //IN - Reference
        assertTrue(WPSIO.isSupportedReferenceOutputClass(RenderedImage.class));         //OUT - Reference


        //Coverage
        assertFalse(WPSIO.isSupportedLiteralInputClass(GridCoverage2D.class));       //IN - Literal
        assertFalse(WPSIO.isSupportedLiteralOutputClass(GridCoverage2D.class));          //OUT - Literal
        assertFalse(WPSIO.isSupportedBBoxInputClass(GridCoverage2D.class));          //IN - BBOX
        assertFalse(WPSIO.isSupportedBBoxOutputClass(GridCoverage2D.class));             //OUT - BBOX
        assertTrue(WPSIO.isSupportedComplexInputClass(GridCoverage2D.class));        //IN - Complex
        assertTrue(WPSIO.isSupportedComplexOutputClass(GridCoverage2D.class));           //OUT - Complex
        assertTrue(WPSIO.isSupportedReferenceInputClass(GridCoverage2D.class));      //IN - Reference
        assertTrue(WPSIO.isSupportedReferenceOutputClass(GridCoverage2D.class));         //OUT - Reference


    }

    @Test
    public void checkSupportedFormat() throws UnconvertibleObjectException {
        /*
         * RenderedImage
         */
        //RenderedImage INPUT
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), null, null);
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null);
        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.UTF8.getValue(), null);
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), "something");
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        //RenderedImage OUTPUT
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), null, null);
        WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null);
        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.UTF8.getValue(), null);
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        try {
             WPSIO.checkSupportedFormat(RenderedImage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), "something");
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        /*
         * GridCoverage2D
         */
         //GridCoverage2D INPUT
        WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_GEOTIFF.val(), null, null);
        WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_GEOTIFF.val(), WPSEncoding.BASE64.getValue(), null);
        try {
             WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_GEOTIFF.val(), WPSEncoding.UTF8.getValue(), null);
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        try {
             WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.INPUT, WPSMimeType.IMG_GEOTIFF.val(), null, "something");
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        //RenderedImage OUTPUT
        WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_GEOTIFF.val(), null, null);
        WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_GEOTIFF.val(), WPSEncoding.BASE64.getValue(), null);
        try {
             WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_GEOTIFF.val(), WPSEncoding.UTF8.getValue(), null);
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        try {
             WPSIO.checkSupportedFormat(Coverage.class, WPSIO.IOType.OUTPUT, WPSMimeType.IMG_GEOTIFF.val(), null, "something");
             fail();
        } catch (UnconvertibleObjectException ex) { /*do nothing*/ }

        WPSIO.checkSupportedFormat(URLConnection.class, WPSIO.IOType.BOTH, null, WPSEncoding.UTF8.getValue(), null);
    }


    @Test
    public void findConverter() throws UnconvertibleObjectException {
        WPSObjectConverter converter = null ;

        /*
         * RenderedImage
         */
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

        /*
         * GridCoverage2D
         */
        // GridCoverage2D -> ComplexDataType
        converter = WPSIO.getConverter(GridCoverage2D.class, WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX);
        assertNotNull(converter);
        assertEquals(GridCoverage2D.class, converter.getSourceClass());
        assertEquals(ComplexDataType.class, converter.getTargetClass());

        //ComplexDataType -> GridCoverage2D
        converter = WPSIO.getConverter(GridCoverage2D.class, WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX);
        assertNotNull(converter);
        assertEquals(GridCoverage2D.class, converter.getTargetClass());
        assertEquals(ComplexDataType.class, converter.getSourceClass());

        // GridCoverage2D -> ReferenceType
        converter = WPSIO.getConverter(GridCoverage2D.class, WPSIO.IOType.OUTPUT, WPSIO.FormChoice.REFERENCE);
        assertNotNull(converter);
        assertEquals(GridCoverage2D.class, converter.getSourceClass());
        assertEquals(ReferenceType.class, converter.getTargetClass());

        //ReferenceType -> GridCoverage2D
        converter = WPSIO.getConverter(GridCoverage2D.class, WPSIO.IOType.INPUT, WPSIO.FormChoice.REFERENCE);
        assertNotNull(converter);
        assertEquals(ReferenceType.class, converter.getSourceClass());
        assertEquals(GridCoverage2D.class, converter.getTargetClass());

        // URL connections
        converter = WPSIO.getConverter(URLConnection.class, WPSIO.IOType.OUTPUT, WPSIO.FormChoice.REFERENCE);
        assertNotNull(converter);
        assertEquals(URLConnection.class, converter.getSourceClass());
        assertEquals(ReferenceType.class, converter.getTargetClass());

        converter = WPSIO.getConverter(URLConnection.class, WPSIO.IOType.INPUT, WPSIO.FormChoice.REFERENCE);
        assertNotNull(converter);
        assertEquals(ReferenceType.class, converter.getSourceClass());
        assertEquals(URLConnection.class, converter.getTargetClass());
    }

    @Test
    public void findClass() {
        Class classFound = null;


        /*
         * RenderedImage
         */
        classFound = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), null, null, null);
        assertEquals(RenderedImage.class, classFound);

        classFound = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null, null);
        assertEquals(RenderedImage.class, classFound);

        classFound = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), null, null, null);
        assertEquals(RenderedImage.class, classFound);

        classFound = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_PNG.val(), WPSEncoding.BASE64.getValue(), null, null);
        assertEquals(RenderedImage.class, classFound);



        /*
         * GridCoverage2D
         */
        classFound = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_GEOTIFF.val(), null, null, null);
        assertEquals(Coverage.class, classFound);

        classFound = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_GEOTIFF.val(), WPSEncoding.BASE64.getValue(), null, null);
        assertEquals(Coverage.class, classFound);

        classFound = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_GEOTIFF.val(), null, null, null);
        assertEquals(Coverage.class, classFound);

        classFound = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, WPSMimeType.IMG_GEOTIFF.val(), WPSEncoding.BASE64.getValue(), null, null);
        assertEquals(Coverage.class, classFound);

    }
}
