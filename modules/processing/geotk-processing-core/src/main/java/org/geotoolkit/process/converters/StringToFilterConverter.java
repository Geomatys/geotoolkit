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


import org.geotoolkit.filter.text.cql2.CQL;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;

import org.opengis.filter.Filter;

/**
 * Implementation of ObjectConverter to convert a String into a Filter using the
 * CQL syntax.
 * @author Quentin Boileau
 * @module pending
 */
public class StringToFilterConverter extends SimpleConverter<String, Filter> {

    private static StringToFilterConverter INSTANCE;

    private StringToFilterConverter(){
    }

    public static StringToFilterConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToFilterConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends Filter> getTargetClass() {
        return Filter.class ;
    }
    @Override
    public Filter convert(final String s) throws NonconvertibleObjectException {

        if(s == null) throw new NonconvertibleObjectException("Empty CQL Query");
        try {
            final Filter filter = CQL.toFilter(s);
            return filter;
        } 
        catch (CQLException ex) {
            throw new NonconvertibleObjectException(ex);
        } 
    }
}


