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


import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;



/**
 * Implementation of ObjectConverter to convert a String into a NumberRange array.
 *
 * @author Quentin Boileau
 * @module pending
 */
public class StringToNumberRangeConverter extends SimpleConverter<String, NumberRange[]> {

    private static StringToNumberRangeConverter INSTANCE;

    private StringToNumberRangeConverter(){
    }

    public static StringToNumberRangeConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToNumberRangeConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends NumberRange[]> getTargetClass() {
        return NumberRange[].class ;
    }
    /**
     *Convert a String into an NumberRange array.
     *
     * <p> The input String must be format like :
     *  "min1,max1:min2,max2: ..."
     * </p>
     *
     * <p>
     * Examples:
     * <ul>
     * <li><code>5,10:128,25:220,95</code></li>
     * </ul>
     * </p>
     *
     */
    @Override
    public NumberRange[] convert(final String s) throws NonconvertibleObjectException {

        if(s == null) throw new NonconvertibleObjectException("Empty NumberRange");

        final String[] range = s.split(":");
        final int nbRange = range.length;
        
        final NumberRange[] ranges = new NumberRange[nbRange];
        //each range
        for(int i = 0 ; i<nbRange; i++){
            
            final String[] aRange = range[i].split(",");

            String min = aRange[0];
            String max = aRange[1];
            
            double dblMin = Double.valueOf(min);
            double dblMax = Double.valueOf(max);
            
            if(dblMin>dblMax){
                throw new NonconvertibleObjectException("Invalid NumberRange Min/Max :"+min+"/"+max);
            }
            
            ranges[i] = new NumberRange(Double.class, dblMin, dblMax);
            
        }
        return ranges;
    }
}