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

import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;

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
public class CRSConverterTest extends org.geotoolkit.test.TestBase {


    @Test
    public void CRSConvertTest() throws NoSuchAuthorityCodeException, FactoryException, UnconvertibleObjectException {

        final ObjectConverter<? super String, ? extends CoordinateReferenceSystem> converter =
                ObjectConverters.find(String.class, CoordinateReferenceSystem.class);

        String inputString = "EPSG:3395";
        CoordinateReferenceSystem convertedCRS = converter.apply(inputString);
        CoordinateReferenceSystem expectedCRS = CRS.decode("EPSG:3395");
        assertEquals(expectedCRS, convertedCRS);


        inputString = "3395";
        try{
            convertedCRS = converter.apply(inputString);
            fail("should not pass");
        }catch(UnconvertibleObjectException ex){
            //ok
        }
    }
}
