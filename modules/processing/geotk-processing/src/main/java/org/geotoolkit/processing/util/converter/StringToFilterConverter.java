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
package org.geotoolkit.processing.util.converter;

import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.opengis.filter.Filter;

/**
 * Implementation of ObjectConverter to convert a String into a Filter using the
 * CQL syntax.
 * @author Quentin Boileau
 * @module pending
 */
public class StringToFilterConverter extends SimpleConverter<String, Filter> {

    /*
     * Public constructor in order to regiser converter in Geotk ConverterRegisry by ServiceLoader system.
     */
    public StringToFilterConverter(){
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Filter> getTargetClass() {
        return Filter.class ;
    }
    @Override
    public Filter apply(final String s) throws UnconvertibleObjectException {

        if(s == null) throw new UnconvertibleObjectException("Empty CQL Query");
        try {
            final Filter filter = CQL.parseFilter(s);
            return filter;
        }
        catch (CQLException ex) {
            throw new UnconvertibleObjectException(ex);
        }
    }
}


