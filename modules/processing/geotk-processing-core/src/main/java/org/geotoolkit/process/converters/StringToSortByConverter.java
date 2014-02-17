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


import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.sort.DefaultSortBy;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;

import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;


/**
 * Implementation of ObjectConverter to convert a String into a SortBy array.
 *
 * @author Quentin Boileau
 * @module pending
 */
public class StringToSortByConverter extends SimpleConverter<String, SortBy[]> {

    private static StringToSortByConverter INSTANCE;

    private StringToSortByConverter(){
    }

    public static StringToSortByConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToSortByConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends SortBy[]> getTargetClass() {
        return SortBy[].class ;
    }
    /**
     *Convert a String into an SortBy array.
     *
     * <p> The input String must be format like :
     *  "property1:asc/desc, property2:asc/desc, ..."
     * </p>
     *
     * <p>
     * Examples:
     * <ul>
     * <li><code>id:asc, name:desc</code></li>
     * </ul>
     * </p>
     *
     */
    @Override
    public SortBy[] convert(final String s) throws NonconvertibleObjectException {

        if(s == null) throw new NonconvertibleObjectException("Empty SortBy");

        final String[] sorters = s.split(",");
        final int nbSorter = sorters.length;
        
        final SortBy[] sortBy = new SortBy[nbSorter];
        //each sorter
        for(int i = 0 ; i<nbSorter; i++){
            //one property sorter "property:order"
            final String[] aSorter = sorters[i].split(":");

            final PropertyName property = new DefaultPropertyName(aSorter[0]);

            if(aSorter[1].equalsIgnoreCase("asc")){
                sortBy[i] = new DefaultSortBy(property, SortOrder.ASCENDING);
            }else if(aSorter[1].equalsIgnoreCase("desc")){
                sortBy[i] = new DefaultSortBy(property, SortOrder.DESCENDING);
            }else{
                throw new NonconvertibleObjectException("Invalid SortBy");
            }
        }
        return sortBy;
    }
}


