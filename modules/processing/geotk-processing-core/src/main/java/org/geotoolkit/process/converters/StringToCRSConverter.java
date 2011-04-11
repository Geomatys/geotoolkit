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
import org.geotoolkit.util.converter.SimpleConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a String into a CoordinateReferenceSystem.
 * The String will be a CRS code like :"EPSG:3395" or "EPSG:4326"
 * @author Quentin Boileau
 * @module pending
 */
public class StringToCRSConverter extends SimpleConverter<String, CoordinateReferenceSystem> {

    private static StringToCRSConverter INSTANCE;

    private StringToCRSConverter(){
    }

    public static StringToCRSConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToCRSConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends CoordinateReferenceSystem> getTargetClass() {
        return CoordinateReferenceSystem.class ;
    }
    @Override
    public CoordinateReferenceSystem convert(final String s) throws NonconvertibleObjectException {
        if(s == null) throw new NonconvertibleObjectException("Empty CRS");
        try {
            final CoordinateReferenceSystem crs = CRS.decode(s);
            return crs;
        } 
        catch (NoSuchAuthorityCodeException ex) {
            throw new NonconvertibleObjectException(ex);
        } catch (FactoryException ex) {
            throw new NonconvertibleObjectException(ex);
        }
    }
}


