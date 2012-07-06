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


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;

/**
 * Implementation of ObjectConverter to convert a String into a Date.
 * The String will be a ISO date string with the following format: yyyy-mm-ddThh:mm:ss.ms+HoMo"
 * @author Fabien Rétif
 * @module pending
 */
public class StringToDate extends SimpleConverter<String, Date> {

    private static StringToDate INSTANCE;

    private StringToDate(){
    }

    public static StringToDate getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToDate();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends Date> getTargetClass() {
        return Date.class ;
    }
    @Override
    public Date convert(final String s) throws NonconvertibleObjectException {
        if(s == null) throw new NonconvertibleObjectException("Empty Date");
        try {
            
            DateFormat formatter ;  
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
          
                return  (Date)formatter.parse(s);        
                    
        } catch (ParseException ex) {
             throw new NonconvertibleObjectException(ex);
        }
    }   
}


