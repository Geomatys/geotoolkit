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


import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.util.UnconvertibleObjectException;

import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;


/**
 * Implementation of ObjectConverter to convert a String into a SortBy array.
 *
 * @author Quentin Boileau
 * @module
 */
public class StringToSortByConverter extends SimpleConverter<String, SortBy[]> {

    private static StringToSortByConverter INSTANCE;

    public static StringToSortByConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToSortByConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<SortBy[]> getTargetClass() {
        return SortBy[].class;
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
    public SortBy[] apply(final String s) throws UnconvertibleObjectException {

        if(s == null) throw new UnconvertibleObjectException("Empty SortBy");

        final String[] sorters = s.split(",");
        final int nbSorter = sorters.length;

        final SortBy[] sortBy = new SortBy[nbSorter];
        //each sorter
        for(int i = 0 ; i<nbSorter; i++){
            //one property sorter "property:order"
            final String[] aSorter = sorters[i].split(":");

            final String property = aSorter[0];

            if(aSorter[1].equalsIgnoreCase("asc")){
                sortBy[i] = DefaultFactories.forBuildin(FilterFactory.class).sort(property, SortOrder.ASCENDING);
            }else if(aSorter[1].equalsIgnoreCase("desc")){
                sortBy[i] = DefaultFactories.forBuildin(FilterFactory.class).sort(property, SortOrder.DESCENDING);
            }else{
                throw new UnconvertibleObjectException("Invalid SortBy");
            }
        }
        return sortBy;
    }
}


