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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for StringToGeometryConverter
 * @author Quentin Boileau
 * @module pending
 */
public class GeometryConverterTest {


    @Test
    public void GeometryConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,Geometry> converter = StringToGeometryConverter.getInstance();
        
        String inputString = "POLYGON ((20 20, 20 100, 120 100, 140 20, 20 20))";
        Geometry convertedGeom = converter.convert(inputString);
        Geometry expectedGeom = buildResultGeom();
        
        Coordinate[] expCoords = expectedGeom.getCoordinates();
        Coordinate[] convCoords = convertedGeom.getCoordinates();

        assertEquals(expCoords.length, convCoords.length);
        assertArrayEquals(expCoords, convCoords);

    }

    private Geometry buildResultGeom() {

        GeometryFactory gf = new GeometryFactory();
        LinearRing ring = gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(20, 20),
                    new Coordinate(20, 100),
                    new Coordinate(120, 100),
                    new Coordinate(140, 20),
                    new Coordinate(20, 20)
                });
        return gf.createPolygon(ring, null);
      
    }
}
