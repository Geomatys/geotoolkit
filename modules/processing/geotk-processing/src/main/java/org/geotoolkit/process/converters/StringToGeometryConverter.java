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


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;

/**
 * Implementation of ObjectConverter to convert a String in WKT format into an Geometry
 * @author Quentin Boileau
 * @module pending
 */
public class StringToGeometryConverter extends SimpleConverter<String, Geometry> {

    private static StringToGeometryConverter INSTANCE;

    private StringToGeometryConverter(){
    }

    public static StringToGeometryConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToGeometryConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends Geometry> getTargetClass() {
        return Geometry.class ;
    }
    @Override
    public Geometry convert(final String s) throws NonconvertibleObjectException {

        if(s == null) throw new NonconvertibleObjectException("Empty WKT Geometry");
        try {
            final WKTReader reader = new WKTReader(new GeometryFactory());
            final Geometry geometry = reader.read(s);
            
            return geometry;

        } catch (ParseException ex) {
            throw new NonconvertibleObjectException(ex);
        }
    }
}


