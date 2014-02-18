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

import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.feature.simple.SimpleFeatureType;

import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import static org.junit.Assert.*;

/**
 * Junit test for StringToFeatyreTypeConverter
 * @author Quentin Boileau
 * @module pending
 */
public class FeatureTypeConverterTest {


    @Test
    public void FeatureTypeConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,FeatureType> converter = StringToFeatureTypeConverter.getInstance();

        String inputString = "Person{name:String,age:0,position:Point:srid=3395}";
        FeatureType convertedType = converter.convert(inputString);
        FeatureType expectedType = buildResultType();
        assertEquals(expectedType, convertedType);
    }

    private FeatureType buildResultType() throws FactoryException {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Person");
        ftb.add("name", String.class);
        ftb.add("age",Integer.class);
        ftb.add("position", Point.class, CRS.decode("EPSG:3395"));

        ftb.setDefaultGeometry("position");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }
}
