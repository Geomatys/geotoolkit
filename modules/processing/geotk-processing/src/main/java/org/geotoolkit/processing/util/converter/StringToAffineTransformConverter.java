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

import java.awt.geom.AffineTransform;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;
import org.geotoolkit.feature.util.converter.SimpleConverter;


/**
 * Implementation of ObjectConverter to convert a String into an AffineTransform.
 *
 * @author Quentin Boileau
 * @module pending
 */
public class StringToAffineTransformConverter extends SimpleConverter<String, AffineTransform> {

    private static StringToAffineTransformConverter INSTANCE;

    private StringToAffineTransformConverter(){
    }

    public static synchronized StringToAffineTransformConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToAffineTransformConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<AffineTransform> getTargetClass() {
        return AffineTransform.class ;
    }
    /**
     *Convert a String into an AffineTransform.
     *
     * <p> You can put 4 or 6 different numeric values separate by a comma. </p>
     *
     *
     * <p>
     * Examples:
     * <ul>
     * <li><code>val1,val2,val3,val4,val5,val6</code></li>
     * or
     * <li><code>val1,val2,val3,val4</code></li>
     * </ul>
     * </p>
     *
     *
     */
    @Override
    public AffineTransform apply(final String s) throws UnconvertibleObjectException {

        if(s == null) throw new UnconvertibleObjectException("Empty AffineTransform");

        final String[] values = s.split(",");

        if(values.length != 6 && values.length != 4){
            throw new UnconvertibleObjectException("Invalid AffineTransform values. Need 4 or 6 values");

        }else{
            //convert string into double
            final ObjectConverter<? super String, ? extends Double> converter = ObjectConverters.find(String.class, Double.class);
            final double[] convertedValues = new double[values.length];
            for(int i = 0; i<values.length; i++){
                convertedValues[i] = converter.apply(values[i]);
            }
            final AffineTransform transform = new AffineTransform(convertedValues);
            return transform;
        }
    }
}
