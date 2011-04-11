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
package org.geotoolkit.process.converters;

import java.awt.geom.AffineTransform;
import junit.framework.AssertionFailedError;


import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for StringToAffineTransformConverter
 * @author Quentin Boileau
 * @module pending
 */
public class AffineTransformConverterTest {


    @Test
    public void AffineTransformConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,AffineTransform> converter = StringToAffineTransformConverter.getInstance();

        String inputString = "1.6,2,3.4,4,5.4,6";
        AffineTransform convertedTransform = converter.convert(inputString);
        AffineTransform expectedTransform = new AffineTransform(1.6, 2.0, 3.4, 4.0, 5.4, 6);
        assertEquals(expectedTransform, convertedTransform);


        inputString = "1.6,2,3.4,6";
        convertedTransform = converter.convert(inputString);
        expectedTransform = new AffineTransform(1.6, 2.0, 3.4, 6.0,0.0,0.0);
        assertEquals(expectedTransform, convertedTransform);

        inputString = "1.6,2";
        try{
            convertedTransform = converter.convert(inputString);
            fail("should not pass");
        }catch(NonconvertibleObjectException ex){
            //ok
        }

    }

    
}
