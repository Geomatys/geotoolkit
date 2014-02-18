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


import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;

import org.opengis.feature.type.FeatureType;


/**
 * Implementation of ObjectConverter to convert a String into a FeatureType.
 * The String will be format like :"typeName{property1:type1, property2:type2, ...}".
 * @author Quentin Boileau
 * @module pending
 */
public class StringToFeatureTypeConverter extends SimpleConverter<String, FeatureType> {

    private static StringToFeatureTypeConverter INSTANCE;

    private StringToFeatureTypeConverter(){
    }

    public static StringToFeatureTypeConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToFeatureTypeConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends FeatureType> getTargetClass() {
        return FeatureType.class ;
    }
    /**
     *
     * Convert a String to a FeatureType.
     *
     * <p>
     * First the typeName is the future name of the FeatureType
     * and after all FeatureType properties between "{ ... }"
     *</p>
     * <p>
     * You may indicate the default Geometry with an astrix: "*geom:Geometry". You
     * may also indicate the srid (used to look up a EPSG code).
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li><code>Person{name:"",age:0,geom:Geometry,centroid:Point,url:java.io.URL}"</code></li>
     * or
     * <li><code>Something{id:String,polygonProperty:Polygon:srid=32615}</code></li>
     * </ul>
     * </p>
     */
    @Override
    public FeatureType convert(final String s) throws NonconvertibleObjectException {

        if(s == null) throw new NonconvertibleObjectException("Empty FeatureType");
        try {
            
            final int split = s.lastIndexOf('{');
            final String typeName = s.substring(0, split);
            final String values = s.substring(split + 1, s.length() - 1);

            final FeatureType type = FeatureTypeUtilities.createType(null,typeName, values);
            if(type != null){
                return type;
            }else {
                throw new NonconvertibleObjectException("Invalid FeatureType");
            }

        } catch (SchemaException ex) {
            throw new NonconvertibleObjectException(ex);
        }

    }
}


