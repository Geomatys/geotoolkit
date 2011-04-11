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

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;

/**
 * Junit test for StringToCRSConverter
 * @author Quentin Boileau
 * @module pending
 */
public class CRSConverterTest {


    @Test
    public void CRSConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,CoordinateReferenceSystem> converter = StringToCRSConverter.getInstance();

        String inputString = "EPSG:3395";
        CoordinateReferenceSystem convertedCRS = converter.convert(inputString);
        CoordinateReferenceSystem expectedCRS = CRS.decode("EPSG:3395");
        assertEquals(expectedCRS, convertedCRS);


        inputString = "3395";
        try{
            convertedCRS = converter.convert(inputString);
            fail("should not pass");
        }catch(NonconvertibleObjectException ex){
            //ok
        }
    }
}
