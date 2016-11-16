/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util.converter;

import org.geotoolkit.processing.util.converter.StringToAffineTransformConverter;
import java.awt.geom.AffineTransform;


import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for StringToAffineTransformConverter
 * @author Quentin Boileau
 * @module
 */
public class AffineTransformConverterTest extends org.geotoolkit.test.TestBase {


    @Test
    public void AffineTransformConvertTest() throws NoSuchAuthorityCodeException, FactoryException, UnconvertibleObjectException {

        final ObjectConverter<String,AffineTransform> converter = StringToAffineTransformConverter.getInstance();

        String inputString = "1.6,2,3.4,4,5.4,6";
        AffineTransform convertedTransform = converter.apply(inputString);
        AffineTransform expectedTransform = new AffineTransform(1.6, 2.0, 3.4, 4.0, 5.4, 6);
        assertEquals(expectedTransform, convertedTransform);


        inputString = "1.6,2,3.4,6";
        convertedTransform = converter.apply(inputString);
        expectedTransform = new AffineTransform(1.6, 2.0, 3.4, 6.0,0.0,0.0);
        assertEquals(expectedTransform, convertedTransform);

        inputString = "1.6,2";
        try{
            convertedTransform = converter.apply(inputString);
            fail("should not pass");
        }catch(UnconvertibleObjectException ex){
            //ok
        }

    }


}
